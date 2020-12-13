/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;

import sancho.model.mldonkey.Network;
import sancho.view.viewer.GView;
import sancho.view.viewer.filters.AbstractViewerFilter;
import sancho.view.viewer.filters.NetworkViewerFilter;

public class NetworkFilterAction extends AbstractFilterAction {

  public NetworkFilterAction(GView gViewer, Network network) {
    super(network.getName(), Action.AS_CHECK_BOX, gViewer, network.getEnumNetwork());
    filterClass = NetworkViewerFilter.class;
  }

  public AbstractViewerFilter createNewFilter() {
    return new NetworkViewerFilter(gView);
  }

}
