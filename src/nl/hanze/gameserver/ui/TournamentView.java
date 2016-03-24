package nl.hanze.gameserver.ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import nl.hanze.gameserver.app.Application;
import nl.hanze.gameserver.server.Client;
import nl.hanze.gameserver.server.Tournament;
import nl.hanze.gameserver.util.KeyValuePair;
import nl.hanze.gameserver.util.Log;
import nl.hanze.t23i.gamemodule.extern.AbstractGameModule;

public class TournamentView extends JFrame implements ActionListener {

	private static final long serialVersionUID = 0L;
	
	public static final int ACTION_CLOSED = 0;
	
	private static final String STARTBUTTON_TEXT_ENABLED = "Start tournament";
	private static final String STARTBUTTON_TEXT_DISABLED = "Tournament in progress";
	
	private JComboBox gameTypeBox;
	private JButton startButton;
	private TournamentScorePanel scorePanel;
	
	private LinkedBlockingQueue<ActionListener> listenerList;
	private Tournament tournament;
	
	private MatchView lastMatchView;
	
	public TournamentView() {
		super("Tournament");
		setVisible(false);
		
		JPanel panel = new JPanel(new GridLayout(2, 1));
		setContentPane(panel);
		
		JPanel controlPanel = new JPanel(new FlowLayout());
		String[] gameTypes = Application.getInstance().getGameLoader().getGameTypeList().toArray(new String[0]);
		Arrays.sort(gameTypes);
		gameTypeBox = new JComboBox(gameTypes);
		gameTypeBox.setSelectedIndex(gameTypes.length > 0 ? 0 : -1);
		controlPanel.add(new JLabel("Game type:"));
		controlPanel.add(gameTypeBox);
		
		startButton = new JButton(STARTBUTTON_TEXT_ENABLED);
		startButton.addActionListener(this);
		controlPanel.add(startButton);
		
		panel.add(controlPanel);
		
		scorePanel = new TournamentScorePanel();
		panel.add(scorePanel);
		
		tournament = null;
		listenerList = new LinkedBlockingQueue<ActionListener>();
		lastMatchView = null;
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				TournamentView.this.close();
			}
		});
		
		setSize(400, 300);
		setResizable(true);
		requestFocus();
	}
	
	private void close() {
		notifyListeners(ACTION_CLOSED);
	}
	
	public void addActionListener(ActionListener l) {
		if(l != null) {
			listenerList.add(l);
		}
	}
	
	public void removeActionListener(ActionListener l) {
		listenerList.remove(l);
	}
	
	private void notifyListeners(int action) {
		for(ActionListener l : listenerList) {
			l.actionPerformed(new ActionEvent(this, action, null));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == startButton) {
			startButton.setEnabled(false);
			startButton.setText(STARTBUTTON_TEXT_DISABLED);
			gameTypeBox.setEnabled(false);
			
			String gameType = (String) gameTypeBox.getSelectedItem();
			tournament = Application.getInstance().getGameServer().getClientManager().organiseTournament(gameType);
			scorePanel.setTournament(tournament);
			tournament.addActionListener(this);
			Application.getInstance().getGameServer().getClientManager().startTournament();
		} else if(e.getSource() == tournament) {
			int action = e.getID();
			
			if(action == Tournament.ACTION_NEXT_ROUND) {
				KeyValuePair<Client, Client> players = tournament.getCurrentPlayers();
				Client playerOne = players.getKey();
				Client playerTwo = players.getValue();
				
				if(tournament.getCurrentMatch().getGameModule().getView() != null) {
					lastMatchView = new MatchView(tournament.getCurrentMatch());
				}
				
				String infoString = String.format("Time for a new round.\n%s and %s are up next!", playerOne.getPlayerName(), playerTwo.getPlayerName());
				JOptionPane.showMessageDialog(this, infoString, "Round finished", JOptionPane.INFORMATION_MESSAGE);
			}
			
			if(action == Tournament.ACTION_ROUND_FINISHED) {
				KeyValuePair<Client, Client> players = tournament.getCurrentPlayers();
				Client playerOne = players.getKey();
				Client playerTwo = players.getValue();
				
				String resultString = "Match resulted in a draw.";
				int playerOneResult = tournament.getCurrentMatch().getPlayerResult(players.getKey());
				if(playerOneResult == AbstractGameModule.PLAYER_WIN) {
					resultString = String.format("%s won, %s lost.", playerOne.getPlayerName(), playerTwo.getPlayerName());
				} else if(playerOneResult == AbstractGameModule.PLAYER_LOSS) {
					resultString = String.format("%s won, %s lost.", playerTwo.getPlayerName(), playerOne.getPlayerName());
				}
				
				int playerOneScore = tournament.getCurrentMatch().getPlayerScore(playerOne);
				int playerTwoScore = tournament.getCurrentMatch().getPlayerScore(playerTwo);
				String playerScoreString = String.format("%s score: %d, %s score: %d.", playerOne.getPlayerName(), playerOneScore, playerTwo.getPlayerName(), playerTwoScore);
				
				resultString += "\n" + playerScoreString;
				resultString += "\n" + tournament.getCurrentMatch().getMatchResultComment();
				
				JOptionPane.showMessageDialog(this, String.format("Round has ended.\n%s", resultString), "Match finished", JOptionPane.INFORMATION_MESSAGE);
				
				if(lastMatchView != null) {
					lastMatchView.dispose();
				}
			}
			
			if(action == Tournament.ACTION_FINISHED) {
				JOptionPane.showMessageDialog(this, "Tournament has ended.", "Tournament finished", JOptionPane.INFORMATION_MESSAGE);
				
				HashMap<KeyValuePair<Client, Client>, KeyValuePair<Integer, Integer>> results = tournament.getResults();
				
				for(Map.Entry<KeyValuePair<Client, Client>, KeyValuePair<Integer, Integer>> roundResultEntry : results.entrySet()) {
					KeyValuePair<Client, Client> players = roundResultEntry.getKey();
					Client playerOne = players.getKey();
					Client playerTwo = players.getValue();
					
					KeyValuePair<Integer, Integer> roundResult = roundResultEntry.getValue();
					Integer playerOneResult = roundResult.getKey();
					Integer playerTwoResult = roundResult.getValue();
					
					Log.DEBUG.printf("Tournament match result: %s vs. %s: %d, %d", playerOne.getPlayerName(), playerTwo.getPlayerName(), playerOneResult, playerTwoResult);
				}
				
				startButton.setEnabled(true);
				startButton.setText(STARTBUTTON_TEXT_ENABLED);
				gameTypeBox.setEnabled(true);
			}
		}
	}

}
