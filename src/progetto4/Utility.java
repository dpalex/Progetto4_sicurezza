/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progetto4;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

/**
 *
 * @author dp.alex
 */
public class Utility implements Serializable {

    public static String nameFile(String sourcePath) {
        File file = new File(sourcePath);
        return file.getName();
    }

    public static byte[] loadFile(String sourcePath) throws IOException {
        Path path = Paths.get(sourcePath);
        byte[] data = Files.readAllBytes(path);
        return data;
    }

    public static void writeFile(String sourcePath, byte[] output) throws IOException {
        Path path = Paths.get(sourcePath);
        Files.write(path, output);

    }

    public static String getTimeFromServer(String id) {
        String time;
        final Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
        sdf.setTimeZone(TimeZone.getTimeZone(id));
        return sdf.format(currentTime);
    }

    public static String[] getTimeIdServer() {
        String ids[] = TimeZone.getAvailableIDs();
        return ids;
    }

    public static byte[] concatByte(byte a[], byte[] b) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(a);
        outputStream.write(b);
        byte c[] = outputStream.toByteArray();
        outputStream.close();
        return c;
    }

    public static byte[] toHash256(byte[] file) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        sha.update(file);
        return sha.digest();
    }

    public static byte[] sign(byte[] textToSign, PrivateKey userKeyPr, String alg) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        if (alg.matches("SHA1withDSA") || alg.matches("SHA224withDSA") || alg.matches("SHA256withDSA")) {
            Signature dsa = Signature.getInstance(alg);
            dsa.initSign(userKeyPr);
            dsa.update(textToSign);
            return dsa.sign();
        } else {
            return null;
        }

    }

    public static boolean verifySign(byte[] signedText, byte[] sign, PublicKey userKeyPub, String alg) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature dsa = Signature.getInstance(alg);
        dsa.initVerify(userKeyPub);
        dsa.update(signedText);
        return dsa.verify(sign);
    }

    public static void writeTxt(String path, String Text) throws IOException {
        BufferedWriter w = new BufferedWriter(new FileWriter(path));
        w.write(Text);
        w.close();
    }

    public static String readTxt(String path) throws FileNotFoundException {

        Scanner Input = new Scanner(new File(path));
        String Text = "";
        while (Input.hasNextLine()) {
            Text += Input.nextLine();
        }
        return Text;
    }

    public static String getPathFolder(String x) {
        Path currentRelativePath = Paths.get("src/progetto3");
        String s = currentRelativePath.toAbsolutePath().toString();
        String myDirectoryPath = s + "/" + x;
        return myDirectoryPath;

    }

    public static String[] getPathFiles(String x) {
        Path currentRelativePath = Paths.get("src/progetto3");
        String s = currentRelativePath.toAbsolutePath().toString();
        String myDirectoryPath = s + "/" + x;
        File dir = new File(myDirectoryPath);
        String[] directoryListing = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.equals(".DS_Store");
            }
        });

        for (int i = 0; i < directoryListing.length; i++) {
            directoryListing[i] = myDirectoryPath + "/" + directoryListing[i];
        }

        Arrays.sort(directoryListing);

        return directoryListing;

    }
    
    public static SecretKey genMacKey(String alg) throws NoSuchAlgorithmException{
        KeyGenerator kg=KeyGenerator.getInstance(alg);
        return kg.generateKey();
    }
    
    public static byte[] genMac(byte[] text,SecretKey sc) throws NoSuchAlgorithmException, InvalidKeyException{
            Mac mac=Mac.getInstance(sc.getAlgorithm());
            mac.init(sc);
        return mac.doFinal(text);
    }

    public static void printDebug(boolean condition, String message, boolean debug) {
        if (debug) {
            if (condition) {
                System.out.println(message);
            }
        }
    }

}
