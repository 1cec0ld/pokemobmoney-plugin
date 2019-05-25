package com.gmail.ak1cec0ld.plugins.pokemobmoney.listeners;

import com.gmail.ak1cec0ld.plugins.pokemobmoney.PokeMobMoney;
import com.gmail.ak1cec0ld.plugins.pokemobmoney.files.Config;
import com.gmail.ak1cec0ld.plugins.pokemobmoney.files.PlayerFile;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;
import java.util.Random;

public class Death implements Listener {
    private Random r;

    public Death(){
        PokeMobMoney.instance().getServer().getPluginManager().registerEvents(this, PokeMobMoney.instance());
        r = new Random();
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
        EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
        if(!(damageEvent instanceof EntityDamageByEntityEvent))return;
        EntityDamageByEntityEvent attackerEvent = (EntityDamageByEntityEvent)damageEvent;
        Player attacker = getPlayerCause(attackerEvent);
        if(attacker == null)return;
        Entity target = event.getEntity();

        double basePay = Config.getBasePay(target.getType());
        double permissionMultiplier = getPermissionMultiplier(attacker);
        double specialMultiplier = getSpecialCaseMultiplier(target);
        double randomizer = Config.getRandomizer();
        double randomMultiplier = r.nextDouble()*2*randomizer + 1 - randomizer;

        double payment = basePay * permissionMultiplier * specialMultiplier * randomMultiplier;
        if(payment <= 0)return;

        payPlayer(attacker, payment, target);

    }

    private void payPlayer(Player payee, double payment, Entity target){
        double shortened = Math.floor(payment * 100) / 100;
        PokeMobMoney.getEconomy().depositPlayer(payee, shortened);
        String message = ChatColor.COLOR_CHAR+"2You were awarded "+ ChatColor.COLOR_CHAR+"d$"+shortened+ChatColor.COLOR_CHAR+"2 for making the "+target.getType().toString()+" faint";
        if(PlayerFile.prefersChat(payee.getUniqueId().toString())){
            payee.sendMessage(message);
        } else {
            PokeMobMoney.msgActionBar(payee, message);
        }
    }

    private Player getPlayerCause(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player)return (Player)event.getDamager();
        if(event.getDamager() instanceof Tameable){
            Tameable pet = (Tameable)event.getDamager();
            if(pet.isTamed() && pet.getOwner() instanceof Player)return (Player)pet.getOwner();
        }
        if(event.getDamager() instanceof Projectile){
            Projectile shot = (Projectile)event.getDamager();
            if(shot.getShooter() instanceof Player)return (Player)shot.getShooter();
        }
        return null;
    }
    private double getPermissionMultiplier(Player player){
        for(String permission : Config.getPermissionSet()){
            if(player.hasPermission(permission))return Config.getPermissionMultiplier(permission);
        }
        return 1.0;
    }
    private double getSpecialCaseMultiplier(Entity entity){
        double multi = 1.0;
        if(entity instanceof Zombie && ((Zombie)entity).isBaby()){
            multi *= Config.getSpecialCaseMultiplier("baby");
        }
        if(entity instanceof Creeper && ((Creeper)entity).isPowered()){
            multi *= Config.getSpecialCaseMultiplier("charged");
        }
        if(entity.hasMetadata("legendary")){
            multi *= 0.0;
        }
        if(inNoPay(entity.getLocation())){
            multi *= 0.0;
        }
        return multi;
    }
    private boolean inNoPay(Location location){
        List<String> noPayRegions = Config.getNoPayRegions();
        RegionContainer getRC = WorldGuard.getInstance().getPlatform().getRegionContainer();
        ApplicableRegionSet playerRegions = getRC.createQuery().getApplicableRegions(BukkitAdapter.adapt(location));
        for(ProtectedRegion reg : playerRegions){
            if(noPayRegions.contains(reg.getId())){
                return true;
            }
        }
        return false;
    }
}
