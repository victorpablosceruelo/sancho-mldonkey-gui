/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import sancho.model.mldonkey.Client;
import sancho.model.mldonkey.File;
import sancho.model.mldonkey.Network;
import sancho.view.MainWindow;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public abstract class AbstractDetailDialog extends org.eclipse.jface.dialogs.Dialog implements Observer {
  protected ArrayList chunkCanvases = new ArrayList();
  protected static final int leftColumn = 100;
  protected static final int rightColumn = leftColumn * 3;

  protected AbstractDetailDialog(Shell parentShell) {
    super(parentShell);
  }

  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setImage(SResources.getImage("ProgramIcon"));
  }

  /**
   * @param composite
   * @param resString
   * @param longlabel
   * @return
   */
  protected CLabel createLine(Composite composite, String resString, boolean longLabel) {
    Label label = new Label(composite, SWT.NONE);
    label.setText(SResources.getString(resString));

    GridData gridData = new GridData();
    gridData.widthHint = leftColumn;
    label.setLayoutData(gridData);

    final CLabel cLabel = new CLabel(composite, SWT.NONE);

    Menu popupMenu = new Menu(cLabel);

    popupMenu.addMenuListener(new MenuListener() {
      public void menuHidden(MenuEvent e) {
        cLabel.setBackground(cLabel.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
      }

      public void menuShown(MenuEvent e) {
        cLabel.setBackground(cLabel.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
      }
    });

    MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
    menuItem.setText(SResources.getString("mi.copy"));
    menuItem.setImage(SResources.getImage("copy"));
    menuItem.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        MainWindow.copyToClipboard(cLabel.getText());
      }
    });

    cLabel.setMenu(popupMenu);

    gridData = new GridData();

    if (longLabel) {
      gridData.widthHint = rightColumn;
      gridData.horizontalSpan = 3;
    } else
      gridData.widthHint = leftColumn;

    cLabel.setLayoutData(gridData);

    return cLabel;
  }

  /**
   * @param parent
   * @param string
   * @param client
   * @param file
   * @param network
   * @return ChunkCanvas
   */
  protected ChunkCanvas createChunkGroup(Composite parent, String string, Client client, File file,
      Network network) {
    Group chunkGroup = new Group(parent, SWT.SHADOW_ETCHED_OUT);

    String totalChunks = SResources.S_ES;

    if (network == null)
      totalChunks = (client == null) ? (" (" + file.getAvail().length() + ")") : SResources.S_ES;
    else {
      if (file.hasAvails())
        totalChunks = " (" + ((String) file.getAvails(network)).length() + ")";
    }

    chunkGroup.setText(string + totalChunks);
    chunkGroup.setLayout(WidgetFactory.createGridLayout(1, 5, 2, 0, 0, false));
    chunkGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    ChunkCanvas chunkCanvas = new ChunkCanvas(chunkGroup, SWT.NO_BACKGROUND, client, file, network, false);

    GridData canvasGD = new GridData(GridData.FILL_HORIZONTAL);
    canvasGD.heightHint = 18;
    chunkCanvas.setLayoutData(canvasGD);

    chunkCanvases.add(chunkCanvas);

    return chunkCanvas;
  }

  protected void updateLabel(CLabel cLabel, String string) {
    if (!cLabel.isDisposed()) {
      cLabel.setText(string);
      cLabel.setToolTipText((string.length() > 10) ? string : SResources.S_ES);
    }
  }

  public boolean close() {
    for (Iterator i = chunkCanvases.iterator(); i.hasNext();)
      ((ChunkCanvas) i.next()).dispose();

    return super.close();
  }

  public abstract void updateLabels();

  public void update(Observable o, Object arg) {
    if (getShell() != null && !getShell().isDisposed())
      getShell().getDisplay().asyncExec(new Runnable() {
        public void run() {
          if (getShell() != null && !getShell().isDisposed())
            updateLabels();
        }
      });

  }
}