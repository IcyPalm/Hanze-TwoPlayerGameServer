package nl.hanze.gameserver.server.command;

import java.util.ArrayList;

import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.message.Command;

public class CommandHandlerResolver {
	
	private ArrayList<ICommandHandler> commandHandlerList;
	private ICommandHandler defaultHandler;
	
	public CommandHandlerResolver(ICommandHandler defaultHandler) {
		commandHandlerList = new ArrayList<ICommandHandler>();
		this.defaultHandler = defaultHandler;
	}
	
	public void addHandler(ICommandHandler handler) {
		synchronized(commandHandlerList) {
			commandHandlerList.add(handler);
		}
	}
	
	public void removeHandler(ICommandHandler handler) {
		synchronized(commandHandlerList) {
			commandHandlerList.remove(handler);
		}
	}
	
	public ArrayList<ICommandHandler> getCommandHandlers() {
		return commandHandlerList;
	}
	
	@SuppressWarnings("unchecked")
	public ICommandHandler resolveHandler(Client client, Command command) {
		ArrayList<ICommandHandler> commandHandlers;
		synchronized(commandHandlerList) {
			commandHandlers = (ArrayList<ICommandHandler>) commandHandlerList.clone();
		}
		
		ICommandHandler resolvedHandler = null;
		
		for(ICommandHandler handler: commandHandlers) {
			if(handler.isSupported(client, command)) {
				resolvedHandler = handler;
				break;
			}
		}
		
		if(resolvedHandler == null) {
			resolvedHandler = defaultHandler;
		}
		
		return resolvedHandler;
	}
}
