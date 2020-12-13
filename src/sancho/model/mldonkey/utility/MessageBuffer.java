/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project. See
 * LICENSE.txt for license information.
 */

package sancho.model.mldonkey.utility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.SocketException;

import sancho.core.ICore;
import sancho.view.utility.SResources;

// to be used like java.nio.ByteBuffer
public class MessageBuffer {
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  private static final Tag[] EMPTY_TAG_ARRAY = new Tag[0];
  private static final int CAPACITY = 8192;
  private static StringBuffer stringBuffer = new StringBuffer();
  private BufferedInputStream bInputStream;
  private byte[] byteArray;
  private ICore core;
  private byte[] intByteArray;
  private byte[] messageByteArray;
  private int lastLength;

  private int iterator;

  public MessageBuffer(ICore core, BufferedInputStream bInputStream) {
    this.core = core;
    this.iterator = 0;
    this.bInputStream = bInputStream;
    this.intByteArray = new byte[4];
    this.messageByteArray = new byte[CAPACITY];
  }

  // buf_bool
  public boolean getBool() {
    return getByte() == 1;
  }

  // raw_byte
  public byte getByte() {
    return byteArray[iterator++];
  }

  // buf_int
  public int getInt32() {
    return (getByte() & 0xff) | ((getByte() & 0xff) << 8) | ((getByte() & 0xff) << 16) | (getByte() << 24);
  }

  // buf_list buf_int
  public int[] getInt32List() {
    int len = getUInt16();
    int[] result = new int[len];
    for (int i = 0; i < len; i++)
      result[i] = getInt32();
    return result;
  }

  // buf_int8
  public short getInt8() {
    return (short) (getByte() & 0xFF);
  }

  // buf_ip
  public void getIP(byte[] byteArray4) {
    for (int i = 0; i < 4; i++)
      byteArray4[i] = getByte();
  }

  // buf_md4/md4.ml
  public String getMd4() {
    stringBuffer.setLength(0);
    short s;
    for (int i = 0; i < 16; i++) {
      s = getInt8();
      if (s <= 0xf)
        stringBuffer.append(0);
      stringBuffer.append(Integer.toHexString(s));
    }
    return stringBuffer.toString().intern();
  }

  // for File#priority
  public int getSignedInt32() {
    int result = 0;
    for (int i = 0; i < 4; i++)
      if (core.getProtocol() > 16)
        result |= ((int) (getByte() & 0xFF) << (i * 8));
      else
        result |= ((int) (getByte()) << (i * 8));
    return result;
  }

  // buf_string
  public String getString() {
    int stringLength = getUInt16();

    // stupid
    if (stringLength >= 0xFFFF) {
      stringLength = getInt32();
    }

    if (stringLength > 0) {
      String result = new String(byteArray, iterator, stringLength);
      this.iterator += stringLength;
      return result.intern();
    } else
      return SResources.S_ES;
  }

  // buf_list buf_string
  public String[] getStringList() {
    int len = getUInt16();

    if (len <= 0)
      return EMPTY_STRING_ARRAY;

    String[] result = new String[len];
    for (int i = 0; i < len; i++)
      result[i] = getString();
    return result;
  }

  // buf_list buf_tag
  public Tag[] getTagList() {

    int len = this.getUInt16();
    Tag[] tagArray;
    if (len > 0) {
      tagArray = new Tag[len];
      for (int i = 0; i < len; i++) {
        tagArray[i] = UtilityFactory.getTag(core);
        tagArray[i].read(this);
      }
      return tagArray;
    } else
      return EMPTY_TAG_ARRAY;
  }

  public int getUInt16() {
    int a = (int) (getByte() & 0xFF);
    int b = (int) (getByte() & 0xFF);
    return (a + 256 * b);
  }

  public long getUInt64() {
    long result = 0L;
    for (int i = 0; i < 8; i++) {
      long b = (getByte() & 0xFF);
      b <<= i * 8;
      result += b;
    }
    return result;
  }

  public byte[] read(byte[] b, int length) throws IOException {
    lastLength = length;
    if (b == null) {
      if (length > CAPACITY) {
        b = new byte[length];
      } else {
        b = messageByteArray;
      }
    }

    int result;
    int pos = 0;

    while (pos < length) {
      try {
        result = bInputStream.read(b, pos, length - pos);
        if (result <= 0)
          throw new IOException();
        pos += result;
      } catch (SocketException e) {
        throw new IOException();
      }
    }
    return b;
  }

  public int readMessageLength() throws IOException {
    byte[] b = read(intByteArray, 4);
    return ((int) b[0] & 0xFF) | (((int) b[1] & 0xFF) << 8) | (((int) b[2] & 0xFF) << 16)
        | (((int) b[3] & 0xFF) << 24);
  }

  public int readMessage() throws IOException {
    iterator = 0;
    this.byteArray = read(null, readMessageLength());
    return getUInt16();
  }

  public String getLastMessage() {
    return hexDump(this.byteArray, lastLength);
  }

  public int getLastLength() {
    return this.lastLength;
  }

  public static String hexDump(byte[] ba, int len) {
    int l = 0;
    StringBuffer buf = new StringBuffer(len);
    for (int i = 0; i < len;) {
      String lineNum = "0000000" + Integer.toString(l, 16);
      buf.append(lineNum.substring(lineNum.length() - 8)).append("  ");
      StringBuffer sBuf = new StringBuffer(16);
      int j = 0;
      char c;
      for (j = 0; i < len && j < 16; j++, i++) {
        buf.append(byteToHex(ba[i])).append(' ');
        c = (char) ba[i];
        sBuf.append(c > 32 && c < 127 ? c : '.');
        if (j == 7)
          buf.append(" ");
      }
      if (j < 8)
        buf.append(" ");
      for (int k = j; k < 16; k++) {
        buf.append("   ");
        sBuf.append(" ");
      }
      buf.append(" ").append("|").append(sBuf).append("|");
      if (i > 0 && i % 16 == 0) {
        buf.append("\n");
        l += 16;
      }
    }
    return buf.toString();
  }

  private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

  public static final String byteToHex(byte b) {
    char[] buf = new char[2];
    buf[0] = HEX_DIGITS[(b >>> 4) & 0x0F];
    buf[1] = HEX_DIGITS[b & 0x0F];
    return new String(buf);
  }

}