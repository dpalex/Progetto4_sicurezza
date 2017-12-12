/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progetto4;

import static java.lang.System.out;
import java.math.BigInteger;
import java.security.Key;
import java.security.SecureRandom;
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
    public static void main(String[] args) {
      
      SecureRandom r =  new SecureRandom();
      byte[] secret = new byte[4];
      r.nextBytes(secret);
      SecretSharing s = new SecretSharing(2,3); //k=2 e n=3
      
      Map<BigInteger,byte[]> mapN = s.split(secret);
      
      out.println("\n********** RISULTATO **********\n");
      
      for(BigInteger k : mapN.keySet()){
          System.out.println(mapN.get(k));
          System.out.println(new BigInteger((byte[]) mapN.get(k)));
          System.out.println(new BigInteger((byte[]) mapN.get(k)).bitCount());
      }
      
      }
      
    
    }
    

