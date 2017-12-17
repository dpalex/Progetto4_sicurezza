/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progetto4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static java.lang.System.out;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

/**
 *
 * @author f.did
 */
public class testenco {


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, DataFormatException {

        byte[] data ={71}; //Utility.loadFile("/Users/f.did/Desktop/VID-20161126-WA0015.mp4");
    //    Random r = new SecureRandom();// ; //{52,123,111,-45};//
    //    r.nextBytes(data);
   //     out.println(new BigInteger(data));
        
         SecretSharing sh = new SecretSharing(2,5,1);
        
       Map<BigInteger, ArrayList<byte[]>> shareMap = sh.split(data);
       Map<BigInteger, ArrayList<byte[]>> rMap =  new TreeMap<BigInteger, ArrayList<byte[]>>();
       BigInteger dB= new BigInteger(data);
      //  out.println("Segreto : "+dB);
       
       for(BigInteger k :shareMap.keySet()){
           if(k.compareTo(BigInteger.valueOf(33))!=0){
           rMap.put(k, shareMap.get(k));
         for(BigInteger k2 :shareMap.keySet()){
             if(k.compareTo(k2)!=0){
                 rMap.put(k2, shareMap.get(k2));
                 byte [] file = sh.getSecret(rMap);
                 boolean b = dB.compareTo(new BigInteger(file)) == 0;
                 out.println(k+" - "+k2+" - |"+b+"|              Segreto : "+ new BigInteger(file));
           //      Utility.writeFile("/Users/f.did/Desktop/test/prova_"+k+"_"+k2+".mp4", file); 
                 rMap.remove(k2);
             }
                      }
         rMap.clear();
           }
       }
/*
        out.println("Segreto : "+dB);
       
       for(BigInteger k :shareMap.keySet()){
           if(k.compareTo(BigInteger.valueOf(12))!=0){
           rMap.put(k, shareMap.get(k));
         for(BigInteger k2 :shareMap.keySet()){
              for(BigInteger k3 :shareMap.keySet()){
             if(k.compareTo(k2)!=0 && k.compareTo(k3)!=0 && k2.compareTo(k3)!=0 ){
                 rMap.put(k2, shareMap.get(k2));
                 rMap.put(k3, shareMap.get(k3));
                 byte [] file = sh.getSecret(rMap);
                 boolean b = dB.compareTo(new BigInteger(file)) == 0;
                 out.println(k+" - "+k2+" - "+k3+" |"+b+"|              Segreto : "+ new BigInteger(file));
                 out.println("\n"+k+" - "+k2+" - "+k3+" |"+b+"|              Segreto : "+ dB);
              //   Utility.writeFile("/Users/f.did/Desktop/test/prova_"+k+"_"+k2+"_"+k3+".rtf", file); 
                 rMap.remove(k2);
                 rMap.remove(k3);
             }
                      }
         }
         rMap.clear();
           }
       }
*/

 
 
 

 
      
    /*   int j = 0;
       int blocksize = 32;
       float blocksize2 = 32;
       byte[] block;
       byte[] dataB = Base64.getEncoder().encode(data);
       
       float d = (dataB.length%blocksize);
       out.println("dataB "+dataB.length);
       out.println("blocksize "+blocksize);
       
       out.println("numero blocchi "+ d );
       
       
       for(int k = 0 ;k<5;k++){
           
        BigInteger prime = genPrime();
        BigInteger two = new BigInteger("2");
        BigInteger maxNumberBlock = (two.pow(8 * blocksize)).divide(two); // 2 ^ n° bit

        while (prime.compareTo(maxNumberBlock) != 1) {
            prime = genPrime();
        }
        
        int resto = dataB.length % blocksize;
        
       
      for(int i = 0;i<dataB.length/blocksize;i++){
          
           j = blocksize * i;  // indice del blocco
           
            block = Arrays.copyOfRange(dataB, j, j + blocksize); // da indice del blocco al successivo
           
           BigInteger blockB = new BigInteger(block);
           if(blockB.compareTo(prime)==1 || blockB.compareTo(prime)==0){
                out.println(blockB +">"+prime);
           }
           
         //  out.println(k+" "+"grandezza del blocco"+i+" : "+block.length);
            
          
       }
      
      if(resto!=0){
          block = Arrays.copyOfRange(dataB, j, j + blocksize); // da indice del blocco al successivo
            out.println("metto 20");
      }
      
      
       }*/
      


    }
    /*
    private static BigInteger genPrime() {
        BigInteger p = null;
        boolean ok = false;
        do {
            p = BigInteger.probablePrime(8*32, new Random());
            if (p.isProbablePrime(50)) {
                ok = true;
            }
        } while (ok == false);
        return p;
    }
*/
    private static  void addmap(TreeMap map) {
        
        map.put("sss", "ciao");
    }

}
