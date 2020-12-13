package sancho.view.utility;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/*
 * (c) Copyright IBM Corp. 2000, 2001. All Rights Reserved.
 *
 * Modified 2004.
 */
public class Spinner extends Composite {
  static final int BUTTON_WIDTH = 16;
  Text text;
  Button up;
  Button down;
  int minimum;
  int maximum;

  public Spinner(Composite parent, int style) {
    super(parent, style);

    text = new Text(this, style | SWT.SINGLE | SWT.BORDER);
    up = new Button(this, style | SWT.ARROW | SWT.UP);
    down = new Button(this, style | SWT.ARROW | SWT.DOWN);

    text.addListener(SWT.Verify, new Listener() {
      public void handleEvent(Event e) {
        verify(e);
      }
    });

    text.addListener(SWT.Traverse, new Listener() {
      public void handleEvent(Event e) {
        myTraverse(e);
      }
    });

    up.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event e) {
        up();
      }
    });

    down.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event e) {
        down();
      }
    });

    addListener(SWT.Resize, new Listener() {
      public void handleEvent(Event e) {
        resize();
      }
    });

    addListener(SWT.FocusIn, new Listener() {
      public void handleEvent(Event e) {
        focusIn();
      }
    });

    text.setFont(getFont());

    minimum = 0;
    maximum = 9;

    setSelection(minimum);
  }

  public Text getText() {
    return text;
  }

  public void setEnabled(boolean b) {
    text.setEnabled(b);
    super.setEnabled(b);
    
  }
  
  void verify(Event e) {

    try {
      Integer.parseInt(e.text);
    } catch (NumberFormatException ex) {
      if (e.character != SWT.BS)
        e.doit = false;
    }
  }

  protected boolean myTraverse(Event e) {
    switch (e.detail) {
      case SWT.TRAVERSE_ARROW_PREVIOUS :
        if (e.keyCode == SWT.ARROW_UP) {
          e.doit = true;
          e.detail = SWT.NULL;
          up();
          return true;
        }
        return false;
      case SWT.TRAVERSE_ARROW_NEXT :
        if (e.keyCode == SWT.ARROW_DOWN) {
          e.doit = true;
          e.detail = SWT.NULL;
          down();
          return true;
        }
        return false;
    }
    return false;
  }

  void up() {
    setSelection(getSelection() + 1);
    notifyListeners(SWT.Selection, new Event());
  }

  void down() {
    setSelection(getSelection() - 1);
    notifyListeners(SWT.Selection, new Event());
  }

  void focusIn() {
    text.setFocus();
  }

  public void setFont(Font font) {
    super.setFont(font);
    text.setFont(font);
  }

  public void setSelection(int selection) {
    if (selection < minimum)
      selection = minimum;
    else if (selection > maximum)
      selection = maximum;

    text.setText(String.valueOf(selection));
    text.selectAll();
    text.setFocus();
  }

  public int getSelection() {
    int result;

    try {
      result = Integer.parseInt(text.getText());
    } catch (NumberFormatException ex) {
      result = 0;
    }

    return result;
  }

  public void setMaximum(int maximum) {
    checkWidget();
    this.maximum = maximum;
  }

  public int getMaximum() {
    return maximum;
  }

  public void setMinimum(int minimum) {
    this.minimum = minimum;
  }

  public int getMinimum() {
    return minimum;
  }

  void resize() {
    Point pt = computeSize(SWT.DEFAULT, SWT.DEFAULT);
    int textWidth = pt.x - BUTTON_WIDTH;
    int buttonHeight = pt.y / 2;
    text.setBounds(0, 0, textWidth, pt.y);
    up.setBounds(textWidth, 0, BUTTON_WIDTH, buttonHeight);
    down.setBounds(textWidth, pt.y - buttonHeight, BUTTON_WIDTH, buttonHeight);
  }

  public Point computeSize(int wHint, int hHint, boolean changed) {
    GC gc = new GC(text);
    Point textExtent = gc.textExtent(String.valueOf(maximum));
    gc.dispose();
    Point pt = text.computeSize(textExtent.x, textExtent.y);
    int width = pt.x + BUTTON_WIDTH;

    int height = pt.y;
    if (SWT.getPlatform().equals("fox"))
      height += 6;

    if (wHint != SWT.DEFAULT)
      width = wHint;

    if (hHint != SWT.DEFAULT)
      height = hHint;

    return new Point(width, height);
  }

  //  public void addSelectionListener(SelectionListener listener) {
  //    if (listener == null)
  //      throw new SWTError(SWT.ERROR_NULL_ARGUMENT);
  //
  //    addListener(SWT.Selection, new TypedListener(listener));
  //  }
}