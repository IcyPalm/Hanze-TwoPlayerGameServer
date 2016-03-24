package nl.hanze.gameserver.ui;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import nl.hanze.gameserver.server.Match;

public class MatchView extends JFrame {

	private static final long serialVersionUID = 0L;

	public MatchView(Match match) {
		super(String.format("Match %d - %s vs. %s", match.getMatchNumber(), match.getPlayerOne().getPlayerName(), match.getPlayerTwo().getPlayerName()));
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setContentPane(new JPanel(new FlowLayout()));
		getContentPane().add(match.getGameModule().getView());
		setSize(400, 400);
		setResizable(true);
	}

}
