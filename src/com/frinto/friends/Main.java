package com.frinto.friends;


import me.Stijn.MPCore.Global.api.PlayerAPI;
import me.Stijn.MPCore.Global.database.MySQLConnection;
import me.Stijn.MPCore.Global.database.MySQLConnectionDetails;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


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
        
        
        
        new MySQLConnection(Main.getMySQLConnectionDetails()).doUpdate("CREATE TABLE IF NOT EXISTS Friend_Requests(" +
                "fromUser VARCHAR(36) NOT NULL," +
                "toUser VARCHAR(36) NOT NULL," +
                "requestCompleted INT(1) NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1");
        
        

        getServer().getPluginManager().registerEvents(this, this);

        registerCommands();
        registerEvents();
    }

    @Override
    public void onDisable()
    {
        //TODO
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        Player joinedPlayer = e.getPlayer();
        
        MySQLConnection connJoin = new MySQLConnection(Main.getMySQLConnectionDetails());
        try
        {
            PreparedStatement statementToEx = connJoin.open().prepareStatement("SELECT DISTINCT target_uuid FROM Frinto_Friends WHERE requester_uuid = ?;");
            statementToEx.setString(1, joinedPlayer.getUniqueId().toString());

            connJoin.doQuery(statementToEx, new MySQLConnection.MySQLConnectionBeforeCloseCallback()
            {
                @Override
                public void executeBeforeClose(ResultSet resultSet)
                {
                    try
                    {
                        resultSet = statementToEx.getResultSet();

                        while (resultSet.next())
                        {
                            String targetStringUUID = resultSet.getString("target_uuid");
                            Player targetPlayer = Bukkit.getPlayer(PlayerAPI.getPlayerUsername(targetStringUUID));

                            if (targetPlayer != null)
                            {
                                if (targetPlayer.isOnline())
                                {
                                    targetPlayer.sendMessage(ChatColor.AQUA + "Your friend " + joinedPlayer.getName() + " is online!");
                                }
                            }
                            
                        }
                        
                    } catch (SQLException e1)
                    {
                        e1.printStackTrace();
                    }


                }
            });


        } catch (SQLException e1)
        {
            e1.printStackTrace();
        }
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
    
    public void registerEvents()
    {
        PluginManager pluginManager = getServer().getPluginManager();
        
        pluginManager.registerEvents(new FriendInventoryClick(), this);
    }

    public static MySQLConnectionDetails getMySQLConnectionDetails()
    {
        return conn_details;
    }


}
