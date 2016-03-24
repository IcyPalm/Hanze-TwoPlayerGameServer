package nl.hanze.gameserver.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StringUtils {

	public static String toStringAsMap(Object... items) {
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		
		for(int i=1;i<items.length;i+=2) {
			map.put(items[i-1], items[i]);
		}
		
		return toString(map);
	}
	
	public static String toStringAsList(Object... items) {
		return toString(Arrays.asList(items));
	}
	
	public static String toString(Collection<?> itemList) {
		return printf("[%s]", join(itemList, ", ", "\"%s\""));
	}
	
	public static String toString(Map<?, ?> itemMap) {
		ArrayList<String> entryStrings = new ArrayList<String>();
		
		for(Map.Entry<?, ?> entry: itemMap.entrySet()) {
			String entryString = printf("%s: \"%s\"", entry.getKey(), entry.getValue());
			entryStrings.add(entryString);
		}
		
		return printf("{%s}", join(entryStrings, ", "));
	}
	
	public static String join(Collection<?> stringList, String seperator) {
		return join(stringList, seperator, "%s");
	}
	
	public static String join(Collection<?> stringList, String seperator, String itemFormat) {
		StringBuffer buffer = new StringBuffer();
		
		Iterator<?> it = stringList.iterator();
		
		for(int i=0; it.hasNext(); i++) {
			if(i > 0) {
				buffer.append(seperator);
			}
			
			buffer.append(printf(itemFormat, it.next().toString()));
		}
		
		return buffer.toString();
	}
	
	public static String printf(String format, Object... args) {
		return String.format(format, args);
	}
}
