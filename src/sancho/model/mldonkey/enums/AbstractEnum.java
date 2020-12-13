/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.enums;

import sancho.utility.SwissArmy;
import sancho.view.utility.SResources;

public abstract class AbstractEnum {
  protected int value;
  protected String name;

  protected AbstractEnum(int i, String resString) {
    value = i;
    name = (i == 0 ? SResources.S_ES : SResources.getString(resString)).intern();
  }

  public String getName() {
    return name != null ? name.intern() : SResources.S_ES;
  }

  public int getValue() {
    return value;
  }

  public byte getByteValue() {
    return (byte) getIntValue();
  }

  public int getIntValue() {
    return SwissArmy.log2(getValue());
  }

}