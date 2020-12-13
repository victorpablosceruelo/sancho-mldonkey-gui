/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.downloads;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;

import sancho.core.Sancho;
import sancho.model.mldonkey.Client;
import sancho.model.mldonkey.File;
import sancho.model.mldonkey.enums.EnumFileState;
import sancho.model.mldonkey.enums.EnumFormat;
import sancho.model.mldonkey.enums.EnumNetwork;
import sancho.model.mldonkey.enums.EnumPriority;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.utility.SwissArmy;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.transfer.FileClient;
import sancho.view.transfer.FileDetailDialog;
import sancho.view.transfer.UniformResourceLocator;
import sancho.view.utility.SResources;
import sancho.view.utility.Spinner;
import sancho.view.utility.WidgetFactory;
import sancho.view.viewer.GView;
import sancho.view.viewer.actions.AddClientAsFriendAction;
import sancho.view.viewer.actions.ClientDetailAction;
import sancho.view.viewer.actions.CopyED2KLinkToClipboardAction;
import sancho.view.viewer.table.GTableMenuListener;

public class DownloadTableTreeMenuListener extends GTableMenuListener {
  private File selectedFile;
  private List selectedClients = new ArrayList();
  private GView clientView;
  private boolean clientTableVisible = false;
  private boolean myDrag = false;

  public DownloadTableTreeMenuListener(DownloadTableTreeView downloadTableTreeViewer) {
    super(downloadTableTreeViewer);
  }

  public void setClientView(GView clientView) {
    this.clientView = clientView;
  }

  public void initialize() {
    super.initialize();

    if (SWT.getPlatform().equals("win32") && PreferenceLoader.loadBoolean("dragAndDrop"))
      activateDragAndDrop();
  }

  public void deselectAll() {
    super.deselectAll();
    selectedClients.clear();
    selectedFile = null;
    if (clientTableVisible)
      clientView.getViewer().setInput(null);
  }

  /**
   * Activate drag and drop
   */
  public void activateDragAndDrop() {
    DragSource dragSource = new DragSource(gView.getTable(), DND.DROP_COPY | DND.DROP_LINK);
    dragSource.setTransfer(new Transfer[]{TextTransfer.getInstance()});

    dragSource.addDragListener(new DragSourceAdapter() {
      public void dragStart(DragSourceEvent event) {
        if (selectedFile == null)
          event.doit = false;
        else {
          event.doit = true;
          myDrag = true;
        }
      }

      public void dragSetData(DragSourceEvent event) {
        event.data = selectedFile.getED2K();
      }

      public void dragFinished(DragSourceEvent event) {
        myDrag = false;
      }
    });

    DropTarget dropTarget = new DropTarget(gView.getTable(), DND.DROP_COPY | DND.DROP_DEFAULT | DND.DROP_LINK);
    final UniformResourceLocator uRL = UniformResourceLocator.getInstance();
    final TextTransfer textTransfer = TextTransfer.getInstance();
    dropTarget.setTransfer(new Transfer[]{uRL, textTransfer});
    dropTarget.addDropListener(new DropTargetAdapter() {
      public void dragEnter(DropTargetEvent event) {
        event.detail = DND.DROP_COPY;

        for (int i = 0; i < event.dataTypes.length; i++) {
          if (uRL.isSupportedType(event.dataTypes[i])) {
            event.detail = DND.DROP_LINK;
            break;
          }
        }
      }

      public void drop(DropTargetEvent event) {
        if ((event.data == null) || myDrag)
          return;

        SwissArmy.sendLink(gView.getCore(), (String) event.data);
      }
    });
  }

  public void selectionChanged(SelectionChangedEvent e) {

    IStructuredSelection sSel = (IStructuredSelection) e.getSelection();

    selectedClients.clear();
    selectedObjects.clear();

    for (Iterator it = sSel.iterator(); it.hasNext();) {
      Object object = it.next();

      if (object instanceof File)
        selectedObjects.add(object);
      else if (object instanceof FileClient)
        selectedClients.add(object);
    }

    if (selectedObjects.size() > 0) {
      selectedFile = (File) selectedObjects.get(0);
      if (clientTableVisible && clientView.getViewer().getInput() != selectedFile)
        clientView.getViewer().setInput(selectedFile);
    } else
      selectedFile = null;

  }

  public void updateClientsTable(boolean visible) {

    if (visible) {
      if (clientTableVisible != visible && selectedFile != null)
        clientView.getViewer().setInput(selectedFile);
    } else
      clientView.getViewer().setInput(null);

    clientTableVisible = visible;
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    if ((selectedFile != null) && selectedFileListContains(EnumFileState.DOWNLOADED))
      menuManager.add(new CommitAction());

    if ((selectedFile != null) && (selectedFile.getFileStateEnum() == EnumFileState.DOWNLOADED)) {
      MenuManager commitAsSubMenu = new MenuManager(SResources.getString("m.d.commitAs"));

      commitAsSubMenu.add(new CommitAction(true));

      for (int i = 0; i < selectedFile.getNames().length; i++)
        commitAsSubMenu.add(new CommitAction(selectedFile.getNames()[i]));

      menuManager.add(commitAsSubMenu);
    }

    if (selectedFile != null)
      menuManager.add(new FileDetailAction());

    if (selectedFile != null && selectedFile.getEnumNetwork() == EnumNetwork.FILETP)
      menuManager.add(new AddMirrorAction(selectedFile));

    if ((selectedFile != null)
        && (selectedFileListContains(EnumFileState.DOWNLOADING) || selectedFileListContains(EnumFileState.QUEUED)))
      menuManager.add(new PauseAction());

    if ((selectedFile != null) && selectedFileListContains(EnumFileState.PAUSED))
      menuManager.add(new ResumeAction());

    if ((selectedFile != null) && selectedFileListContainsOtherThan(EnumFileState.DOWNLOADED))
      menuManager.add(new CancelAction());

    if ((selectedFile != null) && selectedFileListContainsOtherThan(EnumFileState.DOWNLOADED)) {
      MenuManager prioritySubMenu = new MenuManager(SResources.getString("m.d.priority"));
      prioritySubMenu.add(new PriorityAction(EnumPriority.VERY_HIGH));
      prioritySubMenu.add(new PriorityAction(EnumPriority.HIGH));
      prioritySubMenu.add(new PriorityAction(EnumPriority.NORMAL));
      prioritySubMenu.add(new PriorityAction(EnumPriority.LOW));
      prioritySubMenu.add(new PriorityAction(EnumPriority.VERY_LOW));
      prioritySubMenu.add(new Separator());
      prioritySubMenu.add(new CustomPriorityAction(false));
      prioritySubMenu.add(new CustomPriorityAction(true));

      menuManager.add(prioritySubMenu);
    }

    if ((selectedFile != null) && selectedFileListContainsOtherThan(EnumFileState.DOWNLOADED)) {
      MenuManager renameSubMenu = new MenuManager(SResources.getString("m.d.rename"));

      renameSubMenu.add(new RenameAction(true));
      for (int i = 0; i < selectedFile.getNames().length; i++)
        renameSubMenu.add(new RenameAction(selectedFile.getNames()[i]));

      menuManager.add(renameSubMenu);
    }

    if (selectedFile != null && selectedFile.getDownloaded() > 0) {

      String previewExtensions = PreferenceLoader.loadString("previewExtensions");
      String[] apps = null;
      if (!previewExtensions.equals(SResources.S_ES)) {
        apps = SwissArmy.getPreviewApps(selectedFile.getName());
      }

      if (apps != null && apps.length > 1) {
        MenuManager previewSubMenu = new MenuManager(SResources.getString("m.d.preview"));

        for (int i = 0; i < apps.length; i++)
          previewSubMenu.add(new PreviewAppAction(selectedFile, apps[i]));

        menuManager.add(previewSubMenu);

      } else
        menuManager.add(new PreviewAction());
    }

    if ((selectedFile != null)) {
      if (selectedFile.getFormat().getFormat() == EnumFormat.MP3)
        menuManager.add(new EditMP3TagsAction());
      menuManager.add(new ConnectAllAction());
      menuManager.add(new VerifyChunksAction());
      menuManager.add(new RequestFileInfoAction());
      if (selectedObjects.size() > 1)
        menuManager.add(new SetBrothersAction());

    }

    if (selectedClients.size() > 0) {
      Client[] clientArray = new Client[selectedClients.size()];

      for (int i = 0; i < selectedClients.size(); i++) {
        FileClient fileClient = (FileClient) selectedClients.get(i);
        clientArray[i] = fileClient.getClient();
      }

      menuManager.add(new AddClientAsFriendAction(clientArray));
    }

    if (selectedClients.size() > 0) {
      FileClient selectedClient = (FileClient) selectedClients.get(0);
      menuManager.add(new ClientDetailAction(gView.getShell(), selectedClient.getFile(), selectedClient
          .getClient(), gView.getCore()));
    }

    if (selectedFile != null) {
      String[] linkList = new String[selectedObjects.size()];

      for (int i = 0; i < selectedObjects.size(); i++)
        linkList[i] = ((File) selectedObjects.get(i)).getED2K();

      MenuManager clipboardMenu = new MenuManager(SResources.getString("m.d.copyTo"));
      clipboardMenu.add(new CopyED2KLinkToClipboardAction(false, linkList));
      clipboardMenu.add(new CopyED2KLinkToClipboardAction(true, linkList));
      menuManager.add(clipboardMenu);
    }

    if ((selectedFile != null))
      addWebServicesMenu(menuManager, selectedFile.getMd4(), selectedFile.getED2K(), selectedFile.getSize());
  }

  // Helpers
  private boolean selectedFileListContains(EnumFileState e) {
    for (int i = 0; i < selectedObjects.size(); i++)
      if (((File) selectedObjects.get(i)).getFileStateEnum() == e)
        return true;

    return false;
  }

  private boolean selectedFileListContainsOtherThan(EnumFileState e) {
    for (int i = 0; i < selectedObjects.size(); i++)
      if (((File) selectedObjects.get(i)).getFileStateEnum() != e)
        return true;

    return false;
  }

  // Menu Actions

  /**
   * VerifyChunksAction
   */
  private class VerifyChunksAction extends Action {
    public VerifyChunksAction() {
      super(SResources.getString("m.d.verifyChunks"));
      setImageDescriptor(SResources.getImageDescriptor("verify"));
    }

    public void run() {
      for (int i = 0; i < selectedObjects.size(); i++)
        ((File) selectedObjects.get(i)).verifyChunks();
    }
  }

  private class ConnectAllAction extends Action {
    public ConnectAllAction() {
      super(SResources.getString("m.d.connectAll"));
      setImageDescriptor(SResources.getImageDescriptor("menu-connect"));
    }

    public void run() {
      for (int i = 0; i < selectedObjects.size(); i++)
        ((File) selectedObjects.get(i)).connectAll();
    }
  }

  /**
   * PreviewAction
   */
  private class PreviewAction extends Action {
    public PreviewAction() {
      super(SResources.getString("m.d.preview"));
      setImageDescriptor(SResources.getImageDescriptor("preview"));
    }

    public void run() {
      for (int i = 0; i < selectedObjects.size(); i++)
        ((File) selectedObjects.get(i)).preview();
    }
  }

  private class PreviewAppAction extends Action {
    File file;
    String app;

    public PreviewAppAction(File file, String app) {
      super(new java.io.File(app).getName());
      setImageDescriptor(SResources.getImageDescriptor("preview"));
      this.file = file;
      this.app = app;
    }

    public void run() {
      file.preview(app);
    }

  }

  /**
   * FileDetailAction
   */
  private class FileDetailAction extends Action {
    public FileDetailAction() {
      super(SResources.getString("m.d.fileDetails"));
      setImageDescriptor(SResources.getImageDescriptor("info"));
    }

    public void run() {
      if (selectedFile == null)
        return;

      new FileDetailDialog(gView.getShell(), selectedFile).open();
    }
  }

  /**
   * PauseAction
   */
  private class PauseAction extends Action {
    public PauseAction() {
      super(SResources.getString("m.d.pause"));
      setImageDescriptor(SResources.getImageDescriptor("pause"));
    }

    public void run() {
      for (int i = 0; i < selectedObjects.size(); i++) {
        File file = (File) selectedObjects.get(i);

        if (file.getFileStateEnum() != EnumFileState.PAUSED)
          file.setState(EnumFileState.PAUSED);
      }
    }
  }

  /**
   * CommitAction
   */
  private class CommitAction extends Action {
    private String commitAs;
    private boolean manualInput = false;

    public CommitAction() {
      super(SResources.getString("m.d.commitSelected"));
      setImageDescriptor(SResources.getImageDescriptor("commit"));
    }

    public CommitAction(String commitAs) {
      super(commitAs);
      setImageDescriptor(SResources.getImageDescriptor("commit"));
      this.commitAs = commitAs;
    }

    public CommitAction(boolean b) {
      super(SResources.getString("m.d.commitInput"));
      setImageDescriptor(SResources.getImageDescriptor("commit_question"));
      manualInput = b;
    }

    public void run() {
      if ((commitAs == null) && !manualInput) {
        for (int i = 0; i < selectedObjects.size(); i++) {
          File selectedFile = (File) selectedObjects.get(i);

          if (selectedFile.getFileStateEnum() == EnumFileState.DOWNLOADED)
            selectedFile.saveFileAs(selectedFile.getName());
        }
      } else {
        if (manualInput) {
          if (selectedFile == null)
            return;

          InputDialog inputDialog = new InputDialog(gView.getShell(), SResources.getString("m.d.commitAs"),
              SResources.getString("m.d.commitAs"), selectedFile.getName(), null);

          if (inputDialog.open() == InputDialog.OK) {
            String newFileName = inputDialog.getValue();

            if (!newFileName.equals(SResources.S_ES) && selectedFile != null)
              selectedFile.saveFileAs(newFileName);
          }
        } else {
          if (selectedFile != null)
            selectedFile.saveFileAs(commitAs);
        }
      }
    }
  }

  private class RenameAction extends Action {
    private String renameAs;
    private boolean manualInput = false;

    public RenameAction(String renameAs) {
      super(renameAs);
      setImageDescriptor(SResources.getImageDescriptor(selectedFile.getProgramImageString()));
      this.renameAs = renameAs;
    }

    public RenameAction(boolean b) {
      super(SResources.getString("m.d.commitInput")); // <input filename>
      setImageDescriptor(SResources.getImageDescriptor("commit_question"));
      manualInput = b;
    }

    public void run() {

      if (manualInput) {

        for (int i = 0; i < selectedObjects.size(); i++) {
          File selected = (File) selectedObjects.get(i);
          InputDialog inputDialog = new InputDialog(gView.getShell(), SResources.getString("m.d.rename"),
              SResources.getString("m.d.rename"), selected.getName(), null);

          if (inputDialog.open() == InputDialog.OK) {
            String newFileName = inputDialog.getValue();

            if (!newFileName.equals(SResources.S_ES) && selected != null)
              selected.rename(newFileName);
          } else {
            break;
          }
        }
      } else {
        if (selectedFile != null)
          selectedFile.rename(renameAs);
      }
    }
  }

  private class EditMP3TagsAction extends Action {
    public EditMP3TagsAction() {
      super(SResources.getString("m.d.editMP3Tags"));
      setImageDescriptor(SResources.getImageDescriptor("preferences"));
    }

    public void run() {
      if (selectedFile == null)
        return;

      new EditMP3TagsDialog(gView.getShell(), gView.getCore(), selectedFile).open();
    }
  }

  private class RequestFileInfoAction extends Action {
    public RequestFileInfoAction() {
      super(SResources.getString("m.d.requestFileInfo"));
      setImageDescriptor(SResources.getImageDescriptor("rotate"));
    }

    public void run() {
      for (int i = 0; i < selectedObjects.size(); i++) {
        File file = (File) selectedObjects.get(i);
        file.requestFileInfo();
      }
    }
  }

  /**
   * ResumeAction
   */
  private class ResumeAction extends Action {
    public ResumeAction() {
      super(SResources.getString("m.d.resume"));
      setImageDescriptor(SResources.getImageDescriptor("resume"));
    }

    public void run() {
      for (int i = 0; i < selectedObjects.size(); i++) {
        File file = (File) selectedObjects.get(i);

        if (file.getFileStateEnum() == EnumFileState.PAUSED)
          file.setState(EnumFileState.DOWNLOADING);
      }
    }
  }

  /**
   * CancelAction
   */
  private class CancelAction extends Action {
    public CancelAction() {
      super(SResources.getString("m.d.cancel"));
      setImageDescriptor(SResources.getImageDescriptor("cancel"));
    }

    public void run() {
      MessageBox reallyCancel = new MessageBox(gView.getShell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION);

      reallyCancel
          .setMessage(SResources.getString("m.d.reallyCancel")
              + ((selectedObjects.size() > 1)
                  ? (" (" + selectedObjects.size() + " selected)")
                  : SResources.S_ES));

      int answer = reallyCancel.open();

      if (answer == SWT.YES) {
        for (int i = 0; i < selectedObjects.size(); i++) {
          File file = (File) selectedObjects.get(i);

          if (file.getFileStateEnum() != EnumFileState.DOWNLOADED)
            file.setState(EnumFileState.CANCELLED);
        }
        deselectAll();
      }
    }
  }

  private class AddMirrorAction extends Action {

    File file;

    public AddMirrorAction(File file) {
      super(SResources.getString("dd.f.addMirror"));
      setImageDescriptor(SResources.getImageDescriptor("plus"));
      this.file = file;
    }

    public void run() {
      InputDialog dialog = new InputDialog(gView.getShell(), SResources.getString("dd.f.addMirror"),
          SResources.getString("dd.f.addMirrorInfo"), SResources.S_ES, null);
      dialog.open();

      String result = dialog.getValue();

      if (result != null)
        Sancho.send(OpCodes.S_CONSOLE_MESSAGE, "mirror " + file.getId() + " " + result);
    }
  }

  private class SetBrothersAction extends Action {
    public SetBrothersAction() {
      super(SResources.getString("dd.f.setBrothers"));
      setImageDescriptor(SResources.getImageDescriptor("brothers"));
    }

    public void run() {

      int[] iArray = new int[selectedObjects.size()];

      for (int i = 0; i < selectedObjects.size(); i++) {
        File file = (File) selectedObjects.get(i);
        iArray[i] = file.getId();
      }

      if (Sancho.hasCollectionFactory())
        Sancho.getCore().getFileCollection().setBrothers(iArray);

    }

  }

  /**
   * PriorityAction
   */
  private class PriorityAction extends Action {
    private EnumPriority enumPriority;

    public PriorityAction(EnumPriority e) {
      super(e.getName().toLowerCase(), Action.AS_CHECK_BOX);
      enumPriority = e;
    }

    public void run() {
      for (int i = 0; i < selectedObjects.size(); i++) {
        File file = (File) selectedObjects.get(i);

        if (file.getFileStateEnum() != EnumFileState.DOWNLOADED)
          file.sendPriority(enumPriority);
      }
    }

    public boolean isChecked() {
      return (selectedFile.getPriorityEnum() == enumPriority);
    }
  }

  /**
   * CustomPriorityAction
   *  
   */
  private class CustomPriorityAction extends Action {
    private boolean relative;

    public CustomPriorityAction(boolean relative) {
      super(SResources.S_ES, Action.AS_CHECK_BOX);
      this.relative = relative;

      if (relative)
        setText(SResources.getString("m.d.priorityRelative"));
      else
        setText(SResources.getString("m.d.priorityAbsolute"));
    }

    public void run() {
      String title = SResources.getString("m.d.priority")
          + " ("
          + (relative ? SResources.getString("m.d.priorityRelative") : SResources
              .getString("m.d.priorityAbsolute")) + ")";

      PriorityInputDialog priorityInputDialog = new PriorityInputDialog(gView.getShell(), title, (relative
          ? 0
          : selectedFile.getPriority()));

      if (priorityInputDialog.open() == PriorityInputDialog.OK) {
        int newPriority = priorityInputDialog.getIntValue();
        for (int i = 0; i < selectedObjects.size(); i++) {
          File file = (File) selectedObjects.get(i);
          if (file.getFileStateEnum() != EnumFileState.DOWNLOADED)
            file.sendPriority(relative, newPriority);
        }
      }
    }

    public boolean isChecked() {
      return false;
    }
  }

  /**
   * PriorityInputDialog
   */
  static class PriorityInputDialog extends Dialog {
    int initialValue;
    int intValue;
    String title;
    Spinner spinner;
    Button okButton;

    public PriorityInputDialog(Shell parentShell, String dialogTitle, int initialValue) {
      super(parentShell);
      this.initialValue = initialValue;
      this.title = dialogTitle;
    }

    protected void configureShell(Shell newShell) {
      super.configureShell(newShell);
      newShell.setImage(SResources.getImage("ProgramIcon"));
      newShell.setText(title);
    }

    protected void createButtonsForButtonBar(Composite parent) {
      // create OK and Cancel buttons by default
      okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
      createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
      spinner.setFocus();
    }

    protected Button getOkButton() {
      return okButton;
    }

    protected Control createDialogArea(Composite parent) {

      Composite composite = (Composite) super.createDialogArea(parent);
      composite.setLayout(WidgetFactory.createGridLayout(2, 5, 5, 10, 5, false));

      spinner = new Spinner(composite, SWT.NONE);
      spinner.setMinimum(-200);
      spinner.setMaximum(200);
      spinner.setSelection(initialValue);
      spinner.getText().addListener(SWT.Traverse, new Listener() {
        public void handleEvent(Event event) {
          if (event.detail == SWT.TRAVERSE_ESCAPE) {
            // 
          } else if (event.detail == SWT.TRAVERSE_RETURN) {
            intValue = spinner.getSelection();
            close();
          }
        }
      });

      final Scale scale = new Scale(composite, SWT.HORIZONTAL);
      scale.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      scale.setMinimum(0);
      scale.setMaximum(200);
      scale.setIncrement(1);
      scale.setPageIncrement(5);

      if (initialValue < -100)
        scale.setSelection(0);
      else if (initialValue > 100)
        scale.setSelection(200);
      else
        scale.setSelection(initialValue + 100);

      scale.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
          int intValue = (scale.getSelection() - 100);
          spinner.setSelection(intValue);
        }
      });

      return composite;
    }

    protected void buttonPressed(int buttonId) {
      intValue = spinner.getSelection();
      super.buttonPressed(buttonId);
    }

    public int getIntValue() {
      return intValue;
    }

  }
}