package com.cpcp.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A general interface for things that can generate feature sets.
 *
 * This interface is only set up to deal with text classifiers.
 */
public abstract class FeatureSetGenerator {
   /**
    * Build the entire feature set given a training set.
    * This is necessary for classifiers that require the entire feature
    * space up-front during training.
    */
   public abstract Set<String> getFeatureSpace(List<String> documents, List<String> classes);

   /**
    * Parse the features out of a document.
    */
   public abstract Set<String> parseFeatures(String document);

   public List<Set<String>> parseFeatures(List<String> documents) {
      List<Set<String>> rtn = new ArrayList<Set<String>>();

      for (String document : documents) {
         rtn.add(parseFeatures(document));
      }

      return rtn;
   }

   public String toString() {
      return getClass().getCanonicalName();
   }
}
