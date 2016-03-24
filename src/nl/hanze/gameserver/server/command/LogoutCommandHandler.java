package nl.hanze.gameserver.server.command;

import java.util.ArrayList;

import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.message.Command;

public class LogoutCommandHandler extends AbstractCommandHandler {

	public LogoutCommandHandler() {
		super("logout", "exit", "quit", "disconnect", "bye");
	}
	
	@Override
	public void handleCommand(Client client, Command command) {
		client.disconnect();
	}

	@Override
	public String getDesciption() {
		return "Logout and disconnect";
	}

	@Override
	public ArrayList<String> getUsage() {
		ArrayList<String> responseList = new ArrayList<String>();
		
		responseList.add("usage: logout");
		responseList.add("");
		responseList.add("Command aliases: 'exit', 'quit', 'disconnect', 'bye'");
		
		return responseList;
	}

}
