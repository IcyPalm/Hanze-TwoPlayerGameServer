package nl.hanze.gameserver.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.Timer;

import nl.hanze.gameserver.app.Application;
import nl.hanze.gameserver.server.message.GameResponse;
import nl.hanze.gameserver.server.message.Response;
import nl.hanze.gameserver.util.KeyValuePair;
import nl.hanze.gameserver.util.StringUtils;
import nl.hanze.t23i.gamemodule.extern.AbstractGameModule;
import nl.hanze.t23i.gamemodule.extern.IGameModule;


public class Match implements ActionListener {

	private static int matchNumberCount = 0;
	
	private AbstractGameModule gameModule;
	private int matchNumber;
	private String gameType;
	private Client playerOne;
	private Client playerTwo;
	
	private boolean finished;
	private KeyValuePair<Integer, Integer> playerResults;
	private KeyValuePair<Integer, Integer> playerScores;
	private String matchResultComment;
	
	private Timer timer;
	private Client timerClient;
	
	public Match(String gameType, Client playerOne, Client playerTwo) {
		gameModule = Application.getInstance().getGameLoader().loadGameModule(gameType, playerOne.getPlayerName(), playerTwo.getPlayerName());
		matchNumber = matchNumberCount++;
		this.gameType = gameType;
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;
		
		finished = false;
		playerResults = null;
		playerScores = null;
		matchResultComment = null;
		
		playerOne.setCurrentMatch(this);
		playerTwo.setCurrentMatch(this);
		
		playerOne.setSubscribedGameType(null);
		playerTwo.setSubscribedGameType(null);
		
		timer = new Timer(Application.getInstance().getSettings().getTurnTimeLimit() * 1000, this);
		
		// Cancel any player-initiated challenges
		playerOne.getClientManager().cancelChallenge(playerOne.getChallenge());
		playerTwo.getClientManager().cancelChallenge(playerTwo.getChallenge());
	}
	
	public void start() {
		gameModule.start();
		
		HashMap<String, String> messageEntries = new HashMap<String, String>();
		messageEntries.put("GAMETYPE", gameType);
		messageEntries.put("PLAYERTOMOVE", playerOne.getPlayerName());
		
		messageEntries.put("OPPONENT", playerTwo.getPlayerName());
		Response response = new GameResponse(String.format("MATCH %s", StringUtils.toString(messageEntries)));
		playerOne.writeResponse(response);
		
		messageEntries.put("OPPONENT", playerOne.getPlayerName());
		response = new GameResponse(String.format("MATCH %s", StringUtils.toString(messageEntries)));
		playerTwo.writeResponse(response);
		
		if(!playerOne.isLoggedIn() || !playerTwo.isLoggedIn()) {
			removePlayer(playerOne.isLoggedIn() ? playerTwo : playerOne);
		} else {
			nextTurn();
		}
	}
	
	public void nextTurn() {
		Client nextPlayer = getPlayerToMove();
		
		// If tournament in progress and turn delay has is greater than 0 and not first move, then delay turn
		if(timer.isRunning() && Application.getInstance().getGameServer().getClientManager().getTournament() != null) {
			int turnDelay = Application.getInstance().getSettings().getTournamentTurnDelay();
			if(turnDelay > 0) {
				timer.stop();
				try {
					Thread.sleep(turnDelay * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		Response response = new GameResponse(String.format("YOURTURN %s", StringUtils.toStringAsMap("TURNMESSAGE", gameModule.getTurnMessage())));
		nextPlayer.writeResponse(response);
		
		if(timer.isRunning()) {
			timer.restart();
		} else {
			timer.start();
		}
		timerClient = nextPlayer;
	}
	
	public void doPlayerMove(Client player, String move) throws IllegalStateException {
		synchronized(timer) {
			gameModule.doPlayerMove(player.getPlayerName(), move);
			
			Response moveResponse = new GameResponse(String.format("MOVE %s", StringUtils.toStringAsMap("PLAYER", player.getPlayerName(), "MOVE", move, "DETAILS", gameModule.getMoveDetails())));
			
			player.writeResponse(moveResponse);
			getOtherPlayer(player).writeResponse(moveResponse);
			
			if(!isFinished()) {
				nextTurn();
				return;
			} else {
				finished();
				return;
			}
		}
	}
	
	public void forfeit(Client player) {
		finishedAbnormally(player, "Player forfeited match");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == timer) {
			synchronized(timer) {
				if(timerClient == getPlayerToMove()) {
					finishedAbnormally(timerClient, "Turn timelimit reached");
				}
			}
		}
	}
	
	public void removePlayer(Client client) {
		finishedAbnormally(client, "Client disconnected");
	}
	
	private void finishedAbnormally(Client player, String reason) {
		int playerOneResult = player.equals(playerOne) ? AbstractGameModule.PLAYER_LOSS : AbstractGameModule.PLAYER_WIN;
		int playerTwoResult = player.equals(playerTwo) ? AbstractGameModule.PLAYER_LOSS : AbstractGameModule.PLAYER_WIN;
		
		announceGameResult(playerOneResult, playerTwoResult, 0, 0, reason);
		
		Application.getInstance().getGameServer().getClientManager().matchFinished(this);
	}
	
	private void finished() {
		announceGameResult();
		
		Application.getInstance().getGameServer().getClientManager().matchFinished(this);
	}
	
	private void announceGameResult() {
		int playerOneResult = gameModule.getPlayerResult(playerOne.getPlayerName());
		int playerTwoResult = gameModule.getPlayerResult(playerTwo.getPlayerName());
		int playerOneScore = gameModule.getPlayerScore(playerOne.getPlayerName());
		int playerTwoScore = gameModule.getPlayerScore(playerTwo.getPlayerName());
		String matchComment = gameModule.getMatchResultComment();
		announceGameResult(playerOneResult, playerTwoResult, playerOneScore, playerTwoScore, matchComment);
	}
	
	private void announceGameResult(int playerOneResult, int playerTwoResult, int playerOneScore, int playerTwoScore, String matchResultComment) {
		timer.stop();
		
		playerResults = new KeyValuePair<Integer, Integer>(playerOneResult, playerTwoResult);
		playerScores = new KeyValuePair<Integer, Integer>(playerOneResult, playerTwoResult);
		this.matchResultComment = matchResultComment;
		
		String extra = StringUtils.toStringAsMap("COMMENT", matchResultComment, "PLAYERONESCORE", playerOneScore, "PLAYERTWOSCORE", playerTwoScore);
		playerOne.writeResponse(new GameResponse(String.format("%s %s", getPlayerResultString(playerOneResult), extra)));
		playerTwo.writeResponse(new GameResponse(String.format("%s %s", getPlayerResultString(playerTwoResult), extra)));
	}
	
	public boolean isFinished() {
		return finished || gameModule.getMatchStatus() == AbstractGameModule.MATCH_FINISHED;
	}
	
	public Client getPlayerToMove() {
		return gameModule.getPlayerToMove().equals(playerOne.getPlayerName()) ? playerOne : playerTwo;
	}
	
	public IGameModule getGameModule() {
		return gameModule;
	}
	
	public String getGameType() {
		return gameType;
	}
	
	public Client getPlayerOne() {
		return playerOne;
	}
	
	public Client getPlayerTwo() {
		return playerTwo;
	}
	
	public int getPlayerResult(Client player) {
		return player.equals(playerOne) ? playerResults.getKey() : playerResults.getValue();
	}
	
	public int getPlayerScore(Client player) {
		return player.equals(playerOne) ? playerScores.getKey() : playerScores.getValue();
	}
	
	public String getMatchResultComment() {
		return matchResultComment;
	}
	
	public String getPlayerResultString(int playerResult) {
		String result = null;
		
		if(playerResult == AbstractGameModule.PLAYER_DRAW) {
			result = "DRAW";
		} else if(playerResult == AbstractGameModule.PLAYER_LOSS) {
			result = "LOSS";
		} else if (playerResult == AbstractGameModule.PLAYER_WIN) {
			result = "WIN";
		}
		
		return result;
	}
	
	public Client getOtherPlayer(Client player) {
		return player.equals(playerOne) ? playerTwo : playerOne;
	}
	
	public int getMatchNumber() {
		return matchNumber;
	}

}
