import java.util.ArrayList;

public class Inventory {
    private ArrayList<Item> items;

    public Inventory() {
        items = new ArrayList<Item>();
    }

    public boolean addItem(Item item) {
        return items.add(item);
    }

    public Item removeItem(String name) {
        for (int i = 0; i<items.size(); i++) {
            if (name.equals(items.get(i).getName())) {
                return items.remove(i);
            }
        }

        return null;
    }

    public ArrayList<Item> removeAll() {
        ArrayList<Item> temp = new ArrayList<Item>();

        for (int i = 0; i < items.size(); i++) {
            temp.add(items.get(i));
        }

        items.clear();
        return temp;
    }

    public void addAll(ArrayList<Item> temp) {
        items.addAll(temp);
    }

    public Item contains(String name) {
        for (int i = 0; i<items.size(); i++) {
            if (name.equals(items.get(i).getName())) {
                return items.get(i);
            }
        }

        return null;
    }

    public Inventory getInventory() {
        return this;
    }

    public String toString() {
        String msg = "";
        for (Item i : items) {
            msg += i.getName() + "\n";
        }
        return msg;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
