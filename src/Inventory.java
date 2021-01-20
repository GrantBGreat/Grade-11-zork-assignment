import java.util.ArrayList;

public class Inventory {
    private ArrayList<Item> items;

    // create the inv
    public Inventory() {
        items = new ArrayList<Item>();
    }

    // add an item to the inv and return true if the it was added and false if the item is already in the inv
    public boolean addItem(Item item) {
        if (items.contains(item))
            return false;

        return items.add(item);
    }

    // remove an item from the inv and return it
    public Item removeItem(String name) {
        for (int i = 0; i<items.size(); i++) {
            if (name.equals(items.get(i).getName())) {
                return items.remove(i);
            }
        }

        return null;
    }

    // remove all in the inv
    public ArrayList<Item> removeAll() {
        ArrayList<Item> temp = new ArrayList<Item>();

        for (int i = 0; i < items.size(); i++) {
            temp.add(items.get(i));
        }

        items.clear();
        return temp;
    }

    // add a list to the inv
    public void addAll(ArrayList<Item> temp) {
        items.addAll(temp);
    }

    // check if an item contains something
    public Item contains(String name) {
        for (int i = 0; i<items.size(); i++) {
            if (name.equals(items.get(i).getName())) {
                return items.get(i);
            }
        }

        return null;
    }

    // returns the inv
    public Inventory getInventory() {
        return this;
    }

    // returns a string of the inv
    public String toString() {
        String msg = "";
        for (Item i : items) {
            msg += i.getName() + "\n";
        }
        return msg;
    }

    // check if the inv is empty
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
