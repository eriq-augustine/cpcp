package com.cpcp;

/**
 * The result of a classification.
 * Currently, just a wrapper around a confidence and class value.
 */
public class ClassificationResult {
   private final String classValue;
   private final double confidence;

   public ClassificationResult(String classValue, double confidence) {
      this.classValue = classValue;
      this.confidence = confidence;
   }

   public String getClassValue() {
      return classValue;
   }

   public double getConfidence() {
      return confidence;
   }
}
