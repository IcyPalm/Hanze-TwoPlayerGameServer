package nl.hanze.gameserver.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import nl.hanze.gameserver.server.ClientManager;

public class PlayerPanel extends JPanel {

	private static final long serialVersionUID = 0L;
	
	private JTable table;
	private PlayerTableModel tableModel;
	
	public PlayerPanel(ClientManager clientManager) {
		super(new FlowLayout());
		setVisible(true);
		
		tableModel = new PlayerTableModel(clientManager);
		table = new JTable(tableModel);
		table.setPreferredScrollableViewportSize(new Dimension(390, 70));
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);
	}

}
