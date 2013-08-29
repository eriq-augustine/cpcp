package com.cpcp.util.math;

import java.util.List;

/**
 * This is a general class for mathematical functions.
 * All of these functions do no fancy tricks or shortcuts.
 */
public class MathUtils {
   /**
    * Calculate the mean of a range in list of numbers.
    *
    * @param nums The numbers to get the mean of.
    * @param start The index to start taking the mean at
    * @param size The number of elements to include in the mean.
    *
    * @throws IllegalArgumentException If anything is weird (null or zero list), zero size.
    *
    * @return The mean
    */
   public static double mean(List<? extends Number> nums, int start, int size) {
      if (nums == null || nums.size() == 0 ||
          start < 0 || start >= nums.size() ||
          size <= 0 || start + size > nums.size()) {
         throw new IllegalArgumentException();
      }

      double mean = 0;

      for (int i = start; i < start + size; i++) {
         mean += nums.get(i).doubleValue();
      }

      return (mean / size);
   }

   /**
    * Calculate the mean of a list of numbers.
    *
    * @param nums The numbers to get the mean of.
    *
    * @throws IllegalArgumentException If anything is weird (null or zero list).
    *
    * @return The mean.
    */
   public static double mean(List<? extends Number> nums) {
      if (nums == null || nums.size() == 0) {
         throw new IllegalArgumentException();
      }

      return mean(nums, 0, nums.size());
   }

   /**
    * Get the standard deviation.
    */
   public static double stdDev(List<? extends Number> nums) {
      if (nums == null || nums.size() == 0) {
         throw new IllegalArgumentException();
      }

      return stdDev(nums, 0, nums.size());
   }

   /**
    * Get the standard deviation, but if you already know the mean
    *  it can go faster.
    */
   public static double stdDev(List<? extends Number> nums, double mean) {
      if (nums == null || nums.size() == 0) {
         throw new IllegalArgumentException();
      }

      return stdDev(nums, 0, nums.size(), mean);
   }

   /**
    * Get the standard deviation of a range.
    */
   public static double stdDev(List<? extends Number> nums, int start, int size) {
      if (nums == null || nums.size() == 0 ||
          start < 0 || start >= nums.size() ||
          size <= 0 || start + size > nums.size()) {
         throw new IllegalArgumentException();
      }

      double stdDev = 0;
      double mean = mean(nums, start, size);

      for (int i = start; i < start + size; i++) {
         stdDev += Math.pow(nums.get(i).doubleValue() - mean, 2);
      }

      return Math.sqrt(stdDev / size);
   }

   /**
    * Get the standard deviation or a range, but if you already know the mean
    *  it can go faster.
    */
   public static double stdDev(List<? extends Number> nums, int start, int size, double mean) {
      if (nums == null || nums.size() == 0 ||
          start < 0 || start >= nums.size() ||
          size <= 0 || start + size > nums.size()) {
         throw new IllegalArgumentException();
      }

      double stdDev = 0;

      for (int i = start; i < start + size; i++) {
         stdDev += Math.pow(nums.get(i).doubleValue() - mean, 2);
      }

      return Math.sqrt(stdDev / size);
   }

   /**
    * Calculate the pearson between two lists.
    */
   public static double pearson(List<? extends Number> a, List<? extends Number> b) {
      return pearson(a, b, 0, a.size());
   }

   /**
    * Calculate the pearson of a range between two lists.
    * Technically, a and b shouldn't have to be the same size, but I am lazy.
    *  you can fix it if you want.
    */
   public static double pearson(List<? extends Number> a, List<? extends Number> b,
                                int start, int size) {
      if (a == null || a.size() == 0 ||
          b == null || b.size() == 0 ||
          a.size() != b.size() ||
          start < 0 || start >= a.size() ||
          size <= 0 || start + size > a.size()) {
         throw new IllegalArgumentException();
      }

      double pearson = 0;

      double aMean = mean(a, start, size);
      double bMean = mean(b, start, size);
      double aStdDev = stdDev(a, start, size, aMean);
      double bStdDev = stdDev(b, start, size, bMean);

      for (int i = start; i < (start + size); i++) {
         pearson += ((a.get(i).doubleValue() - aMean) * (b.get(i).doubleValue() - bMean));
      }

      return pearson / (size * aStdDev * bStdDev);
   }

   /**
    * Calculates the Spearman correlation between the values in two Lists.
    *
    * @param a The "x-axis" List.
    * @param b The "y-axis" List.
    *
    * @throws IllegalArgumentException Throws if either parameter is null, if either list has a
    * size of zero, or if the lists are of different sizes.
    *
    * @return The Spearman correlation.
    */
   public static double spearman(List<? extends Number> a, List<? extends Number> b) {
      if (a == null || a.size() == 0 ||
          b == null || b.size() == 0 ||
          a.size() != b.size()) {
         throw new IllegalArgumentException();
      }

      double spearman = 0;

      double aMean = mean(a);
      double bMean = mean(b);

      double aPart = 0;
      double bPart = 0;

      double topPart = 0;
      double bottomAPart = 0;
      double bottomBPart = 0;

      for (int i = 0; i < a.size(); i++) {
         aPart = a.get(i).doubleValue() - aMean;
         bPart = b.get(i).doubleValue() - bMean;

         topPart += aPart * bPart;
         bottomAPart += Math.pow(aPart, 2);
         bottomBPart += Math.pow(bPart, 2);
      }

      return topPart / Math.sqrt(bottomAPart * bottomBPart);
   }
}
