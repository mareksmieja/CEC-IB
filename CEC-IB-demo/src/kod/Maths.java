package kod;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * A class for mathematical calculations.
 * 
 * @author m.smieja
 */
public class Maths {

  
    
    public static double sign(double x){
        if(x >= 0) return 1;
        else return -1;
    }
    
     public static double h(double p) {
        if (p == 0 || p == 1) {
            return 0;
        } else {
            return (-p * Math.log(p) - (1-p)*Math.log(1-p));
        }
    }

}
