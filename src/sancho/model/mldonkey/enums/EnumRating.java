/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.enums;

import org.eclipse.swt.graphics.Image;

import sancho.view.utility.SResources;

public class EnumRating extends AbstractEnum {

  private static final String S_I_FAKE = "epRatingFake";
  private static final String S_I_EXCELLENT = "epRatingExcellent";
  private static final String S_I_GOOD = "epRatingGood";
  private static final String S_I_FAIR = "epRatingFair";
  private static final String S_I_POOR = "epRatingPoor";

  public static final EnumRating EXCELLENT = new EnumRating(1, "excellent");
  public static final EnumRating VERY_HIGH = new EnumRating(2, "veryHigh");
  public static final EnumRating HIGH = new EnumRating(4, "high");
  public static final EnumRating NORMAL = new EnumRating(8, "normal");
  public static final EnumRating LOW = new EnumRating(16, "low");
  public static final EnumRating FAKE = new EnumRating(32, "fake");

  private EnumRating(int i, String resString) {
    super(i, "e.rating." + resString);
  }

  public static EnumRating intToEnum(int i) {
    if (i > 100)
      return EnumRating.EXCELLENT;
    else if (i > 50)
      return EnumRating.VERY_HIGH;
    else if (i > 10)
      return EnumRating.HIGH;
    else if (i > 5)
      return EnumRating.NORMAL;
    else
      return EnumRating.LOW;
  }

  public Image getImage() {
    if (this == EnumRating.FAKE)
      return SResources.getImage(S_I_FAKE);
    else if (this == EnumRating.EXCELLENT)
      return SResources.getImage(S_I_EXCELLENT);
    else if (this == EnumRating.VERY_HIGH)
      return SResources.getImage(S_I_EXCELLENT);
    else if (this == EnumRating.HIGH)
      return SResources.getImage(S_I_GOOD);
    else if (this == EnumRating.NORMAL)
      return SResources.getImage(S_I_FAIR);
    else
      return SResources.getImage(S_I_POOR);
  }
}