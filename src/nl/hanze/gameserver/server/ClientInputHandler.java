package nl.hanze.gameserver.server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import nl.hanze.gameserver.app.Application;
import nl.hanze.gameserver.server.command.CommandHandlerResolver;
import nl.hanze.gameserver.server.command.ICommandHandler;
import nl.hanze.gameserver.server.message.Command;
import nl.hanze.gameserver.util.ByteUtils;
import nl.hanze.gameserver.util.KeyValuePair;
import nl.hanze.gameserver.util.Log;

public class ClientInputHandler implements Runnable {
	
	/**
	 * Size of the input buffer per client
	 */
	private static final int INPUT_BUFFER_SIZE = 16 * 1024;
	
	// Queue of data read by the server
	private LinkedBlockingQueue<KeyValuePair<Client, byte[]>> readyQueue;
	// Map containing the client's input buffers
	private HashMap<Client, ByteBuffer> clientInputBufferMap;
	
	// Resolves a CommandHandler based on a Command
	private CommandHandlerResolver commandHandlerResolver;
	
	// Thread processing the client input and commands
	private Thread thread;
	// Value controlling whether thread should continue to run
	private boolean running;
	
	public ClientInputHandler(CommandHandlerResolver commandHandlerResolver) {
		readyQueue = new LinkedBlockingQueue<KeyValuePair<Client, byte[]>>();
		clientInputBufferMap = new HashMap<Client, ByteBuffer>();
		
		this.commandHandlerResolver = commandHandlerResolver;
		
		thread = new Thread(this, "ClientInputHandler-Thread");
		running = true;
		thread.start();
	}

	@Override
	public void run() {
		while(running) {
			try {
				// Wait for client input
				KeyValuePair<Client, byte[]> clientDataPair = readyQueue.take();
				
				Client client = clientDataPair.getKey();
				byte[] data = clientDataPair.getValue();
				
				// null data indicates disconnected client
				if(data == null) {
					Log.DEBUG.println("Client disconnected, removing input buffer");
					clientInputBufferMap.remove(client);
					Application.getInstance().getGameServer().getClientManager().removeClient(client);
					continue;
				}
				
				// Get client input buffer and add data
				ByteBuffer inputBuffer = getInputBuffer(client);
				inputBuffer.put(data);
				inputBuffer.flip();
				
				// Read and remove commands from client input buffer
				ArrayList<Command> commands = readCommands(inputBuffer);
				inputBuffer.compact();
				
				// Handle all commands from this client
				for(Command command: commands) {
					ICommandHandler commandHandler = commandHandlerResolver.resolveHandler(client, command);
					
					if(commandHandler == null) {
						continue;
					}
					
					try {
						Log.DEBUG.printf("Handling command: action=%s, argument=%s", command.getAction(), command.getArgument());
						commandHandler.handleCommand(client, command);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			} catch (InterruptedException e) {
				;
			}
		}
	}
	
	private ArrayList<Command> readCommands(ByteBuffer buffer) {
		ArrayList<Command> commandList = new ArrayList<Command>();
		
		String line;
		while((line = ByteUtils.readLine(buffer)) != null) {
			commandList.add(new Command(line));
		}
		
		return commandList;
	}
	
	private ByteBuffer getInputBuffer(Client client) {
		ByteBuffer inputBuffer = clientInputBufferMap.get(client);
		
		if(inputBuffer == null) {
			inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
			clientInputBufferMap.put(client, inputBuffer);
		}
		
		return inputBuffer;
	}
	
	public void exit() {
		running = false;
		thread.interrupt();
	}
	
	public void addData(Client client, byte[] data) {
		readyQueue.add(new KeyValuePair<Client, byte[]>(client, data));
	}
	
	public CommandHandlerResolver getCommandHandlerResolver() {
		return commandHandlerResolver;
	}
	
}
