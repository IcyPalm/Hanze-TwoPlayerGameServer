package nl.hanze.gameserver.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.ClientManager;
import nl.hanze.gameserver.server.Match;
import nl.hanze.gameserver.util.StringUtils;

public class PlayerTableModel extends AbstractTableModel implements ActionListener {

	private static final long serialVersionUID = 0L;
	private static final String[] COLUMN_NAMES = {"Name", "Subscription", "Match"};
	
	private ClientManager clientManager; 
	private ArrayList<Client> playerList;
	
	public PlayerTableModel(ClientManager clientManager) {
		this.clientManager = clientManager;
		clientManager.addActionListener(this);
		
		playerList = new ArrayList<Client>();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}
	
	@Override
	public String getColumnName(int column) {
		return column < getColumnCount() ? COLUMN_NAMES[column] : "";
	}

	@Override
	public int getRowCount() {
		return playerList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		Client player = playerList.get(rowIndex);
		Object value = "";
		
		if(columnIndex == 0) {
			value = player.getPlayerName();
		} else if(columnIndex == 1) {
			value = player.getSubscribedGameType();
		} else if(columnIndex == 2) {
			Match match = player.getCurrentMatch();
			
			if(match == null) {
				value = "";
			} else {
				String opponent = match.getOtherPlayer(player).getPlayerName();
				String gameType = match.getGameType();
				String matchData = StringUtils.toStringAsMap("Opponent", opponent, "GameType", gameType);
				
				value = String.format("Match-%d %s", match.getMatchNumber(), matchData);
			}
		}
		
		return value;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == clientManager) {
			int action = e.getID();
			String playerName = e.getActionCommand();
			
			Client player = playerName == null ? null : clientManager.getClientByName(playerName);
			
			if(action == ClientManager.ACTION_CONNECTED) {
				return;
			} else if(action == ClientManager.ACTION_LOGIN) {
				playerList.add(player);
				fireTableDataChanged();
			} else if(action == ClientManager.ACTION_LOGOUT) {
				playerList.remove(player);
				fireTableDataChanged();
			} else if(action == ClientManager.ACTION_MATCH) {
				fireTableDataChanged();
			} else if(action == ClientManager.ACTION_SUBSCRIBE) {
				fireTableDataChanged();
			}
		}
	}

}
