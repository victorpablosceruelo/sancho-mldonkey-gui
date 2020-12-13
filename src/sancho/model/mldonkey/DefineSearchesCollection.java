/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.MessageBuffer;

public class DefineSearchesCollection extends ACollection_Hash {

  DefineSearchesCollection(ICore core) {
    super(core);
  }

  // guiEncoding#defineSearches // not used
  public void read(MessageBuffer messageBuffer) {
    //    short len = messageBuffer.getUInt16();
    //    String string;
    //    Query query;
    //    for (int i = 0; i < len; i++) {
    //      string = messageBuffer.getString();
    //      query = UtilityFactory.getQuery(core);
    //      query.read(messageBuffer);
    //      put(string, query);
    //    }
  }

}