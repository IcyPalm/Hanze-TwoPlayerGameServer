package nl.hanze.gameserver.server.command;

import java.util.ArrayList;

import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.Match;
import nl.hanze.gameserver.server.message.Command;
import nl.hanze.gameserver.server.message.ErrorResponse;
import nl.hanze.gameserver.server.message.Response;

public class MoveCommandHandler extends AbstractCommandHandler {

	public MoveCommandHandler() {
		super("move");
	}
	
	@Override
	public void handleCommand(Client client, Command command) {
		String move = command.getArgument();
		
		if(!client.isLoggedIn()) {
			client.writeResponse(new ErrorResponse("Not logged in"));
			return;
		}
		
		if(move.equals("")) {
			client.writeResponse(new ErrorResponse("No move entered"));
			return;
		}
		
		Match match = client.getCurrentMatch();
		
		if(match == null || match.isFinished()) {
			client.writeResponse(new ErrorResponse("Not in any match"));
			return;
		}
		
		client.writeResponse(Response.OK);
		
		try {
			match.doPlayerMove(client, move);
		} catch(IllegalStateException e) {
			e.printStackTrace();
			client.writeResponse(new ErrorResponse(e.toString()));
		}
	}

	@Override
	public String getDesciption() {
		return "Make a game move in current match";
	}

	@Override
	public ArrayList<String> getUsage() {
		ArrayList<String> responseList = new ArrayList<String>();
		
		responseList.add("usage: move <game move>");
		responseList.add("");
		responseList.add("Valid options and arguments:");
		responseList.add("  <game move>    : Game-specific move to make");
		
		return responseList;
	}

}
