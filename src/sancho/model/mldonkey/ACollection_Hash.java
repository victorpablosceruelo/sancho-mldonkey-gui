/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;


import java.util.Observable;
import java.util.Set;

import gnu.trove.map.hash.THashMap;
import sancho.core.ICore;

public abstract class ACollection_Hash extends Observable implements ICollection {
  protected ICore core;
  protected THashMap<Object, Object> infoMap;

  ACollection_Hash() {
    this(null);
  }

  ACollection_Hash(ICore core) {
    this.core = core;
    this.infoMap = new THashMap();
  }

  public synchronized boolean containsKey(Object key) {
    return this.infoMap.contains(key);
  }

  public void dispose() {
    deleteObservers();
  }

  public synchronized Set entrySet() {
    return this.infoMap.entrySet();
  }

  public synchronized Object get(Object key) {
    return this.infoMap.get(key);
  }

  public synchronized Set keySet() {
    return this.infoMap.keySet();
  }

  public synchronized Object put(Object key, Object value) {
    return this.infoMap.put(key, value);
  }

  public synchronized int size() {
    return this.infoMap.size();
  }
}