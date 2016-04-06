package nl.hanze.gameserver.server.command;

import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.message.Command;
import nl.hanze.gameserver.server.message.ErrorResponse;
import nl.hanze.gameserver.server.message.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private void handleSay(Client client, Command command) throws Exception {
        List<String> playerNameText = parseMessageArgument(command.getArgument());
        String playerName = playerNameText.get(0);
        Client player = client.getClientManager().getClientByName(playerName);

        if (player == null) {
            client.writeResponse(new ErrorResponse(String.format("Unknown player: '%s'", player.getPlayerName())));
            return;
        } else if(client.getPlayerName().equals(player.getPlayerName())){
            client.writeResponse(new ErrorResponse(String.format("It is pointless to send a message to yourself %s", player.getPlayerName())));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < playerNameText.size(); i++) {
            sb.append(playerNameText.get(i));
            sb.append(" ");
        }

        String message = sb.toString().trim();
        if (message.length() == 0 || message.length() > 140) {
            client.writeResponse(new ErrorResponse("The message length is not according to the requirements"));
            return;
        }

        // All OK, send to the player
        client.writeResponse(Response.OK);
        client.getClientManager().message(client, player, message);
    }

    private List<String> parseMessageArgument(String argument) throws Exception {
        return Stream.of(argument.split(" ")).collect(Collectors.toList());
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
