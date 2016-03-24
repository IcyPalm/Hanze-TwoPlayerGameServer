package nl.hanze.gameserver.server.message;

public class Response {

	public static final Response OK = new Response(Status.OK);
	
	public static enum Status {
		OK,
		ERR,
		SVR;
	}
	
	private Status status;
	private String message;
	
	public Response(Status status) {
		this(status, null);
	}
	
	public Response(Status status, String message) {
		this.status = status;
		this.message = message;
	}
	
	@Override
	public String toString() {
		String responseLine = null;
		
		if(message == null || message.equals("")) {
			responseLine = status.toString();
		} else {
			responseLine = String.format("%s %s", status, message);
		}
		
		return responseLine;
	}
}
