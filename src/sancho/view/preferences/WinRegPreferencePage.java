/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.preferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;

import sancho.core.Sancho;
import sancho.utility.SwissArmy;
import sancho.utility.VersionInfo;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public class WinRegPreferencePage extends CPreferencePage {
  RegisterLink[] registerLinks;
  RegisterExtension[] registerExtensions;

  /**
   * @param title
   * @param style
   */
  protected WinRegPreferencePage(String title) {
    super(title);
  }

  protected Control createContents(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(WidgetFactory.createGridLayout(1, 0, 0, 0, 0, false));

    TabFolder tabFolder = new TabFolder(composite, SWT.TOP);
    tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

    createProtocolTab(tabFolder);
    createFileExtensionsTab(tabFolder);

    return composite;

  }

  protected void createFileExtensionsTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "l.fileExtensions");
    composite.setLayout(WidgetFactory.createGridLayout(1, 5, 5, 5, 5, false));

    createInformationLabel(composite, "p.registerInfo");

    registerExtensions = new RegisterExtension[1];
    registerExtensions[0] = new RegisterExtension("bittorrent (.torrent)", composite);

    Button button = new Button(composite, SWT.NONE);
    button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    button.setText(SResources.getString("b.updateRegistry"));

    button.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        if (changedExtPrefs())
          createRegFile();
      }
    });

  }

  private boolean changedExtPrefs() {
    for (int i = 0; i < registerLinks.length; i++) {
      if (registerExtensions[i].getSelection() != RegisterExtension.NO_CHANGE)
        return true;
    }
    return false;
  }

  protected void createProtocolTab(TabFolder tabFolder) {
    Composite composite = createNewTab(tabFolder, "l.protocols");
    composite.setLayout(WidgetFactory.createGridLayout(1, 5, 5, 5, 5, false));

    registerLinks = new RegisterLink[3];

    registerLinks[0] = new RegisterLink("ed2k", composite);
    registerLinks[1] = new RegisterLink("magnet", composite);
    registerLinks[2] = new RegisterLink("sig2dat", composite);

    Button button = new Button(composite, SWT.NONE);
    button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    button.setText(SResources.getString("b.updateRegistry"));

    button.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        if (changedLinkPrefs())
          createRegFile();
      }
    });
  }

  /**
   * @return true if we should update the registry
   */
  private boolean changedLinkPrefs() {
    for (int i = 0; i < registerLinks.length; i++) {
      if (registerLinks[i].getSelection() != RegisterLink.NO_CHANGE)
        return true;
    }

    return false;
  }

  /**
   * Create the registry file
   */
  private void createRegFile() {
    String currentDir;
    FileOutputStream out;
    PrintStream p;

    currentDir = System.getProperty("user.dir") + System.getProperty("file.separator");
    String gcjName = System.getProperty("gnu.gcj.progname");

    try {

      String regFile = currentDir + VersionInfo.getName() + ".reg";
      String exeFile = currentDir + VersionInfo.getName() + ".exe";

      if (gcjName != null && !gcjName.toLowerCase().endsWith("exe"))
        gcjName += ".exe";

      if (!new File(exeFile).exists() && gcjName != null)
        exeFile = gcjName;

      String cpath = System.getProperty("java.class.path");

      if (cpath != null && cpath.toLowerCase().endsWith(".exe") && new File(cpath).exists())
        exeFile = cpath;

      exeFile = SwissArmy.replaceAll(exeFile, "\\\\", "\\\\");

      out = new FileOutputStream(regFile);
      p = new PrintStream(out);

      p.println("REGEDIT4");

      for (int i = 0; i < registerLinks.length; i++) {
        switch (registerLinks[i].getSelection()) {
          case RegisterLink.REGISTER :
            registerType(p, registerLinks[i].getText(), exeFile, createExtra());
            break;
          case RegisterLink.UNREGISTER :
            unregisterType(p, registerLinks[i].getText());
            break;
          default :
            break;
        }
      }

      for (int i = 0; i < registerExtensions.length; i++) {
        switch (registerExtensions[i].getSelection()) {
          case RegisterExtension.REGISTER :
            registerTorrent(p, exeFile, createExtra());
            break;
          case RegisterExtension.UNREGISTER :
            unregisterTorrent(p);
            break;
          default :
            break;
        }
      }

      p.close();

      updateRegistry(regFile);
    } catch (Exception e) {
      Sancho.pDebug("createRegFile: " + e);
    }
  }

  /**
   * Spawn regedit passing the regfile as the parameter Requires regedit.exe to
   * be in the system path
   * 
   * @param regFile
   */
  private void updateRegistry(String regFile) {
    String[] cmd = new String[3];

    cmd[0] = "regedit.exe";
    cmd[1] = "/s";
    cmd[2] = regFile;

    Runtime rt = Runtime.getRuntime();

    try {
      rt.exec(cmd);
    } catch (Exception e) {
      Sancho.pDebug("updateRegistry: " + e);
    }
  }

  private void registerTorrent(PrintStream p, String exeFile, String extra) {
    p.println("[HKEY_CLASSES_ROOT\\.torrent]");
    p.println("@=\"bittorrent\"");

    p.println("[HKEY_CLASSES_ROOT\\bittorrent]");
    p.println("@=\"TORRENT File\"");

    p.println("[HKEY_CLASSES_ROOT\\bittorrent\\shell]");
    p.println("@=\"open\"");

    p.println("[HKEY_CLASSES_ROOT\\bittorrent\\shell\\open]");

    p.println("[HKEY_CLASSES_ROOT\\bittorrent\\shell\\open\\command]");
    //  p.println("@=\"\\\"" + exeFile + createExtra() + "\\\" \\\"-l\\\" \\\"\\\\\\\"%1\\\\\\\"\\\"\"");  //why so many?
    p.println("@=\"\\\"" + exeFile + "\\\" " + extra + "\\\"-l\\\" \\\"\\\\\\\"%1\\\\\\\"\\\"\"");
  }

  private void unregisterTorrent(PrintStream p) {
    p.println("[-HKEY_CLASSES_ROOT\\bittorrent\\shell\\open\\command]");
    p.println("[-HKEY_CLASSES_ROOT\\bittorrent\\shell\\open]");
    p.println("[-HKEY_CLASSES_ROOT\\bittorrent\\shell]");
    p.println("[-HKEY_CLASSES_ROOT\\bittorrent]");

    p.println("[-HKEY_CLASSES_ROOT\\.torrent]");
  }

  /**
   * @param p
   * @param name
   * @param exeFile
   * @param prefFile
   */
  private void registerType(PrintStream p, String name, String exeFile, String extra) {
    p.println("[HKEY_CLASSES_ROOT\\" + name + "]");
    p.println("@=\"URL: " + name + " Protocol\"");
    p.println("\"URL Protocol\"=\"\"");
    p.println("[HKEY_CLASSES_ROOT\\" + name + "\\shell]");
    p.println("[HKEY_CLASSES_ROOT\\" + name + "\\shell\\open]");
    p.println("[HKEY_CLASSES_ROOT\\" + name + "\\shell\\open\\command]");
    p.println("@=\"\\\"" + exeFile + "\\\" " + extra + "\\\"-l\\\" \\\"%1\\\"\"");
  }

  private String createExtra() {
    String s = "";

    String prefFile = PreferenceLoader.getPrefFile();
    String homeDir = PreferenceLoader.getHomeDirectory();

    if (prefFile != null)
      prefFile = SwissArmy.replaceAll(prefFile, "\\\\", "\\\\");

    if (homeDir != null) {
      if (homeDir.endsWith("\\"))
        homeDir = homeDir.substring(0, homeDir.length() - 1);

      homeDir = SwissArmy.replaceAll(homeDir, "\\\\", "\\\\");

    }

    if (PreferenceLoader.customPrefFile)
      s += "\\\"-c\\\" \\\"" + prefFile + "\\\" ";
    if (PreferenceLoader.customHomeDir)
      s += "\\\"-j\\\" \\\"" + homeDir + "\\\" ";

    return s;

  }

  /**
   * @param p
   * @param name
   */
  private void unregisterType(PrintStream p, String name) {
    p.println("[-HKEY_CLASSES_ROOT\\" + name + "\\shell\\open\\command]");
    p.println("[-HKEY_CLASSES_ROOT\\" + name + "\\shell\\open]");
    p.println("[-HKEY_CLASSES_ROOT\\" + name + "\\shell]");
    p.println("[-HKEY_CLASSES_ROOT\\" + name + "]");
  }

  static class RegisterLink {
    public static final int NO_CHANGE = 0;
    public static final int REGISTER = 1;
    public static final int UNREGISTER = 2;
    private int selection;
    private String text;

    public RegisterLink(String text, Composite parent) {
      this.text = text;
      selection = NO_CHANGE;
      createContents(parent);
    }

    /**
     * @param parent
     */
    protected void createContents(Composite parent) {
      Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
      group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      group.setLayout(WidgetFactory.createGridLayout(1, 5, 5, 5, 5, false));
      group.setText(text + "://");

      createButton(group, SResources.getString("b.noChange"), NO_CHANGE);
      createButton(group, SResources.getString("b.registerLink"), REGISTER);
      createButton(group, SResources.getString("b.unregisterLink"), UNREGISTER);
    }

    /**
     * @param group
     * @param text
     * @param select
     * @param type
     */
    private void createButton(Group group, String text, final int type) {
      Button button = new Button(group, SWT.RADIO);
      button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      button.setText(text);
      button.setSelection(type == NO_CHANGE);
      button.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
          selection = type;
        }
      });
    }

    /**
     * @return int (the selection type)
     */
    public int getSelection() {
      return selection;
    }

    /**
     * @return String
     */
    public String getText() {
      return text;
    }
  }

  static class RegisterExtension {
    public static final int NO_CHANGE = 0;
    public static final int REGISTER = 1;
    public static final int UNREGISTER = 2;
    private int selection;
    private String text;

    public RegisterExtension(String text, Composite parent) {
      this.text = text;
      selection = NO_CHANGE;
      createContents(parent);
    }

    /**
     * @param parent
     */
    protected void createContents(Composite parent) {
      Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
      group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      group.setLayout(WidgetFactory.createGridLayout(1, 5, 5, 5, 5, false));
      group.setText(text);

      createButton(group, SResources.getString("b.noChange"), NO_CHANGE);
      createButton(group, SResources.getString("b.registerLink"), REGISTER);
      createButton(group, SResources.getString("b.unregisterLink"), UNREGISTER);
    }

    /**
     * @param group
     * @param text
     * @param select
     * @param type
     */
    private void createButton(Group group, String text, final int type) {
      Button button = new Button(group, SWT.RADIO);
      button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      button.setText(text);
      button.setSelection(type == NO_CHANGE);
      button.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
          selection = type;
        }
      });
    }

    /**
     * @return int (the selection type)
     */
    public int getSelection() {
      return selection;
    }

    /**
     * @return String
     */
    public String getText() {
      return text;
    }
  }

}