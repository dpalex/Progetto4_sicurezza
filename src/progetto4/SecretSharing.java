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
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author f.did
 */
public class SecretSharing {

    private int k;
    private int n;
    private int CERTAINTY = 50;
    private int modLength; // n bit del p 
    private BigInteger primeN;
    private int blocksize; //in byte 

    public SecretSharing(int k, int n, int blocksize) {

        this.k = k;
        this.n = n;
        this.blocksize = blocksize;
        this.modLength = 8 * blocksize;

        BigInteger prime = this.genPrime();
        BigInteger two = new BigInteger("2");
        BigInteger maxNumberBlock = (two.pow(8 * this.blocksize)).divide(two); // 2 ^ n° bit

        while (prime.compareTo(maxNumberBlock) != 1) {
            prime = this.genPrime();
        }

        this.primeN = prime;
        out.println("Primo generato : " + this.primeN);

    }

    public Map<BigInteger, ArrayList<byte[]>> split(byte[] secret) throws IOException {

      
        byte[] secretBase64 = Base64.getEncoder().encode(secret);

        Map<BigInteger, ArrayList<byte[]>> mapN = new TreeMap<BigInteger, ArrayList<byte[]>>();
      
        
        byte[] block;

        ArrayList<byte[]> blockList;
        ArrayList<BigInteger> a = new ArrayList<BigInteger>();

        for (int i = 0; i < this.k - 1; i++) {   // scelgo coefficienti random in Zp
            BigInteger ai = this.randomZp(this.primeN) ;
            a.add(ai);
            if(this.primeN.compareTo(ai)!=1){
                     }
        }
        
        int resto = secretBase64.length % this.blocksize;
 
       
        int j = 0;
        for (int i = 0; i < secretBase64.length / this.blocksize; i++) { // scorro tutti i blocchi

            j = this.blocksize * i;  // indice del blocco

            block = Arrays.copyOfRange(secretBase64, j, j + this.blocksize); // da indice del blocco al successivo
           
            ArrayList<BigInteger> sbi = this.splitBlock(block, a);

            this.concShareToMap(sbi, mapN);
            
        }
        
        if(resto!=0){ //se rimane qualche blocco
            
            block = Arrays.copyOfRange(secretBase64, (secretBase64.length-resto), secretBase64.length);
            ArrayList<BigInteger> sbi = this.splitBlock(block, a);
            this.concShareToMap(sbi, mapN);
               
        }//fine
        

        return mapN;
    }
    
    private void concShareToMap(ArrayList<BigInteger> sbi,Map mapN){
        
         ArrayList blockList;
         
         
        
        for (int n = 1; n < sbi.size() + 1; n++) {
                 
                 byte[] value = sbi.get(n - 1).toByteArray(); 
                
                 
                 if (!mapN.containsKey(BigInteger.valueOf(n))) {  // se la mappa non lo contiene
                    
                    blockList = new ArrayList<byte[]>();
                    blockList.add(value);
                    mapN.put(BigInteger.valueOf(n), blockList);  // inseriscilo

                } else {
                  
                    blockList = (ArrayList<byte[]>) mapN.get(BigInteger.valueOf(n));
                    blockList.add(value);
                    mapN.replace(BigInteger.valueOf(n), blockList); //concatena vc al nuovo ed inseriscilo

                }
                 
             }
        
        
    }

    private ArrayList<BigInteger> splitBlock(byte[] block, ArrayList<BigInteger> a) {

        BigInteger Sb = new BigInteger(block); //rappresentazione del blocco iesimo

        ArrayList<BigInteger> rPolynomial = this.makeRP(a, Sb, this.primeN); // crea n polinomi per quel blocco

        return rPolynomial; 

    }

    private ArrayList<BigInteger> makeRP(ArrayList<BigInteger> a, BigInteger S, BigInteger prime) {

        ArrayList<BigInteger> RP = new ArrayList<BigInteger>();

        for (int i = 1; i < this.n + 1; i++) {  //indice del polinomio f(i)

            BigInteger tmp = S.mod(prime);
            BigInteger x = BigInteger.valueOf(i); //sostituisco x con i

            for (int nExp = 1; nExp < this.k; nExp++) {  //indice dell'esponente

                tmp = tmp.add((a.get(nExp - 1).multiply(x.pow(nExp)))); //a(nExp-1)*x^nExp addizionati ad S inizale
            }

            RP.add(tmp.mod(prime)); // polinomio

        }
        return RP;
    }

    public byte[] getSecret(Map<BigInteger, ArrayList<byte[]>> Kp) throws IOException {

        out.println("\nConversione del segreto...");
       
        BigInteger tmp = null;
        byte[] secretFinal = null;

        if (Kp.size() >= this.k) {

            List<BigInteger> idList = new ArrayList<BigInteger>(Kp.keySet()); // set degli ID
            out.println("\nID Ricevuti : " + idList);
            idList = idList.subList(0, this.k);  // prendo i K che mi servono
            out.println("\nNumero di K usati : " + idList.size());
            out.println("\nID usati : " + idList+"\nRisolvo....\n");

            for (int i = 0; i < Kp.get(idList.get(0)).size(); i++) {    // itero i blocchi   

                for (BigInteger idcurrent : idList) {  //id corrente dal set di ID   
                    
                    byte[] block = Kp.get(idcurrent).get(i); //prendo il blocco corrente

                    BigInteger valueID = new BigInteger(block); //nominatore
                    BigInteger den = new BigInteger("1"); //denominatore

                    for (BigInteger id : idList) {  //risolvo il blocco
                        
                        if (!idcurrent.equals(id)) {
                            //out.println("Risolvo : "+idcurrent +" - "+id);
                            valueID = valueID.multiply(id);
                            den = den.multiply(id.subtract(idcurrent));

                        }
                    }
                    if (tmp == null) {
                        tmp = (valueID.divide(den)).mod(this.primeN);

                    } else {
                        tmp = tmp.add((valueID.divide(den)).mod(this.primeN));

                    }

                } //si chiude la risoluzione del blocco

                if (secretFinal == null) {
                    secretFinal = tmp.mod(this.primeN).toByteArray();
                } else {
                    secretFinal = Utility.concatByte(secretFinal, tmp.mod(this.primeN).toByteArray());
                }

                tmp = null; //riazzero 

            }  //qui si chiude il for dei blocchi

            return Base64.getDecoder().decode(secretFinal);
        }else{
             out.println("\n***** Errore blocchi ricevuti minori di k : "+Kp.size()+" < "+this.k);
                 return null;
        }
   

    }

    private BigInteger genPrime() {
        BigInteger p = null;
        boolean ok = false;
        do {
            p = BigInteger.probablePrime(this.modLength, new Random());
            if (p.isProbablePrime(this.CERTAINTY)) {
                ok = true;
            }
        } while (ok == false);
        return p;
    }

    private BigInteger randomZp(BigInteger p) {
        BigInteger r;
        do {
            r = new BigInteger(modLength, new Random());
        } while (r.compareTo(BigInteger.ZERO) < 0 || r.compareTo(p) > 0);
        return r;
    }

}
