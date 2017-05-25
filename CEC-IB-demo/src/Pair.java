package src;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 * A class holding pairs of numbers.
 *
 * @author m.smieja
 */
public class Pair<A, B> {
 
  public A fst;
  public B snd;
 
  public Pair(A fst, B snd) {
    this.fst = fst;
    this.snd = snd;
  }

 
  public String toString() {
    return "Pair[" + fst + "," + snd + "]";
  }
 
  private static boolean equals(Object x, Object y) {
    return (x == null && y == null) || (x != null && x.equals(y));
  }
 
  public boolean equals(Object other) {
     return
      other instanceof Pair &&
      equals(fst, ((Pair)other).fst) &&
      equals(snd, ((Pair)other).snd);
  }
  
  @Override
    public int hashCode() {
        return (fst == null ? 0 : fst.hashCode()) ^ (snd == null ? 0 : snd.hashCode());
    }
 
}

