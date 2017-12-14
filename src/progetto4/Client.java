/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progetto4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import javax.crypto.SecretKey;
import progetto4.SecretSharing;

/**
 *
 * @author dp.alex
 */
public class Client implements Serializable{

    private HashMap<String, HashMap<BigInteger, String>> nameMapping = new HashMap<String, HashMap<BigInteger, String>>();
    private HashMap<String, HashMap<SecretKey, byte[]>> macMapping = new HashMap<String,HashMap<SecretKey, byte[]>>();
    private String id;
    private SecretSharing shamirScheme;
    public ArrayList<String> listFileName=new ArrayList<String>();
    private boolean debug = false;

    public Client(String id) {
        this.id = id;
    }

    public void setShamirScheme(int k, int n,int blockSize) {
        this.shamirScheme = new SecretSharing(k, n, blockSize);
    }

    public void upload(String name) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        Path currentRelativePath = Paths.get("src/progetto4");
        String path = currentRelativePath.toAbsolutePath().toString() + "/Repo/";
        byte[] file = Utility.loadFile(path + name);
        //genero una secretKey per MAC
        SecretKey sk= Utility.genMacKey("HmacSHA256");
        //inserisco il nome del file nel database utente
        this.listFileName.add(name);
        //istanzio e associo una mappa al nome del file
        this.macMapping.put(name,new HashMap<SecretKey, byte[]>());
        //inserisco nella mappa la secretKey e il mac associato al file con la secretKey in considerazione
        this.macMapping.get(name).put(sk, Utility.genMac(file, sk));
        //ottengo gli n-simi split ricevuti per ogni server
        Map<BigInteger, ArrayList<byte[]>> shares = this.shamirScheme.split(file);
        //aggiorno il value associato al nome tramite il metodo distribuite
        this.nameMapping.put(name, this.distribuite(shares));
    }

    private HashMap<BigInteger, String> distribuite(Map<BigInteger, ArrayList<byte[]>> shares) throws IOException {
        //preparo la mappa per server-nome del file associato
        HashMap<BigInteger, String> tmp = new HashMap<BigInteger, String>();
        //creo gli n server come cartelle e inserisco i file
        for (Map.Entry<BigInteger, ArrayList<byte[]>> s : shares.entrySet()) {
            Path path = Paths.get("src/progetto4/Servers/" + s.getKey().toString());
            File folder = new File(path.toString());
            //se il server non esiste lo creo
            if (!folder.exists()) {
                Files.createDirectory(path);
            }
            //genere un UUID casuale da associare al file sull'i-esimo server
            String fileName = UUID.randomUUID().toString() + ".parts";
            File f = new File(path + "/" + fileName);
            //per evitare collisioni sul nome controllo ugualmente se è già stato generato
            while (f.exists()) {
                fileName = UUID.randomUUID().toString() + ".parts";
                f = new File(path + "/" + fileName);
            }
            //serializzo l'array list contente il segreto splittato 
            Utility.writeArrayList(path + "/" +fileName, s.getValue());
            //salvo la mappatura tra nome file e server i-esimo
            tmp.put(s.getKey(), fileName);
        }
        return tmp;
    }

    public boolean checkMac(String name, byte[] downloaded) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKey sK=null;
        byte[] mac=null;
        //prendo la chiave utilizzata per generare il mac e il mac generato sul file in fase di invio
        for (Map.Entry<SecretKey, byte[]> s : this.macMapping.get(name).entrySet()) {
            sK=s.getKey();
            mac=s.getValue();
        }
        //controllo se il mac generato sul file prima di essere inviato corrispende con quello ottenuto in download
        return Arrays.equals(mac,Utility.genMac(downloaded, sK));
    }
    
    public byte[] download(String name) throws IOException, FileNotFoundException, ClassNotFoundException{
        //preparo la mappatura server-Arraylist contente gli n-split del segreto
        Map<BigInteger,ArrayList<byte[]>> fileMap=new HashMap<BigInteger,ArrayList<byte[]>>() {};
        //iterlo la mappatura server-UUID
        for (Map.Entry<BigInteger, String> s : this.nameMapping.get(name).entrySet()) {
             Path path = Paths.get("src/progetto4/Servers/" + s.getKey().toString());
             //inserisco nella mappa al server i-esimo i suoi n-split del segreto
             fileMap.put(s.getKey(),Utility.loadArrayList(path.toString()+"/"+s.getValue()));
        }
        //utilizzo l'interpolazione in f0 in shamirScheme passandogli gli n-split associati al server
        //ritorno il contenuto del segreto
        return this.shamirScheme.getSecret(fileMap);
    }
    
    public ArrayList<String> getNameFilesOnline(){
        return this.listFileName;
    }

}
