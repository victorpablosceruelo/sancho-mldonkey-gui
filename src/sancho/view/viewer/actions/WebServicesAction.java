/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.actions;

import org.eclipse.jface.action.Action;

import sancho.view.utility.SResources;
import sancho.view.utility.WebLauncher;

public class WebServicesAction extends Action {
  public static final int JIGLE = 0;
  public static final int BITZI = 1;
  public static final int FILEDONKEY = 2;
  public static final int SHAREREACTOR = 3;
  public static final int DONKEY_FAKES = 4;
  private String string;
  private int type;

  public WebServicesAction(int type, String string) {
    super();
    this.type = type;
    this.string = string;

    switch (type) {
      case JIGLE :
        setText(SResources.getString("mi.web.jigle"));
        setImageDescriptor(SResources.getImageDescriptor("jigle"));
        break;
      case BITZI :
        setText(SResources.getString("mi.web.bitzi"));
        setImageDescriptor(SResources.getImageDescriptor("bitzi"));
        break;
      case FILEDONKEY :
        setText(SResources.getString("mi.web.filedonkey"));
        setImageDescriptor(SResources.getImageDescriptor("edonkey"));
        break;
      case SHAREREACTOR :
        setText(SResources.getString("mi.web.srFakeCheck"));
        setImageDescriptor(SResources.getImageDescriptor("sharereactor"));
        break;
      case DONKEY_FAKES :
        setText(SResources.getString("mi.web.donkeyFakes"));
        setImageDescriptor(SResources.getImageDescriptor("edonkey"));
        break;
    }
  }

  public void run() {
    launch(type, string);
  }

  public static void launch(int type, String string) {
    switch (type) {
      case JIGLE :
        WebLauncher.openLink("http://www.jigle.com/search?p=ed2k:" + string);
        break;
      case BITZI :
        WebLauncher.openLink("http://bitzi.com/lookup/" + string);
        break;
      case FILEDONKEY :
        WebLauncher.openLink("http://www.filedonkey.com/file.html?md4=" + string);
        break;
      case SHAREREACTOR :
        WebLauncher.openLink("http://www.sharereactor.com/fakesearch.php?search=" + string);
        break;
      case DONKEY_FAKES :
        WebLauncher.openLink("http://donkeyfakes.gambri.net/index.php?action=search&ed2k=" + string);
        break;
    }
  }
}