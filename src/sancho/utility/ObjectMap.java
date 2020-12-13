/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.WeakHashMap;

public class ObjectMap extends Observable {
  public static final int ADDED = 0;
  public static final int UPDATED = 1;
  public static final int REMOVED = 2;
  public static final Integer ADDED_OBJECT = new Integer(ADDED);
  public static final Integer UPDATED_OBJECT = new Integer(UPDATED);
  public static final Integer REMOVED_OBJECT = new Integer(REMOVED);
  private Map mainMap;
  private Map addedMap;
  private Map removedMap;
  private Map updatedMap;

  public ObjectMap() {
    this(false);
  }

  public ObjectMap(boolean weak) {
    if (weak)
      mainMap = new WeakHashMap();
    else
      mainMap = new HashMap();

    addedMap = new WeakHashMap();
    removedMap = new WeakHashMap();
    updatedMap = new WeakHashMap();
  }

  public boolean add(Object object) {
    if (object == null)
      return false;

    boolean notify = false;

    synchronized (this) {
      if (!mainMap.containsKey(object)) {
        mainMap.put(object, null);
        if (this.countObservers() > 0)
          addedMap.put(object, null);
        notify = true;
      }
    }
    if (notify) {
      this.setChanged();
      this.notifyObservers(ADDED_OBJECT);
    }
    return notify;
  }

  public synchronized boolean containsKey(Object object) {
    return mainMap.containsKey(object);
  }

  public void remove(Object object) {
    boolean notify = false;

    synchronized (this) {
      if (mainMap.containsKey(object)) {
        mainMap.remove(object);
        if (this.countObservers() > 0)
          removedMap.put(object, null);

        notify = true;
      }
    }

    if (notify) {
      this.setChanged();
      this.notifyObservers(REMOVED_OBJECT);
    }
  }

  public void addOrUpdate(Object object) {
    if (object == null)
      return;

    boolean notify = false;

    if (!add(object)) {
      if (this.countObservers() > 0) {
        synchronized (this) {
          updatedMap.put(object, null);
        }
      }
      notify = true;
    }

    if (notify) {
      this.setChanged();
      this.notifyObservers(UPDATED_OBJECT);
    }
  }

  public synchronized boolean added() {
    return addedMap.size() > 0;
  }

  public synchronized Object[] getAddedArray() {
    return SwissArmy.toArray(addedMap.keySet());
  }

  public synchronized boolean removed() {
    return removedMap.size() > 0;
  }

  public synchronized Object[] getRemovedArray() {
    return SwissArmy.toArray(removedMap.keySet());
  }

  public synchronized boolean updated() {
    return updatedMap.size() > 0;
  }

  public synchronized Object removeFromMain(Object object) {
    return mainMap.remove(object);
  }

  public synchronized Object[] getUpdatedArray() {
    return SwissArmy.toArray(updatedMap.keySet());
  }

  public synchronized void clearAdded() {
    SwissArmy.clear(addedMap);
  }

  public synchronized void clearRemoved() {
    SwissArmy.clear(removedMap);
  }

  public synchronized void clearUpdated() {
    SwissArmy.clear(updatedMap);
  }

  public synchronized void clearAllLists() {
    SwissArmy.clear(addedMap);
    SwissArmy.clear(removedMap);
    SwissArmy.clear(updatedMap);
  }

  public synchronized boolean contains(Object object) {
    return mainMap.containsKey(object);
  }

  public synchronized Object[] getKeyArray() {
    return SwissArmy.toArray(mainMap.keySet());
  }

  public synchronized int size() {
    return mainMap.size();
  }

  public void notifyObject(Object object) {
    this.setChanged();
    this.notifyObservers(object);
  }

}