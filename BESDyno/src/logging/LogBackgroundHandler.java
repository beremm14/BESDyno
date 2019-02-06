package logging;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


/**
 * A <tt>LogBackgroundHandler</tt> object takes log messages from a <tt>Logger</tt> and
 * processes them in a background thread.<br>
 * A call to the method {@link #publish(LogRecord)} only queues the record in a list and
 * is very cheap for the caller. A background thread takes the queued records and
 * forward them to the handler given to the constructor.
 * The background thread is created and started when this object is constructed.
 * So it needs to call {@link #close()} and/or {@link #awaitTermination(long, TimeUnit)}
 * to flush pending messages and to end the background thread.
 * @author Manfred Steiner (sx@htl-kaindorf.ac.at)
 */
public class LogBackgroundHandler extends Handler
{
  private final Handler handler;
  private final LinkedList<LogRecord> recordQueue;
  private final HandlerThread handlerThread;
  private final AtomicBoolean isTerminated;
  private boolean closed = false;
  private boolean shutdown =false;
  private int recordCount, maxQueueSize;
  
  /**
   * Starts a background thread to forward pending logging records.
   * @param handler the target handler of the incoming log records.
   */
  @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
  public LogBackgroundHandler (Handler handler)
  {
    if (handler==null)
      throw new NullPointerException();
    this.handler = handler;
    recordQueue = new LinkedList<>();
    isTerminated = new AtomicBoolean(false);
    handlerThread = new HandlerThread();
    //handlerThread.setDaemon(true);
    handlerThread.setName(String.format("%s/%s", LogBackgroundHandler.this.getClass().getSimpleName(), handler.getClass().getSimpleName()));
    handlerThread.start();
  }

  /**
   * Queue the given log record and forward them to the handler in background.
   * @param  record  description of the log event. A null record is
   *                 silently ignored and is not published
   */
  @Override
  public void publish (LogRecord record)
  {
    if (record==null)
      return;
    
    synchronized (recordQueue)
    {
      if (closed)
        System.err.println(String.format("%s: Handler is closed and will not accept any new records", getClass().getName()));
      else if (shutdown)
        System.err.println(String.format("%s: Handler is shutdown and will not accept any new records", getClass().getName()));
      else
      {
        recordQueue.add(record);
        recordQueue.notify();
        recordCount++;
        maxQueueSize = Math.max(maxQueueSize, recordQueue.size());
      }
    }
    
  }

  /**
   * Flush out all pending records
   */
  @Override
  public void flush ()
  {
    handler.flush();
  }

  /**
   * Flush pending records and close background thread.<br>
   * This method will not block, because closing procedure is done in background thread.
   * Call {@link #awaitTermination(long, TimeUnit) } to wait until all queued records are 
   * published and background thread has terminated.
   */
  @Override
  public void close ()
  {
    synchronized (recordQueue)
    {
      if (closed)
        return;
      closed = true;
      handlerThread.interrupt();
    }
  }

  
  /**
   * Shutdown this handler, the handlers {@link #publish(LogRecord) publish} method will not accept any new records.<br>
   * The queued record will be processed in normal way.
   * Call {@link #awaitTermination(long, TimeUnit) } to wait until all queued records are processed.
   */
  public void shutdown ()
  {
    synchronized (recordQueue)
    {
      shutdown = true;
      recordQueue.notify();
    }
  }
  
  
  /**
   * Shutdown this handler and interrupt its background thread.<br>
   * The logger will close as fast as possible. There may be some queued records,
   * which are not proper porcessed.
   * @return true if the background thread is terminated.
   */
  public boolean shutdownNow ()
  {
    close();
    synchronized (isTerminated)
    {
      if (isTerminated.get())
        return true;
    }
    handlerThread.interrupt();
    return false;
  }
  
  
  /**
   * Shutdown this handler and wait until all records are processed by the underlying handler.
   * A call to this method blocks the caller until no records are pending, timeout occurs or
   * thread is interrupted. The output stream will be closed afterwards. A seperate call
   * to the Method {@link #close()} is not needed.
   * @param timeout maximum time for blocking
   * @param unit time units for parameter timeout
   * @return true if no records are pending
   * @throws InterruptedException is thrown if cureent thread is interrupted
   */
  public boolean awaitTermination (long timeout, TimeUnit unit) throws InterruptedException
  {
    timeout = System.currentTimeMillis() + unit.toMillis(timeout); 
    shutdown();
    synchronized (isTerminated)
    {
      while (!isTerminated.get())
      {
        long millis = Math.max(0, timeout - System.currentTimeMillis());
        if (millis>0)
          isTerminated.wait(millis);
        else
        {
          // Timeout, Handler does not terminate in time
          break;
        }
      }
      return isTerminated.get();
    }
  }

  
  private class HandlerThread extends Thread
  {
    @Override
    @SuppressWarnings("NestedSynchronizedStatement")
    public void run ()
    {
      try
      {
        while (true)
        {
          try
          {
            LogRecord r = null;
            synchronized (recordQueue)
            {
              if (recordQueue.isEmpty())
              {
                if (isInterrupted() || shutdown)
                {
                  return;
                }
                recordQueue.wait();
              }
              else
                r = recordQueue.removeFirst();
            }
            if (r != null)
              handler.publish(r);
          }
          catch (InterruptedException ex)
          {
            return;
          }
        }
      }
      finally
      {
        synchronized (isTerminated)
        {
          isTerminated.set(true);
          handler.close();
          isTerminated.notify();
        }      
      }
    }
  }

  /**
   * Returns a string representation of the object.<br>
   * This is some statistical information, the class name and hashcode
   * @return the string representation of the object
   */
  @Override
  public String toString ()
  {
    return String.format("%s: %d records (max=%d)", super.toString(), recordCount, maxQueueSize);
  }
  
}
