
package logging;

import java.io.PrintStream;


/**
 * For printing a string as text block.<br>
 * <p>
 * In normal message string all control characters are 
 * translated into a human readable format. So for example
 * line-feeds ('\n') are printed out as "&lt;10&gt;".
 * When using a LogRecordDataString object for the message,
 * all control sequences are written out as they are, for example
 * a line-feed ('\n') will be written as real line feed. </p>
 * @author Manfred Steiner (sx@htl-kaindorf.ac.at)
 */
public class LogRecordDataString implements LogRecordData
{
  private final String str;
  private final int offset;
  private final int length;
  
  /**
   * Constructor.
   * @param str the string to write out 
   */
  public LogRecordDataString (String str)
  {
    this.str = str;
    offset = -1;
    length = -1;
  }
  
  /**
   * Constructor if only a part of the string should be written out.
   * @param str the string from which a substring is taken and written out.
   * @param offset the index of the first character written out
   * @param length the length of the string to write out
   */
  public LogRecordDataString (String str, int offset, int length)
  {
    this.str = str;
    this.offset = offset;
    this.length = length;
  }

  /**
   * Getter Metthod for the string.
   * @return the string
   */
  public String getString ()
  {
    return str;
  }

  /**
   * Getter methode for the index of the first written character.
   * @return the index of the first character written out
   */
  public int getOffset ()
  {
    return offset;
  }

  /**
   * Getter methode for the length of the string which is written out
   * @return the length of the string to write out
   */
  public int getLength ()
  {
    return length;
  }

  
  /**
   * Method which is called from handler to write out the stream.
   * @param out the PrintStream which is used to write out this objects data
   */
  @Override
  public void print (PrintStream out)
  {
    out.print(str);
    if (!str.endsWith("\n"))
      out.println();
  }

  /**
   * Returns a string representation of the object.<br>
   * This is some statistical information, and the hashcode of string
   * @return the string representation of the object
   */
  @Override
  public String toString ()
  {
    return String.format("String@%08x, length=%d", str.hashCode(), str.length());
  }

  
  
}
