/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import sancho.core.Sancho;
import sancho.utility.VersionInfo;
import sancho.view.utility.SResources;
import sancho.view.utility.WebLauncher;

public class AboutDialog extends Dialog {

  private static final String S_URL = "URL:";
  private static final String S_HP = "sancho-gui.sf.net";

  private static final String S_CODE = "Code:";
  private static final String S_RO = "Rutger Ovidius";

  private static final String S_GRAPHICS = "Graphics:";
  private static final String S_BT = "Bruce Thomas";

  private static final int _X1 = 40;
  private static final int _X2 = 100;

  private static final int _X3 = 230;
  private static final int _X4 = 330;

  Rectangle btRect = new Rectangle(38, 395, 162, 17);
  Rectangle roRect = new Rectangle(38, 375, 162, 17);
  Rectangle urlRect = new Rectangle(38, 356, 162, 17);

  Cursor cursorOver;
  boolean mOver;

  public AboutDialog(Shell shell) {
    super(shell);
    setDefaultImage(SResources.getImage("ProgramIcon"));
    setShellStyle(SWT.NO_TRIM | SWT.NO_BACKGROUND | SWT.ON_TOP);
    cursorOver = new Cursor(shell.getDisplay(), SWT.CURSOR_HAND);
  }

  public boolean close() {
    if (cursorOver != null)
      cursorOver.dispose();
    return super.close();
  }

  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(VersionInfo.getName() + " " + VersionInfo.getVersion());
  }

  // bla bla bla
  public Control createContents(Composite parent) {

    final Canvas canvas = new Canvas(parent, SWT.NONE);
    final Image image = SResources.getImage("about");

    GridData gridData = new GridData();
    gridData.widthHint = image.getBounds().width;
    gridData.heightHint = image.getBounds().height;
    canvas.setLayoutData(gridData);
    canvas.setLayout(new FillLayout());
    canvas.addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent e) {

        int y = 315;
        int dy = 20;

        e.gc.drawImage(image, e.x, e.y, e.width, e.height, e.x, e.y, e.width, e.height);
        e.gc.setForeground(canvas.getDisplay().getSystemColor(SWT.COLOR_BLACK));

        drawTextAt(e.gc, "Version:", VersionInfo.getVersion(), "System:", System.getProperty("os.name"), y);

        y += dy;

        drawTextAt(e.gc, "SWT:", VersionInfo.getSWTPlatform() + "-" + SWT.getVersion(), "Processors:", String
            .valueOf(Runtime.getRuntime().availableProcessors()), y);

        y += dy;

        drawTextAt(e.gc, SResources.S_ES, SResources.S_ES, "Uptime:", Sancho.getUptime(), y);
        drawTextAt(e.gc, SWT.COLOR_BLACK, S_URL, S_HP, y);

        y += dy;

        drawTextAt(e.gc, SResources.S_ES, SResources.S_ES, "Total Memory:", String.valueOf(Runtime.getRuntime().totalMemory()), y);
        drawTextAt(e.gc, SWT.COLOR_BLACK, S_CODE, S_RO, y);

        y += dy;

        drawTextAt(e.gc, SResources.S_ES, SResources.S_ES, "Free Memory:", String.valueOf(Runtime.getRuntime().freeMemory()), y);
        drawTextAt(e.gc, SWT.COLOR_BLACK, S_GRAPHICS, S_BT, y);

      }
    });

    canvas.addMouseMoveListener(new MouseMoveListener() {
      public void mouseMove(MouseEvent e) {
        Canvas canvas = (Canvas) e.widget;
        if (btRect.contains(e.x, e.y) && !mOver) {
          canvas.getShell().setCursor(cursorOver);
          GC gc = new GC(canvas);
          drawTextAt(gc, SWT.COLOR_WHITE, S_GRAPHICS, S_BT, 395);
          gc.dispose();
          mOver = true;
        } else if (urlRect.contains(e.x, e.y) && !mOver) {
          canvas.getShell().setCursor(cursorOver);
          GC gc = new GC(canvas);
          drawTextAt(gc, SWT.COLOR_WHITE, S_URL, S_HP, 355);
          gc.dispose();
          mOver = true;
        } else if (roRect.contains(e.x, e.y) && !mOver) {
          canvas.getShell().setCursor(cursorOver);
          GC gc = new GC(canvas);
          drawTextAt(gc, SWT.COLOR_WHITE, S_CODE, S_RO, 375);
          gc.dispose();
          mOver = true;

        } else if (mOver
            && (!roRect.contains(e.x, e.y) && !btRect.contains(e.x, e.y) && !urlRect.contains(e.x, e.y))) {
          canvas.getShell().setCursor(null);
          GC gc = new GC(canvas);
          drawTextAt(gc, SWT.COLOR_BLACK, S_URL, S_HP, 355);
          drawTextAt(gc, SWT.COLOR_BLACK, S_CODE, S_RO, 375);
          drawTextAt(gc, SWT.COLOR_BLACK, S_GRAPHICS, S_BT, 395);
          gc.dispose();
          mOver = false;
        }
      }

    });

    canvas.addMouseListener(new MouseAdapter() {
      public void mouseDown(MouseEvent e) {
        if (btRect.contains(e.x, e.y))
          WebLauncher.openLink(VersionInfo.getBruceHomePage());
        else if (urlRect.contains(e.x, e.y))
          WebLauncher.openLink(VersionInfo.getHomePage());
        else if (roRect.contains(e.x, e.y))
          WebLauncher.openLink(VersionInfo.getHomePage());
      }

      public void mouseUp(MouseEvent e) {
        AboutDialog.this.close();
      }
    });

    canvas.update();
    return parent;
  }

  public void drawTextAt(GC gc, String s1, String s2, String s3, String s4, int y) {
    gc.drawText(s1, _X1, y, true);
    gc.drawText(s2, _X2, y, true);
    gc.drawText(s3, _X3, y, true);
    gc.drawText(s4, _X4, y, true);
  }

  public void drawTextAt(GC gc, int color, String s1, String s2, int y) {
    gc.setForeground(getShell().getDisplay().getSystemColor(color));
    gc.drawText(s1, _X1, y, true);
    gc.drawText(s2, _X2, y, true);
  }

}