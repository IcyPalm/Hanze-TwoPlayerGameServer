package nl.hanze.gameserver.server.command;

import java.util.ArrayList;

import nl.hanze.gameserver.app.Application;
import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.message.Command;
import nl.hanze.gameserver.server.message.ErrorResponse;
import nl.hanze.gameserver.server.message.Response;
import nl.hanze.gameserver.util.StringUtils;

public class GetCommandHandler extends AbstractCommandHandler {

	public GetCommandHandler() {
		super("get");
	}
	
	@Override
	public void handleCommand(Client client, Command command) {
		if(command.getArgument().equalsIgnoreCase("gamelist")) {
			client.writeResponse(Response.OK);
			client.writeResponse(new Response(Response.Status.SVR, String.format("GAMELIST %s", getGameListString())));
		} else if(command.getArgument().equals("playerlist")) {
			client.writeResponse(Response.OK);
			client.writeResponse(new Response(Response.Status.SVR, String.format("PLAYERLIST %s", getPlayerListString())));
		} else {
			client.writeResponse(new ErrorResponse(String.format("Unknown GET argument: '%s'", command.getArgument())));
		}
	}
	
	public String getPlayerListString() {
		ArrayList<Client> playerList = Application.getInstance().getGameServer().getClientManager().getPlayerList();
		ArrayList<String> playerNameList = new ArrayList<String>(playerList.size());
		for(Client client : playerList) {
			playerNameList.add(client.getPlayerName());
		}
		
		return StringUtils.toString(playerNameList);
	}
	
	private String getGameListString() {
		return StringUtils.toString(Application.getInstance().getGameLoader().getGameTypeList());
	}

	@Override
	public String getDesciption() {
		return "Get server data";
	}

	@Override
	public ArrayList<String> getUsage() {
		ArrayList<String> responseList = new ArrayList<String>();
		
		responseList.add("usage: get <gamelist | playerlist>");
		responseList.add("");
		responseList.add("Valid options and arguments:");
		responseList.add("  <gamelist>      : Available games");
		responseList.add("  <playerlist>    : Currently logged in players");
		
		return responseList;
	}

}
