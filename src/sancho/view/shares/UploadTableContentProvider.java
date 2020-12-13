/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.shares;

import java.util.Observable;

import org.eclipse.jface.viewers.Viewer;

import sancho.core.Sancho;
import sancho.model.mldonkey.ClientStats;
import sancho.model.mldonkey.SharedFileCollection;
import sancho.utility.SwissArmy;
import sancho.view.utility.SResources;
import sancho.view.viewer.table.GTableContentProvider;

public class UploadTableContentProvider extends GTableContentProvider {

  private static final String S_UPLOADS = SResources.getString("l.uploads");

  private long lastTimeStamp;

  public UploadTableContentProvider(UploadTableView uTableViewer) {
    super(uTableViewer);
  }

  public Object[] getElements(Object inputElement) {
    synchronized (inputElement) {
      SharedFileCollection sharedFiles = (SharedFileCollection) inputElement;
      sharedFiles.clearAllLists();
      return sharedFiles.getValues();
    }
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    SharedFileCollection oSFC = (SharedFileCollection) oldInput;
    SharedFileCollection nSFC = (SharedFileCollection) newInput;

    if (oSFC != null)
      oSFC.deleteObserver(this);

    if (nSFC != null) {
      nSFC.addObserver(this);
      if (Sancho.hasCollectionFactory())
        gView.getCore().getClientStats().addObserver(this);
    }
  }

  public void update(Observable o, final Object obj) {
    if (gView == null || gView.isDisposed())
      return;

    if (o instanceof ClientStats) {
      if (!gView.isActive() || !gView.isVisible())
        return;

      final ClientStats clientStats = (ClientStats) o;

      if (System.currentTimeMillis() > (lastTimeStamp + 5000)) {
        lastTimeStamp = System.currentTimeMillis();

        gView.getViewFrame().updateCLabelTextInGuiThread(
            S_UPLOADS + SResources.S_COLON + clientStats.getNumSharedFiles() + SResources.S_OBS
                + SwissArmy.calcStringSize(clientStats.getUploadCounter()) + SResources.S_SLASH
                + gView.getCore().getSharedFileCollection().getTotalSizeString() + SResources.S_CB);
      }
    } else if (o instanceof SharedFileCollection) {
      final SharedFileCollection sharedFileCollection = (SharedFileCollection) o;

      tableViewer.getTable().getDisplay().asyncExec(new Runnable() { // sync
        public void run() {
          if (gView == null || gView.isDisposed())
            return;

          if (sharedFileCollection.removed())
            synchronized (sharedFileCollection) {
              tableViewer.remove(sharedFileCollection.getRemovedArray());
              sharedFileCollection.clearRemoved();
            }

          if (sharedFileCollection.added())
            synchronized (sharedFileCollection) {
              tableViewer.add(sharedFileCollection.getAddedArray());
              sharedFileCollection.clearAdded();
            }

          
          if (sharedFileCollection.updated())
            synchronized (sharedFileCollection) {
              tableViewer.update(sharedFileCollection.getUpdatedArray(), null);
              sharedFileCollection.clearUpdated();
            }
        }
      });
    }
  }
}