package com.cpcp.evaluation;

import com.cpcp.TextClassifier;
import com.cpcp.features.FeatureSetGenerator;

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

      loadTrainingSet("data/trainingData.gsv", "`", contents, classes);

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

   //TEST
   public static void loadTrainingSet(String path,
                                      String seperator,
                                      List<String> contents,
                                      List<String> classes) throws Exception {
      // TODO
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

      if (numFolds < 1) {
         return new Results(new int[0][0], (System.currentTimeMillis() - timestamp), classValues);
      }

      // TODO(eriq): When using general confusion matrix, this is unnecessary.
      Map<String, Integer> indexMap = new HashMap<String, Integer>();
      for (int i = 0; i < classValues.length; i++) {
         indexMap.put(classValues[i], new Integer(i));
      }

      int[][] confusionMatrix = new int[size][size];

      for (int row = 0; row < size; row++) {
         for (int col = 0; col < size; col++) {
            confusionMatrix[row][col] = 0;
         }
      }

      List<List> toSplit = new ArrayList<List>();
      toSplit.add(classes);
      toSplit.add(contents);

      List<List<List>> splittedList = new ArrayList<List<List>>();

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
         List<String> predictions = classy.classify(splitContents.get(foldNdx));

         // For all the results.
         for (int stringNdx = 0; stringNdx < predictions.size(); stringNdx++) {
            // Put the result in the confusion matrix
            int actualNdx = indexMap.get(splitClasses.get(foldNdx).get(stringNdx)).intValue();
            int predNdx = indexMap.get(predictions.get(stringNdx)).intValue();

            confusionMatrix[actualNdx][predNdx]++;
         }
      }

      return new Results(confusionMatrix, (System.currentTimeMillis() - timestamp), classValues);
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
   protected static void splitList(List<List> inputLists, List<List<List>> outputLists,
                                   int numFolds, long seed) {
      // Get a new Random
      Random rand = new Random(seed);

      //Empty the output lists
      outputLists.clear();

      //FOR every input list
      for (int numLists = 0; numLists < inputLists.size(); numLists++) {
         List<List> newList = new ArrayList<List>();

         //create all the lists
         for (int ndx = 0; ndx < numFolds; ndx++) {
            newList.add(new ArrayList());
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
      private int[][] confusionMatrix;

      /**
       * The number of misses the classifier had.
       */
      private int misses;

      /**
       * The number of correct classifications (hits) that the classifier had.
       */
      private int hits;

      /**
       * The root mean squared error for this evaluation.
       */
      private double rmsError;

      /**
       * The time it took in ms for the Evaluation to run.
       */
      private long time;

      // TODO(eriq): This becomes unecessary when using general confusion matrix
      private String[] classValues;

      /**
       * Constructor for the Results.
       * Simple calculations (misses, hits, rmsError) are done here.
       */
      public Results(int[][] confusionMatrix, long time, String[] classValues) {
         this.time = time;
         this.confusionMatrix = confusionMatrix;
         this.classValues = classValues;
         misses = 0;
         hits = 0;

         for (int i = 0; i < confusionMatrix.length; i++) {
            for (int j = 0; j < confusionMatrix[i].length; j++) {
               if (i == j) {
                  hits += confusionMatrix[i][j];
               } else {
                  misses += confusionMatrix[i][j];
               }
            }
         }

         if (misses + hits > 0) {
            rmsError = Math.sqrt((misses ^ 2) / (double)(misses + hits));
         } else {
            rmsError = 0;
         }
      }

      /**
       * Get the amount of time that the evaluation took.
       */
      public long getTime() {
         return time;
      }

      /**
       * Get the number of misses.
       */
      public int misses() {
         return misses;
      }

      /**
       * Get the number of hits.
       */
      public int hits() {
         return hits;
      }

      /**
       * Get the root mean squared error for this evaluation.
       */
      public double rmsError() {
         return rmsError;
      }

      /**
       * Get the confusion matrix for this evaluation.
       */
      public int[][] confusionMatrix() {
         return confusionMatrix;
      }

      /**
       * Return a string with the number of hits, misses, confusion matrix, and.
       */
      public String toString() {
         String rtn = "";

         //Put in the hits and misses
         rtn += String.format("Hits: %d, Misses: %d\n\n", hits, misses);

         //Column headers
         rtn += String.format("%15s|", "");
         for (String classVal : classValues) {
            rtn += String.format(String.format("%%%ds|", classVal.length()), classVal);
         }
         rtn += "\n";

         for (int i = 0; i < confusionMatrix.length; i++) {
            rtn += String.format("%15s|", classValues[i]);

            for (int j = 0; j < confusionMatrix[i].length; j++) {
               rtn += String.format(String.format("%%%dd|", classValues[j].length()),
                                    confusionMatrix[i][j]);
            }
            rtn += "\n";
         }

         rtn += ("\nError Rate: " + rmsError + "\n");
         rtn += ("Runtime: " + time + "\n");

         return rtn;
      }
   }
}
