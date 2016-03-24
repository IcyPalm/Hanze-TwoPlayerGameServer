package nl.hanze.gameserver.server.message;

public class ErrorResponse extends Response {
	public ErrorResponse(String message) {
		super(Response.Status.ERR, message);
	}
}
