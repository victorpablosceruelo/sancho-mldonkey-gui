/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import sancho.core.Sancho;
import sancho.utility.SwissArmy;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.transfer.UniformResourceLocator;
import sancho.view.viewer.actions.CTabFolderTabsAction;

public class WidgetFactory {
  public static CTabFolder createCTabFolder(Composite parent) {
    return createCTabFolder(parent, SWT.NONE);
  }

  public static CTabFolder createCTabFolder(Composite parent, int style) {
    CTabFolder cTabFolder = new CTabFolder(parent, style | SWT.FLAT);
    Display display = cTabFolder.getDisplay();

    Color titleBackground = display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND);
    Color lightTitleBackground = changeColor(titleBackground.getRGB(), 20);

    if (PreferenceLoader.loadBoolean("useGradient")) {
      cTabFolder.setSelectionBackground(new Color[]{titleBackground, lightTitleBackground}, new int[]{100});
      cTabFolder.setSelectionForeground(display.getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
    }

    return cTabFolder;
  }

  public static Color changeColor(RGB color, int num) {
    int r = modifyIntColor(color.red, num);
    int g = modifyIntColor(color.green, num);
    int b = modifyIntColor(color.blue, num);

    return new Color(null, r, g, b);
  }

  public static Color changeColor(RGB color, int num, int def) {

    int r = modifyIntColor(color.red, num, def);
    int g = modifyIntColor(color.green, num, def);
    int b = modifyIntColor(color.blue, num, def);

    return new Color(null, r, g, b);
  }

  public static int modifyIntColor(int i, int num) {
    return modifyIntColor(i, num, i);
  }

  public static int modifyIntColor(int i, int num, int def) {
    return (((i + num) >= 0) && ((i + num) <= 255)) ? (i + num) : def;
  }

  /**
   * @param parent
   * @return ViewForm
   */
  public static MyViewForm createViewForm(Composite parent, boolean forceFlat) {
    return new MyViewForm(parent, SWT.BORDER
        | (PreferenceLoader.loadBoolean("flatInterface") || forceFlat ? SWT.FLAT : SWT.NONE));
  }

  /**
   * @param numColumns
   * @param marginWidth
   * @param marginHeight
   * @param horizontalSpacing
   * @param verticalSpacing
   * @param makeColumnsEqualWidth
   * @return GridLayout
   */
  public static GridLayout createGridLayout(int numColumns, int marginWidth, int marginHeight,
      int horizontalSpacing, int verticalSpacing, boolean makeColumnsEqualWidth) {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = numColumns;
    gridLayout.marginWidth = marginWidth;
    gridLayout.marginHeight = marginHeight;
    gridLayout.horizontalSpacing = horizontalSpacing;
    gridLayout.verticalSpacing = verticalSpacing;
    gridLayout.makeColumnsEqualWidth = makeColumnsEqualWidth;

    return gridLayout;
  }

  public static GridData createGridData(int style, int widthHint, int heightHint) {
    GridData gridData = style == SWT.NONE ? new GridData() : new GridData(style);
    gridData.widthHint = widthHint;
    gridData.heightHint = heightHint;
    return gridData;
  }

  public static RowLayout createRowLayout(boolean wrap, boolean pack, boolean justify, int type,
      int marginLeft, int marginTop, int marginRight, int marginBottom, int spacing) {
    RowLayout rowLayout = new RowLayout();
    rowLayout.wrap = wrap;
    rowLayout.pack = pack;
    rowLayout.justify = justify;
    rowLayout.type = type;
    rowLayout.marginLeft = marginLeft;
    rowLayout.marginTop = marginTop;
    rowLayout.marginRight = marginRight;
    rowLayout.marginBottom = marginBottom;
    rowLayout.spacing = spacing;

    return rowLayout;
  }

  /**
   * @param parent
   * @param text
   * @param image
   * @return CLabel
   */
  public static CLabel createCLabel(Composite parent, String text, String image) {
    // GTK/SWT3-M4: (<unknown>:5346): GLib-GObject-CRITICAL **: file
    // gtype.c: line 1942 (g_type_add_interface_static): assertion
    // `g_type_parent (interface_type) == G_TYPE_INTERFACE' failed
    CLabel cLabel = new CLabel(parent, SWT.LEFT);
    cLabel.setFont(PreferenceLoader.loadFont("headerFontData"));
    cLabel.setText(SResources.getString(text));

    cLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    cLabel.setImage(SResources.getImage(image));
    // cLabel.setBackground(cLabel.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
    // cLabel.setForeground(cLabel.getDisplay().getSystemColor(SWT.COLOR_TITLE_FOREGROUND));

    if (PreferenceLoader.loadBoolean("useGradient")) {
      cLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
      cLabel.setBackground(new Color[]{parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND),
          parent.getBackground()}, new int[]{100});
    }

    return cLabel;
  }

  /**
   * Create a sashForm, restoring its preferences
   * 
   * @param parent
   * @param prefString
   * @return SashForm
   */
  public static SashForm createSashForm(Composite parent, String prefString) {
    final String orientationPrefString = prefString + "Orientation";

    int orientation = PreferenceLoader.loadInt(orientationPrefString);

    if ((orientation != SWT.HORIZONTAL) && (orientation != SWT.VERTICAL)) {
      PreferenceStore p = PreferenceLoader.getPreferenceStore();
      orientation = p.getDefaultInt(orientationPrefString);
    }

    final SashForm sashForm = new SashForm(parent, orientation);
    sashForm.setData("prefString", prefString);

    sashForm.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        PreferenceStore p = PreferenceLoader.getPreferenceStore();
        p.setValue(orientationPrefString, sashForm.getOrientation());
      }
    });

    return sashForm;
  }

  /**
   * Load the weights & maximized control # of a sash (call after the sash has
   * children)
   * 
   * @param sashForm
   * @param prefString
   */
  public static void loadSashForm(SashForm sashForm, String prefString) {
    final String sashChildPrefString = prefString + "Child";
    int maximizeControl = PreferenceLoader.loadInt(prefString + "Maximized");

    // First set the sash weights if available
    if (sashPrefsExist(sashForm, prefString)) {
      int[] weights = new int[sashForm.getChildren().length];

      for (int i = 0; i < sashForm.getChildren().length; i++) {
        Rectangle bounds = PreferenceLoader.loadRectangle(sashChildPrefString + i);
        weights[i] = (sashForm.getOrientation() == SWT.HORIZONTAL) ? bounds.width : bounds.height;
      }

      // Weights can not be all 0
      for (int i = 0; i < weights.length; i++) {
        if (weights[i] > 0) {
          sashForm.setWeights(weights);

          break;
        }
      }
    }

    // Then check for maximize (weights still in effect)
    if ((maximizeControl > -1) && (maximizeControl <= sashForm.getChildren().length))
      sashForm.setMaximizedControl(sashForm.getChildren()[maximizeControl]);

    final PreferenceStore p = PreferenceLoader.getPreferenceStore();

    // Save the control size
    for (int i = 0; i < sashForm.getChildren().length; i++) {
      final Control control = sashForm.getChildren()[i];
      final int childNumber = i;
      control.addDisposeListener(new DisposeListener() {
        public void widgetDisposed(DisposeEvent e) {
          Control aControl = (Control) e.widget;
          if ((aControl.getBounds().width > 10) && (aControl.getBounds().height > 10))
            PreferenceConverter.setValue(p, sashChildPrefString + childNumber, aControl.getBounds());
        }
      });

    }
  }

  /**
   * @param sashForm
   * @param prefString
   * @return true if all sashChildren preferences exist
   */
  public static boolean sashPrefsExist(SashForm sashForm, String prefString) {
    PreferenceStore p = PreferenceLoader.getPreferenceStore();

    for (int i = 0; i < sashForm.getChildren().length; i++) {
      if (!p.contains(prefString + "Child" + i))
        return false;
    }

    return true;
  }

  /**
   * Save the maximized control # of a sash
   * 
   * @param sashForm
   * @param control
   */

  public static boolean setMaximizedSashFormControl(SashForm sashForm, int controlNumber) {
    if (controlNumber > sashForm.getChildren().length)
      return false;
    return (setMaximizedSashFormControl(sashForm, sashForm.getChildren()[controlNumber]));
  }

  public static boolean setMaximizedSashFormControl(SashForm sashForm, Control control) {
    PreferenceStore p = PreferenceLoader.getPreferenceStore();
    String maximizedPrefString = null;

    if (sashForm.getData("prefString") != null)
      maximizedPrefString = (String) sashForm.getData("prefString") + "Maximized";

    if (sashForm.getMaximizedControl() == null) {
      sashForm.setMaximizedControl(control);

      if (maximizedPrefString != null) {
        for (int i = 0; i < sashForm.getChildren().length; i++) {
          if (control == sashForm.getChildren()[i]) {
            p.setValue(maximizedPrefString, i);

            break;
          }
        }
      }
      return true;
    } else {
      sashForm.setMaximizedControl(null);

      if (maximizedPrefString != null)
        p.setValue(maximizedPrefString, -1);
      return false;
    }
  }

  public static void createLinkDropTarget(Control control) {
    DropTarget dropTarget = new DropTarget(control, DND.DROP_COPY | DND.DROP_DEFAULT | DND.DROP_LINK);
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
        if ((event.data == null))
          return;

        SwissArmy.sendLink(Sancho.getCore(), (String) event.data);
      }
    });
  }

  public static void addCTabFolderMenu(CTabFolder cTabFolder, String prefString) {

    MenuManager popupMenu = new MenuManager();
    popupMenu.setRemoveAllWhenShown(true);
    popupMenu.addMenuListener(new CTabFolderMenuListener(cTabFolder, prefString));
    cTabFolder.setMenu(popupMenu.createContextMenu(cTabFolder));
  }

  static class CTabFolderMenuListener implements IMenuListener {

    CTabFolder cTabFolder;
    String prefString;

    public CTabFolderMenuListener(CTabFolder cTabFolder, String prefString) {
      this.cTabFolder = cTabFolder;
      this.prefString = prefString;
    }

    public void menuAboutToShow(IMenuManager manager) {
      manager.add(new CTabFolderTabsAction(cTabFolder, prefString));
    }
  }
}