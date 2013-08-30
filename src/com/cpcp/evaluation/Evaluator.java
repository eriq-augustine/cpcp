package com.cpcp.evaluation;

import com.cpcp.TextClassifier;
import com.cpcp.ClassificationResult;
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
      List<String> contents = new ArrayList<String>();
      List<String> classes = new ArrayList<String>();

      loadTrainingSet("data/classifierTrainingTweetData.gsv", "`", contents, classes);
      // loadTrainingSet("data/classifierTrainingTweetDataCompressed.gsv", "`", contents, classes);

      Set<String> classValueSet = getClassValues(classes);
      String[] classValues = classValueSet.toArray(new String[0]);

      List<String> possibleClasses = new ArrayList<String>(classValues.length);
      for (String classValue : classValues) {
         possibleClasses.add(classValue);
      }

      TextClassifier classy = new com.cpcp.weka.GeneralWekaClassifier(
         new weka.classifiers.bayes.NaiveBayes(),
         new com.cpcp.features.NGram(1, 1, new com.cpcp.filter.FullFilter()),
         possibleClasses);

      Results res = crossValidate(classy, contents, classes, 10, 4);
      System.out.println(res);
   }

   /**
    * For testing only, load a training set from a file.
    * It is assumed that there is one document per line in the form:
    *  <class><seperator><document>
    */
   private static void loadTrainingSet(String path,
                                       String seperator,
                                       List<String> contents,
                                       List<String> classes) throws Exception {
      Scanner fileScanner = new Scanner(new File(path));
      while (fileScanner.hasNextLine()) {
         String line = fileScanner.nextLine();
         String[] parts = line.split(seperator);

         classes.add(parts[0]);
         contents.add(parts[1]);
      }
   }

   private static Set<String> getClassValues(List<String> classes) {
      Set<String> rtn = new HashSet<String>();

      for (String classValue : classes) {
         rtn.add(classValue);
      }

      return rtn;
   }

   public static Results crossValidate(TextClassifier classy,
                                       List<String> contents, List<String> classes,
                                       int numFolds, long seed) {
      long timestamp = System.currentTimeMillis();

      Set<String> classValueSet = getClassValues(classes);
      String[] classValues = classValueSet.toArray(new String[0]);
      int size = classValues.length;

      GeneralConfusionMatrix confusionMatrix = new GeneralConfusionMatrix(classValues);

      if (numFolds < 1) {
         return new Results(confusionMatrix, (System.currentTimeMillis() - timestamp));
      }

      List<List<String>> toSplit = new ArrayList<List<String>>();
      toSplit.add(classes);
      toSplit.add(contents);

      List<List<List<String>>> splittedList = new ArrayList<List<List<String>>>();

      //Split the training set into the number of folds.
      splitList(toSplit, splittedList, numFolds, seed);

      Object trickCompiler;

      trickCompiler = splittedList.get(0);
      @SuppressWarnings("unchecked")
      List<List<String>> splitClasses = (List<List<String>>)trickCompiler;

      trickCompiler = splittedList.get(1);
      @SuppressWarnings("unchecked")
      List<List<String>> splitContents = (List<List<String>>)trickCompiler;

      // For the number of folds.
      for (int foldNdx = 0; foldNdx < numFolds; foldNdx++) {
         // Make an empty training set.
         List<String> trainClasses = new ArrayList<String>();
         List<String> trainContents = new ArrayList<String>();

         // For all folds.
         for (int ndx = 0; ndx < numFolds; ndx++) {
            // If it is not the current fold.
            if (ndx != foldNdx) {
               // Add to the training set.
               trainClasses.addAll(splitClasses.get(ndx));
               trainContents.addAll(splitContents.get(ndx));
            }
         }

         // Retrain the classifier on the new training set.
         classy.train(trainContents, trainClasses);

         // Classify the current fold.
         List<ClassificationResult> predictions = classy.classify(splitContents.get(foldNdx));

         // For all the results.
         for (int stringNdx = 0; stringNdx < predictions.size(); stringNdx++) {
            confusionMatrix.add(predictions.get(stringNdx).getClassValue(),
                                splitClasses.get(foldNdx).get(stringNdx));
         }
      }

      return new Results(confusionMatrix, (System.currentTimeMillis() - timestamp));
   }

   /**
    * Split the input data into the given number of folds.
    * The given lists will be destroyed in the process.
    * The price that is paid for this method being so general is that
    *  taking the lists out of outputLists generates unckecked exceptions.
    *
    * @param inputLists All the lists to split
    * @param outputLists Split-up version of the given lists
    * @param numFolds The number of partitions for the list.
    * @param seed The seed to use for partitioning the lists.
    *
    * @.pre Every list in inputLists should be the same size.
    */
   protected static void splitList(List<List<String>> inputLists,
                                   List<List<List<String>>> outputLists,
                                   int numFolds, long seed) {
      // Get a new Random
      Random rand = new Random(seed);

      //Empty the output lists
      outputLists.clear();

      //FOR every input list
      for (int numLists = 0; numLists < inputLists.size(); numLists++) {
         List<List<String>> newList = new ArrayList<List<String>>();

         //create all the lists
         for (int ndx = 0; ndx < numFolds; ndx++) {
            newList.add(new ArrayList<String>());
         }

         outputLists.add(newList);
      }

      //Start at the first sublist
      int currentList = 0;

      //WHILE there are still elements in the list
      while (inputLists.get(0).size() > 0) {
         //Remove a random element from the main list and put it in
         // the current sublist
         int randInt = rand.nextInt(inputLists.get(0).size());

         for (int numList = 0; numList < inputLists.size(); numList++) {
            //Catch the result of the add so that only this line can be suppressed.
            @SuppressWarnings("unchecked")
            boolean res =
             outputLists.get(numList).get(currentList).add(inputLists.get(numList).remove(randInt));
         }

         //Move to the next sublist
         currentList = (currentList + 1) % numFolds;
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
