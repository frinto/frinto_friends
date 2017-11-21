package com.frinto.friends;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin
{

    @Override
    public void onEnable()
    {
        //TODO
        registerCommands();
    }

    @Override
    public void onDisable()
    {
        //TODO
    }
    
    public void registerCommands()
    {
        getCommand("friends").setExecutor(new FriendCommand());
        getCommand("fadd").setExecutor(new FriendCommand());
        getCommand("fremove").setExecutor(new FriendCommand());
        getCommand("flist").setExecutor(new FriendCommand());
    }
    
}
