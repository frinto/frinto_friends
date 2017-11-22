package com.frinto.friends;

import me.Stijn.MPCore.Global.api.PlayerAPI;
import me.Stijn.MPCore.Global.database.MySQLConnection;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;


public class FriendCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Player player = (Player) sender;

        if (sender instanceof Player)
        {

            
            if (label.equalsIgnoreCase("friends"))
            {
                sender.sendMessage("/fadd <name>");
                sender.sendMessage("/fremove <name>");
                sender.sendMessage("/flist");
                return true;
            } else if (label.equalsIgnoreCase("fadd"))
            {
                String requestUUID = player.getUniqueId().toString();

                if (args.length == 0)
                {
                    sender.sendMessage(ChatColor.RED + "please specify the player you which to add");
                    sender.sendMessage(ChatColor.RED + "/fadd <name>");
                } else
                {
                    String targetUUID = null;

                    targetUUID = PlayerAPI.getPlayerUUID(args[0]);


                    if (targetUUID != null)
                    {
                        try
                        {

                            MySQLConnection conn = new MySQLConnection(Main.getMySQLConnectionDetails());
                            PreparedStatement ps = conn.open().prepareStatement("INSERT INTO Frinto_Friends (requester_uuid, target_uuid, accept_status, time_stamp) VALUES (?,?,?,?);");

                            ps.setString(1, requestUUID);
                            ps.setString(2, targetUUID);
                            ps.setInt(3, 0);
                            Calendar calendar = Calendar.getInstance();
                            java.sql.Timestamp ourJavaTimestampObject = new java.sql.Timestamp(calendar.getTime().getTime());
                            ps.setTimestamp(4, ourJavaTimestampObject);

                            conn.doUpdate(ps);
                        } catch (SQLException e)
                        {
                            e.printStackTrace();
                        }
                    } else
                    {
                        try
                        {
                            throw new PlayerHasNotPlayedBefore("player has not played here before exception");
                        } catch (PlayerHasNotPlayedBefore playerHasNotPlayedBefore)
                        {
                            playerHasNotPlayedBefore.printStackTrace();
                        }
                    }
                }


                //TESTING WILL REMOVE ALL THIS CODE LATER!
                try
                {
                    MySQLConnection conn2 = new MySQLConnection(Main.getMySQLConnectionDetails());
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

                                while (resultSet.next())
                                {
                                    String test2 = resultSet.getString("target_uuid");
                                    sender.sendMessage(test2);
                                    int age = 0;
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


                player.sendMessage(ChatColor.BLUE + "this is for adding friends");
            } else if (label.equalsIgnoreCase("fremove"))
            {
                player.sendMessage(ChatColor.BLUE + "this is for removing friends");
            } else if (label.equalsIgnoreCase("flist"))
            {
                player.sendMessage(ChatColor.BLUE + "this is for viewing friends");
            }
        } else

        {
            sender.sendMessage("Not a player please try again..");
            return false;
        }

        return false;
    }

}
