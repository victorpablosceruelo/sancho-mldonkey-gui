/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class NoDuplicatesCombo extends Combo {
  public NoDuplicatesCombo(Composite parent, int style) {
    super(parent, style);
  }

  public void add(String string, int index) {
    if (string.equals(SResources.S_ES))
      return;

    if (indexOf(string) != -1)
      remove(string);

    super.add(string, index);
  }

  protected void checkSubclass() {
  }
}
