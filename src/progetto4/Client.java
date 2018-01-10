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
import java.util.zip.DataFormatException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import progetto4.SecretSharing;

/**
 *
 * @author gruppo13
 */
public class Client implements Serializable {

    private HashMap<String, HashMap<BigInteger, String>> nameMapping = new HashMap<String, HashMap<BigInteger, String>>();
    private HashMap<String, HashMap<String, byte[]>> macMapping = new HashMap<String, HashMap<String, byte[]>>();
    public String id;
    private SecretSharing shamirScheme;

    /*
    Costruttore del client che permette di ricaricare 
    una sessione precedente in caso questa sia stata salvata, 
    in caso di esito negativo si istanzia un nuovo client.
     */
    public Client(String id) throws ClassNotFoundException, IOException {
        Client tmp = Utility.loadSession(id);
        //verifico se è stato trovata una sessione del client con questo id
        if (tmp != null) {
            //carico i parametri della sessione salvata
            this.id = tmp.id;
            this.macMapping = tmp.macMapping;
            this.nameMapping = tmp.nameMapping;
            this.shamirScheme = tmp.shamirScheme;
        } else {
            //nuovo client
            this.id = id;
        }
    }

    /*Metodo che istanzia uno ShamirScheme secodo i parametri passati*/
    public void setShamirScheme(int k, int n, int blockSize) {
        this.shamirScheme = new SecretSharing(k, n, blockSize);
    }

    /*Metodo pubblico di upload che riceve una stringa rappresentante il nome del file su cui effettuare l'upload,
    il metodo legge il file, distribuisce sugli n-server le parti e aggiorna la mappa nome_originale-nomi degli n-split*/
    public void upload(String name) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        //leggo il flusso di byte
        Path currentRelativePath = Paths.get("src/progetto4");
        String path = currentRelativePath.toAbsolutePath().toString() + "/Repo/";
        byte[] file = Utility.loadFile(path + name);
        //istanzio e associo una mappa al nome del file
        this.macMapping.put(name, new HashMap<String, byte[]>());
        //ottengo gli n-simi split ricevuti secondo lo schema di shamir
        Map<BigInteger, ArrayList<byte[]>> shares = this.shamirScheme.split(file);
        //applico Mac a tutti gli split e al file intero
        this.applyMac(name, file, shares);
        //distribuisco gli split sugli n-server
        this.distribuite(name, shares);
        File f = new File(path.toString() + name);
        //rimuovo il file su cui è stato effettuato l'upload
        f.delete();
    }

    /*Metodo privato che applica il Mac ad ogni split e al file intero*/
    private void applyMac(String name, byte[] file, Map<BigInteger, ArrayList<byte[]>> parts) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        //istanzio una chiave di tipo HmacSHA256
        SecretKey sk = Utility.genMacKey("HmacSHA256");
        byte[] encodedKey = sk.getEncoded();
        //salvo nella mappa la chiave utilizzata in formato encoded
        this.macMapping.get(name).put("key", encodedKey);
        //per ogni split da salvare sui server applico il MAC
        for (Map.Entry<BigInteger, ArrayList<byte[]>> s : parts.entrySet()) {
            //genero MAC
            byte[] tmp = Utility.getByteArrayFromObject(s.getValue());
            byte[] mac = Utility.genMac(tmp, sk);
            //inserisco nella mappa ad ogni chiave (rappresentante il server i-esimo) il suo MAC
            this.macMapping.get(name).put(s.getKey().toString(), mac);
        }
        //inserisco il MAC dell'intero file
        this.macMapping.get(name).put("full", Utility.genMac(file, sk));
    }

    /*Metodo privato che distribuisce gli n-shares ricevuti da SecretSharing sugli n-server*/
    private void distribuite(String name, Map<BigInteger, ArrayList<byte[]>> shares) throws IOException {
        //istanzio una mappa per: server-nome del file associato
        HashMap<BigInteger, String> tmp = new HashMap<BigInteger, String>();
        //creo gli n server come cartelle e inserisco i file
        for (Map.Entry<BigInteger, ArrayList<byte[]>> s : shares.entrySet()) {
            Path path = Paths.get("src/progetto4/Servers/" + s.getKey().toString());
            File folder = new File(path.toString());
            //se il server non esiste lo creo
            if (!folder.exists()) {
                Files.createDirectory(path);
            }
            //genere un UUID casuale da associare come nome al file sull'i-esimo server
            String fileName = UUID.randomUUID().toString() + ".parts";
            File f = new File(path + "/" + fileName);
            //per evitare collisioni sul nome controllo ugualmente se è già stato generato
            while (f.exists()) {
                fileName = UUID.randomUUID().toString() + ".parts";
                f = new File(path + "/" + fileName);
            }
            //serializzo l'array list contente lo share i-esimo 
            Utility.writeArrayList(path + "/" + fileName, s.getValue());
            //salvo la mappatura tra nome file e nome file del server i-esimo
            tmp.put(s.getKey(), fileName);
        }
        //in corrispondenza del nome del file originario inserisco l'intera mappa come value
        this.nameMapping.put(name, tmp);
    }

    /*Metodo privato che verifica l'integrita di tutti gli shares sui server*/
    private HashMap<String, String> checkAllIntegrity(String name) throws NoSuchAlgorithmException, InvalidKeyException, IOException, FileNotFoundException, ClassNotFoundException {
        //prendo le parti presenti sui server e le inserisco nella mappa
        Map<BigInteger, ArrayList<byte[]>> received = this.getAllParts(name);
        //get della chiave utilizzata in fase di upload del file
        byte[] sKEnc = this.macMapping.get(name).get("key");
        SecretKey sK = new SecretKeySpec(sKEnc, 0, sKEnc.length, "HmacSHA256");
        Boolean check = false;
        //confronto i mac generati in fase di upload con i mac che calcolo a runtime sugli shares presenti nei server
        HashMap<String, String> macResults = new HashMap<String, String>();
        for (BigInteger i : received.keySet()) {
            byte[] tmp = Utility.getByteArrayFromObject(received.get(i));
            byte[] newMac = Utility.genMac(tmp, sK);
            check = Arrays.equals(this.macMapping.get(name).get(i.toString()), newMac);
            //inserisco i risultati nella mappa
            macResults.put(i.toString(), Boolean.toString(check));
        }
        return macResults;
    }

    /*Metodo privato che verifica l'integrità dell'intero file dopo il download*/
    private boolean checkFinalIntegrity(String name, byte[] file) throws NoSuchAlgorithmException, InvalidKeyException, IOException, FileNotFoundException, ClassNotFoundException {
        //get della chiave utilizzata in fase di upload del file
        byte[] sKEnc = this.macMapping.get(name).get("key");
        SecretKey sK = new SecretKeySpec(sKEnc, 0, sKEnc.length, "HmacSHA256");
        //controllo se il mac generato sul file prima di essere inviato corrispende con quello ottenuto in download
        byte[] macOriginalFile = this.macMapping.get(name).get("full");
        byte[] newMacFile = Utility.genMac(file, sK);
        //ritorno il risultato del check
        return Arrays.equals(macOriginalFile, newMacFile);
    }

    /*Metodo privato che, data una stringa rappresentate il nome del file originale restituisce gli n-shares presenti sui server*/
    private Map<BigInteger, ArrayList<byte[]>> getAllParts(String name) throws IOException, FileNotFoundException, ClassNotFoundException {
        //preparo la mappatura server-Arraylist contente gli n-shares
        Map<BigInteger, ArrayList<byte[]>> fileMap = new HashMap<BigInteger, ArrayList<byte[]>>();
        //itero la mappatura server-UUID leggendo gli shares
        for (Map.Entry<BigInteger, String> s : this.nameMapping.get(name).entrySet()) {
            Path path = Paths.get("src/progetto4/Servers/" + s.getKey().toString());
            File tmp = new File(path.toString() + "/" + s.getValue());
            //se il server/file è online lo inserisco nella mappa
            if (tmp.exists()) {
                fileMap.put(s.getKey(), Utility.loadArrayList(path.toString() + "/" + s.getValue()));
            }
        }
        //restituisco gli split
        return fileMap;
    }
    
    /*Metodo che ricevuta una map rappresentante gli share sui server restituisce una Map dello stesso tipo contente solo gli share non corrotti e integri*/
    private Map<BigInteger, ArrayList<byte[]>> toGetSecret(String name,Map<BigInteger, ArrayList<byte[]>> shares) throws NoSuchAlgorithmException, InvalidKeyException, IOException, FileNotFoundException, ClassNotFoundException{
                HashMap<String, String> results=this.checkAllIntegrity(name);
                Map<BigInteger, ArrayList<byte[]>> tmp= new HashMap<BigInteger, ArrayList<byte[]>>();
                for(BigInteger bg:shares.keySet()){
                    if(Boolean.valueOf(results.get(bg.toString()))){
                        tmp.put(bg, shares.get(bg));
                    }
                }
                return tmp;
            }

    /*Metodo pubblico che, data una stringa rappresentante il nome del file, effettua il download di questultimo, 
    ritorna una stringa rappresentante l'esito dell'integrità del file*/
    public String download(String name) throws IOException, FileNotFoundException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException, DataFormatException {
        //prendo le parti presenti sui server e le inserisco nella mappa
        Map<BigInteger, ArrayList<byte[]>> fileMap = toGetSecret(name,getAllParts(name));
        Path currentRelativePath = Paths.get("src/progetto4");
        String download = currentRelativePath.toAbsolutePath().toString() + "/Download/";
        //ricevo l'intero segreto utilizzando la ricostruzione e l'interpolazione in shamir
        byte[] downloadFile = this.shamirScheme.getSecret(fileMap);
        //verifico l'integrità
        String result = Boolean.toString(this.checkFinalIntegrity(name, downloadFile));
        //scrivo il file in download
        Utility.writeFile(download + name, downloadFile);
        //ritorno l'esito della verifica
        return result;
    }

    /*Metodo pubblico che restituisce una lista contente i nomi dei file per i quali si è effettuato l'upload*/
    public ArrayList<String> getNameFilesOnline() {
        ArrayList<String> tmp = new ArrayList<String>();
        //itero la mappa contenente nome-mappa<server,share-isemo>
        for (Map.Entry<String, HashMap<BigInteger, String>> s : this.nameMapping.entrySet()) {
            //prendo solo i nomi
            tmp.add(s.getKey());
        }
        //restituisco la lista
        return tmp;
    }

    /*Metodo pubblico che restituisce una mappa contente nomeserver-integrità dello share n-esimo*/
    public HashMap<String, String> checkIntegrity(String nameFile) throws IOException, FileNotFoundException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException {
        return this.checkAllIntegrity(nameFile);
    }

    /*Metodo pubblico che serializza la classe salvando la sessione dell'utente corrente*/
    public void SaveSession() throws IOException, FileNotFoundException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Utility.saveSession(this, this.id);
    }

    /*Metodo pubblico che ritorna informazioni base sullo schema: n,k,dim blocco*/
    public int[] getInfoScheme(){
        return this.shamirScheme.getParameters();
}

}
