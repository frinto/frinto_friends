package com.frinto.friends;


import me.Stijn.MPCore.Global.database.MySQLConnection;
import me.Stijn.MPCore.Global.database.MySQLConnectionDetails;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin implements Listener
{
    
    public static Player requester;

	private static MySQLConnectionDetails conn_details = new MySQLConnectionDetails("mp_s_g_FriendSystem", "mp_u_Q7nGBf", "PYgzVG8oURnVWzTu");
	
    @Override
    public void onEnable()
    {
    	new MySQLConnection(Main.getMySQLConnectionDetails()).doUpdate("CREATE TABLE IF NOT EXISTS Frinto_Friends(" + 
                "requester_uuid varchar(36) NOT NULL," + 
                "target_uuid varchar(36) NOT NULL," +
                "time_stamp timestamp NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1");
    	
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
        getCommand("faccept").setExecutor(new FriendCommand());
        getCommand("fdecline").setExecutor(new FriendCommand());
    }
    
    public static MySQLConnectionDetails getMySQLConnectionDetails() {
    	return conn_details;
    }
    
    
    
}
