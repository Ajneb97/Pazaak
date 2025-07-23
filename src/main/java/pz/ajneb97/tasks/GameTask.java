package pz.ajneb97.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import pz.ajneb97.Pazaak;
import pz.ajneb97.managers.InventoryManager;
import pz.ajneb97.model.game.Game;
import pz.ajneb97.model.game.GamePlayer;

public class GameTask {
    private Pazaak plugin;
    private Game game;
    private boolean stop;
    private int time;

    public GameTask(Pazaak plugin,Game game,int time){
        this.plugin = plugin;
        this.game = game;
        this.time = time;
    }

    public void start(){
        new BukkitRunnable(){
            @Override
            public void run() {
                if(stop){
                    this.cancel();
                }else{
                    execute();
                }
            }
        }.runTaskTimer(plugin,0L,20L);
    }

    public void execute(){
        switch (game.getGameStatus()) {
            case STARTING -> executeStarting();
            case PLAYING -> executePlaying();
        }
        if(time > 0){
            time--;
        }
    }

    private void executeStarting(){
        if(time > 0 && time <= 5){
            InventoryManager inventoryManager = plugin.getInventoryManager();
            for(GamePlayer gamePlayer : game.getGamePlayers()){
                inventoryManager.createInventoryStarting(gamePlayer,game);
            }
        }else if(time == 0){
            plugin.getGameManager().startGame(game);
        }
    }

    private void executePlaying(){
        if (time == 0) {
            // End Turn
            if(!game.isInventoryBlocked()){
                plugin.getGameManager().autoMove(game);
            }
        } else {
            // Update inventory
            plugin.getInventoryManager().updateInventoryTitle(game,false,false,null);
        }
    }

    public void stop(){
        this.stop = true;
    }

    public int getTime() {
        return time;
    }
}
