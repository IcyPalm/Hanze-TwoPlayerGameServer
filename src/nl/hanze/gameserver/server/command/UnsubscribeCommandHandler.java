package nl.hanze.gameserver.server.command;

import java.util.ArrayList;

import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.message.Command;
import nl.hanze.gameserver.server.message.ErrorResponse;
import nl.hanze.gameserver.server.message.Response;

/**
 * Creates an handler to handle the unsubscribe command
 * @author Rutger Roffel
 * @version 0.1
 * @since 30-03-2016
 */
public class UnsubscribeCommandHandler extends AbstractCommandHandler {

    /**
     * This constructor calls the parent class
     */
    public UnsubscribeCommandHandler() {
        super("unsubscribe");
    }

    /**
     * Checks whether the user is logged in and removes
     * the subscription
     * @param client
     * @param command
     */
    @Override
    public void handleCommand(Client client, Command command) {
        if(!client.isLoggedIn()) {
            client.writeResponse(new ErrorResponse("Not logged in"));
            return;
        }
        client.getClientManager().unsubscribe(client);
        client.writeResponse(Response.OK);
    }

    /**
     * returns a String with the description of this command.
     * @return
     */
    @Override
    public String getDesciption() {
        return "Unsubscribe from any game";
    }

    /**
     * Returns an ArrayList with the usage of this
     * command.
     * @return
     */
    @Override
    public ArrayList<String> getUsage() {
        ArrayList<String> responseList = new ArrayList<>();
        responseList.add("usage: unsubscribe");
        return responseList;
    }
}
