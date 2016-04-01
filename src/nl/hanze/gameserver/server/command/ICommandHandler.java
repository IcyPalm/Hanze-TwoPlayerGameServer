package nl.hanze.gameserver.server.command;

import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.message.Command;

import java.util.ArrayList;

public interface ICommandHandler {

	public boolean isSupported(Client client, Command command);

	public void handleCommand(Client client, Command command);

	public String getCommandName();

	public String getDescription();

	public ArrayList<String> getUsage();

}
