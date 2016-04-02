package nl.hanze.gameserver.server.command;

import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.message.Command;
import nl.hanze.gameserver.server.message.Response;

import java.util.ArrayList;

public class UnsupportedCommandHandler implements ICommandHandler {

	@Override
	public boolean isSupported(Client client, Command command) {
		return true;
	}

	@Override
	public void handleCommand(Client client, Command command) {
		Response response = new Response(Response.Status.ERR, String.format("Unsupported command: '%s'", command.getAction()));
		client.writeResponse(response);
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public ArrayList<String> getUsage() {
		return null;
	}

	@Override
	public String getCommandName() {
		return null;
	}

}
