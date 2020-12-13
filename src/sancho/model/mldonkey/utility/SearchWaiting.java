/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.utility;

public class SearchWaiting {

  private int id;
  private int numWaiting;

  public int getId() {
    return id;
  }

  public int getNumWaiting() {
    return numWaiting;
  }

  public void read(MessageBuffer messageBuffer) {
    this.id = messageBuffer.getInt32();
    this.numWaiting = messageBuffer.getInt32();
  }
}
