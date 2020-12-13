/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import sancho.model.mldonkey.Option;
import sancho.model.mldonkey.enums.EnumTagType;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public class MLDonkeyPreferencePage extends FieldEditorPreferencePage {
  private static final int inputFieldLength = 20;
  private boolean empty;
  private List options = new ArrayList();

  protected MLDonkeyPreferencePage(String title, int style) {
    super(title, style);
  }

  public void addOption(Option option) {
    options.add(option);
  }

  protected void contributeButtons(Composite parent) {
    parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    ((GridLayout) parent.getLayout()).numColumns++;

    Label x = new Label(parent, SWT.NONE);
    x.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    x.setText(SResources.getString("p.mouseOverHelp"));
  }

  protected Control createContents(Composite myparent) {
    Composite parent;
    if (this.empty) {
      parent = (Composite) super.createContents(myparent);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));
      Label emptyLabel = new Label(parent, SWT.NONE);
      emptyLabel.setText(SResources.getString("p.empty"));
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.verticalAlignment = GridData.CENTER;
      gd.horizontalAlignment = GridData.CENTER;
      emptyLabel.setLayoutData(gd);
      parent.layout();
    } else {
      Composite group = new Composite(myparent, SWT.BORDER);
      GridLayout gridLayout = WidgetFactory.createGridLayout(1, 5, 5, 5, 5, false);
      group.setLayout(gridLayout);
      group.setLayoutData(new GridData(GridData.FILL_BOTH));

      ScrolledComposite sc = new ScrolledComposite(group, SWT.H_SCROLL | SWT.V_SCROLL) {
        public Point computeSize(int wHint, int hHint, boolean changed) {
          // http://dev.eclipse.org/newslists/news.eclipse.tools/msg03994.html
          return new Point(SWT.DEFAULT, SWT.DEFAULT);
        }
      };

      sc.setLayoutData(new GridData(GridData.FILL_BOTH));
      sc.setLayout(new FillLayout());

      parent = (Composite) super.createContents(sc);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));
      sc.setExpandHorizontal(true);
      sc.setExpandVertical(true);
      sc.setContent(parent);

      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      sc.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      parent.layout();
    }

    return parent;
  }

  protected void createFieldEditors() {
    Collections.sort(options, new OptionsComparator());
    Composite parent;

    for (Iterator it = options.iterator(); it.hasNext();) {
      parent = getFieldEditorParent();

      Option temp = (Option) it.next();

      if ((temp.getType() == EnumTagType.BOOL) || isBoolean(temp.getValue())) {
        String optionHelp = temp.getDescription();
        if (optionHelp.equals(SResources.S_ES))
          optionHelp = temp.getName();

        setupEditor(parent, new BooleanFieldEditor(temp.getName(), temp.getName(),
            BooleanFieldEditor.SEPARATE_LABEL, parent), optionHelp);
      } else if ((temp.getType() == EnumTagType.INT) || isInteger(temp.getValue())) {
        String optionHelp = temp.getDescription();

        if (optionHelp.equals(SResources.S_ES))
          optionHelp = temp.getName();

        IntegerFieldEditor iFE =  new IntegerFieldEditor(temp.getName(), temp.getName(), parent) {
          protected void doFillIntoGrid(Composite parent, int numColumns) {
            // StringEditor#doFillIntoGrid
            getLabelControl(parent);
            Text textField = getTextControl(parent);
            GridData gd = new GridData();
            gd.horizontalSpan = numColumns - 1;
            GC gc = new GC(textField);
            Point extent = gc.textExtent("X");
            gd.widthHint = inputFieldLength * extent.x;
            gc.dispose();
            textField.setLayoutData(gd);
          }
        };
        
        iFE.setValidRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
        setupEditor(parent, iFE, optionHelp);
      } else {
        String optionHelp = temp.getDescription();

        if (optionHelp.equals(SResources.S_ES))
          optionHelp = temp.getName();

        setupEditor(parent, new StringFieldEditor(temp.getName(), temp.getName(), inputFieldLength, parent),
            optionHelp);
      }
    }
  }

  private boolean isBoolean(String string) {
    return string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false");
  }

  private boolean isInteger(String string) {
    try {
      int value = Integer.parseInt(string);
      return ((value >= Integer.MIN_VALUE) && (value <= Integer.MAX_VALUE));
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public void setEmpty(boolean empty) {
    this.empty = empty;
  }

  private void setupEditor(Composite parent, FieldEditor e, String optionHelp) {
    e.setPreferencePage(this);
    e.setPreferenceStore(getPreferenceStore());
    e.getLabelControl(parent).setToolTipText(optionHelp);
    e.load();
    addField(e);
  }

  static class OptionsComparator implements Comparator {
    public int compare(Object o1, Object o2) {
      Option option1 = (Option) o1;
      Option option2 = (Option) o2;
      return option1.getName().compareToIgnoreCase(option2.getName());
    }
  }
}