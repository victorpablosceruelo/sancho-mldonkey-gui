/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.enums.EnumTagType;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.view.utility.SResources;

public class Option extends AObject {

  protected String description;
  protected String name;
  protected String plugin;
  protected String section;
  protected EnumTagType type;
  protected String value;

  Option(ICore core) {
    super(core);
  }

  public synchronized void addPluginOption(String plugin, String desc, String name,
      MessageBuffer messageBuffer) {
    this.plugin = plugin;
    this.description = desc;
    this.name = name;
    this.type = readType(messageBuffer);
  }

  public synchronized void addSectionOption(String section, String desc, String name,
      MessageBuffer messageBuffer) {
    this.section = section;
    this.description = desc;
    this.name = name;
    this.type = readType(messageBuffer);
  }

  public String getDefaultValue() {
    return SResources.S_ES;
  }

  public synchronized String getDescription() {
    return description != null ? description : SResources.S_ES;
  }

  public synchronized String getName() {
    return name != null ? name : SResources.S_ES;
  }

  public synchronized String getPlugin() {
    return plugin ;
  }

  // null OK (prefs)
  public synchronized String getSection() {
    return section;
  }

  public synchronized EnumTagType getType() {
    return type;
  }

  public synchronized String getValue() {
    return value;
  }

  public boolean isAdvanced() {
    return false;
  }

  // guiEncoding#Options_info
  public void read(MessageBuffer messageBuffer) {
    read(messageBuffer.getString(), messageBuffer);
  }

  public void read(String key, MessageBuffer messageBuffer) {
    synchronized (this) {
      this.name = key;
      this.value = messageBuffer.getString();
    }
    
  }

  protected EnumTagType readType(MessageBuffer messageBuffer) {
    return EnumTagType.optionByteToEnum(messageBuffer.getByte());
  }

  public void setValue(String newValue) {
    core.send(OpCodes.S_SET_OPTION, new Object[]{getName(), newValue});
  }

}