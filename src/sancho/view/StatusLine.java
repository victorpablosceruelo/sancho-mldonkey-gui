/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import sancho.core.Sancho;
import sancho.view.statusline.CoreConsoleItem;
import sancho.view.statusline.IStatusItem;
import sancho.view.statusline.LinkEntry;
import sancho.view.statusline.LinkEntryItem;
import sancho.view.statusline.NetworkItem;
import sancho.view.statusline.RateItem;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public class StatusLine {
  protected CLabel cLabel;
  protected Composite linkEntryComposite;
  protected MainWindow mainWindow;
  protected List statusItemList;
  protected Composite statusLineComposite;

  public StatusLine(MainWindow mainWindow, boolean create) {
    this.mainWindow = mainWindow;
    statusItemList = new ArrayList();
    if (create)
      createContents();
  }

  private void addSeparator(Composite composite) {
    Label separator = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
    separator.setLayoutData(WidgetFactory.createGridData(GridData.FILL_VERTICAL, SWT.DEFAULT, 0));
  }

  public void clear() {
    if (!cLabel.isDisposed()) {
      cLabel.setText(SResources.S_ES);
      cLabel.setToolTipText(SResources.S_ES);
      cLabel.setImage(null);
    }
  }

  protected void createContents() {
    boolean spawnedCore = (Sancho.getCoreConsole() != null);

    Composite mainComposite = new Composite(mainWindow.getMainComposite(), SWT.BORDER);
    mainComposite.setLayout(WidgetFactory.createGridLayout(1, 0, 0, 0, 0, false));
    mainComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    // hidden linkEntry composite
    createLinkEntry(mainComposite);

    new LinkEntry(this, linkEntryComposite);

    statusLineComposite = new Composite(mainComposite, SWT.NONE);
    statusLineComposite.setLayout(WidgetFactory.createGridLayout(spawnedCore ? 9 : 7, 0, 0, 0, 0, false));
    statusLineComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    statusItemList.add(new NetworkItem(this));

    addSeparator(statusLineComposite);

    if (spawnedCore) {
      new CoreConsoleItem(this);
      addSeparator(statusLineComposite);
    }

    // status
    Composite middle = new Composite(statusLineComposite, SWT.NONE);
    middle.setLayout(new FillLayout());
    middle.setLayoutData(new GridData(GridData.FILL_BOTH));
    cLabel = new CLabel(middle, SWT.NONE);
    cLabel.setText(SResources.S_ES);

    addSeparator(statusLineComposite);

    new LinkEntryItem(this);
    addSeparator(statusLineComposite);

    statusItemList.add(new RateItem(this));
  }

  private void createLinkEntry(Composite parent) {
    linkEntryComposite = new Composite(parent, SWT.NONE);
    linkEntryComposite.setLayout(WidgetFactory.createGridLayout(2, 0, 0, 0, 0, false));
    linkEntryComposite.setLayoutData(WidgetFactory.createGridData(GridData.FILL_HORIZONTAL, SWT.DEFAULT, 0));
  }

  public Composite getLinkEntryComposite() {
    return linkEntryComposite;
  }

  public MainWindow getMainWindow() {
    return mainWindow;
  }

  public Composite getStatusline() {
    return statusLineComposite;
  }

  public void setConnected(boolean connected) {
    for (int i = 0; i < statusItemList.size(); i++)
      ((IStatusItem) statusItemList.get(i)).setConnected(connected);
  }

  public void setImage(Image image) {
    if (!cLabel.isDisposed())
      cLabel.setImage(image);
  }

  public void setText(String string) {
    if (!cLabel.isDisposed())
      cLabel.setText(string);
  }

  public void setToolTip(String string) {
    if (!cLabel.isDisposed())
      cLabel.setToolTipText(string);
  }
}