package com.frinto.friends;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FriendInventoryClick implements Listener
{
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        Inventory inventory = event.getInventory();
        
        if(!inventory.getTitle().equals("Friends System"))
        {
            return;
        }
        
        if(!(event.getWhoClicked() instanceof Player))
        {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if(item.getType() == Material.COMPASS)
        {
            
            player.sendMessage(ChatColor.GOLD +"/fadd <name>");
            player.sendMessage(ChatColor.GOLD +"/fremove <name>");
            player.sendMessage(ChatColor.GOLD +"/flist");
            
            event.setCancelled(true);
            player.closeInventory();

            player.getWorld().playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 1);
        }
        if(item.getType() == Material.CAKE)
        {
            player.chat("/flist");
            
            event.setCancelled(true);
            player.closeInventory();

            player.getWorld().playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 1);
        }
        else
        {
            event.setCancelled(true);
            player.closeInventory();
        }
        
        
        
    }
}
