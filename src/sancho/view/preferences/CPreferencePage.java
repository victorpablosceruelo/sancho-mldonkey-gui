/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.preferences;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import sancho.view.utility.SResources;

public class CPreferencePage extends PreferencePage {
  protected List editorList = new ArrayList();

  public CPreferencePage(String title) {
    super(title);
  }

  protected Control createContents(Composite parent) {
    return null;
  }

  protected void contributeButtons(Composite parent) {
    parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    ((GridLayout) parent.getLayout()).numColumns++;

    Label x = new Label(parent, SWT.NONE);
    x.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    x.setText(SResources.getString("p.restart"));
  }

  protected void setupEditor(FieldEditor fieldEditor, Composite composite) {
    editorList.add(fieldEditor);

    if (fieldEditor.getNumberOfControls() < 3)
      fieldEditor.fillIntoGrid(composite, 3);

    fieldEditor.setPreferencePage(this);
    fieldEditor.setPreferenceStore(getPreferenceStore());
    fieldEditor.load();
  }

  protected void setupColorEditor(String prefString, String resString, Composite composite) {
    setupEditor(new ColorFieldEditor(prefString, SResources.getString(resString), composite), composite);
  }

  protected void setupFontEditor(String prefString, String resString, Composite composite) {
    setupEditor(new FontFieldEditor(prefString, SResources.getString(resString), composite), composite);
  }

  protected void setupBooleanEditor(String prefString, String resString, Composite composite) {
    setupEditor(new BooleanFieldEditor(prefString, SResources.getString(resString), composite), composite);
  }

  protected void setupIntegerEditor(String prefString, String resString, int x, int y, Composite composite) {
    IntegerFieldEditor intEditor = new IntegerFieldEditor(prefString, SResources.getString(resString),
        composite);
    intEditor.setValidRange(x, y);
    setupEditor(intEditor, composite);
  }

  protected void setupStringEditor(String prefString, String resString, char echoChar, Composite composite) {
    setupStringEditor(prefString, SResources.S_ES, resString, echoChar, composite);
  }

  protected void setupStringEditor(String prefString, String prefix, String resString, char echoChar,
      Composite composite) {
    StringFieldEditor stringEditor = new StringFieldEditor(prefString, prefix
        + SResources.getString(resString), composite);

    if (echoChar != '0')
      stringEditor.getTextControl(composite).setEchoChar(echoChar);

    setupEditor(stringEditor, composite);
  }

  protected void setupDirectoryEditor(String prefString, String resString, Composite composite) {
    setupEditor(new GCJDirectoryFieldEditor(prefString, SResources.getString(resString), composite), composite);
  }

  protected void setupFileEditor(String prefString, String resString, String[] extensions, Composite composite) {
    GCJFileFieldEditor fileEditor = new GCJFileFieldEditor(prefString, SResources.getString(resString), false,
        composite, true);
    fileEditor.setFileExtensions(extensions);
    setupEditor(fileEditor, composite);
  }

  protected void createSeparator(Composite composite) {
    Label s = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    s.setLayoutData(gridData);
  }

  protected void createInformationLabel(Composite composite, String resString) {
    Label s = new Label(composite, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    s.setLayoutData(gridData);
    s.setText(SResources.getString(resString));
  }

  protected Composite createNewTab(TabFolder tabFolder, String resString) {
    Composite composite = new Composite(tabFolder, SWT.NONE);
    TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
    tabItem.setControl(composite);
    tabItem.setText(SResources.getString(resString));

    return composite;
  }

  protected void performDefaults() {
    if (editorList != null)
      for (Iterator i = editorList.iterator(); i.hasNext();)
        ((FieldEditor) i.next()).loadDefault();
    super.performDefaults();
  }

  public boolean performOk() {
    if (editorList != null)
      for (Iterator i = editorList.iterator(); i.hasNext();)
        ((FieldEditor) i.next()).store();
    return super.performOk();
  }
}
