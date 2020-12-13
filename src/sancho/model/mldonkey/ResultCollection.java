/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import gnu.trove.TIntIntHashMap;
import gnu.trove.TIntObjectHashMap;
import sancho.core.ICore;
import sancho.model.mldonkey.utility.OpCodes;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.model.mldonkey.utility.SearchWaiting;
import sancho.model.mldonkey.utility.UtilityFactory;
import sancho.utility.ObjectMap;
import sancho.view.preferences.PreferenceLoader;

public class ResultCollection extends ACollection_Int {

  //  private int externalResultID;
  //  private int externalSearchID;
  private TIntIntHashMap minAvailMap;
  public boolean filterProfanity;
  public boolean filterPornography;
  
  // must retain them all?
  private TIntObjectHashMap resultIntMap;
  
  public synchronized int getNumResults() {
    return resultIntMap.size();
  }

  ResultCollection(ICore core) {
    super(core);
    resultIntMap = new TIntObjectHashMap();
    minAvailMap = new TIntIntHashMap();
  }

  public void closeSearch(int searchId) {
    if (searchId < 0)
      remove(searchId);
    else {
      Object[] oArray = {new Integer(searchId), new Byte((byte) 1)};
      core.send(OpCodes.S_CLOSE_SEARCH, oArray);
    }
  }

  public void download(Result result, boolean force) {
    int resultID = result.getId();

    if (resultID < 0) {
      core.send(OpCodes.S_DLLINK, result.getED2K());
    } else {
      Object[] oArray = new Object[3];
      oArray[0] = result.getNames();
      oArray[1] = new Integer(resultID);
      oArray[2] = new Byte((byte) (force ? 1 : 0));
      core.send(OpCodes.S_DOWNLOAD, oArray);
    }
  }

  //  public int getExternalResultID() {
  //    return --externalResultID;
  //  }
  //
  //  public int getExternalSearchID() {
  //    return --externalSearchID;
  //  }

  public synchronized Result getResult(int key) {
    return (Result) resultIntMap.get(key);
  }

  protected boolean hasMinAvail(int searchID, int avail) {
    synchronized (minAvailMap) {
      return avail >= minAvailMap.get(searchID);
    }
  }

  //  public void jigleSearchComplete(JigleSearchComplete jigleSearchComplete) {
  //    this.setChanged();
  //    this.notifyObservers(jigleSearchComplete);
  //  }

  public int cnt;

  public void read(MessageBuffer messageBuffer) {
    int searchID = messageBuffer.getInt32();
    int resultID = messageBuffer.getInt32();
    
    Result result;

    synchronized (this) {
      if (resultIntMap.contains(resultID))
        result = (Result) resultIntMap.get(resultID);
      else {
        return;
      }
    }

    if (!hasMinAvail(searchID, result.getAvail()))
      return;

    if (containsKey(searchID))
      ((ObjectMap) get(searchID)).addOrUpdate(result);
    else {
      ObjectMap map = new ObjectMap();
      map.add(result);
      this.put(searchID, map);
    }

    this.setChanged();
    this.notifyObservers(this);
  }

  //  public void readJigle(int searchID, Object object) {
  //    if (object instanceof ObjectMap) {
  //      ObjectMap objectMap = (ObjectMap) object;
  //      this.put(searchID, objectMap);
  //      this.setChanged();
  //      this.notifyObservers(this);
  //    } else if (object instanceof JigleSearchComplete) {
  //      jigleSearchComplete((JigleSearchComplete) object);
  //    }
  //  }

  public void resultInfo(MessageBuffer messageBuffer) {
    int resultID = messageBuffer.getInt32();

    Result result = (Result) resultMapGet(resultID); 

    if (result != null) {
      result.read(resultID, messageBuffer);
    } else {
      result = core.getCollectionFactory().getResult();
      result.read(resultID, messageBuffer);
      resultMapPut(resultID, result);
    }

  }
  
  public synchronized Object resultMapGet(int key) {
    return resultIntMap.get(key); 
  }

  public synchronized boolean resultMapContains(int key) {
    return resultIntMap.contains(key);
  }

  public synchronized void resultMapPut(int key, Object value) {
    resultIntMap.put(key, value);
  }
  
  public void updatePreferences() {
    filterPornography = PreferenceLoader.loadBoolean("searchFilterPornography");
    filterProfanity = PreferenceLoader.loadBoolean("searchFilterProfanity");
  }
  
  public void searchWaiting(MessageBuffer messageBuffer) {
    SearchWaiting searchWaiting = UtilityFactory.getSearchWaiting(core);
    searchWaiting.read(messageBuffer);

    if (containsKey(searchWaiting.getId()))
      ((ObjectMap) get(searchWaiting.getId())).notifyObject(searchWaiting);
    else {
      this.setChanged();
      this.notifyObservers(searchWaiting);
    }
  }

  public void setMinAvail(int searchID, int minAvail) {
    synchronized (minAvailMap) {
      minAvailMap.put(searchID, minAvail);
    }
  }

}