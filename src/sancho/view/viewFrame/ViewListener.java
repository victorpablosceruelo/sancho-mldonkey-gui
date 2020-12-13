/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewFrame;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

import sancho.core.Sancho;
import sancho.model.mldonkey.Network;
import sancho.view.utility.SResources;
import sancho.view.viewer.GView;
import sancho.view.viewer.actions.ExtensionFilterAction;
import sancho.view.viewer.actions.NetworkFilterAction;
import sancho.view.viewer.actions.SetDynamicColumnAction;
import sancho.view.viewer.actions.SetMinDynamicColumnWidthAction;
import sancho.view.viewer.actions.SortByColumnAction;
import sancho.view.viewer.actions.StateFilterAction;

public abstract class ViewListener implements IMenuListener {
  protected ViewFrame viewFrame;
  protected Control control;
  protected GView gView;

  public ViewListener(ViewFrame viewFrame) {
    this.viewFrame = viewFrame;
    this.control = viewFrame.getControl();
    this.gView = viewFrame.getGView();
  }

  protected void createNetworkWithServersFilterSubMenu(MenuManager menu) {

    if (!Sancho.hasCollectionFactory())
      return;

    Network[] networks = viewFrame.getCore().getNetworkCollection().getNetworks();

    for (int i = 0; i < networks.length; i++) {
      Network network = networks[i];
      if (network.isEnabled() && (network.hasServers() || network.hasSupernodes()))
        menu.add(new NetworkFilterAction(viewFrame.getGView(), network));
    }
  }

  protected void createEnabledNetworkFilterSubMenu(MenuManager menu) {
    if (!Sancho.hasCollectionFactory())
      return;

    Network[] networks = viewFrame.getCore().getNetworkCollection().getNetworks();

    for (int i = 0; i < networks.length; i++) {
      Network network = networks[i];
      if (network.isEnabled() && !network.isVirtual())
        menu.add(new NetworkFilterAction(viewFrame.getGView(), network));
    }
  }

  protected void createStateFilterMenuItems(MenuManager menu) {
    if (gView.getValidStates() != null)
      for (int i = 0; i < gView.getValidStates().length; i++)
        menu
            .add(new StateFilterAction(gView.getValidStates()[i].getName(), gView, gView.getValidStates()[i]));
  }

  protected void createExtensionFilterMenuItems(MenuManager menu) {
    if (gView.getValidExtensions() != null)
      for (int i = 0; i < gView.getValidExtensions().length; i++)
        menu.add(new ExtensionFilterAction(gView.getValidExtensions()[i].getName(), gView, gView
            .getValidExtensions()[i]));
  }

  protected void createSortByColumnSubMenu(IMenuManager menuManager) {
    if (viewFrame.getGView() == null)
      return;

    MenuManager sortSubMenu = new MenuManager(SResources.getString("mi.sort"));

    for (int i = 0; i < viewFrame.getGView().getTable().getColumnCount(); i++)
      sortSubMenu.add(new SortByColumnAction(viewFrame.getGView(), i));

    menuManager.add(sortSubMenu);
  }

  protected void createDynamicColumnSubMenu(IMenuManager menuManager) {
    if (viewFrame.getGView() == null || SWT.getPlatform().equals("gtk"))
      return;

    MenuManager subMenu = new MenuManager(SResources.getString("mi.dynamicColumn"));

    for (int i = 0; i < viewFrame.getGView().getTable().getColumnCount(); i++)
      subMenu.add(new SetDynamicColumnAction(viewFrame.getGView(), i));

    subMenu.add(new Separator());
    subMenu.add(new SetMinDynamicColumnWidthAction(viewFrame.getGView()));

    menuManager.add(subMenu);
  }

}