package com.cpcp.classify;

import com.cpcp.features.FeatureSetGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * A classifier that can classify text.
 */
public abstract class TextClassifier {
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
    * Train the classifier with the given training set.
    * This can also be called to retrain the classifier.
    *
    * @param contents The documents in the training set.
    * @param classes The classes of the documents in the training set.
    *
    * @.pre contents.size() == classes.size().
    */
   public void train(List<String> contents, List<String> classes) {
      assert(contents.size() == classes.size());

      //TEST
      //train(contents, classes, new Unigram
   }

   /**
    * Train the classifier with the given training set, and use the given
    *  feature set reducer to reduce the feature space.
    * This can also be called to retrain the classifier.
    *
    * @param contents The documents in the training set.
    * @param classes The classes of the documents in the training set.
    * @param generator The FeatureSetGenerator to use.
    *
    * @.pre contents.size() == classes.size().
    */
   public abstract void train(List<String> contents, List<String> classes,
                              FeatureSetGenerator generator);
}
