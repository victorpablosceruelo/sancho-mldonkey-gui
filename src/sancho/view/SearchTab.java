/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import sancho.view.search.ASearchTab;
import sancho.view.search.SearchTab_Advanced;
import sancho.view.search.SearchTab_Audio;
import sancho.view.search.SearchTab_Simple;
import sancho.view.search.result.ResultTab;
import sancho.view.search.result.ResultViewFrame;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;
import sancho.view.viewFrame.SashViewFrame;
import sancho.view.viewer.GView;

public class SearchTab extends AbstractTab {
  private Composite composite;
  private CTabFolder resultsCTabFolder;
  private ResultViewFrame resultViewFrame;
  private Button searchButton;
  private CTabFolder searchCTabFolder;
  private ASearchTab[] searchTabs;

  public SearchTab(MainWindow mainWindow, String prefString) {
    super(mainWindow, prefString);
  }

  protected Button createButton(Composite composite, String resString, SelectionAdapter selectionAdapter) {
    Button button = new Button(this.composite, SWT.PUSH);
    button.setLayoutData(new GridData(GridData.FILL_BOTH));
    button.setText(SResources.getString(resString));
    button.addSelectionListener(selectionAdapter);
    return button;
  }

  protected void createContents(Composite parent) {
    String sashPrefString = "searchSash";
    SashForm sashForm = WidgetFactory.createSashForm(parent, sashPrefString);
    createViewFrames(sashForm);
    WidgetFactory.loadSashForm(sashForm, sashPrefString);
  }

  public void onConnect() {
    super.onConnect();
    for (int i = 0; i < searchTabs.length; i++)
      searchTabs[i].onConnect();
  }

  private void createLeftSash(Composite composite) {
    composite.setLayout(WidgetFactory.createGridLayout(1, 0, 0, 0, 0, false));

    searchCTabFolder = WidgetFactory.createCTabFolder(composite);
    searchCTabFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    searchTabs = new ASearchTab[]{new SearchTab_Simple(resultViewFrame, this),
        new SearchTab_Advanced(resultViewFrame, this), new SearchTab_Audio(resultViewFrame, this),
    //new SearchTab_Jigle(resultViewFrame, this)
    };

    Control control;
    CTabItem cTabItem;

    for (int i = 0; i < searchTabs.length; i++) {
      cTabItem = new CTabItem(searchCTabFolder, SWT.NONE);
      cTabItem.setText(searchTabs[i].getText());
      control = searchTabs[i].createTab(searchCTabFolder);

      if (i == 0)
        cTabItem.setControl(control);

      cTabItem.setData("myControl", control);
      cTabItem.setData(searchTabs[i]);
    }

    searchCTabFolder.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        CTabItem cTabItem = (CTabItem) e.item;
        cTabItem.setControl((Control) cTabItem.getData("myControl"));

        for (int i = 0; i < searchCTabFolder.getItems().length; i++) {
          if (searchCTabFolder.getItems()[i] != cTabItem)
            searchCTabFolder.getItems()[i].setControl(null);
        }
        searchCTabFolder.getParent().layout();
      }
    });
    searchCTabFolder.setSelection(0);

    Composite bottomComposite = new Composite(composite, SWT.NONE);
    bottomComposite.setLayout(WidgetFactory.createGridLayout(1, 2, 0, 0, 0, false));
    bottomComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    Label l = new Label(bottomComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
    l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    Composite buttonComp = new Composite(bottomComposite, SWT.NONE);
    buttonComp.setLayout(WidgetFactory.createGridLayout(1, 7, 5, 0, 0, false));
    buttonComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    createSearchButton(buttonComp);
  }

  private void createRightSash(CTabFolder cTabFolder) {
    resultsCTabFolder = cTabFolder;
    resultsCTabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
    resultsCTabFolder.setData(this);

    resultsCTabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
      public void close(CTabFolderEvent event) {
        CTabItem item = (CTabItem) event.item;
        item.getControl().dispose();
      }
    });

    resultsCTabFolder.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        CTabItem item = (CTabItem) e.item;
        GView gView = (GView) item.getData(GView.S_GVIEW);
        if (gView == null) {
          resultViewFrame.updateCLabelText(SResources.getString("t.search.results"));
        } else {
          resultViewFrame.updateCLabelText(SResources.getString("t.search.results") + ": "
              + gView.getTable().getItemCount());
        }
      }
    });
  }

  private void createSearchButton(Composite group) {
    this.composite = new Composite(group, SWT.NONE);

    GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.heightHint = 40;
    gridData.horizontalSpan = 2;
    this.composite.setLayoutData(gridData);
    this.composite.setLayout(new FillLayout());

    searchButton = createButton(this.composite, "s.search", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        getSearch().performSearch();
      }
    });
  }

  protected void createViewFrames(SashForm mainSash) {
    SashViewFrame searchViewFrame = new SashViewFrame(mainSash, "tab.search", "tab.search.buttonSmall", this);
    addViewFrame(searchViewFrame);

    resultViewFrame = new ResultViewFrame(mainSash, "t.search.results", "tab.search.buttonSmall", this);
    addViewFrame(resultViewFrame);

    createLeftSash(searchViewFrame.getChildComposite());
    createRightSash(resultViewFrame.getCTabFolder());
  }

  public CTabFolder getCTabFolder() {
    return resultsCTabFolder;
  }

  private ASearchTab getSearch() {
    CTabItem item = searchCTabFolder.getSelection();
    ASearchTab result = (ASearchTab) item.getData();
    return result;
  }

  public boolean isButtonEnabled() {
    return searchButton != null && !searchButton.isDisposed() ? searchButton.isEnabled() : false;
  }

  public void setActive() {
    super.setActive();
    this.getSearch().setFocus();
  }

  public void setButtonEnabled(boolean b) {
    if (searchButton != null && !searchButton.isDisposed())
      searchButton.setEnabled(b);
  }

  private void toggleSearch(boolean search) {
    CTabItem item = resultsCTabFolder.getSelection();
    if (item == null)
      return;

    ResultTab result = (ResultTab) item.getData();
    if (result == null)
      return;

    if (search)
      result.unPause();
    else
      result.pause();
  }
}