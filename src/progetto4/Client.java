/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progetto4;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import progetto4.SecretSharing;

/**
 *
 * @author dp.alex
 */
public class Client {
    
    private String originalName;
    private int numberOfServer;
    private String id;

    public Client(String id) {
        this.id = id;
    }
    
    public void upload(String path) throws IOException{
        Path currentRelativePath = Paths.get("src/progetto4");
        String channel = currentRelativePath.toAbsolutePath().toString()+"/Channel";
        this.originalName=Utility.nameFile(path);
        Utility.writeFile(channel, Utility.loadFile(path));
    }
    
    public void download(){
        
    }
    
}
