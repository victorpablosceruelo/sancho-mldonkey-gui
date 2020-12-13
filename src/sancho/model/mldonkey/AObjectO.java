/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import java.util.Observable;

import sancho.core.ICore;

public abstract class AObjectO extends Observable implements IObject {

  protected ICore core;

  AObjectO(ICore core) {
    this.core = core;
  }

  public ICore getCore() {
    return this.core;
  }

}