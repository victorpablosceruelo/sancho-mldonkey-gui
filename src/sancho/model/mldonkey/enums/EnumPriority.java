/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.enums;

public class EnumPriority extends AbstractEnum {
  public static final EnumPriority VERY_LOW = new EnumPriority(1, "very_low", -20);
  public static final EnumPriority LOW = new EnumPriority(2, "low", -10);
  public static final EnumPriority NORMAL = new EnumPriority(4, "normal", 0);
  public static final EnumPriority HIGH = new EnumPriority(8, "high", 10);
  public static final EnumPriority VERY_HIGH = new EnumPriority(16, "very_high", 20);
  int maxValue;

  private EnumPriority(int i, String resString, int maxValue) {
    super(i, "e.priority." + resString);
    this.maxValue = maxValue;
  }

  public int getMaxValue() {
    return maxValue;
  }

  public static EnumPriority intToEnum(int i) {
    if (i < EnumPriority.LOW.getMaxValue())
      return EnumPriority.VERY_LOW;
    else if (i < EnumPriority.NORMAL.getMaxValue())
      return EnumPriority.LOW;
    else if (i == EnumPriority.NORMAL.getMaxValue())
      return EnumPriority.NORMAL;
    else if (i <= EnumPriority.HIGH.getMaxValue())
      return EnumPriority.HIGH;
    else
      return EnumPriority.VERY_HIGH;
  }
}
