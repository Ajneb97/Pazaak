package pz.ajneb97.configs;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import pz.ajneb97.Pazaak;
import pz.ajneb97.configs.model.CommonConfig;
import pz.ajneb97.managers.CommonItemManager;
import pz.ajneb97.managers.InventoryManager;
import pz.ajneb97.model.inventory.CommonInventory;
import pz.ajneb97.model.inventory.CommonInventoryItem;
import pz.ajneb97.model.inventory.CustomItems;
import pz.ajneb97.model.items.CommonItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryConfigManager {

    private Pazaak plugin;
    private CommonConfig configFile;


    public InventoryConfigManager(Pazaak plugin){
        this.plugin = plugin;
        this.configFile = new CommonConfig("inventory.yml",plugin,null, false);
        this.configFile.registerConfig();
        checkUpdates();
    }

    public void configure(){
        FileConfiguration config = configFile.getConfig();
        InventoryManager inventoryManager = plugin.getInventoryManager();

        ArrayList<CommonInventory> inventories = new ArrayList<>();
        CommonItemManager commonItemManager = plugin.getCommonItemManager();
        if(config.contains("inventories")) {
            for(String key : config.getConfigurationSection("inventories").getKeys(false)) {
                int slots = config.getInt("inventories."+key+".slots");
                String title = config.getString("inventories."+key+".title");

                List<CommonInventoryItem> items = new ArrayList<>();
                for(String slotString : config.getConfigurationSection("inventories."+key).getKeys(false)) {
                    if(!slotString.equals("slots") && !slotString.equals("title")) {
                        String path = "inventories."+key+"."+slotString;
                        CommonItem item = null;
                        if(config.contains(path+".item")){
                            item = commonItemManager.getCommonItemFromConfig(config, path+".item");
                        }

                        String type = config.contains(path+".type") ?
                                config.getString(path+".type") : null;

                        CommonInventoryItem inventoryItem = new CommonInventoryItem(slotString,item,type);
                        items.add(inventoryItem);
                    }
                }

                CommonInventory inv = new CommonInventory(key,slots,title,items);
                inventories.add(inv);
            }
        }
        inventoryManager.setInventories(inventories);

        Map<String,CommonItem> normalCards = new HashMap<>();
        Map<String,CommonItem> bonusCards = new HashMap<>();
        for(String key : config.getConfigurationSection("custom_items.normal_cards").getKeys(false)) {
            normalCards.put(key,commonItemManager.getCommonItemFromConfig(config, "custom_items.normal_cards."+key));
        }
        for(String key : config.getConfigurationSection("custom_items.bonus_cards").getKeys(false)) {
            bonusCards.put(key,commonItemManager.getCommonItemFromConfig(config, "custom_items.bonus_cards."+key));
        }
        CustomItems customItems = new CustomItems(
                commonItemManager.getCommonItemFromConfig(config, "custom_items.stand_remaining_space"),
                commonItemManager.getCommonItemFromConfig(config, "custom_items.total_points"),
                normalCards,bonusCards,
                commonItemManager.getCommonItemFromConfig(config, "custom_items.unknown_bonus_card")
        );
        inventoryManager.setCustomItems(customItems);
    }

    public boolean reloadConfig(){
        if(!configFile.reloadConfig()){
            return false;
        }
        configure();
        return true;
    }

    public void checkUpdates(){
        Path pathConfig = Paths.get(configFile.getRoute());
        try{
            String text = new String(Files.readAllBytes(pathConfig));
            FileConfiguration config = configFile.getConfig();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig(){
        return configFile.getConfig();
    }
}
