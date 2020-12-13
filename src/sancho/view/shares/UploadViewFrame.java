/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.shares;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import sancho.core.Sancho;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.SResources;
import sancho.view.utility.Spinner;
import sancho.view.utility.WidgetFactory;
import sancho.view.viewFrame.TabbedViewFrame;

public class UploadViewFrame extends TabbedViewFrame {

  ShareDialog shareDialog;

  public UploadViewFrame(Composite parent, String prefString, String prefImageString, AbstractTab aTab) {
    super(parent, prefString, prefImageString, aTab, "uploads");

    gView = new UploadTableView(this);
    createViewListener(new UploadViewListener(this));
    createViewToolBar();

    switchToTab(cTabFolder.getItems()[0]);
  }

  // Temporary - until gui protocol has better access to shared directories
  public void createViewToolBar() {
    super.createViewToolBar();

    addToolItem("ti.u.unshare", "minus", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        shareDialog = new ShareDialog(gView.getShell(), SResources.getString("l.unshareDirectory"), false);
        if (shareDialog.open() == ShareDialog.OK)
          sendShareCommand(false);
        shareDialog = null;
      }
    });

    addToolItem("ti.u.share", "plus", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        shareDialog = new ShareDialog(gView.getShell(), SResources.getString("l.shareDirectory"), true);
        if (shareDialog.open() == ShareDialog.OK)
          sendShareCommand(true);
        shareDialog = null;
      }
    });

    addToolItem("ti.u.reshare", "rotate", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        if (Sancho.hasCollectionFactory())
          getCore().getSharedFileCollection().reshare();
      }
    });

    addToolSeparator();
    addRefine();
  }

  public void sendShareCommand(boolean share) {
    if (shareDialog != null && !shareDialog.getDirectory().equals(SResources.S_ES) && Sancho.hasCollectionFactory()) {
      String command = share ? "share " + shareDialog.getPriority() : "unshare";
      command += " \"" + shareDialog.getDirectory() + "\"";
      Sancho.send(OpCodes.S_CONSOLE_MESSAGE, command);
    }
  }

  class ShareDialog extends Dialog {
    private int priority;
    private String directory;
    private boolean share;
    private Spinner spinner;
    private Text dirText;
    private String title;
    public int ADD_ID = 999;

    public ShareDialog(Shell parentShell, String dialogTitle, boolean share) {
      super(parentShell);
      this.share = share;
      this.title = dialogTitle;
    }

    protected void configureShell(Shell newShell) {
      super.configureShell(newShell);
      newShell.setImage(SResources.getImage("ProgramIcon"));
      newShell.setText(title);
    }

    protected void createButtonsForButtonBar(Composite parent) {
      Button addButton = createButton(parent, ADD_ID, SResources.getString("b.okNoClose"), false);
      super.createButtonsForButtonBar(parent);
    }

    protected Control createDialogArea(Composite parent) {
      Composite composite = (Composite) super.createDialogArea(parent);
      composite.setLayout(WidgetFactory.createGridLayout(2, 10, 10, 10, 10, false));

      dirText = new Text(composite, SWT.BORDER);
      dirText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      activateDropTarget(dirText);

      Button browse = new Button(composite, SWT.NONE);
      browse.setText(SResources.getString("b.browse"));
      browse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
      browse.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent s) {
          Button button = (Button) s.widget;
          DirectoryDialog dirDialog = new DirectoryDialog(button.getShell(), SWT.NULL);
          String result;
          if ((result = dirDialog.open()) != null) {
            dirText.setText(result);
          }
        }
      });

      if (share) {
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;

        Composite priorityComposite = new Composite(composite, SWT.NONE);
        priorityComposite.setLayout(WidgetFactory.createGridLayout(2, 0, 0, 10, 0, false));
        priorityComposite.setLayoutData(gd);

        spinner = new Spinner(priorityComposite, SWT.NONE);
        spinner.setMaximum(999);
        spinner.setMinimum(0);

        Label label = new Label(priorityComposite, SWT.NONE);
        label.setText(SResources.getString("m.d.priority"));
      }
      return composite;
    }

    protected void buttonPressed(int buttonId) {
      if (share)
        priority = spinner.getSelection();
      directory = dirText.getText();
      super.buttonPressed(buttonId);
      if (buttonId == ADD_ID) {
        sendShareCommand(share);
        dirText.setText(SResources.S_ES);
      }
    }

    public String getDirectory() {
      return directory;
    }

    public int getPriority() {
      return priority;
    }

    private void activateDropTarget(final Text text) {
      DropTarget dropTarget = new DropTarget(text, DND.DROP_COPY | DND.DROP_DEFAULT | DND.DROP_LINK);
      final TextTransfer textTransfer = TextTransfer.getInstance();
      final FileTransfer fileTransfer = FileTransfer.getInstance();
      dropTarget.setTransfer(new Transfer[]{fileTransfer, textTransfer});
      dropTarget.addDropListener(new DropTargetAdapter() {
        public void dragEnter(DropTargetEvent event) {
          if (event.detail == DND.DROP_DEFAULT) {
            if ((event.operations & DND.DROP_COPY) != 0) {
              event.detail = DND.DROP_COPY;
            } else {
              event.detail = DND.DROP_NONE;
            }
          }
          // will accept text but prefer to have files dropped 
          for (int i = 0; i < event.dataTypes.length; i++) {
            if (fileTransfer.isSupportedType(event.dataTypes[i])) {
              event.currentDataType = event.dataTypes[i];
              // files should only be copied 
              if (event.detail != DND.DROP_COPY) {
                event.detail = DND.DROP_NONE;
              }
              break;
            }
          }
        }

        public void drop(DropTargetEvent event) {
          if (textTransfer.isSupportedType(event.currentDataType)) {
            text.append((String) event.data);
          }
          if (fileTransfer.isSupportedType(event.currentDataType)) {
            String[] files = (String[]) event.data;

            if (files.length > 1) {
              for (int i = 0; i < files.length; i++) {
                text.setText(files[i]);
                directory = files[i];
                sendShareCommand(share);
                text.setText(SResources.S_ES);
              }
            } else if (files.length == 1) {
              text.append(files[0]);
            }
          }
        }
      });
    }

  }
}