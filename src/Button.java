import java.awt.Image;

/**
 * Keeps track of a button's information and methods
 * 
 * @author William Xu and Tony Wu
 * @version January 1 2015
 *
 */

public class Button
{
	public int width, height;
	public int x1, y1, x2, y2,x,y;
	public boolean exists;
	public Image image, image1, image2;
	public boolean isPressed;

	/**
	 *  Pre-load the button object that can be used later when exists is set to true
	 */
	public Button()
	{
		this.exists = false;
	}

	/** Create the button with its designated location and images
	 * 
	 * @param x the x position of the button
	 * @param y the y position of the button
	 * @param image1 the normal image of the button
	 * @param image2 the pressed image of the button
	 */
	public void create(int x, int y, Image image1, Image image2)
	{
		this.image = image1;
		this.image1 = image1;
		this.image2 = image2;
		
		this.height = image.getHeight(null);
		this.width = image.getWidth(null);
		
		this.x = x;
		this.y = y;
		this.x1 = x;
		this.y1 = y + height;
		this.x2 = x + width;
		this.y2 = y;
		
		this.exists = true;
	}

	/**
	 *  Delete the button from the game
	 */
	public void remove()
	{
		this.exists = false;
	}
	
	/**
	 *  Change the image of the button and set its pressed variable to true
	 */
	public void pressed()
	{
		this.image = image2;
		this.isPressed = true;
	}
	
	/**
	 *  Change the image of the button and set its pressed variable to false
	 */
	public void released()
	{
		this.image = image1;
		this.isPressed = false;
	}
	
	/**
	 *  Scroll the button when we need to (especially the building tiles)
	 */
	public void move(int amount)
	{
		this.x1+=amount;
		this.x2+=amount;
		this.x +=amount;
	}
	
	/**
	 *  Adjust the size of the button when we need to (especially the building tiles)
	 */
	public void sizeAdjust(int x1, int y1, int x2, int y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
}
