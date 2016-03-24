package nl.hanze.gameserver.server.message;

import java.util.HashMap;

import nl.hanze.gameserver.util.StringUtils;

public class GameResponse extends Response {

	public GameResponse(HashMap<String, String> messageEntries) {
		this(StringUtils.toString(messageEntries));
	}
	
	public GameResponse(String message) {
		super(Response.Status.SVR, String.format("GAME %s", message));
	}

}
