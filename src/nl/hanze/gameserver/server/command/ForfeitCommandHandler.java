package nl.hanze.gameserver.server.command;

import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.Match;
import nl.hanze.gameserver.server.message.Command;
import nl.hanze.gameserver.server.message.ErrorResponse;
import nl.hanze.gameserver.server.message.Response;

import java.util.ArrayList;

public class ForfeitCommandHandler extends AbstractCommandHandler {

	public ForfeitCommandHandler() {
		super("forfeit");
	}

	@Override
	public void handleCommand(Client client, Command command) {
		Match match = client.getCurrentMatch();

		if(match == null || match.isFinished()) {
			client.writeResponse(new ErrorResponse("Not in any match"));
		} else {
			client.writeResponse(Response.OK);
			match.forfeit(client);
		}
	}

	@Override
	public String getDescription() {
		return "Forfeit current match";
	}

	@Override
	public ArrayList<String> getUsage() {
		ArrayList<String> responseList = new ArrayList<String>();
		responseList.add("usage: forfeit");
		return responseList;
	}

}
