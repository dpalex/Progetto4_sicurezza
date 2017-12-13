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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import progetto4.SecretSharing;

/**
 *
 * @author dp.alex
 */
public class Client {

    private HashMap<String, HashMap<BigInteger, String>> nameMapping = new HashMap<String, HashMap<BigInteger, String>>();
    private HashMap<String, String> macMapping = new HashMap<String, String>();
    ;
    private String id;
    private SecretSharing shamirScheme;
    private boolean debug = false;

    public Client(String id) {
        this.id = id;
    }

    public void setShamirScheme(int k, int n) {
        this.shamirScheme = new SecretSharing(k, n);
    }

    public void upload(String name) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        Path currentRelativePath = Paths.get("src/progetto4");
        String path = currentRelativePath.toAbsolutePath().toString() + "/Repo/";
        byte[] file = Arrays.copyOfRange(Utility.loadFile(path + name), 10, 11);
        System.out.println("inviato: "+Base64.getEncoder().encodeToString(file));

        this.macMapping.put(name, Base64.getEncoder().encodeToString(Utility.genMac(file)));
        System.out.print("Split ricevuto da shamir");
        Map<BigInteger, byte[]> shares = this.shamirScheme.split(file);
         for (Map.Entry<BigInteger,byte[]> s : shares.entrySet()) {
             System.out.println("Server: " + s.getKey());
             System.out.println("file: " + Base64.getEncoder().encodeToString(s.getValue()));
    }
        this.nameMapping.put(name, this.distribuite(shares));
/*
        for (Map.Entry<String, HashMap<BigInteger, String>> s : this.nameMapping.entrySet()) {
            System.out.println("File: " + s.getKey());
            System.out.println("mac del file: " + Base64.getEncoder().encodeToString(macMapping.get(name)));
            for (Map.Entry<BigInteger, String> s1 : s.getValue().entrySet()) {
                System.out.println("Server: " + s1.getKey());
                System.out.println("nome parte: " + s1.getValue());
            }
        }*/

    }

    private HashMap<BigInteger, String> distribuite(Map<BigInteger, byte[]> shares) throws IOException {
        HashMap<BigInteger, String> tmp = new HashMap<BigInteger, String>();
        for (Map.Entry<BigInteger, byte[]> s : shares.entrySet()) {
            Path path = Paths.get("src/progetto4/Servers/" + s.getKey().toString());
            File folder = new File(path.toString());
            Utility.printDebug(folder.exists(), "Cartella :" + s.getKey().toString() + " già esistente", debug);
            if (!folder.exists()) {
                Files.createDirectory(path);
            }
            String fileName = UUID.randomUUID().toString() + ".parts";
            File f = new File(path + "/" + fileName);
            while (f.exists()) {
                Utility.printDebug(!f.exists(), "file [ " + fileName + " ] già esistente", debug);
                fileName = UUID.randomUUID().toString() + ".parts";
                f = new File(path + "/" + fileName);
            }
            Utility.writeFile(path + "/" + fileName, s.getValue());
            tmp.put(s.getKey(), fileName);

        }
        return tmp;

    }

    public boolean checkMac(String name, byte[] downloaded) throws NoSuchAlgorithmException, InvalidKeyException {
        System.out.println("mac salvato prima dell'invio: "+this.macMapping.get(name));
        System.out.println("mac ricevuto: "+Base64.getEncoder().encodeToString(Utility.genMac(downloaded)));
        
        return this.macMapping.get(name).equals(Base64.getEncoder().encodeToString(Utility.genMac(downloaded)));
    }
    
    public byte[] download(String name) throws IOException{
        Map<BigInteger,byte[]> fileMap=new HashMap<BigInteger,byte[]>() {};
        
        for (Map.Entry<BigInteger, String> s : this.nameMapping.get(name).entrySet()) {
             Path path = Paths.get("src/progetto4/Servers/" + s.getKey().toString());
             fileMap.put(s.getKey(),Utility.loadFile(path.toString()+"/"+s.getValue()));
             
        }
        System.out.print("Split inviato a shamir");
       
         for (Map.Entry<BigInteger,byte[]> s : fileMap.entrySet()) {
             System.out.println("Server: " + s.getKey());
             System.out.println("file: " + Base64.getEncoder().encodeToString(s.getValue()));
    }
        return this.shamirScheme.getSecret(fileMap);
    }
    
    

}
