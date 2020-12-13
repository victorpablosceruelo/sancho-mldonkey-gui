/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.downloadComplete;

import java.util.List;

import sancho.view.viewer.table.GTableContentProvider;

public class DownloadCompleteTableContentProvider extends GTableContentProvider {

  public DownloadCompleteTableContentProvider(DownloadCompleteTableView fTableView) {
    super(fTableView);
  }

  public Object[] getElements(Object inputElement) {
    return ((List) inputElement).toArray();
  }
 
}
