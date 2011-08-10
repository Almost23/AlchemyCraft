package HanzVu;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

/*TODO:
 * [ ]Clean up code
 */

public class acBlockListener extends BlockListener {
    private AlchemyCraft plugin;
    
    
    boolean noCheating = true;
    boolean lock = false;
    
    public acBlockListener(AlchemyCraft plugin){
        this.plugin = plugin;
    }
    
    @Override
    public void onBlockPlace(BlockPlaceEvent ev){
        if(ev.getBlock().getType().equals(Material.DISPENSER)){
            String name = ev.getPlayer().getName();
            boolean furnace = false;
            
            if(plugin.alchemicObject.containsKey(name)){
                String type = plugin.alchemicObject.get(name);
                
                if(type.equals("furnace")){ 
                    plugin.furnaces.put(ev.getBlock().getLocation(),name);
                    furnace = true;
                    ev.getPlayer().sendMessage(ChatColor.GOLD + "Furnace created!");
                }
                        
                else{ 
                    plugin.stills.put(ev.getBlock().getLocation(),name);
                    ev.getPlayer().sendMessage(ChatColor.GOLD + "Still created!");
                }
                
                plugin.alchemicObject.remove(name);
                
                //If the player is level 0, creating a still/furnace adds to their exp.
                if(plugin.leveling.playerInfo.containsKey(name)){
                    
                    if(plugin.leveling.playerInfo.get(name)[0] == 0)
                        
                        
                        plugin.leveling.addEXP(name, furnace?1:2);
                        if(plugin.leveling.levelUP(name))
                            ev.getPlayer().sendMessage(ChatColor.GREEN + "Leveled up to level 1!");
                        return;
                }
            }
        }
        
       if(ev.getBlock().getType().equals(Material.FIRE) || ev.getBlock().getType().equals(Material.WATER)){
           if(noCheating) ev.setCancelled(true);
       }
    }
    
    
    @Override
    public void onBlockDispense(BlockDispenseEvent ev){
        
        
        if(!lock){
            Location loc = ev.getBlock().getLocation();
            int exp = -2;
            if(plugin.furnaces.containsKey(loc)){
                lock = true;
               
                exp = plugin.recipes.Transmute(plugin.leveling.playerInfo.get(plugin.furnaces.get(loc))[0], (Dispenser)ev.getBlock().getState());
                plugin.leveling.addEXP(plugin.furnaces.get(loc), exp);
                   
                lock = false;
                ev.setCancelled(true);
            }
            else if(plugin.stills.containsKey(loc)){
                lock = true;
                exp = plugin.recipes.Distill(plugin.leveling.playerInfo.get(plugin.stills.get(loc))[0], (Dispenser)ev.getBlock().getState());
                plugin.leveling.addEXP(plugin.stills.get(loc), exp);
                lock = false;
                ev.setCancelled(true);
            }
        }
    }
    
    
    //If a user punches a dispenser it will notify them:
    //a: if it is a still/furnace
    //b: who owns it
    @Override
    public void onBlockDamage(BlockDamageEvent ev){
        if(ev.getBlock().getType().equals(Material.DISPENSER)){
            if(plugin.furnaces.containsKey(ev.getBlock().getLocation())){
                ev.getPlayer().sendMessage(ChatColor.AQUA + plugin.furnaces.get(ev.getBlock().getLocation()) + "'s furnace.");
            }
            else if(plugin.stills.containsKey(ev.getBlock().getLocation())){
                ev.getPlayer().sendMessage(ChatColor.AQUA + plugin.stills.get(ev.getBlock().getLocation()) + "'s still.");
            }
        }
    }
}
