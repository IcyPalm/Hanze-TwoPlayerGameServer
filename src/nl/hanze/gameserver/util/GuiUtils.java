package nl.hanze.gameserver.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

public class GuiUtils {

	public static void center(Window window) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) (screen.getWidth() / 2 - window.getWidth() / 2);
		int y = (int) (screen.getHeight() / 2 - window.getHeight() / 2);
		window.setLocation(x, y);
	}

}
