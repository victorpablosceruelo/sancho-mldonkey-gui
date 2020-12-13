/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import sancho.model.mldonkey.Result;

public class WordViewerFilter extends ViewerFilter {
  private int wordFilterType = 0;
  public static final int PROFANITY_FILTER_TYPE = 1;
  public static final int PORNOGRAPHY_FILTER_TYPE = 2;

  public WordViewerFilter(int type) {
    wordFilterType = type;
  }

  public boolean select(Viewer viewer, Object parentElement, Object element) {
    if (element instanceof Result) {
      Result result = (Result) element;
      if ((wordFilterType == PROFANITY_FILTER_TYPE && result.containsProfanity())
          || (wordFilterType == PORNOGRAPHY_FILTER_TYPE && result.containsPornography()))
        return false;
    }
    return true;
  }
}