/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.search.result;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

import sancho.core.Sancho;
import sancho.model.mldonkey.Result;
import sancho.view.MainWindow;
import sancho.view.SearchTab;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;
import sancho.view.viewer.ICustomViewer;
import sancho.view.viewer.actions.CopyED2KLinkToClipboardAction;
import sancho.view.viewer.actions.WebServicesAction;
import sancho.view.viewer.table.GTableMenuListener;
import sancho.view.viewer.table.GTableView;

public class ResultTableMenuListener extends GTableMenuListener {
  private CTabItem cTabItem;

  public ResultTableMenuListener(GTableView rTableViewer, CTabItem aCTabItem) {
    super(rTableViewer);
    this.cTabItem = aCTabItem;
  }

  public void initialize() {
    super.initialize();

    if (PreferenceLoader.loadBoolean("searchTooltips")) {
      final ToolTipHandler toolTipHandler = new ToolTipHandler(gView.getShell());
      toolTipHandler.activateHoverHelp(gView.getTable(), (ICustomViewer) gView.getViewer());
      gView.getTable().addDisposeListener(new DisposeListener() {
        public void widgetDisposed(DisposeEvent e) {
          toolTipHandler.dispose();
        }
      });
    }
  }

  public void selectionChanged(SelectionChangedEvent event) {
    collectSelections(event, Result.class);
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    if (selectedObjects.size() > 0) {
      menuManager.add(new DownloadAction());
      menuManager.add(new ResultDetailAction());
      menuManager.add(new Separator());
      menuManager.add(new CopyNameAction());

      String[] linkList = new String[selectedObjects.size()];

      for (int i = 0; i < selectedObjects.size(); i++)
        linkList[i] = ((Result) selectedObjects.get(i)).getED2K();

      MenuManager clipboardMenu = new MenuManager(SResources.getString("m.d.copyTo"));
      clipboardMenu.add(new CopyED2KLinkToClipboardAction(false, linkList));
      clipboardMenu.add(new CopyED2KLinkToClipboardAction(true, linkList));
      menuManager.add(clipboardMenu);

      menuManager.add(new Separator());

      Result result = (Result) selectedObjects.get(0);
      addWebServicesMenu(menuManager, result.getMd4(), result.getED2K(), result.getSize());
    }
  }

  public boolean downloadResult(final Result result) {
    if (!Sancho.hasCollectionFactory())
      return true;

    boolean force = PreferenceLoader.loadBoolean("searchForceDownload");

    if (result.downloaded() && !force) {
      Shell shell = ((TableViewer) tableViewer).getTable().getShell();
      MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
      box.setText(SResources.getString("s.alreadyDownloadedTitle"));
      box.setMessage(result.getName() + "\n" + SResources.getString("s.alreadyDownloadedText"));

      if (box.open() == SWT.YES) {
        gView.getCore().getResultCollection().download(result, true);
        tableViewer.getTable().getDisplay().timerExec(2000, new Runnable() {
          public void run() {
            if (tableViewer != null && tableViewer.getTable() != null && !tableViewer.getTable().isDisposed())
              tableViewer.update(result, null);
          }
        });
        return true;
      }
    } else {
      gView.getCore().getResultCollection().download(result, force);
      tableViewer.getTable().getDisplay().timerExec(2000, new Runnable() {
        public void run() {
          if (tableViewer != null && tableViewer.getTable() != null && !tableViewer.getTable().isDisposed())
            tableViewer.update(result, null);
        }
      });
      return true;
    }
    return false;
  }

  public void downloadSingleFile(Result result) {
    downloadResult(result);
    postDownloadStats(1, SResources.S_ES);
  }

  // note ...:w
  public void downloadSelected() {
    if (!Sancho.hasCollectionFactory())
      return;

    String anErrorString = SResources.S_ES;
    int counter = 0;

    for (int i = 0; i < selectedObjects.size(); i++) {
      Result result = (Result) selectedObjects.get(i);

      if (downloadResult(result))
        counter++;
      else
        anErrorString += (result.getName() + "\n");
    }
    postDownloadStats(counter, anErrorString);
  }

  public void postDownloadStats(int counter, String anErrorString) {
    SearchTab searchTab = (SearchTab) cTabItem.getParent().getData();
    searchTab.getMainWindow().getStatusline().setText(SResources.getString("s.sl.startedDownload") + counter);
  }

  private class DownloadAction extends Action {
    public DownloadAction() {
      super(SResources.getString("s.r.download"));
      setImageDescriptor(SResources.getImageDescriptor("down_arrow_green"));
    }

    public void run() {
      downloadSelected();
    }
  }

  private class ResultDetailAction extends Action {

    public ResultDetailAction() {
      super(SResources.getString("s.r.resultDetails"));
      setImageDescriptor(SResources.getImageDescriptor("info"));
    }

    public void run() {
      Result result = (Result) selectedObjects.get(0);
      if (result instanceof Result) {
        new ResultDetailDialog(gView.getShell(), result).open();
      }
    }
  }

  private class CopyNameAction extends Action {
    public CopyNameAction() {
      super(SResources.getString("s.r.copyName"));
      setImageDescriptor(SResources.getImageDescriptor("copy"));
    }

    public void run() {
      String string = SResources.S_ES;
      String lSeparator = System.getProperty("line.separator");
      for (int i = 0; i < selectedObjects.size(); i++) {
        Result result = (Result) selectedObjects.get(i);
        if (string.length() > 0)
          string += lSeparator;
        string += result.getName();
      }
      MainWindow.copyToClipboard(string);
    }
  }

  private class ToolTipHandler implements IMenuListener {
    private Shell tipShell;
    private CLabel tipLabelImage;
    private Label tipLabelText;
    private Widget tipWidget;
    private Point tipPosition;
    private Composite parent;
    private List namesList;
    private Point pt;
    private Font boldFont;
    private ToolBar toolBar;
    private Composite namesComposite;
    private String fileSize;
    private String md4;
    private String ed2k;
    private int charHeight;
    private Result result;
    private MenuManager popupMenu;
    private Display display;
    private Timer timer;
    private boolean isVisible;

    public ToolTipHandler(final Composite parent) {
      this.parent = parent;
      this.pt = new Point(0, 0);

      GC gc = new GC(parent);
      charHeight = gc.getFontMetrics().getHeight();
      gc.dispose();

      display = parent.getDisplay();
      tipShell = new Shell(parent.getShell(), SWT.ON_TOP);
      tipShell.addDisposeListener(new DisposeListener() {
        public void widgetDisposed(DisposeEvent e) {
          if (boldFont != null)
            boldFont.dispose();
          if (popupMenu != null)
            popupMenu.dispose();
          if (timer != null)
            timer.cancel();
        }
      });

      // Don't close when you press ESC in the namesList
      tipShell.addListener(SWT.Close, new Listener() {
        public void handleEvent(Event e) {
          e.doit = false;
        }
      });

      tipShell.setLayout(WidgetFactory.createGridLayout(1, 2, 2, 0, 0, false));
      setColors(tipShell);

      tipLabelImage = new CLabel(tipShell, SWT.NONE);
      FontData[] fontDataArray = tipLabelImage.getFont().getFontData();

      for (int i = 0; i < fontDataArray.length; i++)
        fontDataArray[i].setStyle(SWT.BOLD);

      boldFont = new Font(null, fontDataArray);
      tipLabelImage.setFont(boldFont);
      tipLabelImage.setAlignment(SWT.LEFT);
      setColors(tipLabelImage);

      tipLabelImage
          .setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING));

      createSeparator(tipShell);

      tipLabelText = new Label(tipShell, SWT.NONE);
      setColors(tipLabelText);
      tipLabelText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));

      namesComposite = new Composite(tipShell, SWT.NONE);
      namesComposite.setLayout(WidgetFactory.createGridLayout(1, 0, 0, 0, 0, false));
      namesComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      createSeparator(namesComposite);

      namesList = new List(namesComposite, SWT.H_SCROLL | SWT.V_SCROLL);
      setColors(namesList);

      createSeparator(tipShell);

      Composite toolBarComposite = new Composite(tipShell, SWT.NONE);
      toolBarComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      toolBarComposite.setLayout(WidgetFactory.createGridLayout(3, 0, 0, 0, 0, false));
      toolBarComposite.setBackground(namesList.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

      ToolBar leftToolBar = new ToolBar(toolBarComposite, SWT.FLAT);
      setColors(leftToolBar);
      leftToolBar.setLayoutData(new GridData(GridData.BEGINNING));

      Composite strech = new Composite(toolBarComposite, SWT.NONE);
      setColors(strech);
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 1;
      strech.setLayoutData(gd);

      ToolItem toolItem = new ToolItem(leftToolBar, SWT.NONE);
      toolItem.setImage(SResources.getImage("down_arrow_green"));
      toolItem.setToolTipText(SResources.getString("s.download"));
      toolItem.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent s) {
          downloadSingleFile(result);
        }
      });

      toolBar = new ToolBar(toolBarComposite, SWT.FLAT);
      setColors(toolBar);
      toolBar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

      addToolItem("bitzi", "mi.web.bitzi", WebServicesAction.BITZI);
      //   addToolItem("jigle", "mi.web.jigle", WebServicesAction.JIGLE);
      addToolItem("edonkey", "mi.web.filedonkey", WebServicesAction.FILEDONKEY);
      //  addToolItem("sharereactor", "mi.web.srFakeCheck",
      // WebServicesAction.SHAREREACTOR);
      addToolItem("edonkey", "mi.web.donkeyFakes", WebServicesAction.DONKEY_FAKES);

      popupMenu = new MenuManager(SResources.S_ES);
      popupMenu.setRemoveAllWhenShown(true);
      popupMenu.addMenuListener(this);
      tipLabelText.setMenu(popupMenu.createContextMenu(tipShell));

    }

    public void createSeparator(Composite parent) {
      new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    public void setColors(Control control) {
      control.setBackground(control.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      control.setForeground(control.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
    }

    public void addToolItem(String imageString, String toolTipString, final int type) {
      ToolItem toolItem = new ToolItem(toolBar, SWT.NONE);
      toolItem.setImage(SResources.getImage(imageString));
      toolItem.setToolTipText(SResources.getString(toolTipString));
      toolItem.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent arg0) {
          String string;

          switch (type) {
            case WebServicesAction.JIGLE :
              string = fileSize + ":" + md4;
              break;
            case WebServicesAction.SHAREREACTOR :
            case WebServicesAction.DONKEY_FAKES :
              string = ed2k;
              break;
            default :
              string = md4;
              break;
          }

          WebServicesAction.launch(type, string);
          setVisible(false);
        }
      });
    }

    public void startTimer() {
      if (timer != null)
        timer.cancel();
      timer = new Timer();
      timer.scheduleAtFixedRate(new TimerTask() {
        public void run() {
          if (isVisible) // in other thread
            display.asyncExec(new Runnable() { // maybe sync?
                  public void run() {
                    if (tipShell != null && !tipShell.isDisposed() && tipShell.isVisible()
                        && !tipShell.getBounds().contains(display.getCursorLocation()))
                      setVisible(false);

                    //   display.post(SWT.MouseHover)
                    // now check if cursor in the right place and no tooltip and delay met
                  }
                });
        }
      }, 0L, 1000L);
    }

    public void dispose() {
      if (tipShell != null) {
        tipShell.dispose();
        tipShell = null;
      }
    }

    public void activateHoverHelp(final Control control, final ICustomViewer cViewer) {
      startTimer();
      control.addMouseListener(new MouseAdapter() {
        public void mouseDown(MouseEvent e) {
          if (tipShell.isVisible())
            setVisible(false);
        }
      });

      control.addMouseTrackListener(new MouseTrackAdapter() {
        public void mouseExit(MouseEvent e) {
          if (tipShell.isVisible() && ((pt.x != e.x) && (pt.y != e.y)))
            setVisible(false);
          tipWidget = null;
        }

        public void mouseHover(MouseEvent event) {
          if (event.widget == null)
            return;

          pt.x = event.x;
          pt.y = event.y;
          tipPosition = control.toDisplay(pt);

          // curs w/in tt
          Rectangle r = tipShell.getBounds();

          r.x -= 10;
          r.y -= 10;
          r.width += 20;
          r.height += 20;

          if (tipShell.isVisible() && r.contains(tipPosition))
            return;

          Widget widget = event.widget;

          Table table = (Table) widget;

          int goodx1 = 0;
          int goodx2 = 0;
          boolean found = false;

          for (int i = 0; i < table.getColumns().length; i++) {
            if (cViewer.getColumnIDs()[i] == ResultTableView.NAME) {
              goodx2 = goodx1 + table.getColumns()[i].getWidth();
              found = true;
              break;
            } else
              goodx1 += table.getColumns()[i].getWidth();
          }

          widget = table.getItem(pt);

          if (widget == null) {
            setVisible(false);
            tipWidget = null;
          }

          if (widget == tipWidget)
            return;

          tipWidget = widget;

          if (found && !((goodx1 < pt.x) && (pt.x < goodx2))) {
            setVisible(false);
            return;
          }

          // Create the tooltip on demand
          TableItem tableItem = (TableItem) widget;
          result = (Result) tableItem.getData();

          tipLabelImage.setImage(result.getToolTipImage());
          tipLabelImage.setText(result.getName());

          md4 = SResources.S_ES;
          ed2k = SResources.S_ES;

          md4 = result.getMd4().toUpperCase();
          ed2k = result.getED2K();

          fileSize = String.valueOf(result.getSize());

          // set the text/image for the tooltip
          tipLabelText.setText(result.getToolTipContent());

          namesList.removeAll();
          namesList.pack();

          GridData gridData = new GridData();
          gridData.heightHint = 0;
          gridData.widthHint = 0;
          namesList.setLayoutData(gridData);
          gridData = new GridData(GridData.FILL_HORIZONTAL);
          gridData.heightHint = 0;
          gridData.widthHint = 0;
          namesComposite.setLayoutData(gridData);

          //  namesComposite.pack();
          tipShell.pack();

          String[] originalNames = result.getNames();

          if ((originalNames != null) && (originalNames.length > 1)) {
            int numToDisplay = result.getNames().length;
            numToDisplay = (numToDisplay > 6) ? 6 : numToDisplay;

            String[] names = new String[originalNames.length];
            System.arraycopy(originalNames, 0, names, 0, originalNames.length);
            Arrays.sort(names, String.CASE_INSENSITIVE_ORDER);
            gridData = new GridData(GridData.FILL_HORIZONTAL);
            namesComposite.setLayoutData(gridData);
            gridData = new GridData();
            gridData.heightHint = numToDisplay * (charHeight);
            gridData.widthHint = tipShell.getBounds().width;
            namesList.setLayoutData(gridData);

            for (int i = 0; i < names.length; i++)
              namesList.add(names[i]);
          } else {
            gridData = new GridData();
            gridData.heightHint = 0;
            gridData.widthHint = 0;
            namesList.setLayoutData(gridData);

            gridData = new GridData(GridData.FILL_HORIZONTAL);
            gridData.heightHint = 0;
            namesComposite.setLayoutData(gridData);
          }

          // namesComposite.pack();
          tipShell.pack();
          setHoverLocation(tipShell, tipPosition);
          setVisible(true);
        }
      });
    }

    private void setVisible(boolean b) {
      tipShell.setVisible(b);
      isVisible = b;
    }

    private void setHoverLocation(Shell shell, Point position) {
      Rectangle displayBounds = shell.getDisplay().getBounds();
      Rectangle shellBounds = shell.getBounds();
      shellBounds.x = Math.max(Math.min(position.x, displayBounds.width - shellBounds.width), 0);
      shellBounds.y = Math.max(Math.min(position.y, displayBounds.height - shellBounds.height), 0);
      shell.setBounds(shellBounds);
    }

    public void menuAboutToShow(IMenuManager menuManager) {
      menuManager.add(new CopyToolTipToClipboardAction());
    }

    public class CopyToolTipToClipboardAction extends Action {

      public CopyToolTipToClipboardAction() {
        super();
        setText(SResources.getString("copy to clipboard"));
        setImageDescriptor(SResources.getImageDescriptor("copy"));
      }

      public void run() {
        if (result != null) {
          MainWindow.copyToClipboard(result.getToolTip());
        }
      }
    }

  }
}