/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer;

import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import sancho.core.Sancho;
import sancho.model.mldonkey.File;
import sancho.model.mldonkey.Network;
import sancho.model.mldonkey.enums.EnumFileState;
import sancho.model.mldonkey.enums.EnumNetwork;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.utility.SwissArmy;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public class FileDetailDialog extends AbstractDetailDialog {
  private File file;
  private Button fileActionButton;
  private Button fileCancelButton;
  private CLabel clFileName;
  private CLabel clHash;
  private CLabel clSize;
  private CLabel clAge;
  private CLabel clSources;
  private CLabel clChunks;
  private CLabel clTransferred;
  private CLabel clRelativeAvail;
  private CLabel clLast;
  private CLabel clPriority;
  private CLabel clRate;
  private CLabel clETA;
  private CLabel clComment;
  private CLabel clEmpty;
  private List renameList;
  private Text renameText;

  public FileDetailDialog(Shell parentShell, File file) {
    super(parentShell);
    this.file = file;
  }

  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(SResources.getString("l.file") + " " + file.getId() + " "
        + SResources.getString("l.details").toLowerCase());
  }

  protected Control createDialogArea(Composite parent) {
    Composite composite = (Composite) super.createDialogArea(parent);
    composite.setLayout(WidgetFactory.createGridLayout(1, 5, 5, 0, 5, false));

    createFileGeneralGroup(composite);

    createFileTransferGroup(composite);

    // MultiNet chunks or just chunks < proto 17
    createChunkGroup(composite, SResources.getString("dd.f.chunksInformation"), null);

    // Other network chunks
    if (file.hasAvails()) {
      Iterator i = file.getAllAvailNetworks().iterator();

      while (i.hasNext()) {
        Network network = (Network) i.next();

        if (network.isEnabled()) {
          createChunkGroup(composite, network.getName(), network);
        }
      }
    }

    if (file.getEnumNetwork() == EnumNetwork.DONKEY)
      createCommentGroup(composite);

    createRenameGroup(composite);
    if (file.getEnumNetwork() == EnumNetwork.FILETP)
      createMirrorGroup(composite);

    // Separator
    Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
    separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    updateLabels();
    file.addObserver(this);

    return composite;
  }

  /**
   * Tell the core to rename the file
   */
  private void renameFile() {
    String newName = SResources.S_ES;

    if (!renameText.getText().equals(SResources.S_ES) && !renameText.getText().equals(file.getName())) {
      newName = renameText.getText();
    } else if ((renameList.getSelection().length > 0) && !renameList.getSelection()[0].equals(file.getName())) {
      newName = renameList.getSelection()[0];
    }

    if (!newName.equals(SResources.S_ES)) {
      file.rename(newName);
    }
  }

  private void createFileGeneralGroup(Composite parent) {
    Group fileGeneral = new Group(parent, SWT.SHADOW_ETCHED_OUT);

    fileGeneral.setText(SResources.getString("dd.f.fileInformation"));
    fileGeneral.setLayout(WidgetFactory.createGridLayout(4, 5, 0, 0, 0, false));
    fileGeneral.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    clFileName = createLine(fileGeneral, "dd.f.fileName", true);
    clHash = createLine(fileGeneral, "dd.f.hash", true);
    clSize = createLine(fileGeneral, "dd.f.size", false);
    clAge = createLine(fileGeneral, "dd.f.age", false);
    clComment = createLine(fileGeneral, "dd.f.comment", true);
  }

  private void createFileTransferGroup(Composite parent) {
    Group fileTransfer = new Group(parent, SWT.SHADOW_ETCHED_OUT);

    fileTransfer.setText(SResources.getString("dd.f.transferInformation"));
    fileTransfer.setLayout(WidgetFactory.createGridLayout(4, 5, 0, 0, 0, false));
    fileTransfer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    clSources = createLine(fileTransfer, "dd.f.sources", false);
    clChunks = createLine(fileTransfer, "dd.f.chunks", false);
    clTransferred = createLine(fileTransfer, "dd.f.transferred", false);
    clRelativeAvail = createLine(fileTransfer, "dd.f.availability", false);
    clLast = createLine(fileTransfer, "dd.f.last", false);
    clPriority = createLine(fileTransfer, "dd.f.priority", false);
    clRate = createLine(fileTransfer, "dd.f.rate", false);
    clETA = createLine(fileTransfer, "dd.f.eta", false);

    if (SWT.getPlatform().equals("win32") && file.getChunkAges().length < 1000) { // 74824
      clEmpty = createLine(fileTransfer, SResources.S_ES, false);
      createChunkAgesGroup(fileTransfer);
    }

  }

  /**
   * @param parent
   * @param resString
   * @param network
   */
  private void createChunkGroup(Composite parent, String string, Network network) {
    ChunkCanvas chunkCanvas = super.createChunkGroup(parent, string, null, file, network);
    // file.addObserver(chunkCanvas);
  }

  private void createChunkAgesGroup(Composite parent) {
    //  ugly

    Label label = new Label(parent, SWT.NONE);
    label.setText(SResources.getString("dd.f.chunkAges"));

    GridData gridData = new GridData();
    gridData.widthHint = leftColumn;
    label.setLayoutData(gridData);

    final Combo chunkCombo = new Combo(parent, SWT.READ_ONLY);
    GridData data = new GridData(GridData.FILL_HORIZONTAL);
    data.widthHint = 1;
    chunkCombo.setLayoutData(data);

    int[] ages = file.getChunkAges();

    for (int i = 0; i < ages.length; i++) {
      chunkCombo.add((i + 1) + ": " + (ages[i] > 75000000 ? "-" : SwissArmy.calcStringOfSeconds(ages[i])));
    }
    if (ages.length > 0)
      chunkCombo.select(0);

  }

  private void createCommentGroup(Composite parent) {

    Label l = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
    l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    Composite renameComposite = new Composite(parent, SWT.NONE);

    renameComposite.setLayout(WidgetFactory.createGridLayout(2, 0, 0, 4, 0, false));
    renameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    final Text commentText = new Text(renameComposite, SWT.BORDER);
    commentText.setFont(PreferenceLoader.loadFont("consoleFontData"));

    GridData data = new GridData(GridData.FILL_HORIZONTAL);
    data.widthHint = 1;
    commentText.setLayoutData(data);
    commentText.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.character == SWT.CR) {
          addComment(commentText.getText());
          commentText.setText(SResources.S_ES);
        }
      }
    });

    Button commentButton = new Button(renameComposite, SWT.NONE);
    commentButton.setText(SResources.getString("dd.f.addComment"));
    commentButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    commentButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        addComment(commentText.getText());
        commentText.setText(SResources.S_ES);
      }
    });
  }

  public void addComment(String string) {
    if (string != null)
      file.setComment(string);

  }

  private void createMirrorGroup(Composite parent) {

    Label l = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
    l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    Composite renameComposite = new Composite(parent, SWT.NONE);

    renameComposite.setLayout(WidgetFactory.createGridLayout(2, 0, 0, 4, 0, false));
    renameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    final Text mirrorText = new Text(renameComposite, SWT.BORDER);
    mirrorText.setFont(PreferenceLoader.loadFont("consoleFontData"));

    GridData data = new GridData(GridData.FILL_HORIZONTAL);
    data.widthHint = 1;
    mirrorText.setLayoutData(data);
    mirrorText.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.character == SWT.CR) {
          addMirror(mirrorText.getText());
          mirrorText.setText(SResources.S_ES);
        }
      }
    });

    Button mirrorButton = new Button(renameComposite, SWT.NONE);
    mirrorButton.setText(SResources.getString("dd.f.addMirror"));
    mirrorButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    mirrorButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        addMirror(mirrorText.getText());
        mirrorText.setText(SResources.S_ES);
      }
    });
  }

  public void addMirror(String string) {
    if (string != null && string.length() > 3)
      Sancho.send(OpCodes.S_CONSOLE_MESSAGE, "mirror " + file.getId() + " " + string);
  }

  private void createRenameGroup(Composite parent) {
    Group renameGroup = new Group(parent, SWT.SHADOW_ETCHED_OUT);

    renameGroup.setText(SResources.getString("dd.f.alternativeFilenames"));
    renameGroup.setLayout(WidgetFactory.createGridLayout(1, 1, 1, 0, 0, false));
    renameGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    Arrays.sort(file.getNames(), String.CASE_INSENSITIVE_ORDER);

    renameList = new List(renameGroup, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);

    for (int i = 0; i < file.getNames().length; i++)
      renameList.add(file.getNames()[i]);

    GridData listGD = new GridData(GridData.FILL_HORIZONTAL);
    listGD.heightHint = 80;
    listGD.widthHint = 1;
    renameList.setLayoutData(listGD);
    renameList.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        String lItem = renameList.getSelection()[0];
        renameText.setText(lItem);
      }
    });

    Composite renameComposite = new Composite(parent, SWT.NONE);

    renameComposite.setLayout(WidgetFactory.createGridLayout(2, 0, 0, 4, 0, false));
    renameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    renameText = new Text(renameComposite, SWT.BORDER);
    renameText.setText(file.getName());
    renameText.setFont(PreferenceLoader.loadFont("consoleFontData"));

    GridData data = new GridData(GridData.FILL_HORIZONTAL);
    data.widthHint = 1;
    renameText.setLayoutData(data);
    renameText.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.character == SWT.CR) {
          renameFile();
          renameText.setText(SResources.S_ES);
        }
      }
    });

    Button renameButton = new Button(renameComposite, SWT.NONE);
    renameButton.setText(SResources.getString("dd.f.renameFile"));
    renameButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    renameButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        renameFile();
      }
    });
  }

  protected Control createButtonBar(Composite parent) {
    Composite mainComposite = new Composite(parent, SWT.NONE);
    mainComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    mainComposite.setLayout(WidgetFactory.createGridLayout(2, 5, 5, 0, 0, false));

    Composite blankComposite = new Composite(mainComposite, SWT.NONE);
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.heightHint = 5;

    blankComposite.setLayoutData(gd);

    Composite buttonComposite = new Composite(mainComposite, SWT.NONE);
    buttonComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
    buttonComposite.setLayout(WidgetFactory.createRowLayout(false, false, false, SWT.HORIZONTAL, 0, 0, 0, 0,
        5));

    if ((file.getFileStateEnum() == EnumFileState.PAUSED)
        || (file.getFileStateEnum() == EnumFileState.DOWNLOADING)
        || (file.getFileStateEnum() == EnumFileState.QUEUED)) {
      fileCancelButton = new Button(buttonComposite, SWT.NONE);
      fileCancelButton.setLayoutData(new RowData());
      fileCancelButton.setText(SResources.getString("dd.f.cancelFile"));

      fileCancelButton.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent s) {
          MessageBox reallyCancel = new MessageBox(fileCancelButton.getShell(), SWT.YES | SWT.NO
              | SWT.ICON_QUESTION);

          reallyCancel.setMessage(SResources.getString("dd.f.reallyCancel"));

          if (reallyCancel.open() == SWT.YES) {
            file.setState(EnumFileState.CANCELLED);
            fileCancelButton.setEnabled(false);
            fileActionButton.setEnabled(false);
          }
        }
      });
    }

    fileActionButton = new Button(buttonComposite, SWT.NONE);
    fileActionButton.setLayoutData(new RowData());

    if ((file.getFileStateEnum() == EnumFileState.PAUSED)
        || (file.getFileStateEnum() == EnumFileState.QUEUED)) {
      fileActionButton.setText(SResources.getString("dd.f.resumeFile"));
    } else if (file.getFileStateEnum() == EnumFileState.DOWNLOADING) {
      fileActionButton.setText("  " + SResources.getString("dd.f.pauseFile"));
    } else if (file.getFileStateEnum() == EnumFileState.DOWNLOADED) {
      fileActionButton.setText(SResources.getString("dd.f.commitFile"));
    }

    // until we have an unQueue function..
    if (file.getFileStateEnum() == EnumFileState.QUEUED)
      fileActionButton.setEnabled(false);

    fileActionButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        if (file.getFileStateEnum() == EnumFileState.PAUSED) {
          file.setState(EnumFileState.DOWNLOADING);
          fileActionButton.setText(SResources.getString("dd.f.pauseFile"));
        } else if (file.getFileStateEnum() == EnumFileState.DOWNLOADING) {
          file.setState(EnumFileState.PAUSED);
          fileActionButton.setText(SResources.getString("dd.f.resumeFile"));
        } else if (file.getFileStateEnum() == EnumFileState.DOWNLOADED) {
          if (renameText.getText().equals(SResources.S_ES)) {
            file.saveFileAs(file.getName());
          } else {
            file.saveFileAs(renameText.getText());
          }

          fileActionButton.setText(SResources.getString("b.ok"));
          fileActionButton.setEnabled(false);
        }
      }
    });

    Button closeButton = new Button(buttonComposite, SWT.NONE);
    closeButton.setFocus();
    closeButton.setLayoutData(new RowData());
    closeButton.setText(SResources.getString("b.close"));
    closeButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        close();
      }
    });

    return mainComposite;
  }

  /**
   * Update the labels
   */
  public void updateLabels() {
    updateLabel(clFileName, file.getName());
    updateLabel(clHash, file.getMd4().toUpperCase());
    updateLabel(clSize, file.getSizeString());
    updateLabel(clAge, file.getAgeString());
    updateLabel(clSources, Integer.toString(file.getSources()));
    updateLabel(clChunks, Integer.toString(file.getNumChunks()) + " / "
        + Integer.toString(file.getChunks().length()));
    updateLabel(clTransferred, file.getDownloadedString());
    updateLabel(clRelativeAvail, file.getRelativeAvail() + "%");
    updateLabel(clLast, file.getLastSeenString());
    updateLabel(clPriority, file.getPriorityString());
    updateLabel(clComment, file.getComment());
    if (file.getFileStateEnum() == EnumFileState.PAUSED || file.getFileStateEnum() == EnumFileState.QUEUED) {
      updateLabel(clRate, file.getFileStateEnum().getName());
    } else {
      updateLabel(clRate, (Math.round(100.0 * (file.getRate() / 1000f)) / 100.0) + " KB/s");
    }

    updateLabel(clETA, file.getEtaString());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
   */
  public boolean close() {
    file.deleteObserver(this);

    return super.close();
  }
}