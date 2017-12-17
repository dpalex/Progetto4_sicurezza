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
           BigInteger x = BigInteger.valueOf(3);
     BigInteger y = BigInteger.valueOf(5);
     BigInteger k = BigInteger.valueOf(15);
     BigInteger q = BigInteger.valueOf(6);
     ArrayList<BigInteger> l = new ArrayList<BigInteger> ();
     l.add(x);
     l.add(y);
     l.add(k);
     l.add(q);
     BigInteger mod = BigInteger.valueOf(277);
     out.println(Utility.lcm(l));

  /*  x = x.mod(mod);
    out.println(x);
 
    y = y.mod(mod);
       out.println(y);
    BigInteger k = (x.add(y)).divide(BigInteger.valueOf(2));
    out.println(k.mod(mod));*/
  
    

   
    }
    
}
