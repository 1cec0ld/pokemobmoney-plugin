package com.gmail.ak1cec0ld.plugins.pokemobmoney.file;

import com.gmail.ak1cec0ld.plugins.pokemobmoney.PokeMobMoney;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;

public class Config {
    private static CustomYMLStorage yml;
    private static YamlConfiguration storage;

    public Config(){
        yml = new CustomYMLStorage(PokeMobMoney.instance(),"PokeMobMoney"+ File.separator+"config.yml");
        storage = yml.getYamlConfiguration();
        yml.save();
    }

    public static double getBasePay(EntityType entityType){
        return 0.0;
    }
    public static boolean isNoPay(String regionName){

        return false;
    }

}
