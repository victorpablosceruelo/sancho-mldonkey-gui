/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import sancho.view.preferences.PreferenceLoader;

public class Splash {
  private static Shell shell = null;
  private static Display display;

  public static boolean[] on = new boolean[10];
  public static int[] boxes = {13, 66, 107, 162, 212, 259, 314, 363, 398, 441, 492};

  public Splash(Display displayX) {
    display = displayX;
    if (PreferenceLoader.loadBoolean("splashScreen"))
      createContents(display);
  }

  public void createContents(Display display) {
    shell = new Shell(display, SWT.NO_TRIM | SWT.NO_BACKGROUND | SWT.ON_TOP);
    shell.setLayout(new FillLayout());

    Image image = SResources.getImage("splashScreen");
    Rectangle displayBounds = display.getPrimaryMonitor().getBounds();
    Rectangle imageBounds = image.getBounds();
    shell.setBounds(displayBounds.x + ((displayBounds.width - imageBounds.width) / 2), displayBounds.y
        + ((displayBounds.height - imageBounds.height) / 2), imageBounds.width, imageBounds.height);

    shell.open();
    shell.update();
  }

  public static void updateText(String resString) {
    updateText(resString, "");
  }

  public static void updateText(String resString, String parameter) {
    updateText(resString, parameter, -1);
  }

  public static void updateText(final String resString, final String parameter, final int box) {
    if (shell == null)
      return;

    String string = SResources.getString(resString) + parameter;
    Image bufferImage = new Image(shell.getDisplay(), shell.getBounds());
    GC gc = new GC(bufferImage);
    gc.drawImage(SResources.getImage("splashScreen"), 0, 0);

    //    gc.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_GRAY));
    //    gc.drawText(string + "...", 16, shell.getBounds().height - 24, true);
    gc.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    gc.drawText(string + "...", 15, shell.getBounds().height - 25, true);

    if (box >= 0) {
      on[box] = true;
    }

    Image image = SResources.getImage("splashHighlight");

    for (int i = 0; i < on.length; i++) {
      if (on[i]) {
        int width = boxes[i + 1] - boxes[i];
        gc.drawImage(image, boxes[i], 0, width, 57, boxes[i], 173, width, 57);
      }
    }

    GC gcd = new GC(shell);
    gcd.drawImage(bufferImage, 0, 0);
    gcd.dispose();
    bufferImage.dispose();
    gc.dispose();
    
  }

  public static void dispose() {
    if (shell == null)
      return;

    shell.dispose();
    shell = null;
  }

  public static void setVisible(final boolean visible) {
    if (shell == null)
      return;

    shell.setVisible(visible);
  }

}