/**
 * Keeps track of a player's information for the leaderboard
 * 
 * @author William Xu and Tony Wu
 * @version January 18 2015
 *
 */

public class Player
{
	public String name;
	public boolean exists;
	public int score;
	public int days;

	/**
	 *  Pre-load the player object to be used later when the exists variable to set to true
	 */
	public Player()
	{
		this.exists = false;
	}
	
	/**
	 *  Create the player based on the information given
	 */
	public void setPlayer (String name, int score, int days)
	{
		this.name = name;
		this.exists = true;
		this.score = score;
		this.days = days;
	}

}
