package com.gmail.ak1cec0ld.plugins.pokemobmoney;

import com.gmail.ak1cec0ld.plugins.pokemobmoney.files.Config;
import com.gmail.ak1cec0ld.plugins.pokemobmoney.files.PlayerFile;
import com.gmail.ak1cec0ld.plugins.pokemobmoney.listeners.CommandListener;
import com.gmail.ak1cec0ld.plugins.pokemobmoney.listeners.Death;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class PokeMobMoney extends JavaPlugin {

    private static PokeMobMoney instance;

    private static Economy econ = null;

    @Override
    public void onEnable() {
        instance = this;


        if(!setupEconomy()){
            debug("No Vault found! Disabling PokeMobMoney!");
            instance.getPluginLoader().disablePlugin(this);
        }
        setWorldGuard();

        new Config();
        new PlayerFile();
        getServer().getPluginCommand("togglemoneymessages").setExecutor(new CommandListener());
        new Death();




    }

    public static PokeMobMoney instance(){
        return instance;
    }

    private static WorldGuardPlugin setWorldGuard(){
        Plugin WG = instance.getServer().getPluginManager().getPlugin("WorldGuard");

        if (!(WG instanceof WorldGuardPlugin))
        {
            instance.getLogger().severe("WorldGuard Not Found!!!!");
            return null;
        }
        instance.getLogger().info("WorldGuard Plugin Loaded!");
        return (WorldGuardPlugin)WG;
    }
    private static boolean setupEconomy() {
        if (instance.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = instance.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }
    public static Economy getEconomy(){
        return econ;
    }
    public static void msgActionBar(Player player, String message){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(message).create());
    }
    public static void debug(String string){
        Bukkit.getLogger().info("[PMMoney-debug] "+string);
    }
}
