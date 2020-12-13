/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.search.result;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import sancho.core.Sancho;
import sancho.model.mldonkey.ResultCollection;
import sancho.model.mldonkey.utility.SearchWaiting;
import sancho.utility.ObjectMap;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;
import sancho.view.viewer.CustomTableViewer;
import sancho.view.viewer.GView;

public class ResultTab implements Observer, Runnable, DisposeListener {
  private boolean hasTable;
  private CTabFolder cTabFolder;
  private CTabItem cTabItem;
  private GView gView;
  private boolean paused;
  private int searchId;
  private Composite searchingComposite;
  private Label searchingLabel;
  private String searchString;
  private AbstractTab searchTab;
  private ResultViewFrame viewFrame;

  public ResultTab(ResultViewFrame viewFrame, CTabFolder cTabFolder, AbstractTab searchTab, int searchId,
      String string) {
    this.searchString = string;
    this.cTabFolder = cTabFolder;
    this.searchId = searchId;
    this.searchTab = searchTab;
    this.viewFrame = viewFrame;

    createContent();

    if (Sancho.hasCollectionFactory())
      viewFrame.getCore().getResultCollection().addObserver(this);

    viewFrame.onCTabFolderSelection();
  }

  private void createContent() {
    cTabItem = new CTabItem(cTabFolder, SWT.FLAT);
    viewFrame.updateCLabelText(SResources.getString("t.search.results"));

    cTabItem.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        viewFrame.onCTabFolderSelection();
        if (cTabFolder.getItemCount() == 0)
          viewFrame.updateCLabelText(SResources.getString("t.search.results"));
      }
    });

    cTabItem.addDisposeListener(this);
    cTabItem.setText(searchString);
    cTabItem.setToolTipText(SResources.getString("s.r.searchingFor") + searchString);
    cTabItem.setImage(SResources.getImage("search_small"));
    cTabItem.setData(this);

    searchingComposite = new Composite(cTabFolder, SWT.NONE);
    searchingComposite.setLayout(WidgetFactory.createGridLayout(1, 5, 5, 5, 5, false));

    searchingLabel = new Label(searchingComposite, SWT.NONE);
    searchingLabel.setText(SResources.getString("s.r.searching"));
    searchingLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    cTabItem.setControl(searchingComposite);

    cTabFolder.setSelection(cTabItem);
  }

  private void createTable() {
    cTabItem.setImage(SResources.getImage(searchId < 0 ? "jigle" : "search_complete"));
    this.gView = new ResultTableView(viewFrame, cTabItem, searchTab);
    cTabItem.setControl(((CustomTableViewer) gView.getViewer()).getTable());
  }

  public boolean isPaused() {
    return paused;
  }

  public void pause() {
    this.paused = true;
    if (Sancho.hasCollectionFactory()) {
      ObjectMap objectMap = (ObjectMap) viewFrame.getCore().getResultCollection().get(searchId);
      if (objectMap != null)
        objectMap.deleteObservers();
    }
  }

  public void run() {
    if (cTabItem.isDisposed() || this.paused || !Sancho.hasCollectionFactory())
      return;

    if (searchingLabel != null && !searchingLabel.isDisposed()) {
      searchingLabel.dispose();
      searchingComposite.dispose();
    }
    this.createTable();
    if (Sancho.hasCollectionFactory())
      gView.getViewer().setInput(viewFrame.getCore().getResultCollection().get(searchId));

  }

  public void unPause() {
    this.paused = false;
    if (Sancho.hasCollectionFactory() && gView != null && !gView.isDisposed())
      gView.getViewer().setInput(viewFrame.getCore().getResultCollection().get(searchId));
  }

  public void update(Observable o, final Object arg) {
    if (cTabItem == null || cTabItem.isDisposed() || isPaused())
      return;

    if (arg instanceof SearchWaiting) {
      final SearchWaiting searchWaiting = (SearchWaiting) arg;
      if (searchWaiting.getId() == searchId && searchingLabel != null && !searchingLabel.isDisposed())
        searchingLabel.getDisplay().asyncExec(new Runnable() {
          public void run() {
            if (searchingLabel != null && !searchingLabel.isDisposed()) {
              searchingLabel.setText(SResources.getString("s.r.searchesWaiting")
                  + searchWaiting.getNumWaiting());
              searchingLabel.getParent().layout();
            }

          }
        });
    }

    //    else if (arg instanceof JigleSearchComplete) {
    //      final JigleSearchComplete jigleSearchComplete = (JigleSearchComplete) arg;
    //      if (jigleSearchComplete.getSearchID() == searchId && searchingLabel != null
    //          && !searchingLabel.isDisposed())
    //        searchingLabel.getDisplay().asyncExec(new Runnable() {
    //          public void run() {
    //            if (searchingLabel != null && !searchingLabel.isDisposed())
    //              searchingLabel.setText(Resources.getString("s.r.jigleComplete") + " ("
    //                  + jigleSearchComplete.getCode() + ")");
    //          }
    //        });
    //    }

    if (!hasTable && gView == null && ((ResultCollection) o).containsKey(searchId)) {
      if (Sancho.hasCollectionFactory() && viewFrame.getCore().getResultCollection() != null)
        viewFrame.getCore().getResultCollection().deleteObserver(this);
      hasTable = true;
      cTabFolder.getDisplay().asyncExec(this);
    }
  }

  public void widgetDisposed(DisposeEvent e) {
    if (Sancho.hasCollectionFactory()) {
      viewFrame.getCore().getResultCollection().deleteObserver(this);
      viewFrame.getCore().getResultCollection().closeSearch(searchId);
    }
  }

}