package com.gmail.ak1cec0ld.plugins.pokemobmoney.diminishing_returns;

import com.gmail.ak1cec0ld.plugins.pokemobmoney.files.Config;
import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public class DiminishingReturns {

    private static int MINUTES_BEFORE_DIMINISH = 1;
    private static double MINIMUM_MULTIPLIER = 0.01;
    private static int COUNT_BEFORE_DIMINISH = 10;

    private static HashMap<String, Set<tuple>> storage;

    public DiminishingReturns(){
        storage = new HashMap<>();
        reload();
    }

    public static void addEntry(Player attacker, Entity target){
        if(storage.containsKey(attacker.getName())){
            storage.get(attacker.getName()).add(new tuple(target.getType(),System.currentTimeMillis()));
        } else {
            storage.put(attacker.getName(), new HashSet<>());
            addEntry(attacker, target);
        }
    }

    public static double getMultiplier(Player player, Entity entity){
        long time = System.currentTimeMillis();
        if(!storage.containsKey(player.getName()))return 1.0;
        Set<tuple> playerEntities = storage.get(player.getName());
        if(playerEntities == null)return 1.0;
        int size = 0;
        for(tuple each : ImmutableSet.copyOf(playerEntities)){
            if(time-each.two <= MINUTES_BEFORE_DIMINISH*60*1000){
                if(each.one.equals(entity.getType())) {
                    size++;
                }
            } else {
                playerEntities.remove(each);
            }
        }
        if(size <= COUNT_BEFORE_DIMINISH)return 1.0;
        size -= COUNT_BEFORE_DIMINISH;
        //PokeMobMoney.debug("excess size of "+ size + " returned diminishing multiplier of "+formula(size));
        return Math.max(MINIMUM_MULTIPLIER, formula(size));
    }
    private static double formula(int size){
        double numerator = (5*size) - 198;
        double denominator = 5 * (size-44);
        if(denominator >= 0 || numerator/denominator <= 0){
            return MINIMUM_MULTIPLIER;
        }
        return numerator/denominator;
    }
    public static void reload(){
        MINUTES_BEFORE_DIMINISH = Config.getMinutesBeforeDiminish();
        MINIMUM_MULTIPLIER = Config.getMinimumPercentMultiplier();
        COUNT_BEFORE_DIMINISH = Config.getEntityCountBeforeDiminish();
    }
    private static class tuple{
        EntityType one;
        long two;
        tuple(EntityType a, long b){
            one = a;
            two = b;
        }
    }
}
