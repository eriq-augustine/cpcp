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
   public List<String> classifyToString(List<String> contents) {
      List<String> rtn = new ArrayList<String>(contents.size());
      List<ClassificationResult> results = classify(contents);

      for (ClassificationResult result : results) {
         rtn.add(result.getClassValue());
      }

      return rtn;
   }

   public List<ClassificationResult> classify(List<String> contents) {
      List<ClassificationResult> results =
         new ArrayList<ClassificationResult>(contents.size());

      for (String content : contents) {
         results.add(classify(content));
      }

      return results;
   }

   /**
    * Classify a single document.
    */
   public String classifyToString(String content) {
      return classify(content).getClassValue();
   }

   /**
    * The real implementation for classification.
    * If the classifier does not supprt confidences, then just put -1 in the result.
    */
   public abstract ClassificationResult classify(String document);

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
