/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;

import sancho.model.mldonkey.enums.AbstractEnum;
import sancho.view.viewer.GView;
import sancho.view.viewer.filters.AbstractViewerFilter;
import sancho.view.viewer.filters.ExclusionStateViewerFilter;

public class ExclusionStateFilterAction extends AbstractFilterAction {

  public ExclusionStateFilterAction(String name, GView gView, AbstractEnum enumObject) {
    super(name, Action.AS_CHECK_BOX, gView, enumObject);
    this.filterClass = ExclusionStateViewerFilter.class;
  }

  public boolean isChecked() {
    return !super.isChecked();
  }

  protected boolean doRemove() {
    return super.isChecked();
  }

  public AbstractViewerFilter createNewFilter() {
    return new ExclusionStateViewerFilter(gView);
  }

}
