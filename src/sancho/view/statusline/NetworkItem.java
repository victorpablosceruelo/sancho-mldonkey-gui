/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statusline;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Observable;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import sancho.core.Sancho;
import sancho.model.mldonkey.Network;
import sancho.view.StatusLine;
import sancho.view.statusline.actions.NetworkConnectMoreAction;
import sancho.view.statusline.actions.NetworkDisableAction;
import sancho.view.statusline.actions.NetworkEnableAction;
import sancho.view.utility.WidgetFactory;

public class NetworkItem implements IStatusItem {

  private Composite composite = null;
  private Composite parentComposite;
  private ToolBar toolBar;

  public NetworkItem(StatusLine statusline) {
    this.parentComposite = new Composite(statusline.getStatusline(), SWT.NONE);
    this.parentComposite.setLayout(WidgetFactory.createGridLayout(1, 0, 0, 0, 0, false));
    this.parentComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
    setConnected(Sancho.getCoreFactory().isConnected());
  }

  private void createContent() {
    if (composite != null)
      composite.dispose();

    composite = new Composite(parentComposite, SWT.NONE);
    composite.setLayout(WidgetFactory.createGridLayout(1, 0, 0, 0, 0, false));
    composite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
    toolBar = new ToolBar(composite, SWT.FLAT);

    if (!Sancho.hasCollectionFactory())
      return;

    Network[] networks = Sancho.getCore().getNetworkCollection().getNetworks();
    Arrays.sort(networks, new NetworkComparator());

    ToolItem toolItem;

    for (int i = 0; i < networks.length; i++) {
      Network network = networks[i];

      if (!network.isVirtual()) {
        toolItem = new ToolItem(toolBar, SWT.NONE);
        toolItem.setData(network);

        final MenuManager popupMenu = new MenuManager();
        popupMenu.setRemoveAllWhenShown(true);
        popupMenu.addMenuListener(new NetworkMenuListener(network));

        toolItem.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent event) {
            Rectangle bounds = ((ToolItem) event.widget).getBounds();
            Menu menu = popupMenu.createContextMenu(toolBar);
            Point point = new Point(bounds.x, bounds.y + bounds.height);
            point = toolBar.toDisplay(point);
            menu.setLocation(point.x, point.y);
            menu.setVisible(true);
          }
        });
        toolItem.addDisposeListener(new DisposeListener() {
          public void widgetDisposed(DisposeEvent e) {
            if (Sancho.hasCollectionFactory())
              Sancho.getCore().getNetworkCollection().deleteObserver(NetworkItem.this);
          }
        });

        toolItem.setToolTipText(network.getToolTip());
        toolItem.setImage(network.getImage());
      }
    }
    parentComposite.layout();
    parentComposite.getParent().layout();
  }

  private ToolItem getToolItemByNetwork(Network network) {
    if (toolBar == null || toolBar.isDisposed())
      return null;

    ToolItem[] toolItems = toolBar.getItems();
    ToolItem toolItem;
    for (int i = 0; i < toolItems.length; i++) {
      toolItem = toolItems[i];
      if (toolItem != null && !toolItem.isDisposed() && toolItem.getData() == network)
        return toolItem;
    }
    return null;
  }

  public void setConnected(boolean connected) {
    if (connected && Sancho.hasCollectionFactory()) {
      createContent();
      Sancho.getCore().getNetworkCollection().addObserver(this);
    } else {
      if (composite != null)
        composite.dispose();

      composite = new Composite(parentComposite, SWT.NONE);
      composite.setLayoutData(WidgetFactory.createGridData(SWT.NONE, 1, 1));
      parentComposite.layout();
    }
  }

  public void update(Observable o, Object arg) {
    if (arg == null || !(arg instanceof Network) || toolBar == null || toolBar.isDisposed())
      return;

    final Network network = (Network) arg;
    composite.getDisplay().asyncExec(new Runnable() {
      public void run() {
        if (toolBar == null || toolBar.isDisposed())
          return;

        ToolItem toolItem = getToolItemByNetwork(network);
        if (toolItem != null && !toolItem.isDisposed()) {
          toolItem.setImage(network.getImage());
          toolItem.setToolTipText(network.getToolTip());
        }
      }
    });
  }

  static class NetworkComparator implements Comparator {
    public int compare(Object o1, Object o2) {
      Network network1 = (Network) o1;
      Network network2 = (Network) o2;
      if (network1.getName().equalsIgnoreCase("g2") && network2.getName().equalsIgnoreCase("gnutella"))
        return 1;
      if (network2.getName().equalsIgnoreCase("g2") && network1.getName().equalsIgnoreCase("gnutella"))
        return -1;
      return network1.getName().compareToIgnoreCase(network2.getName());
    }
  }

  static class NetworkMenuListener implements IMenuListener {
    Network network;

    public NetworkMenuListener(Network network) {
      this.network = network;
    }

    public void menuAboutToShow(IMenuManager manager) {
      if (!network.isVirtual()) {
        if (network.isEnabled())
          manager.add(new NetworkDisableAction(network));
        else
          manager.add(new NetworkEnableAction(network));
      }

      if (network.isEnabled() && (network.hasServers() || network.hasSupernodes())) {
        manager.add(new Separator());
        manager.add(new NetworkConnectMoreAction(network));
      }
    }
  }

}