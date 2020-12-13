/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import sancho.core.Sancho;
import sancho.model.mldonkey.ConsoleMessage;
import sancho.view.console.Console;
import sancho.view.console.ConsoleViewFrame;
import sancho.view.utility.AbstractTab;

public class ConsoleTab extends AbstractTab implements Observer {
  private Console console;

  public ConsoleTab(MainWindow mainWindow, String prefString) {
    super(mainWindow, prefString);
  }

  protected void createContents(Composite parent) {
    ConsoleViewFrame viewFrame = new ConsoleViewFrame(parent, "tab.console", "tab.console.buttonSmall", this);
    addViewFrame(viewFrame);
    console = new Console(viewFrame.getChildComposite(), SWT.NONE);
  }

  public void setInActive() {
    super.setInActive();
    console.setInactive();

    if (Sancho.hasCollectionFactory())
      getCore().getConsoleMessage().deleteObserver(this);
  }

  public void setActive() {
    super.setActive();
    setObservers();
    console.setActive();
    console.setFocus();
  }

  public void setObservers() {
    if (Sancho.hasCollectionFactory()) {
      getCore().getConsoleMessage().addObserver(this);
      updateConsole();
    }
  }

  public void update(Observable o, Object arg) {
    if (o instanceof ConsoleMessage)
      updateConsole();
  }

  public void updateConsole() {
    if (getContent() == null || getContent().isDisposed())
      return;

    getContent().getDisplay().asyncExec(new Runnable() {
      public void run() {
        if (Sancho.hasCollectionFactory() && console != null && !console.isDisposed())
          console.append(getCore().getConsoleMessage().getMessage());
      }
    });
  }

  public void onConnect() {
    if (this.isActive())
      setObservers();
  }

  public void updateDisplay() {
    super.updateDisplay();
    console.updateDisplay();
  }
}