package com.gmail.ak1cec0ld.plugins.pokemobmoney.files;

import com.gmail.ak1cec0ld.plugins.pokemobmoney.PokeMobMoney;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class PlayerFile {
    private static CustomYMLStorage yml;
    private static YamlConfiguration storage;

    public PlayerFile(){
        yml = new CustomYMLStorage(PokeMobMoney.instance(),"PokeMobMoney"+ File.separator+"config.yml");
        storage = yml.getYamlConfiguration();
        yml.save();
    }

    public static boolean prefersChat(String uuid){
        return storage.contains(uuid);
    }
    public static void togglePreference(String uuid){
        if(prefersChat(uuid)){
            storage.set(uuid,null);
        } else {
            storage.set(uuid,true);
        }
        yml.save();
    }
}
