package net.tnemc.core.common.transrevamp.tax;

import java.math.BigDecimal;

public interface TaxType {

  /**
   * @return The name of this TaxType
   */
  String name();

  /**
   * Used to calculate the tax to be applied to the specified amount.
   * @param amount The amount to use for the calculation.
   * @param tax The tax percent to use for the calculation.
   * @return The resulting BigDecimal representing the tax for the specified amount.
   */
  BigDecimal calculate(BigDecimal amount, BigDecimal tax);
}