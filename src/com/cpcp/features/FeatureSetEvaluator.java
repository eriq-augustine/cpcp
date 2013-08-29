package com.cpcp.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Does evaluation on a feature set (or FeatureSetGenerator) given a training set.
 */
public class FeatureSetEvaluator {
   /**
    * Evaluate a FeatureSetGenerator.
    */
   public double evaluateFeatureSet(List<String> trainingContents, List<String> trainingClasses,
                                    FeatureSetGenerator generator) {
      return evaluateFeatureSet(trainingContents, trainingClasses,
                                generator.getFeatureSpace(trainingContents, trainingClasses),
                                generator);
   }

   /**
    * This is more for testing manual feature sets.
    * It will get the features for each document, but then only pay attention to ones that are in
    *  |featureSet|.
    */
   public double evaluateFeatureSet(List<String> trainingContents, List<String> trainingClasses,
                                    Set<String> featureSet, FeatureSetGenerator generator) {
      // {feature => Set (training indexes)
      Map<String, Map<String, Integer>> counts = new HashMap<String, Map<String, Integer>>();
      // feature => total
      Map<String, Integer> totals = new HashMap<String, Integer>();

      List<Set<String>> features = generator.parseFeatures(trainingContents);

      for (int i = 0; i < trainingContents.size(); i++) {
         for (String feature : features.get(i)) {
            if (!featureSet.contains(feature)) {
               continue;
            }

            if (!counts.containsKey(feature)) {
               counts.put(feature, new HashMap<String, Integer>());
               totals.put(feature, 1);
            } else {
               totals.put(feature, totals.get(feature).intValue() + 1);
            }

            if (!counts.get(feature).containsKey(trainingClasses.get(i))) {
               counts.get(feature).put(trainingClasses.get(i), 1);
            } else {
               counts.get(feature).put(
                     trainingClasses.get(i),
                     counts.get(feature).get(trainingClasses.get(i)).intValue() + 1);
            }
         }
      }

      // Lets get some ordering up in here.
      // Highest % => string
      TreeMap<FeatureValue, String> orderedMap = new TreeMap<FeatureValue, String>();

      for (String feature : counts.keySet()) {
         String line = String.format("%-30s -- ",
                                     String.format("%s (%d / %d)", feature,
                                                   totals.get(feature), trainingContents.size()));
         double max = -1;

         for (Map.Entry<String, Integer> count : counts.get(feature).entrySet()) {
            double val = ((double)count.getValue()) / totals.get(feature);
            line += String.format("%s: %f, ", count.getKey(), val);

            if (val > max) {
               max = val;
            }
         }

         orderedMap.put(new FeatureValue(feature, max), line.replaceFirst(", $", ""));
      }

      for (String line : orderedMap.values()) {
         System.err.println(line);
      }

      return 0;
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
