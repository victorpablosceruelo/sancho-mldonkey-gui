/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.search.result;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import sancho.model.mldonkey.Result;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableLabelProvider;

public class ResultTableLabelProvider extends GTableLabelProvider implements IColorProvider {
  private Color alreadyDownloadedColor;
  private Color containsFakeColor;
  private Color defaultColor;

  public ResultTableLabelProvider(ResultTableView rTableViewer) {
    super(rTableViewer);
  }

  public Image getColumnImage(Object arg0, int columnIndex) {

    Result result = (Result) arg0;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case ResultTableView.NETWORK :
        if (result.isDownloading())
          return SResources.getImage("down_arrow_green");
        else
          return result.getNetworkImage();
      case ResultTableView.AVAILABILITY :
        return result.getRating().getImage();
      default :
        return null;
    }
  }

  public String getColumnText(Object arg0, int columnIndex) {
    Result result = (Result) arg0;
    switch (cViewer.getColumnIDs()[columnIndex]) {
      case ResultTableView.NETWORK :
        return result.getNetworkName();
      case ResultTableView.NAME :
        return result.getName();
      case ResultTableView.SIZE :
        return result.getSizeString();
      case ResultTableView.FORMAT :
        return result.getFormat();
      case ResultTableView.MEDIA :
        return result.getType();
      case ResultTableView.CODEC :
        return result.getCodecTag();
      case ResultTableView.BITRATE :
        return result.getBitrateTagString();
      case ResultTableView.LENGTH :
        return result.getLengthTag();
      case ResultTableView.AVAILABILITY :
        return result.getRatingString();
      case ResultTableView.COMPLETE_SOURCES :
        return result.getCompleteSourcesString();
      default :
        return SResources.S_ES;
    }
  }

  public Color getBackground(Object element) {
    return null;
  }

  public Color getForeground(Object element) {
    if (element instanceof Result) {
      Result result = (Result) element;
      if (result.downloaded()) {
        return alreadyDownloadedColor;
      } else if (result.containsFake()) {
        return containsFakeColor;
      }
    }
    return defaultColor;
  }

  public void updateDisplay() {
    defaultColor = PreferenceLoader.loadColor("resultDefaultColor");
    alreadyDownloadedColor = PreferenceLoader.loadColor("resultAlreadyDownloadedColor");
    containsFakeColor = PreferenceLoader.loadColor("resultFakeColor");
  }

}