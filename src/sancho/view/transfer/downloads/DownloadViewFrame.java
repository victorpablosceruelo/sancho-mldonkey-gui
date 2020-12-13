/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.downloads;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolItem;

import sancho.core.Sancho;
import sancho.utility.SwissArmy;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.SResources;
import sancho.view.viewFrame.TabbedSashViewFrame;
import sancho.view.viewer.CustomTableTreeViewer;

public class DownloadViewFrame extends TabbedSashViewFrame {

  public DownloadViewFrame(SashForm parentSashForm, String prefString, String prefImageString,
      AbstractTab aTab) {
    super(parentSashForm, prefString, prefImageString, aTab, "downloads");
    
    gView = new DownloadTableTreeView(this);
    createViewListener(new DownloadViewListener(this));
    createViewToolBar();
    
    switchToTab(cTabFolder.getItems()[0]);
  }
 
  public void createViewToolBar() {
    super.createViewToolBar();

    if (!PreferenceLoader.loadString("explorerExecutable").equals(SResources.S_ES))
      addToolItem("ti.d.fileExplorer", "file-explorer", new SelectionAdapter() {
        public void widgetSelected(SelectionEvent s) {
          String explorer = PreferenceLoader.loadString("explorerExecutable");
          String downloadPath = PreferenceLoader.loadString("explorerOpenFolder");

          if (!explorer.equals(SResources.S_ES)) {
            String cmdArray[] = new String[2];
            cmdArray[0] = explorer;
            cmdArray[1] = downloadPath;
            SwissArmy.execInThread(cmdArray, null);
          }
        }
      });

    addToolItem("ti.d.commitAll", "commit", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        if (Sancho.hasCollectionFactory())
          getCore().getFileCollection().commitAll();
      }
    });

    addToolItem("ti.d.toggleClients", "split-table", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        ((DownloadTableTreeView) gView).toggleClientsTable();
      }
    });

    addToolItem("ti.d.collapseAll", "collapseall", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        ((CustomTableTreeViewer) gView.getViewer()).collapseAll();
      }
    });

    addToolItem("ti.d.expandAll", "expandall", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        ((CustomTableTreeViewer) gView.getViewer()).expandAll();
      }
    });

    new ToolItem(toolBar, SWT.SEPARATOR);
    addRefine();
  }

  public Control getControl() {
    return super.getParentSashForm();
  }

  public CTabFolder getCTabFolder() {
    return cTabFolder;
  }

  public SashForm getParentSashForm() {
    return getParentSashForm(true);
  }

  public SashForm getParentSashForm(boolean grandParent) {
    if (grandParent)
      return (SashForm) super.getParentSashForm().getParent();
    else
      return super.getParentSashForm();
  }

 

}