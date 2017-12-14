/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progetto4;

import static java.awt.PageAttributes.MediaType.B;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.System.out;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import javafx.util.Pair;

/**
 *
 * @author gia
 */
public class Progetto4 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException, ClassNotFoundException {

      Client gio=new Client("giovanni");
      gio.setShamirScheme(3, 5,256);
      gio.upload("shamir.pdf");
      byte[] down=gio.download("shamir.pdf");
      System.out.println(down.length);
      Path currentRelativePath = Paths.get("src/progetto4");
      String download = currentRelativePath.toAbsolutePath().toString() + "/Download/";
      Utility.writeFile(download+"shamir.pdf", down);
      //System.out.println("ricevuto: "+Base64.getEncoder().encodeToString(down));
      System.out.println("Controllo mac: "+gio.checkMac("shamir.pdf", down));
          
      }
      
      }
    