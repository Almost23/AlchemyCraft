package HanzVu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import org.bukkit.entity.Player;

public class acLeveling {
    private AlchemyCraft plugin;
    
    
    /* TODO:
     * [ ]Individual exp for Distilling/Transmuting
     *     [ ]Higher Transmuting ability unlocks new materials
     *     [ ]Higher Distilling ability gives greater yeild on distilled items
     * [ ]Rewrite addEXP and levelUP code to use less preprocessing
     */
    
    
    
    //A hashmap to hold a player's leveling info
    public HashMap<String, int[]> playerInfo = new HashMap<String, int[]>();
    
    //A list of keys for the int[] in playerInfo (A workaround for a map of maps)
    String[] dataStructure = {"level","exp"};
    
    //Exp list necessary to level up
    //format:
    //0x? | **<<24
    
    //? = bits marking recipes at this level that the user must find to level up
    //** = number of transmutations necessary to level up (resets to 0 every level)
    
    int[] EXP = {
        0xF,                //level 0 -> level 1 
        0xF | 10<<24,       //level 1 -> level 2
        0x7 | 15<<24,       //level 2 -> level 3
        0x7 | 20<<24,       //level 3 -> level 4
        0x7 | 25<<24,       //level 4 -> level 5
        0x7 | 30<<24        //level 5 -> level 6
    };
    
    public acLeveling(AlchemyCraft plugin){
        this.plugin = plugin;
    }
    
    private File OpenFile(String file){
        File pfile = new File(file);
        if(!pfile.exists()){
            pfile.getParentFile().mkdirs();
           try{pfile.createNewFile();}            
           catch(IOException e){return null;}            
        }
        return pfile;
    }
    
    
    private int[] ReadFromFile(File pfile){
        int[] ret = {0,0};
        FileInputStream in;
            try {
                in = new FileInputStream(pfile);
            }
                    
            //This should never be called since we already checked to see if the file existed.
            catch (FileNotFoundException ex) {
                return ret;
            }
            Properties props = new Properties();
            try {
                props.load(in);
                for(int i=0; i<dataStructure.length; i++)
                 ret[i] = Integer.parseInt(props.getProperty(dataStructure[i], "-1"));
                
//                ret[0] = Integer.parseInt(props.getProperty(dataStructure[0], "-1"));
//                ret[1] = Integer.parseInt(props.getProperty("exp", "0")); 
            } catch (IOException ex) {
               
            }
            catch(NumberFormatException ex){
                 
            }
            
            return ret;
    }
    
    private void SaveToFile(File file, int[] data, String comments){
        try{
            Properties prop = new Properties();
            FileOutputStream out = new FileOutputStream(file);
            
            
            for(int i=0; i<dataStructure.length; i++)
                prop.put(dataStructure[i], String.valueOf(data[i]));

            prop.store(out, comments);
        }
                
        catch(IOException ex)
        {
            
        }
        
        
    }
    
    private boolean CheckCorruption(int Exp){
        int corrupt = 0;
        
        for(int i = 0; i<23; i++){
            corrupt += (Exp>>i)&1;
        }
        
        if((Exp >> 24) < corrupt)   
        return true;
        
        return false;
    }
    
        public void LoadLevelingInfo(Player player){
            String name = player.getName();
            
            //Opens the alchemy file for the player
            File file = OpenFile("plugins\\Alchemy\\" + name + ".txt");
            
            //Loads the player's data if their file exists
            int[] data = {0,0};
            if(file == null){
                player.sendMessage("Could not create an Alchemy file for you.");
                AlchemyCraft.log.info("Could not create an Alchemy file for " + name + ".");
                return;
            }
            
            data = ReadFromFile(file);
            
            //Makes sure the experience hasn't been corrupted by improper editing
            //boolean corrupt = CheckCorruption(data[1]);
            if(CheckCorruption(data[1])){
                player.sendMessage("Your experience is corrupt and will be dropped to 0.");
                AlchemyCraft.log.info("Corrupt exp for " + name + ".");
                data[1] = 0;
            }
            
            //Level -1 means the Alchemy file was empty.
            if(data[0] == -1){
                data[0] = 0;
                data[1] = 0;
                SaveToFile(file, data, "New AlchemyCraftFile");
                player.sendMessage("New Alchemy file created for you.");
                AlchemyCraft.log.info("New AlchemyCraft file for " + name + ".");
            }
            
            //Adds the player + info to the public hash map
            playerInfo.put(name, data);  
        }
        
        public void StoreLevelingInfo(Player player){
            String name = player.getName();
            
            //Opens the alchemy file for the player
            File file = OpenFile("plugins\\Alchemy\\" + name + ".txt");
            
            //Checks to see if the player has an Alchemy file
            if(file == null){
                player.sendMessage("Could not create an Alchemy file for you.");
                AlchemyCraft.log.info("Could not create an Alchemy file for " + name + ".");
                return;
            }
            
            //Stores the users information
            SaveToFile(file, playerInfo.get(name), "");
            
            
       }
            
       
        
        
        public boolean addEXP(String pname, int data){
           if(data > 0){ 
            if(playerInfo.containsKey(pname)){
                playerInfo.get(pname)[1] |= (1<<data);
            }
           }
           
           if(data != -2){
               playerInfo.get(pname)[1] |= (1<<24);
               return true;
           }
           
            return false;
        }
        
        public boolean levelUP(String pname){
            if(playerInfo.containsKey(pname)){
                int level = playerInfo.get(pname)[0];
                int exp = playerInfo.get(pname)[1];
            
               if((exp & 0xf) == (EXP[level] & 0xf)
                &&(exp & (0xFF << 24)) >= (EXP[level] & (0xFF << 24))     
                       ){
                   
                   playerInfo.get(pname)[0]++;
                   playerInfo.get(pname)[1] =0;
                   return true;
               }
                
            }
            return false;
        }
        
        
}
