/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.enums.EnumNetwork;
import sancho.model.mldonkey.utility.MessageBuffer;

public class OptionCollection extends ACollection_Hash {
  private static final String S_MAX_U = "max_ultrapeers";
  private static final String S_MAX_S = "max_connected_servers";

  OptionCollection(ICore core) {
    super(core);
  }

  public int getMaxConnected(Network network) {
    if (network.getEnumNetwork() == EnumNetwork.SOULSEEK)
      return 1;

    // TODO: core must send this
    try {
      if (network.hasServers() || network.hasSupernodes()) {
        String defaultPrefix = network.getEnumNetwork().getDefaultOptionPrefix();
        return Integer.parseInt(((Option) get(defaultPrefix + (network.hasSupernodes() ? S_MAX_U : S_MAX_S)))
            .getValue());
      } else
        return 1;
    } catch (Exception e) {
      return 1;
    }
  }

  public void read(MessageBuffer messageBuffer) {
    int len = messageBuffer.getUInt16();
    String key;
    Option option;

    for (int i = 0; i < len; i++) {
      key = messageBuffer.getString();
      option = (Option) get(key);

      if (option == null)
        option = core.getCollectionFactory().getOption();

      option.read(key, messageBuffer);
      put(key, option);
    }
    this.setChanged();
    this.notifyObservers();
  }

  public void addSectionOption(MessageBuffer messageBuffer) {
    String section = messageBuffer.getString();
    String desc = messageBuffer.getString();
    String name = messageBuffer.getString();

    Option option = (Option) get(name);
    if (option != null)
      option.addSectionOption(section, desc, name, messageBuffer);
  }

  public void addPluginOption(MessageBuffer messageBuffer) {
    String plugin = messageBuffer.getString();
    String desc = messageBuffer.getString();
    String name = messageBuffer.getString();

    Option option = (Option) get(name);
    if (option != null)
      option.addPluginOption(plugin, desc, name, messageBuffer);
  }

}