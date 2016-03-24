package nl.hanze.gameserver.server;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import nl.hanze.gameserver.app.Application;
import nl.hanze.gameserver.server.message.Response;
import nl.hanze.gameserver.util.ReadWriteBuffer;

public class Client {
	
	private SocketChannel channel;
	private ReadWriteBuffer buffers;
	private ClientManager clientManager;
	
	private String hostAddress;
	
	private boolean loggedIn;
	private String playerName;
	private String subscribedGameType;
	private Match currentMatch;
	private Challenge challenge;
	private ArrayList<Challenge> challenges;
	
	public Client(SocketChannel channel, ReadWriteBuffer buffers, ClientManager clientManager) {
		this.channel = channel;
		this.buffers = buffers;
		this.clientManager = clientManager;
		
		clientManager.addClient(this);
		
		loggedIn = false;
		subscribedGameType = null;
		currentMatch = null;
		challenge = null;
		challenges = new ArrayList<Challenge>();
		
		hostAddress = channel.socket().getInetAddress().getHostAddress();
		playerName = null;
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}
	
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	
	public String getSubscribedGameType() {
		return subscribedGameType;
	}
	
	public void setSubscribedGameType(String gameType) {
		subscribedGameType = gameType;
	}
	
	public Match getCurrentMatch() {
		return currentMatch;
	}
	
	public void setCurrentMatch(Match match) {
		currentMatch = match;
	}
	
	public Challenge getChallenge() {
		return challenge;
	}
	
	public void setChallenge(Challenge challenge) {
		this.challenge = challenge;
	}
	
	public ArrayList<Challenge> getChallenges() {
		return challenges;
	}
	
	public void addChallenge(Challenge challenge) {
		challenges.add(challenge);
	}
	
	public void removeChallenge(Challenge challenge) {
		challenges.remove(challenge);
	}
	
	public Challenge getChallenge(int challengeNumber) {
		Challenge challenge = null;
		
		for(Challenge c : challenges) {
			if(c.getChallengeNumber() == challengeNumber) {
				challenge = c;
				break;
			}
		}
		
		return challenge;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public SocketChannel getChannel() {
		return channel;
	}
	
	public ClientManager getClientManager() {
		return clientManager;
	}
	
	public void disconnect() {
		Application.getInstance().getGameServer().disconnect(channel);
	}
	
	public void writeResponse(Response response) {
		writeLine(response.toString());
	}
	
	public void writeLine(String line) {
		if(line != null) {
			line += "\r\n";
			
			synchronized(buffers.getWriteBuffer()) {
				buffers.getWriteBuffer().put(line.getBytes());
			}
			try {
				Application.getInstance().getGameServer().setWritable(channel);
			} catch (Exception e) {
				;
			}
		}
	}
	
	public ByteBuffer getReadBuffer() {
		return buffers.getReadBuffer();
	}
	
	public ByteBuffer getWriteBuffer() {
		return buffers.getWriteBuffer();
	}
	
	public ReadWriteBuffer getBuffers() {
		return buffers;
	}
	
	@Override
	public String toString() {
		String repr = null;
		
		if(loggedIn) {
			repr = String.format("%s@%s", playerName, hostAddress);
		} else {
			repr = hostAddress;
		}
		
		return repr;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean result = obj == this;
		
		if(!result && obj != null) {
			Client other = (Client) obj;
			result = other.channel == channel;
		}
		
		return result;
	}
	
	@Override
	public int hashCode() {
		return channel.hashCode();
	}
}
