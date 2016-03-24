package nl.hanze.gameserver.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import nl.hanze.gameserver.app.Application;
import nl.hanze.gameserver.server.message.GameResponse;
import nl.hanze.gameserver.util.Log;
import nl.hanze.gameserver.util.StringUtils;

public class ClientManager {
	
	public static final int ACTION_CONNECTED = 0;
	public static final int ACTION_LOGIN = 1;
	public static final int ACTION_LOGOUT = 2;
	public static final int ACTION_SUBSCRIBE = 3;
	public static final int ACTION_MATCH = 4;
	
	private ArrayList<ActionListener> listenerList;
	
	private ArrayList<Client> clientList;
	private Tournament tournament;
	
	public ClientManager() {
		clientList = new ArrayList<Client>();
		listenerList = new ArrayList<ActionListener>();
		tournament = null;
	}
	
	public ArrayList<Client> getClientList() {
		return clientList;
	}
	
	public ArrayList<Client> getPlayerList() {
		ArrayList<Client> playerList = new ArrayList<Client>(clientList.size());
		for(Client client : clientList) {
			if(client.isLoggedIn()) {
				playerList.add(client);
			}
		}
		
		return playerList;
	}
	
	public void addClient(Client client) {
		synchronized(clientList) {
			clientList.add(client);
		}
		
		notifyListeners(client, ACTION_CONNECTED);
	}
	
	public void removeClient(Client client) {
		if(Application.getInstance().isClosing()) {
			return;
		}
		
		// Cancel player-initiated challenge
		cancelChallenge(client.getChallenge());
		
		client.setLoggedIn(false);
		
		notifyListeners(client, ACTION_LOGOUT);
		
		synchronized(clientList) {
			clientList.remove(client);
		}
		
		Match match = client.getCurrentMatch();
		
		if(match != null && !match.isFinished()) {
			match.removePlayer(client);
		}
	}
	
	public boolean login(Client client, String playerName) {
		if(tournament != null) {
			return false;
		}
		
		boolean result = true;
		
		synchronized(clientList) {
			for(Client c: clientList) {
				if(c.getPlayerName() != null && c.getPlayerName().equals(playerName)) {
					result = false;
					break;
				}
			}
		}
		
		if(result) {
			client.setPlayerName(playerName);
			client.setLoggedIn(true);
			Log.DEBUG.printf("Client logged in: %s", playerName);
			
			notifyListeners(client, ACTION_LOGIN);
		}
		
		return result;
	}
	
	public boolean subscribe(Client client, String gameType) {
		boolean result = false;
		
		if(Application.getInstance().getGameLoader().getGameTypeList().contains(gameType)) {
			client.setSubscribedGameType(gameType);
			result = true;
		}
		
		notifyListeners(client, ACTION_SUBSCRIBE);
		
		return result;
	}
	
	public void challenge(Client player, Client opponent, String gameType) {
		// Cancel previous challenge, max 1 challenge
		cancelChallenge(player.getChallenge(), true);
		
		Challenge challenge = new Challenge(player, opponent, gameType);
		player.setChallenge(challenge);
		opponent.addChallenge(challenge);
		
		String challengeDetails = StringUtils.toStringAsMap("CHALLENGENUMBER", challenge.getChallengeNumber(), "CHALLENGER", player.getPlayerName(), "GAMETYPE", gameType);
		opponent.writeResponse(new GameResponse(String.format("CHALLENGE %s", challengeDetails)));
	}
	
	public void acceptChallenge(Challenge challenge) {
		cancelChallenge(challenge, false);
		
		// Randomize player order
		ArrayList<Client> players = new ArrayList<Client>();
		players.add(challenge.getPlayer());
		players.add(challenge.getOpponent());
		Collections.shuffle(players);
		
		// Start the match
		Match match = createMatch(challenge.getGameType(), players.get(0), players.get(1));
		match.start();
	}
	
	public void cancelChallenge(Challenge challenge) {
		cancelChallenge(challenge, true);
	}
	
	private void cancelChallenge(Challenge challenge, boolean notify) {
		if(challenge == null) {
			return;
		}
		
		challenge.getPlayer().setChallenge(null);
		challenge.getOpponent().removeChallenge(challenge);
		
		if(notify) {
			String challengeNumberString = StringUtils.toStringAsMap("CHALLENGENUMBER", challenge.getChallengeNumber());
			String format = "CHALLENGE CANCELLED %s";
			challenge.getOpponent().writeResponse(new GameResponse(String.format(format, challengeNumberString)));
		}
	}
	
	public Match findMatch(Client client) {
		if(tournament != null) {
			return null;
		}
		
		String gameType = client.getSubscribedGameType();
		
		if(!client.isLoggedIn() || client.getSubscribedGameType() == null) {
			return null;
		}
		
		if(client.getCurrentMatch() != null && !client.getCurrentMatch().isFinished()) {
			return null;
		}
		
		if(gameType== null) {
			return null;
		}
		
		ArrayList<Client> players = null;
		
		synchronized(clientList) {
			for(Client opponent: clientList) {
				if(opponent != client && gameType.equals(opponent.getSubscribedGameType()) && (opponent.getCurrentMatch() == null || opponent.getCurrentMatch().isFinished())) {
					players = new ArrayList<Client>();
					players.add(client);
					players.add(opponent);
					Collections.shuffle(players);
					break;
				}
			}
		}
		
		Match match = null;
		
		if(players != null) {
			match = new Match(gameType, players.get(0), players.get(1));
			match.start();
			
			notifyListeners(players.get(0), ACTION_MATCH);
			notifyListeners(players.get(1), ACTION_MATCH);
		}
		
		return match;
	}
	
	public void matchFinished(Match match) {
		Client playerOne = match.getPlayerOne();
		Client playerTwo = match.getPlayerTwo();
		
		playerOne.setCurrentMatch(null);
		playerTwo.setCurrentMatch(null);
		
		notifyListeners(playerOne, ACTION_MATCH);
		notifyListeners(playerTwo, ACTION_MATCH);
		
		if(tournament != null) {
			tournament.onMatchFinished();
			
			tournament.nextRound();
			
			if(tournament != null && !tournament.hasNextRound()) {
				onTournamentFinished();
			}
		}
		
		findMatch(playerOne);
		findMatch(playerTwo);
	}
	
	public Match createMatch(String gameType, Client playerOne, Client playerTwo) {
		Match match = new Match(gameType, playerOne, playerTwo);
		notifyListeners(playerOne, ACTION_MATCH);
		notifyListeners(playerTwo, ACTION_MATCH);
		
		return match;
	}
	
	public ArrayList<Match> getMatchList() {
		ArrayList<Match> matchList = new ArrayList<Match>();
		
		synchronized(clientList) {
			for(Client c : clientList) {
				Match match = c.getCurrentMatch();
				
				if(match != null && !matchList.contains(match)) {
					matchList.add(match);
				}
			}
		}
		
		return matchList;
	}
	
	public Client getClientByName(String name) {
		Client client = null;
		
		synchronized(clientList) {
			for(Client c : clientList) {
				if(c.getPlayerName() != null && c.getPlayerName().equals(name)) {
					client = c;
					break;
				}
			}
		}
		
		return client;
	}
	
	public Tournament organiseTournament(String gameType) {
		ArrayList<Client> playerList = new ArrayList<Client>();
		
		for(Client c : clientList) {
			if(c.isLoggedIn()) {
				playerList.add(c);
			}
		}
		
		tournament = new Tournament(this, gameType, playerList);
		
		return tournament;
	}
	
	public void startTournament() {
		if(tournament != null) {
			tournament.nextRound();
			if(!tournament.hasNextRound()) {
				onTournamentFinished();
			}
		}
	}
	
	private void onTournamentFinished() {
		tournament = null;
	}
	
	public Tournament getTournament() {
		return tournament;
	}
	
	public void addActionListener(ActionListener l) {
		if(l != null) {
			listenerList.add(l);
		}
	}
	
	private void notifyListeners(Client client, int action) {
		for(ActionListener l : listenerList) {
			l.actionPerformed(new ActionEvent(this, action, client.getPlayerName()));
		}
	}

}
