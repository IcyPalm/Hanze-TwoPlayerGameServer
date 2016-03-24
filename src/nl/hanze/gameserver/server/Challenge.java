package nl.hanze.gameserver.server;

public class Challenge {

	private static int challengeNumberCount = 0;
	
	private int challengeNumber;
	private Client player;
	private Client opponent;
	private String gameType;
	
	public Challenge(Client player, Client opponent, String gameType) {
		challengeNumber = challengeNumberCount++;
		this.player = player;
		this.opponent = opponent;
		this.gameType = gameType;
	}

	public int getChallengeNumber() {
		return challengeNumber;
	}

	public Client getPlayer() {
		return player;
	}

	public Client getOpponent() {
		return opponent;
	}

	public String getGameType() {
		return gameType;
	}
	
	@Override
	public boolean equals(Object obj) {
		boolean result = obj == this;
		
		if(!result && obj != null && obj.getClass() == Challenge.class) {
			Challenge other = (Challenge) obj;
			result = other.challengeNumber == challengeNumber;
		}
		
		return result;
	}
	
	@Override
	public int hashCode() {
		return challengeNumber;
	}

}
