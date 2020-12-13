/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewFrame.actions;

import org.eclipse.jface.action.Action;

import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.viewFrame.TabbedViewFrame;

public class TabsOnTopAction extends Action {

  String tabPrefString;
  TabbedViewFrame viewFrame;

  public TabsOnTopAction(String tabPrefString, TabbedViewFrame viewFrame) {
    super(SResources.getString("mi.tabsOnTop"), Action.AS_CHECK_BOX);
    this.tabPrefString = tabPrefString;
    this.viewFrame = viewFrame;
  }

  public boolean isChecked() {
    return PreferenceLoader.loadBoolean(tabPrefString + "TabsOnTop");
  }

  public void run() {
    PreferenceLoader.getPreferenceStore().setValue(tabPrefString + "TabsOnTop", !isChecked());
    viewFrame.toggleTabPosition();
  }

}