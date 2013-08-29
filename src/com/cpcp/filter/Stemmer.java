package com.cpcp.filter;

import org.tartarus.snowball.SnowballStemmer;

/**
 * A simple wrapper around the Tartarus Snowball stemmer.
 */
public class Stemmer {
   /**
    * The Porter's stemmer to use.
    */
   private SnowballStemmer stem;

   public Stemmer() {
      stem = new org.tartarus.snowball.ext.porterStemmer();
   }

   /**
    * Stem a single word using Proter's algorithm.
    *
    * @param word The single word to stem.
    *
    * @return A new String containing the stemmed version of the word.
    */
   public String stem(String word) {
      stem.setCurrent(word);
      stem.stem();

      return stem.getCurrent();
   }

   /*
    * A static variant of the stemmer.
    * Slower because of overhead.
    */
   public static String staticStem(String word) {
      Stemmer stemmer = new Stemmer();
      return stemmer.stem(word);
   }

   /**
    * A Testing main used to check the behavior of the stemmer.
    *
    * @param args Ignored.
    */
   public static void main(String[] args) {
      SnowballStemmer stem = new org.tartarus.snowball.ext.porterStemmer();

      stem.setCurrent("watching");
      stem.stem();
      System.out.println(stem.getCurrent());

      stem.setCurrent("emotzsmilez");
      stem.stem();
      System.out.println(stem.getCurrent());

      stem.setCurrent("emotzfrownz");
      stem.stem();
      System.out.println(stem.getCurrent());

      stem.setCurrent("dave's");
      stem.stem();
      System.out.println(stem.getCurrent());

      stem.setCurrent("high-er");
      stem.stem();
      System.out.println(stem.getCurrent());

      stem.setCurrent("<watching>");
      stem.stem();
      System.out.println(stem.getCurrent());
   }
}
