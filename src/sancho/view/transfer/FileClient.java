/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer;

import sancho.model.mldonkey.Client;
import sancho.model.mldonkey.File;

public class FileClient {
  private File file;
  private Client client;
  private boolean deleteFlag;

  public FileClient(File file, Client client) {
    this.file = file;
    this.client = client;
  }

  public File getFile() {
    return file;
  }

  public Client getClient() {
    return client;
  }

  public boolean equals(Object o) {
    if (o instanceof FileClient) {
      Client c = ((FileClient) o).getClient();
      File f = ((FileClient) o).getFile();
      return c.equals(this.getClient()) && f.equals(this.getFile());
    } else
      return false;
  }

  public void setDelete() {
    deleteFlag = true;
  }

  public boolean getDelete() {
    return deleteFlag;
  }
}
