package com.frinto.friends;

import com.sun.prism.shader.DrawRoundRect_ImagePattern_AlphaTest_Loader;
import me.Stijn.MPCore.Global.database.MySQLConnection;
import me.Stijn.MPCore.Global.database.MySQLConnectionDetails;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.sql.Timestamp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
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
            String OfflinePlayerName = null;
            
            if(args.length == 0)
            {
                sender.sendMessage(ChatColor.RED + "please specify the player you which to add");
                sender.sendMessage(ChatColor.RED + "/fadd <name>");
            }else
            {
                OfflinePlayerName = args[0];
            }
            
            String targetUUID;

            OfflinePlayer op = Bukkit.getOfflinePlayer(OfflinePlayerName);
            
            if(op.hasPlayedBefore())
            {
                targetUUID = op.getUniqueId().toString();

                MySQLConnection conn3 = new MySQLConnection(conn_details);
                
                String sqlstatement = "INSERT INTO Frinto_Friends" +
                                      "(requester_uuid, target_uuid, accept_status, time_stamp) VALUES" +
                                      "(?,?,?,?)";
                try
                {
                    PreparedStatement newStatement = conn3.open().prepareStatement(sqlstatement);
                    newStatement.setString(1, requestUUID);
                    newStatement.setString(2, targetUUID);
                    newStatement.setInt(3, 0);
                    Calendar calendar = Calendar.getInstance();
                    java.sql.Timestamp ourJavaTimestampObject = new java.sql.Timestamp(calendar.getTime().getTime());
                    newStatement.setTimestamp(4, ourJavaTimestampObject);
                    
                    conn3.doUpdate(newStatement);
                    
                } catch (SQLException e)
                {
                    e.printStackTrace();
                }
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
            
            
            
            //TESTING WILL REMOVE ALL THIS CODE LATER!
            try
            {
                PreparedStatement ps = conn2.open().prepareStatement("SELECT target_uuid FROM Frinto_Friends WHERE requester_uuid = ?;");
                ps.setString(1, requestUUID);
                conn2.doQuery(ps, new MySQLConnection.MySQLConnectionBeforeCloseCallback()
                {
                    @Override
                    public void executeBeforeClose(ResultSet resultSet)
                    {
                        try
                        {
                            resultSet = ps.executeQuery();
                            
                            while(resultSet.next())
                            {
                                String test2 = resultSet.getString("target_uuid");
                                sender.sendMessage(test2);
                            }
                            
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
