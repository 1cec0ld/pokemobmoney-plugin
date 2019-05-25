package com.gmail.ak1cec0ld.plugins.pokemobmoney.listeners;

import com.gmail.ak1cec0ld.plugins.pokemobmoney.files.Config;
import com.gmail.ak1cec0ld.plugins.pokemobmoney.files.PlayerFile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            if(strings.length == 0) {
                PlayerFile.togglePreference(((Player) commandSender).getUniqueId().toString());
                commandSender.sendMessage("Money Notifications Toggled!");
                return true;
            }
            if(strings.length == 1){
                Config.reload();
            }
        }
        return false;
    }
}
