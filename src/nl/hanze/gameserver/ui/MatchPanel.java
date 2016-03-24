package nl.hanze.gameserver.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nl.hanze.gameserver.server.ClientManager;

public class MatchPanel extends JPanel {

	private static final long serialVersionUID = 0L;
	
	private MatchTable table;
	
	public MatchPanel(ClientManager clientManager) {
		super(new FlowLayout());
		setVisible(true);
		
		table = new MatchTable();
		table.setPreferredScrollableViewportSize(new Dimension(390, 70));
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);
	}

}
