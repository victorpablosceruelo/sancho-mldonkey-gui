/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view;

import gnu.trove.TIntObjectHashMap;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

import sancho.core.Sancho;
import sancho.model.mldonkey.Room;
import sancho.model.mldonkey.User;
import sancho.model.mldonkey.enums.EnumMessage;
import sancho.model.mldonkey.enums.EnumRoomState;
import sancho.model.mldonkey.utility.RoomMessage;
import sancho.view.console.RoomConsole;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.rooms.RoomsViewFrame;
import sancho.view.rooms.roomConsole.RoomConsoleViewFrame;
import sancho.view.rooms.roomUsers.RoomUsersViewFrame;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;
import sancho.view.viewFrame.SashViewFrame;
import sancho.view.viewFrame.SashViewListener;

public class RoomsTab extends AbstractTab implements Observer {
  protected CTabFolder cTabFolder;
  protected RoomCTabFolderViewFrame cTabFolderViewFrame;
  protected TIntObjectHashMap roomTabMap;

  public RoomsTab(MainWindow mainWindow, String prefString) {
    super(mainWindow, prefString);
  }

  public void closeAllClosedRooms() {
    Object[] oA = roomTabMap.getValues();
    for (int i = 0; i < oA.length; i++) {
      CTabItem cTabItem = (CTabItem) oA[i];
      Room room = (Room) cTabItem.getData("room");
      if (room.getRoomState() == EnumRoomState.CLOSED)
        closeTab(cTabItem);
    }
  }

  public void closeAllTabs() {
    Object[] oA = roomTabMap.getValues();
    for (int i = 0; i < oA.length; i++) {
      CTabItem cTabItem = (CTabItem) oA[i];
      closeTab(cTabItem);
    }
    roomTabMap.clear();
  }

  public void closeTab(CTabItem cTabItem) {
    Room room = (Room) cTabItem.getData("room");
    RoomUsersViewFrame roomUsersViewFrame = (RoomUsersViewFrame) cTabItem.getData("roomUsersViewFrame");
    SashViewFrame consoleViewFrame = (SashViewFrame) cTabItem.getData("consoleViewFrame");
    removeViewFrame(roomUsersViewFrame);
    removeViewFrame(consoleViewFrame);
    roomTabMap.remove(room.getId());
    room.close();
    ((SashForm) cTabItem.getData("roomSashForm")).dispose();
    ((RoomConsole) cTabItem.getData("roomConsole")).dispose();
    cTabItem.dispose();
    cTabFolderViewFrame.updateCLabelText(SResources.S_ES);
  }

  public void closeTab(int roomNumber) {
    if (roomTabMap.contains(roomNumber))
      closeTab((CTabItem) roomTabMap.get(roomNumber));
  }

  protected void createContents(Composite parent) {
    roomTabMap = new TIntObjectHashMap();
    String sashPrefString = "roomsSash";
    SashForm sashForm = WidgetFactory.createSashForm(parent, sashPrefString);
    addViewFrame(new RoomsViewFrame(sashForm, "t.r.availableRooms", "tab.rooms.buttonSmall", this));
    this.createRoomsCTabFolder(sashForm);
    WidgetFactory.loadSashForm(sashForm, sashPrefString);
    onConnect();
  }

  public void createRoomsCTabFolder(SashForm parent) {
    cTabFolderViewFrame = new RoomCTabFolderViewFrame(parent, "t.r.rooms", "tab.rooms.buttonSmall", this);
    addViewFrame(cTabFolderViewFrame);
    int style = PreferenceLoader.loadBoolean("roomsCTabFolderTabsOnTop") ? SWT.TOP : SWT.BOTTOM;
    cTabFolder = WidgetFactory.createCTabFolder(cTabFolderViewFrame.getChildComposite(), SWT.CLOSE | SWT.FLAT | style);
    WidgetFactory.addCTabFolderMenu(cTabFolder, "roomsCTabFolder");

    cTabFolder.setBorderVisible(false);
    cTabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
      public void close(CTabFolderEvent e) {
        closeTab((CTabItem) e.item);
      }
    });
    cTabFolder.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        setCTabFolderSelection((CTabItem) e.item);
      }
    });
  }

  public void onConnect() {
    super.onConnect();
    if (Sancho.hasCollectionFactory()) {
      getCore().getRoomCollection().addObserver(this);
      Room[] roomArray = getCore().getRoomCollection().getAllOpenRooms();
      for (int i = 0; i < roomArray.length; i++)
        openTab(roomArray[i]);
    }
  }

  public void openTab(int roomNumber) {
    Room room = getCore().getRoomCollection().getRoom(roomNumber);
    if (room != null)
      openTab(room);
  }

  public void openTab(Room room) {

    CTabItem tabItem = new CTabItem(cTabFolder, SWT.NONE);
    tabItem.setText(room.getName());
    roomTabMap.put(room.getId(), tabItem);
    String sashPrefString = "roomSash";
    SashForm sashForm = WidgetFactory.createSashForm(cTabFolder, sashPrefString);
    RoomUsersViewFrame roomUsersViewFrame = new RoomUsersViewFrame(sashForm, "t.r.roomUsers",
        "tab.friends.buttonSmall", this, room);
    roomUsersViewFrame.setActive(true);
    roomUsersViewFrame.setVisible(true);
    addViewFrame(roomUsersViewFrame);
    RoomConsoleViewFrame consoleViewFrame = new RoomConsoleViewFrame(sashForm, "t.r.roomConsole",
        "tab.console.buttonSmall", this);
    RoomConsole roomConsole = new RoomConsole(consoleViewFrame.getChildComposite(), SWT.WRAP, room.getId());
    WidgetFactory.loadSashForm(sashForm, sashPrefString);
    tabItem.setData("roomSashForm", sashForm);
    tabItem.setData("roomUsersViewFrame", roomUsersViewFrame);
    tabItem.setData("roomConsoleViewFrame", consoleViewFrame);
    tabItem.setData("roomConsole", roomConsole);
    tabItem.setData("room", room);
    tabItem.setControl(sashForm);
    if (cTabFolder.getSelection() == null) {
      cTabFolder.setSelection(tabItem);
      setCTabFolderSelection(tabItem);
    }
  }

  public void processRoom(Room room) {
    if (room.getRoomState() == EnumRoomState.OPEN && !roomTabMap.contains(room.getId()))
      openTab(room);
    else {
      if (room.getRoomState() == EnumRoomState.CLOSED && roomTabMap.contains(room.getId())
          && PreferenceLoader.loadBoolean("autoCloseRooms"))
        closeTab(room.getId());
    }
  }

  public void processRoomMessage(RoomMessage roomMessage) {
    String messageText = SResources.S_ES;
    EnumMessage enumMessage = roomMessage.getMessageType();

    if (enumMessage == EnumMessage.PRIVATE)
      messageText = "(private) ";
    if (enumMessage == EnumMessage.SERVER || enumMessage == EnumMessage.PRIVATE
        || enumMessage == EnumMessage.PUBLIC) {

      if (roomMessage.getFrom() > 0) {
        User user = (User) getCore().getUserCollection().get(roomMessage.getFrom());
        messageText += "<" + user.getName() + "> ";
      }

      messageText += roomMessage.getMessage();
      if (!roomTabMap.contains(roomMessage.getRoomNumber()) && PreferenceLoader.loadBoolean("autoOpenRooms"))
        openTab(roomMessage.getRoomNumber());
      if (roomTabMap.contains(roomMessage.getRoomNumber())) {
        CTabItem c = (CTabItem) roomTabMap.get(roomMessage.getRoomNumber());
        RoomConsole roomConsole = (RoomConsole) c.getData("roomConsole");
        roomConsole.append(messageText + roomConsole.getLineDelimiter());
      }
    }
  }

  public void runUpdate(Observable o, Object obj) {
    if (cTabFolder == null || cTabFolder.isDisposed())
      return;
    if (obj instanceof RoomMessage)
      processRoomMessage((RoomMessage) obj);
    else if (obj instanceof Room)
      processRoom((Room) obj);
  }

  public void setCTabFolderSelection(CTabItem cTabItem) {
    Room room = (Room) cTabItem.getData("room");
    RoomConsole roomConsole = (RoomConsole) cTabItem.getData("roomConsole");
    cTabFolderViewFrame.updateCLabelText(room.getName());
    roomConsole.setFocus();
  }

  public void update(final Observable o, final Object obj) {
    if (getContent() != null && !getContent().isDisposed())
      getContent().getDisplay().asyncExec(new Runnable() {
        public void run() {
          runUpdate(o, obj);
        }
      });
  }

  static class RoomCTabFolderViewListener extends SashViewListener {
    public RoomCTabFolderViewListener(SashViewFrame sashViewFrame) {
      super(sashViewFrame);
    }

    public void menuAboutToShow(IMenuManager menuManager) {
      createSashActions(menuManager, "t.r.availableRooms");
    }
  }

  class RoomCTabFolderViewFrame extends SashViewFrame {
    public RoomCTabFolderViewFrame(SashForm parentSashForm, String prefString, String prefImageString,
        AbstractTab aTab) {
      super(parentSashForm, prefString, prefImageString, aTab);
      createViewListener(new RoomCTabFolderViewListener(this));
      createViewToolBar();
    }

    public void createViewToolBar() {
      super.createViewToolBar();
      addToolItem("t.r.closeClosed", "x-light", new SelectionAdapter() {
        public void widgetSelected(SelectionEvent s) {
          closeAllClosedRooms();
        }
      });
      addToolItem("t.r.closeAll", "x", new SelectionAdapter() {
        public void widgetSelected(SelectionEvent s) {
          closeAllTabs();
        }
      });
    }
  }
}