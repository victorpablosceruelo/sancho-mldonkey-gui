/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.filters;

import org.eclipse.jface.viewers.Viewer;

import sancho.model.mldonkey.Client;
import sancho.model.mldonkey.File;
import sancho.model.mldonkey.Result;
import sancho.model.mldonkey.Room;
import sancho.model.mldonkey.Server;
import sancho.view.viewer.GView;

public class StateViewerFilter extends AbstractViewerFilter {

  public StateViewerFilter(GView gView) {
    super(gView);
  }

  public boolean select(Viewer viewer, Object parentElement, Object element) {
    if (element instanceof Server)
      return isFiltered(((Server) element).getStateEnum());
    else if (element instanceof File)
      return isFiltered(((File) element).getFileStateEnum());
    else if (element instanceof Client)
      return isFiltered(((Client) element).getStateEnum());
    else if (element instanceof Result)
      return isFiltered(((Result) element).getRating());
    else if (element instanceof Room)
      return isFiltered(((Room) element).getRoomState());
    else
      return true;
  }

}
