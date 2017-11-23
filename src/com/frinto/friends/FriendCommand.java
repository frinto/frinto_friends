package com.frinto.friends;

import me.Stijn.MPCore.Global.database.MySQLConnection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.security.PublicKey;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.UUID;


public class FriendCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Player player = (Player) sender;
        String targetUUID = null;

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
                    String OfflinePlayerName = null;

                    if (args.length == 0)
                    {
                        sender.sendMessage(ChatColor.RED + "please specify the player you which to add");
                        sender.sendMessage(ChatColor.RED + "/fadd <name>");
                    } else
                    {
                        OfflinePlayerName = args[0];
                    }

                    OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(OfflinePlayerName);


                    if (op.hasPlayedBefore() || (op.getPlayer().isOnline()))
                    {
                        targetUUID = op.getUniqueId().toString();

                        boolean statusOfRequestCompleted = sendFriendRequest(sender.getName(), args[0]);

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
                        sender.sendMessage("player has not played on this server");

                        try
                        {
                            throw new PlayerHasNotPlayedBefore("player has not played here before exception");
                        } catch (PlayerHasNotPlayedBefore playerHasNotPlayedBefore)
                        {
                            playerHasNotPlayedBefore.printStackTrace();
                        }
                    }
                }


                String nameOfTarget = Bukkit.getServer().getOfflinePlayer(UUID.fromString(targetUUID)).getName();

                player.sendMessage(ChatColor.BLUE + "User " + (nameOfTarget) + " has been added");
            } else if (label.equalsIgnoreCase("fremove"))
            {
                String requestUUID = player.getUniqueId().toString();

                if (args.length == 0)
                {
                    sender.sendMessage(ChatColor.RED + "please specify the player you which to remove");
                    sender.sendMessage(ChatColor.RED + "/fremove <name>");
                } else
                {
                    String OfflinePlayerName = null;

                    if (args.length == 0)
                    {
                        sender.sendMessage(ChatColor.RED + "please specify the player you which to remove");
                        sender.sendMessage(ChatColor.RED + "/fremove <name>");
                    } else
                    {
                        OfflinePlayerName = args[0];
                    }

                    OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(OfflinePlayerName);


                    if (op.hasPlayedBefore())
                    {
                        targetUUID = op.getUniqueId().toString();

                        try
                        {

                            MySQLConnection conn = new MySQLConnection(Main.getMySQLConnectionDetails());
                            PreparedStatement ps = conn.open().prepareStatement("DELETE FROM Frinto_Friends WHERE target_uuid = ?;");


                            ps.setString(1, targetUUID);

                            conn.doUpdate(ps);
                        } catch (SQLException e)
                        {
                            e.printStackTrace();
                        }
                    } else
                    {
                        sender.sendMessage("player has not played on this server");

                        try
                        {
                            throw new PlayerHasNotPlayedBefore("player has not played here before exception");
                        } catch (PlayerHasNotPlayedBefore playerHasNotPlayedBefore)
                        {
                            playerHasNotPlayedBefore.printStackTrace();
                        }
                    }
                }


                String nameOfTarget = Bukkit.getServer().getOfflinePlayer(UUID.fromString(targetUUID)).getName();

                player.sendMessage(ChatColor.BLUE + "User " + (nameOfTarget) + " has been removed");
            } else if (label.equalsIgnoreCase("flist"))
            {
                player.sendMessage(ChatColor.BLUE + "Here is your list of friends: ");

                String requestUUID = player.getUniqueId().toString();

                //TESTING WILL REMOVE ALL THIS CODE LATER!
                try
                {
                    MySQLConnection conn2 = new MySQLConnection(Main.getMySQLConnectionDetails());
                    PreparedStatement ps = conn2.open().prepareStatement("SELECT DISTINCT target_uuid FROM Frinto_Friends WHERE requester_uuid = ?;");
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
                                    OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(test2));

                                    if (offlinePlayer.hasPlayedBefore())
                                    {
                                        sender.sendMessage(offlinePlayer.getName());
                                    } else
                                    {
                                        sender.sendMessage("player has not played on this server");
                                    }

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
            }
        } else

        {
            sender.sendMessage("Not a player please try again..");
            return false;
        }

        return false;
    }

    private boolean sendFriendRequest(String name, String arg)
    {
        Player targetPlayer = Bukkit.getPlayerExact(arg);
        Player sender = Bukkit.getPlayerExact(name);

        if (targetPlayer != null)
        {
            if(targetPlayer.isOnline())
            {
                targetPlayer.sendMessage(ChatColor.AQUA + "You recieved a friend request from: " + sender.getName());
                return true; 
            }
        } 
        else
        {
            sender.sendMessage("Player is not online!");
            return false;
        }
        return false;
    }


}
