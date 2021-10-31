package net.tnemc.core.common.transrevamp.charge;

import java.math.BigDecimal;

/**
 * The New Economy Minecraft Server Plugin
 * <p>
 * Created by creatorfromhell on 10/23/2021.
 * <p>
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/ or send a letter to
 * Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * Created by creatorfromhell on 06/30/2017.
 */
public class ChargeEntry {

  /**
   * The world this charge entry is associated with.
   */
  private String world;

  /**
   * The name of the currency to use for this charge entry.
   */
  private String currency;

  /**
   * The amount associated with this charge entry.
   */
  private BigDecimal amount;

  /**
   * The {@link ChargeType} associated with this Charge Type.
   */
  private ChargeType type;

  public ChargeEntry(String world, String currency, BigDecimal amount, ChargeType type) {
    this.world = world;
    this.currency = currency;
    this.amount = amount;
    this.type = type;
  }

  public String getWorld() {
    return world;
  }

  public void setWorld(String world) {
    this.world = world;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public ChargeType getType() {
    return type;
  }

  public void setType(ChargeType type) {
    this.type = type;
  }
}