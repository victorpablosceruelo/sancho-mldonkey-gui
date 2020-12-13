/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import sancho.core.Sancho;
import sancho.model.mldonkey.utility.SearchQuery;
import sancho.view.SearchTab;
import sancho.view.search.result.ResultTab;
import sancho.view.search.result.ResultViewFrame;
import sancho.view.utility.SResources;

public class SearchTab_Advanced extends ASearchTab2 {

  Combo fileType;

  public SearchTab_Advanced(ResultViewFrame viewFrame, SearchTab tab) {
    super(viewFrame, tab);
  }

  public Control createTab(CTabFolder cTabFolder) {
    Composite composite = createMainComposite(cTabFolder);

    // search text
    this.searchCombo = this.createSavedSearchCombo(composite, "s.searchFor", "advancedSearchFor");

    // network
    this.createNetworkCombo(composite);

    // fileType
    fileType = createFileType(composite);

    // searchType 
    searchTypeCombo = createSearchTypeCombo(composite);

    // fileFormat
    String[] items = {SResources.S_ES, "exe", "bin", "img", "gif", "jpg"};
    this.formatCombo = createCombo(composite, SWT.NULL, "s.format", items);

    createSeparator(composite);

    // minAvail
    items = new String[]{SResources.S_ES, "3", "5", "10", "25", "50"};
    this.minAvailCombo = createCombo(composite, SWT.NULL, "s.minAvail", items);

    // minSize
    this.minCombo = createMinMaxCombo(composite, SWT.NULL, "s.minSize");

    // maxSize
    this.maxCombo = createMinMaxCombo(composite, SWT.NULL, "s.maxSize");

    // maxResults
    items = new String[]{SResources.S_ES, "50", "100", "200", "400"};
    this.resultCombo = createIntegerCombo(composite, SWT.NULL, "s.maxResults", items);

    composite.setData(this);

    return composite;
  }

  public String getText() {
    return SResources.getString("s.tab.advanced");
  }

  public void performSearch() {
    String string = this.searchCombo.getText();

    if (string.equals(SResources.S_ES) || !Sancho.hasCollectionFactory())
      return;

    SearchQuery searchQuery = viewFrame.getCore().getCollectionFactory().getSearchQuery();
    searchQuery.setSearchString(string);

    parseFileType(fileType, searchQuery);
    parseSearchType(searchTypeCombo, searchQuery);

    addMinSizeToQuery(searchQuery, minCombo);
    addMaxSizeToQuery(searchQuery, maxCombo);
    addMaxResultsToQuery(searchQuery, resultCombo);

    if (!formatCombo.getText().equals(SResources.S_ES))
      searchQuery.setFormat(formatCombo.getText());

    addNetwork(searchQuery);

    searchQuery.send();

    int searchID = searchQuery.getSearchId();
    viewFrame.getCore().getResultCollection().setMinAvail(searchID, parseMinAvail());

    new ResultTab(viewFrame, searchTab.getCTabFolder(), this.searchTab, searchID, string);
  }
}