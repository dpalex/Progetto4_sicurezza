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

        byte[] data = Utility.loadFile("/Users/f.did/Desktop/JPEG_20160830_195147_115183616.jpg");
        /*  SecretSharing sh = new SecretSharing(2,3,256);
        
       Map<BigInteger, ArrayList<byte[]>> shareMap = sh.split(data);
       for(BigInteger k :shareMap.keySet()){
           out.println("dimensione lista : "+shareMap.get(k).size());
       }
         
        byte [] file = sh.getSecret(shareMap);
         
        Utility.writeFile("/Users/f.did/Desktop/prova2.jpg", file); */

       

        // Create the decompressor and give it the data to compress
       byte[] dc = 
        
        out.println(Arrays.equals(compressedData,decompressedData));

    }

}
