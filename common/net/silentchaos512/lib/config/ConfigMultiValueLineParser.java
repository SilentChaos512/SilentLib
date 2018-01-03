package net.silentchaos512.lib.config;

import javax.annotation.Nullable;

import net.silentchaos512.lib.util.LogHelper;

/**
 * Designed to make parsing strings into multiple values easier. Supports multiple data types in a single line. Handles
 * error logging if provided with a LogHelper.
 * 
 * @author SilentChaos512
 * @since 2.2.16
 */
public class ConfigMultiValueLineParser {

  /** Used for error logging. */
  final String configId;
  /** Used for error logging if not null. */
  final @Nullable LogHelper log;
  /** The delimiter used to split each line. Matching whitespace with "\\s" is recommended. */
  final String delimiter;
  /** The data type of each value in each line. Only certain types are supported (see parse method) */
  final Class[] valueClasses;
  /** Strict mode. If true, lines with extra elements will error. */
  final boolean strict;

  public ConfigMultiValueLineParser(String configId, @Nullable LogHelper log, String delimiter,
      Class... valueClasses) {

    this(configId, log, delimiter, false, valueClasses);
  }

  public ConfigMultiValueLineParser(String configId, @Nullable LogHelper log, String delimiter,
      boolean strict, Class... valueClasses) {

    this.configId = configId;
    this.log = log;
    this.delimiter = delimiter;
    this.strict = strict;
    this.valueClasses = valueClasses;
  }

  /**
   * Attempt to parse the line. If an error occurs (too few values in line, a value can't be parsed), null will be
   * returned. Lines with extra values will still be parsed if not in strict mode.
   * 
   * Currently supported types (as of version 2.2.16): Integer, Float, Boolean, String
   * 
   * @param line
   *          The line to parse. Typically one line in a string list from your config.
   * @return Null if some error occurs. Otherwise, an array of parsed values, safe to cast to the appropriate types.
   *         Array length is equal to the length of valueClasses.
   */
  public @Nullable Object[] parse(String line) {

    String[] values = line.split(delimiter);
    if (values.length < valueClasses.length) {
      warning("Too few values in line '" + line + "'. Ignoring the entire line.");
      return null;
    } else if (values.length > valueClasses.length) {
      if (strict) {
        warning("Too many values in line '" + line + "'. Ignoring the entire line.");
        return null;
      }
      warning("Too many values in line '" + line + "'. Ignoring extra values and parsing others.");
    }

    Object[] result = new Object[valueClasses.length];

    for (int i = 0; i < valueClasses.length; ++i) {
      String value = values[i];
      // Integer
      if (valueClasses[i] == Integer.class) {
        try {
          result[i] = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
          warning("Could not parse '" + value + "' as integer.");
          return null;
        }
      }
      // Float
      else if (valueClasses[i] == Float.class) {
        try {
          result[i] = Float.parseFloat(value);
        } catch (NumberFormatException ex) {
          warning("Could not parse '" + value + "' as float.");
          return null;
        }
      }
      // Boolean
      else if (valueClasses[i] == Boolean.class) {
        if (value.equalsIgnoreCase("true")) {
          result[i] = true;
        } else if (value.equalsIgnoreCase("false")) {
          result[i] = false;
        } else {
          warning("Could not parse '" + value + "' as boolean.");
          return null;
        }
      }
      // String
      else if (valueClasses[i] == String.class) {
        result[i] = value;
      }
      // Unknown
      else {
        warning("Don't know how to parse " + valueClasses[i] + "!");
        return null;
      }
    }

    return result;
  }

  /**
   * Log a warning, if log is not null.
   * 
   * @param message
   */
  private void warning(String message) {

    if (log != null) {
      log.warning(configId + ": " + message);
    }
  }
}
