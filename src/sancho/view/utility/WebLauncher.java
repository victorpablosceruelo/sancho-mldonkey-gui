/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

// A modified ProductInfoDialog.java
package sancho.view.utility;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import sancho.utility.VersionInfo;
import sancho.view.preferences.PreferenceLoader;

public class WebLauncher {
  private static String webBrowser = SResources.S_ES;
  private static boolean webBrowserOpened;

  private static Process openWebBrowser(String href) throws IOException {
    Process p = null;

    String[] cmdArray = new String[2];
    cmdArray[0] = webBrowser;
    cmdArray[1] = href;

    if ((webBrowser == null) || webBrowser.equals(SResources.S_ES)) {
      try {
        webBrowser = "mozilla"; //$NON-NLS-1$
        cmdArray[0] = webBrowser;
        p = Runtime.getRuntime().exec(cmdArray); //$NON-NLS-1$;
      } catch (IOException e) {
        try {
          webBrowser = "konqueror"; //$NON-NLS-1$
          cmdArray[0] = webBrowser;
          p = Runtime.getRuntime().exec(cmdArray); //$NON-NLS-1$;
        } catch (IOException f) {
          p = null;
          webBrowser = "netscape"; //$NON-NLS-1$
          cmdArray[0] = webBrowser;
        }
      }
    }

    if (p == null) {
      try {
        p = Runtime.getRuntime().exec(cmdArray); //$NON-NLS-1$;
      } catch (IOException e) {
        p = null;
        throw e;
      }
    }

    return p;
  }

  public static void openLink(String href) {
    webBrowser = PreferenceLoader.loadString("defaultWebBrowser");

    // format the href for an html file (file:///<filename.html>
    // required for Mac only.
    if (href.startsWith("file:")) { //$NON-NLS-1$
      href = href.substring(5);

      while (href.startsWith("/"))
        href = href.substring(1);

      href = "file:///" + href; //$NON-NLS-1$
    }

    final String localHref = href;

    final Display d = Display.getCurrent();

    String platform = VersionInfo.getSWTPlatform();

    if ("win32".equals(platform) || "win32-fox".equals(platform))
      Program.launch(localHref);
    else if ("carbon".equals(platform)) { //$NON-NLS-1$
      try {
        String[] cmdArray = new String[2];

        cmdArray[0] = "/usr/bin/open";
        cmdArray[1] = localHref;

        Runtime.getRuntime().exec(cmdArray); //$NON-NLS-1$
      } catch (IOException e) {
        openWebBrowserError(d);
      }
    } else {
      Thread launcher = new Thread("webBrowser") { //$NON-NLS-1$
        public void run() {
          try {
            if (webBrowserOpened
                && (webBrowser.equals("MozillaFirebird") || webBrowser.equals("netscape") || webBrowser
                    .equals("mozilla"))) {

              String[] cmdArray = new String[3];
              cmdArray[0] = webBrowser;
              cmdArray[1] = "-remote";
              cmdArray[2] = "openURL(" + localHref + ")";

              Runtime.getRuntime().exec(cmdArray);
              // Runtime.getRuntime().exec(webBrowser + " -remote openURL(" + localHref + ")");
            } else {
              Process p = openWebBrowser(localHref);
              webBrowserOpened = true;

              try {
                if (p != null)
                  p.waitFor();
              } catch (InterruptedException e) {
                openWebBrowserError(d);
              } finally {
                webBrowserOpened = false;
              }
            }
          } catch (IOException e) {
            openWebBrowserError(d);
          }
        }
      };

      launcher.start();
    }
  }

  private static void openWebBrowserError(final Display display) {
    display.asyncExec(new Runnable() {
      public void run() {
        MessageBox failBox = new MessageBox(new Shell(display), SWT.ICON_ERROR);
        failBox.setText("Fail");
        failBox.setMessage("openWebBrowserError: \nCheck Preferences>Default Web Browser");
        failBox.open();
      }
    });
  }
}