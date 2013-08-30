package com.cpcp.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class to handle stop word related activities like removing all the
 *  stop words from a String.
 * Stopwords are marked with varying level of importance with 0 being the most
 *  frequent stopwords.
 * Not only are stopwords kept in the table, but also the stem of stopwords.
 * All calls made available from this class will be static.
 */
public final class StopWordUtils {
   /**
    * Construct a new StopWordFilter.
    * Private to promote static behavior.
    */
   private StopWordUtils() {
   }

   /**
    * Load the stopwords from the database into memory.
    *
    * @return A HashMap representing the stopword table.
    */
   private static Map<Integer, Set<String>> loadStopWords() {
      Map<Integer, Set<String>> rtn = new HashMap<Integer, Set<String>>();

      Stemmer stemmer = new Stemmer();

      Set<String> level0 = new HashSet<String>();

      for (String stopword : LEVEL_0_STOPWORDS) {
         level0.add(stopword);
         // Also add the stem.
         level0.add(stemmer.stem(stopword));
      }

      rtn.put(new Integer(0), level0);

      return rtn;
   }

   public static String[] removeStopWords(List<String> input, int level) {
      return removeStopWords(input.toArray(new String[input.size()]), level);
   }

   /**
    * Return a List with all stopwords removed.
    * Only remove stopwords at the given level and below.
    * The words in input should all be lowercase.
    * Words of length two or less are automatically considered stopwords.
    *
    * @param input The list of words to analyze.
    * @param level The minimum stop level.
    *
    * @return A list of words that is like input, but without stopwords.
    *
    * @.pre level should be positive.
    */
   public static String[] removeStopWords(String[] input, int level) {
      List<String> rtn = new ArrayList<String>();
      boolean isStop;
      Set<String> levelStopWords = getStopWords(level);

      for (String word : input) {
         word = word.trim();

         isStop = false;

         if (word.length() > 2) {
            for (int ndx = 0; ndx <= level; ndx++) {
               if (levelStopWords.contains(word)) {
                  isStop = true;
                  break;
               }
            }

            if (!isStop) {
               rtn.add(word);
            }
         }
      }

      return rtn.toArray(new String[0]);
   }

   public static Set<String> getStopWords(int maxLevel) {
      Set<String> stopWordsForLevel = new HashSet<String>();

      for (int level = 0; level <= maxLevel; level++) {
         Set<String> levelMap = stopwords.get(level);

         if (levelMap != null) {
            stopWordsForLevel.addAll(levelMap);
         }
      }

      return stopWordsForLevel;
   }

   /**
    * The most base stopwords taken from: http://www.ranks.nl/resources/stopwords.html.
    */
   private static final String[] LEVEL_0_STOPWORDS = new String[]{
      "a",
      "about",
      "above",
      "after",
      "again",
      "against",
      "all",
      "am",
      "an",
      "and",
      "any",
      "are",
      "aren't",
      "as",
      "at",
      "be",
      "because",
      "been",
      "before",
      "being",
      "below",
      "between",
      "both",
      "but",
      "by",
      "can't",
      "cannot",
      "could",
      "couldn't",
      "did",
      "didn't",
      "do",
      "does",
      "doesn't",
      "doing",
      "don't",
      "down",
      "during",
      "each",
      "few",
      "for",
      "from",
      "further",
      "had",
      "hadn't",
      "has",
      "hasn't",
      "have",
      "haven't",
      "having",
      "he",
      "he'd",
      "he'll",
      "he's",
      "her",
      "here",
      "here's",
      "hers",
      "herself",
      "him",
      "himself",
      "his",
      "how",
      "how's",
      "i",
      "i'd",
      "i'll",
      "i'm",
      "i've",
      "if",
      "in",
      "into",
      "is",
      "isn't",
      "it",
      "it's",
      "its",
      "itself",
      "let's",
      "me",
      "more",
      "most",
      "mustn't",
      "my",
      "myself",
      "no",
      "nor",
      "not",
      "of",
      "off",
      "on",
      "once",
      "only",
      "or",
      "other",
      "ought",
      "our",
      "ours ",
      "ourselves",
      "out",
      "over",
      "own",
      "same",
      "shan't",
      "she",
      "she'd",
      "she'll",
      "she's",
      "should",
      "shouldn't",
      "so",
      "some",
      "such",
      "than",
      "that",
      "that's",
      "the",
      "their",
      "theirs",
      "them",
      "themselves",
      "then",
      "there",
      "there's",
      "these",
      "they",
      "they'd",
      "they'll",
      "they're",
      "they've",
      "this",
      "those",
      "through",
      "to",
      "too",
      "under",
      "until",
      "up",
      "very",
      "was",
      "wasn't",
      "we",
      "we'd",
      "we'll",
      "we're",
      "we've",
      "were",
      "weren't",
      "what",
      "what's",
      "when",
      "when's",
      "where",
      "where's",
      "which",
      "while",
      "who",
      "who's",
      "whom",
      "why",
      "why's",
      "with",
      "won't",
      "would",
      "wouldn't",
      "you",
      "you'd",
      "you'll",
      "you're",
      "you've",
      "your",
      "yours",
      "yourself",
      "yourselves"
   };

   /**
    * The stopwords table.
    * Each inner Set is just a presence HashMap representing a different stop level.
    * Stopword levels decrease in importance from zero. (Zero is the most
    *  important/frequent stopwords).
    */
   private static Map<Integer, Set<String>> stopwords = loadStopWords();
}
