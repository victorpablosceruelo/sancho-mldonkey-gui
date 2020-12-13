/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;

import sancho.view.MainWindow;
import sancho.view.utility.SResources;

public class CopyED2KLinkToClipboardAction extends Action {
  private boolean useHTML;
  private String[] links;

  public CopyED2KLinkToClipboardAction(boolean useHTML, String[] links) {
    super(SResources.getString("mi.ed2kCopy") + (useHTML ? " (html)" : SResources.S_ES));
    this.useHTML = useHTML;
    this.links = links;
    setImageDescriptor(SResources.getImageDescriptor("edonkey"));
  }

  public void run() {
    if (links == null)
      return;

    String link = SResources.S_ES;
    String fileName;
    String lSeparator = System.getProperty("line.separator");

    for (int i = 0; i < links.length; i++) {
      if (!links[i].equals(SResources.S_ES)) {

        if (link.length() > 0)
          link += lSeparator;

        if (useHTML) {
          // 13 = "ed2k://|file|"
          if (links[i].length() > 15) {
            fileName = links[i].substring(13, links[i].indexOf("|", 13));
            link += ("<a href=\"" + links[i] + "\">" + fileName + "</a>");
          }
        } else {
          link += links[i];
        }
      }
    }

    MainWindow.copyToClipboard(link);
  }
}
