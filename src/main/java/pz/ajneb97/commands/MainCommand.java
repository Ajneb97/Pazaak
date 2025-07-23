package pz.ajneb97.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pz.ajneb97.Pazaak;
import pz.ajneb97.managers.MessagesManager;
import pz.ajneb97.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {

    private Pazaak plugin;
    public MainCommand(Pazaak plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        MessagesManager msgManager = plugin.getMessagesManager();
        FileConfiguration messagesConfig = plugin.getMessagesConfig();

        if (!(sender instanceof Player player)) {
            if (args.length >= 1) {
                String arg = args[0].toLowerCase();
                switch (arg) {
                    case "reload" -> reload(sender,msgManager,messagesConfig);
                }
            }
            return true;
        }

        if(args.length >= 1){
            String arg = args[0].toLowerCase();
            switch (arg) {
                case "reload" -> reload(sender,msgManager,messagesConfig);
                case "invite" -> invite(player, args, msgManager, messagesConfig);
                case "accept" -> accept(player, args, msgManager, messagesConfig);
                default -> help(sender,msgManager,messagesConfig);
            }
        }else{
            help(sender,msgManager,messagesConfig);
        }

        return true;
    }

    public void help(CommandSender sender,MessagesManager msgManager,FileConfiguration messagesConfig){
        if(PlayerUtils.isPazaakAdmin(sender)) {
            sender.sendMessage(MessagesManager.getColoredMessage("&8[ [ &2[ [ &8[ [ &9PAZAAK &8] ] &2] ] &8] ]"));
            sender.sendMessage(MessagesManager.getColoredMessage(""));
            sender.sendMessage(MessagesManager.getColoredMessage("&6/pazaak invite <player> (optional)<bet> &8» &7Invites a player to play Pazaak."));
            sender.sendMessage(MessagesManager.getColoredMessage("&6/pazaak accept <player> &8» &7Accepts a Pazaak invitation."));
            sender.sendMessage(MessagesManager.getColoredMessage("&6/pazaak reload &8» &7Reloads the config"));
            sender.sendMessage(MessagesManager.getColoredMessage(""));
            sender.sendMessage(MessagesManager.getColoredMessage("&8[ [ &2[ [ &8[ [ &9PAZAAK &8] ] &2] ] &8] ]"));
        }else{
            msgManager.sendMessage(sender,messagesConfig.getString("commandDoesNotExist"),true);
        }
    }

    public void reload(CommandSender sender,MessagesManager msgManager,FileConfiguration messagesConfig){
        if(!PlayerUtils.isPazaakAdmin(sender)){
            msgManager.sendMessage(sender,messagesConfig.getString("noPermissions"),true);
            return;
        }

        if(!plugin.getConfigsManager().reload()){
            sender.sendMessage(Pazaak.prefix+MessagesManager.getColoredMessage("&cThere was an error reloading the config, check the console."));
            return;
        }
        msgManager.sendMessage(sender,messagesConfig.getString("configReloaded"),true);
    }

    public void invite(Player player,String[] args,MessagesManager msgManager,FileConfiguration messagesConfig){
        // /pazaak invite <player> (optional)<bet>
        if(args.length <= 1){
            msgManager.sendMessage(player,messagesConfig.getString("commandInviteError"),true);
            return;
        }

        Player player2 = Bukkit.getPlayer(args[1]);
        if(player2 == null){
            msgManager.sendMessage(player,messagesConfig.getString("playerNotOnline").replace("%player%",args[1]),true);
            return;
        }

        double bet = 0;
        if(args.length >= 3){
            try{
                bet = Double.parseDouble(args[2]);
                if(bet <= 0){
                    msgManager.sendMessage(player,messagesConfig.getString("betInvalid"),true);
                    return;
                }
            }catch(NumberFormatException e) {
                msgManager.sendMessage(player, messagesConfig.getString("betInvalid"), true);
                return;
            }
        }

        plugin.getInvitationManager().invitePlayer(player,player2,bet);
    }

    public void accept(Player player,String[] args,MessagesManager msgManager,FileConfiguration messagesConfig){
        // /pazaak accept <player>
        if(args.length <= 1){
            msgManager.sendMessage(player,messagesConfig.getString("commandAcceptError"),true);
            return;
        }

        Player player2 = Bukkit.getPlayer(args[1]);
        if(player2 == null){
            msgManager.sendMessage(player,messagesConfig.getString("playerNotOnline").replace("%player%",args[1]),true);
            return;
        }

        plugin.getInvitationManager().acceptInvitation(player,player2);
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = getArgCommands(sender,args.length);

        if(args.length == 1) {
            for(String c : commands) {
                if(args[0].isEmpty() || c.startsWith(args[0].toLowerCase())) {
                    completions.add(c);
                }
            }
            return completions;
        }

        return null;
    }

    private List<String> getArgCommands(CommandSender sender,int args){
        args = args-1;
        List<String> commands = new ArrayList<>();
        if(args <= 1){
            commands.add("invite");commands.add("accept");
        }
        if(PlayerUtils.isPazaakAdmin(sender)){
            if(args == 0){
                commands.add("reload");
            }
        }
        return commands;
    }

}
