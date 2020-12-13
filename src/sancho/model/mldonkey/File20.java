/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;
import sancho.model.mldonkey.utility.OpCodes;

public class File20 extends File18 {

  File20(ICore core) {
    super(core);
  }

  public void rename(String string) {
    core.send(OpCodes.S_RENAME_FILE, new Object[]{new Integer(getId()), string});
  }
}