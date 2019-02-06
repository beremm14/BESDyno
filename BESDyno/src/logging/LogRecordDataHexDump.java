
package logging;

import java.io.PrintStream;
import java.util.Arrays;


/**
 * For well formated printing of a byte array with {@link logging.Logger}.<br>
 * <p>The byte array is typically given to constructor in the following way:
 * <blockquote><pre style="font-weight:bold">
 * byte [] data = { -10, 65, 66, 0, 127, -1, -128 };
 * LOG.info(new LogRecordDataHexDump(data), "Data Block");
 * </pre></blockquote>
 * <p>
 * If data could change, you should copy the byte array, otherwise only
 * an error message is printed out:</p>
 * <blockquote><pre style="font-weight:bold">
 * byte [] data = { -10, 65, 66, 0, 127, -1, -128 };
 * LOG.info(new LogRecordDataHexDump(Arrays.copyOf(data, data.length), "Data Block"));
 * </pre></blockquote>
 * <p> You can also take only a part of this data array.</p>
 * <blockquote><pre style="font-weight:bold">
 * byte [] data = { 0x41, 0x42, 0x43, 0x44, 0x45, 0x30, 0x31, 0x32, 0x33, 0x34 };
 * LOG.info(new LogRecordDataHexDump(data, 2, 5), "Part of Data Block");
 * </pre></blockquote>
 * <p>This results in the following LOGGER output:</p>
 * <pre style="color:blue; font-size:80%">
 * INFO    10:48:49.218: Part of Data Block [at Test.main(...)]
 * --------------------- LogRecordDataHexDump Begin -------------------------------
 * 00000000:  43 44 45 30                                         CDE0 
 * ===================== LogRecordDataHexDump End =================================
 * </pre>
 * <p>If you want to preserve offset position in LOGGER output, use the constructor with offset parameter
 * {@link #LogRecordDataHexDump(byte[], int, int, int)}:</p>
 * <blockquote><pre style="font-weight:bold">
 * byte [] data = { 0x41, 0x42, 0x43, 0x44, 0x45, 0x30, 0x31, 0x32, 0x33, 0x34 };
 * LOG.info(new LogRecordDataHexDump(data, 2, 5, 2), "Part of Data Block");
 * </pre></blockquote>
 * <p>This results in the following LOGGER output:</p>
 * <pre style="color:blue; font-size:80%">
 * INFO    10:48:49.218: Part of Data Block [at Test.main(...)]
 * --------------------- LogRecordDataHexDump Begin -------------------------------
 * 00000002:  43 44 45 30                                         CDE0
 * ===================== LogRecordDataHexDump End =================================
 * @author Manfred Steiner (sx@htl-kaindorf.ac.at)
 * </pre>
 */
public class LogRecordDataHexDump implements LogRecordData
{
  private final byte [] data;
  private final int offset;
  private final int length;
  private final int start;
  private final int dataHashCode;
  
  /**
   * Constructor which takes object reference of data array.<br>
   * Data array must not change in future!
   * If data could change, make a copy of the array<br>
   * For example by using {@link java.util.Arrays#copyOfRange(boolean[], int, int) }
   * @param data data array from which bytes are taken.
   */
  public LogRecordDataHexDump (byte[] data)
  {
    if (data==null)
      throw new NullPointerException();
    this.data = data;
    dataHashCode = Arrays.hashCode(data);
    offset = 0;
    length = data.length;
    start = 0;
  }
  
  /**
   * Constructor which takes only a part of the data array.<br>
   * Data array must not change in future!
   * If data could change, make a copy of the array<br>
   * For example by using {@link java.util.Arrays#copyOfRange(boolean[], int, int) }
   * @param data data array from which bytes are taken.
   * @param from index of first byte printed
   * @param to index of last byte printed
   */
  public LogRecordDataHexDump (byte[] data, int from, int to)
  {
    if (data==null)
      throw new NullPointerException();
    this.data = data;
    dataHashCode = Arrays.hashCode(data);
    this.offset = from;
    length = to - from + 1;
    start = 0;
  }

  /**
   * Constructor which takes only a part of the data array with preserving the start offset in output.<br>
   * Data array must not change in future!
   * If data could change, make a copy of the array<br>
   * For example by using {@link java.util.Arrays#copyOfRange(boolean[], int, int) }
   * @param data data array from which bytes are taken.
   * @param from index of first byte printed
   * @param to index of last byte printed
   * @param start start index for printing
   */
  public LogRecordDataHexDump (byte[] data, int from, int to, int start)
  {
    this.data = data;
    dataHashCode = Arrays.hashCode(data);
    this.offset = from;
    length = to - from + 1;
    this.start = start;
  }
  
  public byte[] getData ()
  {
    return data;
  }

  public int getOffset ()
  {
    return offset;
  }


  public int getLength ()
  {
    return length;
  }


  public int getStart ()
  {
    return start;
  }
  
  
  @Override
  @SuppressWarnings("ImplicitArrayToString")
  public void print (PrintStream out)
  {
    if (Arrays.hashCode(data) != dataHashCode)
    {
      out.println(" Error: data modified since creating this LogRecord, use Arrays.copyOf()!");
      return;
    }
    StringBuilder sb = new StringBuilder();
    int add = (start/16)*16;
    char [] ascii = new char [16];
    int i;

    for (i=(start/16)*16; i<Math.min(length+start%16, data.length); i++)
    {
      if ((i%16) == 0 || add==start)
      {
        if (i>0)
          out.append("  ").println(ascii);
        out.print(String.format(" %08x:", add));
        add += 16;
        for (int j=0; j<ascii.length; j++)
          ascii[j] = ' ';
      }
      if ((i%4) ==0 ) out.append(' ');
      if ((start%16)>0 && i<start)
      {
        out.append(" ..");
        ascii[i%16] = '.';
      }
      else
      {
        int b = data[i+offset]<0 ? data[i+offset]+256 : data[i+offset];
        out.append(String.format(" %02x", b));
        if (b>=32 && b<127)
          ascii[i%16] = (char)b;
        else
          ascii[i%16] = '.';
      }
    }
    while (i%16 != 0)
    {
      if ((i%4) ==0 ) out.append(" ");
      out.print("   ");
      i++;
    }
    out.append("  ").println(ascii);
  }


  /**
   * Returns a string representation of the object.<br>
   * This is some statistical information, and the hashcode of byte array
   * @return the string representation of the object
   */
  @Override
  @SuppressWarnings("ImplicitArrayToString")
  public String toString ()
  {
    if (offset==0 && start==0 && length==data.length)
      return String.format("byte [%d]=%s", length, data.toString());
    else
      return String.format("byte [%d]=%s, offset=%d, start=%d", length, data.toString(), offset, start);
  }

  
  
}
