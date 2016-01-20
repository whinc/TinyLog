package com.whinc.tinylog;

import android.text.TextUtils;

/**
 * Enhanced version of {@link Log}
 */
public class Log {
    public static final int LEVEL_VERBOSE = android.util.Log.VERBOSE;
    public static final int LEVEL_DEBUG = android.util.Log.DEBUG;
    public static final int LEVEL_INFO = android.util.Log.INFO;
    public static final int LEVEL_WARN = android.util.Log.WARN;
    public static final int LEVEL_ERROR = android.util.Log.ERROR;

    /** Default log formatter shows log output in one line. */
    public static final Formatter DEFAULT_FORMATTER = new DefaultFormatter();

	private static boolean sEnable = true;
    private static boolean sPrintLineInfo = true;
    private static int sLowestLevel = LEVEL_VERBOSE;
    private static Formatter sFormatter = DEFAULT_FORMATTER;
    private static Interceptor sInterceptor = null;

	// Disable default constructor
	private Log() {}

    /** Enable or disable log output (default enable) */
	public static void enable(boolean b) {
		sEnable = b;
	}

    /** Enable or disable print the line info. */
	public static void enablePrintLineInfo(boolean b) {
        sPrintLineInfo = b;
    }

    /**
     * Set custom formatter
     * @param formatter reference to {@link Log.Formatter}
     * @return return previous formatter.
     */
    public static Formatter setFormatter(Formatter formatter) {
        Formatter old = sFormatter;
        sFormatter = formatter == null ? DEFAULT_FORMATTER : formatter;
        return old;
    }

    public static void setInterceptor(Interceptor interceptor) {
        sInterceptor = interceptor;
    }

    public static void restoreDefaultSetting() {
        sEnable = true;
        sPrintLineInfo = true;
        sLowestLevel = LEVEL_VERBOSE;
        sFormatter = DEFAULT_FORMATTER;
        sInterceptor = null;
    }

    /**
     * Set the least log level.
     * @param level one of {@link Log#LEVEL_VERBOSE}, {@link Log#LEVEL_DEBUG},
     * {@link Log#LEVEL_INFO}, {@link Log#LEVEL_WARN}, {@link Log#LEVEL_ERROR}
     */
    public static void setLevel(int level) {
        sLowestLevel = level;
    }

    /**
     * Get stack trace element object
     * @param depth the depth from caller method to this method.
     * @return
     */
    private static StackTraceElement getStackTraceElement(int depth) {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        depth = Math.max(0, Math.min(depth, stackTrace.length - 1));
        return stackTrace[depth];
    }

    /**
     * check if log output is intercepted
     * @param tag
     * @param msg
     * @return return true if log output is intercepted, otherwise return false.
     */
    private static boolean intercept(String tag, String msg) {
        if (sInterceptor == null) {
            return false;
        }

        // store interceptor before call method onIntercept()
        Interceptor prevInterceptor = sInterceptor;
        /* set interceptor to null, this can prevent from recursion call if user call
        Log.v (Log.d, Log.i, etc...) in onIntercept() which will lead to StackOverflow */
        sInterceptor = null;
        boolean r = prevInterceptor.onIntercept(tag, msg);
        // restore interceptor after call method onIntercept()
        sInterceptor = prevInterceptor;
        return r;
    }

    /**
     * <p>Print log on specified level</p>
     * @param level log level
     * @param tag tag
     * @param msg log message
     * @param extra extra message
     * @param depth depth of stack trace info
     */
    private static void print(int level, String tag, String msg, String extra, int depth) {
        if (sEnable && sLowestLevel <= level) {
            if (!intercept(tag, msg)) {
                if (sPrintLineInfo) {
                    printImpl(level, tag, sFormatter.format(msg, getStackTraceElement(depth)));
                } else {
                    printImpl(level, tag, msg);
                }
            }
            if (!TextUtils.isEmpty(extra)) {
                printImpl(level, tag, extra);
            }
        }
    }

    private static void printImpl(int level, String tag, String msg) {
        switch (level) {
            case LEVEL_VERBOSE:
                android.util.Log.v(tag, msg);
                break;
            case LEVEL_DEBUG:
                android.util.Log.d(tag, msg);
                break;
            case LEVEL_INFO:
                android.util.Log.i(tag, msg);
                break;
            case LEVEL_WARN:
                android.util.Log.w(tag, msg);
                break;
            case LEVEL_ERROR:
                android.util.Log.e(tag, msg);
                break;
        }
    }

    public static void v(String tag, String msg) {
        print(LEVEL_VERBOSE, tag, msg, null, 3);
    }

    public static void v(String tag, String msg, int callStackDepth) {
        print(LEVEL_VERBOSE, tag, msg, getCallStack(3, callStackDepth), 3);
    }

    public static void v(String tag, String msg, Throwable tr) {
        print(LEVEL_VERBOSE, tag, msg, getStackString(tr), 3);
    }

    public static void i(String tag, String msg) {
        print(LEVEL_INFO, tag, msg, null, 3);
    }

    public static void i(String tag, String msg, int callStackDepth) {
        print(LEVEL_INFO, tag, msg, getCallStack(3, callStackDepth), 3);
    }

    public static void i(String tag, String msg, Throwable tr) {
        print(LEVEL_INFO, tag, msg, getStackString(tr), 3);
    }

    public static void d(String tag, String msg) {
        print(LEVEL_DEBUG, tag, msg, null, 3);
    }

    public static void d(String tag, String msg, int callStackDepth) {
        print(LEVEL_DEBUG, tag, msg, getCallStack(3, callStackDepth), 3);
    }

    public static void d(String tag, String msg, Throwable tr) {
        print(LEVEL_DEBUG, tag, msg, getStackString(tr), 3);
    }

    public static void w(String tag, String msg) {
        print(LEVEL_WARN, tag, msg, null, 3);
    }

    public static void w(String tag, String msg, int callStackDepth) {
        print(LEVEL_WARN, tag, msg, getCallStack(3, callStackDepth), 3);
    }

    public static void w(String tag, String msg, Throwable tr) {
        print(LEVEL_WARN, tag, msg, getStackString(tr), 3);
    }

    public static void e(String tag, String msg) {
        print(LEVEL_ERROR, tag, msg, null, 3);
    }

    public static void e(String tag, String msg, int callStackDepth) {
        print(LEVEL_ERROR, tag, msg, getCallStack(3, callStackDepth), 3);
    }

    public static void e(String tag, String msg, Throwable tr) {
        print(LEVEL_ERROR, tag, msg, getStackString(tr), 3);
    }

    public static void e(String tag, Throwable tr) {
        e(tag, "", tr);
    }

    /**
     * Handy function to get a loggable stack trace from a Throwable
     * @param tr An exception to log
     */
    public static String getStackString(Throwable tr) {
        return android.util.Log.getStackTraceString(tr);
    }

    /**
     * <p>Get call stack</p>
     * @param start call stack start level
     * @param depth call stack depth
     * @return
     */
    public static String getCallStack(int start, int depth) {
        Throwable tr = new Throwable();
        StackTraceElement[] arr = tr.getStackTrace();
        StringBuilder builder = new StringBuilder();
        // index start from '1' exclude current method call stack info.
        for (int i = start; i < (start + depth) && i < arr.length; ++i) {
            StackTraceElement e = arr[i];
            String callInfo = String.format("    %s.%s(%s:%d)",
                    e.getClassName(),
                    e.getMethodName(),
                    e.getFileName(),
                    e.getLineNumber()
            );
            builder.append(callInfo).append("\n");
        }
        return builder.toString();
    }

    /**
     * Format log output
     */
    public interface Formatter {
        String format(String msg, StackTraceElement e);
    }

    private static class DefaultFormatter implements Formatter{

        @Override
        public String format(String msg, StackTraceElement e) {
            return String.format("%s.%s(%s:%d):%s",
                    e.getClassName(),
                    e.getMethodName(),
                    e.getFileName(),
                    e.getLineNumber(),
                    msg
            );
        }
    }

    /**
     * Intercept log output
     */
    public interface Interceptor {
        /**
         * This method will be called every time print log
         * @return return true means user has handled log output and the normal output process will
         * be ignored, otherwise the normal output process will be executed.
         * @param tag log tag
         * @param msg log message
         */
        boolean onIntercept(String tag, String msg);
    }
}
