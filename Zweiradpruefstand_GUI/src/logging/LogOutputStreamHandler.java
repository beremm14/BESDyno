package logging;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * A <tt>LogOutputStreamHandler</tt> object takes log messages from a <tt>Logger</tt> and
 * prints them to an output stream.<br>
 * This Handler supports {@link logging.ExtendedLogRecord} objects. It marks debugging
 * records, prints location and call stack trace (if available),
 * {@link java.lang.Throwable} and {@link logging.LogRecordData} objects if available.
 * @author Manfred Steiner (sx@htl-kaindorf.ac.at)
 */
public class LogOutputStreamHandler extends Handler
{
  private final LogOutputPrintStream out;
  private boolean closed;
  private boolean showRecordHashcode;
  private boolean colorize;
  
  /**
   * Constructor.
   * Use system property {@code logging.LogOutputStreamHandler.showRecordHashcode}
   * to enable printing out class name and hashcode of any log record. If this
   * property is not set or set to value false, then these record details are not printed out
   * @param out the stream for publishing the incoming log records.
   */
  public LogOutputStreamHandler (OutputStream out)
  {
    if (out == null)
      throw new NullPointerException("Invalid value for Outputstream");
    if (out instanceof PrintStream)
      this.out = new LogOutputPrintStream((PrintStream)out);
      //this.out = (PrintStream)out;
    else
      this.out = new LogOutputPrintStream(new PrintStream(out));
      //this.out = new PrintStream(out);
    String propValue = System.getProperty("logging.LogOutputStreamHandler.showRecordHashcode");
    showRecordHashcode = !(propValue == null || "false".equals(propValue));
    propValue = System.getProperty("logging.LogOutputStreamHandler.colorize");
    colorize = propValue == null || !("false".equals(propValue));
  }
  
  /**
   * Publish a <tt>LogRecord</tt>.
   * @param  record  description of the log event. A null record is
   *                 silently ignored and is not published
   */
  @Override
  public void publish (LogRecord record)
  {
    if (record==null || record.getLevel().intValue() < getLevel().intValue())
      return;
    ExtendedLogRecord r = record instanceof  ExtendedLogRecord ? (ExtendedLogRecord)record : null; 
    
    //if (record.getThrown() != null || ((r !=null) && (r.getStackTraceSize()>0 || r.getData()!=null)))
    //  out.println();
    
    if (!colorize)
      ;
    else if (record.getLevel().intValue()==Logger.DEBUG.intValue())
      out.bgCyan();
    else if (record.getLevel().intValue()>=Level.SEVERE.intValue())
      out.bgRed();
    else if (record.getLevel().intValue()>=Level.WARNING.intValue())
      out.bgYellow();
    else if (record.getLevel().intValue()>=Level.INFO.intValue())
      out.bgGreen();
    
    String s = String.format("%1$-7s%2$c%3$tT.%3$tL:", record.getLevel().getName(),
                            r!=null && r.isDebugRecord()  ? '*' : ' ', new Date(record.getMillis())); 
    out.print(s);
    if (colorize)
      out.bgDefault();
    out.write(' ');
    
    int len = s.length();
    if (record.getMessage() == null)
      len--;
    else
    {
      String msg = record.getMessage();
      for (int i=0; i<msg.length(); i++)
      {
        char c = msg.charAt(i);
        int x = (int)c;
        if (Character.isISOControl(c))
        {
          out.print("<");
          out.print(String.format("%02x", (int)c));
          out.print(">");
          len +=4;
        }
        else
        {
          out.print(c);
          len++;
        }
      }
    }
    
    if (r != null)
    {
      if (r.isLocationAvailable())
      {
        if (r.getMessage()!=null)
          out.print(' ');
        out.print("[");
        out.print(r.getLocationAsString());
        out.print("]");
      }
      if (!r.isStackTraceEmpty())
      {
        String format = String.format("%%%dd -> [%%s]", len-3);
        for (int i=0; i<r.getStackTraceSize(); i++)
        {
          out.println();
          out.print(String.format(format, r.getLocationDepth()-i-1, r.getStackTraceElementAsString(i)));
        }
        out.println();
        for (int i=0; i<80; i++) out.append('-');
      }
    }
    
    if (showRecordHashcode)
    {
      out.print(String.format(" (%s@%08x)", record.getClass().getName(), record.hashCode()));
    }    
    
    out.println();
    
    if (r != null && r.getData()!=null)
    {
      out.startBlock(80);
      r.getData().print(out);
      out.endBlock();
    }

    if (record.getThrown() != null)
    {
      out.startBlock(4, 80);
      record.getThrown().printStackTrace(out);
      out.endBlock();
    }

    out.flush();
  }

  /**
   * Getter-Method for attribute showRecordHashcode.
   * @return true when class-name and hashcode of log record should is printed out
   */
  public boolean isShowRecordHashcode ()
  {
    return showRecordHashcode;
  }

  /**
   * Setter-Method for attribute showRecordHashcode.
   * @param showRecordHashcode true when class-name and hashcode of log record should be printed out
   */
  public void setShowRecordHashcode (boolean showRecordHashcode)
  {
    this.showRecordHashcode = showRecordHashcode;
  }

  /**
   * Getter-Method for attribute colorize.
   * @return true when output is colorized with ANSI Escape sequences
   */
  public boolean isColorize ()
  {
    return colorize;
  }

  /**
   * Setter-Method for attribute colorize.
   * @param colorize true will colorize the output with ANSI Escape sequences
   */
  public void setColorize (boolean colorize)
  {
    this.colorize = colorize;
  }
  
  /**
   * Flush any buffered output.
   */
  @Override
  public void flush ()
  {
    synchronized(out)
    {
      out.flush();
    }
  }

  /**
   * Close this handler.<br>
   * Also the underlying output stream is closed, except it is the
   * {@link java.lang.System#out} or {@link java.lang.System#err} object.
   * @throws SecurityException if a security manager exists and if
   *         the caller does not have <tt>LoggingPermission("control")</tt>.
   */
  @Override
  public void close () throws SecurityException
  {
    synchronized(out)
    {
      if (closed)
        return;
      if (out != System.out && out != System.err)
        out.close();
      closed = true;
    }
  }
  
  /**
   * Checks its error state.<br>
   * The internal error state is set to <code>true</code> when the underlying output stream throws an
   * {@link Exception}
   * @return true if the underlying output stream has thrown an exception.
   */
  public boolean checkError() 
  {
    synchronized(out)
    {
      return out.checkError();
    }
  }
  
  private class LogOutputPrintStream extends PrintStream
  {
    private boolean blockActive;
    private String blockName;
    private int blockLeftIntend;
    private int blockLength;
    private boolean lineFeedDone;
    private boolean suppressMultipleLineFeeds;
    
    public LogOutputPrintStream (OutputStream out)
    {
      super(out);
    }

    public void startBlock (int length)
    {
      blockActive = true;
      blockName = null;
      blockLeftIntend = 0;
      blockLength = length;
    }

    public void startBlock (int leftIntend, int length)
    {
      blockActive = true;
      blockName = null;
      blockLeftIntend = leftIntend;
      blockLength = length;
    }
    
    public void startBlock (String name, int leftIntend, int length)
    {
      blockActive = true;
      blockName = name;
      blockLeftIntend = leftIntend;
      blockLength = length;
      if (!lineFeedDone)
        super.println();
      super.append("-- Begin ").append(blockName).append(' ');
      for (int i=0; i<(blockLength-10-blockName.length()-leftIntend); i++) print('-');
      println();
    }

    public void endBlock ()
    {
      if (!blockActive)
        return;
      if (!lineFeedDone)
        write('\n');
      
      blockActive = false;
      int l = blockLength;
      if (blockName != null)
      {
        super.append("-- End ").append(blockName).append(' ');
        l -= 7+blockName.length();
      }
      for (int i=0; i<l; i++)
        super.print('-');  
      
      write('\n');
      //super.write('\n');
      blockName = null;
      blockLeftIntend = 0;
      blockLength = 0;
    }


    public void setSuppressMultipleLineFeeds (boolean suppressMultipleLineFeeds)
    {
      this.suppressMultipleLineFeeds = suppressMultipleLineFeeds;
    }


    @Override
    public void write (byte[] buf, int off, int len)
    {
      for (int i=0; i<len; i++)
      {
        byte b = buf[off+i];
        if (suppressMultipleLineFeeds && lineFeedDone && b=='\n')
          continue;
        if (lineFeedDone && blockActive)
          for (int j=0; j<blockLeftIntend; j++) write(' ');
        write(b<0 ? b+256 : b);
      }
    }

    @Override
    public void write (int b)
    {
      if (suppressMultipleLineFeeds && lineFeedDone && b=='\n')
          return;
      lineFeedDone = b=='\n';
      super.write(b);
    }
    
    public void bgDefault ()
    {
      append("\033[0;49m");
    }
    
    public void bgRed ()
    {
      append("\033[0;41m");
    }
      
    public void bgGreen ()
    {
      append("\033[0;42m");
    }

    public void bgYellow ()
    {
      append("\033[0;43m");
    }

    public void bgCyan ()
    {
      append("\033[0;46m");
    }
    
    public void fgDefault ()
    {
      append("\033[0;39m");
    }
    
    public void fgWhite ()
    {
      append("\033[0;37m");
    }
    
  }
}
