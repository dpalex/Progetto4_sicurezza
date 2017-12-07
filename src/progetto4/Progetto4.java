/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progetto4;

import java.math.BigInteger;
import java.util.Random;

/**
 *
 * @author dp.alex
 */
public class Progetto4 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        BigInteger b = new BigInteger("4");
        System.out.println(b);
        System.out.println(BigInteger.probablePrime(8, new Random()));
        System.out.println(BigInteger.probablePrime(8, new Random()));
        System.out.println(BigInteger.probablePrime(8, new Random()));
        System.out.println(BigInteger.probablePrime(8, new Random()));
        System.out.println("genPrime");
        System.out.println(genPrime());
        System.out.println(genPrime());
        System.out.println(genPrime());
        randomZp();
        randomZp();
        randomZp();

    }

    private static BigInteger genPrime() {
        BigInteger p = null;
        boolean ok = false;
        int modLength = 8;
        int CERTAINTY = 50;
        do {
            p = BigInteger.probablePrime(modLength, new Random());
            if (p.isProbablePrime(CERTAINTY)) {
                ok = true;
            }
        } while (ok == false);
        return p;
    }

    private static void randomZp() {
        BigInteger r;
        int modLength = 8;
        BigInteger p = genPrime();
        do {
            r = new BigInteger(modLength, new Random());
           
        } while (r.compareTo(BigInteger.ZERO) < 0 || r.compareTo(p) >= 0);
        
        System.out.println("numero casuale:"+r+"  in Z:"+p);
    }

}
