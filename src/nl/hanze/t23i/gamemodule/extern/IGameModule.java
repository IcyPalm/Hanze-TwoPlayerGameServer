package nl.hanze.t23i.gamemodule.extern;

import java.awt.Component;

/**
 * The game module interface for pluggable game modules.
 * <p>
 * DO NOT EDIT!<br />
 * This interface is not to be edited in any way. This ensures compatibility with other pluggable game module load systems.
 * <p>
 * The class that is interested in being plugged in, in a pluggable game module load system, implements this interface.<br />
 * It is a good idea to use the abstract class <code>AbstractGameModule</code> for this purpose, instead of this interface.
 * This because the abstract class contains a mandatory constructor, a mandatory static field and several constants indicating statuses.
 * 
 * @author Sjors van Oers
 * @author Hanze Hogeschool Groningen - Instituut voor ICT
 *
 */
public interface IGameModule {
	
	/**
	 * Starts the match.
	 * @throws IllegalStateException If the match is already started or the match has finished
	 */
	public void start() throws IllegalStateException;
	
	/**
	 * Returns the name of the player who is to move next.
	 * @return The name of the player who is to move next
	 * @throws IllegalStateException If the match has not yet been started or the match has finished
	 */
	public String getPlayerToMove() throws IllegalStateException;
	
	/**
	 * Does a move for the given player.
	 * @param player The player doing the move
	 * @param move The move the player is doing
	 * @throws IllegalStateException If the match is not yet started, the match has finished or it is not player's turn
	 */
	public void doPlayerMove(String player, String move) throws IllegalStateException;
	
	/**
	 * Returns the message dedicated to the player of the next turn.
	 * <p>
	 * The message is game specific and may be empty.
	 * @return A message to the player of the next turn.
	 * @throws IllegalStateException If the match has not yet been started or the match has finished
	 */
	public String getTurnMessage() throws IllegalStateException;
	
	/**
	 * Returns the details of the last move done during this match.
	 * <p>
	 * These details are game specific and may be empty.
	 * @return A string representing the details of the last move
	 * @throws IllegalStateException If no move has been done during this match
	 */
	public String getMoveDetails() throws IllegalStateException;
	
	/**
	 * Returns the status of the match.
	 * <p>
	 * The return code can either indicate that the match has been started, is in progress or has been finished.
	 * @return <code>-1</code> if the match has not yet started, <code>0</code> if the match is started and has not yet finished, <code>1</code> if the match has finished
	 */
	public int getMatchStatus();
	
	/**
	 * Returns the result of the match for a given player.
	 * <p>
	 * The return code will indicate if the player has won the match, has lost the match or the match has ended in a draw.
	 * @param player The player to whom the result belong to.
	 * @return <code>-1</code> if the player lost the match, <code>0</code> if the match resulted in a draw, <code>1</code> if the player won the match
	 * @throws IllegalStateException If the match has not yet finished
	 */
	public int getPlayerResult(String player) throws IllegalStateException;
	
	/**
	 * Returns the score of a player.
	 * <p>
	 * These results are game specific.
	 * @return An integer representing the score of a player
	 * @throws IllegalStateException If the match has not yet finished
	 */
	public int getPlayerScore(String player) throws IllegalStateException;
	
	/**
	 * Returns a string commenting the result of the match.
	 * <p>
	 * This comment is game specific and may be empty.
	 * @return A string commenting the result of the match
	 * @throws IllegalStateException If the match has not yet finished
	 */
	public String getMatchResultComment() throws IllegalStateException;
	
	/**
	 * Returns a view of this match.
	 * <p>
	 * It is a good idea for this view to be resizable.
	 * @return A view of the match
	 */
	public Component getView();
	
}
