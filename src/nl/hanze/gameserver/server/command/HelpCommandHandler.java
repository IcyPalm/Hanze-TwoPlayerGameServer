package nl.hanze.gameserver.server.command;

import java.util.ArrayList;

import nl.hanze.gameserver.app.Application;
import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.message.Command;
import nl.hanze.gameserver.server.message.ErrorResponse;
import nl.hanze.gameserver.server.message.Response;

public class HelpCommandHandler extends AbstractCommandHandler {

	public HelpCommandHandler() {
		super("help");
	}
	
	@Override
	public void handleCommand(Client client, Command command) {
		if(command.getArgument().equals("")) {
			displayHelp(client);
		} else {
			displayHelp(client, command.getArgument());
		}
	}
	
	private void displayHelp(Client client) {
		ArrayList<String> responseList = new ArrayList<String>();
		
		responseList.add(String.format("Help information for %s [Version %s] commands.", Application.getInstance().getName(), Application.getInstance().getVersion()));
		responseList.add("");
		responseList.add("For more information on a command, type 'help <command>'.");
		responseList.add("");
		responseList.add("Available commands:");
		
		for(ICommandHandler commandHandler : Application.getInstance().getGameServer().getClientInputHandler().getCommandHandlerResolver().getCommandHandlers()) {
			String command = commandHandler.getCommandName();
			String description = commandHandler.getDesciption();
			
			responseList.add(String.format("%-12s : %s", command, description));
		}
		
		writeResponseList(client, responseList);
	}
	
	private void displayHelp(Client client, String command) {
		ICommandHandler commandHandler = getCommandHandler(command);
		
		if(commandHandler == null) {
			client.writeResponse(new ErrorResponse(String.format("Unknown command '%s'.", command)));
			return;
		}
		
		ArrayList<String> responseList = new ArrayList<String>();
		
		responseList.add(String.format("Help information for command '%s'.", command));
		responseList.add("");
		responseList.addAll(commandHandler.getUsage());
		
		writeResponseList(client, responseList);
	}
	
	private ICommandHandler getCommandHandler(String command) {
		ICommandHandler commandHandler = null;
		
		for(ICommandHandler handler : Application.getInstance().getGameServer().getClientInputHandler().getCommandHandlerResolver().getCommandHandlers()) {
			if(handler.getCommandName().equals(command)) {
				commandHandler = handler;
				break;
			}
		}
		
		return commandHandler;
	}
	
	private void writeResponseList(Client client, ArrayList<String> responseList) {
		client.writeResponse(Response.OK);
		
		for(String response : responseList) {
			client.writeResponse(new Response(Response.Status.SVR, "HELP " + response));
		}
	}
	
	@Override
	public String getDesciption() {
		return "Displays this help information";
	}
	
	@Override
	public ArrayList<String> getUsage() {
		ArrayList<String> responseList = new ArrayList<String>();
		
		responseList.add("usage: help [command]");
		responseList.add("");
		responseList.add("Valid options and arguments:");
		responseList.add("  <command>    : Command to display detailed help information for");
		
		return responseList;
	}

}
