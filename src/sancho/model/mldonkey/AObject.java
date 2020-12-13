/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import sancho.core.ICore;

public abstract class AObject {

  protected ICore core;

  AObject(ICore core) {
    this.core = core;
  }

  public ICore getCore() {
    return this.core;
  }

}