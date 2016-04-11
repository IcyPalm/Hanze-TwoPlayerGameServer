package nl.hanze.gameserver.server.command;

import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.message.Command;
import nl.hanze.gameserver.server.message.ErrorResponse;
import nl.hanze.gameserver.server.message.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class creates a simple command handler to send small messages to
 * other players. The command can be used as:
 * (message || msg) playername Do you also like cats?!
 *
 * The max message size is 140 chars (trimmed). This limit is set to prevent overflowing.
 * the server.
 *
 * @author Rutger Roffel
 * @version 0.1
 * @since 06-04-2016
 */
public class MessageCommandHandler extends AbstractCommandHandler {

	public MessageCommandHandler() {
		super("message", "msg");
	}

	@Override
	public void handleCommand(Client client, Command command) {
		if (!client.isLoggedIn()) {
			client.writeResponse(new ErrorResponse("Not logged in"));
			System.out.println(command.getArgument());
			return;
		}
		try {
			handleSay(client, command);
		} catch (Exception e) {
			client.writeResponse(new ErrorResponse(e.toString()));
		}
	}

	/**
	 * This method receives the command, sends is to a parser and sends
	 * the message to the other client.
	 * There are some checks involved (size, valid player etc.)
	 * @param client
	 * @param command
	 * @throws Exception
	 */
	private void handleSay(Client client, Command command) throws Exception {
		String[] playerNameText = parseMessageArgument(command.getArgument());
		String playerName = playerNameText[0];
		String message = playerNameText[1];
		Client player = client.getClientManager().getClientByName(playerName);
		if (message == null || message.length() == 0) {
			client.writeResponse(new ErrorResponse("The message should not be empty"));
			return;
		}
		else if (message.length() > 140) {
			client.writeResponse(new ErrorResponse("Messages should not contain more than 140 characters"));
			return;
		}
		if (player == null) {
			client.writeResponse(new ErrorResponse(String.format("Unknown player: '%s'", playerName)));
			return;
		} else if(client.getPlayerName().equals(player.getPlayerName())){
			client.writeResponse(new ErrorResponse(String.format("It is pointless to send a message to yourself %s", player.getPlayerName())));
			return;
		}

		// All OK, send to the player
		client.writeResponse(Response.OK);
		client.getClientManager().message(client, player, message);
	}

	/**
	 * This method creates an array with the parsed arguments
	 * The first entry is the username and the second is the message
	 * @param argument
	 * @return
	 * @throws Exception
	 */
	private String[] parseMessageArgument(String argument) throws Exception {
		String[] parsed = new String[2];
		List<String> arguments = Stream.of(argument.split("\"")).collect(Collectors.toList());
		if(!arguments.get(0).equals("")){
			return parsed;
		}
		String username = arguments.get(1);
		username = username.replace("\"", "");
		StringBuilder message = new StringBuilder();
		for (int i = 2; i < arguments.size(); i++) {
			message.append(arguments.get(i));
			message.append(" ");
		}

		parsed[0] = username.trim();
		parsed[1] = message.toString().trim();

		return parsed;
	}

	@Override
	public String getDescription() {
		return "Say things to others";
	}

	@Override
	public ArrayList<String> getUsage() {
		ArrayList<String> responseList = new ArrayList<>();
		responseList.add("usage: message [username] [message]");
		responseList.add("send a message to another player");
		return responseList;
	}
}
