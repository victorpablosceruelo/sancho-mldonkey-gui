/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import java.util.Map;
import java.util.WeakHashMap;

import sancho.core.ICore;
import sancho.utility.SwissArmy;

public abstract class ACollection_Int2<T> extends ACollection_Int<T> {

  private Map addedMap = new WeakHashMap();
  private Map removedMap = new WeakHashMap();
  private Map updatedMap = new WeakHashMap();

  ACollection_Int2(ICore core) {
    super(core);
  }

  public synchronized boolean added() {
    return addedMap.size() > 0;
  }

  public synchronized void addToAdded(Object object) {
    if (object != null)
      addedMap.put(object, null);
  }

  public synchronized void addToRemoved(Object object) {
    if (object != null)
      removedMap.put(object, null);
  }

  public synchronized void addToUpdated(Object object) {
    if (object != null)
      updatedMap.put(object, null);
  }

  public synchronized void clearAdded() {
    SwissArmy.clear(addedMap);
  }

  public synchronized void clearAllLists() {
    SwissArmy.clear(addedMap);
    SwissArmy.clear(removedMap);
    SwissArmy.clear(updatedMap);
  }

  public synchronized void clearRemoved() {
    SwissArmy.clear(removedMap);
  }

  public synchronized void clearUpdated() {
    SwissArmy.clear(updatedMap);
  }

  public synchronized Object[] getAddedArray() {
    return SwissArmy.toArray(addedMap.keySet());
  }

  public synchronized Object[] getRemovedArray() {
    return SwissArmy.toArray(removedMap.keySet());
  }

  public synchronized Object[] getUpdatedArray() {
    return SwissArmy.toArray(updatedMap.keySet());
  }

  public synchronized boolean removed() {
    return removedMap.size() > 0;
  }

  public synchronized boolean updated() {
    return updatedMap.size() > 0;
  }
}