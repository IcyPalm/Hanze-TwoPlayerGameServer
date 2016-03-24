package nl.hanze.gameserver.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class SettingsFile {
	
	private static final String DELIMITER = "=";
	private static final String LINE_SEPERATOR = System.getProperty("line.separator");
	
	private File settingsFile;
	private HashMap<String, String> settingsMap;
	
	public SettingsFile(File settingsFile) {
		this.settingsFile = settingsFile;
		this.settingsMap = new HashMap<String, String>();
	}
	
	public boolean hasValue(String name) {
		return this.getValue(name) == null;
	}
	
	public String getValue(String name) {
		return this.getValue(name, null);
	}
	
	public String getValue(String name, String defaultValue) {
		String value = this.settingsMap.get(name.toLowerCase());
		
		if(value == null) {
			value = defaultValue;
		}
		
		return value;
	}
	
	public void setValue(String name, String value) {
		this.settingsMap.put(name.toLowerCase(), value);
	}
	
	public int getIntValue(String name, int defaultValue) {
		String intStringValue = getValue(name, Integer.toString(defaultValue));
		
		int value;
		
		try {
			value = Integer.parseInt(intStringValue);
		} catch(NumberFormatException e) {
			value = defaultValue;
		}
		
		return value;
	}
	
	public void setIntValue(String name, int value) {
		setValue(name, Integer.toString(value));
	}
	
	public void load() {
		try {
			BufferedReader infile = new BufferedReader(new FileReader(this.settingsFile));
			
			String line = null;
			while((line = infile.readLine()) != null) {
				String[] setting = line.split(DELIMITER, 2);
				this.setValue(setting[0].trim().toLowerCase(), setting[1].trim());
			}
			
			infile.close();
		} catch(IOException e) {
			Log.ERROR.printf("Error while reading settings file: %s", e);
			e.printStackTrace();
		}
	}
	
	public void save() {
		try {
			BufferedWriter outfile = new BufferedWriter(new FileWriter(this.settingsFile));
			String format = "%s%s%s%s";
			
			for(Entry<String, String> setting: this.settingsMap.entrySet()) {
				String name = setting.getKey();
				String value = setting.getValue();
				
				outfile.write(String.format(format, name, DELIMITER, value, LINE_SEPERATOR));
			}
			
			outfile.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
