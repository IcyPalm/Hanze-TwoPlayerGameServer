package nl.hanze.gameserver.app;

import java.io.File;
import java.io.IOException;

import nl.hanze.gameserver.server.GameServer;
import nl.hanze.gameserver.ui.GameServerGUI;
import nl.hanze.gameserver.util.Log;

public class Application {
	
	private static Application instance;
	
	public static Application getInstance() {
		if(instance == null) {
			instance = new Application();
		}
		
		return instance;
	}
	
	private String shortName;
	private String name;
	private String version;
	
	private File path;
	private Settings settings;
	private boolean closing;
	
	private GameModuleLoader gameLoader;
	
	private GameServer gameServer;
	private GameServerGUI gameServerGui;
	
	private Application() {
		initialize();
		
		closing = false;
		
		File gameModulePath = new File(settings.getGameModulePath());
		if(!gameModulePath.exists()) {
			gameModulePath.mkdirs();
			gameModulePath.mkdir();
		}
		gameLoader = new GameModuleLoader(gameModulePath);
		
		for(String gameType: gameLoader.getGameTypeList()) {
			Log.DEBUG.printf("Loaded game module: %s", gameType);
		}
		
		try {
			gameServer = new GameServer();
			gameServerGui = new GameServerGUI(gameServer);
		} catch (IOException e) {
			Log.ERROR.printf("Error while instantiating GameServer: %s", e);
			
			exit();
		}
	}
	
	private void initialize() {
		shortName = "StrategicGameServer";
		name = "Strategic Game Server";
		version = "1.0";
		
		path = new File("").getAbsoluteFile();
		settings = new Settings(new File(path, "settings.conf"));
		
		instance = this;
	}
	
	public String getShortName() {
		return shortName;
	}
	
	public String getName() {
		return name;
	}
	
	public String getVersion() {
		return version;
	}
	
	public GameServer getGameServer() {
		return gameServer;
	}
	
	public GameServerGUI getGameServerGui() {
		return gameServerGui;
	}
	
	public GameModuleLoader getGameLoader() {
		return gameLoader;
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	public File getLocalFile(String filename) {
		return new File(path, filename);
	}
	
	public File getPath() {
		return path;
	}
	
	public void exit() {
		Log.DEBUG.println("Started closing application");
		closing = true;
		
		Log.DEBUG.println("Saving settings");
		settings.save();
		
		Log.DEBUG.println("Closing GameServerGUI");
		try {
			gameServerGui.exit();
		} catch(Exception e) {
			Log.ERROR.printf("Error while closing GameServerGUI: %s", e);
		}
		
		Log.DEBUG.println("Closing GameServer");
		try {
			gameServer.exit();
		} catch(Exception e) {
			Log.ERROR.printf("Error while closing GameServer: %s", e);
		}
		
		Log.DEBUG.println("Application closed");
		System.exit(0);
	}
	
	public boolean isClosing() {
		return closing;
	}
	
	public static void main(String[] args) {
		getInstance();
		Log.DEBUG.println("Application started");
	}
}
