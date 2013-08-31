package com.cpcp.features;

import com.cpcp.document.TextDocument;
import com.cpcp.filter.SmartSplitString;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * The Subject Feature Selection algorithm propositioned by Dr. Leilei Chu.
 */
public class SF extends NGram {
   /**
    * This may not actually be the number of features in the final set,
    *  however it is a statring point and should usually be close to the
    *  final size.
    */
   private final int numFeatures;

   private final double threshold;

   public SF(int k, double b) {
      this(1, 5, k, b);
   }

   public SF(int n, int unigramMin, int k, double b) {
      super(n, unigramMin);
      numFeatures = k;
      threshold = b;
   }

   /**
    * @inheritDoc
    */
   public Set<String> getFeatureSpace(List<TextDocument> documents, List<String> classes) {
      Map<String, Integer> freqs = getFreqs(documents);

      if (freqs.size() <= numFeatures) {
         return new HashSet<String>(freqs.keySet());
      }

      TreeSet<WordCount> orderedWords = orderCounts(freqs);
      Set<String> features = new HashSet<String>();

      // The frequency of the last word added to the feature set.
      int fenceValue = 0;
      int i = 0;
      for (WordCount wordCount : orderedWords) {
         if (i == numFeatures) {
            break;
         }

         features.add(wordCount.word);

         //Last word
         if (i == (numFeatures - 1)) {
            fenceValue = wordCount.count;
         }

         i++;
      }

      // Use these values to get all the WordCounts with fenceValue count.
      WordCount startVal = new WordCount(null, fenceValue);
      WordCount endVal = new WordCount(null, fenceValue - 1);
      Set<WordCount> fenceWords = orderedWords.subSet(startVal, endVal);

      int onlyFeatureSet = 0;
      for (WordCount wordCount : orderedWords) {
         if (wordCount.count == fenceValue) {
            break;
         } else {
            onlyFeatureSet++;
         }
      }

      int intersection = numFeatures - onlyFeatureSet;
      int onlyTail = fenceWords.size() - intersection;

      if ((double)intersection / onlyTail >= threshold) {
         for (WordCount wordCount : fenceWords) {
            features.add(wordCount.word);
         }
      } else {
         for (WordCount wordCount : fenceWords) {
            features.remove(wordCount.word);
         }

         if (features.size() == 0) {
            for (WordCount wordCount : fenceWords) {
               features.add(wordCount.word);
            }
         }
      }

      return features;
   }

   private TreeSet<WordCount> orderCounts(Map<String, Integer> freqs) {
      TreeSet<WordCount> rtn = new TreeSet<WordCount>(new Comparator<WordCount>(){
         public int compare(WordCount a, WordCount b) {
            return b.count - a.count;
         }
      });

      for (Map.Entry<String, Integer> entry : freqs.entrySet()) {
         rtn.add(new WordCount(entry.getKey(), entry.getValue()));
      }

      return rtn;
   }

   private Map<String, Integer> getFreqs(List<TextDocument> documents) {
      Map<String, Integer> freqs = new HashMap<String, Integer>();

      for (TextDocument document : documents) {
         for (String word : SmartSplitString.split(document.getContent())) {
            if (!freqs.containsKey(word)) {
               freqs.put(word, 1);
            } else {
               freqs.put(word, freqs.get(word) + 1);
            }
         }
      }

      return freqs;
   }

   public String toString() {
      return String.format("%s{super=%s;numFeatures=%d;threshold=%f}",
                           getClass().getCanonicalName(),
                           super.toString(),
                           numFeatures,
                           threshold);
   }

   private static class WordCount {
      public String word;
      public int count;

      public WordCount(String word, int count) {
         this.word = word;
         this.count = count;
      }
   }
}
