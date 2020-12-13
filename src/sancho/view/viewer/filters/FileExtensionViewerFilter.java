/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.filters;

import org.eclipse.jface.viewers.Viewer;

import sancho.model.mldonkey.File;
import sancho.view.viewer.GView;

public class FileExtensionViewerFilter extends AbstractViewerFilter {

  public FileExtensionViewerFilter(GView gView) {
    super(gView);
  }

  public boolean select(Viewer viewer, Object parentElement, Object element) {
    if (element instanceof File)
      return isFiltered(((File) element).getFileType());
    return true;
  }
}