/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.server;

import gnu.regexp.RE;
import gnu.regexp.REException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import sancho.core.Sancho;
import sancho.model.mldonkey.Network;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.SResources;
import sancho.view.utility.Spinner;
import sancho.view.utility.WidgetFactory;
import sancho.view.viewFrame.TabbedSashViewFrame;

public class ServerViewFrame extends TabbedSashViewFrame {
  public ServerViewFrame(SashForm sashForm, String prefString, String prefImageString, AbstractTab aTab) {
    super(sashForm, prefString, prefImageString, aTab, "server");

    gView = new ServerTableView(this);
    createViewListener(new ServerViewListener(this));
    createViewToolBar();

    switchToTab(cTabFolder.getItems()[0]);
  }

  public void createViewToolBar() {
    super.createViewToolBar();

    addToolItem("ti.s.cleanOld", "minus", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        if (Sancho.hasCollectionFactory())
          getCore().getServerCollection().cleanOldServers();
      }
    });

    addToolItem("ti.s.addServer", "plus", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        if (!Sancho.hasCollectionFactory())
          return;
        AddServerByIPDialog dialog = new AddServerByIPDialog(gView.getShell());

        if (dialog.open() == AddServerByIPDialog.OK) {

          String name = dialog.getName();
          short port = (short) dialog.getPort(); //

          InetAddress inetAddress = null;
          try {
            inetAddress = InetAddress.getByName(name);
          } catch (UnknownHostException e) {
            MessageBox box = new MessageBox(gView.getShell(), SWT.ICON_WARNING | SWT.OK);
            box.setText(SResources.getString("l.lookupError"));
            box.setMessage(SResources.getString("l.resolveError"));
            box.open();
          }

          if (Sancho.hasCollectionFactory() && inetAddress != null)
            gView.getCore().getServerCollection().addServer(dialog.getNetwork(), inetAddress, port);
        }
      }
    });

    addToolItem("ti.s.addServerMet", "plus-globe", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        InputDialog dialog = new InputDialog(gView.getShell(), SResources.getString("ti.s.addServerMet"),
            SResources.getString("t.srv.linkToMet"), SResources.getString("t.srv.linkToMetDefault"),
            new HTTPValidator());
        dialog.open();

        String result = dialog.getValue();

        if (result != null && Sancho.hasCollectionFactory())
          getCore().getServerCollection().addServerList(result);
      }
    });

    addToolSeparator();
    addRefine();
  }

  static class HTTPValidator implements IInputValidator {
    static RE regex;
    static {
      try {
        regex = new RE("http(s)?://\\S*");
      } catch (REException e) {
      }
    }

    public String isValid(String newText) {
      return regex.isMatch(newText) ? null : SResources.getString("l.invalidInput");
    }
  }

  private class AddServerByIPDialog extends Dialog {
    private Combo combo;
    private Network network;
    private int port;
    private Spinner spinner;
    private String name;
    private Text nameText;

    public AddServerByIPDialog(Shell parentShell) {
      super(parentShell);
    }

    protected void configureShell(Shell newShell) {
      super.configureShell(newShell);
      newShell.setImage(SResources.getImage("ProgramIcon"));
      newShell.setText(SResources.getString("ti.s.addServer"));
    }

    public void createLabel(Composite composite, String resString) {
      new Label(composite, SWT.NONE).setText(SResources.getString(resString));
    }

    protected Control createDialogArea(Composite parent) {
      Composite composite = (Composite) super.createDialogArea(parent);
      composite.setLayout(WidgetFactory.createGridLayout(4, 5, 5, 10, 5, false));

      createLabel(composite, "hm.host");

      nameText = new Text(composite, SWT.BORDER);
      nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      createLabel(composite, "hm.port");

      spinner = new Spinner(composite, SWT.NONE);
      spinner.setMaximum(65535);
      spinner.setSelection(4661);

      createLabel(composite, "s.network");

      combo = new Combo(composite, SWT.READ_ONLY);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.horizontalSpan = 3;
      combo.setLayoutData(gd);

      Network[] networks = gView.getCore().getNetworkCollection().getNetworks();

      for (int i = 0; i < networks.length; i++) {
        Network network = networks[i];
        if (network.isEnabled() && network.hasServers()) {
          combo.add(network.getName());
          combo.setData(network.getName(), network);
        }
      }
      combo.select(0);
      return composite;
    }

    protected void buttonPressed(int buttonId) {
      this.network = (Network) combo.getData(combo.getItem(combo.getSelectionIndex()));
      this.port = spinner.getSelection();
      this.name = nameText.getText();
      super.buttonPressed(buttonId);
    }

    public String getName() {
      return name;
    }

    public int getPort() {
      return port;
    }

    public Network getNetwork() {
      return this.network;
    }
  }

}