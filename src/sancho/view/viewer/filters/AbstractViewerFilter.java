/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import sancho.model.mldonkey.enums.AbstractEnum;
import sancho.utility.SwissArmy;
import sancho.view.viewer.GView;

public abstract class AbstractViewerFilter extends ViewerFilter {
  protected GView gView;
  protected int filtered;
  protected int counter;

  public AbstractViewerFilter(GView gView) {
    this.gView = gView;
  }

  public void setFiltered(int filtered) {
    this.filtered = filtered;
    this.counter = SwissArmy.countBits(filtered);
  }

  public void add(AbstractEnum enumObject) {
    if (!isFiltered(enumObject)) {
      this.filtered |= enumObject.getValue();
      counter++;
    }
  }

  public void remove(AbstractEnum enumObject) {
    if (isFiltered(enumObject)) {
      this.filtered -= enumObject.getValue();
      counter--;
    }
  }

  public int getFiltered() {
    return filtered;
  }

  public int count() {
    return counter;
  }

  public boolean isFiltered(AbstractEnum enumObject) {
    return (filtered & enumObject.getValue()) != 0;
  }

  public abstract boolean select(Viewer viewer, Object parentElement, Object element);

  public String toString() {
    return GView.filterToInt(this) + "," + filtered + ",";
  }

}