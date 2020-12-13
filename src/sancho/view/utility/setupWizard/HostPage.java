/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility.setupWizard;

import java.util.ArrayList;

import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.utility.Spinner;
import sancho.view.utility.WidgetFactory;

public class HostPage extends WizardPage {
  Combo core;
  Text desc;
  Group group;
  Text host;
  java.util.List hostArray = new ArrayList();
  int initialNumHosts;
  List list;
  Text pass;
  Spinner port;
  Text user;
  int hm_num;

  public HostPage() {
    super("hostPage");
    setTitle(SResources.getString("hm.coreSettings"));
    setMessage(SResources.getString("hm.info"));
  }

  public void addAsNew() {
    HostObject h = new HostObject();
    saveCurrent(h);
    hostArray.add(h);
    fillList();
    list.setSelection(list.getItemCount() - 1);
    resetInfo();
  }

  public void addLabel(Composite composite, String text) {
    Label l = new Label(composite, SWT.NONE);
    l.setText(text);
    l.setLayoutData(new GridData());
  }

  public void createControl(Composite parent) {
    Composite mainComposite = new Composite(parent, SWT.NONE);
    mainComposite.setLayout(WidgetFactory.createGridLayout(2, 0, 0, 0, 0, false));

    loadHosts();

    createMyControl(mainComposite);

    fillList();
    list.setSelection(0);
    resetInfo();

    setControl(mainComposite);
  }

  public Combo createCore(Composite composite, String label, String defText) {
    addLabel(composite, label);

    Combo c = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
    c.add("mldonkey");
    c.select(0);
    c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    addLabel(composite, defText);

    return c;
  }

  protected void createMyControl(Composite mainComposite) {
    GridData gridData;

    Group headGroup = new Group(mainComposite, SWT.SHADOW_ETCHED_IN);
    headGroup.setLayout(WidgetFactory.createGridLayout(1, 5, 5, 5, 5, false));

    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 2;
    headGroup.setLayoutData(gd);

    Label label = new Label(headGroup, SWT.NONE);
    label.setText(SResources.getString("hm.message"));
    label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    // Host

    group = new Group(mainComposite, SWT.SHADOW_ETCHED_IN);
    group.setText(SResources.getString("hm.hostSettings"));
    group.setLayout(WidgetFactory.createGridLayout(3, 5, 5, 5, 5, false));
    group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    host = createText(group, SResources.getString("hm.host"), SResources.getString("l.default")
        + " = 127.0.0.1");

    port = createPort(group, SResources.getString("hm.port"), SResources.getString("l.default") + " = 4001");

    user = createText(group, SResources.getString("hm.username"), SResources.getString("l.default")
        + " = admin");

    pass = createText(group, SResources.getString("hm.password"), SResources.getString("l.default") + " = "
        + SResources.getString("l.empty"));
    pass.setEchoChar('*');

    desc = createText(group, SResources.getString("hm.description"), SResources.getString("l.default")
        + " = " + SResources.getString("l.empty"));

    core = createCore(group, SResources.getString("hm.protocol"), SResources.getString("l.default")
        + " = mldonkey");

    // HM

    Composite hmComposite = new Composite(mainComposite, SWT.NONE);
    hmComposite.setLayout(WidgetFactory.createGridLayout(2, 5, 5, 5, 5, false));
    hmComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    list = new List(hmComposite, SWT.BORDER | SWT.VERTICAL | SWT.HORIZONTAL);
    gridData = new GridData(GridData.FILL_BOTH);
    gridData.heightHint = 60;
    gridData.widthHint = 120;
    gridData.horizontalSpan = 2;
    list.setLayoutData(gridData);

    list.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        resetInfo();
      }
    });

    Button addCurrentInfo = new Button(hmComposite, SWT.PUSH);
    addCurrentInfo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
    addCurrentInfo.setText(SResources.getString("b.addAsNewEntry"));
    addCurrentInfo.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        addAsNew();
      }
    });

    Button deleteCurrent = new Button(hmComposite, SWT.PUSH);
    deleteCurrent.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
    deleteCurrent.setText(SResources.getString("b.deleteEntry"));
    deleteCurrent.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        if (list.getSelectionIndex() != 0)
          removeCurrent();
      }
    });

    Button save = new Button(hmComposite, SWT.PUSH);
    gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 2;

    save.setLayoutData(gd);
    save.setText(SResources.getString("b.saveAsCurrent"));
    save.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        saveAsCurrent();
      }
    });

    Button def = new Button(hmComposite, SWT.PUSH);
    gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 2;
    def.setLayoutData(gd);
    def.setText(SResources.getString("b.makeDefault"));
    def.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        if (list.getSelectionIndex() != 0)
          makeDefault();
      }
    });

  }

  public Spinner createPort(Composite composite, String label, String defText) {
    addLabel(composite, label);

    Spinner s = new Spinner(composite, SWT.NONE);
    s.setMaximum(65536);
    s.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    addLabel(composite, defText);

    return s;
  }

  public Text createText(Composite composite, String label, String defText) {
    addLabel(composite, label);

    Text t = new Text(composite, SWT.BORDER | SWT.SINGLE);
    t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    addLabel(composite, defText);

    return t;
  }

  public void fillList() {
    list.removeAll();

    for (int i = 0; i < hostArray.size(); i++) {
      HostObject h = (HostObject) hostArray.get(i);
      String d = h.description.equals(SResources.S_ES) ? (h.hostname + ":" + h.port) : h.description;
      list.add((i == 0 ? SResources.getString("l.default") + "(" + d + ")" : d));
    }
  }

  public void loadHost(HostObject h, int i) {
    h.hostname = PreferenceLoader.loadString("hm_" + i + "_hostname");
    h.username = PreferenceLoader.loadString("hm_" + i + "_username");
    h.password = PreferenceLoader.loadString("hm_" + i + "_password");
    h.port = PreferenceLoader.loadInt("hm_" + i + "_port");
    h.description = PreferenceLoader.loadString("hm_" + i + "_description");
    h.coreProtocol = PreferenceLoader.loadInt("hm_" + i + "_coreProtocol");
  }

  public void loadHosts() {
    for (int i = 0;; i++) {
      if ((i > 0) && !PreferenceLoader.contains("hm_" + i + "_hostname"))
        break;
      HostObject h = new HostObject();
      loadHost(h, i);
      hostArray.add(h);
    }

    initialNumHosts = hostArray.size();
  }

  public void makeDefault() {
    HostObject h = (HostObject) hostArray.get(list.getSelectionIndex());
    hostArray.remove(list.getSelectionIndex());
    hostArray.add(0, h);
    fillList();
    list.setSelection(0);
    resetInfo();
  }

  public void removeCurrent() {
    int oldIndex = list.getSelectionIndex();
    hostArray.remove(oldIndex);
    fillList();

    int newIndex = (list.getItemCount() > oldIndex ? oldIndex : oldIndex - 1);
    list.setSelection(newIndex);
    resetInfo();
  }

  public void resetInfo() {
    hm_num = list.getSelectionIndex();
    HostObject h = (HostObject) hostArray.get(hm_num);
    resetInfo(h);
  }

  public void resetInfo(HostObject h) {
    host.setText(h.hostname);
    port.setSelection(h.port);
    user.setText(h.username);
    pass.setText(h.password);
    desc.setText(h.description);
    core.select(h.coreProtocol);
    String d = h.description.equals(SResources.S_ES) ? (h.hostname + ":" + h.port) : h.description;
    group.setText(SResources.getString("hm.hostSettings") + d);
  }

  public void saveAsCurrent() {
    int sel = list.getSelectionIndex();
    HostObject h = (HostObject) hostArray.get(list.getSelectionIndex());
    saveCurrent(h);
    fillList();
    list.setSelection(sel);
    resetInfo();
  }

  public void saveCurrent(HostObject h) {
    h.hostname = host.getText();
    h.password = pass.getText();
    h.username = user.getText();
    h.port = port.getSelection();
    h.description = desc.getText();
    h.coreProtocol = core.getSelectionIndex();

  }

  public void saveData() {
    saveAsCurrent();
    PreferenceStore p = PreferenceLoader.getPreferenceStore();
    int cycle = Math.max(initialNumHosts, hostArray.size());

    for (int i = 0; i < cycle; i++) {
      if (i < hostArray.size()) {
        HostObject h = (HostObject) hostArray.get(i);
        setValue(p, i, h);
      } else {
        setToDefault(p, i);
      }
    }

    PreferenceLoader.saveStore();
  }

  public void setToDefault(PreferenceStore p, int i) {
    p.setToDefault("hm_" + i + "_hostname");
    p.setToDefault("hm_" + i + "_port");
    p.setToDefault("hm_" + i + "_username");
    p.setToDefault("hm_" + i + "_password");
    p.setToDefault("hm_" + i + "_description");
    p.setToDefault("hm_" + i + "_coreProtocol");
  }

  public void setValue(PreferenceStore p, int i, HostObject h) {
    p.setValue("hm_" + i + "_hostname", h.hostname);
    p.setValue("hm_" + i + "_port", h.port);
    p.setValue("hm_" + i + "_username", h.username);
    p.setValue("hm_" + i + "_password", h.password);
    p.setValue("hm_" + i + "_description", h.description);
    p.setValue("hm_" + i + "_coreProtocol", h.coreProtocol);
  }

  public int getNum() {
    return hm_num;
  }

}