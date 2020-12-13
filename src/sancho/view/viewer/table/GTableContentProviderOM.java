/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.table;

import sancho.utility.ObjectMap;
import sancho.view.viewer.GView;

public abstract class GTableContentProviderOM extends GTableContentProvider {

  protected boolean updateOnUpdate;

  public GTableContentProviderOM(GView gView) {
    super(gView);
  }

  public Object[] getElements(Object inputElement) {
    if (inputElement instanceof ObjectMap) {
      synchronized (inputElement) {
        ObjectMap objectMap = (ObjectMap) inputElement;
        objectMap.clearAllLists();
        return objectMap.getKeyArray();
      }
    } else
      return EMPTY_ARRAY;
  }

  public void setActive(boolean b) {
    if (b && needsRefresh) {
      Object o = gView.getViewer().getInput();
      if (o != null && o instanceof ObjectMap)
        updateHeaderLabel(((ObjectMap) o).size());
    }
    super.setActive(b);
  }

  public void setVisible(boolean b) {
    if (b && needsRefresh) {
      Object o = gView.getViewer().getInput();
      if (o != null && o instanceof ObjectMap)
        updateHeaderLabel(((ObjectMap) o).size());
    }
    super.setVisible(b);
  }

  public void updateViewer(final ObjectMap objectMap, final int type, final boolean fullRefresh) {
    if (gView == null || gView.isDisposed())
      return;

    if (!gView.isActive() || !gView.isVisible()) {
      needsRefresh = true;
      return;
    }

    gView.getTable().getDisplay().asyncExec(new Runnable() { // TODO; sync
      public void run() {
        if ((gView == null) || gView.isDisposed())
          return;

        if (fullRefresh) {
          synchronized (objectMap) {
            tableViewer.refresh();
            objectMap.clearAllLists();
          }
          updateHeaderLabel(objectMap.size());
          return;
        }

        switch (type) {
          case ObjectMap.REMOVED :

            if (objectMap.removed()) {
              synchronized (objectMap) {
                tableViewer.remove(objectMap.getRemovedArray());
                objectMap.clearRemoved();
              }
              updateHeaderLabel(objectMap.size());
            }
            break;

          case ObjectMap.ADDED :

            if (objectMap.added()) {
              synchronized (objectMap) {
                tableViewer.add(objectMap.getAddedArray());
                objectMap.clearAdded();
              }
              updateHeaderLabel(objectMap.size());
            }
            break;

          case ObjectMap.UPDATED :
            if (objectMap.updated()) {
              synchronized (objectMap) {
                tableViewer.update(objectMap.getUpdatedArray(), null);
                objectMap.clearUpdated();
              }
              if (updateOnUpdate)
                updateHeaderLabel(objectMap.size());
            }
            break;
        }
      }
    });
  }

  protected abstract void updateHeaderLabel(int size);
}