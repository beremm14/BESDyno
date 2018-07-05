package logging;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * A Logger object is used to log messages for a specific component.<br>
 * <p>
 * The Logger object internally uses the Java default {@link java.util.logging.Logger}
 * object and it's capabilities to log messages with Handler objects.</p>
 * <p>
 * Logger objects may be obtained by calls of the {@code getLogger}
 * factory method.  These will either create a new Logger or
 * return the existing Logger object with the desired name.
 * The name is normally the hierarchical dot-separated namespace of
 * the package and class name.</p>
 * <p>
 * Example how to get a Logger object in the class {@code Test}:</p>
 * <blockquote><pre style="font-weight:bold">
 * public class Test
 * {
 *   private static final Logger LOG = Logger.getLogger(Test.class.getName());
 * 
 *   ...
 * }
 * </pre></blockquote>
 * <!--<p>
 * It is important to note that the Logger returned by the {@code getLogger} factory method
 * may be garbage collected at any time if a strong reference to the Logger is not kept.</p>-->
 * <p>
 * Logging messages will be forwarded to registered Handler
 * objects, which can forward the messages to a variety of
 * destinations, including consoles, files, OS logs, etc.</p>
 * <p>
 * In contrast to Java's default {@link java.util.logging.Logger} object, all Logger objects
 * are automatically bound to one single "parent" Logger.
 * This ensures, that it is sufficient to add a Handler object one times to the "parent" Logger.</p>
 * <p>
 * Example of binding a Handler to the "parent" Logger object:</p>
 * <blockquote><pre style="font-weight:bold">
 * private static final Logger LOGP = Logger.getParentLogger();
 * 
 * public static void main (String[] args)
 * {
 *   LOGP.addHandler(new LogOutputStreamHandler(System.out));
 *   ...
 * }
 * </pre></blockquote>
 * <p>
 * <br>
 * <h3>Logger levels</h3>
 * <p>
 * Each Logger has a "Level" associated with it.  This reflects
 * a minimum Level that this logger cares about.  If a Logger's
 * level is set to <tt>null</tt>, then its effective level is inherited
 * from its parent.
 * By default the log level of the "parent" Logger is set to {@code ALL}.</p>
 * <p>
  * For Logger objects created with the factory method {@link #getLogger(String)}, the default logger level can
 * be set with the System Property <b>&lt;Logger-Name&gt;.Logger.Level</b>. This property can be defined
 * inside the Java program (using the method {@link System#setProperty(String, String) }, for example
 * in the static block of the Main-Class which is executed first), 
 * or from outside with the option -D when starting the Java VM with the command <b>java</b>.</p>
 * <p>
 * If this system property is not defined, the logger is configured with the value
 * of the System Property <b>logging.Logger.Level</b>. If this property is not defined the logger level 
 * is configured to Level.INFO by default.</p>
 * <p>
 * For the package and class name, the wildcards * and ? can be used. In case of
 * a name conflict, a complete name without wildcards is preferred. If the name is not found 
 * the value of the property logging.Logger.Level is choosen. If this property is not defined
 * the Level INFO is taken.</p>
 * <p>Here is an example:</p>
 * <ul>
 * <li>the Logger object of the class {@code main.Main} is configured to CONFIG</li>
 * <li>the Logger objects of all other classes in package {@code main.Main} are configured to INFO</li>
 * <li>all other Logger objects will be configured to WARNING</li>
 * </ul>
 * <blockquote><pre style="font-weight:bold">
 * java -jar myProgram.jar \
 *      -D"main.Main.Logger.Level=CONFIG" \
 *      -D"main.*.Logger.Level=INFO" \
 *      -D"*.Logger.Level=WARNING"
 * </pre></blockquote>
 * <p>
 * Instead of level names (SEVERITY, WARNING, ...) numbers can be used and will be automatically translated to
 * the matching predefined level or a new instance of the class {@link java.util.logging.Level}.</p>
 * <p>
 * The log level can also be dynamically changed by calls on the
 * {@link #setLevel(Level)} method. The {@link java.util.logging.Level} class supports
 * a number of predefined Levels ({@link java.util.logging.Level#SEVERE}, {@link java.util.logging.Level#WARNING}, ... ,
 * {@link java.util.logging.Level#FINEST}). 
 * These predefined values could be enlarged by extending the class {@link java.util.logging.Level}.</p>
 * <p>
 * <br>
 * <h3>Logging methods</h3>
 * <p>
 * On each logging method call the Logger initially performs a cheap
 * check of the request level (DEBUG, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST or ALL) against the
 * effective log level of the logger.  If the request level is
 * lower than the log level, the logging call returns immediately.</p>
 * <p>
 * After passing this initial (cheap) test, the Logger will create
 * a LogRecord object to describe the logging message. It will then call a
 * Filter (if present) to do a more detailed check on whether the
 * record should be published.  If that passes it will then publish
 * the LogRecord to its Handlers and to the "parent" Logger.</p>
 * <p>
 * The logging methods are grouped in the following categories:
 * <ul>
 * <li><b>log</b> methods; taking a individual log level and optionally some parameters.
 * <li><b>debug</b> methods; these methods are used while development and should be removed completely for released versions.
 * <li><b>severe</b> methods; for serious errors, ...
 * <li><b>warning</b> methods; for lightweighted problems
 * <li><b>info</b> methods; for normal information (e.g. program version, ...)
 * <li><b>config</b> methods; for configuration hints
 * <li><b>fine</b> methods; for increased verbosity of a program
 * <li><b>finer</b> methods; ...
 * <li><b>finest</b> methods; ...
 * <li><b>all</b> methods; for highest verbosity of a program, only handled
 *                if Logger is configured on level ALL which is the highest avaliable level.
 * </ul>
 * <p>
 * Each method group supports a various collection of overloaded methods:
 * <ul>
 * <li><b>no params</b>: LogRecord without any additional information (normally not used)
 * <li><b>msg</b>: LogRecord with a message string
 * <li><b>msgFormat</b>: LogRecord with a message string which is created with a {@link java.util.Formatter} (like {@code String.format} method)
 * <li><b>th</b>: LogRecord with a Throwable object (Exception or Error)
 * <li><b>data</b>: LogRecord with a byte array or a LogData Object
 * <li><b>stackTraceDepth</b>: LogRecord with call Stack-Trace in the desired depth
 * <li> ... and last but not least, any combination of the parameters above
 * </ul>
 * <p>
 * Here are some examples how to call log methods:</p>
 * <blockquote><pre style="font-weight:bold">
 * LOG.info("Hello world");
 * LOG.info("Starting application version %s", VERSION);
 * LOG.warning(exception, "Error occurs when parsing the file);
 * LOG.warning(exception, "Error occurs when parsing the file '%s'", filename);
 * LOG.finest(bytearray);
 * LOG.finest(bytearray, "data block of frame %d", frameId);
 * if (LOG.isFinestLoggable())
 *   LOG.finest(new LogRecordDataString(text));
 * ...
 * </pre></blockquote>
 * <p>
 * Control characters in messages are changed to a human readable format. For example
 * a line feed (value '\n'=10=0x0a) is changed to "&lt;0a&gt;".</p>
 * <p>To ensure a special output behavior for special kind of data,
 * a {@link logging.LogRecordData} object can be used as parameter of the log method.
 * This class is abstract and the method {@link logging.LogRecordData#print(PrintStream)}
 * must be implemented to get the desired output format.</p>
 * <p>There are some concrete implementations of {@link logging.LogRecordData} already available:
 * <ul>
 * <li>{@link logging.LogRecordDataString} ... for Strings with Line Feeds</li>
 * <li>{@link logging.LogRecordDataHexDump} ... for byte arrays in a hexdump output format</li>
 * <li>{@link logging.LogRecordDataFormattedText} ... for text with underlines, colors, ...</li>
 * </ul>
 * <br>
 * <h3>Logging filters</h3>
 * <p>
 * When logging a message, a cheap initial test checks the log level.
 * If the message is loggable the Logger will create a LogRecord object 
 * to describe the logging message. Afterwards it will call a
 * Filter to do a more detailed check on whether the record should be published.
 * The system property <b>&lt;Logger-Name&gt;.Logger.Filter</b> can be used to
 * configure a filter in the getLogger factory method.</p>
 * <p>Example to set a Filter for the logger of class main.Main:
 * <blockquote><pre style="font-weight:bold">
 * java -jar myProgram.jar -D"logging.Logger.Level=WARNING" -D"main.Main.Logger.Filter=main.MainFilter" 
 * </pre></blockquote>
 * <h3>Call location</h3>
 * <p>
 * If the {@code showLocation} attribute of the Logger object is {@code true}, the call location
 * is automatically stored in the LogRecord object by using the {@link logging.ExtendedLogRecord} class instead of
 * the normal {@link java.util.logging.LogRecord} class. In this case the location is printed out by the Handler.<br>
 * This feature is very useful inside the IDE, because locations are shown as hyperlink and can be selected easily.<br>
 * The {@code showLocation} attribute is true by default, but can be dynamically changed by calls on the
 * {@link #setShowLocation(boolean)} method.</p>
 * <p>
 * <br>
 * <h3>Publishing log records in background</h3>
 * <p>
 * Log records are processed by the Handler's publish method. 
 * This may need some extra time and could delay normal program flow.
 * If this additional delay is not desired (because it may lead to program malfunction), it is suggested
 * to delegate the handler's publish method to a background thread. This can
 * easily be done by using the class {@link logging.LogBackgroundHandler}.
 * In this case ensure that the {@link logging.LogBackgroundHandler} object is proper closed
 * on the end of the program, and time is given to flush out the queued records.
 * This can be done by call of the
 * methods {@link logging.LogBackgroundHandler#close()} and/or
 * {@link logging.LogBackgroundHandler#awaitTermination(long, TimeUnit)}.</p>
 * <p>
 * Example of binding a Background-Thread-Handler to the "parent" Logger object:</p>
 * <blockquote><pre style="font-weight:bold">
 * private static final Logger LOGP = Logger.getParentLogger();
 * 
 * public static void main (String[] args)
 * {
 *   try  
 *   {
 *     LOGP.addHandler(new LogBackgroundHandler(new LogOutputStreamHandler(System.out)));
 *     ...
 *   }
 *   finally
 *   {
 *     ((LogBackgroundHandler)Logger.getParentLogger().getHandlers()[0]).awaitTermination(1, TimeUnit.SECONDS);
 *   }
 * }
 * </pre></blockquote>
 * <h3>System properties</h3>
 * <p>
 * Special system properties are available in order to help doing a correct Logger configuration.
 * <table summary="Logging System Properties" border="1">
 *   <tr>
 *     <th>System Property Name</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td>{@code logging.Logger.printAll}</td>
 *     <td>Print logging configuration (Levels, Filters, Handlers) on {@code System.out}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code logging.Logger.printLevels}</td>
 *     <td>Print logging Level configuration on {@code System.out}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code logging.Logger.printFilters}</td>
 *     <td>Print Filter configuration on {@code System.out}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code logging.Logger.printHandlers}</td>
 *     <td>Print Handler configuration on {@code System.out}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code logging.Logger.Level}</td>
 *     <td>Log Level for all class Logger objects, if nothing defined in other way.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code logging.Logger.ParentLevel}</td>
 *     <td>Log Level for the "parent" logger class. Use this property if the default value (Level ALL)
 *         is not desired.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code <complete class-name>.Logger.Level}</td>
 *     <td>Log Level for the class's logger.<br>
 *         &lt;complete class-name&gt; is the hierarchical dot-separated namespace of the package and class name 
 *         (wildcards * and ? are possible).</td>
 *   </tr>
 *   <tr>
 *     <td>{@code <complete class-name>.Logger.Filter}</td>
 *     <td>Filter class for the class's logger.<br>
 *         &lt;complete class-name&gt; is the hierarchical dot-separated namespace of the package and class name 
 *         (wildcards * and ? are possible).</td>
 *   </tr>
 *   <tr>
 *     <td>{@code <complete class-name>.Logger.Handlers}</td>
 *     <td>Comma seperated list of Handler classes for the class's logger.<br>
 *         &lt;complete class-name&gt; is the hierarchical dot-separated namespace of the package and class name
 *         (wildcards * and ? are possible).</td>
 *   </tr>
 *   <tr>
 *   <tr>
 *     <td style="padding-right:15px">{@code logging.LogOutputStreamHandler.showRecordHashcode}</td>
 *     <td>Define this property if LogOutputStreamHandler should print out class name and hashcode of any log record.</td>
 *   </tr>
 *   <tr>
 *     <td style="padding-right:15px">{@code logging.LogOutputStreamHandler.colorize}</td>
 *     <td>Define this property if LogOutputStreamHandler should print out colorized log level (red for severe,
 *         yellow for warning, green for info, cyan for debug). For colorization ANSI escape sequences are used.
 *         This feature is enabled by default. It can be disabled by defining the value "false" for this property.
 *     </td>
 *   </tr>
 * </table>
 * 
 * 
 * 
 * @author Manfred Steiner (sx@htl-kaindorf.ac.at)
 */
public class Logger
{
  private static final HashMap<String,Logger> LOGGERMAP = new HashMap<>();
  private static Logger parentLogger;
  private static HashMap<String,String>cachedSystemProperties;
  private static boolean shutdownExecuted;
  private static boolean shutdownNowExecuted;
  
  private final static String PROPERTYPARENTLEVEL = "logging.Logger.ParentLevel";
  private final static String PROPERTYDEFAULTLEVEL = "logging.Logger.Level";

  /**
   * Log level used by default for debug method calls.
   * It's value is the highest possible log value (value of OFF minus 1).
   */
  public static final Level DEBUG = new SpecialLevel("DEBUG", Integer.MAX_VALUE-1);


  /**
   * Find or create a logger for a named subsystem.<br>
   * If a logger has already been created with the given name it is returned.
   * Otherwise a new logger is created.
   * @param name A name for the logger. This should
   *             be a dot-separated name, based on the package name and class name.
   * @return a suitable Logger object
   * @throws NullPointerException if the name is null.
   */
  public static Logger getLogger (String name)
  {
    synchronized (LOGGERMAP)
    {
      Logger logger = LOGGERMAP.get(name);

      if (shutdownExecuted && logger==null)
        throw new IllegalStateException("cannot create Logger after shutdown()");
      
      if (logger == null)
      {
        logger = new Logger(name);
        logger.setParent(getParentLogger());
        logger.setUseParentHandlers(true);
        String propName = name + ".Logger.Level";
        String level = getSystemProperty(propName);
        
        if (level == null)
        {
          propName = PROPERTYDEFAULTLEVEL;
          level = System.getProperty(propName);
          if (level==null)
            logger.logger.setLevel(Level.INFO);
        }
        if (level != null)
        {
          try
          {
            logger.logger.setLevel(parseLevel(level));
          }
          catch (Exception ex)
          {
            System.err.println(String.format("\nParsing '%s' from System Property '%s' fails, continue with Level.INFO",
                                             level, propName));
            logger.logger.setLevel(Level.INFO);
          }
        }
        
        propName = name + ".Logger.Filter";
        String filter = getSystemProperty(propName);
        if (filter != null)
        {
          try
          {
            Class c = Class.forName(filter);
            Filter f = (Filter)c.newInstance();
            logger.logger.setFilter(f);
          }
          catch (Exception ex)
          {
            System.err.println(String.format("\nParsing '%s' from System Property '%s' fails, no filter set.",
                                             filter, propName));
          }
        }

        propName = name + ".Logger.Handlers";
        String handlerList = getSystemProperty(propName);
        if (handlerList != null)
        {
          for (String hClassName : handlerList.split(","))
          {
            try
            {
              Class c = Class.forName(hClassName);
              Handler h = (Handler)c.newInstance();
              logger.logger.addHandler(h);
            }
            catch (Exception ex)
            {
              System.err.println(String.format("\nParsing '%s' from System Property '%s' fails, no handler added.",
                                               filter, propName));
            }
          }
        }
        printConfiguration(name, logger, System.out);
        LOGGERMAP.put(name, logger);
      }
      return logger;
    }
  }

  /**
   * Find or create the single "parent" logger.<br>
   * @return the "parent" Logger object
   */
  public static Logger getParentLogger ()
  {
    synchronized (LOGGERMAP)
    {
      if (shutdownExecuted && parentLogger==null)
        throw new IllegalStateException("cannot create Logger after shutdown()");
      
      if (parentLogger == null)
      {
        Logger oldParentLogger = parentLogger;
        final String name = "ParentLogger";
        parentLogger = new Logger(name);
        parentLogger.setUseParentHandlers(false);
        
        String level = getSystemProperty(PROPERTYPARENTLEVEL);
        if (level == null)
          parentLogger.logger.setLevel(Level.ALL);
        else
        {
          try
          {
            parentLogger.logger.setLevel(parseLevel(level));
          }
          catch (Exception ex)
          {
            System.err.println(String.format("\nParsing '%s' from System Property '%s' fails, continue with Level.INFO",
                                             level, PROPERTYPARENTLEVEL));
            parentLogger.logger.setLevel(Level.ALL);
          }
        }        
 
        if (oldParentLogger != null)
        {
          for (Logger l : LOGGERMAP.values())
            l.setParent(parentLogger);
        }
        
        printConfiguration(name, parentLogger, System.out);
      }
      return parentLogger;
    }
  }

  /**
    * Get the Handlers associated with this logger and all child Loggers.
    * <p>
    * @return  an array of all registered Handlers
    */  
  public static Handler [] getAllHandlers ()
  {
    synchronized (LOGGERMAP)
    {
      List<Handler> hl = new LinkedList<>();
      if (parentLogger != null)
        hl.addAll(Arrays.asList(parentLogger.getHandlers()));
      for (Logger l : LOGGERMAP.values())
        hl.addAll(Arrays.asList(l.getHandlers()));
      Handler [] rv = new Handler [hl.size()];
      return hl.toArray(rv);
    }
  }
  
  /**
   * Shutdown Logger system and close all Handler objects.<br>
   * After shutdown getLogger() and getParentLogger() will not create new Logger objects.
   * Handlers publish method will not accept any new records.
   */
  public static void shutdown ()
  {
    synchronized (LOGGERMAP)
    {
     if (shutdownExecuted)
       return;
     
      for (Handler h : getAllHandlers())
      {
        if (h instanceof LogBackgroundHandler)
          ((LogBackgroundHandler) h).shutdown();
        else
          h.close();
      }
        
      shutdownExecuted = true;
    }
  }
  
  /**
    * Attempts to stop all actively executing tasks, halts the
    * processing of waiting for prcessing pending records.
    * 
    * <p>This method does not wait for actively executing tasks to
    * terminate.  Use {@link #awaitTermination awaitTermination} to
    * do that.
    * @return list of handlers which are not terminated proper or null
    * if shutdownNow was already executed
    */
  public static List<Handler> shutdownNow ()
  {
    List<Handler> rv = new LinkedList<>();
    
    synchronized (LOGGERMAP)
    {
      if (shutdownNowExecuted)
        return null;

      if (!shutdownExecuted)
        shutdown();
      
      shutdownNowExecuted = true;
      for (Handler h : getAllHandlers())
      {
        if (h instanceof LogBackgroundHandler)
        {
          if (((LogBackgroundHandler)h).shutdownNow()==false)
            rv.add(h);
        }
      }
    }
    return rv;
  }


 /**
   * Shutdown Logger System and wait until all records (in all Handler objects) are processed.
   * A call to this method blocks the caller until no records are pending, timeout occurs or
   * logger threads are interrupted. The output stream will be closed afterwards. A seperate call
   * to the Method {@link #shutdown()} is not needed.
   * @param timeout maximum time for blocking
   * @param unit time units for parameter timeout
   * @return list of handler objects which are not terminated
   * @throws InterruptedException is thrown if cureent thread is interrupted
   */
  public static List<Handler> awaitTermination (long timeout, TimeUnit unit) throws InterruptedException
  {
    List<Handler> rv = new LinkedList<>();
    timeout = System.currentTimeMillis() + unit.toMillis(timeout);
    synchronized (LOGGERMAP)
    {
      shutdown();
      
      for (Handler h : getAllHandlers())
      {
        if (h instanceof LogBackgroundHandler)
        {
          try
          {
            long dt = Math.max(0, timeout - System.currentTimeMillis());
            if (!((LogBackgroundHandler)h).awaitTermination(dt, TimeUnit.MILLISECONDS))
              throw new TimeoutException();
          }
          catch (InterruptedException e) { throw e; }
          catch (Exception e)
          { 
            rv.add(h);
          }
        }
      }
    }
    return rv;
  }

  
  private static void printConfiguration (String name, Logger logger, PrintStream out)
  {
    printLevel(name, logger, out);
    printFilter(name, logger, out);
    printHandler(name, logger, out);
  }
  

  private static void printLevel (String name, Logger logger, PrintStream out)
  {
    boolean printStackTrace = System.getProperty("logging.Logger.printStackTrace") != null;
    boolean printAll = System.getProperty("logging.Logger.printAll") != null;
    boolean printLevels = System.getProperty("logging.Logger.printLevels") != null;

    if (printLevels || printAll)
    {
      String level = logger.getLevel() == null ? "null" : logger.getLevel().getName();
      System.out.println(String.format("%s.%s: setting level %s on logger %s (%s)", 
                                        Logger.class.getName(), printLevels? "printLevels" : "printAll",
                                        level, name, logger.toString()));
      if (printStackTrace)
      {
        StackTraceElement [] st = Thread.currentThread().getStackTrace();
        for (int i=3; i<st.length; i++)
          System.out.append("        at ").append(st[i].toString()).append(System.lineSeparator());
      }
    }
  }

  private static void printFilter (String name, Logger logger, PrintStream out)
  {
    boolean printStackTrace = System.getProperty("logging.Logger.printStackTrace") != null;
    boolean printAll = System.getProperty("logging.Logger.printAll") != null;
    boolean printFilters = System.getProperty("logging.Logger.printFilters") != null;

    if ((printFilters || printAll) && logger.getFilter()!=null)
    {
      System.out.println(String.format("%s.%s: setting filter %s on logger %s (%s)", 
                                       Logger.class.getName(), printFilters? "printFilters" : "printAll",
                                       logger.getFilter().getClass().getName(), name, logger.toString()));
      if (printStackTrace)
      {
        StackTraceElement [] st = Thread.currentThread().getStackTrace();
        for (int i=3; i<st.length; i++)
          System.out.append("        at ").append(st[i].toString()).append(System.lineSeparator());
      }
    }
  }
  
  private static void printHandler (String name, Logger logger, PrintStream out)
  {
    boolean printStackTrace = System.getProperty("logging.Logger.printStackTrace") != null;
    boolean printAll = System.getProperty("logging.Logger.printAll") != null;
    boolean printHandlers = System.getProperty("logging.Logger.printFilters") != null;    

    if (printHandlers || printAll)
    {
      for (Handler h : logger.getHandlers())
        System.out.println(String.format("%s.%s: setting handler %s on logger %s (%s)",
                                         Logger.class.getName(), printHandlers? "printHandlers" : "printAll",
                                         h.getClass().getName(), name, logger.toString()));
      if (printStackTrace)
      {
        StackTraceElement [] st = Thread.currentThread().getStackTrace();
        for (int i=3; i<st.length; i++)
          System.out.append("        at ").append(st[i].toString()).append(System.lineSeparator());
      }
    }
  }
  
  
  /**
   * Converts a wildcard expression (with * and ?) into a regular expression
   * @param wildcardExp the wildcard expression containing * for any count
   * of any character and ? for one character
   * @return the regular expression
   */  
  private static String getRegExp (String wildcardExp)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("\\Q");
    for (int i=0; i<wildcardExp.length(); i++)
    {
      char c = wildcardExp.charAt(i);
      switch (c)
      {
        case '?': sb.append("\\E.\\Q"); break;
        case '*': sb.append("\\E.*\\Q"); break;
        default: sb.append(c); break;
      }
    }
    sb.append("\\E");
    return sb.toString();
  }

  
  private static String getSystemProperty (String key)
  {
    if (cachedSystemProperties == null)
    {
      cachedSystemProperties = new HashMap<>();
      Properties props = System.getProperties();
      for (Entry e : props.entrySet())
      {
        String k = (String)e.getKey();
        if (k.endsWith(".Logger.Level") || k.endsWith(".Logger.Filter") || k.endsWith(".Logger.Handlers"))
          cachedSystemProperties.put(k, (String)e.getValue());
      }
    }
    try
    {
      String [] f = key.split("\\.");
      String type = String.format(".%s.%s", f[f.length-2], f[f.length-1]);
      for (String k : cachedSystemProperties.keySet())
      {
        if (!k.endsWith(type))
          continue;
        if (k.equals(key))
          return cachedSystemProperties.get(k);
      }
      for (String k : cachedSystemProperties.keySet())
      {
        if (!k.endsWith(type))
          continue;
        String regex = getRegExp(k);
        if (key.matches(regex))
        {
          boolean printAll = System.getProperty("logging.Logger.printAll") != null;
          boolean printWildCardMatches = System.getProperty("logging.Logger.printWildCardMatches") != null;
          if (printAll || printWildCardMatches)
          {
            System.out.println(String.format("%s.%s: matching %s -> %s", 
                       Logger.class.getName(), printWildCardMatches? "printWildCardMatches" : "printAll", k, key));
          }
          return cachedSystemProperties.get(k);
        }
      }
      return null;
    }
    catch (Exception ex)
    {
      return System.getProperty(key);
    }
  }
  
  private static Level parseLevel (String level) throws NumberFormatException, IllegalArgumentException
  {
    switch (level.toUpperCase())
    {
      case "ALL":     return Level.ALL;
      case "FINEST":  return Level.FINEST;
      case "FINER":   return Level.FINER;
      case "FINE":    return Level.FINE;
      case "CONFIG":  return Level.CONFIG;
      case "INFO":    return Level.INFO;
      case "WARNING": return Level.WARNING;
      case "SEVERE":  return Level.SEVERE;
      case "DEBUG":   return DEBUG;
      case "OFF":     return Level.OFF;
      default:
        int l = Integer.parseInt(level);
        if      (l==Level.ALL.intValue())    return Level.ALL;
        else if (l==Level.FINEST.intValue()) return Level.FINEST;
        else if (l==Level.FINER.intValue())  return Level.FINER;
        else if (l==Level.FINE.intValue())   return Level.FINE;
        else if (l==Level.CONFIG.intValue()) return Level.CONFIG;
        else if (l==Level.INFO.intValue())   return Level.INFO;
        else if (l==Level.SEVERE.intValue()) return Level.SEVERE;
        else if (l==DEBUG.intValue())        return DEBUG;
        else if (l==Level.OFF.intValue())    return Level.OFF;
        else
          return Level.parse(level);
    }
  }
  // *********************************************************
  
  private  final java.util.logging.Logger logger;
  private  boolean showLocation = true;
  private  Level debugLevel = DEBUG;
  private final String name;
  
  @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
  private Logger (String name)
  {
    this.name = name;
    logger = java.util.logging.Logger.getLogger(name);
  }

  
  private void setParent (Logger parent)
  {
    logger.setParent(parent.logger);
  }
  
  
  private void setUseParentHandlers (boolean useParentHandlers)
  {
    logger.setUseParentHandlers(useParentHandlers);
  }
  
  /**
   * Add a log Handler to receive logging messages.
   * <p>
   * By default, Loggers also send their output to their "parent" logger.
   * Typically only the "parent" Logger is configured with a set of Handlers
   * that essentially act as default handlers for all loggers.
   *
   * @param   handler a logging Handler
   * @throws  SecurityException if a security manager exists,
   *          this logger is not anonymous, and the caller
   *          does not have LoggingPermission("control").
   */
  public void addHandler (Handler handler) throws SecurityException
  {
    logger.addHandler(handler);
    printHandler(name, this, System.out);
  }
 
  /**
    * Remove a log Handler.
    * <P>
    * Returns silently if the given Handler is not found or is null
    *
    * @param   handler a logging Handler
    * @throws  SecurityException if a security manager exists,
    *          this logger is not anonymous, and the caller
    *          does not have LoggingPermission("control").
    */ 
  public void removeHandler (Handler handler) throws SecurityException
  {
    logger.removeHandler(handler);
  }
  
  /**
    * Get the Handlers associated with this logger.
    * <p>
    * @return  an array of all registered Handlers from this Logger object.
    */  
  public Handler [] getHandlers ()
  {
    return logger.getHandlers();
  }

  
  /**
   * Set a filter to control output on this Logger.
   * <P>
   * After passing the initial cheap "level" check, the Logger will
   * call this Filter to check if a log record should really
   * be published.
   *
   * @param   filter  a filter object (may be null)
   * @throws  SecurityException if a security manager exists,
   *          this logger is not anonymous, and the caller
   *          does not have LoggingPermission("control").
   */
  public void setFilter (Filter filter) throws SecurityException
  {
    logger.setFilter(filter);
    printFilter(name, this, System.out);
  }
   
  /**
   * Get the current filter for this Logger.
   *
   * @return  a filter object (may be null)
   */
  public Filter getFilter ()
  {
    return logger.getFilter();
  }

  
  /**
   * Get the current status if log method call location is stored and shown.
   *
   * @return true if location is shown.
   */ 
  public synchronized boolean getShowLocation ()
  {
    return showLocation;
  }
  
  /**
   * Set if the location of log method call shall be stored and shown.
   * @param showLocation true if location shall be shown.  
   */
  public synchronized void setShowLocation (boolean showLocation)
  {
    this.showLocation = showLocation;
  }
 
  /**
   * Get the log Level that has been specified for this Logger.
   * The result may be null, which means that this logger's
   * effective level will be inherited from its parent.
   *
   * @return this Logger's level
   */  
  public Level getLevel ()
  {
    Level rv = logger.getLevel();
    //if (rv == null && logger.getParent() != null)
    //  rv = logger.getParent().getLevel();
    return rv;
  }
  
  /**
   * Set the log level specifying which message levels will be
   * logged by this logger.  Message levels lower than this
   * value will be discarded.  The level value Level.OFF
   * can be used to turn off logging.
   * <p>
   * If the new level is null, it means that this node should
   * inherit its level from the "parent" logger.
   *
   * @param level   the new value for the log level (may be null)
   * @throws  SecurityException if a security manager exists,
   *          this logger is not anonymous, and the caller
   *          does not have LoggingPermission("control").
   */ 
  public void setLevel (Level level) throws SecurityException
  {
    logger.setLevel(level);
    printLevel(name, this, System.out);    
  }
  
  /**
   * Set the log level specifying which message levels will be
   * logged by this logger.  Message levels lower than this
   * value will be discarded.  The level value Level.OFF
   * can be used to turn off logging.
   * <p>
   * @param level the value as integer or one of the following values (case insensitive)
   * <ul>
   * <li>"OFF"</li>
   * <li>"DEBUG"</li>
   * <li>"SEVERE"</li>
   * <li>"WARNING"</li>
   * <li>"INFO"</li>
   * <li>"CONFIG"</li>
   * <li>"FINE"</li>
   * <li>"FINEST"</li>
   * <li>"FINER"</li>
   * </ul>
   * @throws NumberFormatException if level is no valid level value and not an integer value
   * @throws IllegalArgumentException if level has an illegal value
   * @throws NullPointerException if level is null 
   */
  public void setLevel (String level) throws NumberFormatException, IllegalArgumentException
  {
    logger.setLevel(parseLevel(level));
    printConfiguration(name, this, System.out);
  }

  // *************************************************************************
  
  private  void fireLogRecord (Level l)
  {
    if (isLoggable(l))
    {
      LogRecord r = showLocation ? new ExtendedLogRecord(l, 4) : new LogRecord(l, (String) null);
      logger.log(r);
    }
  }

  private void fireLogRecord (Level l, String msg)
  {
    if (isLoggable(l))
    {
      LogRecord r = showLocation ? new ExtendedLogRecord(l, 4, msg) : new LogRecord(l, msg);
      logger.log(r);
    }
  }

  private void fireLogRecord (Level l, String msgFormat, Object ... args)
  {
    if (isLoggable(l))
    {
      String msg = new java.util.Formatter().format(msgFormat, args).toString();
      LogRecord r = showLocation ? new ExtendedLogRecord(l, 4, msg) : new LogRecord(l, msg);
      logger.log(r);
    }
  }

  private void fireLogRecord (Level l, Throwable th)
  {
    if (isLoggable(l))
    {
      if (showLocation)
        logger.log(new ExtendedLogRecord(l, 4, th));
      else
        logger.log(l, (String)null, th);
    }
  }
  
  private void fireLogRecord (Level l, Throwable th, String msg)
  {
    if (isLoggable(l))
    {
      if (showLocation)
        logger.log(new ExtendedLogRecord(l, 4, th, msg));
      else
        logger.log(l, msg, th);
    }
  }

  private void fireLogRecord (Level l, Throwable th, String msgFormat, Object ... args)
  {
    if (isLoggable(l))
    {
      String msg = new java.util.Formatter().format(msgFormat, args).toString();
      if (showLocation)
        logger.log(new ExtendedLogRecord(l, 4, th, msg));
      else
        logger.log(l, msg, th);
    }
  }

  private void fireLogRecord (Level l, LogRecordData data)
  {
    if (isLoggable(l))
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, data));
  }

  private void fireLogRecord (Level l, LogRecordData data, String msg)
  {
    if (isLoggable(l))
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, data, msg));
  }

  private void fireLogRecord (Level l, LogRecordData data, String msgFormat, Object ... args)
  {
    if (isLoggable(l))
    {
      String msg = new java.util.Formatter().format(msgFormat, args).toString();
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, data, msg));
    }
  }

  private void fireLogRecord (Level l, byte [] data)
  {
    if (isLoggable(l))
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, new LogRecordDataHexDump(data)));
  }
  

  private void fireLogRecord (Level l, byte [] data, String msg)
  {
    if (isLoggable(l))
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, new LogRecordDataHexDump(data), msg));
  }
  
  private void fireLogRecord (Level l, byte [] data, String msgFormat, Object ... args)
  {
    if (isLoggable(l))
    {
      String msg = new java.util.Formatter().format(msgFormat, args).toString();
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, new LogRecordDataHexDump(data), msg));
    }
  }
  
  private void fireLogRecord (Level l, Throwable th, LogRecordData data)
  {
    if (isLoggable(l))
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, th, data));
  }
  
  private void fireLogRecord (Level l, Throwable th, LogRecordData data, String msg)
  {
    if (isLoggable(l))
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, th, data, msg));
  }

  private void fireLogRecord (Level l, Throwable th, LogRecordData data, String msgFormat, Object ...args)
  {
    if (isLoggable(l))
    {
      String msg = new java.util.Formatter().format(msgFormat, args).toString();
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, th, data, msg));
    }
  }

  private void fireLogRecord (Level l, int stackTraceDepth)
  {
    if (isLoggable(l))
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, stackTraceDepth));
  }

  private void fireLogRecord (Level l, int stackTraceDepth, String msg)
  {
    if (isLoggable(l))
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, stackTraceDepth, msg));
  }

  private void fireLogRecord (Level l, int stackTraceDepth, String msgFormat, Object ... args)
  {
    if (isLoggable(l))
    {
      String msg = new java.util.Formatter().format(msgFormat, args).toString();
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, stackTraceDepth, msg));
    }
  }
  
  private void fireLogRecord (Level l, int stackTraceDepth, Throwable th)
  {
    if (isLoggable(l))
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, stackTraceDepth, th));
  }

  private void fireLogRecord (Level l, int stackTraceDepth, Throwable th, String msg)
  {
    if (isLoggable(l))
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, stackTraceDepth, th, msg));
  }

  private void fireLogRecord (Level l, int stackTraceDepth, Throwable th, String msgFormat, Object ... args)
  {
    if (isLoggable(l))
    {
      String msg = new java.util.Formatter().format(msgFormat, args).toString();
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, stackTraceDepth, th, msg));
    }
  }

  private void fireLogRecord (Level l, int stackTraceDepth, LogRecordData data)
  {
    if (isLoggable(l))
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, stackTraceDepth, data));
  }

  private void fireLogRecord (Level l, int stackTraceDepth, LogRecordData data, String msg)
  {
    if (isLoggable(l))
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, stackTraceDepth, data, msg));
  }

  private void fireLogRecord (Level l, int stackTraceDepth, LogRecordData data, String msgFormat, Object ... args)
  {
    if (isLoggable(l))
    {
      String msg = new java.util.Formatter().format(msgFormat, args).toString();
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, stackTraceDepth, data, msg));
    }
  }
  
  private void fireLogRecord (Level l, int stackTraceDepth, Throwable th, LogRecordData data)
  {
    if (isLoggable(l))
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, stackTraceDepth, th, data, (String)null));
  }
  
  private void fireLogRecord (Level l, int stackTraceDepth, Throwable th, LogRecordData data, String msg)
  {
    if (isLoggable(l))
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, stackTraceDepth, th, data, msg));
  }

  private void fireLogRecord (Level l, int stackTraceDepth, Throwable th, LogRecordData data, String msgFormat, Object ...args)
  {
    if (isLoggable(l))
    {
      String msg = new java.util.Formatter().format(msgFormat, args).toString();
      logger.log(new ExtendedLogRecord(l, showLocation ? 4 : -4, stackTraceDepth, th, data, msg));
    }
  }

  // *************************************************************************

  /**
   * Get the log level of debug method calls.
   * By default the level {@link #DEBUG} is used, which value is (Level.OFF.intvalue() - 1)
   * @return level of debug method calls
   */
  public Level getDebugLevel ()
  {
    return debugLevel;
  }

  /**
   * Set the log level for the debug method calls.
   * By default the level {@link #DEBUG} is used, which value is (Level.OFF.intvalue() - 1)
   * @param debugLevel new level for debug method calls
   */
  public void setDebugLevel (Level debugLevel)
  {
    this.debugLevel = debugLevel;
  }
  
  /**
   * Log a empty log record for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   */
  public void debug ()
  {
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log message for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param msg log message string
   */
  public void debug (String msg)
  {
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, msg);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log message for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void debug (String msgFormat, Object ... args)
  {
    String msg = new java.util.Formatter().format(msgFormat, args).toString();
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, msg);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log Throwable for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param th Throwable object (Exception, Error, ...)
   */
  public void debug (Throwable th)
  {
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, th);
    r.setDebugRecord(true);
    logger.log(r);
  }
  
  /**
   * Log Throwable and message for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param th Throwable object (Exception, Error, ...)
   * @param msg log message string
   */
  public void debug (Throwable th, String msg)
  {
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, th, msg);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log Throwable and message for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param th Throwable object (Exception, Error, ...)
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void debug (Throwable th, String msgFormat, Object ... args)
  {
    String msg = new java.util.Formatter().format(msgFormat, args).toString();
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, th, msg);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log a LogRecordData object for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param data the data object to log
   */
  public void debug (LogRecordData data)
  {
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, data);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log a LogRecordData object and a message string for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param data the data object to log
   * @param msg log message string
   */
  public void debug (LogRecordData data, String msg)
  {
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, data, msg);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log a LogRecordData object and a message string for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param data the data object to log
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void debug (LogRecordData data, String msgFormat, Object ... args)
  {
    String msg = new java.util.Formatter().format(msgFormat, args).toString();
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, data, msg);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log a byte array for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   */
  public void debug (byte [] data)
  {
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, new LogRecordDataHexDump(data));
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log a byte array and a message string for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msg log message string
   */
  public void debug (byte [] data, String msg)
  {
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, new LogRecordDataHexDump(data), msg);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log a byte array and a message string for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void debug (byte [] data, String msgFormat, Object ... args)
  {
    String msg = new java.util.Formatter().format(msgFormat, args).toString();
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, new LogRecordDataHexDump(data), msg);
    r.setDebugRecord(true);
    logger.log(r);
  }
  
  /**
   * Log a Throwable object and a LogDataRecord object for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param th Throwable object (Exception, Error, ...)
   * @param data the data object to log
   */
  public void debug (Throwable th, LogRecordData data)
  {
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, th, data);
    r.setDebugRecord(true);
    logger.log(r);
  }
  
  /**
   * Log a Throwable object, a LogDataRecord object and a messsage string for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param th Throwable object (Exception, Error, ...)
   * @param data the data object to log
   * @param msg log message string
   */
  public void debug (Throwable th, LogRecordData data, String msg)
  {
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, th, data, msg);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log a Throwable object, a LogDataRecord object and a messsage string for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param th Throwable object (Exception, Error, ...)
   * @param data the data object to log
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void debug (Throwable th, LogRecordData data, String msgFormat, Object ...args)
  {
    String msg = new java.util.Formatter().format(msgFormat, args).toString();
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, th, data, msg);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log call stack with limited depth for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param stackTraceDepth the depth of call stack trace which should be published 
   */
  public void debug (int stackTraceDepth)
  {
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, stackTraceDepth);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log call stack with limited depth and message string for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msg log message string
   */
  public void debug (int stackTraceDepth, String msg)
  {
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, stackTraceDepth, msg);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log call stack with limited depth and message string for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void debug (int stackTraceDepth, String msgFormat, Object ... args)
  {
    String msg = new java.util.Formatter().format(msgFormat, args).toString();
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, stackTraceDepth, msg);
    r.setDebugRecord(true);
    logger.log(r);
  }
  
  /**
   * Log call stack with limited depth and Throwable object for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th Throwable object (Exception, Error, ...)
   */
  public void debug (int stackTraceDepth, Throwable th)
  {
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, stackTraceDepth, th);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log call stack with limited depth, a Throwable object and a message string for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th Throwable object (Exception, Error, ...)
   * @param msg log message string
   */
  public void debug (int stackTraceDepth, Throwable th, String msg)
  {
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, stackTraceDepth, th, msg);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log call stack with limited depth, a Throwable object and a message string for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th Throwable object (Exception, Error, ...)
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void debug (int stackTraceDepth, Throwable th, String msgFormat, Object ... args)
  {
    String msg = new java.util.Formatter().format(msgFormat, args).toString();
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, stackTraceDepth, th, msg);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log call stack with limited depth and a LogDataRecord object for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the data object to log
   */
  public void debug (int stackTraceDepth, LogRecordData data)
  { 
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, stackTraceDepth, data);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log call stack with limited depth, a LogDataRecord object and a message string for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the data object to log
   * @param msg log message string
   */
  public void debug (int stackTraceDepth, LogRecordData data, String msg)
  {
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, stackTraceDepth, data, msg);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log call stack with limited depth, a LogDataRecord object and a message string for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the data object to log
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void debug (int stackTraceDepth, LogRecordData data, String msgFormat, Object ... args)
  {
    String msg = new java.util.Formatter().format(msgFormat, args).toString();
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, stackTraceDepth, data, msg);
    r.setDebugRecord(true);
    logger.log(r);
  }
  
  /**
   * Log call stack with limited depth, a LogDataRecord object and a Throwable object for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th Throwable object (Exception, Error, ...)
   * @param data the data object to log 
   */
  public void debug (int stackTraceDepth, Throwable th, LogRecordData data)
  {
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, stackTraceDepth, th, data);
    r.setDebugRecord(true);
    logger.log(r);
  }
  
  /**
   * Log call stack with limited depth, a LogDataRecord object, a Throwable object and a message string
   * for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th Throwable object (Exception, Error, ...)
   * @param data the data object to log
   * @param msg log message string
   */
  public void debug (int stackTraceDepth, Throwable th, LogRecordData data, String msg)
  {
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, stackTraceDepth, th, data, msg);
    r.setDebugRecord(true);
    logger.log(r);
  }

  /**
   * Log call stack with limited depth, a LogDataRecord object, a Throwable object and a message string
   * for debugging purposes while development.
   * The log level is {@link #DEBUG} by default, but can be changed by {@link #setDebugLevel(Level)}.
   * The call should be removed for released software versions.
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th Throwable object (Exception, Error, ...)
   * @param data the data object to log
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void debug (int stackTraceDepth, Throwable th, LogRecordData data, String msgFormat, Object ...args)
  {
    String msg = new java.util.Formatter().format(msgFormat, args).toString();
    ExtendedLogRecord r = new ExtendedLogRecord(debugLevel, 3, stackTraceDepth, th, data, msg);
    r.setDebugRecord(true);
    logger.log(r);
  }
  
  // *************************************************************************
  
  /**
   * Check if a message of the given level would actually be logged
   * by this logger.  This check is based on the Loggers effective level,
   * which may be inherited from its parent.
   *
   * @param   level   the message logging level
   * @return  true if the given message level is currently being logged.
   */
  public boolean isLoggable (Level level)
  {
    return logger.isLoggable(level);
  }
  
  /**
   * Log a empty LogRecord
   * @param l the message logging level
   */
  public void log (Level l)
  {
    Logger.this.fireLogRecord(l);
  }

  /**
   * Log a string message.
   * @param l the message logging level
   * @param msg the message string
   */
  public void log (Level l, String msg)
  {
    Logger.this.fireLogRecord(l, msg);
  }

  /**
   * Log a string message.
   * @param l the message logging level
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void log (Level l, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(l, msgFormat, args);
  }
  
  /**
   * Log a Throwable.
   * @param l  the message logging level
   * @param th Throwable associated with log message.
   */
  public void log (Level l, Throwable th)
  {
    Logger.this.fireLogRecord(l, th);
  }
  
  /**
   * Log a Throwable and message string.
   * @param l the message logging level
   * @param th Throwable associated with log message.
   * @param msg the message string
   */
  public void log (Level l, Throwable th, String msg)
  {
    Logger.this.fireLogRecord(l, th, msg);
  }

  /**
   * Log a Throwable and message string.
   * @param l the message logging level
   * @param th Throwable associated with log message.
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void log (Level l, Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(l, th, msgFormat, args);
  }

  /**
   * Log a LogRecordData Object.
   * @param l the message logging level
   * @param data the data object to log 
   */
  public void log (Level l, LogRecordData data)
  {
    Logger.this.fireLogRecord(l, data);
  }

  /**
   * Log a LogRecordData Object and message string.
   * @param l the message logging level
   * @param data the data object to log
   * @param msg the message string
   */
  public void log (Level l, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(l, data, msg);
  }

  /**
   * Log a LogRecordData Object and message string.
   * @param l the message logging level
   * @param data the data object to log
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void log (Level l, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(l, data, msgFormat, args);
  }

  /**
   * Log a byte array.
   * @param l the message logging level
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   */
  public void log (Level l, byte [] data)
  {
    Logger.this.fireLogRecord(l, data);
  }

  /**
   * Log a byte array and message string.
   * @param l the message logging level
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msg the message string
   */
  public void log (Level l, byte [] data, String msg)
  {
    Logger.this.fireLogRecord(l, data, msg);
  }

  /**
   * Log a byte array and message string.
   * @param l the message logging level
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void log (Level l, byte [] data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(l, data, msgFormat, args);
  }
  
  /**
   * Log a LogRecordData Object and Throwable Object.
   * @param l the message logging level
   * @param th the Throwable associated with this
   * @param data the data object to log
   */
  public void log (Level l, Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(l, th, data);
  }

  /**
   * Log a LogRecordData Object, a Throwable Object and a message string.
   * @param l the message logging level
   * @param th the Throwable associated with this
   * @param data the data object to log
   * @param msg the message string
   */
  public void log (Level l, Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(l, th, data, msg);
  }

  /**
   * Log a LogRecordData Object, a Throwable Object and a message string.
   * @param l the message logging level
   * @param th the Throwable associated with this
   * @param data the data object to log
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void log (Level l, Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(l, th, data, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth.
   * @param l the message logging level
   * @param stackTraceDepth the depth of call stack trace which should be published
   */
  public void log (Level l, int stackTraceDepth)
  {
    Logger.this.fireLogRecord(l, stackTraceDepth);
  }

  /**
   * Log call stack with limited depth and message string.
   * @param l the message logging level
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msg the message string 
   */
  public void log (Level l, int stackTraceDepth, String msg)
  {
    Logger.this.fireLogRecord(l, stackTraceDepth, msg);
  }

  /**
   * Log call stack with limited depth and message string.
   * @param l the message logging level
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void log (Level l, int stackTraceDepth, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(l, stackTraceDepth, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth and Throwable object.
   * @param l the message logging level
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   */
  public void log (Level l, int stackTraceDepth, Throwable th)
  {
    Logger.this.fireLogRecord(l, stackTraceDepth, th);
  }
  
  /**
   * Log call stack with limited depth, a Throwable object and message string.
   * @param l the message logging level
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msg the message string
   */
  public void log (Level l, int stackTraceDepth, Throwable th, String msg)
  {
    Logger.this.fireLogRecord(l, stackTraceDepth, th, msg);
  }

  /**
   * Log call stack with limited depth, a Throwable object and message string.
   * @param l the message logging level
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void log (Level l, int stackTraceDepth, Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(l, stackTraceDepth, th, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth and LogRecord Data object.
   * @param l the message logging level
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the data object to log
   */
  public void log (Level l, int stackTraceDepth, LogRecordData data)
  {
    Logger.this.fireLogRecord(l, stackTraceDepth, data);
  }

  /**
   * Log call stack with limited depth, a LogRecord Data object and a message string.
   * @param l the message logging level
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the data object to log
   * @param msg the message string 
   */
  public void log (Level l, int stackTraceDepth, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(l, stackTraceDepth, data, msg);
  }

  /**
   * Log call stack with limited depth, a LogRecord Data object and a message string.
   * @param l the message logging level
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the data object to log
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void log (Level l, int stackTraceDepth, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(l, stackTraceDepth, data, msgFormat, args);
  }

  /**
   * Log call stack with limited depth, a Throwable object and LogRecord Data object.
   * @param l the message logging level
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the data object to log 
   */
  public void log (Level l, int stackTraceDepth, Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(l, stackTraceDepth, th, data);
  }

  /**
   * Log call stack with limited depth, a Throwable object, a LogRecord Data object and a message string. 
   * @param l the message logging level
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the data object to log
   * @param msg the message string
   */  
  public void log (Level l, int stackTraceDepth, Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(l, stackTraceDepth, th, data, msg);
  }

  /**
   * Log call stack with limited depth, a Throwable object, a LogRecord Data object and a message string. 
   * @param l the message logging level
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the data object to log
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void log (Level l, int stackTraceDepth, Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    fireLogRecord(l, stackTraceDepth, th, data, msgFormat, args);
  }
  
  // *************************************************************************
  
  /**
   * Log empty record with {@link java.util.logging.Level#ALL}
   */
  public void all ()
  {
    Logger.this.fireLogRecord(Level.ALL);
  }

  /**
   * Log message string with {@link java.util.logging.Level#ALL}.
   * @param msg the message string
   */
  public void all (String msg)
  {
    Logger.this.fireLogRecord(Level.ALL, msg);
  }

  /**
   * Log message string with {@link java.util.logging.Level#ALL}.
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void all (String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.ALL, msgFormat, args);
  }
  
  /**
   * Log a Throwable object with {@link java.util.logging.Level#ALL}.
   * @param th the Throwable object
   */
  public void all (Throwable th)
  {
    Logger.this.fireLogRecord(Level.ALL, th);
  }

  /**
   * Log a Throwable object and message string with {@link java.util.logging.Level#ALL}.
   * @param th the Throwable object
   * @param msg the message string
   */  
  public void all (Throwable th, String msg)
  {
    Logger.this.fireLogRecord(Level.ALL, th, msg);
  }

  /**
   * Log a Throwable object and message string with {@link java.util.logging.Level#ALL}.
   * @param th the Throwable object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void all (Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.ALL, th, msgFormat, args);
  }

  /**
   * Log LogRecordData with {@link java.util.logging.Level#ALL}
   * @param data the LogRecordData object
   */
  public void all (LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.ALL, data);
  }

  /**
   * Log LogRecordData and message string with {@link java.util.logging.Level#ALL}
   * @param data the LogRecordData object
   * @param msg the message string
   */
  public void all (LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.ALL, data, msg);
  }

  /**
   * Log LogRecordData and message string with {@link java.util.logging.Level#ALL}
   * @param data the LogRecordData object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void all (LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.ALL, data, msgFormat, args);
  }

  /**
   * Log byte array with {@link java.util.logging.Level#ALL}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   */
  public void all (byte [] data)
  {
    Logger.this.fireLogRecord(Level.ALL, data);
  }

  /**
   * Log byte array and message string with {@link java.util.logging.Level#ALL}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msg the message string
   */
  public void all (byte [] data, String msg)
  {
    Logger.this.fireLogRecord(Level.ALL, data, msg);
  }

  /**
   * Log byte array and message string with {@link java.util.logging.Level#ALL}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void all (byte [] data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.ALL, data, msgFormat, args);
  }
  
  /**
   * Log Throwable and LogRecordData object  with {@link java.util.logging.Level#ALL}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   */
  public void all (Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.ALL, th, data);
  }

  /**
   * Log Throwable, LogRecordData and message string with {@link java.util.logging.Level#ALL}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msg the message string  
   */
  public void all (Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.ALL, th, data, msg);
  }

  /**
   * Log Throwable, LogRecordData and message string with {@link java.util.logging.Level#ALL}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void all (Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.ALL, th, data, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth and {@link java.util.logging.Level#ALL}
   * @param stackTraceDepth the depth of call stack trace which should be published
   */
  public void all (int stackTraceDepth)
  {
    Logger.this.fireLogRecord(Level.ALL, stackTraceDepth);
  }

  /**
   * Log call stack with limited depth and message string with {@link java.util.logging.Level#ALL}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msg the message string
   */
  public void all (int stackTraceDepth, String msg)
  {
    Logger.this.fireLogRecord(Level.ALL, stackTraceDepth, msg);
  }

  /**
   * Log call stack with limited depth and message string with {@link java.util.logging.Level#ALL}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void all (int stackTraceDepth, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.ALL, stackTraceDepth, msgFormat, args);
  }

  /**
   * Log call stack with limited depth and Throwable with {@link java.util.logging.Level#ALL}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   */  
  public void all (int stackTraceDepth, Throwable th)
  {
    Logger.this.fireLogRecord(Level.ALL, stackTraceDepth, th);
  }
  
  /**
   * Log call stack with limited depth, Throwable and message string with {@link java.util.logging.Level#ALL}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msg the message string
   */
  public void all (int stackTraceDepth, Throwable th, String msg)
  {
    Logger.this.fireLogRecord(Level.ALL, stackTraceDepth, th, msg);
  }

  /**
   * Log call stack with limited depth, Throwable and message string with {@link java.util.logging.Level#ALL}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void all (int stackTraceDepth, Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.ALL, stackTraceDepth, th, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth, and LogRecordData with {@link java.util.logging.Level#ALL}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   */
  public void all (int stackTraceDepth, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.ALL, stackTraceDepth, data);
  }

  /**
   * Log call stack with limited depth, LogRecordData and message string with {@link java.util.logging.Level#ALL}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   * @param msg the message string
   */
  public void all (int stackTraceDepth, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.ALL, stackTraceDepth, data, msg);
  }

  /**
   * Log call stack with limited depth, LogRecordData and message string with {@link java.util.logging.Level#ALL}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void all (int stackTraceDepth, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.ALL, stackTraceDepth, data, msgFormat, args);
  }

  /**
   * Log call stack with limited depth, Throwable and LogRecordData with {@link java.util.logging.Level#ALL}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   */
  public void all (int stackTraceDepth, Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.ALL, stackTraceDepth, th, data);
  }
  
  /**
   * Log call stack with limited depth, Throwable, LogRecordData and message string with {@link java.util.logging.Level#ALL}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msg the message string
   */
  public void all (int stackTraceDepth, Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.ALL, stackTraceDepth, th, data, msg);
  }

  /**
   * Log call stack with limited depth, Throwable, LogRecordData and message string with {@link java.util.logging.Level#ALL}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void all (int stackTraceDepth, Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    fireLogRecord(Level.ALL, stackTraceDepth, th, data, msgFormat, args);
  }
  
  // *************************************************************************
  
  /**
   * Check if a message with {@link java.util.logging.Level#FINEST} 
   * would actually be logged by this logger.
   * This check is based on the Loggers effective level, which may
   * be inherited from its parent.
   * @return true if FINEST is currently being logged.
   */
  public boolean isFinestLoggable ()
  {
    return isLoggable(Level.FINEST);
  }
  
  /**
   * Log empty record with {@link java.util.logging.Level#FINEST}
   */
  public void finest ()
  {
    Logger.this.fireLogRecord(Level.FINEST);
  }

  /**
   * Log message string with {@link java.util.logging.Level#FINEST}.
   * @param msg the message string
   */
  public void finest (String msg)
  {
    Logger.this.fireLogRecord(Level.FINEST, msg);
  }

  /**
   * Log message string with {@link java.util.logging.Level#FINEST}.
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finest (String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINEST, msgFormat, args);
  }
  
  /**
   * Log a Throwable object with {@link java.util.logging.Level#FINEST}.
   * @param th the Throwable object
   */
  public void finest (Throwable th)
  {
    Logger.this.fireLogRecord(Level.FINEST, th);
  }
  
  /**
   * Log a Throwable object and message string with {@link java.util.logging.Level#FINEST}.
   * @param th the Throwable object
   * @param msg the message string
   */
  public void finest (Throwable th, String msg)
  {
    Logger.this.fireLogRecord(Level.FINEST, th, msg);
  }

  /**
   * Log a Throwable object and message string with {@link java.util.logging.Level#FINEST}.
   * @param th the Throwable object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finest (Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINEST, th, msgFormat, args);
  }

  /**
   * Log LogRecordData with {@link java.util.logging.Level#FINEST}
   * @param data the LogRecordData object
   */
  public void finest (LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.FINEST, data);
  }

  /**
   * Log LogRecordData and message string with {@link java.util.logging.Level#FINEST}
   * @param data the LogRecordData object
   * @param msg the message string
   */
  public void finest (LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.FINEST, data, msg);
  }

  /**
   * Log LogRecordData and message string with {@link java.util.logging.Level#FINEST}
   * @param data the LogRecordData object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finest (LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINEST, data, msgFormat, args);
  }

  /**
   * Log byte array with {@link java.util.logging.Level#FINEST}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   */
  public void finest (byte [] data)
  {
    Logger.this.fireLogRecord(Level.FINEST, data);
  }

  /**
   * Log byte array and message string with {@link java.util.logging.Level#FINEST}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msg the message string
   */
  public void finest (byte [] data, String msg)
  {
    Logger.this.fireLogRecord(Level.FINEST, data, msg);
  }

  /**
   * Log byte array and message string with {@link java.util.logging.Level#FINEST}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finest (byte [] data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINEST, data, msgFormat, args);
  }
  
  /**
   * Log Throwable and LogRecordData object  with {@link java.util.logging.Level#FINEST}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   */
  public void finest (Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.FINEST, th, data);
  }

  /**
   * Log Throwable, LogRecordData and message string with {@link java.util.logging.Level#FINEST}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msg the message string  
   */
  public void finest (Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.FINEST, th, data, msg);
  }

  /**
   * Log Throwable, LogRecordData and message string with {@link java.util.logging.Level#FINEST}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finest (Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINEST, th, data, msgFormat, args);
  }

  /**
   * Log call stack with limited depth and {@link java.util.logging.Level#FINEST}
   * @param stackTraceDepth the depth of call stack trace which should be published
   */  
  public void finest (int stackTraceDepth)
  {
    Logger.this.fireLogRecord(Level.FINEST, stackTraceDepth);
  }

  /**
   * Log call stack with limited depth and message string with {@link java.util.logging.Level#FINEST}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msg the message string
   */
  public void finest (int stackTraceDepth, String msg)
  {
    Logger.this.fireLogRecord(Level.FINEST, stackTraceDepth, msg);
  }

  /**
   * Log call stack with limited depth and message string with {@link java.util.logging.Level#FINEST}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finest (int stackTraceDepth, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINEST, stackTraceDepth, msgFormat, args);
  }

  /**
   * Log call stack with limited depth and Throwable with {@link java.util.logging.Level#FINEST}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   */  
  public void finest (int stackTraceDepth, Throwable th)
  {
    Logger.this.fireLogRecord(Level.FINEST, stackTraceDepth, th);
  }

  /**
   * Log call stack with limited depth, Throwable and message string with {@link java.util.logging.Level#FINEST}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msg the message string
   */  
  public void finest (int stackTraceDepth, Throwable th, String msg)
  {
    Logger.this.fireLogRecord(Level.FINEST, stackTraceDepth, th, msg);
  }

  /**
   * Log call stack with limited depth, Throwable and message string with {@link java.util.logging.Level#FINEST}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finest (int stackTraceDepth, Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINEST, stackTraceDepth, th, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth, and LogRecordData with {@link java.util.logging.Level#FINEST}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   */
  public void finest (int stackTraceDepth, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.FINEST, stackTraceDepth, data);
  }

  /**
   * Log call stack with limited depth, LogRecordData and message string with {@link java.util.logging.Level#FINEST}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   * @param msg the message string
   */
  public void finest (int stackTraceDepth, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.FINEST, stackTraceDepth, data, msg);
  }

  /**
   * Log call stack with limited depth, LogRecordData and message string with {@link java.util.logging.Level#FINEST}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finest (int stackTraceDepth, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINEST, stackTraceDepth, data, msgFormat, args);
  }

  /**
   * Log call stack with limited depth, Throwable and LogRecordData with {@link java.util.logging.Level#FINEST}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   */
  public void finest (int stackTraceDepth, Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.FINEST, stackTraceDepth, th, data);
  }

  /**
   * Log call stack with limited depth, Throwable, LogRecordData and message string with {@link java.util.logging.Level#FINEST}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msg the message string
   */  
  public void finest (int stackTraceDepth, Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.FINEST, stackTraceDepth, th, data, msg);
  }

  /**
   * Log call stack with limited depth, Throwable, LogRecordData and message string with {@link java.util.logging.Level#FINEST}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finest (int stackTraceDepth, Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    fireLogRecord(Level.FINEST, stackTraceDepth, th, data, msgFormat, args);
  }
  
  // *************************************************************************
  
  /**
   * Check if a message with {@link java.util.logging.Level#FINER} 
   * would actually be logged by this logger.
   * This check is based on the Loggers effective level, which may
   * be inherited from its parent.
   * @return true if FINER is currently being logged.
   */
  public boolean isFinerLoggable ()
  {
    return isLoggable(Level.FINER);
  }
  
  /**
   * Log empty record with {@link java.util.logging.Level#FINER}
   */
  public void finer ()
  {
    Logger.this.fireLogRecord(Level.FINER);
  }

  /**
   * Log message string with {@link java.util.logging.Level#FINER}.
   * @param msg the message string
   */
  public void finer (String msg)
  {
    Logger.this.fireLogRecord(Level.FINER, msg);
  }

  /**
   * Log message string with {@link java.util.logging.Level#FINER}.
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finer (String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINER, msgFormat, args);
  }

  /**
   * Log a Throwable object with {@link java.util.logging.Level#FINER}.
   * @param th the Throwable object
   */  
  public void finer (Throwable th)
  {
    Logger.this.fireLogRecord(Level.FINER, th);
  }
  
  /**
   * Log a Throwable object and message string with {@link java.util.logging.Level#FINER}.
   * @param th the Throwable object
   * @param msg the message string
   */
  public void finer (Throwable th, String msg)
  {
    Logger.this.fireLogRecord(Level.FINER, th, msg);
  }

  /**
   * Log a Throwable object and message string with {@link java.util.logging.Level#FINER}.
   * @param th the Throwable object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finer (Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINER, th, msgFormat, args);
  }

  /**
   * Log LogRecordData with {@link java.util.logging.Level#FINER}
   * @param data the LogRecordData object
   */
  public void finer (LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.FINER, data);
  }

  /**
   * Log LogRecordData and message string with {@link java.util.logging.Level#FINER}
   * @param data the LogRecordData object
   * @param msg the message string
   */
  public void finer (LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.FINER, data, msg);
  }

  /**
   * Log LogRecordData and message string with {@link java.util.logging.Level#FINER}
   * @param data the LogRecordData object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finer (LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINER, data, msgFormat, args);
  }

  /**
   * Log byte array with {@link java.util.logging.Level#FINER}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   */
  public void finer (byte [] data)
  {
    Logger.this.fireLogRecord(Level.FINER, data);
  }

  /**
   * Log byte array and message string with {@link java.util.logging.Level#FINER}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msg the message string
   */
  public void finer (byte [] data, String msg)
  {
    Logger.this.fireLogRecord(Level.FINER, data, msg);
  }

  /**
   * Log byte array and message string with {@link java.util.logging.Level#FINER}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finer (byte [] data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINER, data, msgFormat, args);
  }
  
  /**
   * Log Throwable and LogRecordData object  with {@link java.util.logging.Level#FINER}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   */
  public void finer (Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.FINER, th, data);
  }

  /**
   * Log Throwable, LogRecordData and message string with {@link java.util.logging.Level#FINER}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msg the message string  
   */
  public void finer (Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.FINER, th, data, msg);
  }

  /**
   * Log Throwable, LogRecordData and message string with {@link java.util.logging.Level#FINER}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finer (Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINER, th, data, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth and {@link java.util.logging.Level#FINER}
   * @param stackTraceDepth the depth of call stack trace which should be published
   */
  public void finer (int stackTraceDepth)
  {
    Logger.this.fireLogRecord(Level.FINER, stackTraceDepth);
  }

  /**
   * Log call stack with limited depth and message string with {@link java.util.logging.Level#FINER}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msg the message string
   */
  public void finer (int stackTraceDepth, String msg)
  {
    Logger.this.fireLogRecord(Level.FINER, stackTraceDepth, msg);
  }

  /**
   * Log call stack with limited depth and message string with {@link java.util.logging.Level#FINER}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finer (int stackTraceDepth, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINER, stackTraceDepth, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth and Throwable with {@link java.util.logging.Level#FINER}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   */
  public void finer (int stackTraceDepth, Throwable th)
  {
    Logger.this.fireLogRecord(Level.FINER, stackTraceDepth, th);
  }
  
  /**
   * Log call stack with limited depth, Throwable and message string with {@link java.util.logging.Level#FINER}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msg the message string
   */
  public void finer (int stackTraceDepth, Throwable th, String msg)
  {
    Logger.this.fireLogRecord(Level.FINER, stackTraceDepth, th, msg);
  }

  /**
   * Log call stack with limited depth, Throwable and message string with {@link java.util.logging.Level#FINER}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finer (int stackTraceDepth, Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINER, stackTraceDepth, th, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth, and LogRecordData with {@link java.util.logging.Level#FINER}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   */
  public void finer (int stackTraceDepth, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.FINER, stackTraceDepth, data);
  }

  /**
   * Log call stack with limited depth, LogRecordData and message string with {@link java.util.logging.Level#FINER}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   * @param msg the message string
   */
  public void finer (int stackTraceDepth, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.FINER, stackTraceDepth, data, msg);
  }

  /**
   * Log call stack with limited depth, LogRecordData and message string with {@link java.util.logging.Level#FINER}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finer (int stackTraceDepth, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINER, stackTraceDepth, data, msgFormat, args);
  }

  /**
   * Log call stack with limited depth, Throwable and LogRecordData with {@link java.util.logging.Level#FINER}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   */
  public void finer (int stackTraceDepth, Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.FINER, stackTraceDepth, th, data);
  }
  
  /**
   * Log call stack with limited depth, Throwable, LogRecordData and message string with {@link java.util.logging.Level#FINER}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msg the message string
   */
  public void finer (int stackTraceDepth, Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.FINER, stackTraceDepth, th, data, msg);
  }

  /**
   * Log call stack with limited depth, Throwable, LogRecordData and message string with {@link java.util.logging.Level#FINER}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void finer (int stackTraceDepth, Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    fireLogRecord(Level.FINER, stackTraceDepth, th, data, msgFormat, args);
  }
  
  // *************************************************************************
  
  /**
   * Check if a message with {@link java.util.logging.Level#FINE} 
   * would actually be logged by this logger.
   * This check is based on the Loggers effective level, which may
   * be inherited from its parent.
   * @return true if FINE is currently being logged.
   */
  public boolean isFineLoggable ()
  {
    return isLoggable(Level.FINE);
  }
  
  /**
   * Log empty record with {@link java.util.logging.Level#FINE}
   */
  public void fine ()
  {
    Logger.this.fireLogRecord(Level.FINE);
  }

  /**
   * Log message string with {@link java.util.logging.Level#FINE}.
   * @param msg the message string
   */
  public void fine (String msg)
  {
    Logger.this.fireLogRecord(Level.FINE, msg);
  }

  /**
   * Log message string with {@link java.util.logging.Level#FINE}.
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void fine (String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINE, msgFormat, args);
  }
  
  /**
   * Log a Throwable object with {@link java.util.logging.Level#FINE}.
   * @param th the Throwable object
   */
  public void fine (Throwable th)
  {
    Logger.this.fireLogRecord(Level.FINE, th);
  }
  
  /**
   * Log a Throwable object and message string with {@link java.util.logging.Level#FINE}.
   * @param th the Throwable object
   * @param msg the message string
   */
  public void fine (Throwable th, String msg)
  {
    Logger.this.fireLogRecord(Level.FINE, th, msg);
  }

  /**
   * Log a Throwable object and message string with {@link java.util.logging.Level#FINE}.
   * @param th the Throwable object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void fine (Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINE, th, msgFormat, args);
  }

  /**
   * Log LogRecordData with {@link java.util.logging.Level#FINE}
   * @param data the LogRecordData object
   */
  public void fine (LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.FINE, data);
  }

  /**
   * Log LogRecordData and message string with {@link java.util.logging.Level#FINE}
   * @param data the LogRecordData object
   * @param msg the message string
   */
  public void fine (LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.FINE, data, msg);
  }

  /**
   * Log LogRecordData and message string with {@link java.util.logging.Level#FINE}
   * @param data the LogRecordData object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void fine (LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINE, data, msgFormat, args);
  }

  /**
   * Log byte array with {@link java.util.logging.Level#FINE}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   */
  public void fine (byte [] data)
  {
    Logger.this.fireLogRecord(Level.FINE, data);
  }

  /**
   * Log byte array and message string with {@link java.util.logging.Level#FINE}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msg the message string
   */
  public void fine (byte [] data, String msg)
  {
    Logger.this.fireLogRecord(Level.FINE, data, msg);
  }

  /**
   * Log byte array and message string with {@link java.util.logging.Level#FINE}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void fine (byte [] data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINE, data, msgFormat, args);
  }
  
  /**
   * Log Throwable and LogRecordData object  with {@link java.util.logging.Level#FINE}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   */
  public void fine (Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.FINE, th, data);
  }

  /**
   * Log Throwable, LogRecordData and message string with {@link java.util.logging.Level#FINE}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msg the message string  
   */
  public void fine (Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.FINE, th, data, msg);
  }

  /**
   * Log Throwable, LogRecordData and message string with {@link java.util.logging.Level#FINE}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void fine (Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINE, th, data, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth and {@link java.util.logging.Level#FINE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   */
  public void fine (int stackTraceDepth)
  {
    Logger.this.fireLogRecord(Level.FINE, stackTraceDepth);
  }

  /**
   * Log call stack with limited depth and message string with {@link java.util.logging.Level#FINE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msg the message string
   */
  public void fine (int stackTraceDepth, String msg)
  {
    Logger.this.fireLogRecord(Level.FINE, stackTraceDepth, msg);
  }

  /**
   * Log call stack with limited depth and message string with {@link java.util.logging.Level#FINE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void fine (int stackTraceDepth, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINE, stackTraceDepth, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth and Throwable with {@link java.util.logging.Level#FINE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   */
  public void fine (int stackTraceDepth, Throwable th)
  {
    Logger.this.fireLogRecord(Level.FINE, stackTraceDepth, th);
  }
  
  /**
   * Log call stack with limited depth, Throwable and message string with {@link java.util.logging.Level#FINE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msg the message string
   */
  public void fine (int stackTraceDepth, Throwable th, String msg)
  {
    Logger.this.fireLogRecord(Level.FINE, stackTraceDepth, th, msg);
  }

  /**
   * Log call stack with limited depth, Throwable and message string with {@link java.util.logging.Level#FINE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void fine (int stackTraceDepth, Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINE, stackTraceDepth, th, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth, and LogRecordData with {@link java.util.logging.Level#FINE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   */
  public void fine (int stackTraceDepth, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.FINE, stackTraceDepth, data);
  }

  /**
   * Log call stack with limited depth, LogRecordData and message string with {@link java.util.logging.Level#FINE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   * @param msg the message string
   */
  public void fine (int stackTraceDepth, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.FINE, stackTraceDepth, data, msg);
  }

  /**
   * Log call stack with limited depth, LogRecordData and message string with {@link java.util.logging.Level#FINE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void fine (int stackTraceDepth, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.FINE, stackTraceDepth, data, msgFormat, args);
  }

  /**
   * Log call stack with limited depth, Throwable and LogRecordData with {@link java.util.logging.Level#FINE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   */
  public void fine (int stackTraceDepth, Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.FINE, stackTraceDepth, th, data);
  }
  
  /**
   * Log call stack with limited depth, Throwable, LogRecordData and message string with {@link java.util.logging.Level#FINE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msg the message string
   */
  public void fine (int stackTraceDepth, Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.FINE, stackTraceDepth, th, data, msg);
  }

  /**
   * Log call stack with limited depth, Throwable, LogRecordData and message string with {@link java.util.logging.Level#FINE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void fine (int stackTraceDepth, Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    fireLogRecord(Level.FINE, stackTraceDepth, th, data, msgFormat, args);
  }
  
  // *************************************************************************
  
  /**
   * Check if a message with {@link java.util.logging.Level#CONFIG} 
   * would actually be logged by this logger.
   * This check is based on the Loggers effective level, which may
   * be inherited from its parent.
   * @return true if CONFIG is currently being logged.
   */
  public boolean isConfigLoggable ()
  {
    return isLoggable(Level.CONFIG);
  }
  
  /**
   * Log empty record with {@link java.util.logging.Level#CONFIG}
   */
  public void config ()
  {
    Logger.this.fireLogRecord(Level.CONFIG);
  }

  /**
   * Log message string with {@link java.util.logging.Level#CONFIG}.
   * @param msg the message string
   */
  public void config (String msg)
  {
    Logger.this.fireLogRecord(Level.CONFIG, msg);
  }

  /**
   * Log message string with {@link java.util.logging.Level#CONFIG}.
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void config (String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.CONFIG, msgFormat, args);
  }
  
  /**
   * Log a Throwable object with {@link java.util.logging.Level#CONFIG}.
   * @param th the Throwable object
   */
  public void config (Throwable th)
  {
    Logger.this.fireLogRecord(Level.CONFIG, th);
  }
  
  /**
   * Log a Throwable object and message string with {@link java.util.logging.Level#CONFIG}.
   * @param th the Throwable object
   * @param msg the message string
   */
  public void config (Throwable th, String msg)
  {
    Logger.this.fireLogRecord(Level.CONFIG, th, msg);
  }

  /**
   * Log a Throwable object and message string with {@link java.util.logging.Level#CONFIG}.
   * @param th the Throwable object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void config (Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.CONFIG, th, msgFormat, args);
  }

  /**
   * Log LogRecordData with {@link java.util.logging.Level#CONFIG}
   * @param data the LogRecordData object
   */
  public void config (LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.CONFIG, data);
  }

  /**
   * Log LogRecordData and message string with {@link java.util.logging.Level#CONFIG}
   * @param data the LogRecordData object
   * @param msg the message string
   */
  public void config (LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.CONFIG, data, msg);
  }

  /**
   * Log LogRecordData and message string with {@link java.util.logging.Level#CONFIG}
   * @param data the LogRecordData object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void config (LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.CONFIG, data, msgFormat, args);
  }

  /**
   * Log byte array with {@link java.util.logging.Level#CONFIG}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   */
  public void config (byte [] data)
  {
    Logger.this.fireLogRecord(Level.CONFIG, data);
  }

  /**
   * Log byte array and message string with {@link java.util.logging.Level#CONFIG}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msg the message string
   */
  public void config (byte [] data, String msg)
  {
    Logger.this.fireLogRecord(Level.CONFIG, data, msg);
  }

  /**
   * Log byte array and message string with {@link java.util.logging.Level#CONFIG}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void config (byte [] data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.CONFIG, data, msgFormat, args);
  }
  
  /**
   * Log Throwable and LogRecordData object  with {@link java.util.logging.Level#CONFIG}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   */
  public void config (Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.CONFIG, th, data);
  }

  /**
   * Log Throwable, LogRecordData and message string with {@link java.util.logging.Level#CONFIG}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msg the message string  
   */
  public void config (Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.CONFIG, th, data, msg);
  }

  /**
   * Log Throwable, LogRecordData and message string with {@link java.util.logging.Level#CONFIG}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void config (Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.CONFIG, th, data, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth and {@link java.util.logging.Level#CONFIG}
   * @param stackTraceDepth the depth of call stack trace which should be published
   */
  public void config (int stackTraceDepth)
  {
    Logger.this.fireLogRecord(Level.CONFIG, stackTraceDepth);
  }

  /**
   * Log call stack with limited depth and message string with {@link java.util.logging.Level#CONFIG}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msg the message string
   */
  public void config (int stackTraceDepth, String msg)
  {
    Logger.this.fireLogRecord(Level.CONFIG, stackTraceDepth, msg);
  }

  /**
   * Log call stack with limited depth and message string with {@link java.util.logging.Level#CONFIG}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void config (int stackTraceDepth, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.CONFIG, stackTraceDepth, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth and Throwable with {@link java.util.logging.Level#CONFIG}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   */
  public void config (int stackTraceDepth, Throwable th)
  {
    Logger.this.fireLogRecord(Level.CONFIG, stackTraceDepth, th);
  }
  
  /**
   * Log call stack with limited depth, Throwable and message string with {@link java.util.logging.Level#CONFIG}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msg the message string
   */
  public void config (int stackTraceDepth, Throwable th, String msg)
  {
    Logger.this.fireLogRecord(Level.CONFIG, stackTraceDepth, th, msg);
  }

  /**
   * Log call stack with limited depth, Throwable and message string with {@link java.util.logging.Level#CONFIG}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void config (int stackTraceDepth, Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.CONFIG, stackTraceDepth, th, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth, and LogRecordData with {@link java.util.logging.Level#CONFIG}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   */
  public void config (int stackTraceDepth, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.CONFIG, stackTraceDepth, data);
  }

  /**
   * Log call stack with limited depth, LogRecordData and message string with {@link java.util.logging.Level#CONFIG}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   * @param msg the message string
   */
  public void config (int stackTraceDepth, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.CONFIG, stackTraceDepth, data, msg);
  }

  /**
   * Log call stack with limited depth, LogRecordData and message string with {@link java.util.logging.Level#CONFIG}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void config (int stackTraceDepth, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.CONFIG, stackTraceDepth, data, msgFormat, args);
  }

  /**
   * Log call stack with limited depth, Throwable and LogRecordData with {@link java.util.logging.Level#CONFIG}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   */
  public void config (int stackTraceDepth, Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.CONFIG, stackTraceDepth, th, data);
  }
  
  /**
   * Log call stack with limited depth, Throwable, LogRecordData and message string with {@link java.util.logging.Level#CONFIG}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msg the message string
   */
  public void config (int stackTraceDepth, Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.CONFIG, stackTraceDepth, th, data, msg);
  }

  /**
   * Log call stack with limited depth, Throwable, LogRecordData and message string with {@link java.util.logging.Level#CONFIG}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void config (int stackTraceDepth, Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    fireLogRecord(Level.CONFIG, stackTraceDepth, th, data, msgFormat, args);
  }
  
  // *************************************************************************
  
  /**
   * Check if a message with {@link java.util.logging.Level#INFO} 
   * would actually be logged by this logger.
   * This check is based on the Loggers effective level, which may
   * be inherited from its parent.
   * @return true if INFO is currently being logged.
   */
  public boolean isInfoLoggable ()
  {
    return isLoggable(Level.INFO);
  }
  
  /**
   * Log empty record with {@link java.util.logging.Level#INFO}
   */
  public void info ()
  {
    Logger.this.fireLogRecord(Level.INFO);
  }

  /**
   * Log message string with {@link java.util.logging.Level#INFO}.
   * @param msg the message string
   */
  public void info (String msg)
  {
    Logger.this.fireLogRecord(Level.INFO, msg);
  }

  /**
   * Log message string with {@link java.util.logging.Level#INFO}.
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void info (String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.INFO, msgFormat, args);
  }
  
  /**
   * Log a Throwable object with {@link java.util.logging.Level#INFO}.
   * @param th the Throwable object
   */
  public void info (Throwable th)
  {
    Logger.this.fireLogRecord(Level.INFO, th);
  }
  
  /**
   * Log a Throwable object and message string with {@link java.util.logging.Level#INFO}.
   * @param th the Throwable object
   * @param msg the message string
   */
  public void info (Throwable th, String msg)
  {
    Logger.this.fireLogRecord(Level.INFO, th, msg);
  }

  /**
   * Log a Throwable object and message string with {@link java.util.logging.Level#INFO}.
   * @param th the Throwable object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void info (Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.INFO, th, msgFormat, args);
  }

  /**
   * Log LogRecordData with {@link java.util.logging.Level#INFO}
   * @param data the LogRecordData object
   */
  public void info (LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.INFO, data);
  }

  /**
   * Log LogRecordData and message string with {@link java.util.logging.Level#INFO}
   * @param data the LogRecordData object
   * @param msg the message string
   */
  public void info (LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.INFO, data, msg);
  }

  /**
   * Log LogRecordData and message string with {@link java.util.logging.Level#INFO}
   * @param data the LogRecordData object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void info (LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.INFO, data, msgFormat, args);
  }

  /**
   * Log byte array with {@link java.util.logging.Level#INFO}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   */  
  public void info (byte [] data)
  {
    Logger.this.fireLogRecord(Level.INFO, data);
  }

  /**
   * Log byte array and message string with {@link java.util.logging.Level#INFO}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msg the message string
   */
  public void info (byte [] data, String msg)
  {
    Logger.this.fireLogRecord(Level.INFO, data, msg);
  }

  /**
   * Log byte array and message string with {@link java.util.logging.Level#INFO}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void info (byte [] data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.INFO, data, msgFormat, args);
  }
  
  /**
   * Log Throwable and LogRecordData object  with {@link java.util.logging.Level#INFO}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   */
  public void info (Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.INFO, th, data);
  }

  /**
   * Log Throwable, LogRecordData and message string with {@link java.util.logging.Level#INFO}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msg the message string  
   */
  public void info (Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.INFO, th, data, msg);
  }

  /**
   * Log Throwable, LogRecordData and message string with {@link java.util.logging.Level#INFO}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void info (Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.INFO, th, data, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth and {@link java.util.logging.Level#INFO}
   * @param stackTraceDepth the depth of call stack trace which should be published
   */
  public void info (int stackTraceDepth)
  {
    Logger.this.fireLogRecord(Level.INFO, stackTraceDepth);
  }

  /**
   * Log call stack with limited depth and message string with {@link java.util.logging.Level#INFO}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msg the message string
   */
  public void info (int stackTraceDepth, String msg)
  {
    Logger.this.fireLogRecord(Level.INFO, stackTraceDepth, msg);
  }

  /**
   * Log call stack with limited depth and message string with {@link java.util.logging.Level#INFO}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void info (int stackTraceDepth, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.INFO, stackTraceDepth, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth and Throwable with {@link java.util.logging.Level#INFO}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   */
  public void info (int stackTraceDepth, Throwable th)
  {
    Logger.this.fireLogRecord(Level.INFO, stackTraceDepth, th);
  }
  
  /**
   * Log call stack with limited depth, Throwable and message string with {@link java.util.logging.Level#INFO}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msg the message string
   */
  public void info (int stackTraceDepth, Throwable th, String msg)
  {
    Logger.this.fireLogRecord(Level.INFO, stackTraceDepth, th, msg);
  }

  /**
   * Log call stack with limited depth, Throwable and message string with {@link java.util.logging.Level#INFO}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void info (int stackTraceDepth, Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.INFO, stackTraceDepth, th, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth, and LogRecordData with {@link java.util.logging.Level#INFO}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   */
  public void info (int stackTraceDepth, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.INFO, stackTraceDepth, data);
  }

  /**
   * Log call stack with limited depth, LogRecordData and message string with {@link java.util.logging.Level#INFO}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   * @param msg the message string
   */
  public void info (int stackTraceDepth, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.INFO, stackTraceDepth, data, msg);
  }

  /**
   * Log call stack with limited depth, LogRecordData and message string with {@link java.util.logging.Level#INFO}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void info (int stackTraceDepth, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.INFO, stackTraceDepth, data, msgFormat, args);
  }

  /**
   * Log call stack with limited depth, Throwable and LogRecordData with {@link java.util.logging.Level#INFO}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   */
  public void info (int stackTraceDepth, Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.INFO, stackTraceDepth, th, data);
  }
  
  /**
   * Log call stack with limited depth, Throwable, LogRecordData and message string with {@link java.util.logging.Level#INFO}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msg the message string
   */
  public void info (int stackTraceDepth, Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.INFO, stackTraceDepth, th, data, msg);
  }

  /**
   * Log call stack with limited depth, Throwable, LogRecordData and message string with {@link java.util.logging.Level#INFO}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void info (int stackTraceDepth, Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    fireLogRecord(Level.INFO, stackTraceDepth, th, data, msgFormat, args);
  }
  
  // *************************************************************************

  /**
   * Check if a message with {@link java.util.logging.Level#WARNING} 
   * would actually be logged by this logger.
   * This check is based on the Loggers effective level, which may
   * be inherited from its parent.
   * @return true if WARNING is currently being logged.
   */  
  public boolean isWarningLoggable ()
  {
    return isLoggable(Level.WARNING);
  }
  
  /**
   * Log empty record with {@link java.util.logging.Level#WARNING}
   */
  public void warning ()
  {
    Logger.this.fireLogRecord(Level.WARNING);
  }

  /**
   * Log message string with {@link java.util.logging.Level#WARNING}.
   * @param msg the message string
   */
  public void warning (String msg)
  {
    Logger.this.fireLogRecord(Level.WARNING, msg);
  }

  /**
   * Log message string with {@link java.util.logging.Level#WARNING}.
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void warning (String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.WARNING, msgFormat, args);
  }
  
  /**
   * Log a Throwable object with {@link java.util.logging.Level#WARNING}.
   * @param th the Throwable object
   */
  public void warning (Throwable th)
  {
    Logger.this.fireLogRecord(Level.WARNING, th);
  }
  
  /**
   * Log a Throwable object and message string with {@link java.util.logging.Level#WARNING}.
   * @param th the Throwable object
   * @param msg the message string
   */
  public void warning (Throwable th, String msg)
  {
    Logger.this.fireLogRecord(Level.WARNING, th, msg);
  }

  /**
   * Log a Throwable object and message string with {@link java.util.logging.Level#WARNING}.
   * @param th the Throwable object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */  
  public void warning (Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.WARNING, th, msgFormat, args);
  }

  /**
   * Log LogRecordData with {@link java.util.logging.Level#WARNING}
   * @param data the LogRecordData object
   */
  public void warning (LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.WARNING, data);
  }

  /**
   * Log LogRecordData and message string with {@link java.util.logging.Level#WARNING}
   * @param data the LogRecordData object
   * @param msg the message string
   */
  public void warning (LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.WARNING, data, msg);
  }

  /**
   * Log LogRecordData and message string with {@link java.util.logging.Level#WARNING}
   * @param data the LogRecordData object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void warning (LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.WARNING, data, msgFormat, args);
  }

  /**
   * Log byte array with {@link java.util.logging.Level#WARNING}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   */
  public void warning (byte [] data)
  {
    Logger.this.fireLogRecord(Level.WARNING, data);
  }

  /**
   * Log byte array and message string with {@link java.util.logging.Level#WARNING}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msg the message string
   */
  public void warning (byte [] data, String msg)
  {
    Logger.this.fireLogRecord(Level.WARNING, data, msg);
  }

  /**
   * Log byte array and message string with {@link java.util.logging.Level#WARNING}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void warning (byte [] data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.WARNING, data, msgFormat, args);
  }
  
  /**
   * Log Throwable and LogRecordData object  with {@link java.util.logging.Level#WARNING}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   */
  public void warning (Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.WARNING, th, data);
  }

  /**
   * Log Throwable, LogRecordData and message string with {@link java.util.logging.Level#WARNING}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msg the message string  
   */
  public void warning (Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.WARNING, th, data, msg);
  }

  /**
   * Log Throwable, LogRecordData and message string with {@link java.util.logging.Level#WARNING}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void warning (Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.WARNING, th, data, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth and {@link java.util.logging.Level#WARNING}
   * @param stackTraceDepth the depth of call stack trace which should be published
   */
  public void warning (int stackTraceDepth)
  {
    Logger.this.fireLogRecord(Level.WARNING, stackTraceDepth);
  }

  /**
   * Log call stack with limited depth and message string with {@link java.util.logging.Level#WARNING}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msg the message string
   */
  public void warning (int stackTraceDepth, String msg)
  {
    Logger.this.fireLogRecord(Level.WARNING, stackTraceDepth, msg);
  }

  /**
   * Log call stack with limited depth and message string with {@link java.util.logging.Level#WARNING}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void warning (int stackTraceDepth, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.WARNING, stackTraceDepth, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth and Throwable with {@link java.util.logging.Level#WARNING}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   */
  public void warning (int stackTraceDepth, Throwable th)
  {
    Logger.this.fireLogRecord(Level.WARNING, stackTraceDepth, th);
  }
  
  /**
   * Log call stack with limited depth, Throwable and message string with {@link java.util.logging.Level#WARNING}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msg the message string
   */
  public void warning (int stackTraceDepth, Throwable th, String msg)
  {
    Logger.this.fireLogRecord(Level.WARNING, stackTraceDepth, th, msg);
  }

  /**
   * Log call stack with limited depth, Throwable and message string with {@link java.util.logging.Level#WARNING}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void warning (int stackTraceDepth, Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.WARNING, stackTraceDepth, th, msgFormat, args);
  }

  /**
   * Log call stack with limited depth, and LogRecordData with {@link java.util.logging.Level#WARNING}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   */  
  public void warning (int stackTraceDepth, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.WARNING, stackTraceDepth, data);
  }

  /**
   * Log call stack with limited depth, LogRecordData and message string with {@link java.util.logging.Level#WARNING}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   * @param msg the message string
   */
  public void warning (int stackTraceDepth, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.WARNING, stackTraceDepth, data, msg);
  }

  /**
   * Log call stack with limited depth, LogRecordData and message string with {@link java.util.logging.Level#WARNING}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void warning (int stackTraceDepth, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.WARNING, stackTraceDepth, data, msgFormat, args);
  }

  /**
   * Log call stack with limited depth, Throwable and LogRecordData with {@link java.util.logging.Level#WARNING}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   */
  public void warning (int stackTraceDepth, Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.WARNING, stackTraceDepth, th, data);
  }
  
  /**
   * Log call stack with limited depth, Throwable, LogRecordData and message string with {@link java.util.logging.Level#WARNING}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msg the message string
   */
  public void warning (int stackTraceDepth, Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.WARNING, stackTraceDepth, th, data, msg);
  }

  /**
   * Log call stack with limited depth, Throwable, LogRecordData and message string with {@link java.util.logging.Level#WARNING}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void warning (int stackTraceDepth, Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    fireLogRecord(Level.WARNING, stackTraceDepth, th, data, msgFormat, args);
  }
  
  // *************************************************************************
  
  /**
   * Check if a message with {@link java.util.logging.Level#SEVERE} 
   * would actually be logged by this logger.
   * This check is based on the Loggers effective level, which may
   * be inherited from its parent.
   * @return true if SEVERE is currently being logged.
   */
  public boolean isSevereLoggable ()
  {
    return isLoggable(Level.SEVERE);
  }
  
  /**
   * Log empty record with {@link java.util.logging.Level#SEVERE}
   */
  public void severe ()
  {
    Logger.this.fireLogRecord(Level.SEVERE);
  }

  /**
   * Log message string with {@link java.util.logging.Level#SEVERE}.
   * @param msg the message string
   */
  public void severe (String msg)
  {
    Logger.this.fireLogRecord(Level.SEVERE, msg);
  }

  /**
   * Log message string with {@link java.util.logging.Level#SEVERE}.
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void severe (String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.SEVERE, msgFormat, args);
  }
  
  /**
   * Log a Throwable object with {@link java.util.logging.Level#SEVERE}.
   * @param th the Throwable object
   */
  public void severe (Throwable th)
  {
    Logger.this.fireLogRecord(Level.SEVERE, th);
  }
  
  /**
   * Log a Throwable object and message string with {@link java.util.logging.Level#SEVERE}.
   * @param th the Throwable object
   * @param msg the message string
   */
  public void severe (Throwable th, String msg)
  {
    Logger.this.fireLogRecord(Level.SEVERE, th, msg);
  }

  /**
   * Log a Throwable object and message string with {@link java.util.logging.Level#SEVERE}.
   * @param th the Throwable object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void severe (Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.SEVERE, th, msgFormat, args);
  }

  /**
   * Log LogRecordData with {@link java.util.logging.Level#SEVERE}
   * @param data the LogRecordData object
   */
  public void severe (LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.SEVERE, data);
  }

  /**
   * Log LogRecordData and message string with {@link java.util.logging.Level#SEVERE}
   * @param data the LogRecordData object
   * @param msg the message string
   */
  public void severe (LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.SEVERE, data, msg);
  }

  /**
   * Log LogRecordData and message string with {@link java.util.logging.Level#SEVERE}
   * @param data the LogRecordData object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void severe (LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.SEVERE, data, msgFormat, args);
  }

  /**  
   * Log byte array with {@link java.util.logging.Level#SEVERE}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   */
  public void severe (byte [] data)
  {
    Logger.this.fireLogRecord(Level.SEVERE, data);
  }

  /**
   * Log byte array and message string with {@link java.util.logging.Level#SEVERE}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msg the message string
   */
  public void severe (byte [] data, String msg)
  {
    Logger.this.fireLogRecord(Level.SEVERE, data, msg);
  }

  /**
   * Log byte array and message string with {@link java.util.logging.Level#SEVERE}
   * @param data byte array which is transferred to an {@link logging.LogRecordDataHexDump} object
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * <!-- <a href="../util/Formatter.html#syntax">format string</a> not working -->
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void severe (byte [] data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.SEVERE, data, msgFormat, args);
  }
  
  /**
   * Log Throwable and LogRecordData object  with {@link java.util.logging.Level#ALL}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   */
  public void severe (Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.SEVERE, th, data);
  }

  /**
   * Log Throwable, LogRecordData and message string with {@link java.util.logging.Level#SEVERE}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msg the message string  
   */
  public void severe (Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.SEVERE, th, data, msg);
  }

  /**
   * Log Throwable, LogRecordData and message string with {@link java.util.logging.Level#SEVERE}
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void severe (Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.SEVERE, th, data, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth and {@link java.util.logging.Level#SEVERE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   */
  public void severe (int stackTraceDepth)
  {
    Logger.this.fireLogRecord(Level.SEVERE, stackTraceDepth);
  }

  /**
   * Log call stack with limited depth and message string with {@link java.util.logging.Level#SEVERE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msg the message string
   */
  public void severe (int stackTraceDepth, String msg)
  {
    Logger.this.fireLogRecord(Level.SEVERE, stackTraceDepth, msg);
  }

  /**
   * Log call stack with limited depth and message string with {@link java.util.logging.Level#SEVERE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void severe (int stackTraceDepth, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.SEVERE, stackTraceDepth, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth and Throwable with {@link java.util.logging.Level#SEVERE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   */
  public void severe (int stackTraceDepth, Throwable th)
  {
    Logger.this.fireLogRecord(Level.SEVERE, stackTraceDepth, th);
  }
  
  /**
   * Log call stack with limited depth, Throwable and message string with {@link java.util.logging.Level#SEVERE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msg the message string
   */
  public void severe (int stackTraceDepth, Throwable th, String msg)
  {
    Logger.this.fireLogRecord(Level.SEVERE, stackTraceDepth, th, msg);
  }

  /**
   * Log call stack with limited depth, Throwable and message string with {@link java.util.logging.Level#SEVERE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void severe (int stackTraceDepth, Throwable th, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.SEVERE, stackTraceDepth, th, msgFormat, args);
  }
  
  /**
   * Log call stack with limited depth, and LogRecordData with {@link java.util.logging.Level#SEVERE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   */
  public void severe (int stackTraceDepth, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.SEVERE, stackTraceDepth, data);
  }

  /**
   * Log call stack with limited depth, LogRecordData and message string with {@link java.util.logging.Level#SEVERE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   * @param msg the message string
   */
  public void severe (int stackTraceDepth, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.SEVERE, stackTraceDepth, data, msg);
  }

  /**
   * Log call stack with limited depth, LogRecordData and message string with {@link java.util.logging.Level#SEVERE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void severe (int stackTraceDepth, LogRecordData data, String msgFormat, Object ... args)
  {
    Logger.this.fireLogRecord(Level.SEVERE, stackTraceDepth, data, msgFormat, args);
  }

  /**
   * Log call stack with limited depth, Throwable and LogRecordData with {@link java.util.logging.Level#SEVERE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   */
  public void severe (int stackTraceDepth, Throwable th, LogRecordData data)
  {
    Logger.this.fireLogRecord(Level.SEVERE, stackTraceDepth, th, data);
  }
  
  /**
   * Log call stack with limited depth, Throwable, LogRecordData and message string with {@link java.util.logging.Level#SEVERE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msg the message string
   */
  public void severe (int stackTraceDepth, Throwable th, LogRecordData data, String msg)
  {
    Logger.this.fireLogRecord(Level.INFO, stackTraceDepth, th, data, msg);
  }

  /**
   * Log call stack with limited depth, Throwable, LogRecordData and message string with {@link java.util.logging.Level#SEVERE}
   * @param stackTraceDepth the depth of call stack trace which should be published
   * @param th the Throwable associated with this
   * @param data the LogRecordData associated with this
   * @param msgFormat The message format string for a {@link java.util.Formatter} object.
   * @param args Arguments referenced by the format specifiers in the msgFormat string.
   *             If there are more arguments than format specifiers, the extra arguments are ignored.
   *             The number of arguments is variable and may be zero.
   */
  public void severe (int stackTraceDepth, Throwable th, LogRecordData data, String msgFormat, Object ... args)
  {
    fireLogRecord(Level.SEVERE, stackTraceDepth, th, data, msgFormat, args);
  }
  
  // *************************************************************************
  
  /**
   * This class extends {@link java.util.logging.Level} and is used for Level {@link #DEBUG}
   */
  public static class SpecialLevel extends Level
  {
    /**
     * Constructs a individual Level object
     * @param name name of this log level
     * @param level value of this log level
     */
    public SpecialLevel (String name, int level)
    {
      super(name, level);
    }
  }

  
}
