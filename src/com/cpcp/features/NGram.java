package com.cpcp.features;

import com.cpcp.document.TextDocument;
import com.cpcp.filter.FullFilter;
import com.cpcp.filter.TextFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A FeatureSetGenerator that just splits the input into some n-gram.
 * ngrams greater than one are returned as a string with the grams delimited with a '-'.
 * Note that these grams are ordered and not a set.
 *
 * TODO(eriq): Just still returning a string a delimiting with a '-' is a bit of a hack, change it.
 */
public class NGram extends FeatureSetGenerator<TextDocument> {
   /**
    * The minium amout of times that a feature has to appear to be counted.
    */
   private final int min;

   /**
    * The 'n' in n-gram.
    */
   private final int n;

   private TextFilter filter;

   public NGram(int n, int min) {
      this(n, min, new FullFilter());
   }

   public NGram(int n, int min, TextFilter filter) {
      this.n = n;
      this.min = min;
      this.filter = filter;
   }

   /**
    * @inhericDoc
    */
   public Set<String> getFeatureSpace(List<TextDocument> documents,
                                      List<String> classes) {
      // feture => freq
      Map<String, Integer> counts = new HashMap<String, Integer>();
      Set<String> features = new HashSet<String>();

      for (TextDocument document : documents) {
         for (String gram : parseFeatures(document)) {
            if (!counts.containsKey(gram)) {
               counts.put(gram, 1);
            } else {
               counts.put(gram, counts.get(gram).intValue() + 1);
            }

            if (counts.get(gram).intValue() >= min) {
               features.add(gram);
            }
         }
      }

      return features;
   }

   /**
    * @inheritDoc
    */
   public Set<String> parseFeatures(TextDocument document) {
      Set<String> features = new HashSet<String>();

      for (String gram : split(document.getContent())) {
         features.add(gram);
      }

      return features;
   }

   private List<String> split(String content) {
      List<String> rtn = new ArrayList<String>();

      String[] words = filter.splitFilter(content);
      for (int i = 0; i + n <= words.length; i++) {
         String gram = "";
         for (int j = 0; j < n; j++) {
            gram += words[i + j];
            if (j != n - 1) {
               gram += '-';
            }
         }
         rtn.add(gram);
      }

      return rtn;
   }

   public String toString() {
      return String.format("%s{min=%d;n=%d;filter=%s}",
                           getClass().getCanonicalName(),
                           min,
                           n,
                           filter.toString());
   }
}
