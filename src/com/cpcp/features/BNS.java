package com.cpcp.features;

import com.cpcp.document.TextDocument;
import com.cpcp.filter.TextFilter;
import com.cpcp.util.math.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * A FeatureSetGenerator that uses Bi-Normal Separation.
 * See http://dl.acm.org/citation.cfm?id=944974 for a description of BNS.
 */
public class BNS extends NGram {
   public BNS() {
      super(1, 5);
   }

   public BNS(int n, int min, TextFilter filter) {
      super(n, min, filter);
   }

   /**
    * @inhericDoc
    */
   public Set<String> getFeatureSpace(List<TextDocument> documents,
                                      List<String> classes) {
      Set<String> rtn = new HashSet<String>();

      List<Set<String>> documentFeatures = new ArrayList<Set<String>>();
      for (TextDocument document : documents) {
         documentFeatures.add(parseFeatures(document));
      }

      // Get all possible features.
      Set<String> features = super.getFeatureSpace(documents, classes);
      Set<String> allClasses = new HashSet<String>();
      allClasses.addAll(classes);

      for (String className : allClasses) {
         List<Double> tprValues = new ArrayList<Double>();
         List<Double> fprValues = new ArrayList<Double>();

         Map<String, ProbabilityRatio> ratios = new HashMap<String, ProbabilityRatio>();
         for (String feature : features) {
            ProbabilityRatio ratio = getRatio(feature, className, documentFeatures, classes);
            tprValues.add(ratio.tpr);
            fprValues.add(ratio.fpr);
            ratios.put(feature, ratio);
         }

         double tMean = MathUtils.mean(tprValues);
         double tStdDev = MathUtils.stdDev(tprValues, tMean);

         double fMean = MathUtils.mean(fprValues);
         double fStdDev = MathUtils.stdDev(fprValues, fMean);

         TreeMap<FeatureValue, String> orderedMap = new TreeMap<FeatureValue, String>();
         for (Map.Entry<String, ProbabilityRatio> featureRatio : ratios.entrySet()) {
            double score = Math.abs(((featureRatio.getValue().tpr - tMean) / tStdDev) -
                                    ((featureRatio.getValue().fpr - fMean) / fStdDev));
            orderedMap.put(new FeatureValue(featureRatio.getKey(), score), featureRatio.getKey());
         }
      }

      return rtn;
   }

   private ProbabilityRatio getRatio(String feature, String className,
                                     List<Set<String>> documentFeatures, List<String> classes) {
      // Number of times |className| appears.
      int classCount = 0;
      int nonClassCount = 0;
      // Number of times |feature| appears in |className| documents.
      int tp = 0;
      int fp = 0;

      for (int i = 0; i < documentFeatures.size(); i++) {
         if (classes.get(i).equals(className)) {
            classCount++;

            if (documentFeatures.get(i).contains(feature)) {
               tp++;
            }
         } else {
            nonClassCount++;

            if (documentFeatures.get(i).contains(feature)) {
               fp++;
            }
         }
      }

      return new ProbabilityRatio(((double)tp / classCount), ((double)fp / nonClassCount));
   }

   public String toString() {
      return String.format("%s{super=%s}",
                           getClass().getCanonicalName(),
                           super.toString());
   }

   private static class ProbabilityRatio {
      public double tpr;
      public double fpr;

      public ProbabilityRatio(double tpr, double fpr) {
         this.fpr = fpr;
         this.tpr = tpr;
      }
   }

   private static class FeatureValue implements Comparable<FeatureValue> {
      public String name;
      public double value;

      public FeatureValue(String name, double value) {
         this.name = name;
         this.value = value;
      }

      public int compareTo(FeatureValue other) {
         if (value > other.value) {
            return -1;
         } else if (value < other.value) {
            return 1;
         } else {
            return name.compareTo(other.name) * -1;
         }
      }
   }
}
