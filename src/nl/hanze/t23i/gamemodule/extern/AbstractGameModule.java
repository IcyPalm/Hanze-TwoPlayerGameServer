package nl.hanze.t23i.gamemodule.extern;


/**
 * Abstract game module for pluggable game modules.
 * <p>
 * DO NOT EDIT!<br />
 * This abstract class is not to be edited in any way. This ensures compatibility with other pluggable game module load systems.
 * <p>
 * This abstract class is useful when developing game modules to be used in other pluggable game module load systems.<br/>
 * An implementation class should call the mandatory constructor and use the constants to indicate statuses to ensure compatibility.
 * Also the <code>GAME_TYPE</code> constant should be present, indicating the name of the game type.
 * <p>
 * It is a good idea to call methods implemented by this class when overriding, to handle match status checks.
 * Do note, the <code>IGameModule</code> interface requires <code>getMoveDetails()</code> to check if a move has been made during a match.
 * 
 * @author Sjors van Oers
 * @author Hanze Hogeschool Groningen - Instituut voor ICT
 *
 */
public abstract class AbstractGameModule implements IGameModule {

	/**
	 * Value indicating the match is initialized
	 */
	public static final int MATCH_INITIALIZED = -1;
	
	/**
	 * Value indicating the match has started
	 */
	public static final int MATCH_STARTED = 0;
	
	/**
	 * Value indicating the match has stopped
	 */
	public static final int MATCH_FINISHED = 1;
	
	/**
	 * Value indicating the player has lost the match
	 */
	public static final int PLAYER_LOSS = -1;
	
	/**
	 * Value indicating the match resulted in a draw
	 */
	public static final int PLAYER_DRAW = 0;
	
	/**
	 * Value indicating the player has won the match
	 */
	public static final int PLAYER_WIN = 1;
	
	/**
	 * Constant stating the game type of this implementation
	 */
	public static final String GAME_TYPE = "";
	
	protected String playerOne;
	protected String playerTwo;
	
	protected int matchStatus;
	
	/**
	 * Mandatory constructor.
	 * <p>
	 * This function will change the match status to initialized (constant <code>MATCH_INITIALIZED</code>).
	 * 
	 * @param playerOne
	 * @param playerTwo
	 */
	public AbstractGameModule(String playerOne, String playerTwo) {
		this.playerOne = playerOne;
		this.playerTwo = playerTwo;
		
		matchStatus = MATCH_INITIALIZED;
	}
	
	@Override
	public void doPlayerMove(String player, String move) throws IllegalStateException {
		if(matchStatus != MATCH_STARTED) {
			throw new IllegalStateException("Illegal match state");
		}
	}
	
	@Override
	public int getPlayerScore(String player) throws IllegalStateException {
		if(matchStatus != MATCH_FINISHED) {
			throw new IllegalStateException("Illegal match state");
		}
		
		return 0;
	}
	
	@Override
	public String getMatchResultComment() throws IllegalStateException {
		if(matchStatus != MATCH_FINISHED) {
			throw new IllegalStateException("Illegal match state");
		}
		
		return null;
	}
	
	@Override
	public int getMatchStatus() {
		return matchStatus;
	}
	
	@Override
	public String getMoveDetails() throws IllegalStateException {
		if(matchStatus == MATCH_INITIALIZED) {
			throw new IllegalStateException("Illegal match state");
		}
		
		return null;
	}
	
	@Override
	public String getPlayerToMove() throws IllegalStateException {
		if(matchStatus != MATCH_STARTED) {
			throw new IllegalStateException("Illegal match state");
		}
		
		return null;
	}
	
	@Override
	public int getPlayerResult(String player) throws IllegalStateException {
		if(matchStatus != MATCH_FINISHED) {
			throw new IllegalStateException("Illegal match state");
		}
		
		return 0;
	}
	
	@Override
	public String getTurnMessage() throws IllegalStateException {
		if(matchStatus != MATCH_STARTED) {
			throw new IllegalStateException("Illegal match state");
		}
		
		return null;
	}
	
	@Override
	public void start() throws IllegalStateException {
		if(matchStatus != MATCH_INITIALIZED) {
			throw new IllegalStateException("Illegal match state");
		}
		
		matchStatus = MATCH_STARTED;
	}

}
