/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statistics;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import sancho.view.utility.SResources;
import sancho.view.viewFrame.SashViewFrame;
import sancho.view.viewFrame.SashViewListener;

public class GraphViewListener extends SashViewListener {
  private GraphCanvas graphCanvas;
  private String showResString;

  public GraphViewListener(SashViewFrame sashViewFrame, GraphCanvas graphCanvas, String showResString) {
    super(sashViewFrame);
    this.graphCanvas = graphCanvas;
    this.showResString = showResString;
  }

  public void menuAboutToShow(IMenuManager menuManager) {
    menuManager.add(new HourlyGraphHistoryAction(graphCanvas.getGraph()));
    menuManager.add(new ClearGraphHistoryAction(graphCanvas.getGraph()));
    menuManager.add(new ReverseGraphAction(graphCanvas.getGraph()));
    // flip sash/maximize sash
    createSashActions(menuManager, showResString);
  }

  static class ReverseGraphAction extends Action {
    Graph graph;

    public ReverseGraphAction(Graph graph) {
      super(SResources.getString("graph.reverseGraph"));
      setImageDescriptor(SResources.getImageDescriptor("rotate"));
      this.graph = graph;
    }

    public void run() {
      graph.reverse();
    }
  }

  static class HourlyGraphHistoryAction extends Action {
    Graph graph;

    public HourlyGraphHistoryAction(Graph graph) {
      super(SResources.getString("graph.hourlyHistory"));
      setImageDescriptor(SResources.getImageDescriptor("graph"));
      this.graph = graph;
    }

    public void run() {
      new GraphHistory(graph);
    }
  }

  public class ClearGraphHistoryAction extends Action {
    Graph graph;

    public ClearGraphHistoryAction(Graph graph) {
      super(SResources.getString("graph.clearHistory"));
      setImageDescriptor(SResources.getImageDescriptor("clear"));
      this.graph = graph;
    }

    public void run() {
      MessageBox confirm = new MessageBox(graphCanvas.getShell(), SWT.YES | SWT.NO | SWT.ICON_QUESTION);
      confirm.setMessage(SResources.getString("mi.areYouSure"));
      if (confirm.open() == SWT.YES)
        graph.clearHistory();
    }
  }
}