/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import gnu.trove.procedure.TObjectProcedure;
import sancho.core.ICore;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.utility.SwissArmy;
import sancho.view.utility.SResources;

public class SharedFileCollection extends ACollection_Int2 {

  long totalSize;
  String totalSizeString;

  SharedFileCollection(ICore core) {
    super(core);
  }

  
   // not synced
  public void read(MessageBuffer messageBuffer) {
    
      int fileID = messageBuffer.getInt32();
      SharedFile sharedFile = (SharedFile) get(fileID);
      if (sharedFile != null) {
        if (sharedFile.readUpdate(fileID, messageBuffer)) {
          addToUpdated(sharedFile);
          this.setChanged();
          this.notifyObservers(sharedFile);
        }
      } else {
        sharedFile = core.getCollectionFactory().getSharedFile();
        sharedFile.read(fileID, messageBuffer);
        put(fileID, sharedFile);
        addToAdded(sharedFile);
        calculateTotalSize();
        this.setChanged();
        this.notifyObservers();
      }
  }

  public void reshare() {
    core.send(OpCodes.S_CONSOLE_MESSAGE, "reshare");
  }

  public void unshared(MessageBuffer messageBuffer) {
    int fileID = messageBuffer.getInt32();
    if (containsKey(fileID)) {
      addToRemoved(remove(fileID));
      calculateTotalSize();
      this.setChanged();
      this.notifyObservers();
    }
  }

  public void upload(MessageBuffer messageBuffer) {
    int fileID = messageBuffer.getInt32();
    SharedFile sharedFile = (SharedFile) get(fileID);
    if (sharedFile != null) {
      if (sharedFile.upload(fileID, messageBuffer)) {
        addToUpdated(sharedFile);
        this.setChanged();
        this.notifyObservers();
      }
    }
  }

  public synchronized void calculateTotalSize() {
    CalculateTotalSize c = new CalculateTotalSize();
    forEachValue(c);
    totalSize = c.getTotal();
    totalSizeString = SwissArmy.calcStringSize(totalSize);
  }

  public synchronized String getTotalSizeString() {
    return totalSizeString != null ? totalSizeString : SResources.S_ES;
  }

  static class CalculateTotalSize implements TObjectProcedure<SharedFile> {
    long total;

    public boolean execute(SharedFile object) {
      SharedFile sharedFile = (SharedFile) object;
      total += sharedFile.getSize();
      return true;
    }

    public long getTotal() {
      return total;
    }
  }

}