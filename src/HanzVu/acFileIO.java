package HanzVu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;


/*TODO:
 * [x]RETHINK LOADING/SAVING/STORING procs
 */
public class acFileIO {
    private AlchemyCraft plugin;
    
    public acFileIO(AlchemyCraft plugin){
        this.plugin = plugin;
    }
    
    public File OpenFile(String file){
        File pfile = new File(file);
        if(!pfile.exists()){
            pfile.getParentFile().mkdirs();
           try{pfile.createNewFile();}            
           catch(IOException e){return null;}            
        }
        return pfile;
    }
    
    
    public String[] ReadFromFile(File pfile, String[] keys){
        String[] ret = new String[keys.length];
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
                for(int i=0; i<keys.length; i++)
                 ret[i] = props.getProperty(keys[i], null);
                
            } catch (IOException ex) {
               
            }
            catch(NumberFormatException ex){
                 
            }
            
            return ret;
    }
    
    public boolean SaveToFile(File file, String[] data, String[] keys, String comments){
        try{
            Properties prop = new Properties();
            FileOutputStream out = new FileOutputStream(file);
            for(int i=0; i<keys.length; i++)
                prop.put(keys[i], data[i]);
            
            prop.store(out, comments);
        }
                
        catch(IOException ex)
        {
            return false;
        }
        
        return true;
        
    }
    
    
    
}

