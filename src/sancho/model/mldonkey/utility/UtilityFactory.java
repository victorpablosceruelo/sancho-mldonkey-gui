/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.utility;

import sancho.core.ICore;

public class UtilityFactory {

  public static Addr getAddr() {
    return new Addr();
  }

  public static ClientMessage getClientMessage(ICore core) {
    return new ClientMessage();
  }

  public static FileState getFileState(ICore core) {
    return new FileState();
  }

  public static Format getFormat(ICore core) {
    return new Format();
  }

  public static Kind getKind(ICore core) {
    return new Kind();
  }

  public static Query getQuery(ICore core) {
    return new Query(core);
  }

  public static RoomMessage getRoomMessage(ICore core) {
    return new RoomMessage();
  }

  public static SearchWaiting getSearchWaiting(ICore core) {
    return new SearchWaiting();
  }

  public static HostState getHostState(ICore core) {
    if (core.getProtocol() >= 21)
      return new HostState21();
    return new HostState();
  }

  public static Tag getTag(ICore core) {
    return new Tag();
  }

}
