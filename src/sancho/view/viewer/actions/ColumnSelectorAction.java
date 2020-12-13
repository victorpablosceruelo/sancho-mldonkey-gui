/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;

import sancho.view.utility.IDSelector;
import sancho.view.utility.SResources;
import sancho.view.viewer.GView;

public class ColumnSelectorAction extends Action {
  private List gViewList;
  String prefSuffix;

  private ColumnSelectorAction(String prefSuffix) {
    super(prefSuffix + " " + SResources.getString("l.selector"));
    this.prefSuffix = prefSuffix;
    setImageDescriptor(SResources.getImageDescriptor("preferences"));
    gViewList = new ArrayList();
  }

  public ColumnSelectorAction(GView gView) {
    this("TableColumns");
    gViewList.add(gView);
  }

  public ColumnSelectorAction(CTabFolder cTabFolder) {
    this("TableColumns");

    for (int i = 0; i < cTabFolder.getItems().length; i++) {
      CTabItem cTabItem = cTabFolder.getItems()[i];

      if (cTabItem.getData(GView.S_GVIEW) != null)
        gViewList.add(cTabItem.getData(GView.S_GVIEW));
    }
  }

  public void run() {
    if (gViewList.size() == 0)
      return;

    GView gView = (GView) gViewList.get(0);

    IDSelector c = new IDSelector(gView.getShell(), gView.getColumnLabels(), gView.getPreferenceString(),
        prefSuffix);

    if (c.open() == IDSelector.OK) {
      c.savePrefs();

      for (int i = 0; i < gViewList.size(); i++)
        ((GView) gViewList.get(i)).resetColumns();
    }
  }
}
