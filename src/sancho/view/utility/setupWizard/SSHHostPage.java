/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility.setupWizard;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.utility.Spinner;
import sancho.view.utility.WidgetFactory;

public class SSHHostPage extends HostPage {
  Text ssh_host;
  Spinner ssh_lport;
  Text ssh_pass;
  Spinner ssh_port;
  Text ssh_rhost;
  Spinner ssh_rport;
  Text ssh_user;
  Button use_ssh;
  Composite sshComposite;

  protected void createMyControl(Composite mainComposite) {

    super.createMyControl(mainComposite);

    Group sshGroup = new Group(mainComposite, SWT.SHADOW_ETCHED_IN);
    sshGroup.setLayout(WidgetFactory.createGridLayout(1, 0, 0, 0, 0, false));
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 2;
    sshGroup.setLayoutData(gd);

    // --
    Composite toggleComposite = new Composite(sshGroup, SWT.NONE);
    toggleComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    toggleComposite.setLayout(WidgetFactory.createGridLayout(2, 5, 5, 5, 5, false));

    use_ssh = new Button(toggleComposite, SWT.CHECK);
    use_ssh.setText(SResources.getString("hm.use_ssh"));
    use_ssh.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        toggleSSHEnabled(use_ssh.getSelection());
      }
    });

    // --
    sshComposite = new Composite(sshGroup, SWT.NONE);
    sshComposite.setLayout(WidgetFactory.createGridLayout(6, 5, 5, 5, 5, false));
    sshComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    ssh_host = createText(sshComposite, SResources.getString("hm.ssh_host"), SResources.S_ES);
    ssh_rhost = createText(sshComposite, SResources.getString("hm.ssh_rhost"), SResources.S_ES);

    ssh_user = createText(sshComposite, SResources.getString("hm.ssh_user"), SResources.S_ES);
    ssh_rport = createPort(sshComposite, SResources.getString("hm.ssh_rport"), SResources.S_ES);

    ssh_pass = createText(sshComposite, SResources.getString("hm.ssh_pass"), SResources.S_ES);
    ssh_pass.setEchoChar('*');
    ssh_lport = createPort(sshComposite, SResources.getString("hm.ssh_lport"), SResources.S_ES);

    ssh_port = createPort(sshComposite, SResources.getString("hm.ssh_port"), SResources.S_ES);

  }

  public void loadHost(HostObject h, int i) {
    super.loadHost(h, i);
    h.ssh_host = PreferenceLoader.loadString("hm_" + i + "_ssh_host");
    h.ssh_user = PreferenceLoader.loadString("hm_" + i + "_ssh_user");
    h.ssh_pass = PreferenceLoader.loadString("hm_" + i + "_ssh_pass");
    h.ssh_port = PreferenceLoader.loadInt("hm_" + i + "_ssh_port");

    h.ssh_rport = PreferenceLoader.loadInt("hm_" + i + "_ssh_rport");
    h.ssh_lport = PreferenceLoader.loadInt("hm_" + i + "_ssh_lport");
    h.ssh_rhost = PreferenceLoader.loadString("hm_" + i + "_ssh_rhost");

    h.use_ssh = PreferenceLoader.loadBoolean("hm_" + i + "_use_ssh");

  }

  public void resetInfo(HostObject h) {
    super.resetInfo(h);

    ssh_host.setText(h.ssh_host);
    ssh_user.setText(h.ssh_user);
    ssh_pass.setText(h.ssh_pass);
    ssh_port.setSelection(h.ssh_port);

    ssh_rhost.setText(h.ssh_rhost);
    ssh_rport.setSelection(h.ssh_rport);
    ssh_lport.setSelection(h.ssh_lport);

    use_ssh.setSelection(h.use_ssh);
    toggleSSHEnabled(h.use_ssh);
  }

  public void saveCurrent(HostObject h) {
    super.saveCurrent(h);
    h.ssh_port = ssh_port.getSelection();
    h.ssh_user = ssh_user.getText();
    h.ssh_host = ssh_host.getText();
    h.ssh_pass = ssh_pass.getText();

    h.ssh_rhost = ssh_rhost.getText();
    h.ssh_rport = ssh_rport.getSelection();
    h.ssh_lport = ssh_lport.getSelection();

    h.use_ssh = use_ssh.getSelection();
  }

  public void setToDefault(PreferenceStore p, int i) {
    super.setToDefault(p, i);

    p.setToDefault("hm_" + i + "_ssh_host");
    p.setToDefault("hm_" + i + "_ssh_port");
    p.setToDefault("hm_" + i + "_ssh_user");
    p.setToDefault("hm_" + i + "_ssh_pass");

    p.setToDefault("hm_" + i + "_ssh_rhost");
    p.setToDefault("hm_" + i + "_ssh_rportt");
    p.setToDefault("hm_" + i + "_ssh_lport");

    p.setToDefault("hm_" + i + "_use_ssh");
  }

  public void setValue(PreferenceStore p, int i, HostObject h) {
    super.setValue(p, i, h);
    p.setValue("hm_" + i + "_ssh_host", h.ssh_host);
    p.setValue("hm_" + i + "_ssh_user", h.ssh_user);
    p.setValue("hm_" + i + "_ssh_pass", h.ssh_pass);
    p.setValue("hm_" + i + "_ssh_port", h.ssh_port);

    p.setValue("hm_" + i + "_ssh_rhost", h.ssh_rhost);
    p.setValue("hm_" + i + "_ssh_rport", h.ssh_rport);
    p.setValue("hm_" + i + "_ssh_lport", h.ssh_lport);

    p.setValue("hm_" + i + "_use_ssh", h.use_ssh);
  }

  public void toggleSSHEnabled(boolean b) {

    ssh_host.setEnabled(b);
    ssh_user.setEnabled(b);
    ssh_pass.setEnabled(b);
    ssh_port.setEnabled(b);

    ssh_rhost.setEnabled(b);
    ssh_rport.setEnabled(b);
    ssh_lport.setEnabled(b);

  }
}
