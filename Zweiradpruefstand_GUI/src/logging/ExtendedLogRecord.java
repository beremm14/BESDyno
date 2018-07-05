package logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * ExtendedLogRecord objects are used to pass logging requests between
 * the logging framework and individual log Handlers.<br>
 * ExtendedLogRecord extends {@link java.util.logging.LogRecord} to
 * preserve location, additional data, desired stack trace depth and 
 * the information if this record is only used for debugging purposes
 * @author Manfred Steiner (sx@htl-kaindorf.ac.at)
 */
public class ExtendedLogRecord extends LogRecord
{
  protected LogRecordData data;
  private final StackTraceElement location;
  private final StackTraceElement [] stackTrace;
  private final int locationDepth;
  private  boolean debugRecord;

  public ExtendedLogRecord (Level level, int locationStackTraceIndex)
  {
    this(level, locationStackTraceIndex+1, 0, (String)null);
  }

  public ExtendedLogRecord (Level level, int locationStackTraceIndex, String msg)
  {
    this(level, locationStackTraceIndex+1, 0, msg);
  }
   
  public ExtendedLogRecord (Level level, int locationStackTraceIndex, Throwable th)
  {
    this(level, locationStackTraceIndex+1, 0, th, (String)null);
  }

  public ExtendedLogRecord (Level level, int locationStackTraceIndex, Throwable th, String msg)
  {
    this(level, locationStackTraceIndex+1, 0, th, msg);
  }
  
  @SuppressWarnings("")
  public ExtendedLogRecord (Level level, int locationStackTraceIndex, int locationStackTraceDepth, Throwable th, String msg)
  {
    this(level, locationStackTraceIndex+1, locationStackTraceDepth, msg);
    setThrown(th);
  }
  
  public ExtendedLogRecord (Level level, int locationStackTraceIndex, LogRecordData data)
  {
    this(level, locationStackTraceIndex+1, 0, (String)null);
    this.data = data;
  }

  public ExtendedLogRecord (Level level, int locationStackTraceIndex, LogRecordData data, String msg)
  {
    this(level, locationStackTraceIndex+1, 0, msg);
    this.data = data;
  }
  
  @SuppressWarnings("")
  public ExtendedLogRecord (Level level, int locationStackTraceIndex, Throwable th, LogRecordData data)
  {
    this(level, locationStackTraceIndex+1, 0, (String)null);
    this.data = data;
    setThrown(th);
  }

  @SuppressWarnings("")
  public ExtendedLogRecord (Level level, int locationStackTraceIndex, Throwable th, LogRecordData data, String msg)
  {
    this(level, locationStackTraceIndex+1, 0, msg);
    this.data = data;
    setThrown(th);
  }
  
  public ExtendedLogRecord (Level level, int locationStackTraceIndex, int locationStackTraceDepth)
  {
    this(level, locationStackTraceIndex+1, locationStackTraceDepth, (String)null);
  }

  @SuppressWarnings("")
  public ExtendedLogRecord (Level level, int locationStackTraceIndex, int locationStackTraceDepth, Throwable th)
  {
    this(level, locationStackTraceIndex+1, locationStackTraceDepth, (String)null);
    setThrown(th);
  }

  @SuppressWarnings("")
  public ExtendedLogRecord (Level level, int locationStackTraceIndex, int locationStackTraceDepth, Throwable th, LogRecordData data)
  {
    this(level, locationStackTraceIndex+1, locationStackTraceDepth, (String)null);
    this.data = data;
    setThrown(th);
  }

  public ExtendedLogRecord (Level level, int locationStackTraceIndex, int locationStackTraceDepth, LogRecordData data)
  {
    this(level, locationStackTraceIndex+1, locationStackTraceDepth, (String)null);
    this.data = data;
  }

  public ExtendedLogRecord (Level level, int locationStackTraceIndex, int locationStackTraceDepth, LogRecordData data, String msg)
  {
    this(level, locationStackTraceIndex+1, locationStackTraceDepth, msg);
    this.data = data;
  }
  
  @SuppressWarnings("")
  public ExtendedLogRecord (Level level, int locationStackTraceIndex, int locationStackTraceDepth, Throwable th, LogRecordData data, String msg)
  {
    this(level, locationStackTraceIndex+1, locationStackTraceDepth, msg);
    this.data = data;
    setThrown(th);
  }
  
  
  public ExtendedLogRecord (Level level, int locationStackTraceIndex, int locationStackTraceDepth, String msg)
  {
    super(level, msg);
    StackTraceElement [] st = locationStackTraceIndex>=0 || locationStackTraceDepth>0 ? Thread.currentThread().getStackTrace() : null;
      
//    for (StackTraceElement e : st)
//      System.out.println(String.format("at %s.%s(%s:%d)",
//                         e.getClassName(), e.getMethodName(),
//                         e.getFileName(), e.getLineNumber()));
    
    if (st == null || locationStackTraceIndex<0 || locationStackTraceIndex>=st.length)
      location = null;
    else
      location = st[locationStackTraceIndex];

    int locationIndex = Math.abs(locationStackTraceIndex);
    locationDepth = st!=null ? st.length - locationIndex : -1;
    
    if (st == null || locationStackTraceDepth<1)
      stackTrace = null;
    else
    {
      int size = Math.min(locationStackTraceDepth, Math.max(0, st.length - locationIndex - 1));
      if (locationStackTraceIndex<0)
        size++;
      stackTrace = new StackTraceElement[size];
      for (int i=0; i<stackTrace.length; i++)
        stackTrace[i] = st[locationIndex + i + (locationStackTraceIndex<0 ? 0 : 1)];
    }
  }
      
   
  public int getStackTraceSize ()
  {
    return stackTrace == null ? 0 : stackTrace.length;
  }
  
  
  public StackTraceElement getStackTraceElement (int index)
  {
    return index>=0 && index<stackTrace.length ? stackTrace[index] : null;
  }
  

  public boolean isStackTraceEmpty ()
  {
    return stackTrace==null || stackTrace.length==0;
  }


  public LogRecordData getData ()
  {
    return data;
  }

  
  public int getLocationDepth ()
  {
    return locationDepth;
  }
  
  
  public boolean isLocationAvailable ()
  {
    return location != null;
  }
  
  
  public String getLocationAsString ()
  {
    return String.format("at %s.%s(%s:%d)",
                         location.getClassName(), location.getMethodName(),
                         location.getFileName(), location.getLineNumber());
  }

  
  public String getStackTraceElementAsString (int index)
  {
    StackTraceElement e = stackTrace[index];
    return String.format("at %s.%s(%s:%d)",
                         e.getClassName(), e.getMethodName(),
                         e.getFileName(), e.getLineNumber());
  }


  public boolean isDebugRecord ()
  {
    return debugRecord;
  }


  public void setDebugRecord (boolean debugRecord)
  {
    this.debugRecord = debugRecord;
  }
    
  
  
}
