/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statusline;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import sancho.core.Sancho;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.utility.SwissArmy;
import sancho.view.StatusLine;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.transfer.UniformResourceLocator;
import sancho.view.utility.MyViewForm;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public class LinkEntry {
  private StatusLine statusLine;

  public LinkEntry(StatusLine statusLine, Composite parent) {
    this.statusLine = statusLine;
    createContents(parent);
  }

  public void createContents(final Composite parent) {
    MyViewForm linkEntryViewForm = WidgetFactory.createViewForm(parent, false);
    linkEntryViewForm.setLayoutData(new GridData(GridData.FILL_BOTH));

    CLabel linkEntryCLabel = WidgetFactory.createCLabel(linkEntryViewForm, "sl.linkEntryHeader",
        "up_arrow_green");
    linkEntryCLabel.setFont(PreferenceLoader.loadFont("headerFontData"));

    final Text linkEntryText = new Text(linkEntryViewForm, SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
    linkEntryText.setLayoutData(new FillLayout());
    linkEntryText.setFont(PreferenceLoader.loadFont("ircConsoleFontData"));
    linkEntryText.setForeground(PreferenceLoader.loadColor("ircConsoleInputForeground"));
    linkEntryText.setBackground(PreferenceLoader.loadColor("ircConsoleInputBackground"));

    linkEntryText.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if ((e.stateMask & SWT.CTRL) != 0) {
          if ((e.character == SWT.LF) || (e.character == SWT.CR)) {
            enterLinks(linkEntryText);
            e.doit = false;
          }
        }
      }
    });

    final ToolBar linkEntryToolBar = new ToolBar(linkEntryViewForm, SWT.RIGHT | SWT.FLAT);

    ToolItem torrentItem = new ToolItem(linkEntryToolBar, SWT.PUSH);
    torrentItem.setToolTipText(SResources.getString("sl.addLocalTorrents"));
    torrentItem.setImage(SResources.getImage("folder-12"));
    torrentItem.setText(SResources.S_ES);
    torrentItem.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {

        if (!Sancho.hasCollectionFactory())
          return;

        FileDialog fileDialog = new FileDialog(linkEntryToolBar.getShell(), SWT.MULTI);
        fileDialog.setFilterExtensions(new String[]{"*.torrent"});
        if (fileDialog.open() != null) {

          if (Sancho.getCore() != null) {

            String path = fileDialog.getFilterPath() + System.getProperty("file.separator");
            String[] fileNames = fileDialog.getFileNames();
            for (int i = 0; i < fileNames.length; i++)
              SwissArmy.sendLink(Sancho.getCore(), path + fileNames[i]);
            statusLine.setText(SResources.getString("sl.linksSent") + fileNames.length);
          }
        }
      }
    });

    ToolItem clearItem = new ToolItem(linkEntryToolBar, SWT.PUSH);
    clearItem.setText(SResources.getString("sl.clear"));
    clearItem.setImage(SResources.getImage("clear-12"));
    clearItem.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        linkEntryText.setText(SResources.S_ES);
      }
    });

    ToolItem sendItem = new ToolItem(linkEntryToolBar, SWT.PUSH);
    sendItem.setText(SResources.getString("sl.send"));
    sendItem.setImage(SResources.getImage("up_arrow_green"));
    sendItem.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        enterLinks(linkEntryText);
      }
    });

    linkEntryViewForm.setTopLeft(linkEntryCLabel);
    linkEntryViewForm.setContent(linkEntryText);
    linkEntryViewForm.setTopRight(linkEntryToolBar);

    linkEntryToolBar.pack();

    if (SWT.getPlatform().equals("win32") && PreferenceLoader.loadBoolean("dragAndDrop"))
      activateDropTarget(linkEntryText);
  }

  public void enterLinks(Text linkEntryText) {
    String input = linkEntryText.getText();
    RE regex = null;

    try {
      regex = new RE("(ed2k://\\|file\\|[^\\|]+\\|(\\d+)\\|([\\dabcdef]+)\\|)"
          + "|(sig2dat:///?\\|File:[^\\|]+\\|Length:.+?\\|UUHash:\\=.+?\\=)" + "|(\\\"magnet:\\?xt=.+?\\\")"
          + "|(magnet:\\?xt=.+?\n)" + ((linkEntryText.getLineCount() == 1) ? "|(magnet:\\?xt=.+)" : SResources.S_ES)
          + ((linkEntryText.getLineCount() == 1) ? "|(http://.+?\\.torrent.+)" : SResources.S_ES)
          + ((linkEntryText.getLineCount() == 1) ? "|(.+?\\.torrent.*)" : SResources.S_ES)
          + ((linkEntryText.getLineCount() == 1) ? "|(.+?\\.torrent)" : SResources.S_ES)
          + "|(\"http://.+\\.torrent\\?[^>]+\")" + "|(http://.+\\.torrent)", RE.REG_ICASE | RE.REG_MULTILINE);
    } catch (REException e) {
      e.printStackTrace();
    }

    REMatch[] matches = regex.getAllMatches(input);

    for (int i = 0; i < matches.length; i++) {
      String link = SwissArmy.replaceAll(matches[i].toString(), "\"", SResources.S_ES);
      link = SwissArmy.replaceAll(link, "\n", SResources.S_ES);
      Sancho.send(OpCodes.S_DLLINK, link);
    }

    statusLine.setText(SResources.getString("sl.linksSent") + matches.length);
    linkEntryText.setText(SResources.S_ES);
  }

  private void activateDropTarget(final Text linkEntryText) {
    DropTarget dropTarget = new DropTarget(linkEntryText, DND.DROP_COPY | DND.DROP_DEFAULT | DND.DROP_LINK);
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
        if (event.data == null)
          return;
        linkEntryText.append((String) event.data);
      }
    });
  }
}