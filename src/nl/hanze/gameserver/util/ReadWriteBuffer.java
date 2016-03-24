package nl.hanze.gameserver.util;

import java.nio.ByteBuffer;

public class ReadWriteBuffer {
	
	private ByteBuffer readBuffer;
	private ByteBuffer writeBuffer;
	
	public ReadWriteBuffer(ByteBuffer readBuffer, ByteBuffer writeBuffer) {
		this.readBuffer = readBuffer;
		this.writeBuffer = writeBuffer;
	}
	
	public ByteBuffer getReadBuffer() {
		return this.readBuffer;
	}
	
	public ByteBuffer getWriteBuffer() {
		return this.writeBuffer;
	}
}
