package HanzVu;

import java.io.File;
import java.util.HashMap;
import org.bukkit.entity.Player;

public class acLeveling {
    private AlchemyCraft plugin;
    
    
    /* TODO:
     * [ ]Individual exp for Distilling/Transmuting
     *     [ ]Higher Transmuting ability unlocks new materials
     *     [ ]Higher Distilling ability gives greater yeild on distilled items
     * [x]Rewrite addEXP and levelUP code to use less preprocessing
     * [ ]Rewrite Loading proc to be easier to follow
     */
    
    
    
    //A hashmap to hold a player's leveling info
    public HashMap<String, int[]> playerInfo = new HashMap<String, int[]>();
    
    //A list of keys for the int[] in playerInfo (A workaround for a map of maps)
    public String[] dataStructure = {"dlevel","dexp", "tlevel", "texp"};
    
    //Exp list necessary to level up
    //format:
    //0x? | **<<16
    
    //? = bits marking recipes at this level that the user must find to level up
    //** = number of transmutations necessary to level up (resets to 0 every level)
    
    int[] EXP = {
        0x3,                //level 0 -> level 1 
        0xF | 10<<16,       //level 1 -> level 2
        0x7 | 15<<16,       //level 2 -> level 3
        0x7 | 20<<16,       //level 3 -> level 4
        0x7 | 25<<16,       //level 4 -> level 5
        0x7 | 30<<16        //level 5 -> level 6
    };
    
    //Bit masks for exp
    int HI = 0xffff0000; //Number of transmutations
    int LO = 0xffff;     //Found recipes
    
    public acLeveling(AlchemyCraft plugin){
        this.plugin = plugin;
    }
    
    public void LoadLevelingInfo(Player player){
            String name = player.getName();
            
            //Opens the alchemy file for the player
            File file = plugin.fileio.OpenFile("plugins\\Alchemy\\" + name + ".txt");
            
            //Loads the player's data if their file exists
            if(file != null){
                
                String[] temp = plugin.fileio.ReadFromFile(file, dataStructure);
                int data[] = new int[temp.length];
                
                for(int i =0; i<temp.length; i++){
                    try{
                        data[i] = Integer.parseInt(temp[i]);
                    }
                    catch(NumberFormatException ex){
                        data[i] = -1;
                    }
                }
                
                //null data means the Alchemy file was empty.                
                if(data[0] < 0){
                    for(int i =0; i<dataStructure.length; i++)
                        data[i] = 0;
                    
                    player.sendMessage("New Alchemy file created for you.");
                    
                    AlchemyCraft.log.info("New AlchemyCraft file for " + name + ".");
                }

                //Adds the player + info to the public hash map
                playerInfo.put(name, data); 
                
                //Saves player's data to a file (poor placement)
                StoreLevelingInfo(player, "New AlchemyCraftFile");
                
                return;

            }
            
            //Could not load/create an alchemy file.
            player.sendMessage("Could not create an Alchemy file for you.");
            
            AlchemyCraft.log.info("Could not create an Alchemy file for " + name + ".");
            
        }
        
        public void StoreLevelingInfo(Player player, String comments){
            String name = player.getName();
            
            //Opens the alchemy file for the player
            File file = plugin.fileio.OpenFile("plugins\\Alchemy\\" + name + ".txt");
            
            //Create a string to hold the data to be stored
            String[] temp = new String[playerInfo.get(player.getName()).length];
            
            //Checks to see if the player has an Alchemy file
            if(file != null){
                
                for(int i =0; i<playerInfo.get(player.getName()).length; i++){
                    temp[i] = String.valueOf(playerInfo.get(player.getName())[i]);
                }
                
                //Stores the users information
                plugin.fileio.SaveToFile(file, temp, dataStructure, comments);
            }
            else
                AlchemyCraft.log.info("No Alchemy file to save to for " + name + ".");
            
       }
        
        
        public boolean addEXP(String pname, int data, String EXPTYPE){
            if(data == -1) data =0; //-1 = Distillation/Transmutation adds to number performed, but didn't find a new recipe
            if(data >= 0 ){ //-2 means Distillation/Transmutation didn't occur
                if(playerInfo.containsKey(pname)){
                    for(int i=0; i<dataStructure.length; i++){
                        if(dataStructure[i].equals(EXPTYPE)){
                            playerInfo.get(pname)[i] |= 1<<data; //Marks the recipe as found
                            playerInfo.get(pname)[i] += 1<<16;   //Adds 1 to the number of distills/transmutations
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        
        public int levelUP(String pname, String LUPTYPE){
            if(playerInfo.containsKey(pname)){
                for(int i=0; i<dataStructure.length; i++){
                    if(dataStructure[i].equals(LUPTYPE)){
                        if((playerInfo.get(pname)[i+1] & (EXP[playerInfo.get(pname)[i+1]] & LO)) == (EXP[playerInfo.get(pname)[i+1]] & LO) &&
                           (playerInfo.get(pname)[i+1] & HI) >= (EXP[playerInfo.get(pname)[i+1]] & HI))
                            if(playerInfo.get(pname)[i] == 0){
                                playerInfo.get(pname)[i-2]++; //If level is 0 force level up of both distilling and transmuting
                            }
                            return playerInfo.get(pname)[i]++;
                               
                    }
                }
            }
            return 0;
        }
    
        
        
}
