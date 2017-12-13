/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progetto4;

import static java.lang.System.out;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 *
 * @author gia
 */
public class SecretSharing {

    private int k;
    private int n;
    private int CERTAINTY = 50;
    private int modLength = 8;
    private BigInteger primeN;

    public SecretSharing(int k, int n) {

        this.k = k;
        this.n = n;

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

            prime = this.genPrime();
            condition = prime.compareTo(new BigInteger(String.valueOf(this.n))) != 1;
            condition = condition || prime.compareTo(S) != 1;

        }*/
    //    out.println("Prime -> " + prime);
        this.primeN = prime;

        ArrayList<BigInteger> a = new ArrayList<BigInteger>();

        for (int i = 0; i < this.k - 1; i++) {   // scelgo coefficienti random in Zp
            a.add(this.randomZp(prime));
        }

     //   out.println("numero dei coefficienti : " + a.size());
        for (BigInteger ax : a) {
     //       out.println("coefficiente : " + ax);
        }

        ArrayList<BigInteger> rPolynomial = this.makeRP(a, S, prime);

        for (int i = 1; i < this.n + 1; i++) { //assegno i polinomi alla mappa {Pi : f(i)}
            mapN.put(BigInteger.valueOf(i), rPolynomial.get(i - 1).toByteArray());
        }

        return mapN;

    }

    private ArrayList<BigInteger> makeRP(ArrayList<BigInteger> a, BigInteger S, BigInteger prime) {

        ArrayList<BigInteger> RP = new ArrayList<BigInteger>();
      //  out.println("Genero polinomi ");

        for (int i = 1; i < this.n + 1; i++) {  //indice del polinomio f(i)

            BigInteger tmp = S.mod(prime);
            BigInteger x = BigInteger.valueOf(i); //sostituisco x con i
        //    out.println("partecipante ->  " + i);
            

            for (int nExp = 1; nExp < this.k; nExp++) {  //indice dell'esponente
          //      out.println("a("+a.get(nExp - 1)+")*("+x+")^"+nExp);
               
                tmp = tmp.add((a.get(nExp - 1).multiply(x.pow(nExp)))); //a(nExp-1)*x^nExp addizionati ad S inizale
            //    out.println(" + ");
            }
            //out.println("= " + tmp.mod(prime) +"\n");

            RP.add(tmp.mod(prime)); // polinomio

        }
        return RP;
    }
    
    public  byte[] getSecret(Map<BigInteger, byte[]> Kp){
        
        if(Kp.size()>=this.k){
            
            List<BigInteger> idList = new ArrayList<BigInteger>(Kp.keySet());
            idList = idList.subList(0, this.k);
            BigInteger secret=null;
           
         //   out.println("Numero di k per la ricostruzione : "+idList.size());

            for(BigInteger idcurrent : idList){
                BigInteger valueID = new BigInteger(Kp.get(idcurrent)); //nominatore
                 BigInteger den = new BigInteger("1"); //denominatore

          //     out.println("idcurrent : "+idcurrent);
                for(BigInteger id : idList){
                    if(!idcurrent.equals(id)){
                       
             //           out.print("y("+valueID+")*( id("+id+") / id("+id+") - idcurrent("+idcurrent+") ) = ");
              //          out.println( "ris denom -> "+id.subtract(idcurrent) );
                        
                        valueID=  valueID.multiply( id ) ;
                        den = den.multiply(id.subtract(idcurrent));
                        //valueID=  valueID.multiply( id.divide(id.subtract(idcurrent)) ) ;
           //             out.println("nom :"+valueID);
           //             out.println("den :"+den);
                        

                    }
                     
                }
           //     out.println(" = ");
                if(secret == null){
                    //secret=valueID.mod(this.primeN);
                    secret=(valueID.divide(den)).mod(this.primeN);
        //            out.println(valueID+"\n");
                }else{
                //secret = secret.add(valueID.mod(this.primeN));
                 secret = secret.add((valueID.divide(den)).mod(this.primeN));
         //       out.println(valueID+"\n");
                }
                
                
            }
            secret = secret.mod(this.primeN);
            return this.checkPositive(secret);
            
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
    
    private byte[] checkPositive(BigInteger x){
        
        String soglia ="127";
        
        if(x.compareTo(new BigInteger(soglia)) == 1){
            x = x.add(new BigInteger("-128"));
            x = x.add(new BigInteger("-128"));
        }
        
        return x.toByteArray();
    }
    
    
        

}
