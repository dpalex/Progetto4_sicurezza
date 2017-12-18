/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package progetto4;

import static java.lang.System.out;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 *
 * @author f.did
 */
public class TestBB {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

     /*BigInteger x = BigInteger.valueOf(56/3);
     BigInteger y = BigInteger.valueOf(221);
     BigInteger mod = BigInteger.valueOf(277);
     out.println(x);
     out.println(x.modInverse(mod));
     out.println(y.modInverse(mod));*/
     
     int modinv2=0;
     
     for(int x =0 ;x<277;x++){
         if( Math.floorMod(-3*x,277 )==1){
             modinv2=x;
             break;
         }
     }
 //    out.println(modinv2);
     
    
      BigInteger a = BigInteger.valueOf(3);
        BigInteger modInv = a.modInverse(BigInteger.valueOf(277));
   /*     
       for(BigInteger x = BigInteger.ZERO;x.compareTo(BigInteger.valueOf(277))!=1;x = x.add(BigInteger.ONE)){
           modInv= (a.multiply(x)).mod( BigInteger.valueOf(277)  );
           if(  modInv.compareTo(BigInteger.ONE) == 0 ){
              modInv = x ;
              break;
           }
       }
       */
       out.println(modInv);

     
     

  /*  x = x.mod(mod);
    
 
    y = y.mod(mod);
       out.println(y);
    BigInteger k = (x.add(y)).divide(BigInteger.valueOf(2));
    out.println(k.mod(mod));*/
  
    

   
    }
    
}
