/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.utility;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import sancho.core.ICore;
import sancho.core.Sancho;
import sancho.view.MainWindow;
import sancho.view.viewFrame.ViewFrame;

public abstract class AbstractTab {

  private boolean active;
  protected Composite contentComposite;
  protected MainWindow mainWindow;
  protected ToolButton toolButton;
  protected List viewFrameList;

  public AbstractTab(MainWindow mainWindow, String resButtonString) {
    this.mainWindow = mainWindow;
    this.contentComposite = new Composite(mainWindow.getPageContainer(), SWT.NONE);
    this.contentComposite.setLayout(new FillLayout());
    this.contentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
    this.createButton(resButtonString);
    createContents(this.contentComposite);
  }

  public void addViewFrame(ViewFrame viewFrame) {
    if (viewFrameList == null)
      viewFrameList = new ArrayList();

    viewFrameList.add(viewFrame);
  }

  public void allViewFramesUpdateDisplay() {
    if (viewFrameList == null)
      return;
    
    for (int i = 0; i < viewFrameList.size(); ++i) {
      ViewFrame viewFrame = (ViewFrame) viewFrameList.get(i);
      viewFrame.updateDisplay();
    }
  }

  public void createButton(String buttonName) {
    toolButton = new ToolButton(mainWindow.getCoolBar().getToolBar(), SWT.PUSH);
    toolButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent s) {
        if (!isActive())
          setActive();
      }
    });
    toolButton.setText(SResources.getString(buttonName));
    toolButton.setToolTipText(SResources.getString(buttonName + ".tooltip"));
    toolButton.setBigActiveImage(SResources.getImage(buttonName + ".buttonActive"));
    toolButton.setBigInactiveImage(SResources.getImage(buttonName + ".button"));
    toolButton.setSmallActiveImage(SResources.getImage(buttonName + ".buttonSmallActive"));
    toolButton.setSmallInactiveImage(SResources.getImage(buttonName + ".buttonSmall"));
    toolButton.useSmallButtons(this.mainWindow.getCoolBar().isToolbarSmallButtons());
    toolButton.setActive(false);
    toolButton.resetImage();
    this.mainWindow.getCoolBar().getMainToolButtons().add(toolButton);
  }

  protected abstract void createContents(Composite parent);

  public void dispose() {
    this.toolButton.dispose();
  }

  public Composite getContent() {
    return contentComposite;
  }

  public ICore getCore() {
    return Sancho.getCore();
  }

  public MainWindow getMainWindow() {
    return mainWindow;
  }

  public ToolButton getToolButton() {
    return this.toolButton;
  }

  public List getViewFrameList() {
    return viewFrameList;
  }

  public boolean isActive() {
    return active;
  }

  public void onConnect() {
    if (getCore() != null && this.isActive())
      getCore().setActiveTab(this);

    if (viewFrameList == null)
      return;

    for (int i = 0; i < viewFrameList.size(); ++i) {
      ViewFrame viewFrame = (ViewFrame) viewFrameList.get(i);
      viewFrame.onConnect();
    }
  }

  public void onDisconnect() {
    if (viewFrameList == null)
      return;

    for (int i = 0; i < viewFrameList.size(); ++i) {
      ViewFrame viewFrame = (ViewFrame) viewFrameList.get(i);
      viewFrame.onDisconnect();
    }
  }

  public void setVisible(boolean b) {
    if (viewFrameList == null)
      return;

    for (int i = 0; i < viewFrameList.size(); ++i) {
      ViewFrame viewFrame = (ViewFrame) viewFrameList.get(i);
      if (viewFrame != null)
        viewFrame.setVisible(b);
    }
  }

  public void removeViewFrame(ViewFrame viewFrame) {
    viewFrameList.remove(viewFrame);
  }

  public void setActive() {

    this.active = true;
    this.mainWindow.setActive(this);
    this.toolButton.setActive(true);
    toggleAllViewFramesActive(true);
    if (getCore() != null)
      getCore().setActiveTab(this);
  }

  public void setInActive() {
    this.active = false;
    this.toolButton.setActive(false);
    toggleAllViewFramesActive(false);
  }

  public void toggleAllViewFramesActive(boolean b) {
    if (viewFrameList == null)
      return;

    for (int i = 0; i < viewFrameList.size(); ++i) {
      ViewFrame viewFrame = (ViewFrame) viewFrameList.get(i);
      if (viewFrame != null)
        viewFrame.setActive(b);
    }
  }

  public void updateDisplay() {
    getContent().layout();
    allViewFramesUpdateDisplay();
  }
}