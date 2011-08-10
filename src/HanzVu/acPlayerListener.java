package HanzVu;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author HanzVu
 */

/*TODO:
 * 
 */

public class acPlayerListener extends PlayerListener {
    private AlchemyCraft plugin;
    
    public acPlayerListener(AlchemyCraft plugin){
        this.plugin = plugin;
    }
    
    @Override
    public void onPlayerJoin(PlayerJoinEvent ev){
        plugin.leveling.LoadLevelingInfo(ev.getPlayer());
    }
    
    @Override
    public void onPlayerQuit(PlayerQuitEvent ev){
        plugin.leveling.StoreLevelingInfo(ev.getPlayer());
    }
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent ev){
        
        //In order to preserve different alchemical abilities between players,
        // a still/furnace can only be used by its creator.
        if(ev.getClickedBlock().getType().equals(Material.DISPENSER)){
            
            String pname = ev.getPlayer().getName();
            Location loc = ev.getClickedBlock().getLocation();
            
            
            if(plugin.stills.containsKey(loc)){
                if(!plugin.stills.get(loc).equals(pname)){
                    ev.getPlayer().sendMessage(ChatColor.RED + "You do not have access to this still!");
                    ev.setCancelled(true);
                }
            }
            else
            if(plugin.furnaces.containsKey(loc)){
               if(!plugin.furnaces.get(loc).equals(pname)){
                    ev.getPlayer().sendMessage(ChatColor.RED + "You do not have access to this furnace!");
                    ev.setCancelled(true);     
               }
            }

        }
    }
    
  
}
