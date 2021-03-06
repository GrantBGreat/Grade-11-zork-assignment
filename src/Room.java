import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;

public class Room {
  private String roomName;
  private String description;
  private HashMap<String, Room> exits; // stores exits of this room.
  private Inventory inventory;

  /**
   * Create a room described "description". Initially, it has no exits.
   * "description" is something like "a kitchen" or "an open court yard".
   */
  public Room(String description) {
    this.description = description;
    exits = new HashMap<String, Room>();
    inventory = new Inventory();
  }

  public Room() {
    // default constructor.
    roomName = "DEFAULT ROOM";
    description = "DEFAULT DESCRIPTION";
    inventory = new Inventory();
    exits = new HashMap<String, Room>();
  }

  public void setExit(char direction, Room r) throws Exception {
    String dir = "";
    switch (direction) {
      case 'E':
        dir = "east";
        break;
      case 'W':
        dir = "west";
        break;
      case 'S':
        dir = "south";
        break;
      case 'N':
        dir = "north";
        break;
      case 'U':
        dir = "up";
        break;
      case 'D':
        dir = "down";
        break;
      default:
        throw new Exception("Invalid Direction");
    }

    exits.put(dir, r);
  }

  /**
   * Define the exits of this room. Every direction either leads to another room
   * or is null (no exit there).
   */
  public void setExits(Room north, Room east, Room south, Room west, Room up, Room down) {
    if (north != null)
      exits.put("north", north);
    if (east != null)
      exits.put("east", east);
    if (south != null)
      exits.put("south", south);
    if (west != null)
      exits.put("west", west);
    if (up != null)
      exits.put("up", up);
    if (up != null)
      exits.put("down", down);
  }

  /**
   * Return the description of the room (the one that was defined in the
   * constructor).
   */
  public String shortDescription() {
    return "Room: " + roomName + "\n\n" + description;
  }

  /**
   * Return a long description of this room, on the form: You are in the kitchen.
   * Exits: north west
   */
  public String longDescription() {

    return "Room: " + roomName + "\n\n" + description + "\n" + exitString();
  }

  /**
   * Return a string describing the room's exits, for example "Exits: north west
   * ".
   */
  private String exitString() {
    String returnString = "Exits:";
    Set keys = exits.keySet();
    for (Iterator iter = keys.iterator(); iter.hasNext();)
      returnString += " " + iter.next();
    return returnString;
  }

  /**
   * Return the room that is reached if we go from this room in direction
   * "direction". If there is no room in that direction, return null.
   */
  public Room nextRoom(String direction) {
    return (Room) exits.get(direction);
  }

  // returns the rooms name
  public String getRoomName() {
    return roomName;
  }

  // set the rooms name
  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }

  // get the description of a room
  public String getDescription() {
    return description;
  }

  // set the description of a room
  public void setDescription(String description) {
    this.description = description;
  }

  // get the inv of items in a room
  public Inventory getInventory() {
    return inventory;
  }
}
