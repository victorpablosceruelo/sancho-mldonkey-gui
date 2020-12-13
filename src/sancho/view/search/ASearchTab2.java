/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.search;

import java.util.StringTokenizer;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import sancho.model.mldonkey.utility.SearchQuery;
import sancho.utility.SwissArmy;
import sancho.view.SearchTab;
import sancho.view.search.result.ResultViewFrame;
import sancho.view.utility.SResources;

public abstract class ASearchTab2 extends ASearchTab {
  protected Combo formatCombo;
  protected Combo maxCombo;
  protected Combo minAvailCombo;
  protected Combo minCombo;
  protected Combo resultCombo;

  public ASearchTab2(ResultViewFrame viewFrame, SearchTab tab) {
    super(viewFrame, tab);
  }

  public void addMaxSizeToQuery(SearchQuery query, Combo combo) {
    if (combo.getText().equals(SResources.S_ES))
      return;
    query.setMaxSize(parseSizeCombo(combo));
  }

  public void addMinSizeToQuery(SearchQuery query, Combo combo) {
    if (combo.getText().equals(SResources.S_ES))
      return;
    query.setMinSize(parseSizeCombo(combo));
  }

  public void addMaxResultsToQuery(SearchQuery query, Combo combo) {
    if (!combo.getText().equals(SResources.S_ES)) {
      int maxResults = 0;

      try {
        maxResults = Integer.parseInt(resultCombo.getText());
      } catch (NumberFormatException e) {
        maxResults = 100;
      }
      query.setMaxSearchResults(maxResults);
    }
  }

  protected Combo createMinMaxCombo(Composite composite, int style, String resString) {
    String[] items = {SResources.S_ES, "100 KB", "200 KB", "500 KB", "1 MB", "5 MB", "50 MB", "100 MB", "250 MB",
        "500 MB", "1 GB", "2 GB", "3 GB"};

    return createCombo(composite, style, resString, items);
  }

  public int parseMinAvail() {
    int result;
    try {
      result = Integer.parseInt(minAvailCombo.getText());
    } catch (NumberFormatException e) {
      result = 0;
    }
    return result;
  }

  public long parseSizeCombo(Combo combo) {

    String value = SResources.S_ES;
    String unit = "MB";
    StringTokenizer st = new StringTokenizer(combo.getText());
    value = st.nextToken();
    if (st.hasMoreTokens())
      unit = st.nextToken();

    return SwissArmy.stringSizeToLong(value, unit);
  }

}
