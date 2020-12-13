/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;

import sancho.view.transfer.clients.ClientTableView;
import sancho.view.transfer.clients.ClientViewFrame;
import sancho.view.transfer.downloads.DownloadTableTreeView;
import sancho.view.transfer.downloads.DownloadViewFrame;
import sancho.view.transfer.pending.PendingViewFrame;
import sancho.view.transfer.uploaders.UploadersViewFrame;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.WidgetFactory;
import sancho.view.viewFrame.ViewFrame;

public class TransferTab extends AbstractTab {
  public TransferTab(MainWindow mainWindow, String prefString) {
    super(mainWindow, prefString);
  }

  protected void createContents(Composite composite) {
    String sashPrefString = "transferSash";
    SashForm sashForm = WidgetFactory.createSashForm(composite, sashPrefString);
    createDownloadsViews(sashForm);
    createUploadsView(sashForm);
    WidgetFactory.loadSashForm(sashForm, sashPrefString);
  }

  private void createDownloadsViews(SashForm sashForm) {
    String sashPrefString = "clientSash";
    SashForm sashForm2 = WidgetFactory.createSashForm(sashForm, sashPrefString);
    createDownloadsView(sashForm2);
    createClientsView(sashForm2);
    WidgetFactory.loadSashForm(sashForm2, sashPrefString);

    if (sashForm2.getMaximizedControl() == null) {
      DownloadTableTreeView downloadTableTreeView = (DownloadTableTreeView) ((ViewFrame) getViewFrameList()
          .get(0)).getGView();
      downloadTableTreeView.updateClientsTable(true);
    }
  }

  private void createUploadsView(SashForm sashForm) {
    String sashPrefString = "uploadsSash";
    SashForm sashForm2 = WidgetFactory.createSashForm(sashForm, sashPrefString);

    addViewFrame(new UploadersViewFrame(sashForm2, "l.uploaders", "up_arrow_blue", this));
    addViewFrame(new PendingViewFrame(sashForm2, "l.pending", "up_arrow_blue", this));
    WidgetFactory.loadSashForm(sashForm2, sashPrefString);
  }

  private void createDownloadsView(SashForm sashForm) {
    addViewFrame(new DownloadViewFrame(sashForm, "l.downloads", "tab.transfers.buttonSmall", this));
  }

  private void createClientsView(SashForm sashForm) {
    DownloadTableTreeView downloadTableTreeView = (DownloadTableTreeView) ((ViewFrame) getViewFrameList()
        .get(0)).getGView();
    ClientViewFrame clientViewFrame = new ClientViewFrame(sashForm, "l.clients", "tab.transfers.buttonSmall",
        this, downloadTableTreeView);
    downloadTableTreeView.setClientTableView((ClientTableView) clientViewFrame.getGView());
    addViewFrame(clientViewFrame);
  }
}
