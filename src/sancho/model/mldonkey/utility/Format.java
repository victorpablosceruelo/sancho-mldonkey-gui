/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.utility;

import sancho.model.mldonkey.enums.EnumFormat;
import sancho.view.utility.SResources;

public class Format {

  private String avi_codec;
  private int avi_fps;
  private int avi_height;
  private int avi_rate;
  private int avi_width;

  private String extension;
  private EnumFormat format;
  private String kind;

  private String mp3_album;
  private String mp3_artist;
  private String mp3_comment;
  private int mp3_genre;
  private String mp3_title;
  private int mp3_tracknum;
  private String mp3_year;

  public synchronized String getAVICodec() {
    return avi_codec != null ? avi_codec : SResources.S_ES;
  }

  public synchronized int getAVIFPS() {
    return avi_fps;
  }

  public synchronized int getAVIHeight() {
    return avi_height;
  }

  public synchronized int getAVIRate() {
    return avi_rate;
  }

  public synchronized int getAVIWidth() {
    return avi_width;
  }

  public synchronized String getExtension() {
    return extension != null ? extension : SResources.S_ES;
  }

  public synchronized EnumFormat getFormat() {
    return format != null ? format : EnumFormat.UNKNOWN;
  }

  public synchronized String getKind() {
    return kind == null ? SResources.S_ES : kind;
  }

  public synchronized String getMP3Album() {
    return mp3_album == null ? SResources.S_ES : mp3_album;
  }

  public synchronized String getMP3Artist() {
    return mp3_artist == null ? SResources.S_ES : mp3_artist;
  }

  public synchronized String getMP3Comment() {
    return mp3_comment;
  }

  public synchronized int getMP3Genre() {
    return mp3_genre;
  }

  public synchronized String getMP3Title() {
    return mp3_title == null ? SResources.S_ES : mp3_title;
  }

  public synchronized int getMP3TrackNum() {
    return mp3_tracknum;
  }

  public synchronized String getMP3Year() {
    return mp3_year == null ? SResources.S_ES : mp3_year;
  }

  // guiEncoding#buf_format
  public synchronized void read(MessageBuffer messageBuffer) {
    this.format = EnumFormat.byteToEnum(messageBuffer.getByte());
    if (this.format == EnumFormat.GENERIC) {
      this.extension = messageBuffer.getString();
      this.kind = messageBuffer.getString();
    } else if (this.format == EnumFormat.AVI) {
      this.avi_codec = messageBuffer.getString();
      this.avi_width = messageBuffer.getInt32();
      this.avi_height = messageBuffer.getInt32();
      this.avi_fps = messageBuffer.getInt32();
      this.avi_rate = messageBuffer.getInt32();
    } else if (this.format == EnumFormat.MP3) {
      this.mp3_title = messageBuffer.getString();
      this.mp3_artist = messageBuffer.getString();
      this.mp3_album = messageBuffer.getString();
      this.mp3_year = messageBuffer.getString();
      this.mp3_comment = messageBuffer.getString();
      this.mp3_tracknum = messageBuffer.getInt32();
      this.mp3_genre = messageBuffer.getInt32();
    }
  }

}