package nl.hanze.gameserver.app;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import nl.hanze.gameserver.util.Log;
import nl.hanze.t23i.gamemodule.extern.AbstractGameModule;

public class GameModuleLoader {

	private HashMap<String, Class<? extends AbstractGameModule>> gameModuleMap;
	private ArrayList<String> gameTypeList;
	
	public GameModuleLoader(File modulePath) {
		gameModuleMap = new HashMap<String, Class<? extends AbstractGameModule>>();
		loadJarFiles(modulePath);
		
		gameTypeList = new ArrayList<String>(gameModuleMap.keySet());
	}
	
	public ArrayList<String> getGameTypeList() {
		return gameTypeList;
	}
	
	public AbstractGameModule loadGameModule(String gameTypeName, String playerOne, String playerTwo) {
		AbstractGameModule gameModule = null;
		
		Class<? extends AbstractGameModule> gameModuleClass = gameModuleMap.get(gameTypeName);
		
		if(gameModuleClass == null) {
			return null;
		}
		
		try {
			Constructor<? extends AbstractGameModule> constructor = gameModuleClass.getConstructor(String.class, String.class);
			gameModule = constructor.newInstance(playerOne, playerTwo);
		} catch (SecurityException e) {
			Log.ERROR.printf("Error loading game module '%s': %s", gameTypeName, e);
		} catch (NoSuchMethodException e) {
			Log.ERROR.printf("Error loading game module '%s': %s", gameTypeName, e);;
		} catch (IllegalArgumentException e) {
			Log.ERROR.printf("Error loading game module '%s': %s", gameTypeName, e);;
		} catch (InstantiationException e) {
			Log.ERROR.printf("Error loading game module '%s': %s", gameTypeName, e);;
		} catch (IllegalAccessException e) {
			Log.ERROR.printf("Error loading game module '%s': %s", gameTypeName, e);;
		} catch (InvocationTargetException e) {
			Log.ERROR.printf("Error loading game module '%s': %s", gameTypeName, e);;
		}
		
		return gameModule;
	}
	
	private void loadJarFiles(File modulePath) {
		for(File jarFile: getJarFiles(modulePath)) {
			try {
				ArrayList<Class<? extends AbstractGameModule>> moduleClassList = loadGameModuleClasses(jarFile);
				loadGameModules(moduleClassList);
			} catch (IOException e) {
				Log.ERROR.printf("Error loading Jar file '%s': %s", jarFile.getAbsolutePath(), e);
			}
		}
	}
	
	private void loadGameModules(ArrayList<Class<? extends AbstractGameModule>> moduleClassList) {
		for(Class<? extends AbstractGameModule> gameModuleClass: moduleClassList) {
			try {
				String gameType = (String) gameModuleClass.getField("GAME_TYPE").get(null);
				
				if(gameType == null || (gameType.trim().equals(""))) {
					continue;
				}
				
				gameModuleMap.put(gameType, gameModuleClass);
			} catch (SecurityException e) {
				;
			} catch (IllegalArgumentException e) {
				;
			} catch (IllegalAccessException e) {
				;
			} catch (NoSuchFieldException e) {
				;
			}
		}
	}
	
	/**
	 * Loads game module classes from a Jar file.
	 * <p>
	 * This method will iterate the Jar file, looking for classes.<br />
	 * When it finds a class it will check if it is a (subclass of) <code>AbstractGameModule</code>, it is not abstract and is public.<br />
	 * If those criteria are met, then the class is added to the list of game module classes to be returned.
	 * @param file The Jar file containing the classes
	 * @return List of game module classes found in this Jar file
	 * @throws IOException If the Jar file is not found or the Jar file cannot be read
	 */
	private ArrayList<Class<? extends AbstractGameModule>> loadGameModuleClasses(File file) throws IOException {
		// Create classloader for loader classes from within Jar file
		URLClassLoader jarClassLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, ClassLoader.getSystemClassLoader());
		JarFile jarFile = new JarFile(file);
		
		// Store found game module classes
		ArrayList<Class<? extends AbstractGameModule>> classList = new ArrayList<Class<? extends AbstractGameModule>>();
		
		// Find classes, iterate through Jar file looking for classes who's superclass is castable to AbstractGameModule and is not abstract.
		for(Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
			JarEntry jarEntry = entries.nextElement();
			
			// If entry is not a class: skip
			if(!jarEntry.getName().endsWith(".class")) {
				continue;
			}
			
			// Get class name: replace '/' with '.' and remove ".class" suffix
			String className = jarEntry.getName().replace('/', '.').replace(".class", "");
			
			try {
				// Load the class from the Jar file
				Class<?> clazz = Class.forName(className, true, jarClassLoader);
				
				try {
					// Try to cast the class to an AbstractGameModule
					Class<? extends AbstractGameModule> gameModuleClass = clazz.asSubclass(AbstractGameModule.class);
					
					// If class is abstract: skip
					if((gameModuleClass.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT) {
						continue;
					}
					
					// If class not is public: skip
					if((gameModuleClass.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
						continue;
					}
					
					// Add class to game module class list
					classList.add(gameModuleClass);
				} catch (ClassCastException e) {
					;
				}
			} catch (ClassNotFoundException e) {
				;
			}
		}
		
		jarFile.close();
		
		return classList;
	}
	
	private ArrayList<File> getJarFiles(File modulePath) {
		ArrayList<File> jarList = new ArrayList<File>();
		
		for(File file: modulePath.listFiles()) {
			String filename = file.getAbsolutePath();
			if(filename.substring(filename.length() - 4).equalsIgnoreCase(".jar")) {
				jarList.add(file);
			}
		}
		
		return jarList;
	}

}
