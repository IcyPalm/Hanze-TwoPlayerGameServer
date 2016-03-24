package nl.hanze.gameserver.server.command;

import java.util.ArrayList;

import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.message.Command;
import nl.hanze.gameserver.server.message.ErrorResponse;
import nl.hanze.gameserver.server.message.Response;

public class SubscribeCommandHandler extends AbstractCommandHandler {

	public SubscribeCommandHandler() {
		super("subscribe");
	}
	
	@Override
	public void handleCommand(Client client, Command command) {
		String gameType = command.getArgument();
		
		if(!client.isLoggedIn()) {
			client.writeResponse(new ErrorResponse("Not logged in"));
			return;
		}
		
		if(gameType.equals("")) {
			client.writeResponse(new ErrorResponse("No game name entered"));
			return;
		}
		
		boolean subscribed = client.getClientManager().subscribe(client, gameType);
		
		if(!subscribed) {
			client.writeResponse(new ErrorResponse(String.format("Unknown game: '%s'", gameType)));
			return;
		}
		
		client.writeResponse(Response.OK);
		
		client.getClientManager().findMatch(client);
	}

	@Override
	public String getDesciption() {
		return "Subscribe for a game";
	}

	@Override
	public ArrayList<String> getUsage() {
		ArrayList<String> responseList = new ArrayList<String>();
		
		responseList.add("usage: subscribe <game>");
		responseList.add("");
		responseList.add("Valid options and arguments:");
		responseList.add("  <game>    : Game name to subscribe to");
		
		return responseList;
	}

}
