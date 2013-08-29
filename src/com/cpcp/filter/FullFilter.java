package com.cpcp.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * A Filter that removes stop words, replaces links and emotes, and does stemming.
 * It does pretty much everything but Title replacement, and only because that takes longer.
 *
 * Order:
 *  Links
 *  Emotes
 *  Smart Split (Also remove Punct)
 *  Stop words
 */
public class FullFilter extends TextFilter {
   private static EmoticonParser emote = new EmoticonParser(" <$emote:%s$> ");

   private boolean replaceTwitterMetaWords;

   // Convience main for testing/fun/writing papers.
   public static void main(String[] args) {
      String test = "RT @pawlooza: ... wished @netflix had The Littlest Hobo." +
                    " If you're gonna launch in Canada, know your demographic :)" +
                    " http://bit.ly/WbcrU";

      TextFilter filter = new FullFilter();

      System.out.println("Original: " + test);
      System.out.println("Filtered: " + filter.filter(test));
   }

   public FullFilter(boolean replaceTwitterMetaWords) {
      super();
      this.replaceTwitterMetaWords = replaceTwitterMetaWords;
   }

   public FullFilter() {
      this(true /* replace meta words */);
   }

   /**
    * @inheritDoc
    */
   public String[] splitFilter(String input) {
      input = MiscFilters.replaceLinks(input, " <$link$> ");
      input = emote.parse(input);

      String[] allWords = SmartSplitString.split(input, replaceTwitterMetaWords);
      allWords = StopWordUtils.removeStopWords(allWords, 0);

      return allWords;
   }

   public String toString() {
      return super.toString() + "{Replace Meta Words = " + replaceTwitterMetaWords + "}";
   }
}
