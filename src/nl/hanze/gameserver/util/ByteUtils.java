package nl.hanze.gameserver.util;

import java.nio.ByteBuffer;

public class ByteUtils {
	
	public static String readLine(ByteBuffer dataBuffer) {
		// Skip leading characters
		ByteUtils.skipChars(dataBuffer, new byte[]{'\r', '\n'});
		
		byte[] data = dataBuffer.array();
		int offset = dataBuffer.arrayOffset() + dataBuffer.position();
		
		// Find newline characters
		int crIdx = ByteUtils.indexOf(dataBuffer, (byte)'\r');
		int lfIdx = ByteUtils.indexOf(dataBuffer, (byte)'\n');
		
		// Index of first newline character, -1 if not present
		int idx;
		
		if(crIdx >= offset && lfIdx >= offset) {
			idx = Math.min(crIdx, lfIdx);
		} else {
			if(crIdx >= offset) {
				idx = crIdx;
			} else if(lfIdx >= offset) {
				idx = lfIdx;
			} else {
				idx = -1;
			}
		}
		
		String line = null;
		
		if(idx >= 0) {
			line = new String(data, offset, idx - offset);
			
			// Update buffer position to end of string, at first newline character
			dataBuffer.position(idx);
			
			// Skip trailing newline characters
			ByteUtils.skipChars(dataBuffer, new byte[]{'\r', '\n'});
		}
		
		return line;
	}
	
	public static void skipChars(ByteBuffer buffer, byte[] chars) {
		while(buffer.hasRemaining()) {
			byte b = buffer.get();
			if(contains(chars, b)) {
				continue;
			} else {
				buffer.position(buffer.position() - 1);
				break;
			}
		}
	}
	
	public static boolean contains(ByteBuffer buffer, byte b) {
		return contains(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining(), b);
	}
	
	public static boolean contains(byte[] data, byte b) {
		return contains(data, 0, data.length, b);
	}
	
	public static boolean contains(byte[] data, int offset, int length, byte b) {
		return indexOf(data, offset, length, b) >= 0;
	}
	
	public static int indexOf(ByteBuffer buffer, byte b) {
		return indexOf(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining(), b);
	}
	
	public static int indexOf(byte[] data, byte b) {
		return indexOf(data, 0, data.length, b);
	}
	
	public static int indexOf(byte[] data, int offset, int length, byte b) {
		int idx = -1;
		
		for(int i=offset; i < offset + length; i++) {
			if(data[i] == b) {
				idx = i;
				break;
			}
		}
		
		return idx;
	}
}
