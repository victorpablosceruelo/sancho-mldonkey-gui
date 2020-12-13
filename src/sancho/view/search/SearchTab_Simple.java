/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import sancho.core.Sancho;
import sancho.model.mldonkey.utility.SearchQuery;
import sancho.view.SearchTab;
import sancho.view.search.result.ResultTab;
import sancho.view.search.result.ResultViewFrame;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public class SearchTab_Simple extends ASearchTab {

  Combo fileTypeCombo;

  public SearchTab_Simple(ResultViewFrame viewFrame, SearchTab tab) {
    super(viewFrame, tab);
  }

  protected void createSearchTip(Composite composite) {
    final Group group = new Group(composite, SWT.SHADOW_OUT);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    group.setLayoutData(gridData);

    final GridLayout gridLayout = WidgetFactory.createGridLayout(1, 5, 5, 0, 0, false);

    group.setLayout(gridLayout);
    group.setText(SResources.getString("s.searchTip"));

    Label label = new Label(group, SWT.WRAP);
    final GridData lGridData = new GridData();
    lGridData.horizontalAlignment = GridData.BEGINNING;
    lGridData.verticalAlignment = GridData.FILL;
    label.setLayoutData(lGridData);
    label.setText(SResources.getString("s.searchTipText"));
    group.addListener(SWT.Resize, new Listener() {
      public void handleEvent(Event e) {
        Rectangle rect = group.getClientArea();
        lGridData.widthHint = rect.width - (2 * gridLayout.marginWidth);
        group.getParent().getParent().getParent().layout(true);
      }
    });
  }

  public Control createTab(CTabFolder cTabFolder) {
    Composite composite = createMainComposite(cTabFolder);

    // search
    this.searchCombo = this.createSavedSearchCombo(composite, "s.searchFor", "simpleSearchFor");

    // network
    this.createNetworkCombo(composite);

    // fileType
    this.fileTypeCombo = createFileType(composite);

    // searchType
    this.searchTypeCombo = createSearchTypeCombo(composite);

    // searchTip
    String platform = SWT.getPlatform();
    if (!"gtk".equals(platform) && !"motif".equals(platform))
      createSearchTip(composite);

    return composite;
  }

  public String getText() {
    return SResources.getString("s.tab.simple");
  }

  public void performSearch() {
    if (searchCombo.getText().equals(SResources.S_ES))
      return;

    String string = this.searchCombo.getText();
    this.searchCombo.add(string, 0);

    if (!Sancho.hasCollectionFactory())
      return;

    SearchQuery searchQuery = viewFrame.getCore().getCollectionFactory().getSearchQuery();
    searchQuery.setSearchString(string);

    addNetwork(searchQuery);

    parseFileType(fileTypeCombo, searchQuery);
    parseSearchType(searchTypeCombo, searchQuery);

    searchQuery.send();
    new ResultTab(viewFrame, searchTab.getCTabFolder(), this.searchTab, searchQuery.getSearchId(), string);
    searchCombo.setText(SResources.S_ES);
  }
}