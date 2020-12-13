/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.search.result;

import java.util.Observable;

import org.eclipse.jface.viewers.Viewer;

import sancho.model.mldonkey.utility.SearchWaiting;
import sancho.utility.ObjectMap;
import sancho.utility.SwissArmy;
import sancho.view.utility.SResources;
import sancho.view.viewFrame.CTabFolderViewFrame;
import sancho.view.viewer.table.GTableContentProvider;

public class ResultTableContentProvider extends GTableContentProvider implements Runnable {

  private static final String RS_RESULTS = SResources.getString("t.search.results");

  private String searchWaitingString = SResources.S_ES;
  private boolean running;
  private boolean updateAdded;
  private ObjectMap objectMap;

  public ResultTableContentProvider(ResultTableView rTableViewer) {
    super(rTableViewer);
    running = true;
    updateAdded = false;
    new Thread(this).start();
  }

  public Object[] getElements(Object inputElement) {
    ObjectMap objectWeakMap = (ObjectMap) inputElement;

    synchronized (objectWeakMap) {
      objectWeakMap.clearAllLists();
      return objectWeakMap.getKeyArray();
    }
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.inputChanged(viewer, oldInput, newInput);

    if (oldInput != null)
      ((Observable) oldInput).deleteObserver(this);

    objectMap = (ObjectMap) newInput;

    if (newInput != null) {
      objectMap.addObserver(this);
      updateHeaderLabel(objectMap);
    }
  }

  public void dispose() {
    super.dispose();
    running = false;
  }

  public void update(Observable arg0, final Object arg1) {

    if (arg1 instanceof Integer) {

      final Integer updateType = (Integer) arg1;
      final ObjectMap objectWeakMap = (ObjectMap) arg0;

      if ((gView == null) || gView.isDisposed())
        return;

      switch (updateType.intValue()) {
        case ObjectMap.REMOVED :
          if (objectWeakMap.removed()) {
            tableViewer.getTable().getDisplay().asyncExec(new Runnable() { // sync
              public void run() {
                if ((tableViewer.getTable() == null) || tableViewer.getTable().isDisposed())
                  return;
                synchronized (objectWeakMap) {
                  tableViewer.remove(objectWeakMap.getRemovedArray());
                  objectWeakMap.clearRemoved();
                }

                updateHeaderLabel();
              }
            });
          }
          break;

        case ObjectMap.ADDED :
          updateAdded = true;
          break;

        case ObjectMap.UPDATED :
          if (objectWeakMap.updated()) {
            tableViewer.getTable().getDisplay().asyncExec(new Runnable() { // sync
              public void run() {
                if ((tableViewer.getTable() == null) || tableViewer.getTable().isDisposed())
                  return;

                synchronized (objectWeakMap) {
                  tableViewer.update(objectWeakMap.getUpdatedArray(), SResources.SA_Z);
                  objectWeakMap.clearUpdated();
                }
              }
            });
          }
          break;
      }
    } else if (arg1 instanceof SearchWaiting) {
      SearchWaiting searchWaiting = (SearchWaiting) arg1;
      searchWaitingString = " (Waiting: " + searchWaiting.getNumWaiting() + ")";
      tableViewer.getTable().getDisplay().asyncExec(new Runnable() { // sync
        public void run() {
          updateHeaderLabel();
        }
      });
    }
  }

  private void updateHeaderLabel() {
    if ((gView == null) || gView.isDisposed())
      return;
    updateHeaderLabel(((ObjectMap) gView.getViewer().getInput()));
  }

  private void updateHeaderLabel(ObjectMap objectMap) {
    if ((gView == null) || gView.isDisposed() || objectMap == null)
      return;
    CTabFolderViewFrame c = (CTabFolderViewFrame) gView.getViewFrame();
    if (c.getGView() == gView)
      gView.getViewFrame().updateCLabelText(
          RS_RESULTS + SResources.S_COLON + objectMap.size() + searchWaitingString);
  }

  public void run() {

    while (running) {
      SwissArmy.threadSleep(4444);
      if (updateAdded) {
        if (objectMap != null && objectMap.added()) {
          tableViewer.getTable().getDisplay().asyncExec(new Runnable() { // synced already
            public void run() {
              if ((tableViewer.getTable() == null) || tableViewer.getTable().isDisposed())
                return;
              synchronized (objectMap) {
                Object[] oArray = objectMap.getAddedArray();
                if (oArray.length < 99) {
                  tableViewer.add(oArray);
                  objectMap.clearAdded();
                } else {
                  gView.refresh();
                }
                updateAdded = false;
              }
              updateHeaderLabel();
            }
          });
        }
      }

    }
  }

}