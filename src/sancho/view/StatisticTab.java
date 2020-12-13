/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import sancho.core.Sancho;
import sancho.model.mldonkey.ClientStats;
import sancho.utility.SwissArmy;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.statistics.Graph;
import sancho.view.statistics.GraphCanvas;
import sancho.view.statistics.GraphViewFrame;
import sancho.view.statistics.InfoViewFrame;
import sancho.view.utility.AbstractTab;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public class StatisticTab extends AbstractTab implements Observer {

  private static final String RS_UPLOADS = SResources.getString("graph.uploads");
  private static final String RS_DOWNLOADS = SResources.getString("graph.downloads");
  private static final String RS_AVG = SResources.getString("graph.avg");
  private static final String RS_MAX = SResources.getString("graph.max");
  private static final String RS_TOTAL = SResources.getString("l.total");
  private static final String RS_DELAY = SResources.getString("l.delay");

  private static final String S_S = "s ";

  private GraphCanvas uploadsGraphCanvas;
  private GraphCanvas downloadsGraphCanvas;
  private CLabel uploadsHeaderCLabel;
  private CLabel downloadsHeaderCLabel;
  private int updateDelay;
  private long lastTimeStamp;
  private long lastTextTimeStamp;
  private Text statsText;
  private String nl;

  public StatisticTab(MainWindow mainWindow, String prefString) {
    super(mainWindow, prefString);
    onConnect();
    updateDisplay();
  }

  protected void createContents(Composite parent) {

    String statSashPrefString = "statisticsSash";
    SashForm graphSash = WidgetFactory.createSashForm(parent, statSashPrefString);
    createInfo(graphSash);
    createGraphSash(graphSash);
    WidgetFactory.loadSashForm(graphSash, statSashPrefString);

  }

  public void onConnect() {
    super.onConnect();

    if (Sancho.hasCollectionFactory())
      getCore().getClientStats().addObserver(this);
  }

  private void createInfo(SashForm sashForm) {
    InfoViewFrame s = new InfoViewFrame(sashForm, "tab.statistics", "tab.statistics.buttonSmall", this);

    statsText = s.getText();
    nl = statsText.getLineDelimiter();

    addViewFrame(s);
  }

  private void updateText(ClientStats clientStats) {
    if (!Sancho.hasCollectionFactory() || statsText == null || statsText.isDisposed())
      return;

    final StringBuffer stringBuffer = new StringBuffer();

    stringBuffer.append("Downloading: ");
    stringBuffer.append(clientStats.getNumDownloadingFiles() + nl);

    stringBuffer.append("Downloaded(");
    stringBuffer.append(clientStats.getNumDownloadedFiles());
    stringBuffer.append("): ");
    stringBuffer.append(SwissArmy.calcStringSize(clientStats.getDownloadCounter()) + nl);

    stringBuffer.append("Uploaded: ");
    stringBuffer.append(SwissArmy.calcStringSize(clientStats.getUploadCounter()) + nl);

    stringBuffer.append("Shared(");
    stringBuffer.append(clientStats.getNumSharedFiles());
    stringBuffer.append("): ");
    stringBuffer.append(SwissArmy.calcStringSize(clientStats.getSharedCounter()) + nl);

    stringBuffer.append(nl + "DownRate: ");
    stringBuffer.append(clientStats.getTotalDownRateString() + nl);

    stringBuffer.append("UpRate: ");
    stringBuffer.append(clientStats.getTotalUpRateString() + nl);

    /*
     
     protected Network[] connectedNetworks;
     protected long downloadCounter; //
     protected int numDownloadedFiles; //
     protected int numDownloadingFiles; //
     protected int numSharedFiles;
     protected long sharedCounter;
     protected float tcpDownloadRate;
     protected float tcpUploadRate;
     protected float udpDownloadRate;
     protected float udpUploadRate;
     protected long uploadCounter;
     
     */

    stringBuffer.append(getCore().getNetworkCollection().getAllNetworkStats(nl));

    if (statsText != null && !statsText.isDisposed()) {
      statsText.getDisplay().asyncExec(new Runnable() {
        public void run() {
          if (statsText == null || statsText.isDisposed())
            return;

          statsText.setText(stringBuffer.toString());
        }
      });
    }

  }

  private void createGraphSash(Composite parent) {
    String graphSashPrefString = "graphSash";
    SashForm graphSash = WidgetFactory.createSashForm(parent, graphSashPrefString);
    createDownloadsGraph(graphSash);
    createUploadsGraph(graphSash);
    WidgetFactory.loadSashForm(graphSash, graphSashPrefString);
  }

  private void createDownloadsGraph(SashForm graphSash) {
    GraphViewFrame graphViewFrame = createGraph(graphSash, RS_DOWNLOADS, "Downloads");
    addViewFrame(graphViewFrame);
    downloadsGraphCanvas = graphViewFrame.getGraphCanvas();
    downloadsHeaderCLabel = graphViewFrame.getCLabel();
  }

  private void createUploadsGraph(SashForm graphSash) {
    GraphViewFrame graphViewFrame = createGraph(graphSash, RS_UPLOADS, "Uploads");
    addViewFrame(graphViewFrame);
    uploadsGraphCanvas = graphViewFrame.getGraphCanvas();
    uploadsHeaderCLabel = graphViewFrame.getCLabel();
  }

  private GraphViewFrame createGraph(SashForm graphSash, String titleResString, String graphName) {
    return new GraphViewFrame(graphSash, titleResString, "tab.statistics.buttonSmall", this, graphName);
  }

  public void update(Observable o, Object obj) {
    long currTime;
    if ((currTime = System.currentTimeMillis()) - (updateDelay * 1000) < lastTimeStamp)
      return;

    ClientStats clientStats = (ClientStats) o;

    if (clientStats == null)
      return;

    uploadsGraphCanvas.addPoint(clientStats.getTcpUploadRate());
    downloadsGraphCanvas.addPoint(clientStats.getTcpDownloadRate());

    if (this.isActive()) {
      uploadsGraphCanvas.redrawInThread();
      downloadsGraphCanvas.redrawInThread();
      updateHeaderLabels(clientStats);
    }

    lastTimeStamp = System.currentTimeMillis();

    if (lastTextTimeStamp + 30000 < currTime) {
      updateText(clientStats);
      lastTextTimeStamp = currTime;
    }

  }

  public static StringBuffer stringBuffer = new StringBuffer();

  public void updateHeaderLabels(final ClientStats clientStats) {
    if ((uploadsHeaderCLabel == null) || uploadsHeaderCLabel.isDisposed())
      return;

    uploadsHeaderCLabel.getDisplay().asyncExec(new Runnable() {
      public void run() {
        if ((uploadsHeaderCLabel == null) || uploadsHeaderCLabel.isDisposed())
          return;

        uploadsHeaderCLabel.setText(writeLabel(uploadsGraphCanvas.getGraph(), RS_UPLOADS, clientStats
            .getUploadCounter()));
        downloadsHeaderCLabel.setText(writeLabel(downloadsGraphCanvas.getGraph(), RS_DOWNLOADS, clientStats
            .getDownloadCounter()));
      }

      public String writeLabel(Graph graph, String string, long num) {
        stringBuffer.setLength(0);
        stringBuffer.append(string);
        stringBuffer.append(SResources.S_COLON);
        stringBuffer.append(SwissArmy.calcStringSize(num));
        stringBuffer.append(SResources.S_SPACE);
        stringBuffer.append(RS_TOTAL);
        stringBuffer.append(SResources.S_COMMA);
        stringBuffer.append((double) graph.getAvg() / 100);
        stringBuffer.append(SResources.S_SPACE);
        stringBuffer.append(RS_AVG);
        stringBuffer.append(SResources.S_COMMA);
        stringBuffer.append((double) graph.getMax() / 100);
        stringBuffer.append(SResources.S_SPACE);
        stringBuffer.append(RS_MAX);
        if (updateDelay > 0) {
          stringBuffer.append(SResources.S_SPACE);
          stringBuffer.append(SResources.S_OBS);
          stringBuffer.append(updateDelay);
          stringBuffer.append(S_S);
          stringBuffer.append(RS_DELAY);
          stringBuffer.append(SResources.S_CB);
        }
        return stringBuffer.toString();
      }
    });
  }

  public void setInActive(boolean removeObserver) {
    // Do not remove Observer
    super.setInActive();
  }

  public void updateDisplay() {
    super.updateDisplay();
    updateDelay = PreferenceLoader.loadInt("graphUpdateDelay");
    lastTimeStamp = 0;
  }
}