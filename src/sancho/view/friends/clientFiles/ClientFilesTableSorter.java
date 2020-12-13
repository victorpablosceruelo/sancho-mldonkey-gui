/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.friends.clientFiles;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import sancho.model.mldonkey.Result;
import sancho.view.viewer.GSorter;

public class ClientFilesTableSorter extends GSorter {
  public ClientFilesTableSorter(ClientFilesTableView rTableViewer) {
    super(rTableViewer);
  }

  public int compare(Viewer viewer, Object obj1, Object obj2) {
    Result result1 = (Result) obj1;
    Result result2 = (Result) obj2;

    switch (cViewer.getColumnIDs()[columnIndex]) {

      case ClientFilesTableView.NAME :
        return compareStrings(result1.getName(), result2.getName());
      case ClientFilesTableView.SIZE :
        return compareLongs(result1.getSize(), result2.getSize());
      case ClientFilesTableView.FORMAT :
        return compareStrings(result1.getFormat(), result2.getFormat());
      case ClientFilesTableView.MEDIA :
        return compareStrings(result1.getType(), result2.getType());
      case ClientFilesTableView.CODEC :
        return compareStrings(result1.getCodecTag(), result2.getCodecTag());
      case ClientFilesTableView.BITRATE :
        return compareInts(result1.getBitrateTag(), result2.getBitrateTag());
      case ClientFilesTableView.LENGTH :
        return compareStrings(result1.getLengthTag(), result2.getLengthTag());
      case ClientFilesTableView.HASH :
        return compareStrings(result1.getMd4(), result2.getMd4());
      default :
        return compareDefault((TableViewer) viewer, columnIndex, obj1, obj2);

    }
  }
}