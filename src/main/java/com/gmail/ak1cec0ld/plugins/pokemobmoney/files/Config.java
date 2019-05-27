package com.gmail.ak1cec0ld.plugins.pokemobmoney.files;

import com.gmail.ak1cec0ld.plugins.pokemobmoney.PokeMobMoney;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.List;
import java.util.Set;

public class Config {
    private static CustomYMLStorage yml;
    private static YamlConfiguration storage;

    public Config(){
        yml = new CustomYMLStorage(PokeMobMoney.instance(),"PokeMobMoney"+ File.separator+"config.yml");
        storage = yml.getYamlConfiguration();
        yml.save();
    }

    public static double getBasePay(EntityType entityType){
        if(storage.contains("mobs."+entityType.name().toUpperCase())){
            return storage.getDouble("mobs."+entityType.name().toUpperCase(),0.0);
        }
        PokeMobMoney.debug("Unhandled Mob EntityType for mob money: "+entityType.name());
        return 0.0;
    }
    public static List<String> getNoPayRegions(){
        return storage.getStringList("nopay");
    }
    public static double getRandomizer(){
        return storage.getDouble("randomizer",0.0);
    }
    public static double getPermissionMultiplier(String permission){
        return storage.getDouble("permissions."+permission.toLowerCase(),1.0);
    }
    public static Set<String> getPermissionSet(){
        return storage.getConfigurationSection("permissions").getKeys(false);
    }
    public static double getSpecialCaseMultiplier(String spCase){
        if(storage.contains("specialcase."+spCase)){
            return storage.getDouble("specialcase."+spCase);
        }
        PokeMobMoney.debug("Attempted to get Special Case and failed: "+spCase);
        return 1.0;
    }
    public static int getMinutesBeforeDiminish(){
        return storage.getInt("diminishing-returns.minutes-before-triggered",1);
    }
    public static int getEntityCountBeforeDiminish(){
        return storage.getInt("diminishing-returns.killed-entities-before-triggered",10);
    }
    public static double getMinimumPercentMultiplier(){
        return storage.getDouble("diminishing-returns.minimum-multiplier-allowed",0.01);
    }
    public static void reload(){
        yml.reload();
        storage = yml.getYamlConfiguration();
    }
}
