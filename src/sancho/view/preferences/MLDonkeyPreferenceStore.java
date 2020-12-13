/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.preferences;

import org.eclipse.jface.preference.PreferenceStore;

import sancho.model.mldonkey.Option;
import sancho.model.mldonkey.OptionCollection;
import sancho.view.utility.SResources;

public class MLDonkeyPreferenceStore extends PreferenceStore {
  private OptionCollection optionCollection;

  public boolean contains(String name) {
    return optionCollection.containsKey(name);
  }

  public Option getOption(String name) {
    return (Option) optionCollection.get(name);
  }

  public boolean getBoolean(String name) {
    return contains(name) ? Boolean.valueOf((getOption(name)).getValue()).booleanValue() : false;
  }

  public boolean getDefaultBoolean(String name) {
    return contains(name) ? Boolean.valueOf((getOption(name)).getDefaultValue()).booleanValue() : false;
  }

  public int getDefaultInt(String name) {
    try {
      return contains(name) ? Integer.parseInt(getOption(name).getDefaultValue()) : 0;
    } catch (Exception e) {
      return 0;
    }
  }

  public String getDefaultString(String name) {
    return contains(name) ? getOption(name).getDefaultValue() : SResources.S_ES;
  }

  public int getInt(String name) {
    try {
      return contains(name) ? Integer.parseInt(getOption(name).getValue()) : 0;
    } catch (Exception e) {
      return 0;
    }
  }

  public String getString(String name) {
    return contains(name) ? getOption(name).getValue() : SResources.S_ES;
  }

  public void setInput(OptionCollection optionCollection) {
    this.optionCollection = optionCollection;
  }

  public void setToDefault(String name) {
    setValue(name, getDefaultString(name));
  }

  public void setValue(String name, boolean value) {
    setValue(name, value ? "true" : "false");
  }

  public void setValue(String name, int value) {
    setValue(name, String.valueOf(value));
  }

  public void setValue(String name, String value) {
    String oldValue = getString(name);
    if (oldValue == null || !oldValue.equals(value))
      getOption(name).setValue(value);
  }
}
