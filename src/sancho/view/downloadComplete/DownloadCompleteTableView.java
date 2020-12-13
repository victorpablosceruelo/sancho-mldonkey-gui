/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.downloadComplete;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import sancho.utility.VersionInfo;
import sancho.view.utility.SResources;
import sancho.view.viewFrame.ViewFrame;
import sancho.view.viewer.table.GTableView;

public class DownloadCompleteTableView extends GTableView {
  public static final int NAME = 0;
  public static final int SIZE = 1;
  public static final int HASH = 2;
  public static final int DATE = 3;

  List itemList = new ArrayList();

  public DownloadCompleteTableView(ViewFrame viewFrame) {
    super(viewFrame);

    preferenceString = "downloadComplete";
    columnLabels = new String[]{"downloadComplete.name", "downloadComplete.size", "downloadComplete.hash", "downloadComplete.date"};
    columnAlignment = new int[]{SWT.LEFT, SWT.RIGHT, SWT.LEFT, SWT.RIGHT};
    columnDefaultWidths = new int[]{150, 75, 250, 200};
    gSorter = new DownloadCompleteTableSorter(this);
    tableContentProvider = new DownloadCompleteTableContentProvider(this);
    tableLabelProvider = new DownloadCompleteTableLabelProvider(this);
    tableMenuListener = new DownloadCompleteTableMenuListener(this);

    createContents(viewFrame.getChildComposite());
  }

  protected void createContents(Composite parent) {
    parseList();
    super.createContents(parent);
    sViewer.addSelectionChangedListener((DownloadCompleteTableMenuListener) tableMenuListener);
    addMenuListener();
    updateHeader();
  }

  public void setInput() {
    sViewer.setInput(itemList);
  }

  public void updateHeader() {
    getViewFrame().updateCLabelText(SResources.getString("l.downloadCompleteTitle") + ": " + getTable().getItemCount());
  }
  
  protected void parseList() {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(VersionInfo.getDownloadLogFile()));
      String lineText;
      while ((lineText = reader.readLine()) != null) {
        DownloadCompleteItem item = new DownloadCompleteItem();
        if (item.parseLine(lineText))
          itemList.add(item);
      }
      reader.close();
    } catch (FileNotFoundException e) {
    } catch (IOException io) {
    }

  }

}
