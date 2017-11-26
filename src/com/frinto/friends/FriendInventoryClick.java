package com.frinto.friends;

import net.minecraft.server.v1_12_R1.PacketPlayOutEntityHeadRotation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.annotation.ElementType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class FriendInventoryClick implements Listener
{
    public static HashMap<String, String> inv = new HashMap<>();
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        Inventory inventory = event.getInventory();
        
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
        if(item.getType() == Material.CACTUS)
        {
            event.setCancelled(true);
            player.closeInventory();

            player.getWorld().playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 1);

            
            openInv(player);
            
        }
        if(event.getSlot() == event.getRawSlot())
        {
            if(this.inv.containsKey(player.getName()))
            {
                if(this.inv.get(player.getName()).equals("friendadd"))
                {
                    event.setCancelled(true);
                    player.updateInventory();
                    if(item != null)
                    {
                        if(item.getType() == Material.SKULL_ITEM)
                        {
                            String name = item.getItemMeta().getDisplayName();
                            Player p = Bukkit.getPlayerExact(name);
                            if(p != null)
                            {
                                player.chat("/fadd " + p.getName());
                            }
                            else
                            {
                                player.sendMessage("player is not online");
                            }
                            this.closeInv(player);
                        }
                        
                    }
                }
            }
        }
        else
        {
            event.setCancelled(true);
            player.closeInventory();
        }
        
    }
    
    public void openInv(Player player)
    {
        int lines = 0;
        Collection<Player> players = (Collection<Player>) Bukkit.getOnlinePlayers();
        while(lines < players.size() -1)
        {
            lines++;
        }
        Inventory inventory2 = Bukkit.createInventory(null, lines * 9, "Add Friends");

        Iterator iterator = players.iterator();

        int  slot = 0;
        while(iterator.hasNext())
        {
            Player p = (Player) iterator.next();

            if(p != player)
            {
                ItemStack itemz = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                ItemMeta meta = itemz.getItemMeta();
                meta.setDisplayName(p.getName());
                itemz.setItemMeta(meta);
                inventory2.setItem(slot, itemz);
                slot++;
            }
        }
        player.openInventory(inventory2);
        this.inv.put(player.getName(), "friendadd");
    }
    
    public void closeInv(Player player)
    {
        if(this.inv.containsKey(player.getName()))
        {
            player.closeInventory();
            this.inv.remove(player.getName());
        }
    }
    
//    @EventHandler
//    public void onClose(InventoryCloseEvent e)
//    {
//        this.closeInv((Player)e.getPlayer());
//    }
//    
//    @EventHandler
//    public void onClick(InventoryClickEvent e)
//    {
//        Player player = (Player) e.getWhoClicked();
//        if(e.getSlot() == e.getRawSlot())
//        {
//            if(this.inv.containsKey(player.getName()))
//            {
//                if(this.inv.get(player.getName()).equals("friendadd"))
//                {
//                    e.setCancelled(true);
//                    player.updateInventory();
//                    ItemStack item = e.getCurrentItem();
//                    if(item != null)
//                    {
//                        String name = item.getItemMeta().getDisplayName();
//                        Player p = Bukkit.getPlayerExact(name);
//                        if(p != null)
//                        {
//                            player.chat("/fadd" + p.getName());
//                        }
//                        else
//                        {
//                            player.sendMessage("player is not online");
//                        }
//                        this.closeInv(player);
//                    }
//                }
//            }
//        }
//    }
//
//    @EventHandler
//    public void onKick(PlayerKickEvent e)
//    {
//        this.closeInv((Player)e.getPlayer());
//    }
//    @EventHandler
//    public void onQuit(PlayerQuitEvent e)
//    {
//        this.closeInv((Player)e.getPlayer());
//    }
}
