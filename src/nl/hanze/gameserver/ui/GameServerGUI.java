package nl.hanze.gameserver.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import nl.hanze.gameserver.app.Application;
import nl.hanze.gameserver.server.GameServer;
import nl.hanze.gameserver.util.Log;

public class GameServerGUI implements ActionListener {
	
	private JFrame frame;
	private PlayerPanel clientPanel;
	private MatchPanel matchPanel;
	private JButton tournamentButton;
	private TournamentView tournamentView;
	
	private GameServer gameServer;
	
	public GameServerGUI(GameServer gameServer) {
		this.gameServer = gameServer;
		
		createAndShowGui();
	}
	
	private void createAndShowGui() {
		frame = new JFrame(String.format("%s-%s", Application.getInstance().getShortName(), Application.getInstance().getVersion()));
		
		setNativeLookAndFeel();
		
		JPanel panel = new JPanel(new FlowLayout());
		frame.setContentPane(panel);
		
		clientPanel = new PlayerPanel(gameServer.getClientManager());
		panel.add(clientPanel);
		
		matchPanel = new MatchPanel(gameServer.getClientManager());
		panel.add(matchPanel);
		
		tournamentButton = new JButton("Show tournament window");
		tournamentButton.addActionListener(this);
		panel.add(tournamentButton);
		
		tournamentView = new TournamentView();
		tournamentView.addActionListener(this);
		
		hideTournamentView();
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Application.getInstance().exit();
			}
		});
		
		frame.setSize(400, 300);
		frame.setResizable(true);
		frame.setVisible(true);
	}
	
	public void exit() {
		frame.dispose();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == tournamentButton) {
			if(tournamentView.isVisible()) {
				hideTournamentView();
			} else {
				showTournamentView();
			}
		} else if(e.getSource() == tournamentView) {
			if(e.getID() == TournamentView.ACTION_CLOSED) {
				hideTournamentView();
			}
		}
	}
	
	private void showTournamentView() {
		tournamentButton.setText("Close tournament view");
		tournamentView.setVisible(true);
	}
	
	private void hideTournamentView() {
		tournamentButton.setText("Show tournament view");
		tournamentView.setVisible(false);
	}
	
	private void setNativeLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			Log.ERROR.printf("Error setting native LAF: %s", e);
		}
	}

}
