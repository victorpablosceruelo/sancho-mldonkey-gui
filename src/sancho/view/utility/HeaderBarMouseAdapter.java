/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;

public class HeaderBarMouseAdapter extends MouseAdapter {
  private CLabel cLabel;
  private MenuManager menuManager;
 
  public HeaderBarMouseAdapter(CLabel cLabel, MenuManager menuManager) {
    this.cLabel = cLabel;
    this.menuManager = menuManager;
  }

  private boolean overImage(int x) {
    return x < cLabel.getImage().getBounds().width;
  }

  private void showMenu(Point p) {
    Menu menu = menuManager.createContextMenu(cLabel);
    menu.setLocation(p);
    menu.setVisible(true);
  }

  public void mouseDown(MouseEvent e) {
    if (((e.button == 1) && overImage(e.x)) || (e.button == 3)) {
      Point p;
      if (e.button == 1) {
        p = new Point(0, cLabel.getBounds().height);
      } else {
        p = new Point(e.x, e.y);
      }
      showMenu(((CLabel) e.widget).toDisplay(p));
    }
  }
}
