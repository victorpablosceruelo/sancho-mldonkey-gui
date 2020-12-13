/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;

import sancho.model.mldonkey.enums.AbstractEnum;
import sancho.model.mldonkey.enums.EnumHostState;
import sancho.view.viewer.GView;
import sancho.view.viewer.ICustomViewer;
import sancho.view.viewer.filters.AbstractViewerFilter;
import sancho.view.viewer.filters.StateViewerFilter;

public abstract class AbstractFilterAction extends Action {
  protected GView gView;
  protected AbstractEnum enumObject;
  protected Class filterClass;

  public AbstractFilterAction(String string, int style, GView gView, AbstractEnum enumObject) {
    super(string, style);
    this.gView = gView;
    this.enumObject = enumObject;
  }

  public boolean isChecked() {
    AbstractViewerFilter filter = gView.getFilter(filterClass);
    if (filter == null)
      return false;
    return filter.isFiltered(enumObject);
  }

  protected boolean doRemove() {
    return isChecked();
  }

  public void run() {
    AbstractViewerFilter filter = gView.getFilter(filterClass);

    if (doRemove()) {
      if (filter == null)
        return;

      if (filter.isFiltered(enumObject))
        removeFilter(filter, enumObject);
    } else {

      if (filter == null)
        addFilter(createNewFilter(), enumObject);
      else
        addFilter(filter, enumObject);
    }
  }

  public abstract AbstractViewerFilter createNewFilter();

  protected void removeFilter(AbstractViewerFilter filter, AbstractEnum enumObject) {
    filter.remove(enumObject);

    ((ICustomViewer) gView.getViewer()).closeAllTTE();
    if (filter.count() == 0)
      gView.removeFilter(filter);
    else
      gView.refresh();
  }

  protected void addFilter(AbstractViewerFilter filter, AbstractEnum enumObject) {

    filter.add(enumObject);
   // ((ICustomViewer) gView.getViewer()).closeAllTTE();

    if (filter.count() == 1)
      gView.addFilter(filter);
    else
      gView.refresh();
  }

  public static boolean isStateFilteredExcept(GView gView, EnumHostState enumState) {
    AbstractViewerFilter filter = gView.getFilter(StateViewerFilter.class);
    if (filter == null)
      return false;

    return !filter.isFiltered(enumState);
  }

}
