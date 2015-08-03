package installer.libs.kuaiyuoxi.com.kyxinstaller.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.logging.Logger;

public class Base
{
  static Logger log = Logger.getLogger(Base.class.getName());
  public static final int BASE64DEFAULTLENGTH = 76;
  static int _base64length = 76;
  private static final byte[] base64Alphabet = new byte['ÿ'];
  private static final char[] lookUpBase64Alphabet = new char[64];

  static byte[] getBytes(BigInteger paramBigInteger, int paramInt)
  {
    paramInt = paramInt + 7 >> 3 << 3;

    if (paramInt < paramBigInteger.bitLength()) {
      throw new RuntimeException("error get bytes");
    }

    byte[] arrayOfByte1 = paramBigInteger.toByteArray();

    if ((paramBigInteger.bitLength() % 8 != 0) && (paramBigInteger.bitLength() / 8 + 1 == paramInt / 8))
    {
      return arrayOfByte1;
    }

    int i = 0;
    int j = arrayOfByte1.length;

    if (paramBigInteger.bitLength() % 8 == 0) {
      i = 1;

      j--;
    }

    int k = paramInt / 8 - j;
    byte[] arrayOfByte2 = new byte[paramInt / 8];

    System.arraycopy(arrayOfByte1, i, arrayOfByte2, k, j);

    return arrayOfByte2;
  }

  public static String encode(BigInteger paramBigInteger)
  {
    return encode(getBytes(paramBigInteger, paramBigInteger.bitLength()));
  }

  public static byte[] encode(BigInteger paramBigInteger, int paramInt)
  {
    paramInt = paramInt + 7 >> 3 << 3;

    if (paramInt < paramBigInteger.bitLength()) {
    	throw new RuntimeException("error get bytes");
    }

    byte[] arrayOfByte1 = paramBigInteger.toByteArray();

    if ((paramBigInteger.bitLength() % 8 != 0) && (paramBigInteger.bitLength() / 8 + 1 == paramInt / 8))
    {
      return arrayOfByte1;
    }

    int i = 0;
    int j = arrayOfByte1.length;

    if (paramBigInteger.bitLength() % 8 == 0) {
      i = 1;

      j--;
    }

    int k = paramInt / 8 - j;
    byte[] arrayOfByte2 = new byte[paramInt / 8];

    System.arraycopy(arrayOfByte1, i, arrayOfByte2, k, j);

    return arrayOfByte2;
  }





  public static byte[] decode(byte[] paramArrayOfByte)
  {
    return decodeInternal(paramArrayOfByte);
  }

  public static String encode(byte[] paramArrayOfByte)
  {
    return encode(paramArrayOfByte, 76);
  }



  protected static final boolean isWhiteSpace(byte paramByte)
  {
    return (paramByte == 32) || (paramByte == 13) || (paramByte == 10) || (paramByte == 9);
  }

  protected static final boolean isPad(byte paramByte) {
    return paramByte == 61;
  }

  public static String encode(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramInt < 4) {
      paramInt = 2147483647;
    }

    if (paramArrayOfByte == null) {
      return null;
    }
    int i = paramArrayOfByte.length * 8;
    if (i == 0) {
      return "";
    }

    int j = i % 24;
    int k = i / 24;
    int m = j != 0 ? k + 1 : k;
    int n = paramInt / 4;
    int i1 = (m - 1) / n;
    char[] arrayOfChar = null;

    arrayOfChar = new char[m * 4 + i1];

    int i2 = 0; int i3 = 0; int i4 = 0; int i5 = 0; int i6 = 0;

    int i7 = 0;
    int i8 = 0;
    int i9 = 0;
    int i11;
    int i12;
    int i10 = 0;
    for (; i10 < i1; i10++) {
      for (i11 = 0; i11 < 19; i11++) {
        i4 = paramArrayOfByte[(i8++)];
        i5 = paramArrayOfByte[(i8++)];
        i6 = paramArrayOfByte[(i8++)];

        i3 = (byte)(i5 & 0xF);
        i2 = (byte)(i4 & 0x3);

        i12 = (i4 & 0xFFFFFF80) == 0 ? (byte)(i4 >> 2) : (byte)(i4 >> 2 ^ 0xC0);

        int i13 = (i5 & 0xFFFFFF80) == 0 ? (byte)(i5 >> 4) : (byte)(i5 >> 4 ^ 0xF0);
        int i14 = (i6 & 0xFFFFFF80) == 0 ? (byte)(i6 >> 6) : (byte)(i6 >> 6 ^ 0xFC);

        arrayOfChar[(i7++)] = lookUpBase64Alphabet[i12];
        arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i13 | i2 << 4)];
        arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i3 << 2 | i14)];
        arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i6 & 0x3F)];

        i9++;
      }
      arrayOfChar[(i7++)] = '\n';
    }

    for (; i9 < k; i9++) {
      i4 = paramArrayOfByte[(i8++)];
      i5 = paramArrayOfByte[(i8++)];
      i6 = paramArrayOfByte[(i8++)];

      i3 = (byte)(i5 & 0xF);
      i2 = (byte)(i4 & 0x3);

      i10 = (i4 & 0xFFFFFF80) == 0 ? (byte)(i4 >> 2) : (byte)(i4 >> 2 ^ 0xC0);

      i11 = (i5 & 0xFFFFFF80) == 0 ? (byte)(i5 >> 4) : (byte)(i5 >> 4 ^ 0xF0);
      i12 = (i6 & 0xFFFFFF80) == 0 ? (byte)(i6 >> 6) : (byte)(i6 >> 6 ^ 0xFC);

      arrayOfChar[(i7++)] = lookUpBase64Alphabet[i10];
      arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i11 | i2 << 4)];
      arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i3 << 2 | i12)];
      arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i6 & 0x3F)];
    }

    if (j == 8) {
      i4 = paramArrayOfByte[i8];
      i2 = (byte)(i4 & 0x3);

      i10 = (i4 & 0xFFFFFF80) == 0 ? (byte)(i4 >> 2) : (byte)(i4 >> 2 ^ 0xC0);
      arrayOfChar[(i7++)] = lookUpBase64Alphabet[i10];
      arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i2 << 4)];
      arrayOfChar[(i7++)] = '=';
      arrayOfChar[(i7++)] = '=';
    } else if (j == 16) {
      i4 = paramArrayOfByte[i8];
      i5 = paramArrayOfByte[(i8 + 1)];
      i3 = (byte)(i5 & 0xF);
      i2 = (byte)(i4 & 0x3);

      i10 = (i4 & 0xFFFFFF80) == 0 ? (byte)(i4 >> 2) : (byte)(i4 >> 2 ^ 0xC0);
      i11 = (i5 & 0xFFFFFF80) == 0 ? (byte)(i5 >> 4) : (byte)(i5 >> 4 ^ 0xF0);

      arrayOfChar[(i7++)] = lookUpBase64Alphabet[i10];
      arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i11 | i2 << 4)];
      arrayOfChar[(i7++)] = lookUpBase64Alphabet[(i3 << 2)];
      arrayOfChar[(i7++)] = '=';
    }

    return new String(arrayOfChar);
  }

  public static final byte[] decode(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return decodeInternal(paramString.getBytes());
  }

  protected static final byte[] decodeInternal(byte[] paramArrayOfByte) {
    int i = removeWhiteSpace(paramArrayOfByte);

    if (i % 4 != 0) {
    	throw new RuntimeException("error get bytes");
    }

    int j = i / 4;

    if (j == 0) {
      return new byte[0];
    }
    byte[] arrayOfByte = null;
    int k = 0; int m = 0; int n = 0; int i1 = 0;

    int i2 = 0;
    int i3 = 0;
    int i4 = 0;

    i4 = (j - 1) * 4;
    i3 = (j - 1) * 3;

    k = base64Alphabet[paramArrayOfByte[(i4++)]];
    m = base64Alphabet[paramArrayOfByte[(i4++)]];
    if ((k == -1) || (m == -1))
    	throw new RuntimeException("error get bytes");
    byte b1;
    n = base64Alphabet[(b1 = paramArrayOfByte[(i4++)])];
    byte b2;
    i1 = base64Alphabet[(b2 = paramArrayOfByte[(i4++)])];
    if ((n == -1) || (i1 == -1))
    {
      if ((isPad(b1)) && (isPad(b2))) {
        if ((m & 0xF) != 0)
        	throw new RuntimeException("error get bytes");
        arrayOfByte = new byte[i3 + 1];
        arrayOfByte[i3] = (byte)(k << 2 | m >> 4);
      } else if ((!isPad(b1)) && (isPad(b2))) {
        if ((n & 0x3) != 0)
        	throw new RuntimeException("error get bytes");
        arrayOfByte = new byte[i3 + 2];
        arrayOfByte[(i3++)] = (byte)(k << 2 | m >> 4);
        arrayOfByte[i3] = (byte)((m & 0xF) << 4 | n >> 2 & 0xF);
      } else {
    	  throw new RuntimeException("error get bytes");
      }
    }
    else {
      arrayOfByte = new byte[i3 + 3];
      arrayOfByte[(i3++)] = (byte)(k << 2 | m >> 4);
      arrayOfByte[(i3++)] = (byte)((m & 0xF) << 4 | n >> 2 & 0xF);
      arrayOfByte[(i3++)] = (byte)(n << 6 | i1);
    }
    i3 = 0;
    i4 = 0;

    for (i2 = j - 1; i2 > 0; i2--) {
      k = base64Alphabet[paramArrayOfByte[(i4++)]];
      m = base64Alphabet[paramArrayOfByte[(i4++)]];
      n = base64Alphabet[paramArrayOfByte[(i4++)]];
      i1 = base64Alphabet[paramArrayOfByte[(i4++)]];

      if ((k == -1) || (m == -1) || (n == -1) || (i1 == -1))
      {
    	  throw new RuntimeException("error get bytes");
      }

      arrayOfByte[(i3++)] = (byte)(k << 2 | m >> 4);
      arrayOfByte[(i3++)] = (byte)((m & 0xF) << 4 | n >> 2 & 0xF);
      arrayOfByte[(i3++)] = (byte)(n << 6 | i1);
    }
    return arrayOfByte;
  }

  public static final void decode(byte[] paramArrayOfByte, OutputStream paramOutputStream)
    throws  IOException
  {
    int i = removeWhiteSpace(paramArrayOfByte);

    if (i % 4 != 0) {
    	throw new RuntimeException("error get bytes");
    }

    int j = i / 4;

    if (j == 0) {
      return;
    }

    int k = 0; int m = 0; int n = 0; int i1 = 0;

    int i2 = 0;

    int i3 = 0;

    for (i2 = j - 1; i2 > 0; i2--) {
      k = base64Alphabet[paramArrayOfByte[(i3++)]];
      m = base64Alphabet[paramArrayOfByte[(i3++)]];
      n = base64Alphabet[paramArrayOfByte[(i3++)]];
      i1 = base64Alphabet[paramArrayOfByte[(i3++)]];
      if ((k == -1) || (m == -1) || (n == -1) || (i1 == -1))
      {
    	  throw new RuntimeException("error get bytes");
      }

      paramOutputStream.write((byte)(k << 2 | m >> 4));
      paramOutputStream.write((byte)((m & 0xF) << 4 | n >> 2 & 0xF));
      paramOutputStream.write((byte)(n << 6 | i1));
    }
    k = base64Alphabet[paramArrayOfByte[(i3++)]];
    m = base64Alphabet[paramArrayOfByte[(i3++)]];

    if ((k == -1) || (m == -1))
    {
    	throw new RuntimeException("error get bytes");
    }
    byte b1;
    n = base64Alphabet[(b1 = paramArrayOfByte[(i3++)])];
    byte b2;
    i1 = base64Alphabet[(b2 = paramArrayOfByte[(i3++)])];
    if ((n == -1) || (i1 == -1))
    {
      if ((isPad(b1)) && (isPad(b2))) {
        if ((m & 0xF) != 0)
        	throw new RuntimeException("error get bytes");
        paramOutputStream.write((byte)(k << 2 | m >> 4));
      } else if ((!isPad(b1)) && (isPad(b2))) {
        if ((n & 0x3) != 0)
        	throw new RuntimeException("error get bytes");
        paramOutputStream.write((byte)(k << 2 | m >> 4));
        paramOutputStream.write((byte)((m & 0xF) << 4 | n >> 2 & 0xF));
      } else {
    	  throw new RuntimeException("error get bytes");
      }
    }
    else {
      paramOutputStream.write((byte)(k << 2 | m >> 4));
      paramOutputStream.write((byte)((m & 0xF) << 4 | n >> 2 & 0xF));
      paramOutputStream.write((byte)(n << 6 | i1));
    }
  }


  protected static int removeWhiteSpace(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return 0;
    }

    int i = 0;
    int j = paramArrayOfByte.length;
    for (int k = 0; k < j; k++) {
    	if(i== 27681 || i == 15069){
//    		System.out.println();
    	}
      byte b = paramArrayOfByte[k];
      if (!isWhiteSpace(b))
        paramArrayOfByte[(i++)] = b;
    }
    return i;
  }

  static
  {
    for (int i = 0; i < 255; i++) {
      base64Alphabet[i] = -1;
    }
    for (int i = 90; i >= 65; i--) {
      base64Alphabet[i] = (byte)(i - 65);
    }
    for (int i = 122; i >= 97; i--) {
      base64Alphabet[i] = (byte)(i - 97 + 26);
    }

    for (int i = 57; i >= 48; i--) {
      base64Alphabet[i] = (byte)(i - 48 + 52);
    }

    base64Alphabet[43] = 62;
    base64Alphabet[47] = 63;

    for (int i = 0; i <= 25; i++) {
      lookUpBase64Alphabet[i] = (char)(65 + i);
    }
    int  i = 26; for (int j = 0; i <= 51; j++) {
      lookUpBase64Alphabet[i] = (char)(97 + j);

      i++;
    }

    i = 52; for (int j = 0; i <= 61; j++) {
      lookUpBase64Alphabet[i] = (char)(48 + j);

      i++;
    }
    lookUpBase64Alphabet[62] = '+';
    lookUpBase64Alphabet[63] = '/';
  }
}