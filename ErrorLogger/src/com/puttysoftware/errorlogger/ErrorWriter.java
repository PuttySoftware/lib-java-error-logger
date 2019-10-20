package com.puttysoftware.errorlogger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class ErrorWriter {
  // Fields
  private static final String MAC_PREFIX = "HOME"; //$NON-NLS-1$
  private static final String WIN_PREFIX = "USERPROFILE"; //$NON-NLS-1$
  private static final String UNIX_PREFIX = "HOME"; //$NON-NLS-1$
  private static final String MAC_DIR = "/Library/Logs/CrashReporter/"; //$NON-NLS-1$
  private static final String WIN_DIR = "\\Crash\\"; //$NON-NLS-1$
  private static final String UNIX_DIR = "/Crash/"; //$NON-NLS-1$
  private static final String MAC_EXT = ".crash"; //$NON-NLS-1$
  private static final String WIN_EXT = ".log"; //$NON-NLS-1$
  private static final String UNIX_EXT = ".log"; //$NON-NLS-1$
  private final Throwable exception;
  private final Date time;
  private final String program;

  // Constructors
  ErrorWriter(final Throwable problem, final String programName,
      final Calendar timeSource) {
    this.exception = problem;
    this.time = timeSource.getTime();
    this.program = programName;
  }

  // Methods
  void writeErrorInfo() {
    try {
      // Make sure the needed directories exist first
      final File df = this.getErrorFile();
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

  private static String getErrorDirPrefix() {
    final String osName = System.getProperty("os.name"); //$NON-NLS-1$
    if (osName.indexOf("Mac OS X") != -1) { //$NON-NLS-1$
      // Mac OS X
      return System.getenv(ErrorWriter.MAC_PREFIX);
    } else if (osName.indexOf("Windows") != -1) { //$NON-NLS-1$
      // Windows
      return System.getenv(ErrorWriter.WIN_PREFIX);
    } else {
      // Other - assume UNIX-like
      return System.getenv(ErrorWriter.UNIX_PREFIX);
    }
  }

  private static String getErrorDirectory() {
    final String osName = System.getProperty("os.name"); //$NON-NLS-1$
    if (osName.indexOf("Mac OS X") != -1) { //$NON-NLS-1$
      // Mac OS X
      return ErrorWriter.MAC_DIR;
    } else if (osName.indexOf("Windows") != -1) { //$NON-NLS-1$
      // Windows
      return ErrorWriter.WIN_DIR;
    } else {
      // Other - assume UNIX-like
      return ErrorWriter.UNIX_DIR;
    }
  }

  private static String getErrorFileExtension() {
    final String osName = System.getProperty("os.name"); //$NON-NLS-1$
    if (osName.indexOf("Mac OS X") != -1) { //$NON-NLS-1$
      // Mac OS X
      return ErrorWriter.MAC_EXT;
    } else if (osName.indexOf("Windows") != -1) { //$NON-NLS-1$
      // Windows
      return ErrorWriter.WIN_EXT;
    } else {
      // Other - assume UNIX-like
      return ErrorWriter.UNIX_EXT;
    }
  }

  private String getStampSuffix() {
    final SimpleDateFormat sdf = new SimpleDateFormat(
        "'_'yyyyMMdd'_'HHmmssSSS"); //$NON-NLS-1$
    return sdf.format(this.time);
  }

  private String getErrorFileName() {
    return this.program;
  }

  private File getErrorFile() {
    final StringBuilder b = new StringBuilder();
    b.append(ErrorWriter.getErrorDirPrefix());
    b.append(ErrorWriter.getErrorDirectory());
    b.append(this.getErrorFileName());
    b.append(this.getStampSuffix());
    b.append(ErrorWriter.getErrorFileExtension());
    return new File(b.toString());
  }
}
