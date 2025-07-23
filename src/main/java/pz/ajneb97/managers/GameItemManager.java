package pz.ajneb97.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import pz.ajneb97.Pazaak;
import pz.ajneb97.model.game.Game;
import pz.ajneb97.model.game.GameEndReason;
import pz.ajneb97.model.game.GamePlayer;
import pz.ajneb97.utils.ItemUtils;

public class GameItemManager {
    private Pazaak plugin;
    public GameItemManager(Pazaak plugin){
        this.plugin = plugin;
    }

    public void clickOnItem(Game game, GamePlayer gamePlayer, int slot, ItemStack item, ClickType clickType, boolean upperInventory){
        if(game.isInventoryBlocked()){
            return;
        }

        String id = ItemUtils.getTagStringItem(plugin,item,"pazaak_item_type");
        if(id != null){
            switch (id) {
                case "end_turn" -> clickItemEndTurn(game,gamePlayer);
                case "stand" -> clickItemStand(game,gamePlayer);
                case "quit_game" -> clickItemQuitGame(game,gamePlayer);
            }
        }

        String bonusCardPos = ItemUtils.getTagStringItem(plugin,item,"pazaak_bonus_card_pos");
        if(bonusCardPos != null){
            clickItemBonusCard(game,gamePlayer,bonusCardPos);
        }
    }

    private void clickItemEndTurn(Game game,GamePlayer gamePlayer){
        if(!game.getPlayerTurn().equals(gamePlayer)){
            notYourTurnMessage(gamePlayer);
            return;
        }
        plugin.getGameManager().changeTurn(game);
    }

    private void clickItemStand(Game game,GamePlayer gamePlayer){
        if(!game.getPlayerTurn().equals(gamePlayer)){
            notYourTurnMessage(gamePlayer);
            return;
        }
        plugin.getGameManager().stand(game,gamePlayer);
    }

    private void clickItemQuitGame(Game game, GamePlayer gamePlayer){
        plugin.getGameManager().endGame(game, GameEndReason.ITEM, game.getOpponent(gamePlayer.getPlayer()));
    }

    private void clickItemBonusCard(Game game,GamePlayer gamePlayer,String bonusCardPos){
        if(!game.getPlayerTurn().equals(gamePlayer)){
            notYourTurnMessage(gamePlayer);
            return;
        }

        plugin.getGameManager().useBonusCard(game,gamePlayer,Integer.parseInt(bonusCardPos));
    }

    private void notYourTurnMessage(GamePlayer gamePlayer){
        FileConfiguration messages = plugin.getMessagesConfig();
        MessagesManager msgManager = plugin.getMessagesManager();
        msgManager.sendMessage(gamePlayer.getPlayer(),messages.getString("notYourTurn"),true);
    }
}
