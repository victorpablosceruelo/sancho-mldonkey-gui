/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;

import sancho.model.mldonkey.enums.AbstractEnum;
import sancho.view.viewer.GView;
import sancho.view.viewer.filters.AbstractViewerFilter;
import sancho.view.viewer.filters.StateViewerFilter;

public class StateFilterAction extends AbstractFilterAction {

  public StateFilterAction(String name, GView gView, AbstractEnum enumObject) {
    super(name, Action.AS_CHECK_BOX, gView, enumObject);
    this.filterClass = StateViewerFilter.class;
  }

  public AbstractViewerFilter createNewFilter() {
    return new StateViewerFilter(gView);
  }

}