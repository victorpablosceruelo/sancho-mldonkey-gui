/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends.clientFiles;

import sancho.view.search.result.ResultTableMenuListener;

public class ClientFilesTableMenuListener extends ResultTableMenuListener {

  public ClientFilesTableMenuListener(ClientFilesTableView rTableViewer) {
    super(rTableViewer, null);
  }

  public void postDownloadStats(int counter, String anErrorString) {
    // This has no mainWindow or guiTab, 
    // gView.getViewFrame().getGuiTab().getMainWindow().getStatusline().setText(
    //   Resources.getString("s.sl.startedDownload") + counter);
  }

}
