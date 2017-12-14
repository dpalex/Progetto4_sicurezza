/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progetto4;

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

/**
 *
 * @author f.did
 */
public class SecretSharing {

    private int k;
    private int n;
    private int CERTAINTY = 50;
    private int modLength = 16; // n bit del p 
    private BigInteger primeN;
    private int blocksize = 1; //in byte 

    public SecretSharing(int k, int n) {

        this.k = k;
        this.n = n;

<<<<<<< HEAD
=======
       // out.println("K -> " + k + " N - > " + n);
    }

    public  Map<BigInteger, byte[]> split(byte[] secret) {

        Map<BigInteger, byte[]> mapN = new TreeMap<BigInteger, byte[]>();
        BigInteger S = new BigInteger(1,secret); //rappresentazione di S
        BigInteger S2 = new BigInteger(secret); //rappresentazione di S
     //   out.println("Secret -> " + S);
      //  out.println("Secret ricevuto -> " + S2);

        BigInteger prime = new BigInteger("277");
      /*  boolean condition = true;
        while (condition) { //genero prime maggiore di n e di S ????
>>>>>>> c928e697f12f658c957a25ee54abb076cde97e57

        BigInteger prime = this.genPrime();
        BigInteger two = new BigInteger("2");
        BigInteger maxNumberBlock = two.pow(8 * this.blocksize); // 2 ^ n° bit
        
        while(prime.compareTo(maxNumberBlock) != 1){
            prime = this.genPrime();
        }
        
        this.primeN=prime;
  
    }

 
    public Map<BigInteger, ArrayList<byte []>> split(byte[] secret) throws IOException {
        
        out.println("\nConversione Base64...");
        byte[] secretBase64 = Base64.getEncoder().encode(secret);

        TreeMap<BigInteger, ArrayList<byte[]>> mapN = new TreeMap<BigInteger, ArrayList<byte[]>>();
        
        out.println("\nSplit dei blocchi...");
        out.println("\nDimensione in base 64"+secretBase64.length);
        out.println("\nNumer Blocchi da splittare : " + secretBase64.length / this.blocksize);
        
        ArrayList<byte[]> blockList;
        ArrayList<BigInteger> a = new ArrayList<BigInteger>();

        for (int i = 0; i < this.k - 1; i++) {   // scelgo coefficienti random in Zp
            a.add(this.randomZp(this.primeN));
            out.println("valore a"+i+" "+a.get(i));
        }
        

        int j = 0;
        for (int i = 0; i < secretBase64.length / this.blocksize; i++) {

            j = this.blocksize * i;  // indice del blocco
            byte[] block = Arrays.copyOfRange(secretBase64, j, j + this.blocksize); // da indice del blocco al successivo
            out.println("blocco "+i+" "+new BigInteger(block));
            ArrayList<BigInteger> sbi = this.splitBlock(block, a);
            
            for (int n = 1; n < sbi.size() + 1; n++) { // for per il numero di partecipanti

                byte[] value = sbi.get(n - 1).toByteArray();   // prendo il polinomio del blocco n 
                out.println("f"+n+" "+new BigInteger(value));

                if (!mapN.containsKey(BigInteger.valueOf(n))) {  // se la mappa non lo contiene
                    blockList =  new ArrayList<byte[]>();
                    blockList.add(value);
                    mapN.put(BigInteger.valueOf(n),blockList);  // inseriscilo
                    

                } else {
                    blockList = mapN.get(BigInteger.valueOf(n));
                    blockList.add(value);
                    mapN.replace(BigInteger.valueOf(n), blockList); //concatena vc al nuovo ed inseriscilo
                      
                }

            }

           // out.println("Blocco " + (i + 1) + "R - ["+ j+" - "+(j+this.blocksize)+"]" );
        }
        /*
        for(BigInteger k: mapN.keySet()){
            for(int i =0 ; i<mapN.get(k).size();i++){
            out.println("id "+k+" value blocco"+i+" "+new BigInteger(mapN.get(k).get(i)));
            }
        }*/

        return mapN;
    }

    private ArrayList<BigInteger> splitBlock(byte[] block, ArrayList<BigInteger> a) {

        BigInteger Sb = new BigInteger(block); //rappresentazione del blocco iesimo

        ArrayList<BigInteger> rPolynomial = this.makeRP(a, Sb, this.primeN); // crea n polinomi per quel blocco

        return rPolynomial;  //domanda quanti polinomi ho a blocco ?

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
<<<<<<< HEAD


    public byte[] getSecret(Map<BigInteger, ArrayList<byte[]> > Kp) throws IOException {

        //iterare i blocchi e concatenarli
        out.print("\nconversione del segreto...");
        BigInteger tmp = null;
        byte[] secretFinal = null;

        if (Kp.size() >= this.k) {
=======
    
    public  byte[] getSecret(Map<BigInteger, byte[]> Kp){
        
        if(Kp.size()>=this.k){
>>>>>>> c928e697f12f658c957a25ee54abb076cde97e57
            

            List<BigInteger> idList = new ArrayList<BigInteger>(Kp.keySet()); // set degli ID
            idList = idList.subList(0, this.k);  // prendo i K che mi servono
            out.println("quanti k uso ? "+idList.size());

               for (int i = 0; i < Kp.get(idList.get(0)).size() ; i++) {    // itero i blocchi

            for (BigInteger idcurrent : idList) {  //id corrente dal set di ID
                
                byte[] block =  Kp.get(idcurrent).get(i); //prendo il blocco corrente
                //out.println("size block :"+block.length);
                BigInteger valueID = new BigInteger(block); //nominatore
                BigInteger den = new BigInteger("1"); //denominatore

                for (BigInteger id : idList) {  //risolvo il blocco
                    if (!idcurrent.equals(id)) {
                        valueID = valueID.multiply(id);
                        den = den.multiply(id.subtract(idcurrent));

                    }

                }
                
                if (tmp == null) {
                    tmp = (valueID.divide(den)).mod(this.primeN);

                } else {
                    tmp = tmp.add((valueID.divide(den)).mod(this.primeN));

                }
                out.println("blocco "+i+" risolto "+tmp);

            } //si chiude la risoluzione del blocco
           
            if (secretFinal == null) {
                    secretFinal = tmp.mod(this.primeN).toByteArray();

                } else {
                    secretFinal = Utility.concatByte(secretFinal, tmp.mod(this.primeN).toByteArray());

                }
            
            tmp = null; //riazzero 
            
               }  //qui si chiude il for dei blocchi
               
            return Base64.getDecoder().decode(secretFinal);
        }
        return null;

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
        } while (r.compareTo(BigInteger.ZERO) < 0 || r.compareTo(p) >= 0);
        return r;
    }

}
