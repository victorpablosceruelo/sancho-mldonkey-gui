/*
 * Copyright (C) 2004-2005 Rutger M. Ovidius for use with the sancho project.
 * See LICENSE.txt for license information.
 */

package sancho.model.mldonkey.utility;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MessageEncoder {
  private ByteArrayOutputStream baOutputStream;
  private BufferedOutputStream bOutputStream;
  private byte[] numberBuffer;
  private byte[] header;
  private int length;

  private short opCode;
  private Socket socket;

  public MessageEncoder(Socket socket) {
    this.socket = socket;
    this.header = new byte[6];
    this.numberBuffer = new byte[8];
    this.baOutputStream = new ByteArrayOutputStream();
  }

  /**
   * Append a number to the content in LITTLE_ENDIAN. Must be one of: Short,
   * Integer, or Long
   * 
   * @param number
   */
  private void appendNumber(Number number) {
    int nLength;

    if (number instanceof Short) {
      nLength = 2;
      numberBuffer = toBytes((Short) number, numberBuffer, 0);
    } else if (number instanceof Integer) {
      nLength = 4;
      numberBuffer = toBytes((Integer) number, numberBuffer, 0);
    } else {
      nLength = 8;
      numberBuffer = toBytes((Long) number, numberBuffer, 0);
    }
    this.baOutputStream.write(numberBuffer, 0, nLength);
  }

  /**
   * Append an object to the content
   * 
   * @param object
   */
  private void appendObject(Object object) {
    if (object instanceof Byte)
      this.baOutputStream.write(((Byte) object).byteValue());
    else if (object instanceof Number)
      appendNumber((Number) object);
    else if (object instanceof String) {
      String string = (String) object;
      appendNumber(new Short((short) string.length()));
      this.baOutputStream.write(string.getBytes(), 0, string.length());
    }
  }

  /**
   * Creates the message payload from a given object array
   * 
   * @param oArray
   *          object array which represense the payload
   * @return size
   */
  private int createContent(Object[] oArray) {
    for (int i = 0; i < oArray.length; i++) {
      if (oArray[i] instanceof byte[]) {
        byte[] aByteArray = (byte[]) oArray[i];
        this.baOutputStream.write(aByteArray, 0, aByteArray.length);
      } else if (oArray[i].getClass().isArray()) {
        Object[] oArr = (Object[]) oArray[i];
        appendNumber(new Short((short) oArr.length));
        for (int j = 0; j < oArr.length; j++)
          appendObject(oArr[j]);
      } else
        appendObject(oArray[i]);
    }
    return baOutputStream.size();
  }

  private void createHeader() {
    this.header = toBytes(new Integer(this.length), header, 0);
    this.header = toBytes(new Short(this.opCode), header, 4);
  }

  public void send(short opCode, Object[] oArray) throws IOException {
    this.opCode = opCode;
    this.length = 2;
    this.baOutputStream.reset();

    if (oArray != null)
      this.length += createContent(oArray);

    this.createHeader();
    this.write();
  }

  private byte[] toBytes(Integer anInteger, byte[] byteBuffer, int offset) {
    int i = anInteger.intValue();
    byteBuffer[0 + offset] = (byte) (i & 0xFF);
    byteBuffer[1 + offset] = (byte) ((i & 0xFFFF) >> 8);
    byteBuffer[2 + offset] = (byte) ((i & 0xFFFFFF) >> 16);
    byteBuffer[3 + offset] = (byte) ((i & 0x7FFFFFFF) >> 24);
    return byteBuffer;
  }

  private byte[] toBytes(Long aLong, byte[] byteBuffer, int offset) {
    long l = aLong.longValue();
    for (int j = 0; j < 8; j++) {
      byteBuffer[j + offset] = (byte) (l % 256);
      l = l / 256;
    }
    return byteBuffer;
  }

  private byte[] toBytes(Short aShort, byte[] byteBuffer, int offset) {
    short value = aShort.shortValue();
    for (int j = 0; j < 2; j++) {
      byteBuffer[j + offset] = (byte) (value % 256);
      value = (short) (value / 256);
    }
    return byteBuffer;
  }

  public void write() throws IOException {
    if (bOutputStream == null)
      bOutputStream = new BufferedOutputStream(socket.getOutputStream());
    bOutputStream.write(header);
    if (length > 2)
      bOutputStream.write(baOutputStream.toByteArray());
    bOutputStream.flush();
  }
}
