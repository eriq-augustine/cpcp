package com.cpcp.filter;

import dk.brics.automaton.AutomatonMatcher;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses emoticons out of tweets.
 *
 * @author Allen Dunlea and Ryan Hnarakis
 */
public class EmoticonParser {
   /**
    * This string represents how the emoticon replacements will be formated.
    */
   private String format = "%s";

   /**
    * This is the mapping of emoticons to strings,
    * this is just one method that I implemented for it's ease
    * and so that testing could be done.
    */
   private Map <RunAutomaton, String> emoticons = null;

   /**
    *
    * @param format How the emoticon replacements will be formated.
    */
   public EmoticonParser(String format) {
      this();
      this.format = format;
   }

   /**
    *
    */
   public EmoticonParser() {
      emoticons = buildDictionary();
   }

   /**
    * Parses a single tweet by replacing all emoticons.
    *
    * @param string the tweet
    * @return a tweet with the emoticons replaced
    */
   public String parse(String string) {
      String parsedTweet = string;
      String temp = "";
      AutomatonMatcher matcher = null;
      for (RunAutomaton ra : emoticons.keySet()) {
         //parsedTweet = parsedTweet.replaceAll(emote, replaceEmoticon(emote));
         matcher = ra.newMatcher(parsedTweet);

         while (matcher.find()) {
            temp = parsedTweet.substring(0, matcher.start());
            temp = temp.concat(replaceEmoticon(ra));
            temp = temp.concat(parsedTweet.substring(matcher.end()));

            parsedTweet = temp;
            temp = "";
            matcher = ra.newMatcher(parsedTweet);
         }
      }
      return parsedTweet;
   }

   /**
    * This sets the format of the replaced emoticons.
    *
    * @param format the format to set
    */
   public void setFormat(String format) {
      this.format = format;
   }

   /**
    * @return the format
    */
   public String getFormat() {
      return format;
   }


   /**
    * Replaces an emoticon with a word using the set format (defaults to "%s").
    * @param emoticon
    * @return
    */
   protected String replaceEmoticon(RunAutomaton ra) {
      return String.format(format, emoticons.get(ra));
   }

   /**
    * Builds a basic dictionary of emoticons to words
    * The keys are finite state automaton.
    * @return
    */
   private Map<RunAutomaton,String> buildDictionary() {
      Map<RunAutomaton,String> dictionary = new HashMap<RunAutomaton, String>();

      //eyes on left smiles
      dictionary.put(new RunAutomaton(new RegExp("[:8=][ -o]?[\\)\\]>\\}D]").toAutomaton()),
                     "smile");

      //eyes on right smiles
      dictionary.put(new RunAutomaton(new RegExp("[\\(\\[<\\{C][ -o]?[:8=]").toAutomaton()),
                     "smile");

      //eyes on left angry
      dictionary.put(new RunAutomaton(new RegExp(">[:8=][ -o]?[\\(\\[<\\{o]").toAutomaton()),
                     "angry");

       //eyes on right angry
      dictionary.put(new RunAutomaton((new RegExp("[\\)\\]>\\}Do][ -o]?[:8=]\\<")).toAutomaton()),
                     "angry");

      //eyes on left frowns
      dictionary.put(new RunAutomaton(new RegExp("[:8=][ -o]?[\\(\\[\\{C]").toAutomaton()),
                     "frown");

      //eyes on right frowns
      dictionary.put(new RunAutomaton(new RegExp("[\\)\\]\\}D][ -o]?[:8=]").toAutomaton()),
                     "frown");

      //eyes on left smiles
      dictionary.put(new RunAutomaton(new RegExp(";[ -o]?[\\)\\]>\\}D]").toAutomaton()), "wink");

      //eyes on right winks
      dictionary.put(new RunAutomaton(new RegExp("[\\(\\[<\\{][ -o]?;").toAutomaton()), "wink");

      //eyes on left smiles
      dictionary.put(new RunAutomaton(new RegExp("[:8=][ ]?[\\\\/]").toAutomaton()), "slant");

      //eyes on right winks
      dictionary.put(new RunAutomaton(new RegExp("[\\\\/][ ]?[:=8]").toAutomaton()), "slant");

      //heart
      dictionary.put(new RunAutomaton(new RegExp("\\<3").toAutomaton()), "heart");

      //eastern faces
      dictionary.put(new RunAutomaton(new RegExp(">.>").toAutomaton()), "shifty");
      dictionary.put(new RunAutomaton(new RegExp("\\<.\\<").toAutomaton()), "shifty");

      dictionary.put(new RunAutomaton(new RegExp("\\^.\\^").toAutomaton()), "happy");

      dictionary.put(new RunAutomaton(new RegExp(">.\\<").toAutomaton()), "doh");

      return dictionary;
   }
}
