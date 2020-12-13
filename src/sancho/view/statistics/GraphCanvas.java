/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statistics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import sancho.view.viewFrame.ViewFrame;

public class GraphCanvas extends Composite implements PaintListener, Runnable, DisposeListener {
  private Composite composite;
  private Graph graph;
  private GraphPainter graphPainter;
  private Image imageBuffer;
  private boolean needNewBuffer;
  private ViewFrame viewFrame;

  public GraphCanvas(Composite composite, String name, String graphName, ViewFrame viewFrame) {
    super(composite, SWT.NO_BACKGROUND);
    this.viewFrame = viewFrame;
    this.composite = composite;
    this.graphPainter = new GraphPainter(composite);

    graph = new Graph(name, graphName);
    graphPainter.setGraph(graph);

    addMouseListener(new MouseAdapter() {
      public void mouseDoubleClick(MouseEvent e) {
        graph.toggleDisplay();
      }
    });

    addPaintListener(this);
    addDisposeListener(this);

    addControlListener(new ControlAdapter() {
      public void controlResized(ControlEvent e) {
        needNewBuffer = true;
      }
    });
  }

  public void addPoint(float value) {
    graph.addPoint((int) (value * 100));
  }

  public Graph getGraph() {
    return graph;
  }

  public void redrawInThread() {
    if (!isDisposed() && viewFrame.isVisible())
      getDisplay().asyncExec(this);
  }

  public void paintControl(PaintEvent e) {
    if (needNewBuffer) {
      Rectangle r = composite.getBounds();
      if (r.height <= 0 || r.width <= 0)
        return;

      if (imageBuffer != null)
        imageBuffer.dispose();
     
      imageBuffer = new Image(null, composite.getBounds());
    }
    graphPainter.paint(e.gc, imageBuffer);
    needNewBuffer = false;
  }

  public void run() {
    if (!isDisposed() && isVisible())
      redraw();
  }

  public void updateDisplay() {
    graph.updateDisplay();
    graphPainter.updateDisplay();
  }

  public void widgetDisposed(DisposeEvent e) {
    if (imageBuffer != null && !imageBuffer.isDisposed())
      imageBuffer.dispose();
  }

}