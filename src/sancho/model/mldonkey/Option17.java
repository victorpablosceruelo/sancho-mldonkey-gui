/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.enums.EnumTagType;
import sancho.model.mldonkey.utility.MessageBuffer;

public class Option17 extends Option {
  private boolean advanced;
  private String optionDefault;
  private String optionHelp;

  public Option17(ICore core) {
    super(core);
  }

  public void addPluginOption(String plugin, String desc, String name, MessageBuffer messageBuffer) {
    super.addPluginOption(plugin, desc, name, messageBuffer);
    synchronized (this) {
      this.optionHelp = messageBuffer.getString();
      this.value = messageBuffer.getString();
      this.optionDefault = messageBuffer.getString();
      this.advanced = messageBuffer.getBool();
    }
  }

  public void addSectionOption(String section, String desc, String name, MessageBuffer messageBuffer) {
    super.addSectionOption(section, desc, name, messageBuffer);
    synchronized (this) {
      this.optionHelp = messageBuffer.getString();
      this.value = messageBuffer.getString();
      this.optionDefault = messageBuffer.getString();
      this.advanced = messageBuffer.getBool();
    }
  }

  public synchronized String getDefaultValue() {
    return this.optionDefault;
  }

  public synchronized String getDescription() {
    return this.optionHelp;
  }

  public synchronized boolean isAdvanced() {
    return this.advanced;
  }

  protected EnumTagType readType(MessageBuffer messageBuffer) {
    return EnumTagType.stringToEnum(messageBuffer.getString());
  }

}