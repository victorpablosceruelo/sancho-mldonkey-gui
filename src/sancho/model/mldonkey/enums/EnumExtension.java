/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.enums;

import java.util.Map;
import java.util.TreeMap;

public class EnumExtension extends AbstractEnum {
  public static final EnumExtension UNKNOWN = new EnumExtension(0, "");
  public static final EnumExtension AUDIO = new EnumExtension(1, "audio");
  public static final EnumExtension VIDEO = new EnumExtension(2, "video");
  public static final EnumExtension ARCHIVE = new EnumExtension(4, "archive");
  public static final EnumExtension CDIMAGE = new EnumExtension(8, "cdimage");
  public static final EnumExtension PICTURE = new EnumExtension(16, "picture");
  public static final EnumExtension DOCUMENT = new EnumExtension(32, "document");
  
  protected static final Map EXT_MAP;

  private EnumExtension(int i, String resString) {
    super(i, "e.extension." + resString);
  }

  static {

    EXT_MAP = new TreeMap();

    ADD_TO_MAP(EnumExtension.AUDIO, new String[]{"aac", "ape", "au", "flac", "mid", "mpc", "mp2", "mp3",
        "mp4", "wav", "ogg", "wma"});

    ADD_TO_MAP(EnumExtension.VIDEO, new String[]{"avi", "mpg", "mpeg", "ram", "rm", "asf", "vob", "divx",
        "vivo", "ogm", "mov", "wmv"});

    ADD_TO_MAP(EnumExtension.DOCUMENT, new String[]{"doc", "dot", "wpd", "oft", "xls", "wri", "xml", "ppt",
        "pdf", "rtf", "txt", "ps", "htm", "html", "nfo", "chm", "lit", "srt"});

    ADD_TO_MAP(EnumExtension.ARCHIVE, new String[]{"gz", "zip", "ace", "rar", "tar", "tgz", "bz2"});

    ADD_TO_MAP(EnumExtension.CDIMAGE, new String[]{"ccd", "sub", "cue", "bin", "iso", "nrg", "img", "bwa",
        "bwi", "bws", "bwt", "mds", "mdf"});

    ADD_TO_MAP(EnumExtension.PICTURE, new String[]{"jpg", "jpeg", "bmp", "gif", "tif", "tiff", "png", "psd"});

  }

  // originally from downloadTableMenuListener
  private static void ADD_TO_MAP(EnumExtension eNumExtension, String[] extensionList) {
    for (int i = 0; i < extensionList.length; i++)
      EXT_MAP.put(extensionList[i], eNumExtension);
  }

  public static synchronized EnumExtension GET_EXT(String ext) {
    return (EnumExtension) EXT_MAP.get(ext);
  }

}
