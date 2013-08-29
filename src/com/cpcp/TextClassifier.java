package com.cpcp;

import com.cpcp.features.FeatureSetGenerator;
import com.cpcp.features.NGram;

import java.util.ArrayList;
import java.util.List;

/**
 * A classifier that can classify text.
 */
public abstract class TextClassifier {
   /**
    * The possible possibleClasses for documents.
    */
   protected List<String> possibleClasses;

   protected FeatureSetGenerator fsg;

   public TextClassifier(List<String> possibleClasses, FeatureSetGenerator fsg) {
      this.possibleClasses = new ArrayList<String>(possibleClasses);
      this.fsg = fsg;
   }

   /**
    * Classify many documents.
    */
   public List<String> classify(List<String> contents) {
      List<String> results = new ArrayList<String>(contents.size());

      for (String content : contents) {
         results.add(classify(content));
      }

      return results;
   }

   /**
    * Classify a single document.
    */
   public abstract String classify(String content);

   /**
    * Train the classifier with the given training set, and use the given
    *  feature set reducer to reduce the feature space.
    * This can also be called to retrain the classifier.
    *
    * @param contents The documents in the training set.
    * @param classes The classes of the documents in the training set.
    *
    * @.pre contents.size() == classes.size().
    */
   public abstract void train(List<String> contents, List<String> classes);
}
