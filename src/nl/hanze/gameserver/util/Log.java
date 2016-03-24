package nl.hanze.gameserver.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public static final ILogOutput DEBUG = new ILogOutput() {
		
		@Override
		public synchronized void println(String msg) {
			String format = "%s - %s";
			System.out.println(String.format(format, getDateTime(), msg));
		}
		
		@Override
		public synchronized void printf(String format, Object... args) {
			println(String.format(format, args));
		}
		
	};
	
	public static final ILogOutput ERROR = new ILogOutput() {
		
		@Override
		public synchronized void println(String msg) {
			String format = "%s - %s";
			System.out.println(String.format(format, getDateTime(), msg));
		}
		
		@Override
		public synchronized void printf(String format, Object... args) {
			println(String.format(format, args));
		}
		
	};
	
	private static String getDateTime() {
		return DATE_FORMATTER.format(new Date());
	}
}
