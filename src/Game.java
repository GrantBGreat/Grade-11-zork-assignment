import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

class Game {
	private Parser parser;
	private Room currentRoom;
	private Inventory inventory;
	private int tokens;
	private int hunger;
	private int thurst;
	// these check how much you have been told how hungery/thursty you are
	private int thurstTold;
	private int hungerTold;

	// This is a MASTER object that contains all of the rooms and is easily
	// accessible.
	// The key will be the name of the room -> no spaces (Use all caps and
	// underscore -> Great Room would have a key of GREAT_ROOM
	// In a hashmap keys are case sensitive.
	// masterRoomMap.get("GREAT_ROOM") will return the Room Object that is the Great
	private HashMap<String, Room> masterRoomMap;
	private HashMap<String, Item> masterItemMap;


	private void initItems(String fileName) throws Exception{
		Scanner itemScanner;
		masterItemMap = new HashMap<String, Item>();

		try {
			
			itemScanner = new Scanner(new File(fileName));
			while (itemScanner.hasNext()) {
				Item item = new Item();
				String itemName = itemScanner.nextLine().split(":")[1].trim();
				item.setName(itemName);
				String itemDesc = itemScanner.nextLine().split(":")[1].trim();
				item.setDescription(itemDesc);	
				Boolean openable = Boolean.valueOf(itemScanner.nextLine().split(":")[1].trim());
				item.setOpenable(openable);
				
				masterItemMap.put(itemName.toUpperCase().replaceAll(" ", "_"), item);
				
				String temp = itemScanner.nextLine();
				String itemType = temp.split(":")[0].trim();
				String name = temp.split(":")[1].trim();
				if (itemType.equals("Room"))
					masterRoomMap.get(name).getInventory().addItem(item);
				else
					masterItemMap.get(name).addItem(item);
			}
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void initRooms(String fileName) throws Exception {
		masterRoomMap = new HashMap<String, Room>();
		Scanner roomScanner;
		try {
			HashMap<String, HashMap<String, String>> exits = new HashMap<String, HashMap<String, String>>();
			roomScanner = new Scanner(new File(fileName));
			while (roomScanner.hasNext()) {
				Room room = new Room();
				// Read the Name
				String roomName = roomScanner.nextLine();
				room.setRoomName(roomName.split(":")[1].trim());
				// Read the Description
				String roomDescription = roomScanner.nextLine();
				room.setDescription(roomDescription.split(":")[1].replaceAll("<br>", "\n").trim());
				// Read the Exits
				String roomExits = roomScanner.nextLine();
				// An array of strings in the format E-RoomName
				String[] rooms = roomExits.split(":")[1].split(",");
				HashMap<String, String> temp = new HashMap<String, String>();
				for (String s : rooms) {
					temp.put(s.split("-")[0].trim(), s.split("-")[1]);
				}

				exits.put(roomName.substring(10).trim().toUpperCase().replaceAll(" ", "_"), temp);

				// This puts the room we created (Without the exits in the masterMap)
				masterRoomMap.put(roomName.toUpperCase().substring(10).trim().replaceAll(" ", "_"), room);

				// Now we better set the exits.
			}

			for (String key : masterRoomMap.keySet()) {
				Room roomTemp = masterRoomMap.get(key);
				HashMap<String, String> tempExits = exits.get(key);
				for (String s : tempExits.keySet()) {
					// s = direction
					// value is the room.

					String roomName2 = tempExits.get(s.trim());
					Room exitRoom = masterRoomMap.get(roomName2.toUpperCase().replaceAll(" ", "_"));
					roomTemp.setExit(s.trim().charAt(0), exitRoom);

				}

			}

			roomScanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the game and initialise its internal map.
	 */
	public Game() {
		tokens = 500;
		hunger = 100;
		thurst = 100;
		hungerTold = 0;
		thurstTold = 0;
		try {
			initRooms("data/Rooms.dat");	// creates the map from the rooms.dat file
			// initRooms is responsible for building/ initializing the masterRoomMap (private instance variable)
			currentRoom = masterRoomMap.get("GATE");	// the key for the masterRoomMap is the name of the room all in Upper Case (spaces replaced with _)
			inventory = new Inventory();
			initItems("data/items.dat");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parser = new Parser();
	}

	

	/**
	 * Main play routine. Loops until end of play.
	 */
	public void play() {
		printWelcome();
		// Enter the main command loop.  Here we repeatedly read commands and
		// execute them until the game is over.

		boolean finished = false;
		while (!finished) {
			Command command = parser.getCommand();
			if (processCommand(command) || checkHungerThurst())
				finished = true;
		}
	}

	private boolean checkHungerThurst() {
		if (hunger <= 0) {
			System.out.println("You died of hunger.\nYour final score was " + tokens);
			return true;
		} else if (thurst <= 0) {
			System.out.println("You died of thurst.\nYour final score was " + tokens);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Print out the opening message for the player.
	 */
	private void printWelcome() {
		System.out.println();
		System.out.println("Welcome to Zork!");
		System.out.println("Zork is a new, incredibly boring adventure game.");
		System.out.println("Type 'help' if you need help.");
		System.out.println();
		System.out.println(currentRoom.longDescription());
	}

	private boolean hasRodeRing;
	/**
	 * Given a command, process (that is: execute) the command. If this command ends
	 * the game, true is returned, otherwise false is returned.
	 */
	private boolean processCommand(Command command) {
		if (command.isUnknown()) {
			System.out.println("I don't know what you mean...");
			return false;
		}
		String commandWord = command.getCommandWord();
		if (commandWord.equals("help"))
			printHelp();
		else if (commandWord.equals("go"))
			goRoom(command);
		else if (commandWord.equals("leave")) {
			if (command.hasSecondWord())
				System.out.println("leave what?");
			else
				// Check if room is Gate
				if (currentRoom.getRoomName().equals("Gate")) {
					System.out.println("Thank you for attending. Hope to see you soon!\nYour score was: " + tokens);
					return true; // signal that we want to quit
				} else {
					System.out.println("You need to be at the Gate to leave the park. The Gate is at the far south end of the park.");
				}
		} else if (commandWord.equals("eat")) {
			eat(command.getSecondWord());
		} else if (commandWord.equals("jump")) {
			return jump();
		} else if (commandWord.equals("take")) {
			if (!command.hasSecondWord())
				System.out.println("Take what?");
			else
				takeItem(command.getSecondWord());
		} else if (commandWord.equals("drop")) {
			if (!command.hasSecondWord())
				System.out.println("Drop what?");
			else
				dropItem(command.getSecondWord());
		} else if (commandWord.equals("i") || commandWord.equals("inventory")) {
			System.out.println("You have " + tokens + " tokens.");
			System.out.println(inventory);
		} else if (commandWord.equals("tokens")) {
			System.out.println("You have " + tokens + " tokens.");
		} else if (commandWord.equals("open")) {
			if (!command.hasSecondWord())
				System.out.println("Open what?");
			else
				openItem(command.getSecondWord());
		} else if (commandWord.equals("ride")) {
			// check what ride you are at
			if (currentRoom.getRoomName().equals("The Kraken") || currentRoom.getRoomName().equals("The Logger") || currentRoom.getRoomName().equals("The Slippyest Slides")) {
				// add tokens here
			} else if (currentRoom.getRoomName().equals("The Ring")) {
				// you find 220 tokens on the first go, but die if you try again.
				if (!hasRodeRing) {
					// dont die
					System.out.println("You got lucky even though you didnt pay attention to the sign, I wouldnt recomend trying again. You did however find 220 tokens by the exit of the ride!");
					tokens += 220;
					hasRodeRing = true;
				}else{
					System.out.println("You should have listend to the sign. The Coaster de-railed and you died. The score when you died was: " + tokens);
					//return true;
				}
			} else if (currentRoom.getRoomName().equals("Fortune Teller") || currentRoom.getRoomName().equals("Defend The Park") || currentRoom.getRoomName().equals("You Lose Casino") || currentRoom.getRoomName().equals("Pool Party")) {
				System.out.println("There is nothing to ride here, but you can \'play\'");
			} else { // if you are not at a ride
				System.out.println("You are not at a ride.");
			}
		}
		return false;
	}

	private void openItem(String itemName) {
		Item item = inventory.contains(itemName);
		
		if(item != null) {
			System.out.println(item.displayContents());
		}else {
			System.out.println("What is it that you think you have but do not.");
		}
		
	}

	private void takeItem(String itemName) {
		Inventory temp = currentRoom.getInventory();
		
		Item item = temp.removeItem(itemName);
		
		if (item != null) {
			if (inventory.addItem(item)) {
				System.out.println("You have taken the " + itemName);
				
				if (currentRoom.getRoomName().equals("Hallway") &&  itemName.equals("ball")) {
					currentRoom = masterRoomMap.get("ATTIC");
					System.out.println("You seem to be lying on the floor all confused. It seems you have been here for a while.\n");
					System.out.println(currentRoom.longDescription());
				}
			}else {
				System.out.println("You were unable to take the " + itemName);
			}
		}else {
			System.out.println("There is no " + itemName + " here.");
		}
	}
	
	private void dropItem(String itemName) {
		Item item = inventory.removeItem(itemName);
		
		if (item != null) {
			if (currentRoom.getInventory().addItem(item)) {
				System.out.println("You have dropped the " + itemName);
			}else {
				System.out.println("You were unable to drop the " + itemName);
			}
		}else {
			System.out.println("You are not carrying a " + itemName + ".");
		}
	}

	private void eat(String secondWord) {
		if (secondWord.equals("steak"))
			System.out.println("YUMMY");
		else if (secondWord.equals("bread"))
			System.out.println("I don't eat carbs...");
		else 
			System.out.println("You are the " + secondWord);
		
	}

	private void sit() {
		System.out.println("You are now sitting. You lazy excuse for a person.");
		
	}

	private boolean jump() {
		System.out.println("You jumped. Ouch you fell. You fell hard. Really hard. You are getting sleepy. Very sleepy! Yuo are dead!");
		return true;
	}

// implementations of user commands:
	/**
	 * Print out some help information. Here we print some stupid, cryptic message
	 * and a list of the command words.
	 */
	private void printHelp() {
		System.out.println("You are lost. You are alone. You wander");
		System.out.println("around at Monash Uni, Peninsula Campus.");
		System.out.println();
		System.out.println("Your command words are:");
		parser.showCommands();
	}

	/**
	 * Try to go to one direction. If there is an exit, enter the new room,
	 * otherwise print an error message.
	 */
	private void goRoom(Command command) {
		if (!command.hasSecondWord() && ("udeswn".indexOf(command.getCommandWord()) < 0)) {
			// if there is no second word, we don't know where to go...
			System.out.println("Go where?");
			return;
		}

		String direction = command.getSecondWord();
		if ("udeswn".indexOf(command.getCommandWord()) > -1) {
			direction = command.getCommandWord();
			if (direction.equals("u"))
				direction = "up";
			else if (direction.equals("d"))
				direction = "down";
			else if (direction.equals("e"))
				direction = "east";
			else if (direction.equals("w"))
				direction = "west";
			else if (direction.equals("n"))
				direction = "north";
			else if (direction.equals("s"))
				direction = "south";
		}
		
// Try to leave current room.
		Room nextRoom = currentRoom.nextRoom(direction);
		if (nextRoom == null)
			System.out.println("You cannot go that way.");
		else {
			currentRoom = nextRoom;
			System.out.println(currentRoom.longDescription());
			lowerHungerThurst();
		}
	}

	private void lowerHungerThurst() {
		// lower thurst by a random number from 3 to 7
		int thurstInterval = (int)(Math.random() * 5) + 3;
		thurst -= thurstInterval;

		if (thurst <= 40 && thurstTold == 0) {
			thurstTold = 1;
			System.out.println("You're starting to feel thursty");
		} else if (thurst <= 17 && thurstTold > 0) {
			thurstTold = 2;
			System.out.println("You're getting so thursty it hurts");
		} else {
			thurstTold = 0;
		}


		// lower hunger by a random number from 2 to 5
		int hungerInterval = (int)(Math.random() * 4) + 2;
		thurst -= thurstInterval;

		if (thurst <= 40 && hungerTold == 0) {
			hungerTold = 1;
			System.out.println("You're starting to feel hungery");
		} else if (thurst <= 15 && hungerTold > 0) {
			hungerTold = 2;
			System.out.println("You're getting so hungery it hurts");
		} else {
			hungerTold = 0;
		}
	}
}
