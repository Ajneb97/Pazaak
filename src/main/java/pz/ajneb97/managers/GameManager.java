package pz.ajneb97.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pz.ajneb97.Pazaak;
import pz.ajneb97.configs.MainConfigManager;
import pz.ajneb97.configs.model.SoundsConfig;
import pz.ajneb97.model.game.Game;
import pz.ajneb97.model.game.GameEndReason;
import pz.ajneb97.model.game.GamePlayer;
import pz.ajneb97.model.game.GameStatus;
import pz.ajneb97.utils.ActionUtils;
import pz.ajneb97.utils.InventoryUtils;
import pz.ajneb97.utils.ServerVersion;

import java.util.ArrayList;
import java.util.Random;

public class GameManager {

    private Pazaak plugin;
    private ArrayList<Game> games;
    private GameItemManager gameItemManager;

    public GameManager(Pazaak plugin){
        this.plugin = plugin;
        this.games = new ArrayList<>();
        this.gameItemManager = new GameItemManager(plugin);
    }

    public void addGame(Game game){
        games.add(game);
    }

    public void removeGame(Game game){
        games.removeIf(g -> g.equals(game));
    }

    public Game getGameByPlayer(Player player){
        for(Game game : games){
            if(game.getGamePlayer(player) != null){
                return game;
            }
        }
        return null;
    }

    public boolean gameExists(Game game){
        return games.contains(game);
    }

    public void onFoodChange(FoodLevelChangeEvent event,Player player){
        if(getGameByPlayer(player) != null){
            event.setCancelled(true);
        }
    }

    public void onDamage(EntityDamageEvent event, Player player){
        if(getGameByPlayer(player) != null){
            event.setCancelled(true);
        }
    }

    public void onMove(PlayerMoveEvent event, Player player){
        if(getGameByPlayer(player) != null){
            event.setCancelled(true);
        }
    }

    public void onLeave(Player player){
        Game game = getGameByPlayer(player);
        if(game != null){
            GamePlayer gamePlayer = game.getGamePlayer(player);
            endGame(game,GameEndReason.LEAVE_SERVER,game.getOpponent(gamePlayer.getPlayer()));
        }
    }

    public void onInventoryClose(Player player, InventoryCloseEvent event) {
        Game game = getGameByPlayer(player);
        if(game != null){
            GamePlayer gamePlayer = game.getGamePlayer(player);
            if(gamePlayer.isChangingInventory()){
                return;
            }
            new BukkitRunnable(){
                @Override
                public void run() {
                    if(InventoryUtils.getTopInventory(player).getType().equals(InventoryType.CRAFTING)){
                        if(!gameExists(game)){
                            return;
                        }
                        if(game.getGameStatus().equals(GameStatus.STARTING)){
                            player.openInventory(event.getInventory());
                            return;
                        }
                        player.openInventory(gamePlayer.getTopInventoryCopy());
                        plugin.getInventoryManager().updateInventoryTitleWhenClosing(game,player);
                    }
                }
            }.runTaskLater(plugin,1L);
        }
    }

    public void onInventoryClick(Player player, InventoryClickEvent event) {
        Game game = getGameByPlayer(player);
        if (game != null) {
            event.setCancelled(true);

            if(event.getCurrentItem() == null || event.getSlotType() == null){
                return;
            }

            ClickType clickType = event.getClick();
            int slot = event.getSlot();
            ItemStack item = event.getCurrentItem();

            if(event.getClickedInventory() == null){
                return;
            }

            boolean upperInventory = event.getClickedInventory().equals(InventoryUtils.getTopInventory(player));

            gameItemManager.clickOnItem(game,game.getGamePlayer(player),slot,item,clickType,upperInventory);
        }
    }


    public void startStartingPhase(Player player1, Player player2, double bet){
        Game game = new Game(player1,player2);
        game.setBet(bet);
        game.setGameStatus(GameStatus.STARTING);
        addGame(game);

        clearPlayer(player1);clearPlayer(player2);

        game.startCooldownTask(plugin,5);
    }

    private void clearPlayer(Player player){
        plugin.getPlayerDataManager().getPlayerDataBackupManager().createPlayerDataBackup(player);
        player.getInventory().clear();
        player.getEquipment().clear();
    }

    public void startGame(Game game){
        game.stopCooldownTask();
        game.setGameStatus(GameStatus.PLAYING);
        game.setMillisStart(System.currentTimeMillis());
        game.generatePlayerStart();
        game.setTurnTime(plugin.getConfigsManager().getMainConfigManager().getTimeInEachTurn());

        InventoryManager inventoryManager = plugin.getInventoryManager();
        inventoryManager.createInventory(game,game.getPlayer1());
        inventoryManager.createInventory(game,game.getPlayer2());
        inventoryManager.updateTotalPointsItem(game);

        changeTurn(game);
    }

    // A player has used the end turn item
    public void changeTurn(Game game){
        GamePlayer playerTurn = game.getPlayerTurn();
        if(playerTurn.tableFull() && !playerTurn.isOnStand()){
            FileConfiguration messages = plugin.getMessagesConfig();
            MessagesManager msgManager = plugin.getMessagesManager();
            msgManager.sendMessage(playerTurn.getPlayer(),messages.getString("tableFull"),true);
            return;
        }

        if(checkRoundEnd(game,playerTurn)) {
            return;
        }

        game.stopCooldownTask();
        playerTurn.setBonusCardPlayed(false);
        game.changeTurn();
        plugin.getInventoryManager().updateInventoryTitle(game,false,false,null);

        game.setInventoryBlocked(true);

        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();
        for(GamePlayer gamePlayer : game.getGamePlayers()){
            ActionUtils.playSoundFromConfig(
                    gamePlayer.getPlayer(),mainConfigManager.getSoundsConfig().getEndTurnSound());
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                // Generate card
                if(!gameExists(game)){
                    return;
                }

                int cardNumber = new Random().nextInt(10)+1;
                game.getPlayerTurn().addNormalCard(cardNumber);

                for(GamePlayer gamePlayer : game.getGamePlayers()){
                    ActionUtils.playSoundFromConfig(
                            gamePlayer.getPlayer(),mainConfigManager.getSoundsConfig().getGenerateCardSound());
                }

                // Update inventory
                InventoryManager inventoryManager = plugin.getInventoryManager();
                inventoryManager.addCardItem(game);
                inventoryManager.updateTotalPointsItem(game);


                game.setInventoryBlocked(false);

                // Automatic stand
                if(mainConfigManager.isAutomaticStand() && game.getPlayerTurn().getPoints() == 20){
                    stand(game,game.getPlayerTurn());
                    return;
                }

                game.startCooldownTask(plugin,game.getTurnTime());
            }
        }.runTaskLater(plugin,19L);
    }

    private boolean checkRoundEnd(Game game,GamePlayer playerTurn){
        int playerTurnPoints = playerTurn.getPoints();

        // Check possible cases
        // 1. Player has more than 20 points
        // 2. Both players on stand, check points
        // 3. If player has a full table

        if(playerTurn.tableFull()){
            playerWin(game,playerTurn);
            return true;
        }

        GamePlayer opponent = game.getOpponent(playerTurn.getPlayer());
        if(playerTurnPoints > 20){
            playerWin(game,opponent);
            return true;
        }

        if(playerTurn.isOnStand() && opponent.isOnStand()){
            if(playerTurnPoints > opponent.getPoints()){
                playerWin(game,playerTurn);
            }else if(opponent.getPoints() > playerTurnPoints){
                playerWin(game,opponent);
            }else{
                playerTie(game);
            }
            return true;
        }

        return false;
    }

    private void playerWin(Game game,GamePlayer winner){
        GamePlayer opponent = game.getOpponent(winner.getPlayer());
        SoundsConfig soundsConfig = plugin.getConfigsManager().getMainConfigManager().getSoundsConfig();

        winner.setWinRounds(winner.getWinRounds()+1);

        boolean winGame = winner.getWinRounds() == 3;
        if(winGame){
            ActionUtils.playSoundFromConfig(winner.getPlayer(),soundsConfig.getWinGameSound());
            ActionUtils.playSoundFromConfig(opponent.getPlayer(),soundsConfig.getLoseGameSound());

            plugin.getInventoryManager().updateInventoryTitle(game,false,true,winner);
        }else{
            ActionUtils.playSoundFromConfig(winner.getPlayer(),soundsConfig.getWinRoundSound());
            ActionUtils.playSoundFromConfig(opponent.getPlayer(),soundsConfig.getLoseRoundSound());

            plugin.getInventoryManager().updateInventoryTitle(game,true,false,winner);
            game.generatePlayerStart();
        }

        game.setInventoryBlocked(true);

        // Update inventory
        InventoryManager inventoryManager = plugin.getInventoryManager();
        inventoryManager.clearCardItems(game);
        inventoryManager.updateTotalPointsItem(game);
        inventoryManager.addWinItem(game,winner);

        new BukkitRunnable(){
            @Override
            public void run() {
                if(!gameExists(game)){
                    return;
                }

                if(winGame){
                    endGame(game,GameEndReason.WIN,winner);
                }else{
                    changeTurn(game);
                }
            }
        }.runTaskLater(plugin,30L);
    }

    private void playerTie(Game game){
        for(GamePlayer g : game.getGamePlayers()){
            ActionUtils.playSoundFromConfig(
                    g.getPlayer(),plugin.getConfigsManager().getMainConfigManager().getSoundsConfig().getTieRoundSound());
        }

        game.generatePlayerStart();

        game.setInventoryBlocked(true);

        // Update inventory
        plugin.getInventoryManager().updateInventoryTitle(game,true,false,null);
        InventoryManager inventoryManager = plugin.getInventoryManager();
        inventoryManager.clearCardItems(game);
        inventoryManager.updateTotalPointsItem(game);

        new BukkitRunnable(){
            @Override
            public void run() {
                if(!gameExists(game)){
                    return;
                }

                changeTurn(game);
            }
        }.runTaskLater(plugin,30L);
    }

    public void useBonusCard(Game game,GamePlayer gamePlayer,int bonusCardPos){
        FileConfiguration messages = plugin.getMessagesConfig();
        MessagesManager msgManager = plugin.getMessagesManager();
        if(gamePlayer.isBonusCardPlayed()){
            msgManager.sendMessage(gamePlayer.getPlayer(),messages.getString("useBonusCardError"),true);
            return;
        }

        if(gamePlayer.tableFull()){
            msgManager.sendMessage(gamePlayer.getPlayer(),messages.getString("tableFull"),true);
            return;
        }

        if(!gamePlayer.useBonusCard(bonusCardPos)){
            msgManager.sendMessage(gamePlayer.getPlayer(),messages.getString("negativeNumberError"),true);
            return;
        }

        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();
        for(GamePlayer g : game.getGamePlayers()){
            ActionUtils.playSoundFromConfig(
                    g.getPlayer(),mainConfigManager.getSoundsConfig().getAddCardSound());
        }

        // Update inventory
        InventoryManager inventoryManager = plugin.getInventoryManager();
        inventoryManager.addCardItem(game);
        inventoryManager.updateTotalPointsItem(game);
        inventoryManager.removeCardItem(game,bonusCardPos);

        // Automatic stand
        if(mainConfigManager.isAutomaticStand() && game.getPlayerTurn().getPoints() == 20){
            stand(game,game.getPlayerTurn());
        }
    }

    public void stand(Game game,GamePlayer gamePlayer){
        gamePlayer.setOnStand(true);

        // Update inventory
        InventoryManager inventoryManager = plugin.getInventoryManager();
        inventoryManager.addStandItems(game);

        for(GamePlayer g : game.getGamePlayers()){
            ActionUtils.playSoundFromConfig(
                    g.getPlayer(),plugin.getConfigsManager().getMainConfigManager().getSoundsConfig().getStandSound());
        }

        game.setInventoryBlocked(true);
        new BukkitRunnable(){
            @Override
            public void run() {
                if(!gameExists(game)){
                    return;
                }

                game.setInventoryBlocked(false);
                changeTurn(game);
            }
        }.runTaskLater(plugin,19L);
    }

    public void autoMove(Game game){
        GamePlayer playerTurn = game.getPlayerTurn();
        if(playerTurn.tableFull()){
            stand(game,playerTurn);
        }else{
            changeTurn(game);
        }
    }

    public void endGame(Game game, GameEndReason reason, GamePlayer winner){
        FileConfiguration messages = plugin.getMessagesConfig();
        MessagesManager msgManager = plugin.getMessagesManager();

        game.stopCooldownTask();

        removeGame(game);

        boolean serverStop = reason.equals(GameEndReason.SERVER_STOP);
        GamePlayer loser = null;
        if(winner != null){
            loser = game.getOpponent(winner.getPlayer());
        }

        double bet = game.getBet()*2;
        long millisPlayed = System.currentTimeMillis()-game.getMillisStart();

        ServerVersion serverVersion = Pazaak.serverVersion;

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        PlayerDataBackupManager playerDataBackupManager = plugin.getPlayerDataManager().getPlayerDataBackupManager();
        for(GamePlayer gamePlayer : game.getGamePlayers()){
            Player player = gamePlayer.getPlayer();
            playerDataBackupManager.restorePlayerDataBackup(player,!serverStop);
            if(!serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_21_R1)){
                player.updateInventory();
            }

            player.closeInventory();

            if(serverStop){
                continue;
            }

            if(reason.equals(GameEndReason.LEAVE_SERVER) || reason.equals(GameEndReason.ITEM)){
                msgManager.sendMessage(gamePlayer.getPlayer(),messages.getString("leaveGame")
                        .replace("%player_1%",loser.getPlayer().getName())
                        .replace("%player_2%",winner.getPlayer().getName()),true);
            }else{
                msgManager.sendMessage(gamePlayer.getPlayer(),messages.getString("winGame")
                        .replace("%player%",winner.getPlayer().getName()),true);
            }

            playerDataManager.endGame(player, gamePlayer.equals(winner),millisPlayed);

            //Bet
            if(bet != 0){
                if(gamePlayer.equals(winner)){
                    plugin.getDependencyManager().getVault().depositPlayer(gamePlayer.getPlayer(),bet);
                    msgManager.sendMessage(gamePlayer.getPlayer(),messages.getString("winBet")
                            .replace("%bet%",bet+""),true);
                }else{
                    msgManager.sendMessage(gamePlayer.getPlayer(),messages.getString("loseBet")
                            .replace("%player%",winner.getPlayer().getName())
                            .replace("%bet%",bet+""),true);
                }
            }
        }
    }

    public void endAllGames(){
        for(Game game : new ArrayList<>(games)){
            endGame(game,GameEndReason.SERVER_STOP,null);
        }
    }

    public GameItemManager getGameItemManager() {
        return gameItemManager;
    }
}
