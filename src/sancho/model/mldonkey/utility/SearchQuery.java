/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.utility;

import java.util.ArrayList;
import java.util.List;

import sancho.core.ICore;
import sancho.model.mldonkey.enums.EnumQuery;
import sancho.utility.SwissArmy;
import sancho.view.utility.SResources;

public class SearchQuery {

  public static final String S_OR = "OR";
  public static final String S_AND = "AND";
  public static final String S_ANDNOT = "ANDNOT";

  public static final String S_KEYWORD = "Keyword";
  public static final String S_AUDIO = "Audio";
  public static final String S_VIDEO = "Video";
  public static final String S_SOFTWARE = "Software";
  public static final String S_IMAGE = "Image";

  private static final int OR = 1;
  private static final int AND = 2;
  private static final int ANDNOT = 3;
  private static int searchId;

  private List andNotList = new ArrayList();

  private Query andQuery;
  private Query firstQuery;
  private Query orQuery;

  private ICore core;

  private int currentType;
  private int keyWords;
  private int maxSearchResults;
  private int networkId;
  // guiEncoding#buf_search_type
  private byte searchType;

  public SearchQuery(ICore core) {
    this.core = core;
    andQuery = UtilityFactory.getQuery(core);
    andQuery.setEnumQuery(EnumQuery.AND);
    orQuery = UtilityFactory.getQuery(core);
    orQuery.setEnumQuery(EnumQuery.OR);
    searchType = 1;
    networkId = 0;
  }

  public void addKeyword(String word) {
    if (word == null)
      return;

    Query query = UtilityFactory.getQuery(core);
    query.setEnumQuery(EnumQuery.KEYWORDS);
    query.setString1(S_KEYWORD);
    query.setString2(word);
    keyWords++;

    if (keyWords == 1)
      firstQuery = query;
    else {
      switch (currentType) {
        case OR :
          if (firstQuery != null)
            orQuery.addQuery(firstQuery);
          orQuery.addQuery(query);
          firstQuery = null;
          break;
        case ANDNOT :
          andNotList.add(query);
          break;
        default :
          if (firstQuery != null)
            andQuery.addQuery(firstQuery);
          andQuery.addQuery(query);
          firstQuery = null;
          break;
      }
    }
  }

  public void addQueryToAnd(EnumQuery enumQuery, String string1, String string2) {
    Query query = UtilityFactory.getQuery(core);
    query.setEnumQuery(enumQuery);
    query.setString1(string1);
    query.setString2(string2);
    andQuery.addQuery(query);
  }

  public int getSearchId() {
    return searchId++;
  }

  public String parseWord(String word) {
    if (word.startsWith("-")) {
      currentType = ANDNOT;
      return word.substring(1);
    } else if (word.startsWith("+")) {
      currentType = AND;
      return word.substring(1);
    } else if (word.equalsIgnoreCase("|")) {
      currentType = OR;
      return null;
    } else if (word.equalsIgnoreCase(S_AND)) {
      currentType = AND;
      return null;
    } else if (word.equalsIgnoreCase(S_OR)) {
      currentType = OR;
      return null;
    } else if (word.equalsIgnoreCase(S_ANDNOT)) {
      currentType = ANDNOT;
      return null;
    } else {
      return word;
    }
  }

  public void send() {
    Query query;
    Query tQuery;

    if (firstQuery != null)
      if (andQuery.queryListSize() >= 1) {
        andQuery.addQuery(firstQuery);
        tQuery = andQuery;
      } else
        tQuery = firstQuery;
    else {
      if (orQuery.queryListSize() >= 1 && andQuery.queryListSize() >= 1) {
        andQuery.addQuery(orQuery);
        tQuery = andQuery;
      } else if (orQuery.queryListSize() >= 1) {
        tQuery = orQuery;
      } else {
        tQuery = andQuery;
      }
    }

    if (andNotList.size() > 0) {
      Query andNotQuery2;
      if (andNotList.size() > 1) {
        andNotQuery2 = UtilityFactory.getQuery(core);
        andNotQuery2.setEnumQuery(EnumQuery.OR);
        for (int i = 0; i < andNotList.size(); i++) {
          andNotQuery2.addQuery((Query) andNotList.get(i));
        }
      } else {
        andNotQuery2 = (Query) andNotList.get(0);
      }

      query = UtilityFactory.getQuery(core);
      query.setEnumQuery(EnumQuery.ANDNOT);
      query.setQuery1(tQuery);
      query.setQuery2(andNotQuery2);
    } else {
      query = tQuery;
    }

    List arrayList = new ArrayList();
    arrayList.add(new Integer(searchId));
    Object[] oArray = query.toObjectArray();
    for (int i = 0; i < oArray.length; i++)
      arrayList.add(oArray[i]);

    arrayList.add(new Integer(maxSearchResults));
    arrayList.add(new Byte(searchType));
    arrayList.add(new Integer(networkId));
    core.send(OpCodes.S_SEARCH_QUERY, arrayList.toArray());

  }

  public void setFormat(String format) {
    addQueryToAnd(EnumQuery.FORMAT, SResources.S_ES, format);
  }

  public void setLocalSearch() {
    searchType = 0;
  }

  public void setMaxSearchResults(int i) {
    maxSearchResults = i;
  }

  public void setMaxSize(long size) {
    addQueryToAnd(EnumQuery.MAXSIZE, SResources.S_ES, String.valueOf(size));
  }

  public void setMedia(String media) {
    addQueryToAnd(EnumQuery.MEDIA, SResources.S_ES, media);
  }

  public void setMinSize(long size) {
    addQueryToAnd(EnumQuery.MINSIZE, SResources.S_ES, String.valueOf(size));
  }

  public void setMp3Album(String album) {
    addQueryToAnd(EnumQuery.MP3_ALBUM, SResources.S_ES, album);
  }

  public void setMp3Artist(String artist) {
    addQueryToAnd(EnumQuery.MP3_ARTIST, SResources.S_ES, artist);
  }

  public void setMp3Bitrate(String bitrate) {
    addQueryToAnd(EnumQuery.MP3_BITRATE, SResources.S_ES, bitrate);
  }

  public void setMp3Title(String title) {
    addQueryToAnd(EnumQuery.MP3_TITLE, SResources.S_ES, title);
  }

  public void setNetwork(int i) {
    networkId = i;
  }

  public void setSearchString(String string) {
    String[] stringArray = SwissArmy.split(string, ' ');
    String word = SResources.S_ES;
    boolean inQuotes = false;
    for (int i = 0; i < stringArray.length; i++) {
      if (stringArray[i].startsWith("\"") && !inQuotes) {
        inQuotes = true;
        if (stringArray[i].length() > 1)
          word += (word.equals(SResources.S_ES) ? SResources.S_ES : " ") + stringArray[i].substring(1);
      } else if (stringArray[i].endsWith("\"") && inQuotes) {
        if (stringArray[i].length() > 2) {
          String pWord = stringArray[i].substring(0, stringArray[i].length() - 1);
          word += (word.equals(SResources.S_ES) ? SResources.S_ES : " ") + pWord;
          addKeyword(parseWord(word));
          word = SResources.S_ES;
          inQuotes = false;
        }
      } else if (inQuotes) {
        word += (word.equals(SResources.S_ES) ? SResources.S_ES : " ") + stringArray[i];
      } else
        addKeyword(parseWord(stringArray[i]));
    }

    if (!word.equals(SResources.S_ES))
      addKeyword(parseWord(word));
  }

  public void setSubscribeSearch() {
    searchType = 2;
  }
}