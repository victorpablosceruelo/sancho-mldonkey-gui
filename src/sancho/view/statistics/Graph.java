/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.statistics;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import gnu.trove.list.array.TIntArrayList;
import sancho.view.preferences.PreferenceLoader;

public class Graph implements Runnable {
  public static final short MAX_POINTS = 1600;
  public static final short MAX_GRAPH_TYPES = 2;
  public static final int ONE_HOUR = 60 * 60000;
  private int[] iPoints = new int[MAX_POINTS];
  private int insertAt = 0;
  private String graphName;
  private String rawName;
  private Color graphColor1;
  private Color graphColor2;

  // arrayLists of primatives can't be synch'd..
  private TIntArrayList maxList, avgList;

  private boolean graphReverse;
  private int graphType;
  private long sumValue;
  private long hourlySumValue;
  private int amount;
  private int maxValue;
  private int avgValue;
  private int hourlyAmount;
  private int hourlyMaxValue;
  private int hourlyAvgValue;

  public Graph(String name, String graphName) {
    this.graphName = name;
    this.rawName = graphName;
    updateDisplay();

    this.maxList = new TIntArrayList(0);
    this.avgList = new TIntArrayList(0);

    sumValue = hourlySumValue = 0;
    graphType = avgValue = maxValue = 0;
    hourlyMaxValue = 0;
    hourlyAvgValue = 0;

    Display.getDefault().timerExec(ONE_HOUR, this);
  }

  public int getInsertAt() {
    return insertAt;
  }

  public int getPointAt(int i) {
    return iPoints[i];
  }

  public int findMax(int width) {
    int max = 20;
    int searchPoint = insertAt - 1;

    if (width > amount)
      width = amount;

    for (int i = 0; i < width; i++) {
      if (searchPoint < 0)
        searchPoint = MAX_POINTS - 1;

      if (iPoints[searchPoint] > max)
        max = iPoints[searchPoint];

      searchPoint--;
    }

    return max;
  }

  public int getNewestPoint() {
    int newestPoint = insertAt - 1;

    if (newestPoint < 0)
      newestPoint = MAX_POINTS - 1;

    return iPoints[newestPoint];
  }

  public void addPoint(int value) {
    if (insertAt > (MAX_POINTS - 1))
      insertAt = 0;

    iPoints[insertAt++] = value;

    if (value > maxValue)
      maxValue = value;

    sumValue += value;
    amount++;
    avgValue = (int) (sumValue / (long) amount);

    if (value > hourlyMaxValue)
      hourlyMaxValue = value;

    hourlySumValue += value;
    hourlyAmount++;
    hourlyAvgValue = (int) (hourlySumValue / (long) hourlyAmount);
  }

  public int getAmount() {
    return amount;
  }

  public Color getGraphColor1() {
    return graphColor1;
  }

  public Color getGraphColor2() {
    return graphColor2;
  }

  public int getMax() {
    return maxValue;
  }

  public String getName() {
    return graphName;
  }

  public int getAvg() {
    return avgValue;
  }

  public Color getColor1() {
    return graphColor1;
  }

  public Color getColor2() {
    return graphColor2;
  }

  public synchronized void run() {
    maxList.add(hourlyMaxValue);
    avgList.add(hourlyAvgValue);

    hourlyMaxValue = hourlyAvgValue = 0;
    hourlySumValue = hourlyAmount = 0;

    Display.getDefault().timerExec(ONE_HOUR, this);
  }

  public void toggleDisplay() {
    graphType++;

    if (graphType > MAX_GRAPH_TYPES)
      graphType = 0;

  }

  public int getGraphType() {
    if (graphType > MAX_GRAPH_TYPES)
      graphType = 0;

    return graphType;
  }

  public void reverse() {
    graphReverse = !graphReverse;
  }

  public boolean getReverse() {
    return graphReverse;
  }

  public synchronized TIntArrayList getMaxList() {
    return maxList;
  }

  public synchronized TIntArrayList getAvgList() {
    return avgList;
  }

  public synchronized void clearHistory() {
    sumValue = amount = 0;
    graphType = avgValue = maxValue = 0;
    insertAt = 0;
    maxList.clear();
    avgList.clear();
  }

  public void updateDisplay() {
    graphColor1 = PreferenceLoader.loadColor("graph" + rawName + "Color1");
    graphColor2 = PreferenceLoader.loadColor("graph" + rawName + "Color2");
    graphType = PreferenceLoader.loadInt("graph" + rawName + "Type");
    graphReverse = PreferenceLoader.loadBoolean("graph" + rawName + "Reverse");
  }

}