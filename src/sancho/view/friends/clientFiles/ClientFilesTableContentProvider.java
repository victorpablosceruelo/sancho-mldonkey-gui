/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends.clientFiles;

import java.util.Map;

import org.eclipse.jface.viewers.Viewer;

import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableContentProvider;

public class ClientFilesTableContentProvider extends GTableContentProvider {
  public static final String RS_CLIENT_FILES = SResources.getString("l.clientFiles");

  public ClientFilesTableContentProvider(ClientFilesTableView rTableViewer) {
    super(rTableViewer);
  }

  public Object[] getElements(Object inputElement) {
    if (inputElement instanceof Map) {
      Map fileMap = (Map) inputElement;
      updateHeaderLabel(fileMap.size());
      return fileMap.keySet().toArray();
    }
    return EMPTY_ARRAY;
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.inputChanged(viewer, oldInput, newInput);
    updateHeaderLabel();

  }

  public void updateHeaderLabel(int i) {
    gView.getViewFrame().updateCLabelText(RS_CLIENT_FILES + SResources.S_COLON + i);
  }

  public void updateHeaderLabel() {
    gView.getViewFrame().updateCLabelText(RS_CLIENT_FILES);
  }

}
