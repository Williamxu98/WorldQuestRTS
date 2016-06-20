import java.awt.Color;
import java.awt.Image;

/**
 * Keeps track of a building's information and methods
 * 
 * @author William Xu and Tony Wu
 * @version January 1 2015
 *
 */
public class Building
{
	public int width, height;
	public int x, y;
	public String type;
	public boolean exists;
	public Image image;
	public static WorldQuestEngine world;
	public int towerVision;
	public String name;
	public int goldCost;
	public int lumberCost;
	public int stoneCost;
	public int timeElapsed;
	public int noOfResidents;
	public int maxNoOfResidents;
	public int stage;
	public int resourcesStored;
	public int maxResources;

	public boolean isFull;

	/**
	 * Pre-load the button object that can be used later when exists is set to
	 * true
	 */
	public Building()
	{
		this.exists = false;
	}

	/**
	 * Make sure to have the building refer to the engine and be able to use all
	 * of its variables
	 * 
	 * @param newWorld the engine object
	 */
	public static void setWorld(WorldQuestEngine newWorld)
	{
		world = newWorld;
	}

	/**
	 * Create a building object with its designated type, location, and image
	 * 
	 * @param type the type of building that is created
	 * @param x the x coordinate of the building
	 * @param y the y coordinate of the building
	 * @param image the image of the building
	 */
	public void create(String type, int x, int y, Image image)
	{

		this.image = image;

		this.isFull = false;
		this.resourcesStored = 0;
		this.maxResources = 1;

		this.height = image.getHeight(null);
		this.width = image.getWidth(null);

		this.type = type;
		this.x = x - width / 2;

		this.y = y - height;
		this.exists = true;
		this.timeElapsed = 0;

		this.noOfResidents = -1;
		this.maxNoOfResidents = -1;

		// Perform certain tasks for each building type
		if (type.equals("watchTower"))
		{

			// Extend the world vision
			this.name = "Watch Tower";
			this.towerVision = 2500;
			if (this.x + towerVision > world.viewLimitx2)
			{
				world.viewLimitx2 = x + towerVision;
			}

			if (this.x - towerVision < world.viewLimitx1)
			{
				world.viewLimitx1 = x - towerVision;
			}
			this.goldCost = 150;
			this.lumberCost = 50;
			this.stoneCost = 200;
		}
		else if (type.equals("townHall"))
		{
			this.name = "Town Hall";
			this.towerVision = 1500;
			world.viewLimitx2 = x + this.towerVision;
			world.viewLimitx1 = x - this.towerVision;

			this.goldCost = 500;
			this.lumberCost = 250;
			this.stoneCost = 250;
		}
		else if (type.equals("dirtHouse"))
		{
			this.name = "Dirt House";
			this.goldCost = 75;
			this.noOfResidents = 0;
			this.maxNoOfResidents = 5;
		}
		else if (type.equals("woodenHouse"))
		{
			this.name = "Wooden House";
			this.goldCost = 100;
			this.lumberCost = 25;
			this.noOfResidents = 0;
			this.maxNoOfResidents = 10;
		}

		else if (type.equals("wheatField"))
		{
			this.name = "Wheat Field";
			this.goldCost = 50;
			this.lumberCost = 10;
			this.stage = 1;
			this.y = y - height + 22;
		}
		else if (type.equals("lumberMill"))
		{
			this.name = "Lumber Mill";
			this.goldCost = 250;
			this.lumberCost = 50;
			this.maxResources = 250;

		}
		else if (type.equals("stoneMine"))
		{
			this.name = "Stone Mine";
			this.goldCost = 250;
			this.lumberCost = 50;
			this.maxResources = 250;
		}
		else if (type.equals("tavern"))
		{
			this.name = "Tavern";
			this.goldCost = 150;
			this.lumberCost = 50;
			world.currentHappiness += 1000;
			world.totalHappiness += 1000;
		}

		// Assign a random number to each building except the town hall
		if (!(type.equals("townHall")))
		{
			int randomCode = (int) (Math.random() * 1000);
			this.name += " " + randomCode;
		}

		// Decrease the player's resources in the world
		world.currentGold -= goldCost;
		world.currentLumber -= lumberCost;
		world.currentStone -= stoneCost;

	}

	/**
	 * Keep track of the time each building has had since its collection and
	 * construction
	 */
	public void cycle()
	{
		timeElapsed++;

		// Generate taxes or have citizens move into the buildings when the time
		// is right
		if (type.equals("woodenHouse") || type.equals("dirtHouse"))
		{
			int nextLine = 0;
			if (timeElapsed % (world.DELAY * 24) == 0)
			{

				world.currentGold += (int) (world.incomePerCitizen
						* noOfResidents
						* (1.0 * world.currentHappiness / world.totalHappiness) + 0.5);

				if (!(x + 1000 < 0 || x - 1300 > world.SCREEN_WIDTH))
				{
					world.createText(
							"Taxed "
									+ (int) (world.incomePerCitizen
											* noOfResidents
											* (1.0 * world.currentHappiness / world.totalHappiness)
											+ 0.5) + " Gold", x + width / 2,
							y - 50,
							world.arial24Bold, Color.yellow, true,
							90, 0,
							true);
				}
				nextLine += 25;
			}

			if (timeElapsed % (world.DELAY * 6) == 0
					&& noOfResidents < maxNoOfResidents)
			{
				noOfResidents++;
				world.currentPopulation++;
				world.totalHappiness++;

				if (!(x + 1000 < 0 || x - 1300 > world.SCREEN_WIDTH))
				{
					world.createText("A citizen has moved in", x + width / 2,
							y - 50 - nextLine,
							world.arial24Bold, Color.black, true,
							90, 0,
							true);
				}

				// Reduce the percentage of happiness by increasing the
				// denominator only
				nextLine += 25;
			}

			// Have the population consume food every three seconds
			if (timeElapsed % (world.DELAY * 3) == 0)
			{
				if (world.currentFood - noOfResidents > 0)
				{
					world.currentFood -= noOfResidents;
				}
				else if (world.currentFood > 0)
				{
					world.currentFood = 0;
				}
			}

			nextLine = 0;
		}

		// Make sure the resources in the building does not exceed the max it
		// can store
		if (resourcesStored >= maxResources)
		{
			isFull = true;
		}
		else
		{
			// Have the stone mine and lumber mill collect its resources at a
			// fixed rate (6 seconds)
			isFull = false;
			if (type.equals("stoneMine"))
			{
				if (timeElapsed % (world.DELAY * 6) == 0)
				{
					resourcesStored += 50;
				}
			}
			else if (type.equals("lumberMill"))
			{

				if (timeElapsed % (world.DELAY * 6) == 0)
				{
					resourcesStored += 50;
				}

			}
		}

		// Change the stages in the production of the crops in a wheatfield
		if (type.equals("wheatField"))
		{
			if (timeElapsed % (world.DELAY * 12) == 0 && stage < 4)
			{
				stage++;
			}

			if (stage == 1)
			{
				image = world.images[12];
			}
			else if (stage == 2)
			{
				image = world.images[15];
			}
			else if (stage == 3)
			{
				image = world.images[16];
			}
			else if (stage == 4)
			{
				image = world.images[17];
				resourcesStored = 100;
			}
		}
	}

	/**
	 * Execute certain actions when the player demolishes a building
	 */
	public void demolish()
	{
		this.exists = false;

		// Return the salvaged resources and decrease the population
		world.currentLumber += lumberCost / 2;
		world.currentStone += stoneCost / 2;
		world.currentPopulation -= noOfResidents;

		// Set the vertical distance between text so they don't overlap
		int nextLine = 0;

		// Display the salvaging
		if (lumberCost > 0)
		{
			world.createText(lumberCost / 2 + " lumber salvaged",
					x + width / 2, y + height / 2, world.arial24Bold,
					new Color(139, 69, 19), true, 90, 0, true);
			nextLine += 25;
		}

		if (stoneCost > 0)
		{
			world.createText(stoneCost / 2 + " stone salvaged", x + width / 2,
					y + height / 2 + nextLine, world.arial24Bold,
					Color.darkGray, true, 90, 0, true);
			nextLine += 25;
		}

		if (noOfResidents > 0)
		{
			world.createText(noOfResidents + " citizens have left the town", x
					+ width / 2,
					y + height / 2 + nextLine, world.arial24Bold,
					new Color(0, 102, 0), true, 90, 0, true);
			nextLine += 25;
		}

		if (stoneCost == 0 && lumberCost == 0)
		{
			world.createText("No Salvage Value", x + width / 2, y + height / 2,
					world.arial24Bold, Color.red, true, 90, 0, true);
		}

		// Reset the cost of the building
		goldCost = 0;
		lumberCost = 0;
		stoneCost = 0;

		// Decrease the happiness if taverns are destroyed
		if (this.type.equals("tavern"))
		{
			world.currentHappiness -= 1000;
			world.totalHappiness -= 1000;
			world.createText("Citizens are disappointed", x + width / 2,
					y + height / 2 + nextLine, world.arial24Bold,
					new Color(0, 102, 0), true, 90, 0, true);

		}

		// Decrease the view limit when watchtowers are destroyed
		else if (this.type.equals("watchTower"))
		{
			if (this.x + towerVision == world.viewLimitx2)
			{
				int secondRightMost = 0;
				boolean secondRightMostSet = false;
				int buildingIndex = 0;
				while (!secondRightMostSet
						&& buildingIndex < world.buildings.length)
				{
					if (world.buildings[buildingIndex].exists
							&& (world.buildings[buildingIndex].type
									.equals("watchTower") || world.buildings[buildingIndex].type
									.equals("townHall")))
					{
						secondRightMostSet = true;
						secondRightMost = world.buildings[buildingIndex].x
								+ world.buildings[buildingIndex].towerVision;
					}
					buildingIndex++;
				}

				if (secondRightMostSet)
				{
					for (int index = buildingIndex; index < world.buildings.length; index++)
					{
						if (world.buildings[index].exists
								&& (world.buildings[index].type
										.equals("watchTower") || world.buildings[buildingIndex]
										.equals("townHall"))
								&& world.buildings[index].x
										+ world.buildings[index].towerVision > secondRightMost)
						{
							secondRightMost = world.buildings[index].x
									+ world.buildings[index].towerVision;
						}
					}

					world.viewLimitx2 = secondRightMost;
				}

			}
			else if (this.x / 2 - towerVision == world.viewLimitx1)
			{
				int secondLeftMost = 0;
				boolean secondLeftMostSet = false;
				int buildingIndex = 0;
				while (!secondLeftMostSet
						&& buildingIndex < world.buildings.length)
				{
					if (world.buildings[buildingIndex].exists
							&& (world.buildings[buildingIndex].type == "watchTower" || world.buildings[buildingIndex].type == "townHall"))
					{
						secondLeftMostSet = true;
						secondLeftMost = world.buildings[buildingIndex].x
								- world.buildings[buildingIndex].towerVision;
					}
					buildingIndex++;
				}

				if (secondLeftMostSet)
				{
					for (int index = buildingIndex; index < world.buildings.length; index++)
					{
						if (world.buildings[index].exists
								&& (world.buildings[index].type == "watchTower" || world.buildings[buildingIndex].type == "townHall")
								&& world.buildings[index].x
										- world.buildings[index].towerVision < secondLeftMost)
						{
							secondLeftMost = world.buildings[index].x
									- world.buildings[index].towerVision;
						}
					}
					world.viewLimitx1 = secondLeftMost;
				}

			}
		}
	}

	/**
	 * Collect the resources and add them to the inventory of the player when a
	 * building is pressed or the collect all button is pressed
	 */
	public void collect()
	{
		if (resourcesStored > 0)
		{
			
			// Make the player pay a fee for collecting resources
			if (world.currentGold >= (int) (1.0 * resourcesStored / 4 + 0.5))
			{

				world.currentGold -= (int) (1.0 * resourcesStored / 4 + 0.5);

				if (!(x + 1000 < 0 || x - 1300 > world.SCREEN_WIDTH))
				{
					world.createText((int) (1.0 * resourcesStored / 4 + 0.5)
							+ " Gold spent",
							x + width / 2,
							y - 20,
							world.arial24Bold, Color.yellow, true,
							90, 0,
							true);
				}

				if (this.type.equals("wheatField"))
				{
					if (stage == 4)
					{
						stage = 1;
						world.harvestAmount += resourcesStored;
						if (!(x + 1000 < 0 || x - 1300 > world.SCREEN_WIDTH))
						{
							world.createText("Harvested " + resourcesStored
									+ " Food",
									x
											+ width / 2,
									y - 50,
									world.arial24Bold, Color.ORANGE, true,
									90, 0,
									true);
						}

						// Increase the happiness percentage
						world.currentHappiness += 50;
						world.totalHappiness += 50;

						resourcesStored = 0;
					}
				}
				else if (this.type.equals("stoneMine"))
				{
					world.harvestAmount += resourcesStored;

					if (!(x + 1000 < 0 || x - 1300 > world.SCREEN_WIDTH))
					{
						world.createText("Collected " + resourcesStored
								+ " Stone", x
								+ width / 2,
								y - 50,
								world.arial24Bold, Color.darkGray, true,
								90, 0,
								true);
					}

					resourcesStored = 0;
					timeElapsed = 0;
				}
				else if (this.type.equals("lumberMill"))
				{
					world.harvestAmount += resourcesStored;

					if (!(x + 1000 < 0 || x - 1300 > world.SCREEN_WIDTH))
					{
						world.createText("Collected " + resourcesStored
								+ " Lumber", x
								+ width
								/ 2,
								y - 50,
								world.arial24Bold, new Color(139, 69, 19),
								true,
								90, 0,
								true);
					}

					resourcesStored = 0;
					timeElapsed = 0;
				}
			}
			else
			{
				// Prompt the player on the reason why a building cannot be collected from
				world.displayMessage("You have not enough gold to pay for these resources!");
			}

		}
	}
}
