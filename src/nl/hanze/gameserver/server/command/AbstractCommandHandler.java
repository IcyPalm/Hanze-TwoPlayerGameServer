package nl.hanze.gameserver.server.command;

import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.message.Command;

public abstract class AbstractCommandHandler implements ICommandHandler {

	String[] supportedActions;
	
	public AbstractCommandHandler(String... actions) {
		supportedActions = actions;
	}
	
	@Override
	public boolean isSupported(Client client, Command command) {
		boolean supported = false;
		
		for(int i=0;i<supportedActions.length;i++) {
			if(command.getAction().equalsIgnoreCase(supportedActions[i])) {
				supported = true;
				break;
			}
		}
		
		return supported;
	}
	
	@Override
	public String getCommandName() {
		return supportedActions[0];
	}
}
