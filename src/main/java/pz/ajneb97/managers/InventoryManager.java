package pz.ajneb97.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pz.ajneb97.Pazaak;
import pz.ajneb97.model.game.Game;
import pz.ajneb97.model.game.GamePlayer;
import pz.ajneb97.model.game.GameStatus;
import pz.ajneb97.model.internal.CommonVariable;
import pz.ajneb97.model.inventory.CommonInventory;
import pz.ajneb97.model.inventory.CommonInventoryItem;
import pz.ajneb97.model.inventory.CustomItems;
import pz.ajneb97.model.items.CommonItem;
import pz.ajneb97.utils.InventoryItem;
import pz.ajneb97.utils.InventoryUtils;
import pz.ajneb97.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public class InventoryManager {
    private Pazaak plugin;
    private ArrayList<CommonInventory> inventories;
    private CustomItems customItems;
    public InventoryManager(Pazaak plugin){
        this.plugin = plugin;
        this.inventories = new ArrayList<>();
    }

    public ArrayList<CommonInventory> getInventories() {
        return inventories;
    }

    public void setInventories(ArrayList<CommonInventory> inventories) {
        this.inventories = inventories;
    }

    public CustomItems getCustomItems() {
        return customItems;
    }

    public void setCustomItems(CustomItems customItems) {
        this.customItems = customItems;
    }

    public CommonInventory getInventory(String name){
        for(CommonInventory inventory : inventories){
            if(inventory.getName().equals(name)){
                return inventory;
            }
        }
        return null;
    }

    public void createInventoryStarting(GamePlayer gamePlayer, Game game){
        int time = game.getGameTask().getTime();

        FileConfiguration messagesConfig = plugin.getMessagesConfig();
        Player player = gamePlayer.getPlayer();

        Inventory inv = Bukkit.createInventory(null,9,MessagesManager.getColoredMessage(messagesConfig.getString("inventoryStartingTitle")
                .replace("%time%",time+"")));

        for(int i=0;i<=8;i++){
            new InventoryItem(inv,i, Material.BLACK_STAINED_GLASS_PANE).name(" ").ready();
        }

        if(time <= 4){
            new InventoryItem(inv,0, Material.RED_STAINED_GLASS_PANE).name(" ").ready();
            new InventoryItem(inv,8, Material.RED_STAINED_GLASS_PANE).name(" ").ready();
        }
        if(time <= 3){
            new InventoryItem(inv,1, Material.RED_STAINED_GLASS_PANE).name(" ").ready();
            new InventoryItem(inv,7, Material.RED_STAINED_GLASS_PANE).name(" ").ready();
        }
        if(time <= 2){
            new InventoryItem(inv,2, Material.YELLOW_STAINED_GLASS_PANE).name(" ").ready();
            new InventoryItem(inv,6, Material.YELLOW_STAINED_GLASS_PANE).name(" ").ready();
        }
        if(time <= 1){
            new InventoryItem(inv,3, Material.YELLOW_STAINED_GLASS_PANE).name(" ").ready();
            new InventoryItem(inv,5, Material.YELLOW_STAINED_GLASS_PANE).name(" ").ready();
        }

        gamePlayer.setChangingInventory(true);
        player.openInventory(inv);
        gamePlayer.setChangingInventory(false);
    }

    // When the game starts.
    public void createInventory(Game game,GamePlayer gamePlayer){
        CommonInventory inventory = getInventory("main_upper");

        Inventory inv = Bukkit.createInventory(null,inventory.getSlots(), getInventoryTurnTitle(game));
        gamePlayer.setChangingInventory(true);
        gamePlayer.getPlayer().openInventory(inv);
        gamePlayer.setChangingInventory(false);

        List<CommonInventoryItem> items = inventory.getItems();
        CommonItemManager commonItemManager = plugin.getCommonItemManager();

        ArrayList<CommonVariable> variables = new ArrayList<>();
        GamePlayer opponentPlayer = game.getOpponent(gamePlayer.getPlayer());
        variables.add(new CommonVariable("%player_name%",gamePlayer.getPlayer().getName()));
        variables.add(new CommonVariable("%opponent_name%",opponentPlayer.getPlayer().getName()));

        for(CommonInventoryItem itemInventory : items){
            List<Integer> slots = itemInventory.getSlots();
            String type = itemInventory.getType();

            for(int slot : slots){
                if(type != null){
                    continue;
                }

                CommonItem item = itemInventory.getItem().clone();
                commonItemManager.replaceVariables(item,variables, gamePlayer.getPlayer());
                inv.setItem(slot,commonItemManager.createItemFromCommonItem(item));
            }
        }

        gamePlayer.setTopInventoryCopy(inv);

        PlayerInventory inv2 = gamePlayer.getPlayer().getInventory();
        inventory = getInventory("main_lower");
        items = inventory.getItems();
        for(CommonInventoryItem itemInventory : items){
            List<Integer> slots = itemInventory.getSlots();
            String type = itemInventory.getType();

            if(type != null) {
                if(type.endsWith("_bonus_cards")){
                    setBonusCardItems(game,gamePlayer.getPlayer(),inv2,slots,type);
                    continue;
                }else if(type.endsWith("_wins")){
                    continue;
                }
            }

            for(int slot : slots){
                CommonItem item = itemInventory.getItem().clone();
                commonItemManager.replaceVariables(item,variables, gamePlayer.getPlayer());
                ItemStack itemStack = commonItemManager.createItemFromCommonItem(item);
                if(type != null){
                    itemStack = ItemUtils.setTagStringItem(plugin,itemStack, "pazaak_item_type", type);
                }
                inv2.setItem(slot,itemStack);
            }
        }
    }

    private void setBonusCardItems(Game game,Player player,PlayerInventory inv,List<Integer> slots,String type){
        CommonItemManager commonItemManager = plugin.getCommonItemManager();

        boolean opponent = type.equals("opponent_bonus_cards");
        GamePlayer gamePlayer = opponent ? game.getOpponent(player) : game.getGamePlayer(player);

        ArrayList<String> bonusCards = gamePlayer.getBonusCards();
        int pos = 0;
        for(int i : slots){
            String cardId = bonusCards.get(pos);

            ItemStack item;
            if(opponent){
                item = commonItemManager.createItemFromCommonItem(customItems.getUnknownBonusCard());
            }else{
                item = commonItemManager.createItemFromCommonItem(customItems.getBonusCard(cardId));
                item = ItemUtils.setTagStringItem(plugin,item, "pazaak_bonus_card_pos", pos+"");
            }

            inv.setItem(i,item);

            pos++;
        }
    }

    public void updateTotalPointsItem(Game game){
        CommonInventory inventory = getInventory("main_upper");
        for(GamePlayer gamePlayer : game.getGamePlayers()){
            Inventory inv = getAndOpenInventory(gamePlayer);

            CommonItemManager commonItemManager = plugin.getCommonItemManager();

            List<Integer> slots = inventory.getTypedItemSlots("total_points",false);
            updateTotalPointsItem(slots,commonItemManager,gamePlayer,inv);

            List<Integer> slotsOpponent = inventory.getTypedItemSlots("total_points",true);
            updateTotalPointsItem(slotsOpponent,commonItemManager,game.getOpponent(gamePlayer.getPlayer()),inv);
            gamePlayer.setTopInventoryCopy(inv);
        }
    }

    private void updateTotalPointsItem(List<Integer> slots,CommonItemManager commonItemManager,GamePlayer gamePlayer,Inventory inv){
        ArrayList<CommonVariable> variables = new ArrayList<>();
        variables.add(new CommonVariable("%player_points%",gamePlayer.getPoints()+""));
        CommonItem commonItem = customItems.getTotalPoints().clone();
        commonItemManager.replaceVariables(commonItem,variables, gamePlayer.getPlayer());
        ItemStack item = plugin.getCommonItemManager().createItemFromCommonItem(commonItem);
        item.setAmount(Math.max(1,gamePlayer.getPoints()));
        for(int slot : slots){
            inv.setItem(slot,item);
        }
    }

    public void addCardItem(Game game){
        CommonInventory inventory = getInventory("main_upper");
        for(GamePlayer gamePlayer : game.getGamePlayers()){
            Inventory inv = getAndOpenInventory(gamePlayer);

            GamePlayer turnPlayer = game.getPlayerTurn();
            boolean isOpponent = !gamePlayer.equals(turnPlayer);

            List<Integer> slots = inventory.getTypedItemSlots("cards",isOpponent);

            // The card was already added to the list
            ArrayList<String> cards = turnPlayer.getCards();
            String cardAddedId = cards.get(cards.size()-1);
            int slot = slots.get(cards.size()-1);

            CommonItemManager commonItemManager = plugin.getCommonItemManager();
            ItemStack item;
            if(cardAddedId.startsWith("+") || cardAddedId.startsWith("-")){
                item = commonItemManager.createItemFromCommonItem(customItems.getBonusCard(cardAddedId));
            }else{
                item = commonItemManager.createItemFromCommonItem(customItems.getNormalCard(cardAddedId));
            }
            inv.setItem(slot,item);

            gamePlayer.setTopInventoryCopy(inv);
        }
    }

    public void removeCardItem(Game game,int bonusCardPos){
        CommonInventory inventory = getInventory("main_lower");
        for(GamePlayer gamePlayer : game.getGamePlayers()){
            PlayerInventory inv = gamePlayer.getPlayer().getInventory();

            GamePlayer turnPlayer = game.getPlayerTurn();
            boolean isOpponent = !gamePlayer.equals(turnPlayer);

            List<Integer> slots = inventory.getTypedItemSlots("bonus_cards",isOpponent);

            inv.setItem(slots.get(bonusCardPos),null);
        }
    }

    public void clearCardItems(Game game){
        CommonInventory inventory = getInventory("main_upper");
        for(GamePlayer gamePlayer : game.getGamePlayers()){
            Inventory inv = getAndOpenInventory(gamePlayer);

            List<Integer> slots = inventory.getTypedItemSlots("cards",false);
            List<Integer> slots2 = inventory.getTypedItemSlots("cards",true);
            slots.addAll(slots2);
            for(int slot : slots){
                inv.setItem(slot,null);
            }
            gamePlayer.setTopInventoryCopy(inv);
        }
    }

    public void addWinItem(Game game,GamePlayer winner){
        CommonInventory inventory = getInventory("main_lower");
        for(GamePlayer gamePlayer : game.getGamePlayers()){
            PlayerInventory inv = gamePlayer.getPlayer().getInventory();

            boolean isOpponent = !gamePlayer.equals(winner);

            CommonInventoryItem cInvItem = inventory.getWinItemSlots(isOpponent);
            ItemStack item = plugin.getCommonItemManager().createItemFromCommonItem(cInvItem.getItem());

            int wins = winner.getWinRounds();
            int slot = cInvItem.getSlots().get(wins-1);
            inv.setItem(slot,item);
        }
    }

    public void addStandItems(Game game){
        CommonInventory inventory = getInventory("main_upper");
        for(GamePlayer gamePlayer : game.getGamePlayers()){
            Inventory inv = getAndOpenInventory(gamePlayer);

            GamePlayer turnPlayer = game.getPlayerTurn();
            boolean isOpponent = !gamePlayer.equals(turnPlayer);

            List<Integer> slots = inventory.getTypedItemSlots("cards",isOpponent);

            ArrayList<String> cards = turnPlayer.getCards();
            ItemStack item = plugin.getCommonItemManager().createItemFromCommonItem(customItems.getStandRemainingSpace());

            int fromPos = cards.size();
            for(int i=fromPos;i<slots.size();i++){
                inv.setItem(slots.get(i),item);
            }
            gamePlayer.setTopInventoryCopy(inv);
        }
    }

    private String getInventoryTurnTitle(Game game){
        FileConfiguration messagesConfig = plugin.getMessagesConfig();
        int time = game.getGameTask() != null ? game.getGameTask().getTime() : game.getTurnTime();
        String title = MessagesManager.getColoredMessage(messagesConfig.getString("inventoryTurnTitle")
                .replace("%player%",game.getPlayerTurn().getPlayer().getName())
                .replace("%time%",time+""));
        return title;
    }

    public void updateInventoryTitle(Game game,boolean endRound, boolean endGame, GamePlayer winner){
        FileConfiguration messagesConfig = plugin.getMessagesConfig();
        String title;
        if(endRound){
            if(winner == null){
                title = MessagesManager.getColoredMessage(messagesConfig.getString("inventoryTieTitle"));
            }else{
                title = MessagesManager.getColoredMessage(messagesConfig.getString("inventoryWinRoundTitle")
                        .replace("%player%",winner.getPlayer().getName()));
            }
        }else if(endGame){
            title = MessagesManager.getColoredMessage(messagesConfig.getString("inventoryWinGameTitle")
                    .replace("%player%",winner.getPlayer().getName()));
        }else{
            title = getInventoryTurnTitle(game);
            if(game.isInventoryBlocked()){
                return;
            }
        }

        for(GamePlayer player : game.getGamePlayers()){
            Player p = player.getPlayer();
            if(!InventoryUtils.getTopInventory(p).getType().equals(InventoryType.CRAFTING)){
                InventoryUtils.setOpenInventoryTitle(p,title,plugin);
            }
        }
    }

    public Inventory getAndOpenInventory(GamePlayer gamePlayer){
        Player player = gamePlayer.getPlayer();
        if(!InventoryUtils.getTopInventory(player).getType().equals(InventoryType.CRAFTING)){
            return InventoryUtils.getTopInventory(player);
        }

        // If no inventory is opened, open last saved one
        Inventory invCopy = gamePlayer.getTopInventoryCopy();

        gamePlayer.setChangingInventory(true);
        player.openInventory(invCopy);
        gamePlayer.setChangingInventory(false);

        return invCopy;
    }

    public void updateInventoryTitleWhenClosing(Game game,Player player){
        if(game.getGameStatus().equals(GameStatus.STARTING)){
            return;
        }
        InventoryUtils.setOpenInventoryTitle(player,getInventoryTurnTitle(game),plugin);
    }
}
