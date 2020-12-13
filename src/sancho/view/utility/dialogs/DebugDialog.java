/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility.dialogs;

import java.util.Arrays;
import java.util.Date;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import gnu.trove.map.hash.TLongIntHashMap;
import sancho.core.ICore;
import sancho.core.Sancho;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;

public class DebugDialog extends Dialog {
  public DebugDialog(Shell shell) {
    super(shell);
    setShellStyle(getShellStyle() | SWT.RESIZE);
    Runtime.getRuntime().runFinalization();
    Runtime.getRuntime().gc();
  }

  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(SResources.getString("menu.tools.debug"));
    newShell.setImage(SResources.getImage("ProgramIcon"));
  }

  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);

    Text textInfo = new Text(composite, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    textInfo.setFont(PreferenceLoader.loadFont("consoleFontData"));
    GridData gd = new GridData(GridData.FILL_BOTH);
    gd.heightHint = 200;
    textInfo.setLayoutData(gd);
    StringBuffer sb = new StringBuffer();
    String nl = "\n";
    sb.append(System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") + nl);
    sb.append("Config: " + PreferenceLoader.getPrefFile() + nl);
    sb.append("Connect: " + Sancho.getCoreFactory().getUptime() + " "
        + Sancho.getCoreFactory().getConnectedString() + nl);

    if (Sancho.hasCollectionFactory()) {
      ICore core = Sancho.getCore();

      sb.append("Core Protocol: " + core.getProtocol() + nl);
      sb.append("Network Collection: " + core.getNetworkCollection().size() + nl);
      sb.append("File Collection: " + core.getFileCollection().size() + nl);
      sb.append("SharedFile Collection: " + core.getSharedFileCollection().size() + nl);
      sb.append("Client Collection: " + core.getClientCollection().size() + nl);
      sb.append("Server Collection: " + core.getServerCollection().size() + nl);
      sb.append("User Collection: " + core.getUserCollection().size() + nl);
      sb.append("Room Collection: " + core.getRoomCollection().size() + nl);
      sb.append("Result Collection: " + core.getResultCollection().size() + "/" + core.getResultCollection().getNumResults() + nl);
      sb.append("Uploaders Collection: " + core.getClientCollection().getUploadersWeakMap().size() + nl);
      sb.append("Pending Collection: " + core.getClientCollection().getPendingWeakMap().size() + nl);
      sb.append("Friends Collection: " + core.getClientCollection().getFriendsWeakMap().size() + nl);

      appendCleanMap(sb, core.getClientCollection().getHistoryMap(), "Clean clients: ", nl);
      appendCleanMap(sb, core.getServerCollection().getHistoryMap(), "Clean servers: ", nl);
      
      sb.append(core.getLastMessage());
    }

    textInfo.setText(sb.toString());

    return composite;
  }

  public void appendCleanMap(StringBuffer sb, TLongIntHashMap tMap, String string, String nl) {
    long[] keys = tMap.keys();
    Arrays.sort(keys);

    for (int i = 0; i < keys.length; i++)
      sb.append(string + tMap.get(keys[i]) + " @ " + new Date(keys[i]) + nl);
  }

}