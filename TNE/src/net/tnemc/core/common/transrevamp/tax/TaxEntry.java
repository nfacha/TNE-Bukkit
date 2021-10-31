package net.tnemc.core.common.transrevamp.tax;

import net.tnemc.core.common.transrevamp.charge.ChargeEntry;

import java.math.BigDecimal;

public class TaxEntry {

  /**
   * The {@link TaxType} associated with this {@link TaxEntry}.
   */
  private TaxType type;

  /**
   * The {@link ChargeEntry} charge associated with this tax entry.
   */
  private ChargeEntry charge;

  public TaxEntry(TaxType type, String currency, String world, BigDecimal amount) {
    this.type = type;
    this.charge = new ChargeEntry(world, currency, amount, null);
  }

  public TaxEntry(TaxType type, ChargeEntry charge) {
    this.type = type;
    this.charge = charge;
  }

  public TaxType getType() {
    return type;
  }

  public void setType(TaxType type) {
    this.type = type;
  }

  public ChargeEntry getCharge() {
    return charge;
  }

  public void setCharge(ChargeEntry charge) {
    this.charge = charge;
  }
}