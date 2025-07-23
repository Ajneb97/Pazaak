package pz.ajneb97.api;

import org.bukkit.entity.Player;
import pz.ajneb97.Pazaak;

public class PazaakAPI {
    private static Pazaak plugin;

    public PazaakAPI(Pazaak plugin) {
        this.plugin = plugin;
    }

    public static Pazaak getPlugin() {
        return plugin;
    }

    public static int getWins(Player player){
        return plugin.getPlayerDataManager().getWins(player);
    }

    public static int getLoses(Player player){
        return plugin.getPlayerDataManager().getLoses(player);
    }

    public static String getTimePlayed(Player player){
        return plugin.getPlayerDataManager().getTimePlayed(player);
    }
}
