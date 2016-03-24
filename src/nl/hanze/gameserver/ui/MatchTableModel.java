package nl.hanze.gameserver.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import nl.hanze.gameserver.server.ClientManager;
import nl.hanze.gameserver.server.Match;

public class MatchTableModel extends AbstractTableModel implements ActionListener {

	private static final long serialVersionUID = 0L;
	private static final String[] COLUMN_NAMES = {"Number", "GameType", "Player One", "Player Two"};
	
	private ClientManager clientManager; 
	private ArrayList<Match> matchList;
	
	public MatchTableModel(ClientManager clientManager) {
		this.clientManager = clientManager;
		clientManager.addActionListener(this);
		
		matchList = new ArrayList<Match>();
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
	public Class<?> getColumnClass(int col) {
		return getValueAt(0, col).getClass();
	}
	
	@Override
	public int getRowCount() {
		return matchList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(rowIndex < 0 || columnIndex < 0) {
			return null;
		}
		
		return matchList.get(rowIndex);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == clientManager) {
			int action = e.getID();
			
			if(action == ClientManager.ACTION_MATCH) {
				matchList = clientManager.getMatchList();
				fireTableDataChanged();
			}
		}
	}

}
