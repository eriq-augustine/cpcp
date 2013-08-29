package com.cpcp.util.math;

/**
 * This class maintains a running windowed Pearson corelation.
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
 *
 * w = window size
 * Now to calculate a window pearson, just calculate T in a window.
 * T(k) = T(k - 1) + (Xk * Yk) - (Xk-w * Yk-w)
 */
public class WindowPearson {
   /**
    * The running std dev for one of the variables.
    */
   private WindowStdDev xStdDev;

   /**
    * The running std dev for one of the variables.
    */
   private WindowStdDev yStdDev;

   /**
    * The covariance of x and y (numerator).
    */
   private double covariance;

   public WindowPearson(int windowSize) {
      xStdDev = new WindowStdDev(windowSize);
      yStdDev = new WindowStdDev(windowSize);

      covariance = 0;
   }

   public WindowPearson(WindowStdDev xStdDev, WindowStdDev yStdDev, double covariance) {
      this.xStdDev = new WindowStdDev(xStdDev);
      this.yStdDev = new WindowStdDev(yStdDev);
      this.covariance = covariance;

      assert(xStdDev.getWindowSize() == yStdDev.getWindowSize());
   }

   public void addValues(double xVal, double yVal) {
      if (xStdDev.getCount() < xStdDev.getWindowSize()) {
         double removeX = xStdDev.addValue(xVal);
         double removeY = yStdDev.addValue(yVal);
         double deltaCovariance = (xVal * yVal);

         covariance += deltaCovariance;
      } else {
         double removeX = xStdDev.addValue(xVal);
         double removeY = yStdDev.addValue(yVal);
         double deltaCovariance = (xVal * yVal);

         double removeCovariance = (removeX * removeY);
         covariance += deltaCovariance - removeCovariance;
      }
   }

   public double getCovariance() {
      return covariance;
   }

   public double getPearson() {
      return (covariance - (xStdDev.getCount() * xStdDev.getMean() * yStdDev.getMean())) /
       ((xStdDev.getCount() - 1) * xStdDev.getStdDev() * yStdDev.getStdDev());
   }

   public WindowStdDev getXStdDev() {
      return new WindowStdDev(xStdDev);
   }

   public WindowStdDev getYStdDev() {
      return new WindowStdDev(yStdDev);
   }
}
