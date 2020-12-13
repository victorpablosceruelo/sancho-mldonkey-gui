/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends.clientDirectories;

import org.eclipse.jface.viewers.Viewer;

import sancho.view.viewer.GSorter;

public class ClientDirectoriesTableSorter extends GSorter {
  public ClientDirectoriesTableSorter(ClientDirectoriesTableView rTableViewer) {
    super(rTableViewer);
  }

  public int compare(Viewer viewer, Object obj1, Object obj2) {

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case ClientDirectoriesTableView.DIRECTORY :
        return compareStrings((String) obj1, (String) obj2);
      default :
        return 0;
    }
  }
}
