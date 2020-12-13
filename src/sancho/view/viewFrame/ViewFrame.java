/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.viewFrame;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import sancho.core.ICore;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.HeaderBarMouseAdapter;
import sancho.view.utility.MyViewForm;
import sancho.view.utility.NoDuplicatesCombo;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;
import sancho.view.viewer.GView;

public class ViewFrame {
  protected boolean active;
  protected AbstractTab aTab;
  protected Composite childComposite;
  protected CLabel cLabel;
  protected ToolItem clearRefineToolItem;
  protected GView gView;
  protected MenuManager menuManager;
  protected Composite parent;
  protected String prefString;
  protected Combo refineText;
  protected ToolBar toolBar;
  protected MyViewForm viewForm;
  protected boolean visible;

  public ViewFrame(Composite composite, String prefString, String prefImageString, AbstractTab aTab) {
    this(composite, prefString, prefImageString, aTab, false);
  }

  public ViewFrame(Composite composite, String prefString, String prefImageString, AbstractTab aTab,
      boolean forceFlat) {
    this.parent = composite;
    this.aTab = aTab;
    this.prefString = prefString;

    viewForm = WidgetFactory.createViewForm(parent, forceFlat);

    childComposite = new Composite(viewForm, SWT.NONE);
    childComposite.setLayout(new FillLayout());

    cLabel = WidgetFactory.createCLabel(viewForm, prefString, prefImageString);

    viewForm.setContent(childComposite);
    viewForm.setTopLeft(cLabel);
  }

  public void addPopupMenu(ToolBar toolBar) {
    final MenuManager popupMenu = new MenuManager();
    popupMenu.setRemoveAllWhenShown(true);
    popupMenu.addMenuListener(new RefineMenuListener());
    toolBar.setMenu(popupMenu.createContextMenu(toolBar));
  }

  public void setRefineText(String string) {
    if (refineText != null)
      refineText.setText(string);
  }

  public void addRefine() {

    clearRefineToolItem = new ToolItem(toolBar, SWT.NONE);
    clearRefineToolItem.setImage(SResources.getImage("refine"));
    clearRefineToolItem.setToolTipText(SResources.getString("ti.clearRefine"));
    clearRefineToolItem.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        if (refineText != null) {
          refineText.add(refineText.getText(), 0);
          refineText.setText(SResources.S_ES);
        }

        if (getGView() != null)
          getGView().setRefineString(SResources.S_ES);
      }
    });
    addPopupMenu(toolBar);

    ToolItem sep = new ToolItem(toolBar, SWT.SEPARATOR);

    refineText = new NoDuplicatesCombo(toolBar, SWT.BORDER);
    refineText.setItems(PreferenceLoader.loadStringArray(prefString + ".refineSArray"));
    refineText.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        NoDuplicatesCombo combo = (NoDuplicatesCombo) e.widget;
        PreferenceLoader.setValue(prefString + ".refineSArray", combo.getItems(), 25);
      }
    });

    refineText.setToolTipText(SResources.getString("ti.refine"));
    refineText.setSize(75, SWT.DEFAULT);
    
    if (SWT.getPlatform().equals("fox"))
      sep.setControl(refineText);
    
    sep.setWidth(75);
    refineText.pack();
    
    if (!SWT.getPlatform().equals("fox"))
      sep.setControl(refineText);

    refineText.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        NoDuplicatesCombo combo = (NoDuplicatesCombo) e.widget;

        if (combo.getSelectionIndex() > -1)
          refineText.setText(combo.getItem(combo.getSelectionIndex()));

        if (getGView() != null)
          getGView().setRefineString(refineText.getText());
      }
    });

    if (SWT.getPlatform().equals("fox")) {
      refineText.setSize(75, refineText.getSize().y);
      refineText.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          updateRefine(e);
        }
      });
    } else {
      refineText.addKeyListener(new KeyAdapter() {
        public void keyReleased(KeyEvent e) {
          updateRefine(e);
        }
      });
    }
    if (getGView() == null) {
      refineText.setEnabled(false);
      clearRefineToolItem.setEnabled(false);
    }
  }

  public ToolItem addToolItem(String resToolTipString, String resImageString,
      SelectionListener selectionListener) {
    ToolItem toolItem = new ToolItem(toolBar, SWT.NONE);
    toolItem.setToolTipText(SResources.getString(resToolTipString));
    toolItem.setImage(SResources.getImage(resImageString));
    toolItem.addSelectionListener(selectionListener);
    return toolItem;
  }

  public void addToolSeparator() {
    ToolItem toolItem = new ToolItem(toolBar, SWT.SEPARATOR);
  }

  public void createViewListener(ViewListener viewFrameListener) {
    setupViewListener(viewFrameListener);
    cLabel.addMouseListener(new HeaderBarMouseAdapter(cLabel, menuManager));
  }

  public void createViewToolBar() {
    Composite tComposite = new Composite(viewForm, SWT.NONE);
    tComposite.setLayout(WidgetFactory.createGridLayout(1, 1, 1, 0, 0, false));

    toolBar = new ToolBar(tComposite, SWT.RIGHT | SWT.FLAT);
    //toolBar.setBackground(toolBar.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
    viewForm.setTopRight(tComposite);
  }

  public Composite getChildComposite() {
    return childComposite;
  }

  public CLabel getCLabel() {
    return cLabel;
  }

  public Control getControl() {
    return getViewForm();
  }

  public ICore getCore() {
    return getGuiTab().getMainWindow().getCore();
  }

  public AbstractTab getGuiTab() {
    return aTab;
  }

  public GView getGView() {
    return gView;
  }

  public Composite getParent() {
    return parent;
  }

  public MyViewForm getViewForm() {
    return viewForm;
  }

  public boolean isActive() {
    return active;
  }

  public boolean isVisible() {
    return visible;
  }

  public void onConnect() {
    if (gView != null)
      gView.setInput();
  }

  public void onDisconnect() {
    if (gView != null)
      gView.unsetInput();

    resetLabel();
  }

  public void resetLabel() {
    if (cLabel != null)
      cLabel.setText(SResources.getString(prefString));
  }

  public void setActive(boolean b) {
    active = b;
    if (gView != null)
      gView.setActive(b);
  }

  protected void setupViewListener(ViewListener viewFrameListener) {
    menuManager = new MenuManager(SResources.S_ES);
    menuManager.setRemoveAllWhenShown(true);
    menuManager.addMenuListener(viewFrameListener);

    cLabel.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        menuManager.dispose();
      }
    });
  }

  public void setVisible(boolean b) {
    visible = b;
    if (gView != null)
      gView.setVisible(b);
  }

  public void updateCLabelText(String string) {
    if ((cLabel != null) && !cLabel.isDisposed())
      if (!cLabel.getText().equals(string))
        cLabel.setText(string);
  }

  public void updateCLabelTextInGuiThread(final String string) {
    if ((cLabel != null) && !cLabel.isDisposed())
      cLabel.getDisplay().asyncExec(new Runnable() {
        public void run() {
          updateCLabelText(string);
        }
      });

  }

  public void updateCLabelToolTip(String string) {
    if ((cLabel != null) && !cLabel.isDisposed())
      cLabel.setToolTipText(string);
  }

  public void updateCLabelToolTipInGuiThread(final String string) {
    if ((cLabel != null) && !cLabel.isDisposed())
      cLabel.getDisplay().asyncExec(new Runnable() { // was sync
        public void run() {
          updateCLabelToolTip(string);
        }
      });

  }

  public void updateDisplay() {
    cLabel.setFont(PreferenceLoader.loadFont("headerFontData"));

    if (gView != null)
      gView.updateDisplay();
  }

  public void updateRefine(KeyEvent e) {
    switch (e.keyCode) {
      case SWT.ARROW_DOWN :
      case SWT.ARROW_UP :
      case SWT.ARROW_LEFT :
      case SWT.ARROW_RIGHT :
      case SWT.PAGE_DOWN :
      case SWT.PAGE_UP :
      case SWT.CR :
        break;

      default :

        if (getGView() != null)
          getGView().setRefineString(refineText.getText());
    }
  }

  static class RefineMenuListener implements IMenuListener {
    public void menuAboutToShow(IMenuManager menuManager) {
      menuManager.add(new ToggleRefineAction("mi.refineFilterNegation", "refineFilterNegation"));
      menuManager.add(new ToggleRefineAction("mi.refineFilterAlternates", "refineFilterAlternates"));
    }
  }

  static class ToggleRefineAction extends Action {

    String prefString;

    public ToggleRefineAction(String resString, String prefString) {
      super(SResources.getString(resString), Action.AS_CHECK_BOX);
      this.prefString = prefString;
    }

    public boolean isChecked() {
      return PreferenceLoader.loadBoolean(prefString);
    }

    public void run() {
      PreferenceLoader.getPreferenceStore().setValue(prefString, !isChecked());
      PreferenceLoader.saveStore();
    }
  }
}