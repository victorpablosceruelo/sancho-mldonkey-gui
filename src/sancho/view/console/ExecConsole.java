/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.console;

import gnu.regexp.RE;
import gnu.regexp.REException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import sancho.core.Sancho;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.utility.SwissArmy;
import sancho.view.MainWindow;
import sancho.view.preferences.PreferenceLoader;
import sancho.view.utility.SResources;
import sancho.view.utility.WidgetFactory;

public class ExecConsole implements Observer {
  private static final int STDOUT = 1;
  private static final int STDERR = 2;
  private boolean coreStarted;
  private RE errorRE;
  private Process execProcess;
  private Color highlightColor;
  private final int MAX_LINES = PreferenceLoader.loadInt("consoleMaxLines");
  private StyledText outputConsole;
  private Shell shell;
  private StreamMonitor stderrMonitor;
  private StreamMonitor stdoutMonitor;

  public ExecConsole() {
    createContents();
    runExec();
  }

  public void appendLine(StreamMonitor streamMonitor, String newLine) {
    int lCount;
    if ((lCount = outputConsole.getLineCount()) > MAX_LINES)
      outputConsole.replaceTextRange(0, outputConsole.getOffsetAtLine(lCount - MAX_LINES + 5), SResources.S_ES);
    
    outputConsole.setCaretOffset(outputConsole.getText().length());

    int start = outputConsole.getCharCount();
    outputConsole.append(newLine + outputConsole.getLineDelimiter());
    if (streamMonitor.getType() == STDERR)
      outputConsole.setStyleRange(new StyleRange(start, newLine.length(), outputConsole.getDisplay()
          .getSystemColor(SWT.COLOR_RED), outputConsole.getBackground()));
    else if (errorRE.getMatch(newLine) != null)
      outputConsole.setStyleRange(new StyleRange(start, newLine.length(), highlightColor, outputConsole
          .getBackground()));

    outputConsole.setCaretOffset(outputConsole.getCaretOffset() + newLine.length());
    outputConsole.showSelection();

  }

  public boolean coreStarted() {
    return coreStarted;
  }

  public void createContents() {
    shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.RESIZE | SWT.BORDER);
    shell.setImage(SResources.getImage("ProgramIcon"));
    shell.setText("Core");
    shell.setLayout(WidgetFactory.createGridLayout(1, 0, 0, 0, 0, false));
    shell.addDisposeListener(new DisposeListener() {
      public synchronized void widgetDisposed(DisposeEvent e) {
        PreferenceStore p = PreferenceLoader.getPreferenceStore();
        PreferenceConverter.setValue(p, "coreExecutableWindowBounds", shell.getBounds());
      }
    });
    shell.addListener(SWT.Close, new Listener() {
      public void handleEvent(Event event) {
        event.doit = false;
        shell.setVisible(false);
      }
    });

    if (PreferenceLoader.contains("coreExecutableWindowBounds"))
      shell.setBounds(PreferenceLoader.loadRectangle("coreExecutableWindowBounds"));

    outputConsole = new StyledText(shell, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
        | SWT.READ_ONLY);
    outputConsole.setLayoutData(new GridData(GridData.FILL_BOTH));
    outputConsole.setFont(PreferenceLoader.loadFont("consoleFontData"));
    outputConsole.setBackground(PreferenceLoader.loadColor("consoleBackground"));
    outputConsole.setForeground(PreferenceLoader.loadColor("consoleForeground"));
    highlightColor = PreferenceLoader.loadColor("consoleHighlight");
    outputConsole.append("availableProcessors: " + Runtime.getRuntime().availableProcessors()
        + outputConsole.getLineDelimiter());
    outputConsole
        .append("maximumMem: " + Runtime.getRuntime().maxMemory() + outputConsole.getLineDelimiter());
    outputConsole
        .append("totalMem: " + Runtime.getRuntime().totalMemory() + outputConsole.getLineDelimiter());
    outputConsole.append("freeMem: " + Runtime.getRuntime().freeMemory() + outputConsole.getLineDelimiter()
        + outputConsole.getLineDelimiter());

    Menu popupMenu = new Menu(outputConsole);
    MenuItem copyItem = new MenuItem(popupMenu, SWT.PUSH);
    copyItem.setText(SResources.getString("mi.copy"));
    copyItem.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        MainWindow.copyToClipboard(outputConsole.getSelectionText());
      }
    });
    outputConsole.setMenu(popupMenu);
    try {
      errorRE = new RE("error", RE.REG_ICASE);
    } catch (REException e) {
      errorRE = null;
    }
  }

  public void dispose() {
    stdoutMonitor.stop();
    stderrMonitor.stop();
    outputConsole.dispose();
    shell.dispose();
  }

  public Shell getShell() {
    return shell;
  }

  public void runExec() {
    try {
      File executable = new File(PreferenceLoader.loadString("coreExecutable"));
      String workingDirectory = executable.getParent();
      execProcess = Runtime.getRuntime().exec(executable.toString(), null, new File(workingDirectory));

      Runtime.getRuntime().addShutdownHook(new Thread() {
        public void run() {
          if (PreferenceLoader.loadBoolean("killSpawnedCoreOnExit")) {
            Sancho.send(OpCodes.S_KILL_CORE);
            SwissArmy.threadSleep(1000);
            if (execProcess != null) {
              execProcess.destroy();
            }
          }
        }
      });

      stdoutMonitor = new StreamMonitor(execProcess.getInputStream(), STDOUT);
      stdoutMonitor.addObserver(this);
      Thread stdoutThread = new Thread(stdoutMonitor);
      stdoutThread.setDaemon(true);
      stdoutThread.start();

      stderrMonitor = new StreamMonitor(execProcess.getErrorStream(), STDERR);
      stderrMonitor.addObserver(this);
      Thread stderrThread = new Thread(stderrMonitor);
      stderrThread.setDaemon(true);
      stderrThread.start();

    } catch (IOException e) {
      Sancho.pDebug("exec:" + e);
    }
  }

  public void update(final Observable o, Object obj) {
    if (obj instanceof String) {
      final String newLine = (String) obj;

      if (!outputConsole.isDisposed())
        outputConsole.getDisplay().asyncExec(new Runnable() {
          public void run() {
            if (outputConsole.isDisposed())
              return;

            appendLine((StreamMonitor) o, newLine);
          }
        });
    }
  }

  class StreamMonitor extends Observable implements Runnable {
    private InputStream inputStream;
    private boolean keepAlive = true;
    private int type;

    public StreamMonitor(InputStream inputStream, int type) {
      this.inputStream = inputStream;
      this.type = type;
    }

    public int getType() {
      return type;
    }

    public void run() {
      try {
        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        while (keepAlive && ((line = in.readLine()) != null)) {
          if (line.startsWith("Core started"))
            coreStarted = true;
          setChanged();
          notifyObservers(line);
        }

        in.close();

      } catch (IOException e) {
        Sancho.pDebug("streamMonitor:" + e);
      }
    }

    public void stop() {
      keepAlive = false;
    }
  }
}