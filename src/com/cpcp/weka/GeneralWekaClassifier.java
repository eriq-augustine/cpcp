package com.cpcp.weka;

import com.cpcp.TextClassifier;
import com.cpcp.ClassificationResult;
import com.cpcp.features.FeatureSetGenerator;
import com.cpcp.filter.TextFilter;

import org.apache.commons.lang.StringUtils;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The base for different types of classifiers.
 * This Classifier will be build around a WEKA classifier.
 * Some WEKA classifiers to try:
 *  - weka.classifiers.bayes.NaiveBayes
 *  - weka.classifiers.bayes.net.BayesNetGenerator
 *  - weka.classifiers.functions.SMO (SVM)
 *  - weka.classifiers.lazy.IBk (KNN)
 *  - weka.classifiers.trees.J48
 */
public class GeneralWekaClassifier extends TextClassifier {
   /**
    * The WEKA classifier to use make copies for specific classifiers.
    */
   private Classifier untrainedClassifier;

   /**
    * The filter to run the documents through before classifying them.
    */
   private StringToWordVector stringFilter;

   private Classifier activeClassifier;

   /**
    * Construct a GeneralClassifier.
    *
    * @param classy An UNTRAINED Classifier.
    *  This classifier should have all its desired options set.
    */
   public GeneralWekaClassifier(Classifier classy,
                                FeatureSetGenerator fsg,
                                List<String> possibleClasses) {
      super(possibleClasses, fsg);
      untrainedClassifier = classy;

      activeClassifier = null;
      stringFilter = null;
   }

   /**
    * @inheritDoc
    */
   public void train(List<String> contents, List<String> classes) {
      Instances trainSet = prepTrainingSet(contents, classes);
      buildClassifier(trainSet);
   }

   public ClassificationResult classify(String document) {
      List<String> documents = new ArrayList<String>(1);
      documents.add(document);

      List<ClassificationResult> classes = classify(documents);
      return classes.get(0);
   }

   /**
    * @override
    * Need to override because it is more efficient for WEKA classifiers
    * to do groups at a time.
    */
   public List<ClassificationResult> classify(List<String> contents) {
      assert(activeClassifier != null);

      Instances unclassed = prepUnclassed(contents);

      List<ClassificationResult> rtn = new ArrayList<ClassificationResult>();

      for (int ndx = 0; ndx < unclassed.numInstances(); ndx++) {
         rtn.add(classifyInstance(unclassed.instance(ndx)));
      }

      return rtn;
   }

   /**
    * First try to get the class distribution, if it fails just try to do normal classification.
    */
   private ClassificationResult classifyInstance(Instance instance) {
      try {
         double[] classDistribution = activeClassifier.distributionForInstance(instance);

         double maxValue = -1;
         int maxIndex = 0;
         for (int i = 0; i < classDistribution.length; i++) {
            if (classDistribution[i] > maxValue) {
               maxValue = classDistribution[i];
               maxIndex = i;
            }
         }

         return new ClassificationResult(instance.classAttribute().value(maxIndex), maxValue);
      } catch (Exception distributionEx) {
         try {
            int prediction = (int)activeClassifier.classifyInstance(instance);
            return new ClassificationResult(instance.classAttribute().value(prediction), -1);
         } catch (Exception ex) {
            return new ClassificationResult(null, -1);
         }
      }
   }

   /**
    * Jump through all the WEKA hoops to get the training set ready.
    */
   private Instances prepTrainingSet(List<String> contents, List<String> classes) {
      ArrayList<Attribute> features = getWekaFeatures();

      Instances trainSet = new Instances("ClassTrainingSet", features, classes.size());
      trainSet.setClassIndex(0);

      List<Set<String>> featureSets = fsg.parseFeatures(contents);
      List<String> newContents = new ArrayList<String>();
      for (Set<String> contentFeatures : featureSets) {
         newContents.add(StringUtils.join(contentFeatures, " "));
      }

      // Add the classes to the training set.
      for (int ndx = 0; ndx < classes.size(); ndx++) {
         Instance inst = new DenseInstance(2);
         inst.setValue(features.get(0), classes.get(ndx).toString());
         inst.setValue(features.get(1), newContents.get(ndx));

         trainSet.add(inst);
      }

      stringFilter = makeStringToWordVectorFilter(trainSet);

      try {
         trainSet = weka.filters.Filter.useFilter(trainSet, stringFilter);
         trainSet.setClassIndex(0);
      } catch (Exception ex) {
         // TODO(eriq): Real logging
         System.err.println("Unable to apply classification filter." + ex);
      }

      return trainSet;
   }

   /**
    * Make a copy of the untrained classifier, train it, and make it the active classifier.
    */
   private void buildClassifier(Instances trainSet) {
      // Note(eriq): I hope WEKA does not leak this.
      activeClassifier = null;

      try {
         activeClassifier = AbstractClassifier.makeCopy(untrainedClassifier);
      } catch (Exception ex) {
         // TODO(eriq): Real logging, fatal.
         System.err.println("Unable to make a classifier copy." + ex);
      }

      // Train the classifier
      try {
         activeClassifier.buildClassifier(trainSet);
      } catch (Exception ex) {
         // TODO(eriq): Real logging, fatal.
         System.err.println("Unable to train classifier." + ex);
      }
   }

   /**
    * Create a StringToWordVector filter.
    * If there is a problem creating the filter, a fatal error will be logged and
    *  the program terminated.
    *
    * @param inputFormat The training set to use as the input format to the filter.
    *
    * @return A filter to use for classification.
    */
   private StringToWordVector makeStringToWordVectorFilter(Instances inputFormat) {
      StringToWordVector newFilter = null;

      //Create the filter to use.
      try {
         //The options for the filter.
         // "-L" - Convert words to lovercase.
         String[] options = {"-L", "-O", "-R", "2"};
         newFilter = new StringToWordVector();

         newFilter.setInputFormat(inputFormat);
         newFilter.setOptions(options);
      } catch (Exception ex) {
         // TODO(eriq): Real logging, fatal.
         System.err.println("Unable to create classification filter." + ex);
      }

      return newFilter;
   }

   /**
    * Get the features for the trainning set Instances.
    *
    * @return The features to use for the training set.
    */
   private ArrayList<Attribute> getWekaFeatures() {
      // WEKA needs a null ref to list to have a text (String) attirbute.
      ArrayList<String> nullList = null;

      Attribute classAttr = new Attribute("document_class", possibleClasses);

      // TEXT attribute
      Attribute textAttr = new Attribute("text", nullList);

      // Features
      ArrayList<Attribute> features = new ArrayList<Attribute>();
      features.add(classAttr);
      features.add(textAttr);

      return features;
   }

   /**
    * Prepare a list of document contents for classification.
    * Prep work includes getting the features for the classifier,
    *  setting the class index, filtering the contents,
    *  loading the contents into Instances and
    *  filtering the instances.
    */
   private Instances prepUnclassed(List<String> contents) {
      ArrayList<Attribute> newFeatures = getWekaFeatures();

      Instances unclassed = new Instances("UnclassifiedTweets", newFeatures, contents.size());
      unclassed.setClassIndex(0);

      List<String> newContents = new ArrayList<String>();
      List<Set<String>> featureSets = fsg.parseFeatures(contents);
      for (Set<String> features : featureSets) {
         newContents.add(StringUtils.join(features, " "));
      }

      for (String content : newContents) {
         Instance inst = new DenseInstance(2);
         inst.setValue(newFeatures.get(1), content);

         unclassed.add(inst);
      }

      // Filter the instance
      try {
         unclassed = weka.filters.Filter.useFilter(unclassed, stringFilter);
      } catch (Exception ex) {
         // TODO(eriq): Real logging, error.
         System.err.println("Unable to filter unclassified documents." + ex);
         return null;
      }

      return unclassed;
   }
}
