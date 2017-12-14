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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author f.did
 */
public class testenco {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
       byte[] data = Utility.loadFile("/Users/f.did/Desktop/JPEG_20160830_195147_115183616.jpg");
       out.println(data.length);
       SecretSharing sh = new SecretSharing(2,3);
        
       Map<BigInteger, ArrayList<byte[]>> shareMap = sh.split(data);
       for(BigInteger k :shareMap.keySet()){
           out.println("dimensione lista : "+shareMap.get(k).size());
       }
         
        byte [] file = sh.getSecret(shareMap);
         
        Utility.writeFile("/Users/f.did/Desktop/prova2.jpg", file);
        
        /*
        byte[] arrayencS = Base64.getEncoder().encode(data);
        
        
        byte[] dataZ = Zipper.Zip(data);
         byte[] arrayencSZ = Base64.getEncoder().encode(dataZ);
        out.println("dimensione data  : " + (data.length));
        out.println("dimensione data base 64 : " + (arrayencS.length));
        out.println("dimensione encB64 - data  : " + (arrayencS.length - data.length));
        out.println("dimensione data Zip : " + (dataZ.length));
        out.println("dimensione data Zip base 64 : " + (arrayencSZ.length));
        out.println("dimensione encB64Z - dataZ  : " + (arrayencSZ.length - dataZ.length));

        for (int i = 0; i < arrayencS.length / 3; i += 3) {

            byte[] a = Arrays.copyOfRange(arrayencS, i, i + 4);
            BigInteger ba = new BigInteger(a);

            if (ba.signum() == -1) {
                out.println("Negativo");

            }

            if (ba.compareTo(new BigInteger("2147483647")) == 1) {
                out.println("MAggiore di 2147483647");
                out.println(ba);

            }

        }*/

    }

    public static class Zipper {

        
        public static byte[]  Zip(byte[] dataToCompress) {

            try {
                ByteArrayOutputStream byteStream
                        = new ByteArrayOutputStream(dataToCompress.length);
                try {
                    GZIPOutputStream zipStream
                            = new GZIPOutputStream(byteStream);
                    try {
                        zipStream.write(dataToCompress);
                    } finally {
                        zipStream.close();
                    }
                } finally {
                    byteStream.close();
                }

                byte[] compressedData = byteStream.toByteArray();

                return compressedData;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
