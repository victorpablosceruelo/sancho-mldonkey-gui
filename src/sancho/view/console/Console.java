/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.console;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import sancho.core.Sancho;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.view.MainWindow;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public class Console {
  protected List commandHistory = new ArrayList();
  protected Composite composite;
  protected Color highlightColor;
  protected int historyMark = 0;
  protected StyledText infoDisplay;
  protected Text input;
  protected final int MAX_LINES = PreferenceLoader.loadInt("consoleMaxLines");

  public Console(Composite parent, int style) {
    createContents(parent, style);
  }

  public void addMenuItem(Menu menu, String resString, String imageString, SelectionAdapter selectionAdapter) {
    MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
    menuItem.setText(SResources.getString(resString));
    menuItem.setImage(SResources.getImage(imageString));
    menuItem.addSelectionListener(selectionAdapter);
  }

  public void append(String message) {
    int lCount;
    if ((lCount = infoDisplay.getLineCount()) > MAX_LINES)
      infoDisplay.replaceTextRange(0, infoDisplay.getOffsetAtLine(lCount - MAX_LINES + 5), SResources.S_ES);

    infoDisplay.setCaretOffset(infoDisplay.getText().length());
    infoDisplay.append(message);
    infoDisplay.setCaretOffset(infoDisplay.getCaretOffset() + message.length() + 1);
    infoDisplay.showSelection();

  }

  public void appendInput() {
    prefixAppend();

    String outText = input.getText();
    appendNewLine(outText);
    int start = infoDisplay.getCharCount() - outText.length() - getLineDelimiter().length();
    infoDisplay.setStyleRange(new StyleRange(start, outText.length(), highlightColor, infoDisplay
        .getBackground()));
  }

  public void appendNewLine(String message) {
    this.append(message + infoDisplay.getLineDelimiter());
  }

  protected void createContents(Composite parent, int style) {
    composite = new Composite(parent, SWT.NONE);
    composite.setLayout(WidgetFactory.createGridLayout(1, 0, 0, 0, 0, false));

    infoDisplay = new StyledText(composite, style | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
    infoDisplay.setLayoutData(new GridData(GridData.FILL_BOTH));

    Menu popupMenu = new Menu(infoDisplay);

    addMenuItem(popupMenu, "mi.copy", "copy", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        MainWindow.copyToClipboard(infoDisplay.getSelectionText());
      }
    });

    addMenuItem(popupMenu, "mi.selectAll", "plus", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        infoDisplay.selectAll();
      }
    });

    addMenuItem(popupMenu, "mi.clear", "clear", new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        infoDisplay.replaceTextRange(0, infoDisplay.getText().length(), SResources.S_ES);
      }
    });

    infoDisplay.setMenu(popupMenu);
    input = new Text(composite, SWT.SINGLE | SWT.BORDER);
    input.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    input.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        int numLinesDisplayed = infoDisplay.getClientArea().height / infoDisplay.getLineHeight();

        switch (e.keyCode) {
          case SWT.PAGE_UP :
            if (infoDisplay.getTopIndex() > numLinesDisplayed)
              infoDisplay.setTopIndex(infoDisplay.getTopIndex() - (numLinesDisplayed));
            else
              infoDisplay.setTopIndex(0);
            break;

          case SWT.PAGE_DOWN :

            infoDisplay.setTopIndex(infoDisplay.getTopIndex() + (numLinesDisplayed));
            break;

          case SWT.CR :

            appendInput();
            sendMessage();

            if (input.getText().length() > 0) {

              if (commandHistory.contains(input.getText()))
                commandHistory.remove(input.getText());

              commandHistory.add(input.getText());
              historyMark = commandHistory.size() - 1;
            }
            input.setText(SResources.S_ES);
            break;
          case SWT.ESC :

            input.setText(SResources.S_ES);
            break;

          case SWT.ARROW_UP :
            if (commandHistory.size() > 0) {
              if (historyMark < 0 || historyMark >= commandHistory.size())
                historyMark = commandHistory.size() - 1;

              input.setText((String) commandHistory.get(historyMark--));
              input.setSelection(input.getText().length());
              e.doit = false;
            }
            break;

          case SWT.ARROW_DOWN :
            if (commandHistory.size() > 0) {
              if (historyMark >= commandHistory.size() || historyMark < 0)
                historyMark = 0;

              input.setText((String) commandHistory.get(historyMark++));
              input.setSelection(input.getText().length());
            }
            break;
        }
      }
    });
    updateDisplay();
  }

  public void dispose() {
    input.dispose();
    infoDisplay.dispose();
    composite.dispose();
  }

  public boolean isDisposed() {
    return infoDisplay == null || infoDisplay.isDisposed();
  }

  public Composite getComposite() {
    return composite;
  }

  public String getLineDelimiter() {
    return infoDisplay.getLineDelimiter();
  }

  public void prefixAppend() {
  }

  public void sendMessage() {
    Sancho.send(OpCodes.S_CONSOLE_MESSAGE, input.getText());
  }

  public void setFocus() {
    input.setFocus();
  }
  
  public void setActive() {
    input.setEnabled(true);
    infoDisplay.setEnabled(true);
  }
  
  public void setInactive() {
    input.setEnabled(false);
    infoDisplay.setEnabled(false);
  }

  public void updateDisplay() {
    infoDisplay.setFont(PreferenceLoader.loadFont("consoleFontData"));
    input.setFont(PreferenceLoader.loadFont("consoleFontData"));
    highlightColor = PreferenceLoader.loadColor("consoleHighlight");
    infoDisplay.setBackground(PreferenceLoader.loadColor("consoleBackground"));
    infoDisplay.setForeground(PreferenceLoader.loadColor("consoleForeground"));
    input.setBackground(PreferenceLoader.loadColor("consoleInputBackground"));
    input.setForeground(PreferenceLoader.loadColor("consoleInputForeground"));
  }

}