package nl.hanze.gameserver.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nl.hanze.gameserver.app.Application;
import nl.hanze.gameserver.app.Settings;
import nl.hanze.gameserver.util.KeyValuePair;

public class Tournament {

	public static final int ACTION_ROUND_FINISHED = 0;
	public static final int ACTION_NEXT_ROUND = 1;
	public static final int ACTION_FINISHED = 2;
	public static final int ACTION_DISCONNECTED = 3;
	
	private ArrayList<ActionListener> listenerList;
	
	private String gameType;
	private ArrayList<Client> players;
	
	private ArrayList<KeyValuePair<Client, Client>> roundList;
	private HashMap<KeyValuePair<Client, Client>, KeyValuePair<Integer, Integer>> results;
	private Match currentMatch;
	
	public Tournament(ClientManager clientManager, String gameType, ArrayList<Client> players) {
		this.gameType = gameType;
		this.players = players;
		listenerList = new ArrayList<ActionListener>();
		
		results = new HashMap<KeyValuePair<Client, Client>, KeyValuePair<Integer, Integer>>();
		fillRoundList();
	}
	
	public boolean hasNextRound() {
		return results.size() < roundList.size();
	}
	
	public void nextRound() {
		if(!hasNextRound()) {
			notifyListeners(ACTION_FINISHED);
			currentMatch = null;
			return;
		}
		
		KeyValuePair<Client, Client> round = currentRound();
		currentMatch = Application.getInstance().getGameServer().getClientManager().createMatch(gameType, round.getKey(), round.getValue());
		
		notifyListeners(ACTION_NEXT_ROUND);
		
		currentMatch.start();
	}
	
	public KeyValuePair<Client, Client> getCurrentPlayers() {
		return currentRound();
	}
	
	public ArrayList<Client> getPlayers() {
		return players;
	}
	
	public HashMap<KeyValuePair<Client, Client>, KeyValuePair<Integer, Integer>> getResults() {
		return results;
	}
	
	private KeyValuePair<Client, Client> currentRound() {
		return roundList.get(results.size());
	}
	
	public Match getCurrentMatch() {
		return currentMatch;
	}
	
	public KeyValuePair<Integer, Integer> getResult(Client playerOne, Client playerTwo) {
		int playerOneResults = 0;
		int playerTwoResults = 0;
		
		for(Map.Entry<KeyValuePair<Client, Client>, KeyValuePair<Integer, Integer>> roundResultEntry : results.entrySet()) {
			KeyValuePair<Client, Client> players = roundResultEntry.getKey();
			if((players.getKey() == playerOne && players.getValue() == playerTwo) ||(players.getValue() == playerOne && players.getKey() == playerTwo)) {
				playerOneResults = currentMatch.getPlayerResult(playerOne);
				playerTwoResults = currentMatch.getPlayerResult(playerTwo);
			}
		}
		
		return new KeyValuePair<Integer, Integer>(playerOneResults, playerTwoResults);
	}
	
	public void onMatchFinished() {
		notifyListeners(ACTION_ROUND_FINISHED);
		
		KeyValuePair<Client, Client> round = currentRound();
		int playerOneResult = currentMatch.getPlayerResult(round.getKey());
		int playerTwoResult = currentMatch.getPlayerResult(round.getValue());
		
		KeyValuePair<Integer, Integer> result = new KeyValuePair<Integer, Integer>(playerOneResult, playerTwoResult);
		results.put(round, result);
		
		checkDisconnectedClients();
	}
	
	private void checkDisconnectedClients() {
		// Only continue checking if removal of players on disconnect is required
		if(Application.getInstance().getSettings().getTournamentDisconnectAction() != Settings.ACTION_REMOVE) {
			return;
		}
		
		// Get list of disconnected players
		ArrayList<Client> disconnectedList = new ArrayList<Client>();
		for(Client client : players) {
			if(!client.isLoggedIn()) {
				disconnectedList.add(client);
			}
		}
		
		// Remove disconnected players
		for(Client client : disconnectedList) {
			// Remove disconnected players from round list
			Iterator<KeyValuePair<Client, Client>> itRound = roundList.iterator();
			while(itRound.hasNext()) {
				KeyValuePair<Client, Client> clientPair = itRound.next();
				if(clientPair.getKey() == client || clientPair.getValue() == client) {
					itRound.remove();
				}
			}
			
			// Remove disconnected players from result map
			Iterator<Map.Entry<KeyValuePair<Client, Client>, KeyValuePair<Integer, Integer>>> itResult = results.entrySet().iterator();
			while(itResult.hasNext()) {
				KeyValuePair<Client, Client> clientPair = itResult.next().getKey();
				if(clientPair.getKey() == client || clientPair.getValue() == client) {
					itResult.remove();
				}
			}
			
			// Remove disconnected players from player list
			Iterator<Client> itPlayer = players.iterator();
			while(itPlayer.hasNext()) {
				if(itPlayer.next() == client) {
					itPlayer.remove();
				}
			}
		}
		
		// Notify listeners
		if(!disconnectedList.isEmpty()) {
			notifyListeners(ACTION_DISCONNECTED);
		}
	}
	
	private void fillRoundList() {
		roundList = new ArrayList<KeyValuePair<Client, Client>>();
		
		ArrayList<Client> remainingPlayers = new ArrayList<Client>(players);
		
		for(Client player : players) {
			remainingPlayers.remove(player);
			
			for(Client opponent : remainingPlayers) {
				ArrayList<Client> roundPlayers = new ArrayList<Client>(2);
				roundPlayers.add(player);
				roundPlayers.add(opponent);
				Collections.shuffle(roundPlayers);
				
				KeyValuePair<Client, Client> round = new KeyValuePair<Client, Client>(roundPlayers.get(0), roundPlayers.get(1));
				roundList.add(round);
			}
		}
		
		Collections.shuffle(roundList);
	}
	
	public void addActionListener(ActionListener l) {
		if(l != null) {
			listenerList.add(l);
		}
	}
	
	private void notifyListeners(int action) {
		for(ActionListener l : listenerList) {
			l.actionPerformed(new ActionEvent(this, action, null));
		}
	}

}
