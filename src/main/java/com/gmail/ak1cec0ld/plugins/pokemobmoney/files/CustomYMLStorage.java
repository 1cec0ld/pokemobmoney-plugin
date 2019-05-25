package com.gmail.ak1cec0ld.plugins.pokemobmoney.files;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class CustomYMLStorage {
    private String filePath;
    private File file = null;
    private YamlConfiguration yml = null;
    public CustomYMLStorage(JavaPlugin plugin, String path){
        filePath = plugin.getDataFolder().getParent()+File.separator+path;
        file = new File(filePath);
        yml = YamlConfiguration.loadConfiguration(file);
        this.save();
    }
    public File getFile(){
        return this.file;
    }
    public YamlConfiguration getYamlConfiguration(){
        return this.yml;
    }
    public void setYamlConfiguration(YamlConfiguration myYml){
        yml = myYml;
    }
    public void save(){
        try{
            this.yml.save(file);
        } catch(IOException e){}
    }
    
    public void reload(){
        file = new File(filePath);
        yml = YamlConfiguration.loadConfiguration(file);
        this.save();
    }
}
