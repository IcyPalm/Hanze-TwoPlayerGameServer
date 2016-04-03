package nl.hanze.gameserver.server.message;

public class Command {
	
	private String action;
	private String argument;
	
	public Command(String command) {
		String[] cmdParts = command.split(" ", 2);
		
		action = cmdParts[0].trim();
		if(cmdParts.length > 1) {
			argument = cmdParts[1].trim();
		} else {
			argument = "";
		}
	}
	
	public String getAction() {
		return action;
	}
	
	public String getArgument() {
		return argument;
	}
	
	@Override
	public String toString() {
		String command = null;
		
		if(!argument.equals("")) {
			command = String.format("%s %s", action, argument);
		} else {
			command = action;
		}
		
		return command;
	}
}
