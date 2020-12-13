/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.pending;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolItem;

import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;
import sancho.view.viewFrame.SashViewFrame;

public class PendingViewFrame extends SashViewFrame {
  public PendingViewFrame(SashForm parentSashForm, String prefString, String prefImageString, AbstractTab aTab) {
    super(parentSashForm, prefString, prefImageString, aTab);

    gView = new PendingTableView(this);
    createViewListener(new PendingViewListener(this));
    createViewToolBar();
  }

  public void createViewToolBar() {
    super.createViewToolBar();

    final ToolItem toolItem = new ToolItem(toolBar, SWT.NONE);

    toolItem.setImage(SResources.getImage("plus"));
    toolItem.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        boolean b = !PreferenceLoader.loadBoolean("pollPending");
        PreferenceLoader.getPreferenceStore().setValue("pollPending", b);
        PreferenceLoader.saveStore();

        if (getCore() != null)
          getCore().updatePreferences();
        toggleActive(toolItem, b);

        if (b)
          gView.setInput();
        else {
          gView.getViewer().setInput(null);
          WidgetFactory.setMaximizedSashFormControl(getParentSashForm(), 0);
        }
      }
    });
    toggleActive(toolItem, PreferenceLoader.loadBoolean("pollPending"));
    
    new ToolItem(toolBar, SWT.SEPARATOR);
    addRefine();
  }

  public void toggleActive(ToolItem toolItem, boolean b) {
    toolItem.setImage(SResources.getImage(b ? "minus" : "plus"));
    toolItem.setToolTipText(SResources.getString(b ? "l.disableTable" : "l.enableTable"));
  }

}