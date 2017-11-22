package com.frinto.friends;


import me.Stijn.MPCore.Global.database.MySQLConnection;
import me.Stijn.MPCore.Global.database.MySQLConnectionDetails;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin
{

    @Override
    public void onEnable()
    {
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
