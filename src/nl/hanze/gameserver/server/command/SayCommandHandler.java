package nl.hanze.gameserver.server.command;

import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.message.Command;
import nl.hanze.gameserver.server.message.ErrorResponse;
import nl.hanze.gameserver.server.message.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SayCommandHandler extends AbstractCommandHandler {


    public SayCommandHandler() {
        super("say");
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
        List<String> playerNameText = parseChallengeArgument(command.getArgument());
        String playerName = playerNameText.get(0);
        Client player = client.getClientManager().getClientByName(playerName);

        if (player == null) {
            client.writeResponse(new ErrorResponse(String.format("Unknown player: '%s'", playerName)));
            return;
        }
        else if(client.getPlayerName().equals(player)){
            client.writeResponse(new ErrorResponse(String.format("It is pointless to send a message to yourself, '%s'", playerName)));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < playerNameText.size(); i++) {
            sb.append(playerNameText.get(i));
            sb.append(" ");
        }

        String chatText = sb.toString().trim();
        if (chatText.length() == 0) {
            client.writeResponse(new ErrorResponse("Empty message"));
            return;
        }

        // All OK, send to the player
        client.writeResponse(Response.OK);
        client.getClientManager().say(client, player, chatText);
    }

    private List<String> parseChallengeArgument(String argument) throws Exception {
        return Stream.of(argument.split(" ")).collect(Collectors.toList());
    }

    @Override
    public String getDescription() {
        return "Say things to others";
    }

    @Override
    public ArrayList<String> getUsage() {
        ArrayList<String> responseList = new ArrayList<>();
        responseList.add("usage: say [username] [message]");
        responseList.add("send a message to another player");
        return responseList;
    }
}
