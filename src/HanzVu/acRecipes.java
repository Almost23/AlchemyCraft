package HanzVu;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author HanzVu
 */

/* TODO:
 * [ ]Rewrite distilling code to have independent leveling
 * [ ]Figure out fair exchangerate
 * 
 */



//I highly recommend that you do not read this list.
//I think the fun of the plugin is in discovering the recipes.
//That being said, proceed at your own discretion.
  
/*        A Comprehensive List of Alchemical Recpies
 * 
 *  Level 0:
 *  Bucket of Lava  -> Bucket + Fire
 *  Bucket of Water -> Bucket + Water
 * 
 * 
 *               Aether    Fire    Water    Earth     Air        
 *   Level 1: 
 *      Wood          0       2        0        3       0 
 *      Clay          0       0        2        2       0
 *     Glass          0       1        0        3       1
 *  Snowball          0       0        3        0       2  
 * 
 * Level 2:
 *      Coal          0       2        0        5       0
 *     Flint          0       2        0        4       0
 *    Sponge          0       0        2        2       3
 * 
 * Level 3:
 *  Lapis             2       0        3        3       0
 *  RedStone          2       3        0        3       0
 *  Sappling          3       2        2        2       1
 * 
 * Level 4:
 *  Brown Mushroom    3       1        2        4       3
 *  Raw Fish          3       2        5        2       1
 *  Raw Porkchop      3       3        3        2       4
 * 
 * Level 5:
 *     iron           4       2        2        3       1
 *     gold           5       3        3        3       3 
 *  diamond           6       5        5        5       5
 *
 * Level 6:
 * Philosopher's Stone 10     10       10       10      10 
 * (AKA Golden Apple)
 */



public class acRecipes {
    //A list of all recipes 
    //Format:
    //{ae,f,w,e,a,mat. id, amt, data}
    LinkedList<int[]> recipes = new LinkedList<int[]>();

    
     Random generator = new Random();
    
    int level0 = 0;
    int level1 = 4;
    int level2 = level1+3;
    int level3 = level2+3;
    int level4 = level3+3;
    int level5 = level4+3;
    int level6 = level5+1;
    
    //padded with level0 to pretend it's not zero indexed
    int[] levels = {level0, level1, level2, level3, level4, level5};
    
    
    
    private AlchemyCraft plugin;
    
    public acRecipes(AlchemyCraft plugin){
        this.plugin = plugin;
        
        //Format:
        //{ae,f,w,e,a,mat. id, amt, data}
       int[][] temp =     {
        //level 1
        {0,2,0,3,0,17,1,0},            //log
        {0,0,2,2,0,337,1,0},           //clay drop
        {0,1,0,3,1,20,1,0},            //glass
        {0,0,3,0,2,332,1,0},           //snowball
        //level 2
        {0,2,0,5,0,263,1,0},           //coal
        {0,2,0,4,0,318,1,0},           //flint
        {0,0,2,2,3,19,1,0},            //sponge 
        //level 3
        {2,0,3,3,0,351,1,4},           //lapis lazuli <- data = 0x4
        {2,3,0,3,0,331,1,0},           //redstone
        {3,2,2,2,1,6,1,0},             //sappling
        //level 4
        {3,1,2,4,3,39,1,0},            //brown Mushroom
        {3,2,5,2,1,349,1,0},           //raw fish
        {3,3,3,2,4,319,1,0},           //raw Porkchop
        //level 5 {Shaped}
        {4,2,2,3,1,266,1,0},           //iron       
        {5,3,3,3,3,265,1,0},           //gold
        {6,5,5,5,5,264,1,0},           //diamond
        //level 6
        {10,10,10,10,10,348,1,0}       //Philosophers stone (golden apple)
        };
       
        recipes.addAll(Arrays.asList(temp));
    }
    
    public int Transmute(int level, Dispenser furnace){
        //Level 0 can't make stuff!
        if(level ==0) return -2;
        
        //If the user is level 3+, but his/her recipe doesn't have the quintesscence, don't check
        //level 3+ recipes.
        if(level >=3 && !furnace.getInventory().contains(Material.GLOWSTONE_DUST)) level =2;
        
        //Split up the furnace inventory into each element
        ItemStack item = null;
        Integer[] elements = {0,0,0,0,0};
        
        for(int i =0; i<furnace.getInventory().getSize(); i++){
            item = null;
            item = furnace.getInventory().getItem(i);
            switch(item.getTypeId()){
                //Aether
                case 348: elements[0] += item.getAmount(); break;
                //Fire
                case 51: elements[1] += item.getAmount(); break;
                //Water
                case 8: elements[2] += item.getAmount(); break;
                //Earth
                case 3: elements[3] += item.getAmount(); break;
                //Air
                case 288: elements[4] += item.getAmount(); break;
            }
            
        }
        
        
        //Make a list of potential recipes that this user is using
        LinkedList<ItemStack> potential = new LinkedList<ItemStack>();
        
        for(int i=0; i<levels[level]; i++){
            //If they have a recipe exactly right, return that recipe
            ItemStack temp =null;
             if(elements[0] == recipes.get(i)[0] &&
               elements[1] == recipes.get(i)[1] &&
               elements[2] == recipes.get(i)[2] &&
               elements[3] == recipes.get(i)[3] &&
               elements[4] == recipes.get(i)[4]){
               temp = new ItemStack(recipes.get(i)[5], recipes.get(i)[6], (short)recipes.get(i)[7]);
               furnace.getInventory().clear();
               furnace.getInventory().addItem(temp);
               for(int j=0; j<temp.getAmount(); j++){
                   furnace.dispense();
               }
               if(i>levels[level-1]) return -1;
                    else return (i-levels[level-1]);
             }
             
            //If the dispenser has excess of anything, add the recipe to a list of potential items 
            if(elements[0] >= recipes.get(i)[0] &&
               elements[1] >= recipes.get(i)[1] &&
               elements[2] >= recipes.get(i)[2] &&
               elements[3] >= recipes.get(i)[3] &&
               elements[4] >= recipes.get(i)[4]){
                temp = new ItemStack(recipes.get(i)[5], recipes.get(i)[6], (short)recipes.get(i)[7]);
                potential.add(temp);
            }
        }
        
        //Dispense an item stack:
        //If there are no possible recipes using the items they gave, return null
        //If there is one possible recipe, return that recipe
        //If there are multiple recipes, return one at random.
        ItemStack t = null;
        switch(potential.size()){
            case 0: return -2;
            case 1: t = potential.get(0); break;
            default: t = potential.get(generator.nextInt(potential.size()));
        }
        furnace.getInventory().clear();
        furnace.getInventory().addItem(t);
        for(int j=0; j<t.getAmount(); j++){
            furnace.dispense();
        }
        
        //You cannot gain experience if you don't get the recipe exactly right!
        return -1;
    }
    
    public int Distill(int level, Dispenser still){
        AlchemyCraft.log.info("Distilling");
        
        //level 0 can't distill
        if(level ==0) return -2;
        
        //Find the position of the block in the dispenser
        //0  1  2
        //3  4  5
        //6  7  8
        int item = -1;
        for(int q =0; q<9; q++){
            if(still.getInventory().getContents()[q] != null) item =q;
        }
        //If item <0, the dispenser is empty
        if(item <0) return -2;
        
        ItemStack[] composition = new ItemStack[5];
        for(int i=0; i<levels[level]; i++){
                if(recipes.get(i)[5] == still.getInventory().getContents()[item].getTypeId()){
                    AlchemyCraft.log.info("Found the object!");
                    composition[0] = new ItemStack(348,recipes.get(i)[0]);    //aether
                    composition[1] = new ItemStack(51,recipes.get(i)[1]);     //fire
                    composition[2] = new ItemStack(8,recipes.get(i)[2]);      //water
                    composition[3] = new ItemStack(3,recipes.get(i)[3]);      //earth
                    composition[4] = new ItemStack(288,recipes.get(i)[4]);    //air

                    //GIVE THEM STUFF!
                    for(int k =0; k<composition.length; k++){
                        if(composition[k].getAmount() == 0) continue;
                        
                        still.getInventory().clear();
                        still.getInventory().addItem(composition[k]);
                        for(int p = 0; p<composition[k].getAmount(); p++){
                            AlchemyCraft.log.info("DISPENSING!");
                            still.dispense();
                        }
                    }
                    //If the distillation was not an object at this level return -1
                    if(i>levels[level-1]) return -1;
                    //Other wise return the number (in the array) of that object
                    //NOTE: this is offset to ignore all objects in lower levels
                    else return (i-levels[level-1]);
                }
            
        }
        return -2;
    }
}
