/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.search.result;

import java.util.Arrays;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import sancho.model.mldonkey.Result;
import sancho.view.utility.WidgetFactory;

public class ResultDetailDialog extends Dialog {
  Result result;

  public ResultDetailDialog(Shell shell, Result result) {
    super(shell);
    this.result = result;
  }

  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setImage(result.getToolTipImage());
    newShell.setText(result.getName());
  }

  protected void createButtonsForButtonBar(Composite parent) {
    // create OK and Cancel buttons by default
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
  }

  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);
    composite.setLayout(WidgetFactory.createGridLayout(1, 5, 5, 0, 5, false));

    Text text = new Text(composite, SWT.MULTI | SWT.BORDER);
    text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    text.setText(result.getToolTip());

    String[] originalNames = result.getNames();

    if ((originalNames != null) && (originalNames.length > 1)) {
      GC gc = new GC(parent);
      int charHeight = gc.getFontMetrics().getHeight();
      gc.dispose();

      int numToDisplay = result.getNames().length;
      numToDisplay = (numToDisplay > 6) ? 6 : numToDisplay;

      String[] names = new String[originalNames.length];
      System.arraycopy(originalNames, 0, names, 0, originalNames.length);
      Arrays.sort(names, String.CASE_INSENSITIVE_ORDER);

      List namesList = new List(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
      namesList.setItems(names);

      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.heightHint = numToDisplay * (charHeight);
      namesList.setLayoutData(gridData);
    }

    return composite;
  }

}
