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

public class SearchTab_Audio extends ASearchTab2 {
  private Combo albumCombo;
  private Combo artistCombo;
  private Combo bitrateCombo;

  public SearchTab_Audio(ResultViewFrame viewFrame, SearchTab tab) {
    super(viewFrame, tab);
  }

  public Control createTab(CTabFolder cTabFolder) {
    Composite composite = createMainComposite(cTabFolder);

    this.searchCombo = createSavedSearchCombo(composite, "s.a.title", "audioTitleSearchFor");
    this.artistCombo = createSavedSearchCombo(composite, "s.a.artist", "audioArtistSearchFor");
    this.albumCombo = createSavedSearchCombo(composite, "s.a.album", "audioAlbumSearchFor");

    createSeparator(composite);

    // bitrate
    String[] items = {SResources.S_ES, "32kb", "64kb", "96kb", "128kb", "160kb", "192kb", "256kb", "320kb"};
    this.bitrateCombo = createCombo(composite, SWT.READ_ONLY, "s.a.bitrate", items);

    // network
    this.createNetworkCombo(composite);

    // format
    items = items = new String[]{SResources.S_ES, "mp3", "ogg", "wav", "midi"};
    formatCombo = createCombo(composite, SWT.NULL, "s.format", items);

    this.searchTypeCombo = createSearchTypeCombo(composite);

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
    return SResources.getString("s.tab.audio");
  }

  public void performSearch() {
    String title = this.searchCombo.getText();
    String album = this.albumCombo.getText();
    String artist = this.artistCombo.getText();
    String tabTitle = SResources.S_ES;

    if (title.equals(SResources.S_ES) && artist.equals(SResources.S_ES) && album.equals(SResources.S_ES))
      return;

    if (!Sancho.hasCollectionFactory())
      return;

    SearchQuery searchQuery = viewFrame.getCore().getCollectionFactory().getSearchQuery();

    if (!title.equals(SResources.S_ES)) {
      searchCombo.add(title);
      searchQuery.setMp3Title(title);
      tabTitle += title;
    }

    if (!artist.equals(SResources.S_ES)) {
      artistCombo.add(artist);
      searchQuery.setMp3Artist(artist);
      if (tabTitle.length() > 0)
        tabTitle += "/";
      tabTitle += artist;

    }
    if (!album.equals(SResources.S_ES)) {
      albumCombo.add(album);
      searchQuery.setMp3Album(album);
      if (tabTitle.length() > 0)
        tabTitle += "/";
      tabTitle += album;
    }

    artistCombo.setText(SResources.S_ES);
    albumCombo.setText(SResources.S_ES);
    searchCombo.setText(SResources.S_ES);

    String bitrateString = bitrateCombo.getText();

    if (!bitrateString.equals(SResources.S_ES)) {
      int rate;
      try {
        rate = Integer.parseInt(bitrateString.substring(0, bitrateString.length() - 2));
      } catch (NumberFormatException e) {
        rate = 0;
      }
      searchQuery.setMp3Bitrate(String.valueOf(rate));
    }

    parseSearchType(searchTypeCombo, searchQuery);

    addMinSizeToQuery(searchQuery, minCombo);
    addMaxSizeToQuery(searchQuery, maxCombo);

    if (!resultCombo.getText().equals(SResources.S_ES)) {
      int maxResults = 0;

      try {
        maxResults = Integer.parseInt(resultCombo.getText());
      } catch (NumberFormatException e) {
        maxResults = 100;
      }
      searchQuery.setMaxSearchResults(maxResults);
    }

    if (!formatCombo.getText().equals(SResources.S_ES))
      searchQuery.setFormat(formatCombo.getText());

    addNetwork(searchQuery);

    searchQuery.send();
    new ResultTab(viewFrame, searchTab.getCTabFolder(), this.searchTab, searchQuery.getSearchId(), tabTitle);
  }

}