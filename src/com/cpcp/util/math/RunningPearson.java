package com.cpcp.util.math;

/**
 * This class maintains a running Pearson corelation.
 * If you want to only hold a windows worth of data, use
 * com.cpcp.util.math.WindowPearson.
 *
 * Pearson(K) = (SUM(i=0, n) (Xi - mean(X))(Yi - mean(Y))) / (n * StdDev(x) * StdDev(y))
 *
 * S = StdDev
 * ApproxPearson(K) = SUM(i=0, k-1)(Xi * Yi) + (Xk * Yk) - (n * mean(X) * mean(y))
 * This can sometimes be unstable.
 *
 * meank = mean up to k
 * Sxk = Standaed Deviation of X up to k
 *
 * Let T(k) = T(k - 1) + (Xk * Yk)
 * ApproxPearson(k) = (T(k) - (n * meank(X) * meank(y))) / ((n - 1) * Sxk * Syx)
 * Note: Some formulas suggest using n instead of (n - 1) in the denominator.
 *  Using n-1 yeilds far better precision in every test.
 */
public class RunningPearson {
   /**
    * The running std dev for one of the variables.
    */
   private RunningStdDev xStdDev;

   /**
    * The running std dev for one of the variables.
    */
   private RunningStdDev yStdDev;

   /**
    * The covariance of x and y (numerator).
    */
   private double covariance;

   public RunningPearson() {
      xStdDev = new RunningStdDev();
      yStdDev = new RunningStdDev();

      covariance = 0;
   }

   public RunningPearson(int count, double xMean, double xPartialVariance,
                         double yMean, double yPartialVariance) {
      xStdDev = new RunningStdDev(count, xMean, xPartialVariance);
      yStdDev = new RunningStdDev(count, yMean, yPartialVariance);

      covariance = 0;
   }

   public void addValues(double xVal, double yVal) {
      xStdDev.addValue(xVal);
      yStdDev.addValue(yVal);

      covariance += (xVal * yVal);
   }

   public double getCovariance() {
      return covariance;
   }

   public double getPearson() {
      if (xStdDev.getCount() - 1 <= 0) {
         return covariance;
      }

      double denominator =
            (xStdDev.getCount() - 1) * xStdDev.getStdDev() * yStdDev.getStdDev();
      double numerator =
            (covariance - (xStdDev.getCount() * xStdDev.getMean() * yStdDev.getMean()));

      return numerator / denominator;
   }

   public RunningStdDev getXStdDev() {
      return new RunningStdDev(xStdDev.getCount(),
       xStdDev.getMean(), xStdDev.getPartialVariance());
   }

   public RunningStdDev getYStdDev() {
      return new RunningStdDev(yStdDev.getCount(),
       yStdDev.getMean(), yStdDev.getPartialVariance());
   }
}
