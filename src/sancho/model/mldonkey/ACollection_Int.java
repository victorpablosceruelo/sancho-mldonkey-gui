/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import java.util.Observable;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import sancho.core.ICore;

public abstract class ACollection_Int<T> extends Observable implements ICollection {
  protected ICore core;
  private TIntObjectHashMap<T> intObjectMap;

  ACollection_Int() {
    this(null);
  }

  ACollection_Int(ICore core) {
    this.core = core;
    this.intObjectMap = new TIntObjectHashMap();
  }

  public synchronized void clear() {
    this.intObjectMap.clear();
  }

  public synchronized boolean containsKey(int key) {
    return this.intObjectMap.contains(key);
  }

  public void dispose() {
    deleteObservers();
  }

  public synchronized boolean forEachValue(TObjectProcedure<T> procedure) {
    return intObjectMap.forEachValue(procedure);
  }

  public synchronized Object get(int key) {
    return intObjectMap.get(key);
  }

  public ICore getCore() {
    return this.core;
  }

  public synchronized Object[] getValues() {
    return this.intObjectMap.values();
  }

  public TIntObjectIterator<T> iterator() {
    return this.intObjectMap.iterator();
  }

  public synchronized T put(int key, T value) {
    return intObjectMap.put(key, value);
  }

  public synchronized Object remove(int key) {
    return intObjectMap.remove(key);
  }

  public synchronized boolean retainEntries(TIntObjectProcedure procedure) {
    return intObjectMap.retainEntries(procedure);
  }

  public synchronized int size() {
    return this.intObjectMap.size();
  }

  static class CleanIntMap implements TIntObjectProcedure<Object> {
    TIntArrayList retainIntList;

    public CleanIntMap(TIntArrayList retainIntList) {
      this.retainIntList = retainIntList;
    }

    public boolean execute(int i, Object object) {
      return retainIntList.contains(i);
    }
  }

}