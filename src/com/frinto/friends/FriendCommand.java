package com.frinto.friends;

import me.Stijn.MPCore.Global.database.MySQLConnection;
import me.Stijn.MPCore.Global.database.MySQLConnectionDetails;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class FriendCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[]args)
    {
        Player player = (Player) sender;
        if(label.equalsIgnoreCase("friends"))
        {
            if(!(sender instanceof Player))
            {
                sender.sendMessage("Not a player please try again..");
                return false;
            }
            
            player.sendMessage("/fadd <name>");
            player.sendMessage("/fremove <name>");
            player.sendMessage("/flist");
            return true;
        }else if (label.equalsIgnoreCase("fadd"))
        {
            MySQLConnectionDetails conn_details = new MySQLConnectionDetails("mp_s_g_FriendSystem", "mp_u_Q7nGBf", "PYgzVG8oURnVWzTu");
            

            MySQLConnection conn = new MySQLConnection(conn_details);
            conn.open();


            String createTableSQL = "CREATE TABLE IF NOT EXISTS Frinto_Friends(" + 
                    "requester_uuid varchar(36) NOT NULL," + 
                    "target_uuid varchar(36) NOT NULL," + 
                    "accept_status int(1) NOT NULL," + 
                    "time_stamp timestamp NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1";
            
            conn.doUpdate(createTableSQL);


            MySQLConnection conn2 = new MySQLConnection(conn_details);
            
            String requestUUID = player.getUniqueId().toString();
            String OfflinePlayerName = args[0];
            String targetUUID;

            OfflinePlayer op = Bukkit.getOfflinePlayer(OfflinePlayerName);
            
            if(op.hasPlayedBefore())
            {
                targetUUID = op.getUniqueId().toString();
            }
            else
            {
                targetUUID = null;
                try
                {
                    throw new PlayerHasNotPlayedBefore("player has not played here before exception");
                } catch (PlayerHasNotPlayedBefore playerHasNotPlayedBefore)
                {
                    playerHasNotPlayedBefore.printStackTrace();
                }
            }
            
            try
            {
                PreparedStatement ps = conn2.open().prepareStatement("SELECT * FROM Frinto_Friends;");
                conn2.doQuery(ps, new MySQLConnection.MySQLConnectionBeforeCloseCallback()
                {
                    @Override
                    public void executeBeforeClose(ResultSet resultSet)
                    {
                        try
                        {
                            String c = resultSet.getString("requester_uuid");
                            int test = 0;
                        } catch (SQLException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (SQLException e)
            {
                e.printStackTrace();
            }


            player.sendMessage(ChatColor.BLUE +"this is for adding friends");
        }else if (label.equalsIgnoreCase("fremove"))
        {
            player.sendMessage(ChatColor.BLUE +"this is for removing friends");
        }
        else if(label.equalsIgnoreCase("flist"))
        {
            player.sendMessage(ChatColor.BLUE + "this is for viewing friends");
        }

        return false;
    }
    
}
