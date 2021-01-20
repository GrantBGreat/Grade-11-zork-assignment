public class Item {
	private String name;
	private String description;
	private Inventory items;
	private boolean isOpenable;
	
	
	public Item() {
		
	}
	
	// returns if an item is openable or not
	public boolean isOpenable() {
		return isOpenable;
	}

	// set an inv for an item if its openable
	public void setOpenable(boolean isOpenable) {
		this.isOpenable = isOpenable;
		if(isOpenable)
			this.items = new Inventory(); 
	}

	//set a name for an item
	public void setName(String name) {
		this.name = name;
	}

	// set a description for an item
	public void setDescription(String description) {
		this.description = description;
	}

	// create an item with input components
	public Item(String name, String description, boolean isOpenable) {
		super();
		this.name = name;
		this.description = description;
		this.isOpenable = isOpenable;
		if(isOpenable)
			this.items = new Inventory(); 
	}
	
	// same as the last method but without being openable
	public Item(String name, String description) {
		super();
		this.name = name;
		this.description = description;
		this.isOpenable = false;
	}

	// get the name of an item
	public String getName() {
		return name;
	}

	// get the description of an item
	public String getDescription() {
		return description;
	}
	
	// get the contents of an openable item
	public Inventory getContents() {
		if (!isOpenable) return null;
		return items;
	}
	
	// add an item to an openable item's inv
	public boolean addItem(Item item) {
		if (!isOpenable) return false;
		return items.addItem(item);
	}
	
	// remove an item from an openable item's inv
	public Item removeItem(String item) {
		if (!isOpenable) return null;
		return items.removeItem(item);
	}
	
	// display the inv of an item
	public String displayContents() {
		if (!isOpenable) return null;

		if (items.isEmpty()) {
			return "The " + name + " is empty.";
		}

		return "The " + name + " contains:\n" + items;
	}

	// returns the item's inv
	public Inventory getInventory() {
		return items;
	}
}
