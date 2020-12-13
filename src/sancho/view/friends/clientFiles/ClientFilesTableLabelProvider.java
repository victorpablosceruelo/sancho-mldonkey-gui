/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends.clientFiles;

import sancho.model.mldonkey.Result;
import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableLabelProvider;

public class ClientFilesTableLabelProvider extends GTableLabelProvider {

  public ClientFilesTableLabelProvider(ClientFilesTableView rTableViewer) {
    super(rTableViewer);
  }

  public String getColumnText(Object arg0, int columnIndex) {
    Result result = (Result) arg0;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case ClientFilesTableView.NAME :
        return result.getName();

      case ClientFilesTableView.SIZE :
        return result.getSizeString();

      case ClientFilesTableView.FORMAT :
        return result.getFormat();

      case ClientFilesTableView.MEDIA :
        return result.getType();

      case ClientFilesTableView.CODEC :
        return result.getCodecTag();

      case ClientFilesTableView.BITRATE :
        return result.getBitrateTagString();

      case ClientFilesTableView.LENGTH :
        return result.getLengthTag();

      case ClientFilesTableView.HASH :
        return result.getMd4().toUpperCase();

      default :
        return SResources.S_ES;
    }
  }

}
