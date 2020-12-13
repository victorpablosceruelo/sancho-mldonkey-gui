/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewer.filters;

import gnu.regexp.RE;
import gnu.regexp.REException;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import sancho.model.mldonkey.Client;
import sancho.model.mldonkey.File;
import sancho.model.mldonkey.Result;
import sancho.model.mldonkey.Server;
import sancho.model.mldonkey.SharedFile;
import sancho.view.downloadComplete.DownloadCompleteItem;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.viewer.GView;

public class RefineFilter extends ViewerFilter {

  private static String SEPARATOR = " ";

  private String refineString;
  private GView gView;
  private RE regex;
  private boolean refine;
  private boolean returnValue;
  private boolean searchAlternates;
  private static StringBuffer stringBuffer = new StringBuffer();

  public RefineFilter(GView gView) {
    this.gView = gView;
    update();
  }

  public void update() {
    if ((refine = (!gView.getRefineString().equals(SResources.S_ES) && !gView.getRefineString().equals("-"))))
      try {
        String s = gView.getRefineString();
        if (s.startsWith("-"))
          s = s.substring(1);
        regex = new RE(s, RE.REG_ICASE);
      } catch (REException e) {
        refine = false;
      }

    returnValue = !PreferenceLoader.loadBoolean("refineFilterNegation");
    if (returnValue && gView.getRefineString().startsWith("-"))
      returnValue = false;

    searchAlternates = PreferenceLoader.loadBoolean("refineFilterAlternates");
  //  ((ICustomViewer) gView.getViewer()).closeAllTTE();
    gView.refresh(true);
  }

  public boolean select(Viewer viewer, Object parentElement, Object element) {
    if (!refine)
      return true;

    String string;

    if (element instanceof Server)
      string = ((Server) element).getName() + " " + ((Server) element).getDescription();
    else if (element instanceof Result) {
      Result result = (Result) element;
      stringBuffer.setLength(0);
      stringBuffer.append(result.getName());
      if (searchAlternates)
        appendAlternates(result.getNames());
      string = stringBuffer.toString();
    } else if (element instanceof SharedFile) {
      string = ((SharedFile) element).getName();
    } else if (element instanceof Client) {
      Client client = (Client) element;
      stringBuffer.setLength(0);
      stringBuffer.append(client.getName());
      stringBuffer.append(SEPARATOR);
      stringBuffer.append(client.getUploadFilename());
      string = stringBuffer.toString();
    } else if (element instanceof File) {
      File file = (File) element;
      stringBuffer.setLength(0);
      stringBuffer.append(file.getName());
      if (searchAlternates)
        appendAlternates(file.getNames());
      string = stringBuffer.toString();
    } else if (element instanceof DownloadCompleteItem) {
      string = ((DownloadCompleteItem) element).getName();
    } else
      return true;

    if (regex.getMatch(string) != null)
      return returnValue;

    return !returnValue;
  }

  public void appendAlternates(String[] stringArray) {
    for (int i = 0; i < stringArray.length; i++) {
      stringBuffer.append(SEPARATOR);
      stringBuffer.append(stringArray[i]);
    }
  }

  public String toString() {
    return GView.REFINE_FILTER + "," + gView.getRefineString() + ",";
  }

}