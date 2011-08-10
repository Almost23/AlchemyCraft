package HanzVu;

import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/*TODO:
 *[ ]View recipes already discovered
 *[ ]View levels of distillation/transmutation
 *[ ]View exp needed for level up
 *[ ]Add separate class for FileIO
 */
public class AlchemyCraft extends JavaPlugin{
    public static final Logger log = Logger.getLogger("Minecraft");
    
    private final acPlayerListener playerListener = new acPlayerListener(this);
    private final acBlockListener blockListener = new acBlockListener(this);
    public final acRecipes recipes = new acRecipes(this);
    public final acLeveling leveling = new acLeveling(this);
    
    public HashMap<Location, String> stills = new HashMap<Location, String>();
    public HashMap<Location, String> furnaces = new HashMap<Location, String>();
    public HashMap<String, String> alchemicObject = new HashMap<String, String>();
    
    @Override
    public void onEnable(){
        log.info("AlchemyCraft version " + this.getDescription().getVersion() + " started.");
        
        
       
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Low, this);
        pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Low, this);
        pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Low, this);
        
        pm.registerEvent(Type.BLOCK_DISPENSE, blockListener, Priority.Low, this);
        pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Low, this);
        pm.registerEvent(Type.BLOCK_DAMAGE, blockListener, Priority.Low, this);
        //recipes.createRecipes();
        
    }
    
    @Override
    public void onDisable(){
        
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        Player player=null;
        if(sender instanceof Player) player = (Player)sender;
        if(player!=null){
            String name = player.getName();
            if(label.equalsIgnoreCase("still")){
                if(alchemicObject.containsKey(name)) player.sendMessage("You are already creating a " + alchemicObject.get(name));
                else{
                    alchemicObject.put(player.getName(), "still");
                    player.sendMessage(ChatColor.GOLD + "Still ready to be created");
                }
            }
                

            if(label.equalsIgnoreCase("furnace")){
                if(alchemicObject.containsKey(name))
                    player.sendMessage("You are already creating a " + alchemicObject.get(name));
                else{
                    alchemicObject.put(player.getName(), "furnace");
                    player.sendMessage(ChatColor.GOLD + "Furnace ready to be created");
                }
            }
        }
        return true;
    }
}
