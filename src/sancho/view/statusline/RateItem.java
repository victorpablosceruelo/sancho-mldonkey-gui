/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statusline;

import java.util.Observable;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;

import sancho.core.Sancho;
import sancho.model.mldonkey.ClientStats;
import sancho.view.StatusLine;
import sancho.view.statusline.actions.DNDBoxAction;
import sancho.view.statusline.actions.RateBandwidthDialogAction;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public class RateItem implements IStatusItem {
  private Composite statusLineComposite;
  private CLabel downCLabel;
  private CLabel upCLabel;
  private boolean updateImages;
  private boolean connected;
  private MenuManager popupMenu;
  private StatusLine statusLine;
  private int oldLength;

  public RateItem(StatusLine statusline) {
    this.statusLine = statusline;
    this.statusLineComposite = statusline.getStatusline();
    this.createContent();
    updateImages = true;
    setConnected(true);
  }

  public void setConnected(boolean connected) {
    this.connected = connected;
    if (Sancho.hasCollectionFactory()) {
      Sancho.getCore().getClientStats().addObserver(this);
    } else {
      updateDisconnected();
    }
  }

  private void createContent() {
    statusLineComposite = new Composite(statusLineComposite, SWT.NONE);
    statusLineComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));

    statusLineComposite.setLayout(WidgetFactory.createRowLayout(false, true, false, SWT.HORIZONTAL, 0, 0, 0,
        0, 0));

    popupMenu = new MenuManager();
    popupMenu.setRemoveAllWhenShown(true);
    popupMenu.addMenuListener(new RateMenuListener());

    downCLabel = new CLabel(statusLineComposite, SWT.RIGHT);
    downCLabel.setLayoutData(new RowData());
    downCLabel.setMenu(popupMenu.createContextMenu(downCLabel));

    upCLabel = new CLabel(statusLineComposite, SWT.NONE);
    upCLabel.setLayoutData(new RowData());
    upCLabel.setMenu(popupMenu.createContextMenu(upCLabel));
  }

  public void update(final Observable o, final Object arg) {
    if (!(o instanceof ClientStats) || o == null || upCLabel == null || upCLabel.isDisposed())
      return;

    statusLineComposite.getDisplay().asyncExec(new Runnable() {
      public void run() {
        if (connected)
          updateClientStats((ClientStats) o);
      }
    });
  }

  public void updateClientStats(ClientStats stats) {
    if (upCLabel == null || upCLabel.isDisposed())
      return;

    if (updateImages) {
      downCLabel.setImage(SResources.getImage("rateDownArrow"));
      upCLabel.setImage(SResources.getImage("rateUpArrow"));
    }

    downCLabel.setText(stats.getTcpDownRateString());
    upCLabel.setText(stats.getTcpUpRateString());
    downCLabel.setToolTipText(stats.getDownloadToolTip());
    upCLabel.setToolTipText(stats.getUploadToolTip());

    int newLength = downCLabel.getText().length() + upCLabel.getText().length();

    // only run Layout() if needed.. it seems to be an expensive call
    if (newLength != oldLength || updateImages) {
      oldLength = newLength;
      statusLineComposite.getParent().layout();
      updateImages = false;
    }
  }

  public void updateDisconnected() {
    if (upCLabel == null || upCLabel.isDisposed())
      return;

    downCLabel.setImage(SResources.getImage("RedCrossSmall"));
    downCLabel.setText(SResources.getString("l.disconnected"));
    downCLabel.setToolTipText(SResources.S_ES);
    upCLabel.setImage(null);
    upCLabel.setText(SResources.S_ES);
    upCLabel.setToolTipText(SResources.S_ES);
    statusLineComposite.getParent().layout();
    updateImages = true;
  }

  class RateMenuListener implements IMenuListener {
    public void menuAboutToShow(IMenuManager manager) {
      if (!Sancho.monitorMode) {
        manager.add(new DNDBoxAction(statusLine.getMainWindow()));
        manager.add(new RateBandwidthDialogAction(downCLabel.getShell()));
      }
    }
  }

}