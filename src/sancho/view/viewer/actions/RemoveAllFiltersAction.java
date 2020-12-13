/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.viewers.ViewerFilter;

import sancho.view.search.result.ResultTableView;
import sancho.view.server.ServerTableView;
import sancho.view.shares.UploadTableView;
import sancho.view.utility.SResources;
import sancho.view.viewer.GView;
import sancho.view.viewer.filters.AbstractViewerFilter;

public class RemoveAllFiltersAction extends AbstractFilterAction {

  public RemoveAllFiltersAction(GView gView) {
    super(SResources.getString("mi.noFilters"), 0, gView, null);
  }

  public boolean isChecked() {
    return ((gView != null) && (gView.getFilters().length == 0));
  }

  public AbstractViewerFilter createNewFilter() {
    return null;
  }

  public void run() {
    if (gView instanceof ResultTableView || gView instanceof ServerTableView
        || gView instanceof UploadTableView) {
      ViewerFilter[] filters = gView.getFilters();

      for (int i = 0; i < filters.length; i++) {
        if (filters[i] instanceof AbstractViewerFilter)
          gView.removeFilter(filters[i]);
        //	if (!(filters[i] instanceof WordViewerFilter) && !(filters[i]
        // instanceof RefineFilter))
        //	gView.removeFilter(filters[i]);
      }
    } else
      gView.resetFilters();
  }
}
