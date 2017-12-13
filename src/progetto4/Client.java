/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progetto4;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import progetto4.SecretSharing;

/**
 *
 * @author dp.alex
 */
public class Client {

    private Map<String, Map<String, String>> nameMapping;
    private String id;
    private SecretSharing shamirScheme;
    private boolean debug =true;
    
    public Client(String id) {
        this.id = id;
    }

    public void setShamirScheme(int k, int n) {
        this.shamirScheme = new SecretSharing(k, n);
    }

    public void upload(String name) throws IOException {
        Path currentRelativePath = Paths.get("src/progetto4");
        String path = currentRelativePath.toAbsolutePath().toString() + "/Repo/";
        this.nameMapping.put(name,this.distribuite(this.shamirScheme.split(Utility.loadFile(path + name))));
    }

    private Map<String, String> distribuite(Map<BigInteger, byte[]> shares) throws IOException {
        Map<String, String> tmp=new HashMap<String,String>();
        for (Map.Entry<BigInteger, byte[]> s : shares.entrySet()) {
            Path path = Paths.get("src/progetto4/Servers/" + s.getKey().toString());
            Files.createDirectory(path);
            String fileName = UUID.randomUUID().toString()+".parts";
            File f=new File(path+"/"+fileName);
            while(f.exists()){
                Utility.printDebug(f.exists(), "file già esistente", debug);
                fileName = UUID.randomUUID().toString()+".parts";
                f=new File(path+"/"+fileName);
            }
            Utility.writeFile(path+"/"+fileName, s.getValue());
            tmp.put(s.getKey().toString(),fileName);
            
        }
        return tmp;
    }

}
