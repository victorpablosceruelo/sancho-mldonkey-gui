/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewFrame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import sancho.view.utility.AbstractTab;
import sancho.view.utility.MaximizeSashMouseAdapter;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;
import sancho.view.viewer.GView;

public class CTabFolderViewFrame extends SashViewFrame {
  protected CTabFolder cTabFolder;

  public CTabFolderViewFrame(SashForm parentSashForm, String prefString, String prefImageString,
      AbstractTab aTab) {
    super(parentSashForm, prefString, prefImageString, aTab);
    this.cTabFolder = createCTabFolder();

    this.cTabFolder.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        onCTabFolderSelection();
      }
    });
  }

  protected CTabFolder createCTabFolder() {
    return WidgetFactory.createCTabFolder(childComposite, SWT.NONE);
  }

  public void createViewListener(CTabFolderViewListener cTabFolderViewFrameListener) {
    setupViewListener(cTabFolderViewFrameListener);

    cLabel.addMouseListener(new MaximizeSashMouseAdapter(cLabel, menuManager, getParentSashForm(),
        getControl()));
  }

  public void onCTabFolderSelection() {
    updateRefine();
  }

  public CTabFolder getCTabFolder() {
    return cTabFolder;
  }

  public GView getGView() {
    if (((cTabFolder != null) && !cTabFolder.isDisposed() && (cTabFolder.getSelection() != null) && !cTabFolder
        .getSelection().isDisposed())
        && (cTabFolder.getSelection().getData(GView.S_GVIEW) != null))
      return (GView) cTabFolder.getSelection().getData(GView.S_GVIEW);

    return null;
  }

  public void updateDisplay() {
    super.updateDisplay();
    for (int i = 0; i < cTabFolder.getItems().length; i++) {
      CTabItem tabItem = cTabFolder.getItems()[i];

      if (tabItem.getData(GView.S_GVIEW) != null) {
        // GView gView = (GView) cTabFolder.getSelection().getData(GView.S_GVIEW);
        GView gView = (GView) tabItem.getData(GView.S_GVIEW);
        if (gView != null)
          gView.updateDisplay();
      }
    }
  }

  public void updateRefine() {
    if (refineText != null)
      if (getGView() != null) {
        refineText.setText(getGView().getRefineString());
        refineText.setEnabled(true);
        clearRefineToolItem.setEnabled(true);
      } else {
        refineText.setText(SResources.S_ES);
        refineText.setEnabled(false);
        clearRefineToolItem.setEnabled(false);
      }
  }

  public void onDisconnect() {
    for (int i = 0; i < cTabFolder.getItems().length; i++) {
      CTabItem tabItem = cTabFolder.getItems()[i];

      if (tabItem.getData(GView.S_GVIEW) != null) {
        // GView gView = (GView) cTabFolder.getSelection().getData(GView.S_GVIEW);
        GView gView = (GView) tabItem.getData(GView.S_GVIEW);
        gView.unsetInput();
      }
    }
    super.onDisconnect();
  }

  public void onConnect() {
    for (int i = 0; i < cTabFolder.getItems().length; i++) {
      CTabItem tabItem = cTabFolder.getItems()[i];

      if (tabItem.getData(GView.S_GVIEW) != null) {
        //  GView gView = (GView) cTabFolder.getSelection().getData(GView.S_GVIEW);
        GView gView = (GView) tabItem.getData(GView.S_GVIEW);
        gView.setInput();
      }
    }
  }
}