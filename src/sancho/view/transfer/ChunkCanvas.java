/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import sancho.core.Sancho;
import sancho.model.mldonkey.Client;
import sancho.model.mldonkey.File;
import sancho.model.mldonkey.Network;

public class ChunkCanvas extends Canvas implements Observer, DisposeListener, PaintListener, Runnable {
  private static final int INITIAL_HEIGHT = 18;
  private static final int HALF_INITIAL_HEIGHT = INITIAL_HEIGHT / 2;
  private static final int MAX_LENGTH = 200;
  private static final boolean forceLimit = !SWT.getPlatform().equals("win32");

  private String avail;
  private String chunks;
  private Client client;
  private File file;
  private Network network;

  private ImageData imageData;
  private ImageData resizedImageData;
  private boolean limitLength;

  public ChunkCanvas(Composite parent, int style, Client client, File file, Network network,
      boolean limitLength) {
    super(parent, style);
    this.client = client;
    this.file = file;
    this.network = network;
    this.limitLength = limitLength || forceLimit;
   

    if (client != null)
      client.addObserver(this);
    else {
      if (file != null)
        file.addObserver(this);
    }

    createImage();

    addDisposeListener(this);
    addPaintListener(this);

    addControlListener(new ControlAdapter() {
      public void controlResized(ControlEvent e) {
        resizeImage(e, false);
      }
    });
  }

  /**
   * to make transition from emule easier
   * http://www.emule-project.net/faq/progress.htm
   */
  private void createClientImage() {
    this.avail = client.getFileAvailability(file.getId());
    this.chunks = file.getChunks();

    int length = 0;

    if (avail != null)
      length = avail.length();

    if (length == 0)
      return;

    Display display = getDisplay();

    Color red = display.getSystemColor(SWT.COLOR_RED);
    Color black = display.getSystemColor(SWT.COLOR_BLACK);
    Color yellow = display.getSystemColor(SWT.COLOR_YELLOW);
    Color blue = new Color(display, 0, 150, 255);
    Color silver = new Color(display, 226, 225, 221);
    Color darkGray = new Color(display, 107, 81, 9);

    Color fromColor = black;
    Color toColor;

    int jump = 1;
    if (length > MAX_LENGTH && limitLength) {
      jump = length / MAX_LENGTH;
      length = MAX_LENGTH;
    }

    Image image = new Image(display, length, INITIAL_HEIGHT);
    GC imageGC = new GC(image);

    int cnt = 0;
    for (int i = 0; i < length; i++) {
      toColor = blue;

      // we have it
      if ((chunks.length() == length) && (chunks.charAt(cnt) == '2')) {
        toColor = darkGray;
      } // doesn't have it
      else if (avail.charAt(cnt) == '0') {
        toColor = silver;
      } // they have it
      else if (avail.charAt(cnt) == '1') {
        toColor = blue;
      } // ???
      else if (avail.charAt(cnt) == '2') {
        toColor = yellow;
      }

      drawDoubleG(imageGC, fromColor, toColor, i);
      cnt += jump;
    }
    imageData = image.getImageData();

    imageGC.dispose();
    image.dispose();

    blue.dispose();
    silver.dispose();
    darkGray.dispose();

    if (resizedImageData == null)
      resizedImageData = imageData;

    resizeImage(null, true);
  }

  private void createFileImage() {
    this.chunks = file.getChunks();

    if (network != null) {
      if (file.hasAvails()) {
        this.avail = file.getAvails(network);
      } else {
        this.avail = file.getAvail();
      }
    } else {
      this.avail = file.getAvail();
    }

    Display display = getDisplay();
    Color red = display.getSystemColor(SWT.COLOR_RED);
    Color black = display.getSystemColor(SWT.COLOR_BLACK);
    Color yellow = display.getSystemColor(SWT.COLOR_YELLOW);
    Color darkGray = new Color(display, 107, 81, 9);

    int length = 0;
    if (avail.length() != 0)
      length = avail.length();

    if (length == 0)
      return;

    int numChunkSources;
    int highestNumSources = 0;

    for (int i = 0; i < length; i++) {
      numChunkSources = avail.charAt(i);
      if (numChunkSources > highestNumSources)
        highestNumSources = numChunkSources;
    }

    float factor = highestNumSources > 0 ? 10f / highestNumSources : 1f;

    int jump = 1;
    if (length > MAX_LENGTH && limitLength) {
      jump = length / MAX_LENGTH;
      length = MAX_LENGTH;
    }

    Image image = new Image(display, length, INITIAL_HEIGHT);
    final GC imageGC = new GC(image);

    Color fromColor = black;
    Color toColor;

    int cnt = 0;
    for (int i = 0; i < length; i++) {
      numChunkSources = avail.charAt(cnt);
      Color intenseColor = null;
      if ((chunks.length() == length) && (chunks.charAt(cnt) == '2')) {
        toColor = darkGray;
      } else if ((chunks.length() == length) && (chunks.charAt(cnt) == '3')) {
        toColor = yellow;
      } else if (numChunkSources == 0) {
        toColor = red;
      } else {
        int colorIntensity = 255 - ((int) (numChunkSources * factor) * 25);
        intenseColor = new Color(null, 0, colorIntensity, 255);
        toColor = intenseColor;
      }
      drawDoubleG(imageGC, fromColor, toColor, i);

      if (intenseColor != null)
        intenseColor.dispose();
      cnt += jump;
    }

    imageData = image.getImageData();

    darkGray.dispose();
    imageGC.dispose();
    image.dispose();

    if (resizedImageData == null)
      resizedImageData = imageData;

    resizeImage(null, true);
  }

  private synchronized void createImage() {
    if (client != null)
      createClientImage();
    else
      createFileImage();
  }

  private void createProgressBar(int srcWidth, GC bufferGC) {
    Color green1 = new Color(null, 15, 136, 0);
    Color green2 = new Color(null, 41, 187, 26);
    int width = (int) (  ( (double) file.getPercent() / 100) * (double) (srcWidth - 1));
    bufferGC.setBackground(green1);
    bufferGC.setForeground(green2);
    bufferGC.fillGradientRectangle(0, 0, width, 4, false);
    green1.dispose();
    green2.dispose();
  }

  private void drawDoubleG(GC gc, Color fromColor, Color toColor, int start) {
    gc.setBackground(toColor);
    gc.setForeground(fromColor);
    gc.fillGradientRectangle(start, 0, 1, HALF_INITIAL_HEIGHT, true);

    gc.setForeground(toColor);
    gc.setBackground(fromColor);
    gc.fillGradientRectangle(start, HALF_INITIAL_HEIGHT, 1, HALF_INITIAL_HEIGHT, true);
  }

  private boolean hasChanged() {
    boolean result = false;

    if (client == null) {
      boolean part1 = chunks.hashCode() != file.getChunks().hashCode();
      boolean part2 = avail.hashCode() != file.getAvail().hashCode();
      result = part1 || part2;
    } else {
      String tempAvail = client.getFileAvailability(file.getId());
      if (avail == null && tempAvail != null)
        result = true;
      else if (avail != null && tempAvail != null)
        result = tempAvail.hashCode() != avail.hashCode();
    }
    return result;
  }

  public synchronized void paintControl(PaintEvent e) {
    if (resizedImageData != null) {
      Image bufferImage = new Image(getDisplay(), resizedImageData);
      GC bufferGC = new GC(bufferImage);

      if (client == null)
        createProgressBar(resizedImageData.width, bufferGC);

      roundCorners(resizedImageData.width, resizedImageData.height, bufferGC);
      bufferGC.dispose();

      boolean isValid = ((e.x + e.width) <= bufferImage.getBounds().width)
          && ((e.y + e.height) <= bufferImage.getBounds().height);

      try {
        if (isValid)
          e.gc.drawImage(bufferImage, e.x, e.y, e.width, e.height, e.x, e.y, e.width, e.height);
      } catch (Exception x) {
        Sancho.pDebug("e.width: " + e.width + " e.height: " + e.height + " bw: "
            + bufferImage.getBounds().width + " bh: " + bufferImage.getBounds().height);
        x.printStackTrace();
      }
      bufferImage.dispose();
    } else {
      e.gc.setBackground(getParent().getBackground());
      e.gc.fillRectangle(e.x, e.y, e.width, e.height);
    }
  }

  private void refresh() {
    if (this.hasChanged()) {
      createImage();
      this.redraw();
    }
  }

  protected synchronized void resizeImage(ControlEvent e, boolean force) {
    if (imageData != null) {
      int caWidth = getClientArea().width;
      int caHeight = getClientArea().height;
      int iWidth = resizedImageData.width;
      int iHeight = resizedImageData.height;
      
      // resize to same imagesize on createImage()..
      if (caWidth > 0 && caHeight > 0 &&  (force || !(caWidth == iWidth && caHeight == iHeight))) {
        resizedImageData = imageData.scaledTo(caWidth, caHeight);
        
      } 
    }
  }

  private void roundCorners(int srcWidth, int srcHeight, GC bufferGC) {
    // spacer in background colour
    bufferGC.setForeground(getParent().getBackground());
    bufferGC.drawLine(0, 0, srcWidth - 1, 0);

    // round the corners
    bufferGC.drawPoint(0, 1);
    bufferGC.drawPoint(0, srcHeight - 1);
    bufferGC.drawPoint(srcWidth - 1, 1);
    bufferGC.drawPoint(srcWidth - 1, srcHeight - 1);
  }

  public void run() {
    if (!isDisposed() && isVisible())
      refresh();
  }

  public void update(Observable o, Object obj) {
    if (isDisposed())
      return;

    boolean update = false;

    if (o instanceof Client && obj instanceof Integer) {
      int i = ((Integer) obj).intValue();
      update = (i == Client.CHANGED_AVAIL);
    } else if (o instanceof File) {
      update = ((File) o).hasChangedBit(File.CHANGED_AVAIL);
    }

    if (update) {
      getDisplay().syncExec(this); 
    }
  }

  public synchronized void widgetDisposed(DisposeEvent e) {
    if (file != null)
      file.deleteObserver(this);
    
    if (client != null)
      client.deleteObserver(this);
  }
}