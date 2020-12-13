/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.downloadComplete;

import java.util.Date;

import sancho.utility.SwissArmy;
import sancho.view.utility.SResources;

public class DownloadCompleteItem {
  long dateLong;
  String dateString;
  String hash;
  String name;
  long size;
  String sizeString;

  public long getDateLong() {
    return dateLong;
  }

  public String getDateString() {
    return (dateString == null) ? SResources.S_ES : dateString;
  }

  public String getHash() {
    return (hash == null) ? SResources.S_ES : hash;
  }

  public String getLink() {
    return "ed2k://|" + getName() + "|" + getSize() + "|" + getHash() + "|";
  }

  public String getName() {
    return (name == null) ? SResources.S_ES : name;
  }

  public long getSize() {
    return size;
  }

  public String getSizeString() {
    return (sizeString == null) ? SResources.S_ES : sizeString;
  }

  public boolean parseLine(String line) {

    int space = line.indexOf(" ");
    if (space == -1)
      return false;

    String timeStamp = line.substring(0, space);

    try {
      dateLong = Long.parseLong(timeStamp);
      dateString = new Date(dateLong).toString();
    } catch (Exception e) {
      // bla
    }

    String ed2kLink = line.substring(space + 1);

    if (ed2kLink.startsWith("ed2k://|file|")) {
      int end = ed2kLink.indexOf("|", 13);
      name = ed2kLink.substring(13, ed2kLink.indexOf("|", 13));
      int start = end + 1;
      end = ed2kLink.indexOf("|", start);
      try {
        size = Long.parseLong(ed2kLink.substring(start, end));
        sizeString = SwissArmy.calcStringSize(size);
      } catch (NumberFormatException e) {
        size = 0;
      }
      start = end + 1;
      end = ed2kLink.indexOf("|", start);
      hash = ed2kLink.substring(start, end).toUpperCase();
      return true;
    }
    return false;
  }

}