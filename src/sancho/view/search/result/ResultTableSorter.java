/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.search.result;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import sancho.model.mldonkey.Result;
import sancho.view.viewer.GSorter;

public class ResultTableSorter extends GSorter {
  public ResultTableSorter(ResultTableView rTableViewer) {
    super(rTableViewer);
  }

  public boolean sortOrder(int columnIndex) {
    switch (cViewer.getColumnIDs()[columnIndex]) {
      case ResultTableView.NETWORK :
      case ResultTableView.NAME :
      case ResultTableView.CODEC :
        return true;

      default :
        return false;
    }
  }

  public int compare(Viewer viewer, Object obj1, Object obj2) {
    Result result1 = (Result) obj1;
    Result result2 = (Result) obj2;

    switch (cViewer.getColumnIDs()[columnIndex]) {
      case ResultTableView.NETWORK :
        return compareStrings(result1.getNetworkName(), result2.getNetworkName());

      case ResultTableView.NAME :
        return compareStrings(result1.getName(), result2.getName());

      case ResultTableView.SIZE :
        return compareLongs(result1.getSize(), result2.getSize());

      case ResultTableView.FORMAT :
        return compareStrings(result1.getFormat(), result2.getFormat());

      case ResultTableView.MEDIA :
        return compareStrings(result1.getType(), result2.getType());

      case ResultTableView.CODEC :
        return compareStrings(result1.getCodecTag(), result2.getCodecTag());

      case ResultTableView.BITRATE :
        return compareInts(result1.getBitrateTag(), result2.getBitrateTag());

      case ResultTableView.LENGTH :
        return compareStrings(result1.getLengthTag(), result2.getLengthTag());

      case ResultTableView.AVAILABILITY :
        return compareInts(result1.getAvail(), result2.getAvail());

      case ResultTableView.COMPLETE_SOURCES :
        return compareInts(result1.getCompleteSources(), result2.getCompleteSources());

      default :
        return compareDefault((TableViewer) viewer, columnIndex, obj1, obj2);

    }
  }
}