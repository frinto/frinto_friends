package com.frinto.friends;

import me.Stijn.MPCore.Global.database.MySQLConnection;


import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

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
import java.util.Calendar;
import java.util.UUID;


public class FriendCommand implements CommandExecutor
{
    private static boolean statusOfAccept = false;
    private String requestUUID = null;
    private String targetUUID = null;

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
                requestUUID = player.getUniqueId().toString();
                Main.requester = player;


                if (args.length == 0)
                {
                    sender.sendMessage(ChatColor.RED + "please specify the player you which to add");
                    sender.sendMessage(ChatColor.RED + "/fadd <name>");
                } else
                {
                    String OfflinePlayerName = null;


                    OfflinePlayerName = args[0];
                    


                    OfflinePlayer op = Bukkit.getServer().getOfflinePlayer(OfflinePlayerName);


                    if (op.hasPlayedBefore() || (op.getPlayer().isOnline()))
                    {
                        targetUUID = op.getUniqueId().toString();
                        statusOfAccept = true;
                        player.sendMessage(ChatColor.RED + "sending friend request to....." + args[0]);
                        sendFriendRequest(sender.getName(), args[0]);

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
            } else if (label.equalsIgnoreCase("fremove"))
            {
                requestUUID = player.getUniqueId().toString();

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
                            PreparedStatement ps = conn.open().prepareStatement("DELETE FROM Frinto_Friends WHERE target_uuid = ? AND requester_uuid = ?;");


                            ps.setString(1, targetUUID);
                            ps.setString(2, requestUUID);

                            conn.doUpdate(ps);

                            MySQLConnection conn777 = new MySQLConnection(Main.getMySQLConnectionDetails());
                            PreparedStatement ps777 = conn777.open().prepareStatement("DELETE FROM Frinto_Friends WHERE target_uuid = ? AND requester_uuid = ?;");


                            ps777.setString(1, requestUUID);
                            ps777.setString(2, targetUUID);

                            conn777.doUpdate(ps777);
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

                requestUUID = player.getUniqueId().toString();

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
                                    String targetStringUUID = resultSet.getString("target_uuid");
                                    OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(UUID.fromString(targetStringUUID));

                                    if (offlinePlayer.hasPlayedBefore())
                                    {
                                        String onlineStatus = "Offline";
                                        if(offlinePlayer.isOnline())
                                        {
                                            onlineStatus = " [ONLINE]";
                                        }else
                                        {
                                            onlineStatus = " [OFFLINE]";
                                        }
                                        
                                        sender.sendMessage( ChatColor.GREEN + offlinePlayer.getName() + onlineStatus);
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
            } else if (label.equalsIgnoreCase("faccept"))
            {
                
                
                if (statusOfAccept)
                {
                    player.sendMessage(ChatColor.AQUA + "You have Accepted the friend request!");
                    try
                    {
                        targetUUID = player.getUniqueId().toString();
                        Player requesterPlayer = Main.requester;
                        requestUUID = requesterPlayer.getUniqueId().toString();

                        MySQLConnection conn = new MySQLConnection(Main.getMySQLConnectionDetails());
                        PreparedStatement ps = conn.open().prepareStatement("INSERT INTO Frinto_Friends (requester_uuid, target_uuid, time_stamp) VALUES (?,?,?);");

                        ps.setString(1, requestUUID);
                        ps.setString(2, targetUUID);
                        Calendar calendar = Calendar.getInstance();
                        java.sql.Timestamp ourJavaTimestampObject = new java.sql.Timestamp(calendar.getTime().getTime());
                        ps.setTimestamp(3, ourJavaTimestampObject);

                        conn.doUpdate(ps);

                        MySQLConnection conn33 = new MySQLConnection(Main.getMySQLConnectionDetails());
                        PreparedStatement ps33 = conn33.open().prepareStatement("INSERT INTO Frinto_Friends (requester_uuid, target_uuid, time_stamp) VALUES (?,?,?);");

                        ps33.setString(1, targetUUID);
                        ps33.setString(2, requestUUID);
                        Calendar calendar33 = Calendar.getInstance();
                        java.sql.Timestamp ourJavaTimestampObject33 = new java.sql.Timestamp(calendar.getTime().getTime());
                        ps33.setTimestamp(3, ourJavaTimestampObject);

                        conn33.doUpdate(ps33);
                        
                        
                        requesterPlayer.sendMessage(ChatColor.AQUA + "player accepted the friend request and has been added!");
                        String nameOfTarget = Bukkit.getServer().getOfflinePlayer(UUID.fromString(targetUUID)).getName();
                        String nameOfRequester = Bukkit.getServer().getOfflinePlayer(UUID.fromString(requestUUID)).getName();

                        player.sendMessage(ChatColor.BLUE + "User " + (nameOfRequester) + " has been added");
                        requesterPlayer.sendMessage(ChatColor.BLUE + "User " + (nameOfTarget) + " has been added");
                        statusOfAccept = false;
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                }else
                {
                    player.sendMessage(ChatColor.RED + "you cant click anymore");
                }

            } else if (label.equalsIgnoreCase("fdecline"))
            {
                
                Player requesterPlayer = Main.requester;

                if (statusOfAccept)
                {
                    player.sendMessage(ChatColor.AQUA + "You have declined the friend request!");
                    requesterPlayer.sendMessage(ChatColor.RED + "Player has declined!");
                    statusOfAccept = false;
                }else
                {
                    player.sendMessage(ChatColor.RED + "you cant click anymore");
                }
            }
        } else

        {
            sender.sendMessage(ChatColor.RED + "Not a player please try again..");
            return false;
        }

        return false;
    }

    private void sendFriendRequest(String name, String arg)
    {
        Player targetPlayer = Bukkit.getPlayerExact(arg);
        Player sender = Bukkit.getPlayerExact(name);

        if (targetPlayer != null)
        {

            if (targetPlayer.isOnline())
            {
                
                TextComponent clickMsg = new TextComponent("Click ");

                //TESTING TESTING TESTING
                TextComponent acceptMsg = new TextComponent("Accept");
                acceptMsg.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                acceptMsg.setBold(true);
                acceptMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/faccept"));
                
                
                
                TextComponent declineMsg = new TextComponent("Decline");
                declineMsg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fdecline"));
                declineMsg.setColor(net.md_5.bungee.api.ChatColor.RED);
                declineMsg.setBold(true);
                
                
                clickMsg.addExtra(acceptMsg);
                
                TextComponent line = new TextComponent(" | ");
                
                clickMsg.addExtra(line);
                
                clickMsg.addExtra(declineMsg);

                targetPlayer.sendMessage("---------------------------------------------");

                targetPlayer.sendMessage(ChatColor.AQUA + "You recieved a friend request from: " + sender.getName());

                targetPlayer.spigot().sendMessage(clickMsg);
                targetPlayer.sendMessage("---------------------------------------------");

            }
        } else
        {
            sender.sendMessage("Player is not online!");
        }
    }


}
