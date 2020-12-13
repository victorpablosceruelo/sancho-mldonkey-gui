/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.mainWindow;

import java.io.File;
import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import sancho.core.Sancho;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.utility.SwissArmy;
import sancho.utility.VersionInfo;
import sancho.view.MainWindow;
import sancho.view.downloadComplete.DownloadCompleteShell;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.SResources;
import sancho.view.utility.WebLauncher;
import sancho.view.utility.dialogs.AboutDialog;
import sancho.view.utility.dialogs.DebugDialog;
import sancho.view.utility.setupWizard.SetupWizard;
import sancho.view.utility.setupWizard.SetupWizardDialog;

public class MenuBar {
  private MainWindow mainWindow;
  private Shell shell;
  private Menu mainMenuBar;
  private Menu subMenu;
  private MenuItem menuItem;

  public MenuBar(MainWindow mainWindow) {
    this.mainWindow = mainWindow;
    this.shell = mainWindow.getShell();
    this.createContent();
  }

  private void createContent() {
    mainMenuBar = new Menu(shell, SWT.BAR);
    shell.setMenuBar(mainMenuBar);

    // File
    menuItem = new MenuItem(mainMenuBar, SWT.CASCADE);
    menuItem.setText("&" + SResources.getString("menu.file"));

    final Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
    menuItem.setMenu(fileMenu);

    // Build the fileMenu dynamically
    fileMenu.addMenuListener(new MenuAdapter() {
      public void menuShown(MenuEvent e) {
        MenuItem[] menuItems = fileMenu.getItems();

        for (int i = 0; i < menuItems.length; i++)
          menuItems[i].dispose();

        if (Sancho.getCoreFactory().isAutoReconnecting())
          createMenuItem(fileMenu, "&" + SResources.getString("menu.file.stopAutoReconnect"), "nuke",
              new Listener() {
                public void handleEvent(Event e) {
                  Sancho.getCoreFactory().setAutoReconnecting(false);
                }
              });

        if (Sancho.getCoreFactory().isConnected())
          createMenuItem(fileMenu, "&" + SResources.getString("menu.file.inputLink"),
              "tab.transfers.buttonSmall", new Listener() {
                public void handleEvent(Event e) {
                  InputDialog dialog = new InputDialog(mainWindow.getShell(), SResources
                      .getString("menu.file.inputLink"), SResources.getString("menu.file.inputLink"), SResources.S_ES, null);
                  dialog.open();

                  String result = dialog.getValue();

                  if (result != null && !result.equals(SResources.S_ES) && Sancho.getCore() != null)
                    SwissArmy.sendLink(Sancho.getCore(), result);
                }
              });

        // File>Kill core if connected && gui has not spawned the core
        if (Sancho.getCoreFactory().isConnected() && (Sancho.getCoreConsole() == null))
          createMenuItem(fileMenu, "&" + SResources.getString("menu.file.killCore"), "nuke", new Listener() {
            public void handleEvent(Event e) {
              MessageBox confirm = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION);

              confirm.setMessage(SResources.getString("mi.areYouSure"));

              if (confirm.open() == SWT.YES && mainWindow.getCore() != null) {
                Sancho.send(OpCodes.S_KILL_CORE);
              }
            }
          });

        if (Sancho.hasCollectionFactory()) {

          createMenuItem(fileMenu, "&" + SResources.getString("menu.file.disconnect"), "menu-disconnect",
              new Listener() {
                public void handleEvent(Event e) {
                  if (Sancho.getCore() != null)
                    Sancho.getCoreFactory().disconnect();
                }
              });

        } else {
          menuItem = new MenuItem(fileMenu, SWT.CASCADE);
          menuItem.setText(SResources.getString("menu.file.connect"));

          final Menu hostMenu = new Menu(menuItem);
          menuItem.setMenu(hostMenu);

          createMenuItem(hostMenu, "&" + SResources.getString("menu.file.reconnect"), "menu-connect",
              new Listener() {
                public void handleEvent(Event e) {
                  Sancho.getCoreFactory().reconnect();
                }
              });

          menuItem = new MenuItem(hostMenu, SWT.SEPARATOR);

          for (int i = 0;; i++) {
            if ((i > 0) && !PreferenceLoader.contains("hm_" + i + "_hostname"))
              break;

            String d = ((i == 0) ? SResources.getString("l.default") + ": " : SResources.S_ES);
            String d1 = PreferenceLoader.loadString("hm_" + i + "_description");

            if (d1.equals(SResources.S_ES))
              d1 = PreferenceLoader.loadString("hm_" + i + "_hostname") + ":"
                  + PreferenceLoader.loadString("hm_" + i + "_port");

            d += d1;

            final int j = i;

            createMenuItem(hostMenu, d, "menu-connect", new Listener() {
              public void handleEvent(Event e) {
                Sancho.getCoreFactory().reconnect(j);
              }
            });
          }
        }
        // File>Separator
        menuItem = new MenuItem(fileMenu, SWT.SEPARATOR);

        // File>Exit
        String xString = SResources.getString("menu.file.exit");

        if (PreferenceLoader.loadBoolean("killCoreOnExit") && Sancho.getCoreFactory().isConnected())
          xString = SResources.getString("menu.file.exitAndKill");

        createMenuItem(fileMenu, xString, "x", new Listener() {
          public void handleEvent(Event e) {
            mainWindow.getMinimizer().forceClose();
            shell.close();
          }
        });
      }
    });

    // File
    menuItem = new MenuItem(mainMenuBar, SWT.CASCADE);
    menuItem.setText("&" + SResources.getString("menu.view"));

    final Menu viewMenu = new Menu(shell, SWT.DROP_DOWN);
    menuItem.setMenu(viewMenu);

    // Build the viewMenu dynamically
    viewMenu.addMenuListener(new MenuAdapter() {
      public void menuShown(MenuEvent e) {
        MenuItem[] menuItems = viewMenu.getItems();

        for (int i = 0; i < menuItems.length; i++)
          menuItems[i].dispose();

        final List mainTabs = mainWindow.getTabs();

        for (int i = 0; i < mainTabs.size(); i++) {
          final AbstractTab aTab = (AbstractTab) mainTabs.get(i);
          menuItem = new MenuItem(viewMenu, SWT.PUSH);
          menuItem.setText(aTab.getToolButton().getText());
          menuItem.setImage(aTab.getToolButton().getSmallActiveImage());
          menuItem.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
              aTab.setActive();
            }
          });
        }

        // Separator
        menuItem = new MenuItem(viewMenu, SWT.SEPARATOR);

        createMenuItem(viewMenu, "&" + SResources.getString("menu.view.tabSelector"), "preferences",
            new Listener() {
              public void handleEvent(Event event) {
                mainWindow.configureTabs();
              }
            });
      }
    });

    // Tools
    menuItem = new MenuItem(mainMenuBar, SWT.CASCADE);
    menuItem.setText("&" + SResources.getString("menu.tools"));

    subMenu = new Menu(shell, SWT.DROP_DOWN);
    menuItem.setMenu(subMenu);

    // Tools

    createMenuItem(subMenu, "&" + SResources.getString("menu.tools.downloadHistory"),
        "tab.transfers.buttonSmall", new Listener() {
          public void handleEvent(Event e) {
            File f = new File(VersionInfo.getDownloadLogFile());
            if (!f.exists()) {
              MessageBox confirm = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
              confirm.setMessage(SResources.getString("l.noCompleteDownloads") + "\n"
                  + VersionInfo.getDownloadLogFile());
              confirm.open();
            } else {
              new DownloadCompleteShell(mainWindow.getShell()).open();
            }
          }
        });
  

    createMenuItem(subMenu, "&" + SResources.getString("menu.tools.debug"), "info", new Listener() {
      public void handleEvent(Event event) {
        new DebugDialog(shell).open();
      }
    });

    menuItem = new MenuItem(subMenu, SWT.SEPARATOR);

    createMenuItem(subMenu, "&" + SResources.getString("menu.tools.hostManager"), "cabinet", new Listener() {
      public void handleEvent(Event e) {
        SetupWizardDialog dialog = new SetupWizardDialog(mainWindow.getShell(), new SetupWizard());
        dialog.create();
        dialog.open();
      }
    });

    createMenuItem(subMenu, "&" + SResources.getString("menu.tools.preferences"), "preferences",
        new Listener() {
          public void handleEvent(Event event) {
            mainWindow.openPreferences();
          }
        });

    // Help
    menuItem = new MenuItem(mainMenuBar, SWT.CASCADE);
    menuItem.setText("&" + SResources.getString("menu.help"));
    subMenu = new Menu(shell, SWT.DROP_DOWN);
    menuItem.setMenu(subMenu);

    createMenuItem(subMenu, "&" + SResources.getString("menu.help.homepage"), "globe", new URLListener(
        VersionInfo.getHomePage()));

    createMenuItem(subMenu, "&" + SResources.getString("menu.help.faq"), "globe", new URLListener(VersionInfo
        .getFAQPage()));

    createMenuItem(subMenu, "&" + SResources.getString("menu.help.bugs"), "globe", new URLListener(VersionInfo
        .getBugPage()));

    createMenuItem(subMenu, SResources.getString("menu.help.features"), "globe", new URLListener(VersionInfo
        .getFeaturePage()));

    // Help>Separator
    menuItem = new MenuItem(subMenu, SWT.SEPARATOR);

    createMenuItem(subMenu, "&" + SResources.getString("menu.help.checkVersion"), "ProgramIcon",
        new Listener() {
          public void handleEvent(Event event) {
            // Sancho.getCore().send(OpCodes.S_CONSOLE_MESSAGE, "block_list");
          }
        });

    // Help>About
    createMenuItem(subMenu, "&" + SResources.getString("menu.help.about"), "commit_question", new Listener() {
      public void handleEvent(Event event) {
        new AboutDialog(shell).open();
      }
    });

  }

  public void createMenuItem(Menu menu, String resText, String resImage, Listener listener) {
    menuItem = new MenuItem(menu, SWT.PUSH);
    menuItem.setText(resText);
    menuItem.setImage(SResources.getImage(resImage));
    menuItem.addListener(SWT.Selection, listener);
  }

  static class URLListener implements Listener {
    private String url;

    public URLListener(String url) {
      this.url = url;
    }

    public void handleEvent(Event event) {
      WebLauncher.openLink(url);
    }
  }
}