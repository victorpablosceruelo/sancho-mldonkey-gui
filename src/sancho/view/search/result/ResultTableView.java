/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.search.result;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import sancho.model.mldonkey.enums.AbstractEnum;
import sancho.model.mldonkey.enums.EnumRating;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.SResources;
import sancho.view.viewer.GView;
import sancho.view.viewer.filters.WordViewerFilter;
import sancho.view.viewer.table.GTableMenuListener;
import sancho.view.viewer.table.GTableView;

public class ResultTableView extends GTableView implements IDoubleClickListener {
  public static final int NETWORK = 0;
  public static final int NAME = 1;
  public static final int SIZE = 2;
  public static final int FORMAT = 3;
  public static final int MEDIA = 4;
  public static final int CODEC = 5;
  public static final int BITRATE = 6;
  public static final int LENGTH = 7;
  public static final int AVAILABILITY = 8;
  public static final int COMPLETE_SOURCES = 9;

  public ResultTableView(ResultViewFrame viewFrame, CTabItem cTabItem, AbstractTab searchTab) {
    super(viewFrame);
    cTabItem.setData(GView.S_GVIEW, this);

    preferenceString = "result";
    columnLabels = new String[]{"result.network", "result.name", "result.size", "result.format",
        "result.media", "result.codec", "result.bitrate", "result.length", "result.availability",
        "result.completeSources"};

    columnDefaultWidths = new int[]{60, 300, 65, 50, 50, 60, 60, 60, 90, 60};

    columnAlignment = new int[]{SWT.LEFT, SWT.LEFT, SWT.RIGHT, SWT.LEFT, SWT.LEFT, SWT.RIGHT, SWT.RIGHT,
        SWT.RIGHT, SWT.LEFT, SWT.RIGHT};

    validStates = new AbstractEnum[]{EnumRating.EXCELLENT, EnumRating.VERY_HIGH, EnumRating.HIGH,
        EnumRating.NORMAL, EnumRating.LOW};

    tableContentProvider = new ResultTableContentProvider(this);
    tableLabelProvider = new ResultTableLabelProvider(this);
    gSorter = new ResultTableSorter(this);
    tableMenuListener = new ResultTableMenuListener(this, cTabItem);

    this.createContents(viewFrame.getCTabFolder());
  }

  public void setInput() {
  }

  public void setInput(Object object) {
    sViewer.setInput(object);
  }

  public GTableMenuListener getMenuListener() {
    return this.tableMenuListener;
  }

  protected void createContents(Composite parent) {
    super.createContents(parent);
    sViewer.addSelectionChangedListener((ResultTableMenuListener) tableMenuListener);

    // add optional filters
    if (PreferenceLoader.loadBoolean("searchFilterPornography"))
      sViewer.addFilter(new WordViewerFilter(WordViewerFilter.PORNOGRAPHY_FILTER_TYPE));
    else if (PreferenceLoader.loadBoolean("searchFilterProfanity"))
      sViewer.addFilter(new WordViewerFilter(WordViewerFilter.PROFANITY_FILTER_TYPE));

    addMenuListener();

    getTableViewer().addDoubleClickListener(this);
    getTable().setToolTipText(SResources.S_ES);

    ((ResultViewFrame) getViewFrame()).onCTabFolderSelection();
  }

  public void doubleClick(DoubleClickEvent e) {
    ((ResultTableMenuListener) getMenuListener()).downloadSelected();
  }

}