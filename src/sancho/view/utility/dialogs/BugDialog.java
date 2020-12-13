/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import sancho.core.ICore;
import sancho.core.Sancho;
import sancho.utility.VersionInfo;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.utility.WebLauncher;
import sancho.view.utility.WidgetFactory;

public class BugDialog extends Dialog {
  String string;

  public BugDialog(Shell shell, String string) {
    super(shell);
    this.string = string;
    setShellStyle(getShellStyle() | SWT.RESIZE);
  }

  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("Boog Ditekted!");
  }

  protected int boolean2Int(boolean b) {
    return b ? 1 : 0;
  }

  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);

    String totals = SResources.S_ES;
    if (Sancho.getCore() != null) {
      ICore core = Sancho.getCore();
      totals = core.getProtocol() + "|" + core.getFileCollection().size() + "|"
          + core.getClientCollection().size() + "|" + core.getUserCollection().size() + "|"
          + core.getRoomCollection().size() + "|" + core.getResultCollection().size() + "|"
          + core.getResultCollection().getNumResults();
    } else {
      totals = "No Core";
    }

    totals += "\nCF:" + Sancho.getCoreFactory().getNumRetries() + ":" + Sancho.getCoreFactory().getUptime()
        + ":" + Sancho.getCoreFactory().getConnectedString();

    String mem = Runtime.getRuntime().freeMemory() + "/" + Runtime.getRuntime().totalMemory();

    String other = "CG:" + boolean2Int(PreferenceLoader.loadBoolean("displayChunkGraphs")) + "|CE:"
        + boolean2Int(!PreferenceLoader.loadString("coreExecutable").equals(SResources.S_ES)) + "|KC:"
        + boolean2Int(PreferenceLoader.loadBoolean("killCoreOnExit")) + "|";

    Text textInfo = new Text(composite, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    textInfo.setFont(PreferenceLoader.loadFont("consoleFontData"));
    
    GridData gd = new GridData(GridData.FILL_BOTH);
    gd.heightHint = 300;
    textInfo.setLayoutData(gd);
    textInfo
        .setText("Please submit a bug report detailing *exactly* what you were doing when this happened!!!\n"
            + "(Describe exactly how to _reproduce_ the bug if you can.  Thanks!)\n\n"
            + "---<snip>--- PLEASE INCLUDE ALL OF THE FOLLOWING ---\n\n"
            + "1. Are you using the latest version?\n\n"
            + "2. Can you duplicate the error using the java binary version?\n"
            + "   (this will report better stackTraces and help locate the bug)\n\n"
            + "3. Does the bug depend on a certain configuration setting?  Does renaming your\n"
            + "   ConfigFile and starting with a new one fix the problem? Can you include the\n"
            + "   ConfigFile that causes the problem with the bug report?\n\n" + "ConfigFile: "
            + PreferenceLoader.getPrefFile() + "\n" + System.getProperty("os.name") + " "
            + System.getProperty("java.vm.specification.vendor") + System.getProperty("java.version") + "\n"
            + "swt-" + VersionInfo.getSWTPlatform() + "-" + SWT.getVersion() + " " + Sancho.getUptime()
            + "\n" + VersionInfo.getName() + " " + VersionInfo.getVersion() + "\n" + mem + "\n" + totals
            + "\n" + other + "\n" + string);

    return composite;
  }

  protected Control createButtonBar(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(WidgetFactory.createGridLayout(2, 5, 5, 5, 5, false));
    composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    Button launch = new Button(composite, SWT.NONE);
    launch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    launch.setText(SResources.getString("b.reportBug"));
    launch.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        WebLauncher.openLink(VersionInfo.getBugPage());
      }
    });

    Button close = new Button(composite, SWT.NONE);
    close.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    close.setText(SResources.getString("b.close"));
    close.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        close();
      }
    });

    return composite;
  }

}