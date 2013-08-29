package com.cpcp.util.math;

import java.util.LinkedList;
import java.util.Queue;

/**
 * WindowStdDevs are like WindowStdDevs, except they have a set window
 *  size.
 * Because of the need to drop elements from the StdDev as they fall out of
 *  the window, much more data needs to be maintined than a normal WindowStdDev.
 * This method is based off of the method proposed by David Knuth in
 *  "The Art of Computer Programming" volume 2.
 * On an algorithmic note: Instead of keepiong the addition to the partial variance
 *  in the window to later subtract it out. I just use:
 *   ((removeVal - mean) * (removeVal - newMean))
 *  This seems to work better (by a lot).
 *  I have no mathematical backing for this, only experimental.
 */
public class WindowStdDev {
   /**
    * The number of elements currently analyzed.
    * Eventually, this should stay equal to FRAME_SIZE.
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

   /**
    * All the values that are currently in the window.
    */
   private Queue<Double> window;

   /**
    * The size of the window.
    */
   private int windowSize;

   public WindowStdDev(int windowSize) {
      this.windowSize = windowSize;
      window = new LinkedList<Double>();

      count = 0;
      mean = 0;
      partialVariance = 0;
   }

   public WindowStdDev(int windowSize, int count, double mean, double partialVariance,
                       Queue<Double> window) {
      this.windowSize = windowSize;
      this.window = new LinkedList<Double>();

      for (Double windowVal : window) {
         this.window.add(new Double(windowVal));
      }

      this.count = count;
      this.mean = mean;
      this.partialVariance = partialVariance;
   }

   public WindowStdDev(WindowStdDev copy) {
      windowSize = copy.windowSize;
      count = copy.count;
      mean = copy.mean;
      partialVariance = copy.partialVariance;

      window = new LinkedList<Double>();
      for (Double windowVal : copy.window) {
         window.add(new Double(windowVal));
      }
   }

   /**
    * Abandon anything that is currently in the window, and fill
    *  the window with the specified value.
    */
   public void fill(double value) {
      count = 0;
      mean = 0;
      partialVariance = 0;
      window.clear();

      for (int i = 0; i < windowSize; i++) {
         addValue(value);
      }
   }

   /**
    * Add a value to the running standard deviation.
    *
    * @param value The value to add.
    *
    * @return The old value that was pushed out of the window or
    *  Double.NaN if no value was removed (count lt windowSize).
    */
   public double addValue(double value) {
      if (count == 0) {
         window.add(new Double(value));
         mean = value;
         partialVariance = 0;
         count++;

         return Double.NaN;
      } else if (count < windowSize) {
         double newMean = ((mean * count) + value) / (count + 1);
         double pvAdd = (value - mean) * (value - newMean);
         partialVariance += pvAdd;

         mean = newMean;
         count++;
         window.add(new Double(value));

         return Double.NaN;
      } else {
         double removeVal = window.remove().doubleValue();

         double newMean = ((mean * count) - removeVal + value) / count;
         double pvAdd = (value - mean) * (value - newMean);
         partialVariance = partialVariance + pvAdd - ((removeVal - mean) * (removeVal - newMean));

         mean = newMean;
         window.add(new Double(value));

         return removeVal;
      }
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
      if (count - 1 <= 0) {
         return 0;
      }

      return partialVariance / (count - 1);
   }

   /**
    * Get the current partial variance.
    * This value, not the varience, is necessary to build a new
    *  WindowStdDev.
    */
   public double getPartialVariance() {
      return partialVariance;
   }

   /**
    * Get the current standard deviation.
    */
   public double getStdDev() {
      if (count - 1 <= 0) {
         return 0;
      }

      return Math.sqrt(getVariance());
   }

   /**
    * Get the window size.
    */
   public int getWindowSize() {
      return windowSize;
   }
}
