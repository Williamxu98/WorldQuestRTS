import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

/**
 * The main program from which the code is run from, but not where most of the
 * code is stored
 * 
 * @author William Xu & Tony Wu
 * @version December 28, 2014
 *
 */

public class WorldQuestMain extends JFrame
{
	/**
	 *  Loads the icon and sets the screen as the main program is created
	 */
	public WorldQuestMain()
	{
		try
		{
			setScreen();
		}
		catch (UnsupportedAudioFileException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (LineUnavailableException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Load up the icon image (penguin image from www.iconshock.com)
		setIconImage(Toolkit.getDefaultToolkit().getImage("earthIcon.png"));
	}

	/** Sets the screen information for the game
	 * 
	 * @throws UnsupportedAudioFileException if an audio file doesn't exist
	 * @throws IOException if a different file doesn't exist
	 * @throws LineUnavailableException if a line in the text file doesn't exist
	 */
	private void setScreen() throws UnsupportedAudioFileException, IOException,
			LineUnavailableException
	{

		add(new WorldQuestEngine());

		setResizable(false);
		pack();

		setTitle("WorldQuest");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// The main program
	public static void main(String[] args) throws LineUnavailableException,
			UnsupportedAudioFileException, IOException
	{

		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				JFrame ex = new WorldQuestMain();
				ex.setVisible(true);
			}
		});
	}
}
