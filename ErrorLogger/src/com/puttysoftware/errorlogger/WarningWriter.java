package com.puttysoftware.errorlogger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class WarningWriter {
  // Fields
  private static final String MAC_PREFIX = "HOME"; //$NON-NLS-1$
  private static final String WIN_PREFIX = "USERPROFILE"; //$NON-NLS-1$
  private static final String UNIX_PREFIX = "HOME"; //$NON-NLS-1$
  private static final String MAC_DIR = "/Library/Logs/CrashReporter/"; //$NON-NLS-1$
  private static final String WIN_DIR = "\\Crash\\"; //$NON-NLS-1$
  private static final String UNIX_DIR = "/Crash/"; //$NON-NLS-1$
  private static final String MAC_EXT = ".nonfatal"; //$NON-NLS-1$
  private static final String WIN_EXT = ".nonfatal"; //$NON-NLS-1$
  private static final String UNIX_EXT = ".nonfatal"; //$NON-NLS-1$
  private final Throwable exception;
  private final String program;
  private final Date time;

  // Constructors
  WarningWriter(final Throwable problem, final String programName,
      final Calendar timeSource) {
    this.exception = problem;
    this.program = programName;
    this.time = timeSource.getTime();
  }

  // Methods
  void writeLogInfo() {
    try {
      // Make sure the needed directories exist first
      final File df = this.getLogFile();
      final File parent = new File(df.getParent());
      if (!parent.exists()) {
        final boolean res = parent.mkdirs();
        if (!res) {
          throw new FileNotFoundException("Cannot make directories!"); //$NON-NLS-1$
        }
      }
      // Print to the file
      try (PrintStream s = new PrintStream(
          new BufferedOutputStream(new FileOutputStream(df)))) {
        this.exception.printStackTrace(s);
        s.close();
      }
    } catch (final FileNotFoundException fnf) {
      // Print to standard error, if something went wrong
      this.exception.printStackTrace(System.err);
    }
  }

  private static String getLogDirPrefix() {
    final String osName = System.getProperty("os.name"); //$NON-NLS-1$
    if (osName.indexOf("Mac OS X") != -1) { //$NON-NLS-1$
      // Mac OS X
      return System.getenv(WarningWriter.MAC_PREFIX);
    } else if (osName.indexOf("Windows") != -1) { //$NON-NLS-1$
      // Windows
      return System.getenv(WarningWriter.WIN_PREFIX);
    } else {
      // Other - assume UNIX-like
      return System.getenv(WarningWriter.UNIX_PREFIX);
    }
  }

  private static String getLogDirectory() {
    final String osName = System.getProperty("os.name"); //$NON-NLS-1$
    if (osName.indexOf("Mac OS X") != -1) { //$NON-NLS-1$
      // Mac OS X
      return WarningWriter.MAC_DIR;
    } else if (osName.indexOf("Windows") != -1) { //$NON-NLS-1$
      // Windows
      return WarningWriter.WIN_DIR;
    } else {
      // Other - assume UNIX-like
      return WarningWriter.UNIX_DIR;
    }
  }

  private static String getLogFileExtension() {
    final String osName = System.getProperty("os.name"); //$NON-NLS-1$
    if (osName.indexOf("Mac OS X") != -1) { //$NON-NLS-1$
      // Mac OS X
      return WarningWriter.MAC_EXT;
    } else if (osName.indexOf("Windows") != -1) { //$NON-NLS-1$
      // Windows
      return WarningWriter.WIN_EXT;
    } else {
      // Other - assume UNIX-like
      return WarningWriter.UNIX_EXT;
    }
  }

  private String getStampSuffix() {
    final SimpleDateFormat sdf = new SimpleDateFormat(
        "'_'yyyyMMdd'_'HHmmssSSS"); //$NON-NLS-1$
    return sdf.format(this.time);
  }

  private String getLogFileName() {
    return this.program;
  }

  private File getLogFile() {
    final StringBuilder b = new StringBuilder();
    b.append(WarningWriter.getLogDirPrefix());
    b.append(WarningWriter.getLogDirectory());
    b.append(this.getLogFileName());
    b.append(this.getStampSuffix());
    b.append(WarningWriter.getLogFileExtension());
    return new File(b.toString());
  }
}
