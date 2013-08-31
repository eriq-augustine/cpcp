package com.cpcp.features;

import com.cpcp.document.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A general interface for things that can generate feature sets.
 * The burden of understanding how to work with documents is placed on
 * subclasses of this class.
 */
public abstract class FeatureSetGenerator<E extends Document> {
   /**
    * Build the entire feature set given a training set.
    * This is necessary for classifiers that require the entire feature
    * space up-front during training.
    */
   public abstract Set<String> getFeatureSpace(List<E> documents, List<String> classes);

   /**
    * Parse the features out of a document.
    */
   public abstract Set<String> parseFeatures(E document);

   public List<Set<String>> parseFeatures(List<E> documents) {
      List<Set<String>> rtn = new ArrayList<Set<String>>();

      for (E document : documents) {
         rtn.add(parseFeatures(document));
      }

      return rtn;
   }

   public String toString() {
      return getClass().getCanonicalName();
   }
}
