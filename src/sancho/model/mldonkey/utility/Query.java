/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.utility;

import java.util.ArrayList;
import java.util.List;

import sancho.core.ICore;
import sancho.model.mldonkey.enums.EnumQuery;

public class Query {

  private ICore core;

  private EnumQuery enumQuery;

  // ANDNOT|MODULE
  private Query query1;
  private Query query2;

  // AND|OR|HIDDEN
  private List queryList;

  private String string1;
  private String string2;

  public Query(ICore core) {
    this.queryList = new ArrayList();
    this.core = core;
  }

  public void addQuery(Query query) {
    this.queryList.add(query);
  }

  public int queryListSize() {
    return this.queryList.size();
  }

  public void addQueryToList(List list, Query query) {
    Object[] oArray = query.toObjectArray();
    for (int i = 0; i < oArray.length; i++)
      list.add(oArray[i]);
  }

  public Query createQuery() {
    return UtilityFactory.getQuery(core);
  }

  public EnumQuery getEnumQuery() {
    return enumQuery;
  }

  public Query[] getQueryList() {
    Query[] queryArray = new Query[queryList.size()];
    queryList.toArray(queryArray);
    return queryArray;
  }

  //guiEncoding#buf_query
  public void read(MessageBuffer messageBuffer) {
    this.enumQuery = EnumQuery.byteToEnum(messageBuffer.getByte());

    if (enumQuery == EnumQuery.AND || enumQuery == EnumQuery.OR || enumQuery == EnumQuery.HIDDEN) {

      int len = messageBuffer.getUInt16();
      Query query;
      for (int i = 0; i < len; i++) {
        query = createQuery();
        query.read(messageBuffer);
        queryList.add(query);
      }

    } else if (enumQuery == EnumQuery.ANDNOT) {

      query1 = createQuery();
      query1.read(messageBuffer);
      query2 = createQuery();
      query2.read(messageBuffer);

    } else if (enumQuery == EnumQuery.MODULE) {

      string1 = messageBuffer.getString();
      query1 = createQuery();
      query1.read(messageBuffer);

    } else {

      string1 = messageBuffer.getString();
      string2 = messageBuffer.getString();

    }
  }

  public void setEnumQuery(EnumQuery enumQuery) {
    this.enumQuery = enumQuery;
  }

  public void setQuery1(Query query) {
    this.query1 = query;
  }

  public void setQuery2(Query query) {
    this.query2 = query;
  }

  public void setString1(String string) {
    this.string1 = string;
  }

  public void setString2(String string) {
    this.string2 = string;
  }

  public Object[] toObjectArray() {
    List arrayList = new ArrayList();
    arrayList.add(new Byte((enumQuery.getByteValue())));

    if (enumQuery == EnumQuery.AND || enumQuery == EnumQuery.OR || enumQuery == EnumQuery.HIDDEN) {

      arrayList.add(new Short((short) queryList.size()));
      for (int i = 0; i < queryList.size(); i++)
        addQueryToList(arrayList, (Query) queryList.get(i));

    } else if (enumQuery == EnumQuery.ANDNOT) {

      addQueryToList(arrayList, query1);
      addQueryToList(arrayList, query2);

    } else if (enumQuery == EnumQuery.MODULE) {

      arrayList.add(string1);
      addQueryToList(arrayList, query1);

    } else {

      arrayList.add(string1);
      arrayList.add(string2);
    }
    return arrayList.toArray();
  }

}
