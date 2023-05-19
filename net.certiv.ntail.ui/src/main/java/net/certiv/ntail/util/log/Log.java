package net.certiv.ntail.util.log;

import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

public class Log {

	public static void trace(String message) {
		log(Level.TRACE, message, null);
	}

	public static void trace(String format, Object... args) {
		log(Level.TRACE, String.format(format, args), null);
	}

	public static void trace(String message, Throwable e) {
		log(Level.TRACE, message, e);
	}

	public static void debug(String message) {
		log(Level.DEBUG, message, null);
	}

	public static void debug(String format, Object... args) {
		log(Level.DEBUG, String.format(format, args), null);
	}

	public static void debug(String message, Throwable e) {
		log(Level.DEBUG, message, e);
	}

	public static void info(String message) {
		log(Level.INFO, message, null);
	}

	public static void info(String format, Object... args) {
		log(Level.INFO, String.format(format, args), null);
	}

	public static void info(String message, Throwable e) {
		log(Level.INFO, message, e);
	}

	public static void warn(String message) {
		log(Level.WARN, message, null);
	}

	public static void warn(String format, Object... args) {
		log(Level.WARN, String.format(format, args), null);
	}

	public static void warn(String message, Throwable e) {
		log(Level.WARN, message, e);
	}

	public static void error(String message) {
		log(Level.ERROR, message, null);
	}

	public static void error(String format, Object... args) {
		log(Level.ERROR, String.format(format, args), null);
	}

	public static void error(Throwable e, String format, Object... args) {
		log(Level.ERROR, String.format(format, args), e);
	}

	public static void error(String message, Throwable e) {
		log(Level.ERROR, message, e);
	}

	public static void fatal(String message) {
		log(Level.FATAL, message, null);
	}

	public static void fatal(String message, Throwable e) {
		log(Level.FATAL, message, e);
	}

	private static void log(Level level, String msg, Throwable e) {
		StackFrame frame = caller();
		if (frame != null) {
			Class<?> srcCls = frame.getDeclaringClass();
			if (loggable(srcCls, level)) {
				String out = sprintf(frame, msg);
				logger.getLog().log(new Status(level.severity(), logger.getClass().getName(), 0, out, e));
			}
		}
	}

	public static void printf(Level level, String fmt, Object... args) {
		log(level, String.format(fmt, args), null);
	}

	public static void printf(Level level, Throwable e, String fmt, Object... args) {
		log(level, String.format(fmt, args), e);
	}

	public static String sprintf(StackFrame frame, String fmt, Object... args) {
		String msg = String.format(fmt, args);
		return String.format("%s%s", info(frame), msg); //$NON-NLS-1$
	}

	private static String info(StackFrame f) {
		String caller = f != null ? String.format("%s:%s", f.getClassName(), f.getLineNumber()) : UNKNOWN;  //$NON-NLS-1$
		return String.format("%-40s : ", caller);  //$NON-NLS-1$
	}

	/** @return the stack frame for the class that called Log, or {@code null} */
	public static StackFrame caller() {
		StackWalker walker = StackWalker.getInstance(OPTIONS);
		return walker.walk(Log::caller);
	}

	// find the stack frame for the class that called Log
	private static StackFrame caller(Stream<StackFrame> frames) {
		return frames //
				.filter(f -> !f.getClassName().equals(Log.class.getName())) //
				.findFirst() //
				.orElse(null);
	}

	// -------------------------------------

	private static final EnumSet<Option> OPTIONS = EnumSet.of(StackWalker.Option.SHOW_HIDDEN_FRAMES,
			StackWalker.Option.RETAIN_CLASS_REFERENCE);

	// key=src class hashcode; value=assigned reporting level
	private static final HashMap<Integer, Level> Levels = new HashMap<>();

	private static final int LogId = Log.class.hashCode();
	private static final String UNKNOWN = "Unknown";

	private static boolean initd_;
	private static Plugin logger;

	public static void init(Plugin plugin) {
		init(plugin, Level.WARN);
	}

	public static void init(Plugin plugin, Level global) {
		setLevel(plugin.getClass(), global);
		setLevel(Log.class, Level.WARN);
		initd_ = true;
		logger = plugin;
	}

	private static void chkInit() {
		if (!initd_) {
			setLevel(Log.class, Level.WARN);
			initd_ = true;
			logger = null;
		}
	}

	/**
	 * Returns {@code true} if the logger has been initialized.
	 * <p>
	 * The first call to use the logger will force initialization. The initialization will use the
	 * currently set log name and location to create a log file. If no log name or location is set,
	 * defaults are used. If the name or location is set, or reset, after any initialization, the
	 * logger is forced to reinitialize.
	 *
	 * @return the current initialization state
	 */
	public static boolean isInitalized() {
		return initd_;
	}

	public static void setLevel() {
		setLevel((Level) null);
	}

	public static void setLevel(String level) {
		if (level == null) setLevel();
		setLevel(Level.valueOf(level));
	}

	public static void setLevel(Level level) {
		if (level == null) level = defaultLevel();

		StackFrame frame = caller();
		if (frame != null) {
			Class<?> srcCls = frame.getDeclaringClass();
			setLevel(srcCls, level);
		}
	}

	private static void setLevel(Class<?> srcCls, Level level) {
		int srcId = srcCls.hashCode();
		Levels.put(srcId, level);
	}

	private static boolean loggable(Class<?> srcCls, Level level) {
		chkInit();
		if (logger == null) return false;

		Level srcLevel = levelOf(srcCls);
		return level.isMoreSpecificThan(srcLevel);
	}

	private static Level levelOf(Class<?> srcCls) {
		Level level = Levels.get(srcCls.hashCode());
		return level != null ? level : defaultLevel();
	}

	private static Level defaultLevel() {
		chkInit();

		int global = logger != null ? logger.getClass().hashCode() : LogId;
		return Levels.get(global);
	}
}
