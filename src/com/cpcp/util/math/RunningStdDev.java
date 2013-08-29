package com.cpcp.util.math;

/**
 * A class to keep track of a running standard deviation.
 * This method is based off of the method proposed by David Knuth in
 *  "The Art of Computer Programming" volume 2.
 */
public class RunningStdDev {
   /**
    * The number of elements currently analyzed.
    */
   private int count;

   /**
    * The current mean.
    */
   private double mean;

   /**
    * The current partial variance.
    */
   private double partialVariance;

   public RunningStdDev() {
      count = 0;
      mean = 0;
      partialVariance = 0;
   }

   /**
    * Start with a single value.
    */
   public RunningStdDev(double initialValue) {
      count = 1;
      mean = initialValue;
      partialVariance = 0;
   }

   public RunningStdDev(int count, double mean, double partialVariance) {
      this.count = count;
      this.mean = mean;
      this.partialVariance = partialVariance;
   }

   /**
    * Add a value to the running standard deviation.
    *
    * @param value The value to add.
    *
    * @return The new standard deviation.
    */
   public double addValue(double value) {
      if (count == 0) {
         count++;
         mean = value;
         partialVariance = 0;
      } else {
         double newMean = ((mean * count) + value) / (count + 1);

         partialVariance = partialVariance + (value - mean) * (value - newMean);
         mean = newMean;
         count++;
      }

      return getStdDev();
   }

   /**
    * Get the number of elements that have been analyzed.
    */
   public int getCount() {
      return count;
   }

   /**
    * Get the current mean.
    */
   public double getMean() {
      return mean;
   }

   /**
    * Get the current variance.
    */
   public double getVariance() {
      int denominator = count - 1;
      if (denominator <= 0) {
         return partialVariance;
      }

      return partialVariance / denominator;
   }

   /**
    * Get the current partial variance.
    * This value, not the variance, is necessary to build a new
    *  RunningStdDev.
    */
   public double getPartialVariance() {
      return partialVariance;
   }

   /**
    * Get the current standard deviation.
    */
   public double getStdDev() {
      if ((count - 1) <= 0) {
         return 0;
      }

      return Math.sqrt(partialVariance / (count - 1));
   }
}
