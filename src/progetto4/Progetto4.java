/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progetto4;

import static java.awt.PageAttributes.MediaType.B;
import java.io.IOException;
import static java.lang.System.out;
import java.math.BigInteger;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;
import javafx.util.Pair;

/**
 *
 * @author gia
 */
public class Progetto4 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
      
      byte[] file = Utility.loadFile("/Users/f.did/Desktop/8971_eightbit.jpg");
      SecretSharing s = new SecretSharing(2,5); //k=2 e n=3
      byte[] tmp = new byte[file.length];
      
      for(int i = 0 ;i<file.length;i++){
          byte[] x = {file[i]};
          Map<BigInteger,byte[]> mapN = s.split(x);
          byte[] y = s.getSecret(mapN);
          tmp[i] = y[0];
          
      }
      
      Utility.writeFile("/Users/f.did/Desktop/test.jpg", tmp);
      
      
      
      /*
     SecureRandom r =  new SecureRandom();
      byte[] secret =new byte[1]; // Base64.getDecoder().decode("ePplFQ==");
      r.nextBytes(secret);
      SecretSharing s = new SecretSharing(5,15); //k=2 e n=3
     // System.out.println("Secret base64 -> "+Base64.getEncoder().encodeToString(secret));

      Map<BigInteger,byte[]> mapN = s.split(secret);
      
      byte[] secretr = s.getSecret(mapN);
      BigInteger s2 = new BigInteger(secretr);
      
    
          out.println(Arrays.equals(secret, secretr));
    
   //   out.println("Segreto ricostruito : "+s2);
    //  System.out.println("Secret  base64 -> "+Base64.getEncoder().encodeToString(secretr)); */
      }
      
      }
    