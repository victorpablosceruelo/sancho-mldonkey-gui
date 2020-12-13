/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.rooms;

import java.util.Observable;

import org.eclipse.jface.viewers.Viewer;

import sancho.model.mldonkey.RoomCollection;
import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableContentProvider;

public class RoomsTableContentProvider extends GTableContentProvider {

  public RoomsTableContentProvider(RoomsTableView rTableView) {
    super(rTableView);
  }

  public Object[] getElements(Object inputElement) {
    if (inputElement instanceof RoomCollection) {
      synchronized (inputElement) {
        RoomCollection roomCollection = (RoomCollection) inputElement;
        roomCollection.clearAllLists();
        return roomCollection.getValues();
      }
    }
    return EMPTY_ARRAY;
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.inputChanged(viewer, oldInput, newInput);

    if (oldInput != null)
      ((Observable) oldInput).deleteObserver(this);

    if (newInput != null) {
      ((Observable) newInput).addObserver(this);
      updateHeaderLabel();
    }
  }

  public void update(final Observable o, final Object obj) {
    if ((gView == null) || gView.isDisposed())
      return;

    if (o instanceof RoomCollection) {
      final RoomCollection roomCollection = (RoomCollection) o;

      tableViewer.getTable().getDisplay().asyncExec(new Runnable() {
        public void run() {
          if ((gView == null) || gView.isDisposed())
            return;
          if (roomCollection.removed()) {
            synchronized (roomCollection) {
              tableViewer.remove(roomCollection.getRemovedArray());
              roomCollection.clearRemoved();
            }
            updateHeaderLabel();
          }
          if (roomCollection.added()) {
            synchronized (roomCollection) {
              tableViewer.add(roomCollection.getAddedArray());
              roomCollection.clearAdded();
            }
            updateHeaderLabel();
          }
          if (roomCollection.updated())
            synchronized (roomCollection) {
              tableViewer.update(roomCollection.getUpdatedArray(), null);
              roomCollection.clearUpdated();
            }

        }
      });
    }
  }

  public void updateHeaderLabel() {
    gView.getViewFrame().updateCLabelText(
        SResources.getString("t.r.availableRooms") + ": " + tableViewer.getTable().getItemCount());
  }
}
