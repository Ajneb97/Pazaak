package pz.ajneb97.model.data;

import org.bukkit.inventory.ItemStack;

public class PlayerDataBackup {
    private ItemStack[] inventory;

    public PlayerDataBackup(ItemStack[] inventory) {
        this.inventory = inventory;
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public void setInventory(ItemStack[] inventory) {
        this.inventory = inventory;
    }

}
