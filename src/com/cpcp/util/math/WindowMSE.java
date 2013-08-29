package com.cpcp.util.math;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Get the Mean Square Error bewteen a and b within a window.
 */
public class WindowMSE {
   /**
    * The number of elements currently analyzed.
    * Eventually, this should stay equal to windowSize.
    */
   private int count;

   /**
    * The current mean.
    */
   private double mean;

   /**
    * All the values that are currently in the window.
    */
   private Queue<Double> window;

   /**
    * The size of the window.
    */
   private int windowSize;

   public WindowMSE(int windowSize) {
      count = 0;
      this.windowSize = windowSize;
      window = new LinkedList<Double>();
   }

   /**
    * Add a valut to the window.
    *
    * @return The new MSE.
    */
   public double addValue(double a, double b) {
      double val = Math.pow(a - b, 2);

      if (count < windowSize) {
         mean = (mean * count + val) / (count + 1);
         window.add(new Double(val));
         count++;
      } else {
         double removeVal = window.remove().doubleValue();

         mean = (mean * windowSize - removeVal + val) / windowSize;
         window.add(new Double(val));
      }

      return mean;
   }

   public double getMSE() {
      return mean;
   }
}
