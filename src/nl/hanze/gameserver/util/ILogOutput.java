package nl.hanze.gameserver.util;

public interface ILogOutput {
	public void println(String msg);
	
	public void printf(String format, Object... args);
}
