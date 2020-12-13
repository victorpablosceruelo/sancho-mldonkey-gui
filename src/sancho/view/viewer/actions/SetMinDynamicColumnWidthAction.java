/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

import sancho.view.utility.SResources;
import sancho.view.utility.Spinner;
import sancho.view.utility.WidgetFactory;
import sancho.view.viewer.GView;

public class SetMinDynamicColumnWidthAction extends Action {
  GView gView;

  public SetMinDynamicColumnWidthAction(GView gView) {
    super(SResources.getString("mi.setMinWidth"));
    this.gView = gView;
  }

  public void run() {
    MinWidthDialog minWidthDialog = new MinWidthDialog(gView.getShell(), SResources
        .getString("mi.setMinWidth"), gView.getMinDynamicColumnWidth());
    if (minWidthDialog.open() == MinWidthDialog.OK)
      gView.setMinDynamicColumnWidth(minWidthDialog.getIntValue());
  }

  static class MinWidthDialog extends Dialog {
    int initialValue;
    int intValue;
    String title;
    Spinner spinner;

    public MinWidthDialog(Shell parentShell, String dialogTitle, int initialValue) {
      super(parentShell);
      this.initialValue = initialValue;
      this.title = dialogTitle;
    }

    protected void configureShell(Shell newShell) {
      super.configureShell(newShell);
      newShell.setImage(SResources.getImage("ProgramIcon"));
      newShell.setText(title);
    }

    protected Control createDialogArea(Composite parent) {

      Composite composite = (Composite) super.createDialogArea(parent);
      composite.setLayout(WidgetFactory.createGridLayout(2, 5, 5, 10, 5, false));

      spinner = new Spinner(composite, SWT.NONE);
      spinner.setMaximum(1000);
      spinner.setSelection(initialValue);

      final Scale scale = new Scale(composite, SWT.HORIZONTAL);
      scale.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      scale.setMinimum(0);
      scale.setMaximum(1000);
      scale.setSelection(initialValue);
      scale.setIncrement(1);
      scale.setPageIncrement(5);

      scale.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
          int intValue = (scale.getSelection());
          spinner.setSelection(intValue);
        }
      });

      return composite;
    }

    protected void buttonPressed(int buttonId) {
      intValue = spinner.getSelection();
      super.buttonPressed(buttonId);
    }

    public int getIntValue() {
      return intValue;
    }

  }

}
