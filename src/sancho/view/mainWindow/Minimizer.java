/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.mainWindow;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.widgets.Shell;

import sancho.core.Sancho;
import sancho.model.mldonkey.ClientStats;
import sancho.view.MainWindow;
import sancho.view.utility.SResources;

public class Minimizer implements Observer {
  protected Shell shell;
  protected String titleBarText;
  protected static final StringBuffer stringBuffer = new StringBuffer();

  protected static final String S_D = "D:";
  protected static final String S_U = "U:";

  public Minimizer(MainWindow mainWindow, String titleBarText) {
    this.shell = mainWindow.getShell();
    this.titleBarText = titleBarText;
  }

  public void setTitleBarText() {
    shell.setText(titleBarText);
  }

  public boolean close() {
    return true;
  }

  public void setConnected(boolean b) {
  }

  public boolean minimize() {
    if (Sancho.hasCollectionFactory())
      Sancho.getCore().getClientStats().addObserver(this);
    return true;
  }

  public void forceClose() {
  }

  public void restore() {
    if (Sancho.hasCollectionFactory())
      Sancho.getCore().getClientStats().deleteObserver(this);
    setTitleBarText();
  }

  public void update(Observable o, Object obj) {
    final ClientStats clientStats = (ClientStats) o;
    if (clientStats == null)
      return;

    if (shell != null && !shell.isDisposed())
      shell.getDisplay().asyncExec(new Runnable() {
        public void run() {
          if (shell != null && !shell.isDisposed())
            if (shell.getMinimized()) {
              setTitleBar(clientStats);
            } else {
               //if ("fox".equals(SWT.getPlatform()))
               // restore();
            }
        }
      });
  }

  public void setTitleBar(ClientStats clientStats) {
    stringBuffer.setLength(0);
    stringBuffer.append(SResources.S_OB);
    stringBuffer.append(S_D);
    stringBuffer.append(clientStats.getTcpDownRateStringS());
    stringBuffer.append(SResources.S_CB);
    stringBuffer.append(SResources.S_OB);
    stringBuffer.append(S_U);
    stringBuffer.append(clientStats.getTcpUpRateStringS());
    stringBuffer.append(SResources.S_CB);
    shell.setText(stringBuffer.toString());
  }

}