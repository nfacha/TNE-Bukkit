package net.tnemc.core.common.account;

import net.tnemc.core.TNE;
import net.tnemc.core.common.account.handlers.HoldingsHandler;
import net.tnemc.core.common.account.history.AccountHistory;
import net.tnemc.core.common.api.IDFinder;
import net.tnemc.core.common.currency.ItemCalculations;
import net.tnemc.core.common.currency.TNECurrency;
import net.tnemc.core.common.transaction.TNETransaction;
import net.tnemc.core.common.transaction.charge.TransactionCharge;
import net.tnemc.core.common.transaction.charge.TransactionChargeType;
import net.tnemc.core.common.utils.MISCUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The New Economy Minecraft Server Plugin
 *
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * Created by Daniel on 12/7/2017.
 */

public class TNEAccount {
  private Map<UUID, AccountAccessor> accessors = new HashMap<>();
  private AccountHistory history;

  private int accountNumber = 0;
  private UUID id;
  private String displayName;
  private AccountStatus status;
  private String language;
  private boolean player;
  private long joined;
  private long lastOnline;

  //Extended TNEAccount Functionality
  private String pin;

  public TNEAccount(UUID id, String displayName) {
    this.id = id;
    this.displayName = displayName;
    this.status = AccountStatus.NORMAL;
    this.language = "Default";
    this.player = !IDFinder.isNonPlayer(displayName);
    this.joined = new Date().getTime();
    this.lastOnline = new Date().getTime();
    this.pin = "";
    history = new AccountHistory();
  }

  public void log(TNETransaction transaction) {
    history.log(transaction);
  }

  public void setHoldings(String world, String currency, BigDecimal newHoldings) {
    TNE.debug("=====START TNEAccount.setHoldings(3) =====");
    TNE.debug("Holdings: " + newHoldings.toPlainString());
    setHoldings(world, currency, newHoldings, false);
    TNE.debug("=====END TNEAccount.setHoldings =====");
  }

  public void setHoldings(String world, String currency, BigDecimal amount, boolean skip) {
    world = TNE.instance().getWorldManager(world).getBalanceWorld();
    TNE.debug("=====START TNEAccount.setHoldings(4) =====");
    TNE.debug("Holdings: " + amount.toPlainString());

    TNECurrency cur = TNE.manager().currencyManager().get(world, currency);

    if(cur == null) cur = TNE.manager().currencyManager().get(world);
    if(cur == null) cur = TNE.manager().currencyManager().get(TNE.instance().defaultWorld);

    TNE.debug("TNECurrency: " + cur.name());
    try {
      cur.getCurrencyType().setHoldings(identifier(), world, cur, amount, skip);
    } catch (SQLException e) {
      TNE.debug(e);
    }
    TNE.debug("=====END TNEAccount.setHoldings =====");
  }

  public void removeHoldings(BigDecimal amount, String world, String currency, boolean core) {
    BigDecimal leftOver = amount;
    for(Map.Entry<Integer, List<HoldingsHandler>> entry : TNE.manager().getHoldingsHandlers().descendingMap().entrySet()) {
      if(leftOver.compareTo(BigDecimal.ZERO) <= 0) break;
      for(HoldingsHandler handler : entry.getValue()) {
        if(leftOver.compareTo(BigDecimal.ZERO) <= 0) break;
        if(!core || handler.coreHandler()) {
          if(!handler.userContains().equalsIgnoreCase("") ||
              displayName().contains(handler.userContains())) {
            leftOver = handler.removeHoldings(identifier(), world, TNE.manager().currencyManager().get(world, currency), leftOver);
          }
        }
      }
    }
  }

  private boolean hasHoldings(String world, String currency) {
    TNECurrency cur = TNE.manager().currencyManager().get(world, currency);
    world = TNE.instance().getWorldManager(world).getBalanceWorld();
    if(cur.isXp() && MISCUtils.isOnline(id, world)) {
      return true;
    } else if(!cur.isItem() || !MISCUtils.isOnline(id, world)) {
      BigDecimal holdings = null;

      try {
        holdings = TNE.saveManager().getTNEManager().getTNEProvider().loadBalance(identifier(), world, currency);
      } catch (SQLException e) {
        TNE.debug(e);
      }
      return holdings != null;
    } else {
      return ItemCalculations.getCurrencyItems(cur, getPlayer().getInventory()).compareTo(BigDecimal.ZERO) > 0;
    }
  }

  public BigDecimal getHoldings(String world, String currency, boolean core, boolean database) {
    BigDecimal holdings = BigDecimal.ZERO;
    for (Map.Entry<Integer, List<HoldingsHandler>> entry : TNE.manager().getHoldingsHandlers().descendingMap().entrySet()) {
      for (HoldingsHandler handler : entry.getValue()) {
        if (!core || handler.coreHandler()) {
          if (handler.userContains().equalsIgnoreCase("") ||
              displayName().contains(handler.userContains())) {
            TNE.debug("TNECurrency: " + currency);
            TNE.debug("TNECurrency: " + TNE.manager().currencyManager().get(world, currency));
            TNE.debug("identifier(): " + identifier());
            TNE.debug("world: " + world);
            holdings = holdings.add(handler.getHoldings(identifier(), world, TNE.manager().currencyManager().get(world, currency), database));
          }
        }
      }
    }
    return holdings;
  }

  public BigDecimal getNonCoreHoldings(String world, String currency, boolean database) {
    BigDecimal holdings = BigDecimal.ZERO;
    for (Map.Entry<Integer, List<HoldingsHandler>> entry : TNE.manager().getHoldingsHandlers().descendingMap().entrySet()) {
      for (HoldingsHandler handler : entry.getValue()) {
        if (!handler.coreHandler()) {
          if (handler.userContains().equalsIgnoreCase("") ||
              displayName().contains(handler.userContains())) {
            holdings = holdings.add(handler.getHoldings(identifier(), world, TNE.manager().currencyManager().get(world, currency), database));
          }
        }
      }
    }
    return holdings;
  }

  public void saveItemCurrency(String world) {
    saveItemCurrency(world, true);
  }

  public void saveItemCurrency(String world, boolean save) {
    saveItemCurrency(world, save, getPlayer().getInventory());
  }

  public void saveItemCurrency(String world, boolean save, PlayerInventory inventory) {
    TNE.debug("saveItemCurrency for world : " + world + " Save: " + save);
    List<String> currencies = TNE.instance().getWorldManager(world).getItemCurrencies();

    currencies.forEach((currency)->{
      TNE.debug("TNECurrency: " + currency);
      final TNECurrency cur = TNE.manager().currencyManager().get(world, currency);
      try {
        TNE.saveManager().getTNEManager().getTNEProvider().saveBalance(identifier(), world, currency, ItemCalculations.getCurrencyItems(cur, inventory));
      } catch (SQLException e) {
        TNE.debug(e);
      }
    });
    if(save) TNE.manager().addAccount(this);
  }

  public static TNEAccount getAccount(String identifier) {
    return TNE.manager().getAccount(IDFinder.getID(identifier));
  }

  public void initializeHoldings(String world) {
    TNE.manager().currencyManager().getWorldCurrencies(world).forEach((currency)->{
      TNE.debug("TNECurrency: " + currency.name());
      TNE.debug("Balance: " + currency.defaultBalance().toPlainString());
      TNE.debug("Comparison: " + (currency.defaultBalance().compareTo(BigDecimal.ZERO) > 0));
      TNE.debug("Has: " + hasHoldings(world, currency.name()));
      if(currency.defaultBalance().compareTo(BigDecimal.ZERO) > 0 && !hasHoldings(world, currency.name())) {
        TNE.debug("Adding default");
        addHoldings(currency.defaultBalance(), currency, world);
      }
    });
  }

  public Player getPlayer() {
    return IDFinder.getPlayer(displayName);
  }

  public AccountHistory getHistory() {
    return history;
  }

  public void setHistory(AccountHistory history) {
    this.history = history;
  }

  public int getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(int accountNumber) {
    this.accountNumber = accountNumber;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public AccountStatus getStatus() {
    return status;
  }

  public void setStatus(AccountStatus status) {
    this.status = status;
  }

  public void setPlayerAccount(boolean player) {
    this.player = player;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public long getJoined() {
    return joined;
  }

  public void setJoined(long joined) {
    this.joined = joined;
  }

  public long getLastOnline() {
    return lastOnline;
  }

  public void setLastOnline(long lastOnline) {
    this.lastOnline = lastOnline;
  }

  public String getPin() {
    return pin;
  }

  public void setPin(String pin) {
    this.pin = pin;
  }

  public UUID identifier() {
    return id;
  }

  public String displayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public boolean playerAccount() {
    return !IDFinder.isNonPlayer(displayName);
  }

  public boolean isAccessor(TNEAccount account) {
    return accessors.containsKey(account.identifier());
  }

  public boolean canWithdraw(TNEAccount account) {
    if(isAccessor(account) && accessors.get(account.identifier()).canWithdraw()) {
      return true;
    }
    return false;
  }

  public boolean canDeposit(TNEAccount account) {
    if(isAccessor(account) && accessors.get(account.identifier()).canDeposit()) {
      return true;
    }
    return false;
  }

  public boolean canRemoveAccessor(TNEAccount account) {
    if(isAccessor(account)) {
      return accessors.get(account.identifier()).canRemoveAccessor();
    }
    return false;
  }

  public boolean canAddAccessor(TNEAccount account) {
    if(isAccessor(account)) {
      return accessors.get(account.identifier()).canAddAccessor();
    }
    return false;
  }

  public BigDecimal getHoldings() {
    String world = TNE.instance().defaultWorld;
    TNECurrency currency = TNE.manager().currencyManager().get(world);
    return getHoldings(world, currency.name(), false, false);
  }

  public BigDecimal getHoldings(String world) {
    TNECurrency currency = TNE.manager().currencyManager().get(world);
    return getHoldings(world, currency.name(), false, false);
  }

  public BigDecimal getHoldings(String world, TNECurrency currency) {
    TNE.debug("=====START TNEAccount.getHoldings w/ World & TNECurrency parameter =====");
    return getHoldings(world, currency.name(), false, false);
  }

  public BigDecimal getHoldings(TNECurrency currency) {
    return getHoldings(TNE.instance().defaultWorld, currency.name(), false, false);
  }

  public boolean hasHoldings(BigDecimal amount) {
    return getHoldings().compareTo(amount) >= 0;
  }

  public boolean hasHoldings(BigDecimal amount, String world) {
    return getHoldings(world).compareTo(amount) >= 0;
  }

  public boolean hasHoldings(BigDecimal amount, TNECurrency currency) {
    return getHoldings(currency).compareTo(amount) >= 0;
  }

  public boolean hasHoldings(BigDecimal amount, TNECurrency currency, String world) {
    return getHoldings(world, currency).compareTo(amount) >= 0;
  }

  public boolean setHoldings(BigDecimal amount) {
    String world = TNE.instance().defaultWorld;
    setHoldings(world, TNE.manager().currencyManager().get(world).name(), amount);
    return true;
  }

  public boolean setHoldings(BigDecimal amount, String world) {
    setHoldings(world, TNE.manager().currencyManager().get(world).name(), amount);
    return true;
  }

  public boolean setHoldings(BigDecimal amount, TNECurrency currency) {
    setHoldings(TNE.instance().defaultWorld, currency.name(), amount);
    return true;
  }

  public boolean setHoldings(BigDecimal amount, TNECurrency currency, String world) {
    setHoldings(world, currency.name(), amount);
    return true;
  }

  public boolean addHoldings(BigDecimal amount) {
    if(amount.equals(BigDecimal.ZERO)) return true;
    String world = TNE.instance().defaultWorld;
    TNECurrency currency = TNE.manager().currencyManager().get(world);
    setHoldings(world, currency.name(), getHoldings(world, currency.name(), true, false).add(amount));
    return true;
  }

  public boolean addHoldings(BigDecimal amount, String world) {
    if(amount.equals(BigDecimal.ZERO)) return true;
    TNECurrency currency = TNE.manager().currencyManager().get(world);
    setHoldings(world, currency.name(), getHoldings(world, currency.name(), true, false).add(amount));
    return true;
  }

  public boolean addHoldings(BigDecimal amount, TNECurrency currency) {
    if(amount.equals(BigDecimal.ZERO)) return true;
    String world = TNE.instance().defaultWorld;
    setHoldings(world, currency.name(), getHoldings(world, currency.name(), true, false).add(amount));
    return true;
  }

  public boolean addHoldings(BigDecimal amount, TNECurrency currency, String world) {
    if(amount.equals(BigDecimal.ZERO)) return true;
    setHoldings(world, currency.name(), getHoldings(world, currency.name(), true, false).add(amount));
    return true;
  }

  public boolean canAddHoldings(BigDecimal amount) {
    return true;
  }

  public boolean canAddHoldings(BigDecimal amount, String world) {
    final BigDecimal max = TNE.manager().currencyManager().get(world).getMaxBalance();

    if(getHoldings(world).add(amount).compareTo(max) > 0) {
      return false;
    }
    return true;
  }

  public boolean canAddHoldings(BigDecimal amount, TNECurrency currency) {
    return true;
  }

  public boolean canAddHoldings(BigDecimal amount, TNECurrency currency, String world) {
    final BigDecimal max = TNE.manager().currencyManager().get(world, currency.name()).getMaxBalance();

    if(getHoldings(world, currency).add(amount).compareTo(max) > 0) {
      return false;
    }
    return true;
  }

  public boolean removeHoldings(BigDecimal amount) {
    if(amount.equals(BigDecimal.ZERO)) return true;
    if(hasHoldings(amount)) {
      String world = TNE.instance().defaultWorld;
      TNECurrency currency = TNE.manager().currencyManager().get(world);
      removeHoldings(amount, world, currency.name(), false);
      return true;
    }
    return false;
  }

  public boolean removeHoldings(BigDecimal amount, String world) {
    if(amount.equals(BigDecimal.ZERO)) return true;
    if(hasHoldings(amount, world)) {
      TNECurrency currency = TNE.manager().currencyManager().get(world);
      removeHoldings(amount, world, currency.name(), false);
      return true;
    }
    return false;
  }

  public boolean removeHoldings(BigDecimal amount, TNECurrency currency) {
    if(amount.equals(BigDecimal.ZERO)) return true;
    if(hasHoldings(amount, currency)) {
      String world = TNE.instance().defaultWorld;
      removeHoldings(amount, world, currency.name(), false);
      return true;
    }
    return false;
  }

  public boolean removeHoldings(BigDecimal amount, TNECurrency currency, String world) {
    if(amount.equals(BigDecimal.ZERO)) return true;
    if(hasHoldings(amount, currency, world)) {
      removeHoldings(amount, world, currency.name(), false);
      return true;
    }
    return false;
  }

  public boolean canRemoveHoldings(BigDecimal amount) {
    if(hasHoldings(amount)) return true;
    return false;
  }

  public boolean canRemoveHoldings(BigDecimal amount, String world) {
    if(hasHoldings(amount, world)) return true;
    return false;
  }

  public boolean canRemoveHoldings(BigDecimal amount, TNECurrency currency) {
    if(hasHoldings(amount, currency)) return true;
    return false;
  }

  public boolean canRemoveHoldings(BigDecimal amount, TNECurrency currency, String world) {
    if(hasHoldings(amount, currency, world)) return true;
    return false;
  }

  /**
   * Used to handle an {@link TransactionCharge}. This is mostly a shorthand method.
   * @param charge The {@link TransactionCharge} to handle.
   * @return True if charge is able to be handled successfully, otherwise false.
   */
  public boolean handleCharge(TransactionCharge charge) {
    if(charge.getType().equals(TransactionChargeType.LOSE)) {
      return removeHoldings(charge.getAmount(), charge.getCurrency(), charge.getWorld());
    }
    return addHoldings(charge.getAmount(), charge.getCurrency(), charge.getWorld());
  }

  /**
   * Used to determine if a call to handleCharge would be successful. This method does not affect an account's funds.
   * @param charge The {@link TransactionCharge} to handle.
   * @return True if a call to handleCharge would return true, otherwise false.
   */
  public boolean canCharge(TransactionCharge charge) {
    if(charge.getType().equals(TransactionChargeType.LOSE)) {
      return canRemoveHoldings(charge.getAmount(), charge.getCurrency(), charge.getWorld());
    }
    return canAddHoldings(charge.getAmount(), charge.getCurrency(), charge.getWorld());
  }
}