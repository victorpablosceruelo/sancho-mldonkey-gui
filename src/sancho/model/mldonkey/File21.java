/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;

public class File21 extends File20 {

  File21(ICore core) {
    super(core);
  }

  protected boolean checkFileNum(Client client) {
    return client.isTransferring(getId());
  }

}