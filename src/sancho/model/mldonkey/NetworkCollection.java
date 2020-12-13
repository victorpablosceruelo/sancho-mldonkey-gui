/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import gnu.trove.TObjectProcedure;
import sancho.core.ICore;
import sancho.model.mldonkey.enums.EnumNetwork;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.utility.SwissArmy;

public class NetworkCollection extends ACollection_Int implements ICollection {

  NetworkCollection(ICore communication) {
    super(communication);
  }

  public Network getByEnum(EnumNetwork enumNetwork) {
    GetNetworkByEnum getNetworkByEnum = new GetNetworkByEnum(enumNetwork);
    forEachValue(getNetworkByEnum);
    return getNetworkByEnum.getNetwork();
  }

  public int getEnabledAndSearchable() {
    CountEnabledAndSearchable countEnabledAndSearchable = new CountEnabledAndSearchable();
    forEachValue(countEnabledAndSearchable);
    return countEnabledAndSearchable.getCount();
  }

  public EnumNetwork getNetworkEnum(int num) {
    Network network;
    if ((network = (Network) get(num)) != null)
      return network.getEnumNetwork();

    return EnumNetwork.UNKNOWN;
  }

  public String getAllNetworkStats(String nl) {
    GetNetworkStats g = new GetNetworkStats(nl);
    forEachValue(g);
    return g.getResultString();
  }

  public Network[] getNetworks() {
    Object[] oArray = getValues();
    Network[] networkArray = new Network[oArray.length];
    for (int i = 0; i < oArray.length; i++)
      networkArray[i] = (Network) oArray[i];
    return networkArray;
  }

  public void read(MessageBuffer messageBuffer) {
    int id = messageBuffer.getInt32();

    Network network = (Network) get(id);
    if (network != null) {
      network.read(id, messageBuffer);
    } else {
      network = core.getCollectionFactory().getNetwork();
      network.read(id, messageBuffer);
      put(id, network);
    }
    this.setChanged();
    this.notifyObservers(network);

  }

  protected void setConnectedServers(Network network, int num) {
    if (network == null)
      return;

    if (network.numConnectedServers() != num) {
      network.setConnectedServers(num);
      this.setChanged();
      this.notifyObservers(network);
    }
  }

  static class CountEnabledAndSearchable implements TObjectProcedure {

    private int counter;

    public boolean execute(Object object) {
      Network network = (Network) object;
      if (network.isEnabled() && network.isSearchable())
        counter++;
      return true;
    }

    public int getCount() {
      return counter;
    }
  }

  static class GetNetworkByEnum implements TObjectProcedure {
    EnumNetwork enumNetwork;

    Network foundNetworkInfo = null;

    public GetNetworkByEnum(EnumNetwork enumNetwork) {
      this.enumNetwork = enumNetwork;
    }

    public boolean execute(Object object) {
      Network network = (Network) object;
      if (network.equals(enumNetwork))
        foundNetworkInfo = network;
      return true;
    }

    public Network getNetwork() {
      return foundNetworkInfo;
    }
  }

  static class GetNetworkStats implements TObjectProcedure {
    StringBuffer stringBuffer = new StringBuffer();
    String nl;

    public GetNetworkStats(String nl) {
      this.nl = nl;
    }

    public boolean execute(Object object) {
      Network network = (Network) object;
      stringBuffer.append(nl + "Network: ");
      stringBuffer.append(network.getName());
      if (network.isEnabled())
        stringBuffer.append(" (enabled)" + nl);
      else
        stringBuffer.append(" (disabled)" + nl);

      stringBuffer.append("Downloaded: ");
      stringBuffer.append(SwissArmy.calcStringSize(network.getDownloaded()) + nl);
      stringBuffer.append("Uploaded: ");
      stringBuffer.append(SwissArmy.calcStringSize(network.getUploaded()) + nl);

      return true;
    }

    public String getResultString() {
      return stringBuffer.toString();
    }

  }

}