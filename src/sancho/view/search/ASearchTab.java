/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import sancho.core.Sancho;
import sancho.model.mldonkey.Network;
import sancho.model.mldonkey.utility.SearchQuery;
import sancho.view.SearchTab;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.search.result.ResultViewFrame;
import sancho.view.utility.NoDuplicatesCombo;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public abstract class ASearchTab implements Observer {

  protected static String S_NO_NETWORK = SResources.getString("s.m.noNetwork");
  protected static String S_ALL_NETWORKS = SResources.getString("s.m.all");

  protected Combo networkCombo;
  protected List networkList = new ArrayList();
  protected Combo searchCombo;
  protected SearchTab searchTab;
  protected List tempList = new ArrayList();
  protected ResultViewFrame viewFrame;
  protected Combo searchTypeCombo;

  public ASearchTab(ResultViewFrame viewFrame, SearchTab searchTab) {
    this.viewFrame = viewFrame;
    this.searchTab = searchTab;
    onConnect();
  }

  public synchronized void addNetwork(SearchQuery searchQuery) {
    int ind = networkCombo.getSelectionIndex();
    if (ind >= 0 && ind < networkList.size()) {
      Network network = (Network) networkList.get(ind);
      searchQuery.setNetwork(network.getId());
    }
  }

  protected Combo createCombo(Composite composite, int style, String resString, String[] items) {
    Label label = new Label(composite, SWT.NONE);
    label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
    label.setText(SResources.getString(resString));

    Combo combo = new Combo(composite, style | SWT.SINGLE | SWT.BORDER);
    combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    combo.setItems(items);
    combo.select(0);
    return combo;
  }

  protected Combo createFileType(Composite composite) {
    String[] resItems = {"s.m.all", "s.m.audio", "s.m.video", "s.m.image", "s.m.software"};
    return createResCombo(composite, SWT.READ_ONLY, "s.fileType", resItems);
  }

  protected Combo createIntegerCombo(Composite composite, int style, String resString, String[] items) {
    Combo combo = createCombo(composite, style, resString, items);
    combo.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        switch (e.keyCode) {
          case SWT.BS :
          case SWT.DEL :
          case SWT.ARROW_LEFT :
          case SWT.ARROW_RIGHT :
            return;
        }

        try {
          Integer.parseInt(String.valueOf(e.character));
        } catch (NumberFormatException nE) {
          e.doit = false;
        }
      }
    });
    return combo;
  }

  protected Composite createMainComposite(CTabFolder cTabFolder) {
    Composite composite = new Composite(cTabFolder, SWT.NONE);
    composite.setLayout(WidgetFactory.createGridLayout(2, 7, 5, 5, 5, false));
    return composite;
  }

  protected void createNetworkCombo(Composite composite) {
    Label label = new Label(composite, SWT.NONE);
    label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
    label.setText(SResources.getString("s.network"));

    this.networkCombo = new Combo(composite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
    this.networkCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    syncNetworkCombo(true);
  }

  protected Combo createResCombo(Composite composite, int style, String resString, String[] resItems) {
    String[] items = new String[resItems.length];
    for (int i = 0; i < resItems.length; i++)
      items[i] = SResources.getString(resItems[i]);

    return createCombo(composite, style, resString, items);
  }

  protected Combo createSearchCombo(Composite composite, String resString) {
    Label label = new Label(composite, SWT.NONE);
    label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
    label.setText(SResources.getString(resString));

    NoDuplicatesCombo combo = new NoDuplicatesCombo(composite, SWT.SINGLE | SWT.BORDER);
    combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    combo.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.character == SWT.CR) {
          performSearch();
          Combo combo = (Combo) e.widget;
          combo.add(combo.getText(), 0);
          combo.setText(SResources.S_ES);
        }
      }
    });

    return combo;
  }

  protected Combo createSavedSearchCombo(Composite composite, String resString, final String saveString) {
    Combo combo = createSearchCombo(composite, resString);

    combo.setItems(PreferenceLoader.loadStringArray(saveString + ".sArray"));
    combo.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e) {
        Combo combo = (Combo) e.widget;
        PreferenceLoader.setValue(saveString + ".sArray", combo.getItems(), 25);
      }
    });

    return combo;
  }

  protected Combo createSearchTypeCombo(Composite composite) {
    String[] resItems = {"s.st.remote", "s.st.local", "s.st.subscribe"};
    return createResCombo(composite, SWT.READ_ONLY, "s.searchType", resItems);
  }

  public void createSeparator(Composite composite) {
    Label s = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 2;
    s.setLayoutData(gridData);
  }

  public abstract Control createTab(CTabFolder cTabFolder);

  public abstract String getText();

  public void onConnect() {
    if (Sancho.hasCollectionFactory()) {
      viewFrame.getCore().getNetworkCollection().addObserver(this);
      if (networkCombo != null) {
        syncNetworkCombo(true);
      }
    }
  }

  public void parseFileType(Combo combo, SearchQuery query) {
    switch (combo.getSelectionIndex()) {
      case 1 :
        query.setMedia(SearchQuery.S_AUDIO);
        break;
      case 2 :
        query.setMedia(SearchQuery.S_VIDEO);
        break;
      case 3 :
        query.setMedia(SearchQuery.S_IMAGE);
        break;
      case 4 :
        query.setMedia(SearchQuery.S_SOFTWARE);
        break;
    }
  }

  public void parseSearchType(Combo combo, SearchQuery query) {
    switch (combo.getSelectionIndex()) {
      case 1 :
        query.setLocalSearch();
        break;
      case 2 :
        query.setSubscribeSearch();
        break;
    }
  }

  public abstract void performSearch();

  public boolean setFocus() {
    return this.searchCombo.setFocus();
  }

  public synchronized void syncNetworkCombo(boolean force) {
    boolean requiresUpdate = false;
    if (!force) {

      tempList.clear();
      if (!Sancho.hasCollectionFactory())
        return;
      Network[] networks = viewFrame.getCore().getNetworkCollection().getNetworks();
      for (int i = 0; i < networks.length; i++) {
        Network network = networks[i];
        if (network.isEnabled() && network.isSearchable()) {
          tempList.add(network);
        }
      }
      if (tempList.size() != networkList.size())
        requiresUpdate = true;
      else {
        for (int i = 0; i < tempList.size(); i++) {
          if (!networkList.contains(tempList.get(i))) {
            requiresUpdate = true;
            break;
          }
        }
        if (!networkCombo.isEnabled() && tempList.size() == 1)
          requiresUpdate = true;
      }

    }
    if (requiresUpdate || force) {
      networkCombo.removeAll();

      networkList.clear();
      for (int i = 0; i < tempList.size(); i++) {
        Network network = (Network) tempList.get(i);
        networkList.add(network);
        networkCombo.add(network.getName());
      }
      if (networkCombo.getItemCount() > 1) {
        networkCombo.add(S_ALL_NETWORKS);
      }
      networkCombo.select(networkCombo.getItemCount() - 1);

      if (!networkCombo.isEnabled())
        networkCombo.setEnabled(true);
      if (!searchCombo.isEnabled()) {
        searchCombo.setText(SResources.S_ES);
        searchCombo.setEnabled(true);
      }
      if (!searchTab.isButtonEnabled()) {
        searchTab.setButtonEnabled(true);
      }
    }
  }

  public void update(Observable o, Object arg) {
    if (this.networkCombo == null || this.networkCombo.isDisposed())
      return;

    this.networkCombo.getDisplay().asyncExec(new Runnable() {
      public void run() {
        if (networkCombo == null || networkCombo.isDisposed())
          return;

        if (!Sancho.hasCollectionFactory()
            || viewFrame.getCore().getNetworkCollection().getEnabledAndSearchable() == 0) {

          if (searchCombo.isEnabled()) {
            searchCombo.setText(S_NO_NETWORK);
            searchCombo.setEnabled(false);
          }
          if (networkCombo.isEnabled()) {
            networkCombo.removeAll();
            networkCombo.setText(S_NO_NETWORK);
            networkCombo.setEnabled(false);
          }
          if (searchTab.isButtonEnabled()) {
            searchTab.setButtonEnabled(false);
          }
        } else {
          syncNetworkCombo(false);
        }
      }
    });
  }

}