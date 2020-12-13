/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Control;

public class MaximizeSashMouseAdapter extends HeaderBarMouseAdapter {
  private SashForm sashForm;
  private Control control;

  public MaximizeSashMouseAdapter(CLabel cLabel, MenuManager menuManager, SashForm sashForm, Control control) {
    super(cLabel, menuManager);
    this.sashForm = sashForm;
    this.control = control;
  }

  public void mouseDoubleClick(MouseEvent e) {
    WidgetFactory.setMaximizedSashFormControl(sashForm, control);
  }
}
