package nl.hanze.gameserver.util;


public class KeyValuePair<K, V> {
	
	private K key;
	private V value;
	
	public KeyValuePair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean result = obj == this;
		
		if(!result && obj != null) {
			KeyValuePair<?, ?> other = (KeyValuePair<?, ?>) obj;
			result = other.key == key && other.value == value;
		}
		
		return result;
	}
	
	@Override
	public int hashCode() {
		return key.hashCode() + value.hashCode();
	}
}
