/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.view.transfer.downloads;

import java.util.Observable;

import org.eclipse.jface.viewers.Viewer;

import sancho.core.Sancho;
import sancho.model.mldonkey.File;
import sancho.model.mldonkey.FileCollection;
import sancho.view.transfer.FileClient;
import sancho.view.utility.SResources;
import sancho.view.viewer.tableTree.GTableTreeContentProvider;

public class DownloadTableTreeContentProvider extends GTableTreeContentProvider {

  public DownloadTableTreeContentProvider(DownloadTableTreeView downloadTableTreeView) {
    super(downloadTableTreeView);
  }

  public Object[] getChildren(Object parent) {
    if (parent instanceof File) {
      return ((File) parent).getFileClientSetArray();
    }

    return EMPTY_ARRAY;
  }

  public Object[] getElements(Object element) {
    if (element instanceof FileCollection) {
      return ((FileCollection) element).getAllInteresting();
    }

    return EMPTY_ARRAY;
  }

  public Object getParent(Object child) {
    if (child instanceof FileClient)
      return ((FileClient) child).getFile();
    else if (child instanceof File)
      return tableTreeViewer.getInput();

    return null;
  }

  public boolean hasChildren(Object parent) {
    if (parent instanceof File)
      return (((File) parent).getFileClientSetSize() > 0);

    return false;
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.inputChanged(viewer, oldInput, newInput);

    if (oldInput != null) {
      ((Observable) oldInput).deleteObserver(this);
    }

    if (newInput != null) {
      ((Observable) newInput).addObserver(this);
      updateHeaderLabel();
    }
  }

  public void sendUpdate(Observable o, Object arg) {
    if (gView == null || gView.isDisposed())
      return;

    if (o instanceof FileCollection) {
      FileCollection fileCollection = (FileCollection) o;

      if (fileCollection.requiresRefresh()) {
        tableTreeViewer.refresh();
        updateHeaderLabel();
        return;
      }

      if (fileCollection.added()) {
        synchronized (fileCollection) {
          Object[] oArray = fileCollection.getAddedArray();
          gView.getTable().setRedraw(false);
          tableTreeViewer.add(tableTreeViewer.getInput(), oArray);
          gView.getTable().setRedraw(true);
          for (int i = 0; i < oArray.length; i++)
            ((File) oArray[i]).clearChangedBits();
          fileCollection.clearAdded();
        }
      }

      if (fileCollection.updated()) {
        synchronized (fileCollection) {
          Object[] oArray = fileCollection.getUpdatedArray();
          tableTreeViewer.update(oArray, SResources.SA_Z);
          for (int i = 0; i < oArray.length; i++)
            ((File) oArray[i]).clearChangedBits();
          fileCollection.clearUpdated();
        }
      }

      if (fileCollection.removed()) {
        synchronized (fileCollection) {
          Object[] oArray = fileCollection.getRemovedArray();
          gView.getTable().setRedraw(false);
          tableTreeViewer.remove(oArray);
          gView.getTable().setRedraw(true);
          for (int i = 0; i < oArray.length; i++)
            ((File) oArray[i]).clearChangedBits();
          fileCollection.clearRemoved();
        }
      }

      if (arg instanceof FileClient) {
        FileClient fileClient = (FileClient) arg;
        gView.getTable().setRedraw(false);
        if (fileClient.getDelete()) {
          tableTreeViewer.remove(fileClient);
        } else {
          tableTreeViewer.add(fileClient.getFile(), fileClient);
        }
        gView.getTable().setRedraw(true);
      }

      updateHeaderLabel();
    }

  }

  public void update(final Observable o, final Object object) {

    if (gView == null || gView.isDisposed())
      return;

    if (!gView.isVisible() || !gView.isActive()) {
      needsRefresh = true;
      return;
    }

    tableTreeViewer.getTableTree().getDisplay().syncExec(new Runnable() { 
          public void run() {
            sendUpdate(o, object);
          }
        });
  }

  public void updateHeaderLabel() {
    if (!Sancho.hasCollectionFactory())
      return;

    String header = gView.getCore().getFileCollection().getHeaderText();
    if (!gView.getViewFrame().getCLabel().getText().equals(header))
      gView.getViewFrame().updateCLabelText(header);
  }

}