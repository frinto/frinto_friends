package com.frinto.friends;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin{
    
    @Override
    public void onEnable()
    {
        //TODO
      
    }
    @Override
    public void onDisable()
    {
        //TODO
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[]args)
    {
        if(label.equalsIgnoreCase("friends"))
        {
            if(!(sender instanceof Player))
            {
                sender.sendMessage("you are not a player");
                return false;
            }

            //TODO
            Player player = (Player) sender;
            player.sendMessage("fixed");
            return true;
        }
        
        return false;
    }
    
    
}
