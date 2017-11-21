package com.frinto.friends;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

            //TODO
            player.sendMessage("/fadd <name>");
            player.sendMessage("/fremove <name>");
            player.sendMessage("/flist");
            return true;
        }else if (label.equalsIgnoreCase("fadd"))
        {
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
