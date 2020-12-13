/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility.setupWizard;

import sancho.view.utility.SResources;

public class HostObject {
  public String hostname = "127.0.0.1";
  public String username = "admin";
  public String password = SResources.S_ES;
  public String description = SResources.S_ES;
  public int coreProtocol = 0;
  public int port = 4001;

  public String ssh_host = SResources.S_ES;
  public String ssh_user = SResources.S_ES;
  public String ssh_pass = SResources.S_ES;
  public int ssh_port = 22;

  public String ssh_rhost = SResources.S_ES;
  public int ssh_rport = 4001;
  public int ssh_lport = 4001;

  public boolean use_ssh = false;

}
