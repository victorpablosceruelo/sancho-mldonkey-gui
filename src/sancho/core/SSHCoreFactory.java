/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.core;

import java.io.File;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import sancho.utility.SwissArmy;
import sancho.utility.VersionInfo;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.utility.Splash;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

public class SSHCoreFactory extends CoreFactory {
  private JSch jsch;
  private Session session;
  private String ssh_host;
  private int ssh_lport = -1;
  private String ssh_pass;
  private int ssh_port = -1;
  private String ssh_rhost;
  private int ssh_rport = -1;
  private String ssh_user;

  private boolean use_ssh;

  public SSHCoreFactory(Display display) {
    super(display);
  }

  public String getConnectedString() {
    String append = SResources.S_ES;
    if (session != null)
      append = " | " + session.getServerVersion();

    return super.getConnectedString() + " " + append;
  }

  // proper thread?
  public void disconnectSSH() {
    if (jsch != null) {
      if (session != null) {
        try {
          session.delPortForwardingL(ssh_lport);
        } catch (JSchException e) {
          System.err.println("delPortForwarding: " + e);
        }
        session.disconnect();
        session = null;
      }
      jsch = null;
    }
  }

  public int initializeSSH() {
    if (!use_ssh)
      return OK;
    if (SwissArmy.portInUse(ssh_lport))
      return OK;

    
    try {
      jsch = new JSch();
      String sep = System.getProperty("file.separator");
      String sshDir = VersionInfo.getUserHomeDirectory() + sep + ".ssh" + sep;
      jsch.setKnownHosts(sshDir + "known_hosts");

      addIdentity(jsch, new File(sshDir + "id_dsa"), ssh_pass);
      addIdentity(jsch, new File(sshDir + "id_rsa"), ssh_pass);
      session = jsch.getSession(ssh_user, ssh_host, ssh_port);
      session.setPassword(ssh_pass);
      session.setUserInfo(new SSHUserInfo());
      session.connect();

      session.setPortForwardingL(ssh_lport, ssh_rhost, ssh_rport);
      
    } catch (Exception e) {
      return autoReconnecting ? RETRY : errorHandling(SResources.getString("core.sshFailedTitle"), SResources
          .getString("core.sshFailedText")
          + "\n\n" + e);
    }
    return OK;
  }

  private void addIdentity(JSch jsch, File file, String pass) throws JSchException {
    if (file.exists()) {
      if (pass != null && !pass.equals("")) {
        jsch.addIdentity(file.getAbsolutePath(), pass);
      } else {
        jsch.addIdentity(file.getAbsolutePath());
      }
    }
  }

  public void readPreferences(int i, boolean overwrite) {
    super.readPreferences(i, overwrite);

    use_ssh = !use_ssh || overwrite ? PreferenceLoader.loadBoolean("hm_" + i + "_use_ssh") : use_ssh;

    ssh_user = ssh_user == null || overwrite
        ? PreferenceLoader.loadString("hm_" + i + "_ssh_user")
        : ssh_user;

    ssh_host = ssh_host == null || overwrite
        ? PreferenceLoader.loadString("hm_" + i + "_ssh_host")
        : ssh_host;

    ssh_pass = ssh_pass == null || overwrite
        ? PreferenceLoader.loadString("hm_" + i + "_ssh_pass")
        : ssh_pass;

    ssh_port = ssh_port == -1 || overwrite ? PreferenceLoader.loadInt("hm_" + i + "_ssh_port") : ssh_port;

    ssh_rhost = ssh_rhost == null || overwrite
        ? PreferenceLoader.loadString("hm_" + i + "_ssh_rhost")
        : ssh_rhost;

    ssh_rport = ssh_rport == -1 || overwrite ? PreferenceLoader.loadInt("hm_" + i + "_ssh_rport") : ssh_rport;

    ssh_lport = ssh_lport == -1 || overwrite ? PreferenceLoader.loadInt("hm_" + i + "_ssh_lport") : ssh_lport;
  }

  public void setDisconnected() {
    disconnectSSH();
    super.setDisconnected();
  }

  public int startCore() {

    int rc = initializeSSH();

    if (rc == RETRY || rc == CLOSE)
      return rc;

    return super.startCore();

  }

  public class SSHUserInfo implements UserInfo {
    boolean bResult;
    String passwd;

    public String getPassphrase() {
      System.err.println("getPassphrase");
      return null;
    }

    public String getPassword() {
      return passwd;
    }

    public boolean promptPassphrase(String message) {
      System.err.println("prompPassphrase");
      return true;
    }

    public boolean promptPassword(final String message) {
      display.syncExec(new Runnable() {
        public void run() {
          Splash.setVisible(false);
          InputDialog i = new InputDialog((Shell) null, VersionInfo.getName() + "/SSH", message,
              SResources.S_ES, (IInputValidator) null) {
            protected Control createDialogArea(Composite parent) {
              Control c = super.createDialogArea(parent);
              getText().setEchoChar('*');
              return c;
            }

            protected void configureShell(Shell newShell) {
              super.configureShell(newShell);
              newShell.setImage(SResources.getImage("ProgramIcon"));
            }
          };
          if (i.open() == InputDialog.OK) {
            passwd = i.getValue();
            bResult = true;
          } else {
            bResult = false;
          }
          Splash.setVisible(true);
        }
      });
      return bResult;
    }

    public boolean promptYesNo(String str) {
      return createYesNoBox(VersionInfo.getName() + "/SSH", str);
    }

    public void showMessage(String message) {
      openInformation(null, VersionInfo.getName() + "/SSH", message);
    }
  }
}
