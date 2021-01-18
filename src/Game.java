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
		// this is the hunger and thurst %'s of the charicter
		hunger = 100;
		thurst = 100;
		// these are the amounts of times you have been told you are hungery/thursty encoded as an int
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
		System.out.println("Welcome to Zork Themepark!");
		System.out.println("Zork Themepark is a new, incredibly boring adventure game. In this game you run around going on rides and playing activitys in order to earn tokens. Your Goal is to have the largest amount of tokens when the game ends.");
		System.out.println("Once you start, you can type 'help' to get a list of valid commands.");
		System.out.println("Type \'ready\' when you are ready to play.");
		System.out.println();

		String ready = getInput();
		boolean isDone = false;
		while (!isDone) {
			if (ready.toLowerCase().equals("ready")) {
				isDone = true;
				continue;
			} else {
				System.out.println("I'm waiting.");
				ready = getInput();
			}
		}

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
			// Check if room is Gate
			if (currentRoom.getRoomName().equals("Gate")) {
				System.out.println("Thank you for attending. Hope to see you soon!\nYour score was: " + tokens);
				return true; // signal that we want to quit
			} else {
				System.out.println("You need to be at the Gate to leave the park. The Gate is at the far south end of the park.");
			}
		} else if (commandWord.equals("eat")) {
			return eat(command.getSecondWord());
		} else if (commandWord.equals("drink")) {
			drink(command.getSecondWord());
		} else if (commandWord.equals("jump")) {
			return jump();
		} else if (commandWord.equals("take")) {
			if (!command.hasSecondWord())
				System.out.println("Take what?");
			else
				takeItem(command.getSecondWord());
		} else if (commandWord.equals("buy")) {
			if (!command.hasSecondWord())
				System.out.println("buy what?");
			else
				buyItem(command.getSecondWord());
		} else if (commandWord.equals("drop")) {
			if (!command.hasSecondWord())
				System.out.println("Drop what?");
			else
				dropItem(command.getSecondWord());
		} else if (commandWord.equals("inventory")) {
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
				int rand = (int)(Math.random() * 4) + 1;
				if (rand == 1) { // 1 in 4 chance of this happening
					tokens += 30;
					System.out.println("It cost 20 tokens to ride this, but you made such a funny face that the operator gave you 50 tokens in return. That means you gained 30 tokens!");
				}else{
					tokens -= 20;
					System.out.println("Well that was fun");
				}
			} else if (currentRoom.getRoomName().equals("The Ring")) {
				// you find 220 tokens on the first go, but die if you try again.
				if (!hasRodeRing) {
					// dont die
					System.out.println("You got lucky even though you didnt pay attention to the sign, I wouldnt recomend trying again. You did however find 220 tokens by the exit of the ride!");
					tokens += 220;
					hasRodeRing = true;
				}else{
					System.out.println("You should have listend to the sign. The Coaster de-railed and you died. The score when you died was: " + tokens);
					return true;
				}
			} else if (currentRoom.getRoomName().equals("Fortune Teller") || currentRoom.getRoomName().equals("Defend The Park") || currentRoom.getRoomName().equals("You Lose Casino") || currentRoom.getRoomName().equals("Pool Party")) {
				System.out.println("There is nothing to ride here, but you can \'play\' here.");
			} else { // if you are not at a ride
				System.out.println("You are not at a ride.");
			}
		}else if (commandWord.equals("play")) {
			if (currentRoom.getRoomName().equals("Fortune Teller")) {
				System.out.println("The fortune teller told you:\nYour hunger is at %" + hunger + ".\nYour thurst is at %" + thurst + ".\nYou seem like an unlucky person.");
			} else if (currentRoom.getRoomName().equals("Pool Party")) {
				lowerHungerThurst();
				System.out.println("You swam. It was fun (ish).");
			} else if (currentRoom.getRoomName().equals("You Lose Casino")) {
				boolean done = false;
				while (!done) {
					// find out what they want to bet on
					System.out.println("What would you like to bet on? (a, b, or c)\na) Horse racing\nb) BlackJack\nc) Poker");
					String betType = getInput();
					if (!betType.equals("a") && !betType.equals("b") && !betType.equals("c")){ // check if its valid
						System.out.println("Please enter ether \"a\", \"b\", or \"c\".");
						continue;
					}

					// find out how much they would like to bet
					System.out.println("How much would you like to bet?  --  You have " + tokens + " tokens.");
					boolean valid = false;
					int bet = 0;
					while (!valid) {
						String betString = getInput();
						try {
							bet = Integer.parseInt(betString);
							if (bet > tokens) {
								System.out.println("You cannot afford this bet because you only have " + tokens + " tokens.");
								continue;
							}
							valid = true;
							
						}catch(NumberFormatException e) {
							System.out.println("Please enter a valid integer.");
						}
					}

					tokens -= bet;
					// find out what they earned/lost
					if (betType.equals("a")) {
						int win = (int)(Math.random() * 4) + 1;
						if (win == 1) {
							int earned = (int)(bet * ((Math.random() * 3) + 1));
							tokens += earned;
							System.out.println("The team you bet on won the horse race, you won " + earned + " tokens!");
						}else{
							System.out.println("The team you bet on lost the horse race, you now have " + tokens + " tokens.");
						}
					}else if (betType.equals("b")){
						int win = (int)(Math.random() * 2) + 1;
						if (win == 1) {
							int earned = (int)(bet * (Math.random() + 1));
							tokens += earned;
							System.out.println("You won the Blackjack. You got " + earned + " tokens!");
						}else{
							System.out.println("You lost the Blackjack. You now have " + tokens + " tokens.");
						}
					}else {
						int win = (int)(Math.random() * 3) + 1;
						if (win == 1) {
							int earned = (int)(bet * ((Math.random() * 2) + 1));
							tokens += earned;
							System.out.println("You won Poker. You got " + earned + " tokens!");
						}else{
							System.out.println("You lost Poker. You now have " + tokens + " tokens.");
						}
					}

					System.out.println("Would you like to play again?");
					boolean loop = true;
					while (loop) {
						String yn = getInput();
						if (yn.equals("yes") || yn.equals("y")) {
							System.out.println();
							loop = false;
						} else if (yn.equals("no") || yn.equals("n")) {
							System.out.println("Have a nice day!");
							done = true;
							loop = false;
						} else {
							System.out.println("yes or no?");
						}
					}
				}
			} else if (currentRoom.getRoomName().equals("Defend The Park")) {
				int changeRand = (int)(Math.random() * 3);

				if (changeRand >= 1) {
					tokens += 50;
					System.out.println("You won 50 tokens!");
				} else {
					tokens -= 50;
					System.out.println("You lost 50 tokens!");
				}
			} else if (currentRoom.getRoomName().equals("The Kraken") || currentRoom.getRoomName().equals("The Logger") || currentRoom.getRoomName().equals("The Slippyest Slides") || currentRoom.getRoomName().equals("The Ring")) {
				System.out.println("There is nothing to play here, but you can \'ride\' here.");
			} else {
				System.out.println("There is nothing to play here.");
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
		
		if (currentRoom.getRoomName().equals("Bobby's Burger Shop") || currentRoom.getRoomName().equals("Just Juice") || currentRoom.getRoomName().equals("Papa's Pizzaria")) {
			System.out.println("You have to \'buy\' items here.");
			return;
		}

		if (item != null) {
			if (inventory.addItem(item)) {
				System.out.println("You have taken the " + itemName);
			}else {
				System.out.println("You were unable to take the " + itemName);
			}
		}else {
			System.out.println("There is no " + itemName + " here.");
		}
	}

	private void buyItem(String itemName) {
		if (!currentRoom.getRoomName().equals("Bobby's Burger Shop") && !currentRoom.getRoomName().equals("Just Juice") && !currentRoom.getRoomName().equals("Papa's Pizzaria")) {
			System.out.println("You cannot \'buy\' items here.");
			return;
		}

		Inventory temp = currentRoom.getInventory();

		if (itemName.equals("burger")) {
			Item item = temp.removeItem("burgerbag");
			if (item != null) {
				if (inventory.addItem(item)) {
					System.out.println("You have bought the " + itemName + " it came in a openable \'burgerbag\'");
					tokens -= 15;
				}else {
					System.out.println("You were unable to buy the " + itemName + ". It is likely sold out.");
				}
			}else {
				System.out.println("There is no " + itemName + " here.");
			}
		} else if (itemName.equals("pizza")) {
			Item item = temp.removeItem("pizzabox");
			if (item != null) {
				if (inventory.addItem(item)) {
					System.out.println("You have bought the " + itemName + " it came in a openable \'pizzabox\'");
					tokens -= 10;
				}else {
					System.out.println("You were unable to buy the " + itemName + ". It is likely sold out.");
				}
			}else {
				System.out.println("There is no " + itemName + " here.");
			}
		} else if (itemName.equals("juice")) {
			Item item = temp.removeItem("juice");
			if (item != null) {
				if (inventory.addItem(item)) {
					System.out.println("You have bought the " + itemName);
					tokens -= 5;
				}else {
					System.out.println("You were unable to buy the " + itemName + ". It is likely sold out.");
				}
			}else {
				System.out.println("There is no " + itemName + " here.");
			}
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

	private boolean eat(String secondWord) {
		// get kicked out at the pool
		if (currentRoom.getRoomName().equals("Pool Party")) {
			System.out.println("You got kicked out for eating at the pool.\nThe score when you lost was: " + tokens);
			return true;
		}

		if (secondWord.equals("pizza")) {
			System.out.println("You ate the pizza.");
			hunger += 75;
			if (hunger > 100)
				hunger = 100;
		} else if (secondWord.equals("burger")) {
			System.out.println("You ate the burger");
			hunger = 100;
		} else {
			System.out.println("You cannot eat a " + secondWord);
		}

		return false;
	}

	private void drink(String secondWord) {
		if (secondWord.equals("juice")) {
			Item item = inventory.removeItem(secondWord);
			if (item != null) {
				if (thurst == 100) {
					System.out.println("You are not thursty.");
				} else {
					System.out.println("You drank the juice.");
					thurst = 100;
				}
			}else {
				System.out.println("You do not have any juice on you.");
			}
		} else {
			System.out.println("You cannot drink that.");
		}
	}

	private boolean jump() {
		// all haunted house rooms you die, otherwise nothing happens
		if (currentRoom.getRoomName().equals("Haunted House Enterance") || currentRoom.getRoomName().equals("Bottom of Haunted House Stairwell") || currentRoom.getRoomName().equals("Top of Haunted House Stairwell") || currentRoom.getRoomName().equals("Top floor hall") || currentRoom.getRoomName().equals("Top floor room") || currentRoom.getRoomName().equals("Haunted House Room")) {
			System.out.println("You jumped and fell through the floorboards. You died.\nYour final score was " + tokens);
			return true;
		} else {
			System.out.println("You jumped. Why? Why not?\nSome people are staring at you");
			return false;
		}
	}

// implementations of user commands:
	/**
	 * Print out some help information. Here we print some stupid, cryptic message
	 * and a list of the command words.
	 */
	private void printHelp() {
		System.out.println("So much to do as you walk");
		System.out.println("around at Zork Themepark");
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
		hunger -= hungerInterval;

		if (hunger <= 40 && hungerTold == 0) {
			hungerTold = 1;
			System.out.println("You're starting to feel hungery");
		} else if (hunger <= 15 && hungerTold > 0) {
			hungerTold = 2;
			System.out.println("You're getting so hungery it hurts");
		} else {
			hungerTold = 0;
		}
	}

	private String getInput() {
		Scanner in = new Scanner(System.in);
		System.out.print("> ");
		return in.nextLine();
	}
}