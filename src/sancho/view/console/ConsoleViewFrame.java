/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.console;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

import sancho.core.Sancho;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.SResources;
import sancho.view.viewFrame.ViewFrame;

public class ConsoleViewFrame extends ViewFrame {

  int numToolItems;

  public ConsoleViewFrame(Composite parent, String prefString, String prefImageString, AbstractTab aTab) {
    super(parent, prefString, prefImageString, aTab);
    createViewToolBar();
  }

  public void createViewToolBar() {
    super.createViewToolBar();

    numToolItems = PreferenceLoader.loadInt("consoleToolItems");

    for (int i = 1; i < numToolItems + 1; i++) {
      final int num = i;
      addToolItem(PreferenceLoader.loadString("consoleToolItem" + num), String.valueOf(i),
          new SelectionAdapter() {
            public void widgetSelected(SelectionEvent s) {
              String command = PreferenceLoader.loadString("consoleToolItem" + num);
              if (!command.equals(SResources.S_ES))
                Sancho.send(OpCodes.S_CONSOLE_MESSAGE, PreferenceLoader.loadString("consoleToolItem" + num));
            }
          });
    }
  }

  public void updateDisplay() {
    super.updateDisplay();

    if (numToolItems != PreferenceLoader.loadInt("consoleToolItems") && toolBar != null) {
      for (int i = toolBar.getItemCount() - 1; i >= 0; i--)
        toolBar.getItems()[i].dispose();

      toolBar.dispose();
      createViewToolBar();
      toolBar.getParent().layout();
    } else if (toolBar != null) {
      for (int i = 1; i <= toolBar.getItemCount(); i++) {
        toolBar.getItems()[i - 1].setToolTipText(PreferenceLoader.loadString("consoleToolItem" + i));
      }
    }
  }
}