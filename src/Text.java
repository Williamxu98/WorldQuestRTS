import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

/**
 * Keeps track of a text object's information and methods
 * 
 * @author William Xu and Tony Wu
 * @version January 10 2015
 *
 */

public class Text
{
	public int width;
	public boolean getWidth;
	public int x, y;
	public boolean exists;
	public Font font;
	public Color color;
	public String string;
	public boolean doesRise;
	public int pixelsMoved;
	public int alignment;
	public int timeElapsed;
	public int maxTime;
	public boolean doesScroll;

	/**
	 * Pre-create the text object to be used later when the exists variable is
	 * set to true
	 */
	public Text()
	{
		this.exists = false;
	}

	public void create(String string, int x, int y, Font font, Color color,
			boolean doesRise, int time, int alignment, boolean doesScroll)
	{
		this.string = string;
		this.x = x;
		this.y = y;
		this.exists = true;
		this.getWidth = true;
		this.color = color;
		this.font = font;
		this.doesRise = doesRise;
		this.pixelsMoved = 0;
		this.alignment = alignment;
		this.maxTime = time;
		this.timeElapsed = 0;
		this.doesScroll = doesScroll;

	}

	/**
	 * Align the text however you need to, only after the width of the text has
	 * been worked out!
	 * 
	 */
	public void align()
	{
		if (alignment == 0)
		{
			x = x - width / 2;
		}
		else if (alignment == 1)
		{
			x = x - width;
		}
	}

	/**
	 * To keep track of the time in the text object, which is cycled through
	 * every frame from the engine
	 */
	public void passTime()
	{

		timeElapsed++;
		if (doesRise)
		{
			rise();
		}

		if (timeElapsed >= 90)
		{
			this.exists = false;
		}

	}

	/**
	 * To gradually float the text object and create a cool effect by having it
	 * fade
	 */
	public void rise()
	{

		y -= 1;
		pixelsMoved++;

		int alpha = color.getAlpha();
		if (alpha > 0)
		{
			Color newColor = new Color(color.getRed(), color.getGreen(),
					color.getBlue(), alpha - 3);
			color = newColor;

		}

	}

	/**
	 * Check whether or not the text is visible on the screen so it is not
	 * unnecessarily drawn
	 * 
	 * @return whether or not the text object can be seen on the screen
	 */
	public boolean isOnScreen()
	{
		if (this.x > 1024 || this.x + width < 0)
		{
			return false;
		}
		return true;
	}
}
