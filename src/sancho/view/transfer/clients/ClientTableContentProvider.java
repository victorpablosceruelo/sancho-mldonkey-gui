/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.clients;

import java.util.Observable;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.custom.CLabel;

import sancho.model.mldonkey.File;
import sancho.utility.ObjectMap;
import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableContentProviderOM;

public class ClientTableContentProvider extends GTableContentProviderOM {

  private static final String RS_CLIENTS = SResources.getString("l.clients");
  private static final String RS_CONNECTED = SResources.getString("l.connected");

  private long lastRefreshTime;
  private long currentTime;
  private File inputElementFile;

  public ClientTableContentProvider(ClientTableView cTableViewer, CLabel headerCLabel) {
    super(cTableViewer);
    updateOnUpdate = true;
  }

  public Object[] getElements(Object inputElement) {
    if (inputElement instanceof File) {
      File file = (File) inputElement;
      ObjectMap objectWeakMap = file.getClientWeakMap();
      lastRefreshTime = System.currentTimeMillis();

      synchronized (objectWeakMap) {
        objectWeakMap.clearAllLists();
        return objectWeakMap.getKeyArray();
      }
    }
    return EMPTY_ARRAY;
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.inputChanged(viewer, oldInput, newInput);
    inputElementFile = null;

    if ((oldInput != null) && oldInput instanceof File) {
      File file = (File) oldInput;
      file.getClientWeakMap().deleteObserver(this);
    }

    if ((newInput != null) && newInput instanceof File) {
      File file = (File) newInput;
      inputElementFile = file;
      file.getClientWeakMap().addObserver(this);
      updateHeaderLabel(file.getClientWeakMap().size());
    } else if (newInput == null) {
      updateHeaderLabel();
    }
  }

  public void update(Observable o, Object obj) {
    boolean fullRefresh = false;
 
    if ((currentTime = System.currentTimeMillis()) > (lastRefreshTime + 120000)) {
      fullRefresh = true;
      lastRefreshTime = currentTime;
    }

    updateViewer((ObjectMap) o, ((Integer) obj).intValue(), fullRefresh);
  }

  public void updateHeaderLabel(int size) {
    if (inputElementFile == null)
      return;
    gView.getViewFrame().updateCLabelText(
        RS_CLIENTS + SResources.S_COLON + inputElementFile.getConnected() + SResources.S_SLASH2 + size
            + SResources.S_SPACE + RS_CONNECTED);
  }

  public void updateHeaderLabel() {
    if (gView == null || gView.isDisposed())
      return;
    gView.getViewFrame().updateCLabelText(RS_CLIENTS);
  }

}
