package progetto4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import static java.lang.System.out;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author f.did
 */
public class SecretSharing implements Serializable {

    private int k;
    private int n;
    private int CERTAINTY = 50;
    private int modLength; // n bit del p 
    private BigInteger primeN;
    private int blocksize; //in byte 
    private boolean debug = false;  //abilito stampe di debug 

    public SecretSharing(int k, int n, int blocksize) {

        this.k = k;
        this.n = n;
        this.blocksize = blocksize;
        this.modLength = 8 * blocksize + 1;

        BigInteger prime = this.genPrime();
        BigInteger two = new BigInteger("2");
        BigInteger maxNumberBlock = (two.pow(8 * this.blocksize)); // 2 ^ n° bit

        while (prime.compareTo(maxNumberBlock.divide(two)) != 1) {  // servono la metà poichè è stato convertito in base64
            prime = this.genPrime();
        }
        this.primeN = prime;
        if (debug) {
            out.println("Primo generato : " + this.primeN);
        }

    }

    public Map<BigInteger, ArrayList<byte[]>> split(byte[] secret) throws IOException {

        if (debug) {
            out.println("\nConversione Base64...");
        }

        byte[] compressData = Utility.compress(secret);
        byte[] secretBase64 = Base64.getEncoder().encode(compressData);

        Map<BigInteger, ArrayList<byte[]>> mapN = new TreeMap<BigInteger, ArrayList<byte[]>>();

        if (debug) {
            out.println("\nDimensione file : " + secret.length);
        }
        if (debug) {
            out.println("\nDimensione file compresso : " + compressData.length);
        }
        if (debug) {
            out.println("\nDimensione in base 64 : " + secretBase64.length);
        }

        byte[] block;
        ArrayList<BigInteger> a;
        ArrayList<byte[]> blockList;
        int resto = secretBase64.length % this.blocksize;

        if (debug) {
            out.println("\nNumer Blocchi da splittare : " + secretBase64.length / this.blocksize);
        }
        if (debug) {
            out.println("\nResto del blocco : " + resto);
        }
        if (debug) {
            out.println("\nSplit dei blocchi...");
        }

        int j = 0;
        for (int i = 0; i < secretBase64.length / this.blocksize; i++) { // scorro tutti i blocchi

            j = this.blocksize * i;  // indice del blocco
            block = Arrays.copyOfRange(secretBase64, j, j + this.blocksize); // prendo il blocco corrente
            a = this.randomCoeffList();
            ArrayList<BigInteger> sbi = this.splitBlock(block, a);
            this.concShareToMap(sbi, mapN);  // creo gli share del blocco

        }

        if (resto != 0) { // controllo se la dimensione del file non è un multiplo

            if (debug) {
                out.println("elaboro resto : " + resto);
            }
            block = Arrays.copyOfRange(secretBase64, (secretBase64.length - resto), secretBase64.length);
            a = this.randomCoeffList();
            ArrayList<BigInteger> sbi = this.splitBlock(block, a);
            this.concShareToMap(sbi, mapN);

        }

        return mapN;
    }

    public byte[] getSecret(Map<BigInteger, ArrayList<byte[]>> Kp) throws IOException, DataFormatException {

        BigInteger result = BigInteger.ZERO;
        byte[] secretFinal = null;
        Map<BigInteger, BigInteger> invModMap = new TreeMap<BigInteger, BigInteger>();

        if (Kp.size() >= this.k) {

            List<BigInteger> idList = new ArrayList<BigInteger>(Kp.keySet()); // set degli ID

            idList = idList.subList(0, this.k);  // prendo i K che mi servono

            for (int i = 0; i < Kp.get(idList.get(0)).size(); i++) {    // itero i blocchi

                for (BigInteger idcurrent : idList) {  //id corrente dal set di ID   

                    byte[] block = Kp.get(idcurrent).get(i); //risolvo il blocco ,prendo il blocco corrente
                    BigInteger nom = new BigInteger(block); //nominatore
                    BigInteger den = new BigInteger("1"); //denominatore

                    for (BigInteger id : idList) {  // itero gli id diversi dal corrente

                        if (!idcurrent.equals(id)) {
                            den = den.multiply(id.subtract(idcurrent));
                            nom = nom.multiply(id);

                        }

                    }
                    if (invModMap.containsKey(den)) {  // se ho già calcolato il mInv lo utilizzo
                        nom = (nom).multiply(invModMap.get(den));
                    } else // se no lo calcolo
                    {
                        BigInteger minv = this.mInv(den);
                        invModMap.put(den, minv);
                        nom = (nom).multiply(minv);
                    }

                    result = result.add((nom));

                }// fine risoluzione del blocco

                result = result.mod(this.primeN); // calcolato la soluzione del blocco iesimo

                if (secretFinal == null) {  // concateno alla soluzione finale
                    secretFinal = result.toByteArray();
                } else {
                    secretFinal = Utility.concatByte(secretFinal, result.toByteArray());
                }
                result = BigInteger.ZERO; //riazzero 

            }  //qui si chiude il for dei blocchi

            return Utility.decompress(Base64.getDecoder().decode(secretFinal));

        } else {
            out.println("\n***** Errore blocchi ricevuti minori di k : " + Kp.size() + " < " + this.k);
            return null;
        }

    }

    private void concShareToMap(ArrayList<BigInteger> sbi, Map mapN) {

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

    private ArrayList<BigInteger> randomCoeffList() {

        ArrayList<BigInteger> a = new ArrayList<BigInteger>();
        for (int t = 0; t < this.k - 1; t++) {   // scelgo coefficienti random in Zp
            BigInteger ai = this.randomZp(this.primeN);
            a.add(ai);
        }
        return a;
    }

    private ArrayList<BigInteger> splitBlock(byte[] block, ArrayList<BigInteger> a) {  //1

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

    private BigInteger computeBlock(ArrayList<BigInteger> numList, ArrayList<BigInteger> denList) {

        BigInteger lcm = Utility.lcm(denList);
        BigInteger n;
        BigInteger d;
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < numList.size(); i++) {
            n = numList.get(i);
            d = denList.get(i);
            result = (result.add(n.multiply(lcm.divide(d)))).mod(this.primeN);

        }

        return (result.divide(lcm)).mod(this.primeN);

    }

    private BigInteger mInv(BigInteger a) {

        return a.modInverse(this.primeN);

    }


    public int[] getParameters(){
        int[] info=new int[3];
        info[0]=this.k;
        info[1]=this.n;
        info[2]=this.blocksize;
        return info;
    }

}
