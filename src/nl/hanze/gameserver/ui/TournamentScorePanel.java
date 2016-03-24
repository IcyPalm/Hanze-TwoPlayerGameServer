package nl.hanze.gameserver.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nl.hanze.gameserver.server.Tournament;

public class TournamentScorePanel extends JPanel {

	private static final long serialVersionUID = 0L;
	
	private TournamentScoreTable table;
	private JScrollPane scrollPane;
	
	public TournamentScorePanel() {
		super(new FlowLayout());
		setVisible(true);
		
		table = new TournamentScoreTable();
		table.setPreferredScrollableViewportSize(new Dimension(390, 70));
		table.setFillsViewportHeight(true);
		scrollPane = new JScrollPane(table);
		add(scrollPane);
	}
	
	public void setTournament(Tournament tournament) {
		table.setTournament(tournament);
	}
	
	@Override
	public void repaint() {
		if(scrollPane != null) {
			table.setPreferredScrollableViewportSize(getSize());
			scrollPane.setPreferredSize(getSize());
		}
		super.repaint();
	}

}
