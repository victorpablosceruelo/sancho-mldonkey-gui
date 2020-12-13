/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey;

import gnu.regexp.RE;
import gnu.regexp.REException;

import java.util.StringTokenizer;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;

import sancho.core.ICore;
import sancho.model.mldonkey.enums.EnumExtension;
import sancho.model.mldonkey.enums.EnumNetwork;
import sancho.model.mldonkey.enums.EnumRating;
import sancho.model.mldonkey.enums.EnumType;
import sancho.model.mldonkey.utility.MessageBuffer;
import sancho.model.mldonkey.utility.Tag;
import sancho.utility.SwissArmy;
import sancho.view.utility.SResources;

public class Result extends AObject {

  private static final String RS_MD4 = SResources.getString("r.tt.md4");
  private static final String RS_NETWORK = SResources.getString("r.tt.network");
  private static final String RS_FORMAT = SResources.getString("r.tt.format");
  private static final String RS_SIZE = SResources.getString("r.tt.size");
  private static final String RS_ADOWNLOADED = SResources.getString("r.tt.alreadyDownloaded");

  private static final String TAG_LENGTH = "length";
  private static final String TAG_TIME = "time";
  private static final String TAG_SECONDS = "seconds";
  private static final String TAG_CODEC = "codec";
  private static final String TAG_RESOLUTION = "resolution";
  private static final String TAG_SAMPLE_RATE = "sampleRate";
  private static final String TAG_BITRATE = "bitrate";
  private static final String TAG_QUALITY = "quality";
  private static final String TAG_AVAILABILITY = "availability";
  private static final String TAG_COMPLETESOURCES = "completesources";

  private static final String S_NEWLINE = "\n";

  protected static RE fakeRE;
  protected static RE pornographyFilterRE;
  protected static RE profanityFilterRE;

  protected boolean downloaded;
  protected boolean containsFake;
  protected boolean containsPornography;
  protected boolean containsProfanity;

  protected EnumExtension extensionEnum;
  protected String comment;
  protected String format;
  protected int id;
  protected String md4;
  protected String[] names;
  protected EnumNetwork networkEnum;
  protected EnumRating rating;
  protected long size;

  protected int tag_availability;
  protected int tag_bitrate;
  protected int tag_completesources = -1;
  protected String tag_codec;
  protected String tag_length;

  protected Tag[] tagList;
  protected String type;

  Result(ICore core) {
    super(core);
  }

  public synchronized boolean downloaded() {
    return downloaded;
  }

  public synchronized boolean containsFake() {
    return containsFake;
  }

  public synchronized boolean containsPornography() {
    return containsPornography;
  }

  public synchronized boolean containsProfanity() {
    return containsProfanity;
  }

  public boolean equals(Object obj) {
    return (obj instanceof Result && getId() == ((Result) obj).getId());
  }

  public synchronized int getAvail() {
    return tag_availability;
  }

  public synchronized int getBitrateTag() {
    return tag_bitrate;
  }

  public synchronized EnumNetwork getEnumNetwork() {
    return networkEnum;
  }

  public synchronized String getNetworkName() {
    return networkEnum.getName();
  }

  public synchronized Image getNetworkImage() {
    return networkEnum.getImage();
  }

  public synchronized String getBitrateTagString() {
    return tag_bitrate > 0 ? String.valueOf(tag_bitrate).intern() : SResources.S_ES;
  }

  public synchronized String getCodecTag() {
    return tag_codec != null ? tag_codec : SResources.S_ES;
  }

  public synchronized String getComment() {
    return comment != null ? comment : SResources.S_ES;
  }

  public synchronized int getCompleteSources() {
    return tag_completesources;
  }

  public String getCompleteSourcesString() {
    int result = getCompleteSources();
    if (result != -1)
      return String.valueOf(result).intern();
    else
      return SResources.S_ES;
  }

  // TODO: UNKNOWN network
  public String getED2K() {
    if (this.getName() != null
        && (this.getEnumNetwork() == EnumNetwork.DONKEY || this.getEnumNetwork() == EnumNetwork.UNKNOWN))
      return "ed2k://|file|" + this.getName() + "|" + this.getSize() + "|" + this.getMd4() + "|/";
    else
      return SResources.S_ES;
  }

  public synchronized String getFormat() {
    return format != null ? format : SResources.S_ES;
  }

  protected void calcFormat() {
    if (format == null || format.equals(SResources.S_ES)) {
      int index = this.getName().lastIndexOf(".");
      if (index != -1)
        format = this.getName().substring(index + 1).toLowerCase().intern();
    }
  }

  public synchronized int getId() {
    return id;
  }

  public synchronized String getLengthTag() {
    return tag_length != null ? tag_length : SResources.S_ES;
  }

  public synchronized String getMd4() {
    return md4 != null ? md4 : SResources.S_ES;
  }

  public String getName() {
    String[] result = this.getNames();
    return result.length >= 1 ? result[0] : SResources.S_ES;
  }

  public synchronized String[] getNames() {
    return names != null ? names : new String[0];
  }

  public synchronized EnumRating getRating() {
    return rating;
  }

  public String getRatingString() {
    StringBuffer ratingSB = new StringBuffer();
    ratingSB.append(rating.getName());
    ratingSB.append(SResources.S_OB);
    ratingSB.append(getAvail());
    ratingSB.append(SResources.S_CB);
    return ratingSB.toString().intern();
  }

  public synchronized long getSize() {
    return size;
  }

  public synchronized String getSizeString() {
    return SwissArmy.calcStringSize(getSize()).intern();
  }

  public Tag[] getTagList() {
    if (tagList == null || tagList.length == 0)
      return new Tag[0];
    else
      return tagList;
  }

  public String getToolTip() {
    return getName() + S_NEWLINE + getToolTipContent();
  }

  public String getToolTipContent() {
    StringBuffer stringBuffer = new StringBuffer();
    if (this.getEnumNetwork() == EnumNetwork.DONKEY) {
      stringBuffer.append(RS_MD4);
      stringBuffer.append(this.getMd4().toUpperCase());
      stringBuffer.append(S_NEWLINE);
    }
    stringBuffer.append(RS_NETWORK);
    stringBuffer.append(this.getEnumNetwork().getName());
    stringBuffer.append(S_NEWLINE);

    if (!this.getFormat().equals(SResources.S_ES)) {
      stringBuffer.append(RS_FORMAT);
      stringBuffer.append(this.getFormat());
      stringBuffer.append(S_NEWLINE);
    }

    stringBuffer.append(RS_SIZE);
    stringBuffer.append(this.getSizeString());

    Tag[] tagList = getTagList();
    Tag tag;
    for (int i = 0; i < tagList.length; i++) {
      tag = tagList[i];
      stringBuffer.append(S_NEWLINE);
      stringBuffer.append(tag.getName());
      stringBuffer.append(": ");
      if (tag.getType() == EnumType.STRING)
        stringBuffer.append(tag.getStringValue());
      else
        stringBuffer.append(tag.getValue());
    }

    if (downloaded()) {
      stringBuffer.append(S_NEWLINE);
      stringBuffer.append(RS_ADOWNLOADED);
    }
    return stringBuffer.toString();
  }

  public Image getToolTipImage() {
    Program p = null;

    if (!this.getFormat().equals(SResources.S_ES))
      p = Program.findProgram(this.getFormat());
    else {
      int index;
      String fileName = this.getName();

      if ((fileName != null) && ((index = fileName.lastIndexOf(".")) != -1))
        p = Program.findProgram(fileName.substring(index));
    }

    Image programImage = null;

    if (p != null) {
      if ((programImage = SResources.getImage(p.getName())) == null) {
        ImageData data = p.getImageData();

        if (data != null) {
          programImage = new Image(null, data);
          SResources.putImage(p.getName(), programImage);
        }
      }
    }
    return programImage;
  }

  public synchronized String getType() {
    if (type != null && !type.equals(SResources.S_ES))
      return type;
    else
      return extensionEnum != null ? extensionEnum.getName() : EnumExtension.UNKNOWN.getName();
  }

  public int hashCode() {
    return getId();
  }

  public boolean isDownloading() {
    return core.getFileCollection().containsHash(getMd4());
  }

  protected void parseTags() {
    tag_codec = SResources.S_ES;
    tag_length = SResources.S_ES;
    String tagName;
    for (int i = 0; i < tagList.length; i++) {
      tagName = tagList[i].getName();
      if (tagName.equals(TAG_LENGTH) || tagName.equals(TAG_TIME) || tagName.equals(TAG_SECONDS))
        tag_length = tagList[i].getStringValue();
      else if (tagName.equals(TAG_CODEC) || tagName.equals(TAG_RESOLUTION) || tagName.equals(TAG_SAMPLE_RATE))
        tag_codec = tagList[i].getStringValue();
      else if (tagName.equals(TAG_BITRATE))
        tag_bitrate = tagList[i].getValue();
      else if (tagName.equals(TAG_COMPLETESOURCES))
        tag_completesources = tagList[i].getValue();
      else if (tagName.equals(TAG_QUALITY)) {
        StringTokenizer st = new StringTokenizer(tagList[i].getStringValue());
        if (st.hasMoreTokens()) {
          try {
            tag_bitrate = Integer.parseInt(st.nextToken());
          } catch (Exception e) {
          }
        }
      } else if (tagName.equals(TAG_AVAILABILITY))
        tag_availability = tagList[i].getValue();
      if (tag_availability == 0)
        tag_availability = 1;
    }
  }

  protected void readUIDs(MessageBuffer messageBuffer) {
    this.md4 = messageBuffer.getMd4();
  }

  //guiEncoding#buf_result
  public void read(int id, MessageBuffer messageBuffer) {
    synchronized (this) {
      this.id = id;
      this.networkEnum = readNetworkEnum(messageBuffer);
      this.names = messageBuffer.getStringList();
      readUIDs(messageBuffer);
      this.size = readSize(messageBuffer);
      this.format = messageBuffer.getString();
      this.calcFormat();
      this.type = messageBuffer.getString();
      this.tagList = messageBuffer.getTagList();
      this.comment = messageBuffer.getString();
      this.downloaded = messageBuffer.getBool();
      this.regexFilters();
      this.parseTags();
      this.setRating();
      this.calcFileType();
    }
  }

  // guiEncoding#buf_result
  public void read(MessageBuffer messageBuffer) {
    read(messageBuffer.getInt32(), messageBuffer);
  }

  protected long readSize(MessageBuffer messageBuffer) {
    return messageBuffer.getInt32() & 0xFFFFFFFFL;
  }

  //  public void readJigle(int id, long size, String hash, int avail, String[]
  // filenames) {
  //    this.id = id;
  //    setSize(size);
  //    this.md4 = hash;
  //    this.tag_availability = avail;
  //    this.names = filenames;
  //    this.tag_length = Resources.S_ES;
  //    this.tag_codec = Resources.S_ES;
  //    this.tag_availability = avail;
  //    this.comment = Resources.S_ES;
  //    this.network = core.getNetworkCollection().getByEnum(EnumNetwork.DONKEY);
  //    this.stringSize = SwissArmy.calcStringSize(this.getSize());
  //    this.regexFilters();
  //    this.setRating();
  //  }

  protected void regexFilters() {
    if (core.getResultCollection().filterPornography || core.getResultCollection().filterProfanity) {
      for (int i = 0; i < names.length; i++) {
        if ((profanityFilterRE != null) && (profanityFilterRE.getMatch(names[i]) != null)) {
          containsProfanity = true;
          if (containsPornography)
            break;
        }

        if ((pornographyFilterRE != null) && (pornographyFilterRE.getMatch(names[i]) != null)) {
          containsPornography = true;
          if (containsProfanity)
            break;
        }

        if ((fakeRE != null) && (fakeRE.getMatch(names[i]) != null))
          containsFake = true;
      }
    }

    if (!containsFake && (fakeRE != null)) {
      if (fakeRE.getMatch(this.comment) != null)
        containsFake = true;
    }
  }

  protected void calcFileType() {
    EnumExtension enumExtension = EnumExtension.GET_EXT(getFormat());
    extensionEnum = enumExtension != null ? enumExtension : EnumExtension.UNKNOWN;
  }

  protected EnumNetwork readNetworkEnum(MessageBuffer messageBuffer) {
    return core.getNetworkCollection().getNetworkEnum(messageBuffer.getInt32());
  }

  protected void setRating() {
    this.rating = containsFake ? EnumRating.FAKE : EnumRating.intToEnum(getAvail());
  }

  static {
    // who knows how to filter this garbage properly...
    try {
      profanityFilterRE = new RE("fuck|shit", RE.REG_ICASE);
    } catch (REException e) {
      profanityFilterRE = null;
    }

    try {
      pornographyFilterRE = new RE("fuck|shit|porn|pr0n|pussy|xxx|sex|erotic|anal|lolita|sluts|fetish"
          + "|naked|incest|bondage|masturbat|blow.*job|barely.*legal", RE.REG_ICASE);
    } catch (REException e) {
      pornographyFilterRE = null;
    }

    try {
      fakeRE = new RE("fake", RE.REG_ICASE);
    } catch (REException e) {
      fakeRE = null;
    }
  }
}