/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import sancho.model.mldonkey.Client;
import sancho.model.mldonkey.utility.ClientMessage;
import sancho.view.console.MessageConsole;
import sancho.view.friends.FriendsTableView;
import sancho.view.friends.FriendsViewFrame;
import sancho.view.friends.clientDirectories.ClientDirectoriesTableView;
import sancho.view.friends.clientDirectories.ClientDirectoriesViewFrame;
import sancho.view.friends.clientFiles.ClientFilesTableView;
import sancho.view.friends.clientFiles.ClientFilesViewFrame;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;
import sancho.view.viewFrame.SashViewFrame;
import sancho.view.viewFrame.SashViewListener;

public class FriendsTab extends AbstractTab implements Observer {
  private CTabFolder cTabFolder;
  private Hashtable openTabs = new Hashtable();
  private MessagesViewFrame messagesViewFrame;
  private FriendsViewFrame friendsViewFrame;

  public FriendsTab(MainWindow mainWindow, String prefString) {
    super(mainWindow, prefString);
  }

  protected void createContents(Composite parent) {
    String sashPrefString = "messagesSash";
    SashForm sashForm = WidgetFactory.createSashForm(parent, sashPrefString);
    this.createLeftSash(sashForm);
    this.createRightSash(sashForm);
    WidgetFactory.loadSashForm(sashForm, sashPrefString);
    onConnect();
  }

  public void onConnect() {
    super.onConnect();

    if (getCore() != null)
      getCore().addObserver(this);
  }

  private void createLeftSash(SashForm parent) {
    friendsViewFrame = new FriendsViewFrame(parent, "l.friends", "tab.friends.buttonSmall", this);
    addViewFrame(friendsViewFrame);

    friendsViewFrame.getGView().getTable().addMouseListener(new MouseAdapter() {
      public void mouseDoubleClick(MouseEvent e) {
        if (e.widget instanceof Table) {
          Table table = (Table) e.widget;
          TableItem[] currentItems = table.getSelection();
          for (int i = 0; i < currentItems.length; i++)
            openTab((Client) currentItems[i].getData());
        }
      }
    });
  }

  private void createFilesView(SashForm parent) {
    String sashPrefString = "directoriesFilesSash";
    SashForm sashForm = WidgetFactory.createSashForm(parent, sashPrefString);

    ClientDirectoriesViewFrame cd = new ClientDirectoriesViewFrame(sashForm, "l.clientDirectories",
        "tab.friends.buttonSmall", this);

    ((FriendsTableView) friendsViewFrame.getGView()).setDirectoryView((ClientDirectoriesTableView) cd
        .getGView());

    ClientFilesViewFrame cf = new ClientFilesViewFrame(sashForm, "l.clientFiles", "tab.friends.buttonSmall",
        this);

    ((ClientDirectoriesTableView) cd.getGView()).setFilesView((ClientFilesTableView) cf.getGView());
    WidgetFactory.loadSashForm(sashForm, sashPrefString);

    addViewFrame(cd);
    addViewFrame(cf);
  }

  private void createRightSash(SashForm parent) {
    String sashPrefString = "filesMessagesSash";
    SashForm sashForm = WidgetFactory.createSashForm(parent, sashPrefString);
    this.createFilesView(sashForm);
    this.createMessagesView(sashForm);
    WidgetFactory.loadSashForm(sashForm, sashPrefString);
  }

  private void createMessagesView(SashForm parent) {
    messagesViewFrame = new MessagesViewFrame(parent, "l.messageTabs", "tab.friends.buttonSmall", this);
    addViewFrame(messagesViewFrame);

    int style = PreferenceLoader.loadBoolean("messagesCTabFolderTabsOnTop") ? SWT.TOP : SWT.BOTTOM;
    cTabFolder = WidgetFactory.createCTabFolder(messagesViewFrame.getChildComposite(), SWT.FLAT | style);
    WidgetFactory.addCTabFolderMenu(cTabFolder, "messagesCTabFolder");

    cTabFolder.setBorderVisible(false);
    cTabFolder.setLayoutData(new FillLayout());

    cTabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
      public void close(CTabFolderEvent event) {
        CTabItem item = (CTabItem) event.item;

        MessageConsole messageConsole = (MessageConsole) item.getData("messageConsole");
        Integer id = (Integer) item.getData("id");
        openTabs.remove(id);
        messageConsole.dispose();
        item.dispose();
        setTabsLabel();
      }
    });

    cTabFolder.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        CTabItem cTabItem = (CTabItem) e.item;
        MessageConsole messageConsole = (MessageConsole) cTabItem.getData("messageConsole");
        setTabsLabel();
        messageConsole.setFocus();
      }
    });
  }

  public void closeAllTabs() {
    Iterator iterator = openTabs.keySet().iterator();

    while (iterator.hasNext()) {
      Integer id = (Integer) iterator.next();
      CTabItem cTabItem = (CTabItem) openTabs.get(id);
      MessageConsole messageConsole = (MessageConsole) cTabItem.getData("messageConsole");
      if (messageConsole != null)
        messageConsole.dispose();
      if (cTabItem != null)
        cTabItem.dispose();
    }

    openTabs.clear();
    setTabsLabel();
  }

  public void update(final Observable arg0, final Object arg1) {
    if (arg1 instanceof ClientMessage) {
      if (!cTabFolder.isDisposed())
        cTabFolder.getDisplay().asyncExec(new Runnable() {
          public void run() {
            messageFromClient((ClientMessage) arg1);
          }
        });
    }
  }

  public void setTabsLabel() {
    String extra = SResources.S_ES;

    if (cTabFolder.getSelection() != null)
      extra = " -> " + cTabFolder.getSelection().getText();

    messagesViewFrame.updateCLabelText(SResources.getString("l.messageTabs") + ": " + openTabs.size() + extra);
  }

  public void sendTabMessage(int id, String textMessage) {
    CTabItem cTabItem = (CTabItem) openTabs.get(new Integer(id));
    MessageConsole messageConsole = (MessageConsole) cTabItem.getData("messageConsole");
    messageConsole.append(textMessage + messageConsole.getLineDelimiter());
  }

  public void messageFromClient(ClientMessage message) {
    if (cTabFolder == null || cTabFolder.isDisposed())
      return;

    getMainWindow().getStatusline().setText("New message!");
    getMainWindow().getStatusline().setImage(SResources.getImage("new-message"));

    if (openTabs.containsKey(new Integer(message.getId()))) {
      String textMessage;
      Client client = (Client) getCore().getClientCollection().get(message.getId());

      if (client == null)
        textMessage = getTimeStamp() + message.getId() + ": <unknown>> " + message.getText();
      else
        textMessage = getTimeStamp() + message.getId() + ": " + client.getName() + "> " + message.getText();

      sendTabMessage(message.getId(), textMessage);
    } else {
      // the core sends the client for this message's clientID AFTER
      // the message itself.. not very smart. So, neither is this curious
      // loop.
      // TODO: does this help at all? remove if not.
      Client client = null;

      for (int i = 0; (i < 3)
          && ((client = (Client) getCore().getClientCollection().get(message.getId())) == null); i++)
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

      String tabText;

      if (client == null)
        tabText = message.getId() + ": <unknown>";
      else
        tabText = client.getId() + ": " + client.getName();

      String textMessage = getTimeStamp() + tabText + "> " + message.getText();
      CTabItem cTabItem = addCTabItem(message.getId(), "  " + tabText);

      if (cTabFolder.getItemCount() == 1)
        setItemFocus(cTabItem);

      sendTabMessage(message.getId(), textMessage);
      setTabsLabel();
    }
  }

  public CTabItem addCTabItem(int id, String tabText) {
    CTabItem tabItem = new CTabItem(cTabFolder, SWT.NONE);
    tabItem.setText(tabText);

    MessageConsole messageConsole = new MessageConsole(cTabFolder, SWT.WRAP, id);
    tabItem.setControl(messageConsole.getComposite());
    tabItem.setData("id", new Integer(id));
    tabItem.setData("messageConsole", messageConsole);
    openTabs.put(new Integer(id), tabItem);

    return tabItem;
  }

  public void openTab(Client client) {
    if (!openTabs.containsKey(new Integer(client.getId()))) {
      String tabText = "  " + client.getId() + ": " + client.getName();
      setItemFocus(addCTabItem(client.getId(), tabText));
    } else
      cTabFolder.setSelection((CTabItem) openTabs.get(new Integer(client.getId())));

    setTabsLabel();
  }

  public String getTimeStamp() {
    SimpleDateFormat sdFormatter = new SimpleDateFormat("[HH:mm:ss] ");
    return sdFormatter.format(new Date());
  }

  public void setItemFocus(CTabItem cTabItem) {
    cTabFolder.setSelection(cTabItem);
    MessageConsole messageConsole = (MessageConsole) cTabItem.getData("messageConsole");
    messageConsole.setFocus();
  }

  public void setActive() {
    super.setActive();
    if (getMainWindow().getStatusline() != null)
      getMainWindow().getStatusline().clear();
    if (cTabFolder.getSelection() != null)
      setItemFocus(cTabFolder.getSelection());
  }

  private class MessagesViewFrame extends SashViewFrame {
    public MessagesViewFrame(SashForm parentSashForm, String prefString, String prefImageString,
        AbstractTab aTab) {
      super(parentSashForm, prefString, prefImageString, aTab);
      createViewListener(new MessagesViewListener(this));
      createViewToolBar();
    }

    public void createViewToolBar() {
      super.createViewToolBar();
      addToolItem("ti.f.closeAllTabs", "x", new SelectionAdapter() {
        public void widgetSelected(SelectionEvent s) {
          closeAllTabs();
        }
      });
    }
  }

  static class MessagesViewListener extends SashViewListener {
    public MessagesViewListener(SashViewFrame cSashViewFrame) {
      super(cSashViewFrame);
    }

    public void menuAboutToShow(IMenuManager menuManager) {
      createSashActions(menuManager, "l.clientFiles");
    }
  }

}