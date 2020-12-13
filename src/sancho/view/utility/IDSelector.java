/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import sancho.core.Sancho;
import sancho.view.preferences.PreferenceLoader;

public class IDSelector extends Dialog {
  public static final int MAGIC_NUMBER = 65;
  private String[] legend;
  private String allIDs;
  private String leftIDs;
  private String rightIDs;
  private String prefOption;
  private String prefOptionOff;
  private String prefSuffix;
  private TableItem hoverTableItem;
  private Table table;
  private boolean dragging;

  public IDSelector(Shell parentShell, String[] legend, String prefOption, String prefSuffix) {
    super(parentShell);
    this.legend = legend;
    allIDs = SResources.S_ES;

    for (int i = 0; i < legend.length; i++)
      allIDs += String.valueOf((char) (IDSelector.MAGIC_NUMBER + i));

    this.prefSuffix = prefSuffix;
    this.prefOption = prefOption + prefSuffix;
    this.prefOptionOff = this.prefOption + "Off";

    String tmpString = PreferenceLoader.loadString(this.prefOption);
    leftIDs = PreferenceLoader.loadString(this.prefOptionOff);
    rightIDs = ((!tmpString.equals(SResources.S_ES)) ? tmpString : allIDs);

    for (int i = 0; i < allIDs.length(); i++) {
      if (leftIDs.indexOf(allIDs.charAt(i)) == -1) {
        if (rightIDs.indexOf(allIDs.charAt(i)) == -1)
          rightIDs += allIDs.charAt(i);
      }
    }
  }

  public Control createDialogArea(Composite oldParent) {
    Composite parent = (Composite) super.createDialogArea(oldParent);
    parent.setLayout(WidgetFactory.createGridLayout(2, 5, 5, 5, 5, false));

    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;

    Label l = new Label(parent, SWT.NONE);
    l.setText(SResources.getString("l.selectorInfo"));
    l.setLayoutData(gridData);

    createTable(parent);
    createButtons(parent);
    createDefault(parent);
    return parent;
  }

  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText(prefSuffix + " " + SResources.getString("l.selector"));
    shell.setImage(SResources.getImage("preferences"));
  }

  protected void createDefault(Composite parent) {
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 2;
    Button moveUp = new Button(parent, SWT.NONE);
    moveUp.setText(SResources.getString("l.default"));
    moveUp.setLayoutData(gd);
    moveUp.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        rightIDs = allIDs;
        leftIDs = SResources.S_ES;
        table.removeAll();
        createItems();
      }
    });
  }

  public void createTable(Composite parent) {
    table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE | SWT.CHECK);
    table.setLayoutData(new GridData(GridData.FILL_BOTH));

    Listener shellListener = new Listener() {
      public void handleEvent(Event e) {
        switch (e.type) {
          case SWT.MouseDown :
            onMouseDown(e);
            break;
          case SWT.MouseMove :
            onMouseMove(e);
            break;
          case SWT.MouseUp :
            onMouseUp(e);
        }
      }
    };
    int[] shellEvents = new int[]{SWT.MouseDown, SWT.MouseUp, SWT.MouseMove};

    for (int i = 0; i < shellEvents.length; i++)
      table.addListener(shellEvents[i], shellListener);

    createItems();
  }

  public void onMouseMove(Event e) {
    Table table = (Table) e.widget;
    TableItem tableItem = table.getItem(new Point(e.x, e.y));
    if (tableItem != null) {
      if (hoverTableItem != null) {
        if (hoverTableItem != tableItem)
          hoverTableItem.setBackground(null);
      }
      if (dragging) {
        hoverTableItem = tableItem;
        hoverTableItem.setBackground(table.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
      }

    } else {
      dragging = false;
      if (hoverTableItem != null && !hoverTableItem.isDisposed()) {
        hoverTableItem.setBackground(null);
        hoverTableItem = null;
      }
    }
  }

  public void onMouseUp(Event e) {
    Table table = (Table) e.widget;
    TableItem tableItem = table.getItem(new Point(e.x, e.y));

    if (dragging) {
      if (hoverTableItem != null && table.getSelection().length > 0) {

        TableItem selectedTableItem = table.getSelection()[0];
        if (selectedTableItem != tableItem) {
          int selID = table.getSelectionIndex();

          boolean isChecked = selectedTableItem.getChecked();
          String ID = (String) selectedTableItem.getData("ID");
          int IDnum = ID.charAt(0) - IDSelector.MAGIC_NUMBER;

          table.remove(selID);
          int targetID = table.indexOf(hoverTableItem) + 1;

          TableItem newTableItem = new TableItem(table, SWT.NONE, targetID);
          newTableItem.setData("ID", ID);
          newTableItem.setText(SResources.getString(legend[IDnum]));
          newTableItem.setChecked(isChecked);

          table.setSelection(targetID);
          hoverTableItem.setBackground(null);
          hoverTableItem = null;

        }
      }
      dragging = false;
    }
  }

  public void onMouseDown(Event e) {
    Table table = (Table) e.widget;
    TableItem tableItem = table.getItem(new Point(e.x, e.y));

    if (tableItem != null) {
      dragging = true;
      hoverTableItem = null;
    }
  }

  public void createItems() {

    for (int i = 0; i < rightIDs.length(); i++) {
      TableItem tableItem = new TableItem(table, SWT.NONE);
      int IDnum = rightIDs.charAt(i) - IDSelector.MAGIC_NUMBER;
      tableItem.setData("ID", SResources.S_ES + rightIDs.charAt(i));
      tableItem.setText(SResources.getString(legend[IDnum]));
      tableItem.setChecked(true);
    }

    for (int i = 0; i < leftIDs.length(); i++) {
      TableItem tableItem = new TableItem(table, SWT.NONE);
      int IDnum = leftIDs.charAt(i) - IDSelector.MAGIC_NUMBER;
      tableItem.setData("ID", SResources.S_ES + leftIDs.charAt(i));
      tableItem.setText(SResources.getString(legend[IDnum]));
    }

  }

  public void createButtons(Composite parent) {
    Composite buttonComposite = new Composite(parent, SWT.NONE);
    buttonComposite.setLayout(WidgetFactory.createGridLayout(1, 0, 0, 5, 5, false));
    buttonComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

    Button moveUp = new Button(buttonComposite, SWT.NONE);
    moveUp.setText(SResources.getString("l.up"));
    moveUp.setLayoutData(new GridData(GridData.FILL_BOTH));
    moveUp.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        int index;
        if ((index = table.getSelectionIndex()) > 0)
          moveItem(index, -1);
      }
    });

    Button moveDown = new Button(buttonComposite, SWT.NONE);
    moveDown.setText(SResources.getString("l.down"));
    moveDown.setLayoutData(new GridData(GridData.FILL_BOTH));
    moveDown.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        int index = table.getSelectionIndex();
        if (index  < table.getItemCount() - 1 && index > -1)
          moveItem(index, 1);
      }
    });
  }

  public void moveItem(int index, int increment) {
    TableItem tableItem = table.getItem(index);
    boolean isChecked = tableItem.getChecked();
    String ID = (String) tableItem.getData("ID");
    int IDnum = ID.charAt(0) - IDSelector.MAGIC_NUMBER;
    table.remove(index);

    TableItem newTableItem = new TableItem(table, SWT.NONE, index + increment);
    newTableItem.setData("ID", ID);
    newTableItem.setText(SResources.getString(legend[IDnum]));
    newTableItem.setChecked(isChecked);

    table.setSelection(index + increment);
  }

  /**
   * savePrefs
   */
  public void savePrefs() {
    if (rightIDs.length() > 1) {
      PreferenceStore p = PreferenceLoader.getPreferenceStore();
      p.setValue(prefOption, rightIDs);
      p.setValue(prefOptionOff, leftIDs);
      PreferenceLoader.saveStore();
    }

    if (Sancho.getCore() != null)
      Sancho.getCore().updatePreferences();
  }

  /**
   * refreshLists
   */
  public void refreshLists() {
    TableItem[] tableItems = table.getItems();
    leftIDs = SResources.S_ES;
    rightIDs = SResources.S_ES;
    for (int i = 0; i < tableItems.length; i++) {
      if (tableItems[i].getChecked())
        rightIDs += (String) tableItems[i].getData("ID");
      else
        leftIDs += (String) tableItems[i].getData("ID");
    }
  }

  protected void buttonPressed(int buttonId) {
    refreshLists();
    super.buttonPressed(buttonId);
  }

  public static String createIDString(String[] legend) {
    String result = "";

    for (int i = 0; i < legend.length; i++)
      result += String.valueOf((char) (IDSelector.MAGIC_NUMBER + i));

    return result;
  }

  public static String getID(int i) {
    return String.valueOf((char) (IDSelector.MAGIC_NUMBER + i));
  }

  public static String loadIDs(String prefString, String allIDs) {
    String prefStringOff = prefString + "Off";

    String savedIDs = PreferenceLoader.loadString(prefString);
    String savedOffIDs = PreferenceLoader.loadString(prefStringOff);

    for (int i = 0; i < savedIDs.length(); i++) {
      if (allIDs.indexOf(savedIDs.charAt(i)) == -1) {
        PreferenceStore p = PreferenceLoader.getPreferenceStore();
        p.setValue(prefString, allIDs);
        p.setValue(prefStringOff, "");
        return allIDs;
      }
    }
    
    for (int i = 0; i < savedOffIDs.length(); i++) {
      if (allIDs.indexOf(savedOffIDs.charAt(i)) == -1) {
        PreferenceStore p = PreferenceLoader.getPreferenceStore();
        savedOffIDs="";
        p.setValue(prefStringOff, "");
      }
    }
    

    for (int i = 0; i < allIDs.length(); i++) {
      if ((savedIDs.indexOf(allIDs.charAt(i)) == -1) && (savedOffIDs.indexOf(allIDs.charAt(i)) == -1))
        savedIDs += allIDs.charAt(i);
    }

    String tmp = "";
    for (int i = 0; i < savedIDs.length(); i++) {
      if ((savedOffIDs.indexOf(savedIDs.charAt(i)) == -1))
        tmp += savedIDs.charAt(i);
    }

    savedIDs = tmp;

    PreferenceStore p = PreferenceLoader.getPreferenceStore();
    p.setValue(prefString, savedIDs);
    p.setValue(prefStringOff, savedOffIDs);
    
    //  PreferenceLoader.saveStore();

    return savedIDs;
  }

}