package net.tnemc.core.common.transrevamp.tax.type;


import net.tnemc.core.common.transrevamp.tax.TaxType;

import java.math.BigDecimal;

public class PercentileType implements TaxType {
  @Override
  public String name() {
    return "percent";
  }

  @Override
  public BigDecimal calculate(BigDecimal amount, BigDecimal tax) {
    return amount.add(amount.multiply(tax));
  }
}