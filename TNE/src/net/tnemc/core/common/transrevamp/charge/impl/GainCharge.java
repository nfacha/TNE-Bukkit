package net.tnemc.core.common.transrevamp.charge.impl;

import net.tnemc.core.common.transrevamp.charge.ChargeType;

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
public class GainCharge implements ChargeType {
  /**
   * Used to calculate a resulting BigDecimal, result, with a starting amount and an amount to use for
   * the calculation.
   *
   * @param starting The BigDecimal representing the starting amount.
   * @param amount   The BigDecimal representing the amount to use for the calculation.
   * @return The resulting BigDecimal after the calculation is finished.
   */
  @Override
  public BigDecimal calculate(BigDecimal starting, BigDecimal amount) {
    return starting.add(amount);
  }
}
