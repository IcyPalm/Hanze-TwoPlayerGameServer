package nl.hanze.gameserver.server.command;

import java.util.ArrayList;

import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.message.Command;

public interface ICommandHandler {
	
	public boolean isSupported(Client client, Command command);
	
	public void handleCommand(Client client, Command command);
	
	public String getCommandName();
	
	public String getDesciption();
	
	public ArrayList<String> getUsage();

}
