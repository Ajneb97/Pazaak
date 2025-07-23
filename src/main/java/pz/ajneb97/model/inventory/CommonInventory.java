package pz.ajneb97.model.inventory;

import java.util.ArrayList;
import java.util.List;

public class CommonInventory {

    private String name;
    private int slots;
    private String title;
    private List<CommonInventoryItem> items;

    public CommonInventory(String name, int slots, String title, List<CommonInventoryItem> items) {
        this.name = name;
        this.slots = slots;
        this.title = title;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<CommonInventoryItem> getItems() {
        return items;
    }

    public void setItems(List<CommonInventoryItem> items) {
        this.items = items;
    }


    public List<Integer> getTypedItemSlots(String type,boolean opponent){
        List<Integer> slots = new ArrayList<>();
        for(CommonInventoryItem item : items) {
            if(item.getType() != null) {
                if (item.getType().equals("player_"+type) && !opponent) {
                    slots.addAll(item.getSlots());
                    return slots;
                } else if (item.getType().equals("opponent_"+type) && opponent) {
                    slots.addAll(item.getSlots());
                    return slots;
                }
            }
        }
        return slots;
    }

    public CommonInventoryItem getWinItemSlots(boolean opponent){
        for(CommonInventoryItem item : items) {
            if(item.getType() != null) {
                if (item.getType().equals("player_wins") && !opponent) {
                    return item;
                } else if (item.getType().equals("opponent_wins") && opponent) {
                    return item;
                }
            }
        }
        return null;
    }
}
