package com.cpcp.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface for a text filter.
 * Filters will take a string and return a new string that has been filtered.
 */
public abstract class TextFilter {
   /**
    * Filter a string.
    */
   public String filter(String input) {
      String rtn = "";
      String[] words = splitFilter(input);

      for (String word : words) {
         rtn += word + " ";
      }

      return rtn.replaceFirst(" $", "");
   }

   /**
    * Filter and split a string.
    * This is where the core functionality is.
    */
   public abstract String[] splitFilter(String input);

   /**
    * Filter many strings.
    */
   public List<String> filter(List<String> input) {
      List<String> rtn = new ArrayList<String>();

      for (String str : input) {
         rtn.add(filter(str));
      }

      return rtn;
   }

   public List<String[]> splitFilter(List<String> input) {
      List <String[]> rtn = new ArrayList<String[]>();

      for (String str : input) {
         rtn.add(splitFilter(str));
      }

      return rtn;
   }

   public String toString() {
      return this.getClass().getCanonicalName();
   }
}
