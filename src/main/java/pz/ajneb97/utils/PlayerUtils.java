package pz.ajneb97.utils;

import org.bukkit.command.CommandSender;

public class PlayerUtils {

    public static boolean isPazaakAdmin(CommandSender sender){
        return sender.hasPermission("pazaak.admin");
    }
}
