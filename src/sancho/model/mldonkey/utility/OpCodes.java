/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.utility;

// guiEncoding.ml
public class OpCodes {
  /**
   * value = 0
   */
  public static final short R_CORE_PROTOCOL = 0;

  /**
   * value = 1
   */
  public static final short R_OPTIONS_INFO = 1;

  /**
   * value = 3
   */
  public static final short R_DEFINE_SEARCHES = 3;

  /**
   * value = 4
   */
  public static final short R_RESULT_INFO = 4;

  /**
   * value = 5
   */
  public static final short R_SEARCH_RESULT = 5;

  /**
   * value = 6
   */
  public static final short R_SEARCH_WAITING = 6;

  //  public static final short R_FILE_INFO = 7; (< 8)
  //  public static final short R_FILE_DOWNLOADED = 8; (< 9)

  /**
   * value = 9
   */
  public static final short R_FILE_UPDATE_AVAILABILITY = 9;

  /**
   * value = 10
   */
  public static final short R_FILE_ADD_SOURCE = 10;

  // public static final short R_SERVER_BUSY = 11;

  /**
   * value = 12
   */
  public static final short R_SERVER_USER = 12;

  /**
   * value = 13
   */
  public static final short R_SERVER_STATE = 13;

  // public static final R_SERVER_INFO = 14; (< 2)

  /**
   * value = 15
   */
  public static final short R_CLIENT_INFO = 15;

  /**
   * value = 16
   */
  public static final short R_CLIENT_STATE = 16;

  // public static final short R_CLIENT_FRIEND = 17;

  /**
   * value = 18
   */
  public static final short R_CLIENT_FILE = 18;

  /**
   * value = 19
   */
  public static final short R_CONSOLE = 19;

  /**
   * value = 20
   */
  public static final short R_NETWORK_INFO = 20;

  /**
   * value = 21
   */
  public static final short R_USER_INFO = 21;

  // public static final short R_ROOM_INFO = 22; (< 3)

  /**
   * value = 28
   */
  public static final short R_ROOM_MESSAGE = 23;

  /**
   * value = 27
   */
  public static final short R_ROOM_ADD_USER = 24;

  // public static final short R_CLIENT_STATS = 25; (< 5)

  /**
   * value = 26
   */
  public static final short R_SERVER_INFO = 26;

  /**
   * value = 27
   */
  public static final short R_MESSAGE_FROM_CLIENT = 27;

  // public static final short R_CONNECTED_SERVERS = 28;
  // public static final short R_DOWNLOAD_FILES = 29; (< 8)
  // public static final short R_DOWNLOADED_FILES = 30; (< 8)

  /**
   * value = 31
   */
  public static final short R_ROOM_INFO = 31;

  /**
   * value = 32
   */
  public static final short R_ROOM_REMOVE_USER = 32;

  // public static final short R_SHARED_FILE_INFO = 33; (< 10)

  /**
   * value = 34
   */
  public static final short R_SHARED_FILE_UPLOAD = 34;

  /**
   * value = 35
   */
  public static final short R_SHARED_FILE_UNSHARED = 35;

  /**
   * value = 36
   */
  public static final short R_ADD_SECTION_OPTION = 36;

  // public static final short R_CLIENT_STATES = 37; (< 6)

  /**
   * value = 38
   */
  public static final short R_ADD_PLUGIN_OPTION = 38;

  // public static final short R_CLIENT_STATES = 39; (< 10)
  // public static final short R_FILE_INFO = 40; (< 9)
  // public static final short R_DOWNLOAD_FILES = 41 (< 9)
  // public static final short R_DOWNLOADED_FILES = 42 (< 9)
  // public static final short R_FILE_INFO = 43; (< 14)
  // public static final short R_DOWNLOAD_FILES = 44; (< 14)
  // public static final short R_DOWNLOADED_FILES = 45; (< 14)

  /**
   * value = 46
   */
  public static final short R_FILE_DOWNLOAD_UPDATE = 46;

  /**
   * value = 47
   */
  public static final short R_BAD_PASSWORD = 47;

  /**
   * value = 48
   */
  public static final short R_SHARED_FILE_INFO = 48;

  /**
   * value = 49
   */
  public static final short R_CLIENT_STATS = 49;

  /**
   * value = 50
   */
  public static final short R_FILE_REMOVE_SOURCE = 50;

  /**
   * value = 51
   */
  public static final short R_CLEAN_TABLES = 51;

  /**
   * value = 52
   */
  public static final short R_DOWNLOAD = 52;

  /**
   * value = 53
   */
  public static final short R_DOWNLOADING_LIST = 53;
  /**
   * value = 54
   */
  public static final short R_DOWNLOADED_LIST = 54;
  /**
   * value = 55
   */
  public static final short R_UPLOADERS = 55;
  /**
   * value = 56
   */
  public static final short R_PENDING = 56;
  /**
   * value = 57
   */
  public static final short R_SEARCH = 57;

  public static final short R_VERSION = 58;
  
  /**
   * value = 0
   */
  public static final short S_CORE_PROTOCOL = 0;

  /**
   * value = 1
   */
  public static final short S_CONNECT_MORE = 1;

  /**
   * value = 2
   */
  public static final short S_CLEAN_OLD_SERVERS = 2;

  /**
   * value = 3
   */
  public static final short S_KILL_CORE = 3;

  /**
   * value = 4
   */
  public static final short S_EXTEND_SEARCH = 4;

  // public static final short S_PASSWORD = 5; (< 14)
  // public static final short S_SEARCH_QUERY = 6;
  // public static final short S_DOWNLOAD_QUERY = 7;

  /**
   * value = 8
   */
  public static final short S_DLLINK = 8;

  /**
   * value = 9
   */
  public static final short S_REMOVE_SERVER = 9;

  /**
   * value = 10
   */
  public static final short S_SAVE_OPTION = 10;

  /**
   * value = 11
   */
  public static final short S_REMOVE_DOWNLOAD = 11;

  /**
   * value = 12
   */
  // public static final short S_GET_SERVER_USERS = 12;
  /**
   * value = 13
   */
  public static final short S_SAVE_FILE_AS = 13;

  /**
   * value = 14
   */
  public static final short S_ADD_CLIENT_FRIEND = 14;

  /**
   * value = 15
   */
  public static final short S_ADD_USER_FRIEND = 15;

  /**
   * value = 16
   */
  public static final short S_REMOVE_FRIEND = 16;

  /**
   * value = 17
   */
  public static final short S_REMOVE_ALL_FRIENDS = 17;

  // public static final short S_FIND_FRIEIND = 18;
  // public static final short S_VIEW_USERS = 19;
  /**
   * value = 22;
   */
  public static final short S_CONNECT_ALL = 20;

  /**
   * value = 21
   */
  public static final short S_CONNECT_SERVER = 21;

  /**
   * value = 22
   */
  public static final short S_DISCONNECT_SERVER = 22;

  /**
   * value = 23
   */
  public static final short S_SWITCH_DOWNLOAD = 23;

  /**
   * value = 24
   */
  public static final short S_VERIFY_ALL_CHUNKS = 24;

  /**
   * value = 25
   */
  public static final short S_QUERY_FORMAT = 25;

  /**
   * value = 26
   */
  public static final short S_MODIFY_MP3_TAGS = 26;

  /**
   * value = 27
   */
  public static final short S_OLD_CLOSE_SEARCH = 27;

  /**
   * value = 28
   */
  public static final short S_SET_OPTION = 28;

  /**
   * value = 29
   */
  public static final short S_CONSOLE_MESSAGE = 29;

  /**
   * value = 30
   */
  public static final short S_PREVIEW = 30;

  /**
   * value = 31
   */
  public static final short S_CONNECT_FRIEND = 31;

  /**
   * value = 32
   */
  public static final short S_GET_SERVER_USERS = 32;

  /**
   * value = 33;
   */
  public static final short S_GET_CLIENT_FILES = 33;
  /**
   * value = 34
   */
  public static final short S_GET_FILE_LOCATIONS = 34;

  // public static final short S_GET_SERVER_INFO = 35;

  /**
   * value = 36
   */
  public static final short S_GET_CLIENT_INFO = 36;

  /**
   * value = 37
   */
  public static final short S_GET_FILE_INFO = 37;

  // public static final short S_GET_USER_INFO = 38;

  /**
   * value = 39
   */
  public static final short S_SEND_MESSAGE = 39;

  /**
   * value = 40
   */
  public static final short S_ENABLE_NETWORK = 40;

  // public static final short S_BROWSE_USER = 41;

  /**
   * value = 42
   */
  public static final short S_SEARCH_QUERY = 42;

  /**
   * value = 43
   */
  public static final short S_MESSAGE_TO_CLIENT = 43;

  /**
   * value = 44
   */
  public static final short S_GET_CONNECTED_SERVERS = 44;

  /**
   * value = 45
   */
  public static final short S_GET_DOWNLOADING_FILES = 45;

  /**
   * value = 46
   */
  public static final short S_GET_DOWNLOADED_FILES = 46;

  /**
   * value = 47
   */
  public static final short S_GUI_EXTENSIONS = 47;

  /**
   * value = 48
   */
  public static final short S_SET_ROOM_STATE = 48;

  /**
   * value = 49
   */
  public static final short S_REFRESH_UPLOAD_STATS = 49;

  /**
   * value = 50
   */
  public static final short S_DOWNLOAD = 50;

  /**
   * value = 51
   */
  public static final short S_SET_FILE_PRIO = 51;

  /**
   * value = 52
   */
  public static final short S_PASSWORD = 52;

  /**
   * value = 53
   */
  public static final short S_CLOSE_SEARCH = 53;

  /**
   * value = 54
   */
  public static final short S_ADD_SERVER = 54;

  /**
   * value = 55
   */
  public static final short S_MESSAGE_VERSION = 55;

  /**
   * value = 56
   */
  public static final short S_RENAME_FILE = 56;
  /**
   * value = 57
   */
  public static final short S_GET_UPLOADERS = 57;
  /**
   * value = 58
   */
  public static final short S_GET_PENDING = 58;
  /**
   * value = 59
   */
  public static final short S_GET_SEARCHES = 59;
  /**
   * value = 60
   */
  public static final short S_GET_SEARCH = 60;
  /**
   * value = 61
   */
  public static final short S_CONNECT_CLIENT = 61;
  /**
   * value = 62
   */
  public static final short S_DISCONNECT_CLIENT = 62;
  /**
   * value = 63
   */
  public static final short S_NETWORK_MESSAGE = 63;
  /**
   * value = 64
   */
  public static final short S_INTERESTED_IN_SOURCES = 64;
  
  /** value = 65 */
  public static final short S_GET_VERSION = 65;

}