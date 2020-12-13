/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends.clientDirectories;

import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableLabelProvider;

public class ClientDirectoriesTableLabelProvider extends GTableLabelProvider {

  public ClientDirectoriesTableLabelProvider(ClientDirectoriesTableView rTableViewer) {
    super(rTableViewer);
  }

  public String getColumnText(Object arg0, int columnIndex) {
    switch (cViewer.getColumnIDs()[columnIndex]) {
      case ClientDirectoriesTableView.DIRECTORY :
        String s = (String) arg0;
        return s.equals(SResources.S_ES) ? "[\\]" : s;
      default :
        return SResources.S_ES;
    }
  }
}
