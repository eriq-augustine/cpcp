package com.cpcp.evaluation;

import com.cpcp.CPCPClassifier;
import com.cpcp.ClassificationResult;
import com.cpcp.document.Document;
import com.cpcp.document.TextDocument;
import com.cpcp.features.FeatureSetGenerator;
import com.cpcp.util.math.GeneralConfusionMatrix;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * A Class for running evaluations Classifiers.
 * For just a standard evaluation, look at GeneralEvaluator.
 */
public class Evaluator {
   public static void main(String[] args) throws Exception {
      List<TextDocument> documents = new ArrayList<TextDocument>();
      List<String> classes = new ArrayList<String>();

      loadTrainingSet("data/classifierTrainingTweetData.gsv", "`", documents, classes);
      // loadTrainingSet("data/classifierTrainingTweetDataCompressed.gsv", "`",
      //                 documents, classes);

      Set<String> classValueSet = getClassValues(classes);
      String[] classValues = classValueSet.toArray(new String[0]);

      List<String> possibleClasses = new ArrayList<String>(classValues.length);
      for (String classValue : classValues) {
         possibleClasses.add(classValue);
      }

      CPCPClassifier<TextDocument> classy = new com.cpcp.weka.GeneralWekaClassifier<TextDocument>(
         new weka.classifiers.bayes.NaiveBayes(),
         new com.cpcp.features.NGram(1, 1, new com.cpcp.filter.FullFilter()),
         possibleClasses);

      Results res = crossValidate(classy, documents, classes, 10, 4);
      System.out.println(res);
   }

   /**
    * For testing only, load a training set from a file.
    * It is assumed that there is one document per line in the form:
    *  <class><seperator><document>
    */
   private static void loadTrainingSet(String path,
                                       String seperator,
                                       List<TextDocument> documents,
                                       List<String> classes) throws Exception {
      Scanner fileScanner = new Scanner(new File(path));
      while (fileScanner.hasNextLine()) {
         String line = fileScanner.nextLine();
         String[] parts = line.split(seperator);

         classes.add(parts[0]);
         documents.add(new TextDocument(parts[1]));
      }
   }

   private static Set<String> getClassValues(List<String> classes) {
      Set<String> rtn = new HashSet<String>();

      for (String classValue : classes) {
         rtn.add(classValue);
      }

      return rtn;
   }

   public static <E extends Document> Results crossValidate(
         CPCPClassifier<E> classy,
         List<E> documents, List<String> classes,
         int numFolds, long seed) {
      long timestamp = System.currentTimeMillis();

      Set<String> classValueSet = getClassValues(classes);
      String[] classValues = classValueSet.toArray(new String[0]);
      int size = classValues.length;

      GeneralConfusionMatrix confusionMatrix = new GeneralConfusionMatrix(classValues);

      if (numFolds < 1) {
         return new Results(confusionMatrix, (System.currentTimeMillis() - timestamp));
      }

      List<TrainingDocument> trainingDocuments = TrainingDocument.buildList(documents, classes);

      // Split the training set into the number of folds.
      List<List<TrainingDocument>> splitList = splitList(trainingDocuments, numFolds, seed);

      for (int foldNdx = 0; foldNdx < numFolds; foldNdx++) {
         List<String> trainClasses = new ArrayList<String>();
         List<E> trainDocuments = new ArrayList<E>();

         List<String> actualClasses = new ArrayList<String>();
         List<E> toClassify = new ArrayList<E>();

         // For all folds.
         for (int ndx = 0; ndx < numFolds; ndx++) {
            for (TrainingDocument trainDocument : splitList.get(ndx)) {
               // If it is not the current fold.
               if (ndx != foldNdx) {
                  // Add to the training set.
                  trainClasses.add(trainDocument.classValue);
                  @SuppressWarnings("unchecked")
                  E suppressWarningDocument = (E)trainDocument.document;
                  trainDocuments.add(suppressWarningDocument);
               } else {
                  // Add to the classify set.
                  actualClasses.add(trainDocument.classValue);
                  @SuppressWarnings("unchecked")
                  E suppressWarningDocument = (E)trainDocument.document;
                  toClassify.add(suppressWarningDocument);
               }
            }
         }

         // Retrain the classifier on the new training set.
         classy.train(trainDocuments, trainClasses);

         // Classify the current fold.
         List<ClassificationResult> predictions = classy.classify(toClassify);

         for (int stringNdx = 0; stringNdx < predictions.size(); stringNdx++) {
            confusionMatrix.add(predictions.get(stringNdx).getClassValue(),
                                actualClasses.get(stringNdx));
         }
      }

      return new Results(confusionMatrix, (System.currentTimeMillis() - timestamp));
   }

   /**
    * Split the input data into the given number of folds.
    * The given list will be destroyed in the process.
    *
    * @param inputLists All the lists to split
    * @param numFolds The number of partitions for the list.
    * @param seed The seed to use for partitioning the lists.
    */
   private static <E extends TrainingDocument> List<List<E>> splitList(List<E> input,
                                                                       int numFolds,
                                                                       long seed) {
      // Get a new Random
      Random rand = new Random(seed);

      List<List<E>> outputLists = new ArrayList<List<E>>();
      for (int i = 0; i < numFolds; i++) {
         outputLists.add(new ArrayList<E>());
      }

      int currentList = 0;

      // While there are still elements in the input list.
      while (input.size() > 0) {
         //Remove a random element from the main list and put it in the current sublist
         int randInt = rand.nextInt(input.size());
         outputLists.get(currentList).add(input.remove(randInt));

         currentList = (currentList + 1) % numFolds;
      }

      return outputLists;
   }

   /**
    * A wrapper for a document and class value together.
    */
   public static class TrainingDocument {
      public Document document;
      public String classValue;

      public TrainingDocument(Document document, String classValue) {
         this.document = document;
         this.classValue = classValue;
      }

      public static List<TrainingDocument> buildList(
            List<? extends Document> documents,
            List<String> classes) {
         List<TrainingDocument> rtn = new ArrayList<TrainingDocument>();

         for (int i = 0; i < documents.size(); i++) {
            rtn.add(new TrainingDocument(documents.get(i), classes.get(i)));
         }

         return rtn;
      }
   }

   /**
    * A container for all of the results of an evaluation.
    */
   public static class Results {
      /**
       * The confusion matrix for this evaluation.
       */
      private GeneralConfusionMatrix confusionMatrix;

      /**
       * The time it took in ms for the Evaluation to run.
       */
      private long time;

      /**
       * Constructor for the Results.
       */
      public Results(GeneralConfusionMatrix confusionMatrix, long time) {
         this.time = time;
         this.confusionMatrix = confusionMatrix;
      }

      /**
       * Get the amount of time that the evaluation took.
       */
      public long getTime() {
         return time;
      }

      /**
       * Get the confusion matrix for this evaluation.
       */
      public GeneralConfusionMatrix confusionMatrix() {
         return confusionMatrix;
      }

      /**
       * Return a string with a summary of the evaluation.
       */
      public String toString() {
         String rtn = "";

         rtn += confusionMatrix.fullToString() + "\n";
         rtn += "Runtime: " + time + "\n";

         return rtn;
      }
   }
}
