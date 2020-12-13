/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Control;

import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public class MaximizeAction extends AbstractSashAction {
  private Control control;

  public MaximizeAction(SashForm aSashForm, Control control, String showResString) {
    super(aSashForm);
    this.control = control;

    if (sashForm.getMaximizedControl() == null) {
      setText(SResources.getString("mi.maximize"));
      setImageDescriptor(SResources.getImageDescriptor("maximize"));
    } else {
      setText(SResources.getString("mi.show") + " " + SResources.getString(showResString));
      setImageDescriptor(SResources.getImageDescriptor("restore"));
    }
  }

  public void run() {
    WidgetFactory.setMaximizedSashFormControl(sashForm, control);
  }
}
