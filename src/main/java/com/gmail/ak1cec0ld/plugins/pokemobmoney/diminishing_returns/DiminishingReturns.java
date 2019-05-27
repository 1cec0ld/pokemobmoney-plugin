package com.gmail.ak1cec0ld.plugins.pokemobmoney.diminishing_returns;

import com.gmail.ak1cec0ld.plugins.pokemobmoney.PokeMobMoney;
import com.gmail.ak1cec0ld.plugins.pokemobmoney.files.Config;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class DiminishingReturns {

    private static int MINUTES_BEFORE_DIMINISH = 1;
    private static double MINIMUM_MULTIPLIER = 0.01;
    private static int COUNT_BEFORE_DIMINISH = 10;

    private static HashMap<String, HashMap<EntityType, Set<Long>>> storage;

    public DiminishingReturns(){
        storage = new HashMap<>();
        PokeMobMoney.instance().getServer().getScheduler().scheduleSyncRepeatingTask(PokeMobMoney.instance(),
                DiminishingReturns::cleanStorage, 100L, 100L);
        reload();
    }

    public static void addEntry(Player attacker, Entity target){
        if(storage.containsKey(attacker.getName())){
            if(storage.get(attacker.getName()).containsKey(target.getType())){
                storage.get(attacker.getName()).get(target.getType()).add(System.currentTimeMillis());
            } else {
                storage.get(attacker.getName()).put(target.getType(), new HashSet<>());
                addEntry(attacker,target);
            }
        } else {
            storage.put(attacker.getName(), new HashMap<>());
            addEntry(attacker, target);
        }
    }

    public static double getMultiplier(Player player, Entity entity){
        long time = System.currentTimeMillis();
        HashMap<EntityType,Set<Long>> playerEntities = storage.get(player.getName());
        Set<Long> entityEntries = playerEntities.get(entity.getType());
        for(long each : entityEntries){
            if(time-each > MINUTES_BEFORE_DIMINISH*60*1000)
            entityEntries.remove(each);
        }
        int size = entityEntries.size();
        if(size <= COUNT_BEFORE_DIMINISH)return 1.0;
        size -= COUNT_BEFORE_DIMINISH;
        PokeMobMoney.debug("excess size of "+size + " returned diminishing multiplier of "+formula(size));
        return Math.max(MINIMUM_MULTIPLIER, formula(size));
    }

    private static void cleanStorage(){
        long time = System.currentTimeMillis();
        for(Map.Entry<String, HashMap<EntityType, Set<Long>>> player : storage.entrySet()){
            for(Map.Entry<EntityType, Set<Long>> entity : player.getValue().entrySet()){
                for(long each : entity.getValue()){
                    if(time-each > MINUTES_BEFORE_DIMINISH*60*1000){
                        storage.get(player.getKey()).get(entity.getKey()).remove(each);
                    }
                    if(entity.getValue().size()==0){
                        storage.get(player.getKey()).remove(entity.getKey());
                    }
                    if(player.getValue().size()==0){
                        storage.remove(player.getKey());
                    }
                }
            }
        }
    }

    private static double formula(int size){
        double numerator = 11;
        double denominator = (5*size/2)-110;
        return 1+numerator/denominator;
    }
    public static void reload(){
        MINUTES_BEFORE_DIMINISH = Config.getMinutesBeforeDiminish();
        MINIMUM_MULTIPLIER = Config.getMinimumPercentMultiplier();
        COUNT_BEFORE_DIMINISH = Config.getEntityCountBeforeDiminish();
    }
}
