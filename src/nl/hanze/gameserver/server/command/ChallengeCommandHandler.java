package nl.hanze.gameserver.server.command;

import java.util.ArrayList;
import java.util.Arrays;

import nl.hanze.gameserver.app.Application;
import nl.hanze.gameserver.server.Challenge;
import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.message.Command;
import nl.hanze.gameserver.server.message.ErrorResponse;
import nl.hanze.gameserver.server.message.Response;

public class ChallengeCommandHandler extends AbstractCommandHandler {

	private static final String ARGUMENT_ACCEPT = "accept";
	
	public ChallengeCommandHandler() {
		super("challenge");
	}
	
	@Override
	public void handleCommand(Client client, Command command) {
		if(!client.isLoggedIn()) {
			client.writeResponse(new ErrorResponse("Not logged in"));
			return;
		}
		
		if(client.getCurrentMatch() != null) {
			client.writeResponse(new ErrorResponse("Currently in match"));
		}
		
		if(Application.getInstance().getGameServer().getClientManager().getTournament() != null) {
			client.writeResponse(new ErrorResponse("Tournament in progress"));
		}
		
		boolean accept = false;
		
		if(command.getArgument().length() >= ARGUMENT_ACCEPT.length()) {
			if(command.getArgument().substring(0, ARGUMENT_ACCEPT.length()).equalsIgnoreCase(ARGUMENT_ACCEPT)) {
				accept = true;
			}
		}
		
		try {
			if(accept) {
				handleAccept(client, command);
			} else {
				handleChallenge(client, command);
			}
		} catch (Exception e) {
			client.writeResponse(new ErrorResponse("Illegal argument(s) for command"));
		}
	}
	
	private void handleAccept(Client client, Command command) throws Exception {
		int challengeNumber = parseAcceptArgument(command.getArgument());
		Challenge challenge = client.getChallenge(challengeNumber);
		
		if(challenge == null) {
			client.writeResponse(new ErrorResponse(String.format("Invalid challenge number '%d'", challengeNumber)));
			return;
		}
		
		// All OK, accept challenge
		client.writeResponse(Response.OK);
		client.getClientManager().acceptChallenge(challenge);
	}
	
	private int parseAcceptArgument(String acceptArgument) throws Exception {
		String challengeNumberString = acceptArgument.split(" ")[1];
		return Integer.parseInt(challengeNumberString);
	}
	
	private void handleChallenge(Client client, Command command) throws Exception {
		String[] playerNameGameType = parseChallengeArgument(command.getArgument());
		String playerName = playerNameGameType[0].trim();
		String gameType = playerNameGameType[1].trim();
		
		if(playerName.trim().equals("") || gameType.equals("")) {
			client.writeResponse(new ErrorResponse("No player name or game name entered"));
			return;
		}
		
		Client player = client.getClientManager().getClientByName(playerName);
		
		if(player == null) {
			client.writeResponse(new ErrorResponse(String.format("Unknown player: '%s'", playerName)));
			return;
		}
		
		if(!Application.getInstance().getGameLoader().getGameTypeList().contains(gameType)) {
			client.writeResponse(new ErrorResponse(String.format("Unknown game: '%s'", gameType)));
			return;
		}
		
		if(player.equals(client)) {
			client.writeResponse(new ErrorResponse("Cannot challenge self"));
		}
		
		// All OK, challenge player
		client.writeResponse(Response.OK);
		client.getClientManager().challenge(client, player, gameType);
	}
	
	private String[] parseChallengeArgument(String argument) throws Exception {
		String[] playerNameGameType = new String[2];
		Arrays.fill(playerNameGameType, "");
		
		String[] arguments = argument.split("\"");
		playerNameGameType[0] = arguments[1];
		playerNameGameType[1] = arguments[3];
		
		return playerNameGameType;
	}

	@Override
	public String getDesciption() {
		return "Handle challenges";
	}

	@Override
	public ArrayList<String> getUsage() {
		ArrayList<String> responseList = new ArrayList<String>();
		
		responseList.add("usage: challenge [accept] [args]");
		responseList.add("");
		responseList.add("Valid options and arguments:");
		responseList.add("  \"<player>\" \"<game>\"          : Challenge player for game");
		responseList.add("  accept <challenge number>    : Accept challenge");
		
		return responseList;
	}

}
