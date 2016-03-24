package nl.hanze.gameserver.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import nl.hanze.gameserver.app.Application;
import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.ClientManager;
import nl.hanze.gameserver.server.Match;
import nl.hanze.gameserver.server.Tournament;
import nl.hanze.gameserver.util.KeyValuePair;
import nl.hanze.t23i.gamemodule.extern.AbstractGameModule;

public class TournamentScoreTableModel extends AbstractTableModel implements ActionListener {

	private static final long serialVersionUID = 0L;
	
	public static final Integer SCORE_UNABLE = Integer.MAX_VALUE;
	public static final Integer SCORE_UNAVAILABLE = Integer.MIN_VALUE;
	public static final Integer SCORE_BUSY = Integer.MIN_VALUE + 1;
	public static final Integer SCORE_WIN = AbstractGameModule.PLAYER_WIN;
	public static final Integer SCORE_LOSS = AbstractGameModule.PLAYER_LOSS;
	public static final Integer SCORE_DRAW = AbstractGameModule.PLAYER_DRAW;
	
	private static final String FIRST_COLUMN = "P1\\P2";
	
	private ClientManager clientManager;
	private Tournament tournament;
	private String[] players;
	private int[][] scores;
	
	public TournamentScoreTableModel() {
		clientManager = Application.getInstance().getGameServer().getClientManager();
		clientManager.addActionListener(this);
		setTournament(null);
	}
	
	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
		updateStructure();
		fireTableStructureChanged();
		
		if(this.tournament != null) {
			this.tournament.addActionListener(this);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == tournament) {
			int action = e.getID();
			
			if(action == Tournament.ACTION_DISCONNECTED) {
				updateStructure();
				fireTableStructureChanged();
				
				updateScores();
				fireTableDataChanged();
			}
			
			if(action == Tournament.ACTION_ROUND_FINISHED) {
				updateScores();
				fireTableDataChanged();
			}
		}
		
		if(e.getSource() == clientManager) {
			int action = e.getID();
			
			if(action == ClientManager.ACTION_MATCH) {
				if(clientManager.getTournament() != null) {
					updateBusy();
					fireTableDataChanged();
				}
			}
		}
	}
	
	private void updateStructure() {
		if(tournament == null) {
			players = null;
			scores = null;
			return;
		}
		
		ArrayList<Client> playerList = tournament.getPlayers();
		players = new String[playerList.size()];
		for(int i=0;i<playerList.size();i++) {
			players[i] = playerList.get(i).getPlayerName();
		}
		
		scores = new int[players.length][players.length];
		
		for(int y=0;y<scores.length;y++) {
			for(int x=0;x<scores[y].length;x++) {
				if(y == x) {
					scores[y][x] = SCORE_UNABLE;
				} else {
					scores[y][x] = SCORE_UNAVAILABLE;
				}
			}
		}
	}
	
	private void updateScores() {
		for(Map.Entry<KeyValuePair<Client, Client>, KeyValuePair<Integer, Integer>> resultEntry : tournament.getResults().entrySet()) {
			Client playerOne = resultEntry.getKey().getKey();
			Client playerTwo = resultEntry.getKey().getValue();
			int playerOneResult = resultEntry.getValue().getKey();
			int playerTwoResult = resultEntry.getValue().getValue();
			
			updateScore(playerOne, playerTwo, playerOneResult, playerTwoResult);
		}
		
		Client playerOne = tournament.getCurrentMatch().getPlayerOne();
		int playerOneResult = tournament.getCurrentMatch().getPlayerResult(playerOne);
		
		Client playerTwo = tournament.getCurrentMatch().getPlayerTwo();
		int playerTwoResult = tournament.getCurrentMatch().getPlayerResult(playerTwo);
		
		updateScore(playerOne, playerTwo, playerOneResult, playerTwoResult);
	}
	
	private void updateScore(Client playerOne, Client playerTwo, int playerOneResult, int playerTwoResult) {
		int playerOneIndex = getPlayerIndex(playerOne.getPlayerName());
		int playerTwoIndex = getPlayerIndex(playerTwo.getPlayerName());
		
		if(playerOneIndex != -1 && playerTwoIndex != -1) {
			scores[playerOneIndex][playerTwoIndex] = playerOneResult;
			scores[playerTwoIndex][playerOneIndex] = playerTwoResult;
		}
	}
	
	private void updateBusy() {
		for(Match match : clientManager.getMatchList()) {
			int playerOneIndex = getPlayerIndex(match.getPlayerOne().getPlayerName());
			int playerTwoIndex = getPlayerIndex(match.getPlayerTwo().getPlayerName());
			
			if(playerOneIndex != -1 && playerTwoIndex != -1) {
				scores[playerOneIndex][playerTwoIndex] = SCORE_BUSY;
				scores[playerTwoIndex][playerOneIndex] = SCORE_BUSY;
			}
		}
	}
	
	public int getPlayerIndex(String player) {
		int index = -1;
		
		for(int i=0;i<players.length;i++) {
			if(players[i].equals(player)) {
				index = i;
				break;
			}
		}
		
		return index;
	}
	
	@Override
	public int getColumnCount() {
		return players == null ? 1 : players.length + 1;
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		String columnName = null;
		
		if(columnIndex == 0) {
			columnName = FIRST_COLUMN;
		} else {
			columnName = players[columnIndex - 1];
		}
		
		return columnName;
	}
	
	@Override
	public int getRowCount() {
		return players == null ? 0 : players.length;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object value = null;
		
		if(columnIndex == 0) {
			value = tournament.getPlayers().get(rowIndex).getPlayerName();
		} else {
			value = scores[rowIndex][columnIndex - 1];
		}
		
		return value;
	}

}
