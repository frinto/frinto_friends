package com.frinto.friends;

import jdk.net.SocketFlow;
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
            }else if(label.equalsIgnoreCase("faccept"))
            {
                FriendCommand.statusOfAccept = true;

                if(statusOfAccept)
                {
                    try
                    {
                        requestUUID = player.getUniqueId().toString();
                        
                        MySQLConnection conn = new MySQLConnection(Main.getMySQLConnectionDetails());
                        PreparedStatement ps = conn.open().prepareStatement("INSERT INTO Frinto_Friends (requester_uuid, target_uuid, accept_status, time_stamp) VALUES (?,?,?,?);");

                        ps.setString(1, requestUUID);
                        ps.setString(2, targetUUID);
                        ps.setInt(3, 0);
                        Calendar calendar = Calendar.getInstance();
                        java.sql.Timestamp ourJavaTimestampObject = new java.sql.Timestamp(calendar.getTime().getTime());
                        ps.setTimestamp(4, ourJavaTimestampObject);

                        conn.doUpdate(ps);
                        sender.sendMessage("player accepted the friend request and has been added!");
                        String nameOfTarget = Bukkit.getServer().getOfflinePlayer(UUID.fromString(targetUUID)).getName();

                        player.sendMessage(ChatColor.BLUE + "User " + (nameOfTarget) + " has been added");
                    } catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                }
                
            }else if(label.equalsIgnoreCase("fdecline"))
            {
                FriendCommand.statusOfAccept = false;
                
                if(statusOfAccept == false)
                {
                    sender.sendMessage(ChatColor.RED +"player has declined or hasn't accepted the friend request!");
                }
            }
        } else

        {
            sender.sendMessage("Not a player please try again..");
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
            if(targetPlayer.isOnline())
            {
                
                //TESTING TESTING TESTING
                TextComponent acceptMsg = new TextComponent( "CLICK ME [Accept]" );
                acceptMsg.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                acceptMsg.setBold(true);
                acceptMsg.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/faccept" ));
                TextComponent declineMsg = new TextComponent( "CLICK ME [Decline]" );
                declineMsg.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND,  "/fdecline"));
                declineMsg.setColor(net.md_5.bungee.api.ChatColor.RED);
                declineMsg.setBold(true);
                
                targetPlayer.sendMessage(ChatColor.AQUA + "You recieved a friend request from: " + sender.getName());

                targetPlayer.spigot().sendMessage(acceptMsg);
                targetPlayer.spigot().sendMessage(declineMsg);
                
            }
        } 
        else
        {
            sender.sendMessage("Player is not online!");
        }
    }


}
