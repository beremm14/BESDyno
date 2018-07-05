package logging;

import java.io.PrintStream;

/**
 * Interface for additional logging data objects.
 * @author Manfred Steiner (sx@htl-kaindorf.ac.at)
 */
public interface LogRecordData
{
  /**
   * Write formatted data to PrintStream.
   * @param out the PrintStream for writing the formatted data
   */
  public void print (PrintStream out);
}
