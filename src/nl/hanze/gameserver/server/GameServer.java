package nl.hanze.gameserver.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import nl.hanze.gameserver.app.Application;
import nl.hanze.gameserver.server.command.ChallengeCommandHandler;
import nl.hanze.gameserver.server.command.CommandHandlerResolver;
import nl.hanze.gameserver.server.command.ForfeitCommandHandler;
import nl.hanze.gameserver.server.command.GetCommandHandler;
import nl.hanze.gameserver.server.command.HelpCommandHandler;
import nl.hanze.gameserver.server.command.LoginCommandHandler;
import nl.hanze.gameserver.server.command.LogoutCommandHandler;
import nl.hanze.gameserver.server.command.MoveCommandHandler;
import nl.hanze.gameserver.server.command.SubscribeCommandHandler;
import nl.hanze.gameserver.server.command.UnsupportedCommandHandler;
import nl.hanze.gameserver.util.Log;
import nl.hanze.gameserver.util.ReadWriteBuffer;

public class GameServer implements Runnable {
	
	private static final int BUFFER_SIZE = 4 * 1024;
	
	// Selector thread
	private Thread thread;
	// Value controlling whether thread should continue to run
	private boolean running;
	
	private ServerSocketChannel serverSocketChannel;
	private Selector selector;
	private Object opsChangeLock;
	
	// Handles client input data
	private ClientInputHandler clientInputHandler;
	
	// Handles clients
	private ClientManager clientManager;
	
	public GameServer() throws IOException {
		CommandHandlerResolver commandHandlerResolver = new CommandHandlerResolver(new UnsupportedCommandHandler());
		commandHandlerResolver.addHandler(new LoginCommandHandler());
		commandHandlerResolver.addHandler(new LogoutCommandHandler());
		commandHandlerResolver.addHandler(new GetCommandHandler());
		commandHandlerResolver.addHandler(new SubscribeCommandHandler());
		commandHandlerResolver.addHandler(new MoveCommandHandler());
		commandHandlerResolver.addHandler(new ForfeitCommandHandler());
		commandHandlerResolver.addHandler(new ChallengeCommandHandler());
		commandHandlerResolver.addHandler(new HelpCommandHandler());
		
		clientInputHandler = new ClientInputHandler(commandHandlerResolver);
		
		clientManager = new ClientManager();
		
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		
		int port = Application.getInstance().getSettings().getListenerPort();
		serverSocketChannel.socket().bind(new InetSocketAddress(port));
		
		Log.DEBUG.printf("Server listening on port %d", port);
		
		selector = Selector.open();
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		opsChangeLock = new Object();
		
		thread = new Thread(this, "GameServer-Thread");
		running = true;
		thread.start();
	}
	
	@Override
	public void run() {
		while(running) {
			try {
				selector.select();
				
				synchronized(opsChangeLock) {}
			} catch (IOException e) {
				e.printStackTrace();
				
				running = false;
				continue;
			}
			
			if(!selector.isOpen()) {
				running = false;
				continue;
			}
			
			Set<SelectionKey> selectionKeys = selector.selectedKeys();
			Iterator<SelectionKey> it = selectionKeys.iterator();
			
			while(it.hasNext()) {
				SelectionKey key = it.next();
				it.remove();
				
				if(!key.isValid()) {
					Log.DEBUG.println("Key is invalid");
					disconnect(key);
					continue;
				}
				
				try {
					if(key.isAcceptable()) {
						handleAccept(key);
					} else {
						
						if(key.isReadable()) {
							handleRead(key);
						}
						
						if(!key.isValid()) {
							continue;
						}
						
						if(key.isWritable()) {
							handleWrite(key);
						}
					}
				} catch (IOException e) {
					Log.ERROR.printf("IOException while processing key: %s", e);
					disconnect(key);
				} catch (Exception e) {
					Log.ERROR.printf("Exception while processing key: %s", e);
					disconnect(key);
				}
			}
		}
	}
	
	private void handleAccept(SelectionKey key) throws IOException {
		SocketChannel channel = serverSocketChannel.accept();
		channel.configureBlocking(false);
		
		ReadWriteBuffer rwBuffer = new ReadWriteBuffer(ByteBuffer.allocateDirect(BUFFER_SIZE), ByteBuffer.allocateDirect(BUFFER_SIZE));
		Client client = new Client(channel, rwBuffer, clientManager);
		
		channel.register(selector, SelectionKey.OP_READ, client);
		
		client.writeLine(String.format("%s [Version %s]", Application.getInstance().getName(), Application.getInstance().getVersion()));
		client.writeLine("(C) Copyright 2009 Hanze Hogeschool Groningen");
	}
	
	private void handleRead(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		Client client = (Client) key.attachment();
		ReadWriteBuffer buffers = client.getBuffers();
		ByteBuffer readBuffer = buffers.getReadBuffer();
		
		int readSize;
		readSize = channel.read(readBuffer);
		
		if(readSize < 0) {
			Log.DEBUG.println("Read -1 bytes, disconnecting client");
			disconnect(key);
			return;
		}
		
		readBuffer.flip();
		byte[] data = new byte[readBuffer.remaining()];
		readBuffer.get(data);
		readBuffer.clear();
		
		clientInputHandler.addData(client, data);
	}
	
	private void handleWrite(SelectionKey key) throws IOException {
		key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
		
		SocketChannel client = (SocketChannel) key.channel();
		ByteBuffer writeBuffer = ((Client) key.attachment()).getWriteBuffer();
		
		synchronized(writeBuffer) {
			writeBuffer.flip();
			client.write(writeBuffer);
			
			if(writeBuffer.hasRemaining()) {
				key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
			}
			
			writeBuffer.compact();
		}
	}
	
	private void disconnect(SelectionKey key) {
		Log.DEBUG.println("Disconnecting client");
		
		SocketChannel client = (SocketChannel) key.channel();
		try {
			if(client != null) {
				clientInputHandler.addData((Client) key.attachment(), null);
				
				Log.DEBUG.println("Closing client socket connection");
				client.close();
			}
		} catch (IOException e) {
			;
		} catch (Exception e) {
			;
		}
		
		key.cancel();
		key.attach(null);
	}
	
	public void disconnect(SocketChannel client) {
		SelectionKey key = client.keyFor(selector);
		
		synchronized(opsChangeLock) {
			selector.wakeup();
			/*try {
				client.close();
			} catch (IOException e) {
				;
			}*/
			disconnect(key);
		}
	}
	
	public void setWritable(SocketChannel client) {
		SelectionKey key = client.keyFor(selector);
		
		synchronized(opsChangeLock) {
			selector.wakeup();
			key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
		}
	}
	
	public void exit() {
		running = false;
		
		try {
			for(SelectionKey key: selector.keys()) {
				try {
					if(key.channel() != null && key.attachment() != null) {
						disconnect(key);
					}
				} catch (Exception e) {
					;
				}
			}
		} catch (Exception e) {
			;
		}
		
		try {
			Log.DEBUG.println("Closing client input handler");
			clientInputHandler.exit();
		} catch (Exception e) {
			;
		}
		
		try {
			Log.DEBUG.println("Closing server selector");
			selector.close();
		} catch (IOException e) {
			;
		}
		
		try {
			Log.DEBUG.println("Closing server socket");
			serverSocketChannel.close();
		} catch (IOException e) {
			;
		}
	}
	
	public ClientManager getClientManager() {
		return clientManager;
	}
	
	public ClientInputHandler getClientInputHandler() {
		return clientInputHandler;
	}
	
	public void delayedCommand(Client client, String command) {
		clientInputHandler.addData(client, command.getBytes());
	}
}
