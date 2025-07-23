package pz.ajneb97.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pz.ajneb97.Pazaak;
import pz.ajneb97.managers.MessagesManager;

public class PlayerListener implements Listener {

    private Pazaak plugin;
    public PlayerListener(Pazaak plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        plugin.getPlayerDataManager().setJoinPlayerData(event.getPlayer());

        //Update notification
        Player player = event.getPlayer();
        String latestVersion = plugin.getUpdateCheckerManager().getLatestVersion();
        if(player.isOp() && plugin.getConfigsManager().getMainConfigManager().isUpdateNotify() && !plugin.version.equals(latestVersion)){
            player.sendMessage(MessagesManager.getColoredMessage(plugin.prefix+" &cThere is a new version available. &e(&7"+latestVersion+"&e)"));
            player.sendMessage(MessagesManager.getColoredMessage("&cYou can download it at: &fhttps://www.spigotmc.org/resources/57428/"));
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        plugin.getInvitationManager().manageInvitations(event.getPlayer());
        plugin.getGameManager().onLeave(event.getPlayer());
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event){
        plugin.getGameManager().onFoodChange(event,(Player)event.getEntity());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        plugin.getGameManager().onMove(event,event.getPlayer());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        Entity e = event.getEntity();
        if(e instanceof Player){
            plugin.getGameManager().onDamage(event,(Player)e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event){
        plugin.getGameManager().onInventoryClick((Player)event.getWhoClicked(),event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event){
        plugin.getGameManager().onInventoryClose((Player)event.getPlayer(),event);
    }
}
