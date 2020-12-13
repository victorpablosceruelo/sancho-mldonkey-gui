/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statistics;

import org.eclipse.swt.custom.SashForm;

import sancho.view.utility.AbstractTab;
import sancho.view.utility.SResources;
import sancho.view.viewFrame.SashViewFrame;

public class GraphViewFrame extends SashViewFrame {
  GraphCanvas graphCanvas;

  public GraphViewFrame(SashForm sashForm, String prefString, String prefImageString, AbstractTab aTab,
      String graphName) {
    super(sashForm, prefString, prefImageString, aTab);
    graphCanvas = new GraphCanvas(childComposite, SResources.getString(prefString), graphName, this);
    createViewListener(new GraphViewListener(this, graphCanvas, prefString.equals("graph.uploads")
        ? "graph.downloads"
        : "graph.uploads"));
  }

  public GraphCanvas getGraphCanvas() {
    return graphCanvas;
  }

  public void updateDisplay() {
    super.updateDisplay();
    graphCanvas.updateDisplay();
  }

}
