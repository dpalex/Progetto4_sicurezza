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
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import progetto4.SecretSharing;

/**
 *
 * @author dp.alex
 */
public class Client implements Serializable {

    private HashMap<String, HashMap<BigInteger, String>> nameMapping = new HashMap<String, HashMap<BigInteger, String>>();
    private HashMap<String, ArrayList<byte[]>> macMapping = new HashMap<String, ArrayList<byte[]>>();
    private HashMap<String, HashMap<String, String>> macResults = new HashMap<String, HashMap<String, String>>();
    private String id;
    private SecretSharing shamirScheme;

    public Client(String id, boolean session) throws ClassNotFoundException, IOException {
        if (!session) {
            this.id = id;
        } else {
            Client tmp = Utility.loadSession(id);
            this.id = tmp.id;
            this.macMapping = tmp.macMapping;
            this.nameMapping = tmp.nameMapping;
            this.shamirScheme = tmp.shamirScheme;
        }
    }

    public void setShamirScheme(int k, int n, int blockSize) {
        this.shamirScheme = new SecretSharing(k, n, blockSize);
    }

    public void upload(String name) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        Path currentRelativePath = Paths.get("src/progetto4");
        String path = currentRelativePath.toAbsolutePath().toString() + "/Repo/";
        byte[] file = Utility.loadFile(path + name);

        //istanzio e associo una mappa al nome del file
        this.macMapping.put(name, new ArrayList<byte[]>());
        //inserisco nella mappa la secretKey e il mac associato al file con la secretKey in considerazione
        //this.macMapping.get(name).put(Utility.genMac(file, sk),sk);
        //ottengo gli n-simi split ricevuti per ogni server
        Map<BigInteger, ArrayList<byte[]>> shares = this.shamirScheme.split(file);
        applyMac(name, file, shares);
        //aggiorno il value associato al nome tramite il metodo distribuite
        this.distribuite(name,shares);
        File f = new File(path.toString() + name);
        f.delete();
    }

    private void applyMac(String name, byte[] file, Map<BigInteger, ArrayList<byte[]>> parts) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKey sk = Utility.genMacKey("HmacSHA256");
        byte[] encodedKey = sk.getEncoded();
        this.macMapping.get(name).add(encodedKey);

        for (Map.Entry<BigInteger, ArrayList<byte[]>> s : parts.entrySet()) {
            //genero MAC
            byte[] tmp = Utility.getByteArrayFromObject(s.getValue());
            byte[] mac = Utility.genMac(tmp, sk);
            this.macMapping.get(name).add(mac);
        }
        this.macMapping.get(name).add(Utility.genMac(file, sk));
        this.macResults.put(name, new HashMap<String, String>());
    }

    private void distribuite(String name, Map<BigInteger, ArrayList<byte[]>> shares) throws IOException {
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
            Utility.writeArrayList(path + "/" + fileName, s.getValue());
            //salvo la mappatura tra nome file e server i-esimo
            tmp.put(s.getKey(), fileName);
        }
        this.nameMapping.put(name, tmp);
    }

    private void checkAllIntegrity(String name) throws NoSuchAlgorithmException, InvalidKeyException, IOException, FileNotFoundException, ClassNotFoundException {
        Map<BigInteger, ArrayList<byte[]>> received = this.getAllParts(name);
        byte[] sKEnc = this.macMapping.get(name).get(0);
        SecretKey sK = new SecretKeySpec(sKEnc, 0, sKEnc.length, "HmacSHA256");
        Boolean check = false;
        //controllo se il mac generato sul file prima di essere inviato corrispende con quello ottenuto in download
        for (int i = 1; i < (received.size() + 1); i++) {
            byte[] tmp = Utility.getByteArrayFromObject(received.get(BigInteger.valueOf(i)));
            byte[] newMac = Utility.genMac(tmp, sK);
            check = Arrays.equals(this.macMapping.get(name).get(i), newMac);
            this.macResults.get(name).put(BigInteger.valueOf(i).toString(), Boolean.toString(check));
        }

    }

    private void checkFinalIntegrity(String name, byte[] file) throws NoSuchAlgorithmException, InvalidKeyException, IOException, FileNotFoundException, ClassNotFoundException {
        Map<BigInteger, ArrayList<byte[]>> received = this.getAllParts(name);
        byte[] sKEnc = this.macMapping.get(name).get(0);
        SecretKey sK = new SecretKeySpec(sKEnc, 0, sKEnc.length, "HmacSHA256");
        //controllo se il mac generato sul file prima di essere inviato corrispende con quello ottenuto in download
        byte[] macOriginalFile = this.macMapping.get(name).get((received.size() + 1));
        byte[] newMacFile = Utility.genMac(file, sK);
        Boolean check = Arrays.equals(macOriginalFile, newMacFile);
        this.macResults.get(name).put(BigInteger.ZERO.toString(), Boolean.toString(check));
        this.nameMapping.remove(name);
        this.macMapping.remove(name);

    }
        
    private Map<BigInteger, ArrayList<byte[]>> getAllParts(String name) throws IOException, FileNotFoundException, ClassNotFoundException {
        //preparo la mappatura server-Arraylist contente gli n-split del segreto
        Map<BigInteger, ArrayList<byte[]>> fileMap = new HashMap<BigInteger, ArrayList<byte[]>>();

        //iterlo la mappatura server-UUID
        for (Map.Entry<BigInteger, String> s : this.nameMapping.get(name).entrySet()) {
            Path path = Paths.get("src/progetto4/Servers/" + s.getKey().toString());
            //inserisco nella mappa al server i-esimo i suoi n-split del segreto
            fileMap.put(s.getKey(), Utility.loadArrayList(path.toString() + "/" + s.getValue()));
        }
        return fileMap;
    }

    public String download(String name) throws IOException, FileNotFoundException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException {
        Map<BigInteger, ArrayList<byte[]>> fileMap = getAllParts(name);
        //utilizzo l'interpolazione in f0 in shamirScheme passandogli gli n-split associati al server
        //ritorno il contenuto del segreto
        Path currentRelativePath = Paths.get("src/progetto4");
        String download = currentRelativePath.toAbsolutePath().toString() + "/Download/";
        byte[] downloadFile = this.shamirScheme.getSecret(fileMap);
        this.checkFinalIntegrity(name, downloadFile);
        Utility.writeFile(download + name, downloadFile);
        String finalMac=this.macResults.get(name).get(String.valueOf(0));
        this.macResults.remove(name);
        return finalMac;
    }

    public ArrayList<String> getNameFilesOnline() {
        ArrayList<String> tmp = new ArrayList<String>();
        for (Map.Entry<String, HashMap<BigInteger, String>> s : this.nameMapping.entrySet()) {
            tmp.add(s.getKey());
        }
        return tmp;
    }

    public ArrayList<String> getServerOnline(String nameFile) {
        ArrayList<String> servers = new ArrayList<String>();
        for (Map.Entry<BigInteger, String> s : this.nameMapping.get(nameFile).entrySet()) {
            servers.add(s.getKey().toString());
        }
        return servers;
    }

    public HashMap<String, String> getStates(String nameFile) throws IOException, FileNotFoundException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException {
        this.checkAllIntegrity(nameFile);
        return this.macResults.get(nameFile);
    }

    public void SaveSession() throws IOException, FileNotFoundException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Utility.saveSession(this, this.id);
    }

}
