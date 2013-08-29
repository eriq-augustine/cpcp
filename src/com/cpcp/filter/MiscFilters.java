package com.cpcp.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to miscilanious filtering activiting like stripping links.
 */
public class MiscFilters {
   /**
    * Construct a new MiscFilter.
    * Private to promote static behavior.
    */
   private MiscFilters() {
   }

   /**
    * Turn a string into a String[].
    * Stemming will occur, links will be removed, and all non-alpha characters will
    *  be taken out.
    * There will be no extra space around the words.
    *
    * @param text The String to split up. This will be modified.
    *
    * @return The list of words in text.
    */
   public static List<String> fullSplitString(String text) {
      String[] words = new String[0];
      Stemmer stemmer = new Stemmer();

      text = MiscFilters.removeLinks(text);

      text = text.replaceAll("[^a-zA-Z\\s]", " ").trim();
      text = text.replaceAll("\\s\\s+", " ");
      text = text.toLowerCase();
      words = text.split("\\s");
      words = StopWordUtils.removeStopWords(words, 0);

      List<String> rtn = new ArrayList<String>();

      for (int ndx = 0; ndx < rtn.size(); ndx++) {
         rtn.add(stemmer.stem(words[ndx]));
      }

      return rtn;
   }

   /**
    * Turn a string into a String[].
    * Stemming will NOT occur, links will be removed, and all non-alpha characters will
    *  be taken out.
    * There will be no extra space around the words.
    *
    * @param text The String to split up. This will be modified.
    *
    * @return The list of words in text.
    */
   public static List<String> fullSplitStringNoStem(String text) {
      String[] words = new String[0];

      text = MiscFilters.removeLinks(text);

      text = text.replaceAll("[^a-zA-Z\\s]", " ").trim();
      text = text.replaceAll("\\s\\s+", " ");
      text = text.toLowerCase();
      words = text.split("\\s");
      words = StopWordUtils.removeStopWords(words, 0);

      List<String> rtn = new ArrayList<String>();
      for (String word : words) {
         rtn.add(word);
      }

      return rtn;
   }

   /**
    * Split a string into an array of Strings.
    * No Stemming will occur, but all non-alpha characters will
    *  be taken out.
    * There will be no extra space around the words.
    *
    * @param text The String to split up. This will be modified.
    *
    * @return The list of words in text.
    */
   public static String[] splitString(String text) {
      String[] words = new String[0];

      text = text.replaceAll("[^a-zA-Z\\s']", " ").trim();
      text = text.replaceAll("[']", "");
      text = text.replaceAll("\\s\\s+", " ");
      text = text.toLowerCase();
      words = text.split("\\s");

      return words;
   }

   /**
    * Remove hyperlinks from the text and return a clean string.
    *
    * @param input The String to remove hyperlinks from.
    *
    * @return A String without hyperlinks.
    */
   public static String removeLinks(String input) {
      //Note that bitly links dont have an extension.
      //Links must start with a (http(s) or www) or end with an extension.
      String linkRegex = "((\\S*\\.)?www\\.\\S+)|(https?://\\S+\\.\\S+(\\.\\S+)?)|" +
                         "([^\\s\\.]+\\.((com)|(edu)|(org)|(net)|(gov)))(/\\S+)*/?";

      return input.replaceAll(linkRegex, "");
   }

   /**
    * Remove hyperlinks from the text and return a clean string.
    * Note that running this with replacement = "" (empty string) will result
    *  in the same behavior as removeLinks.
    *
    * As per "Cailin's Convention", any bangs ('!') in the replacement string
    *  will be replaced with the matching link.
    *  Ex: (the is a stopword) input = "the dog runs", replacement = "<!>"
    *  output = "<the> dog runs"
    *
    * @param input The String to remove hyperlinks from.
    * @param replacement The string to replace the stopword with.
    *
    * @return A String without hyperlinks.
    *
    * @TODO(eriq): The regex that this method uses has some subtle errors.
    */
   public static String replaceLinks(String input, String replacement) {
      //Note that bitly links dont have an extension.
      //Links must start with a (http(s) or www) or end with an extension.
      String linkRegex = "((\\S*\\.)?www\\.\\S+)|(https?://\\S+\\.\\S+(\\.\\S+)?)|" +
                         "([^\\s\\.]+\\.((com)|(edu)|(org)|(net)|(gov)))(/\\S+)*/?";

      //Escape '$' in the replacemnet string
      replacement = replacement.replaceAll("\\$", "\\\\\\$");
      replacement = replacement.replaceAll("!", "\\$0");

      return input.replaceAll(linkRegex, replacement);
   }

   /**
    * Remove usernames from the text and return a clean string.
    *
    * @param input
    *           The String to remove usernames from.
    *
    * @return A String without usernames.
    */
   public static String removeUsernames(String input) {
      String usernameRegex = "(@(\\S+))";

      return input.replaceAll(usernameRegex, "");
   }
}
