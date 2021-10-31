package net.tnemc.core.common.transrevamp.tax.type;


import net.tnemc.core.common.transrevamp.tax.TaxType;

import java.math.BigDecimal;

public class FlatType implements TaxType {
  @Override
  public String name() {
    return "flat";
  }

  @Override
  public BigDecimal calculate(BigDecimal amount, BigDecimal tax) {
    return amount.add(tax);
  }
}