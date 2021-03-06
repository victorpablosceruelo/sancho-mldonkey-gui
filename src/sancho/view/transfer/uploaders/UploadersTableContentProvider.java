/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.uploaders;

import java.util.Observable;

import org.eclipse.jface.viewers.Viewer;

import sancho.utility.ObjectMap;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableContentProviderOM;

public class UploadersTableContentProvider extends GTableContentProviderOM {

  public static final String RS_UPLOADERS = SResources.getString("l.uploaders");
  public static final String RS_DISABLED = SResources.getString("l.disabled");

  public UploadersTableContentProvider(UploadersTableView uTableViewer) {
    super(uTableViewer);
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.inputChanged(viewer, oldInput, newInput);

    if (oldInput != null)
      ((Observable) oldInput).deleteObserver(this);

    if (newInput != null) {
      ObjectMap objectMap = (ObjectMap) newInput;
      objectMap.addObserver(this);
      if (!PreferenceLoader.loadBoolean("pollUploaders"))
        updateHeaderLabel();
      else
        updateHeaderLabel(objectMap.size());
    } else {
      updateHeaderLabel();
    }
  }

  public void update(Observable o, Object obj) {
    updateViewer((ObjectMap) o, ((Integer) obj).intValue(), false);
  }

  protected void updateHeaderLabel(int size) {
    if (gView == null || gView.isDisposed())
      return;

    gView.getViewFrame().updateCLabelText(RS_UPLOADERS + SResources.S_COLON + size);
  }

  protected void updateHeaderLabel() {
    if (gView == null || gView.isDisposed())
      return;

    gView.getViewFrame().updateCLabelText(RS_UPLOADERS + SResources.S_COLON + RS_DISABLED);
  }
}
