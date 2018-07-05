
package logging;

import java.io.PrintStream;


/**
 * For printing formatted text with {@link logging.Logger}.<br>
 * <p>This class is using ANSI escape codes for text formatting.</p>
 * <p>Note that result depends on type of terminal. Therefore
 * you can select terminal type in constructor or by defining the System Property
 * <i>logging.LogRecordDataFormattedText.Terminal</i> (which can be set to any enum constants of 
 * {@link logging.LogRecordDataFormattedText.Terminal}.
 * <p>
 * <u>Example:</u>
 * <blockquote><pre>
 * LogRecordDataFormattedText t = new LogRecordDataFormattedText();
 * t.append("Hello ").bgRed().append("world").bgDefault().append("!").linefeed();
 * t.bold().append("Bold?").plain().linefeed();
 * t.underline().append("Underline?").plain().linefeed();
 * t.italic().append("Italic?").plain().linefeed();
 * t.inverse().append("Inverse?").plain().linefeed();
 * t.striked().append("Striked?").plain().linefeed();
 * t.appendLink("http://google.com").linefeed();
 * LOG.info(t);
 * </pre></blockquote>
 * @see <a href="https://en.wikipedia.org/wiki/ANSI_escape_code" target="blank">https://en.wikipedia.org/wiki/ANSI_escape_code</a>
 * @see <a href="http://wiki.netbeans.org/TerminalEmulatorHyperlinking" target="blank">http://wiki.netbeans.org/TerminalEmulatorHyperlinking</a>
 * @author Manfred Steiner (sx@htl-kaindorf.ac.at)
 */
public class LogRecordDataFormattedText implements LogRecordData
{
  public static enum Terminal  { LINUX, NETBEANS }
  
  String s;
  private final StringBuilder text;
  private final Terminal terminal;
  
  /**
   * Constructor.
   * The value of the system property <b>logging.LogRecordDataFormattedText.Terminal</b>
   * is taken to select the proper link format.
   */
  public LogRecordDataFormattedText ()
  {
    text = new StringBuilder();

    Terminal t = null;
    String p = System.getProperty(getClass().getName() + ".Terminal");
    if (p!=null)
    {
      for (Terminal x : Terminal.values())
      {
        if (x.name().equals(p))
        {
          t = x;
          break;
        }
      }
    }
    terminal = t==null ? Terminal.LINUX : t;
  }

  /**
   * Constructor.
   * @param terminal the link output format
   */
  public LogRecordDataFormattedText (Terminal terminal)
  {
    text = new StringBuilder();
    this.terminal = terminal;
  }
  
  /**
   * Append a character sequence.
   * @param s the character sequence
   * @return this object
   */
  public LogRecordDataFormattedText append (CharSequence s)
  {
    text.append(s);
    return this;
  }

  /**
   * Print line feed.
   */
  public void println ()
  {
    text.append(System.lineSeparator());
  }

  /**
   * Print Character Sequence.
   * @param s the character sequence
   */
  public void print (CharSequence s)
  {
    text.append(s);
  }
  
  /**
   * Print Character Sequence and line feed.
   * @param s the character sequence
   */
  public void println (CharSequence s)
  {
    text.append(s).append(System.lineSeparator());
  }
  
  /**
   * Method which is called from handler to write out the stream.
   * @param out the PrintStream which is used to write out this objects data
   */
  @Override
  public void print (PrintStream out)
  {
    out.println(text.toString());
  }

  /**
   * Append line feed.
   * @return this object
   */
  public LogRecordDataFormattedText linefeed ()
  {
    text.append(System.lineSeparator());
    return this;
  }

  /**
   * Append link.
   * The format of the link can be configured with the constructor {@link #LogRecordDataFormattedText(Terminal)}
   * or by the system property {@code logging.LogRecordDataFormattedText.Terminal}
   * @param target the link
   * @return this object
   */
  public LogRecordDataFormattedText appendLink (String target)
  {
    switch (terminal)
    {
      case LINUX:
        text.append(target);
        break;
        
      case NETBEANS: // http://wiki.netbeans.org/TerminalEmulatorHyperlinking
        text.append("\033]10;").append(target).append(';').append(target).append("\007");
        break;
        
      default:
        throw new IllegalArgumentException();
    }
    return this;
  }
  
  /**
   * Clear all format attributes and return to plain text format.
   * @return this object
   */
  public LogRecordDataFormattedText plain ()
  {
    text.append("\033[0;0m");
    return this;
  }

  /**
   * Set Underline attribute.
   * @return this object
   */
  public LogRecordDataFormattedText underline ()
  {
    text.append("\033[0;4m");
    return this;
  }
 
  /**
   * Set Bold attribute.
   * @return this object
   */
  public LogRecordDataFormattedText bold ()
  {
    text.append("\033[0;1m");
    return this;
  }

  /**
   * Set Italic attribute.
   * @return this object
   */
  public LogRecordDataFormattedText italic ()
  {
    text.append("\033[0;3m");
    return this;
  }

  /**
   * Set Inverse attribute.
   * @return this object
   */
  public LogRecordDataFormattedText inverse ()
  {
    text.append("\033[0;7m");
    return this;
  }

  /**
   * Set Striked attribute.
   * @return this object
   */
  public LogRecordDataFormattedText striked ()
  {
    text.append("\033[0;9m");
    return this;
  }
  
  /**
   * Return to default foreground color
   * @return this object
   */
  public LogRecordDataFormattedText fgDefault ()
  {
    text.append("\033[0;39m");
    return this;
  }

  /**
   * Set foreground color Red
   * @return this object
   */
  public LogRecordDataFormattedText fgRed ()
  {
    text.append("\033[0;31m");
    return this;
  }

  /**
   * Set foreground color Green
   * @return this object
   */
  public LogRecordDataFormattedText fgGreen ()
  {
    text.append("\033[0;32m");
    return this;
  }

  /**
   * Set foreground color Yellow
   * @return this object
   */
  public LogRecordDataFormattedText fgYellow ()
  {
    text.append("\033[0;33m");
    return this;
  }
  
  /**
   * Set foreground color Blue
   * @return this object
   */
  public LogRecordDataFormattedText fgBlue ()
  {
    text.append("\033[0;34m");
    return this;
  }

  /**
   * Set foreground color Magenta
   * @return this object
   */
  public LogRecordDataFormattedText fgMagenta ()
  {
    text.append("\033[0;35m");
    return this;
  }
  
  /**
   * Set foreground color Cyan
   * @return this object
   */
  public LogRecordDataFormattedText fgCyan ()
  {
    text.append("\033[0;36m");
    return this;
  }
  
  /**
   * Set foreground color White
   * @return this object
   */
  public LogRecordDataFormattedText fgWhite ()
  {
    text.append("\033[0;37m");
    return this;
  }

  /**
   * Return to default background color
   * @return this object
   */
  public LogRecordDataFormattedText bgDefault ()
  {
    text.append("\033[0;49m");
    return this;
  }

  /**
   * Set background color Red
   * @return this object
   */
  public LogRecordDataFormattedText bgRed ()
  {
    text.append("\033[0;41m");
    return this;
  }

  /**
   * Set background color Green
   * @return this object
   */
  public LogRecordDataFormattedText bgGreen ()
  {
    text.append("\033[0;42m");
    return this;
  }

  /**
   * Set background color Yellow
   * @return this object
   */
  public LogRecordDataFormattedText bgYellow ()
  {
    text.append("\033[0;43m");
    return this;
  }
  
  /**
   * Set background color Blue
   * @return this object
   */
  public LogRecordDataFormattedText bgBlue ()
  {
    text.append("\033[0;44m");
    return this;
  }

  /**
   * Set background color Magenta
   * @return this object
   */
  public LogRecordDataFormattedText bgMagenta ()
  {
    text.append("\033[0;45m");
    return this;
  }
  
  /**
   * Set background color Cyan
   * @return this object
   */
  public LogRecordDataFormattedText bgCyan ()
  {
    text.append("\033[0;46m");
    return this;
  }
  
  /**
   * Set background color White
   * @return this object
   */
  public LogRecordDataFormattedText bgWhite ()
  {
    text.append("\033[0;47m");
    return this;
  }
  
}
