package com.cpcp;

import com.cpcp.document.Document;
import com.cpcp.features.FeatureSetGenerator;
import com.cpcp.features.NGram;

import java.util.ArrayList;
import java.util.List;

/**
 * Base classifier functionality for a Cal PolyClassification Package classifier.
 */
public abstract class CPCPClassifier<E extends Document> {
   /**
    * The possible possibleClasses for documents.
    */
   protected List<String> possibleClasses;

   protected FeatureSetGenerator<E> fsg;

   public CPCPClassifier(List<String> possibleClasses, FeatureSetGenerator<E> fsg) {
      this.possibleClasses = new ArrayList<String>(possibleClasses);
      this.fsg = fsg;
   }

   /**
    * Classify many documents.
    */
   public List<String> classifyToString(List<E> documents) {
      List<String> rtn = new ArrayList<String>(documents.size());
      List<ClassificationResult> results = classify(documents);

      for (ClassificationResult result : results) {
         rtn.add(result.getClassValue());
      }

      return rtn;
   }

   public List<ClassificationResult> classify(List<E> documents) {
      List<ClassificationResult> results = new ArrayList<ClassificationResult>(documents.size());

      for (E document : documents) {
         results.add(classify(document));
      }

      return results;
   }

   /**
    * Classify a single document.
    */
   public String classifyToString(E document) {
      return classify(document).getClassValue();
   }

   /**
    * The real implementation for classification.
    * If the classifier does not supprt confidences, then just put -1 as the confidence.
    */
   public abstract ClassificationResult classify(E document);

   /**
    * Train the classifier with the given training set, and use the given
    *  feature set reducer to reduce the feature space.
    * This can also be called to retrain the classifier.
    *
    * @param documents The documents in the training set.
    * @param classes The classes of the documents in the training set.
    *
    * @.pre documents.size() == classes.size().
    */
   public abstract void train(List<E> documents, List<String> classes);
}
