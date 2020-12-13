/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.search.result;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolItem;

import sancho.core.Sancho;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;
import sancho.view.viewFrame.CTabFolderViewFrame;

public class ResultViewFrame extends CTabFolderViewFrame {

  ToolItem pauseContinueToolItem;
  ToolItem extendSearchToolItem;
  ToolItem closeAllTabsToolItem;

  public ResultViewFrame(SashForm parentSashForm, String prefString, String prefImageString, AbstractTab aTab) {
    super(parentSashForm, prefString, prefImageString, aTab);
    createViewListener(new ResultViewListener(this));
    createViewToolBar();
  }

  public void onCTabFolderSelection() {
    super.onCTabFolderSelection();
    updatePauseContinue();
    updateExtendSearch();
    closeAllTabsToolItem.setEnabled(cTabFolder.getItemCount() > 0);
  }

  protected CTabFolder createCTabFolder() {
    boolean onTop = PreferenceLoader.loadBoolean("resultsCTabFolderTabsOnTop");
    CTabFolder cTabFolder = WidgetFactory.createCTabFolder(childComposite, SWT.CLOSE
        | (onTop ? SWT.TOP : SWT.BOTTOM));
    WidgetFactory.addCTabFolderMenu(cTabFolder, "resultsCTabFolder");
    return cTabFolder;
  }

  public void createViewToolBar() {
    super.createViewToolBar();
    addCloseAllTabs();
    addExtendSearch();
    addPauseContinue();
    addRefine();
  }

  public void addCloseAllTabs() {
    closeAllTabsToolItem = new ToolItem(toolBar, SWT.NONE);
    closeAllTabsToolItem.setImage(SResources.getImage("x"));
    closeAllTabsToolItem.setToolTipText(SResources.getString("ti.f.closeAllTabs"));
    closeAllTabsToolItem.setEnabled(false);
    closeAllTabsToolItem.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        CTabItem[] cTabItems = cTabFolder.getItems();
        for (int i = 0; i < cTabItems.length; i++) {
          cTabItems[i].dispose();
        }
      }
    });
  }

  public void addExtendSearch() {
    extendSearchToolItem = new ToolItem(toolBar, SWT.NONE);
    extendSearchToolItem.setImage(SResources.getImage("plus"));
    extendSearchToolItem.setToolTipText(SResources.getString("ti.extendSearch"));
    extendSearchToolItem.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        Sancho.send(OpCodes.S_EXTEND_SEARCH);
      }
    });

    updateExtendSearch();
  }

  public void updateExtendSearch() {
    extendSearchToolItem.setEnabled(getGView() != null);
  }

  public void addPauseContinue() {
    pauseContinueToolItem = new ToolItem(toolBar, SWT.NONE);
    togglePauseContinue(false);
    pauseContinueToolItem.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        CTabItem cTabItem = cTabFolder.getSelection();
        if (cTabItem == null)
          return;
        ResultTab resultTab = (ResultTab) cTabItem.getData();
        if (resultTab == null)
          return;

        if (resultTab.isPaused()) {

          togglePauseContinue(false);
          resultTab.unPause();
        } else {
          resultTab.pause();
          togglePauseContinue(true);
        }
      }
    });
    ToolItem sep = new ToolItem(toolBar, SWT.SEPARATOR);

    if (getGView() == null)
      pauseContinueToolItem.setEnabled(false);
  }

  public void togglePauseContinue(boolean isPaused) {
    pauseContinueToolItem.setImage(SResources.getImage(isPaused ? "forward" : "pause"));
    pauseContinueToolItem.setToolTipText(SResources.getString(isPaused
        ? "ti.continueSearch"
        : "ti.pauseSearch"));
  }

  public void updatePauseContinue() {
    if (getGView() != null) {

      CTabItem cTabItem = cTabFolder.getSelection();
      if (cTabItem == null)
        return;
      ResultTab resultTab = (ResultTab) cTabItem.getData();
      if (resultTab == null)
        return;

      togglePauseContinue(resultTab.isPaused());
      pauseContinueToolItem.setEnabled(true);
    } else {
      togglePauseContinue(false);
      pauseContinueToolItem.setEnabled(false);
    }
  }
}