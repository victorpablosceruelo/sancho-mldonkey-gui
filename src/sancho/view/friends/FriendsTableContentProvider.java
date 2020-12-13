/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import org.eclipse.jface.viewers.Viewer;

import sancho.utility.ObjectMap;
import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableContentProviderOM;

public class FriendsTableContentProvider extends GTableContentProviderOM {
  private static final String RS_FRIENDS = SResources.getString("l.friends");

  private List observedClients = Collections.synchronizedList(new ArrayList());

  public FriendsTableContentProvider(FriendsTableView fTableView) {
    super(fTableView);
  }

  public Object[] getElements(Object inputElement) {
    if (inputElement instanceof ObjectMap) {
      ObjectMap objectMap = (ObjectMap) inputElement;
      objectMap.clearAllLists();
      return objectMap.getKeyArray();
    } else
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
    /*
     if (o instanceof Client)
     gView.getTable().getDisplay().syncExec(new Runnable() { // sync
     public void run() {
     if ((gView == null) || gView.isDisposed())
     return;
     gView.getViewer().update(o, null);
     }
     });
     else */if (o instanceof ObjectMap) {
      updateViewer((ObjectMap) o, ((Integer) obj).intValue(), false);
    }
  }

  public void updateHeaderLabel(int size) {
    gView.getViewFrame().updateCLabelText(RS_FRIENDS + SResources.S_COLON + size);
  }

  public void updateHeaderLabel() {
    gView.getViewFrame().updateCLabelText(
        RS_FRIENDS + SResources.S_COLON + tableViewer.getTable().getItemCount());
  }
}