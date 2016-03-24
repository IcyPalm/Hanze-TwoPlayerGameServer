package nl.hanze.gameserver.server.command;

import java.util.ArrayList;

import nl.hanze.gameserver.app.Application;
import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.message.Command;
import nl.hanze.gameserver.server.message.ErrorResponse;
import nl.hanze.gameserver.server.message.Response;

public class LoginCommandHandler extends AbstractCommandHandler {

	public LoginCommandHandler() {
		super("login");
	}
	
	@Override
	public void handleCommand(Client client, Command command) {
		String argument = command.getArgument();
		
		Response response = null;
		
		if(argument.equals("")) {
			response = new ErrorResponse("No name entered");
		} else if(Application.getInstance().getGameServer().getClientManager().getTournament() != null) {
			response = new ErrorResponse("Tournament in progress, login disabled");
		} else {
			if(client.isLoggedIn()) {
				response = new ErrorResponse("Already logged in");
			} else {
				if(Application.getInstance().getGameServer().getClientManager().login(client, argument)) {
					response = Response.OK;
				} else {
					response = new ErrorResponse("Duplicate name exists");
				}
			}
		}
		
		client.writeResponse(response);
	}

	@Override
	public String getDesciption() {
		return "Login as player";
	}

	@Override
	public ArrayList<String> getUsage() {
		ArrayList<String> responseList = new ArrayList<String>();
		
		responseList.add("usage: login <player name>");
		responseList.add("");
		responseList.add("Valid options and arguments:");
		responseList.add("  <player name>    : Name to login with");
		
		return responseList;
	}

}
