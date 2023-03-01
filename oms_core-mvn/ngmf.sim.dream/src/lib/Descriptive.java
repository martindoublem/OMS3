/*
 * $Id$
 * 
 * This file is part of the Object Modeling System (OMS),
 * 2007-2012, Olaf David and others, Colorado State University.
 *
 * OMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 2.1.
 *
 * OMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OMS.  If not, see <http://www.gnu.org/licenses/lgpl.txt>.
 */
package lib;

public class Descriptive extends Object {
    
    /*
     * Makes this class non instantiable, but still let's others inherit from it.
     */
    protected Descriptive() {}
    
    /*
     * Returns the auto-correlation of a data sequence.
     */
    public static double autoCorrelation(DoubleArrayList data, int lag, double mean, double variance) {
        int N = data.size();
	if (lag >= N) {
            throw new IllegalArgumentException("Lag is too large");
        }

	double[] elements = data.elements();
	double run = 0;
	for( int i = lag; i < N; ++i) {
            run += (elements[i] - mean) * (elements[i - lag] - mean);
        }
        return (run / (N - lag)) / variance;
    }
    
    /*
     * Checks if the given range is within the contained array's bounds.
     * @throws IndexOutOfBoundsException if to != from - 1 || from < 0 || from > to || to >= size().
     */
    protected static void checkRangeFromTo(int from, int to, int theSize) {
        if (to==from-1) {
            return;
        }
	if (from < 0 || from > to || to >= theSize) {
            throw new IndexOutOfBoundsException("from: " + from + ", to: " + to + ", size = " + theSize);
        }
    }
    
    /*
     * Returns the correlation of two data sequences, covariance(data1, data2) / (standardDev1 * standardDev2).
     */
    public static double correlation(DoubleArrayList data1, double standardDev1,
            DoubleArrayList data2, double standardDev2) {
        return covariance(data1, data2) / (standardDev1 * standardDev2);
    }
    
    /*
     * Returns the covariance of two data sequences, which is cov(x, y) = 
     * (1 / (size() - 1)) * Sum((x[i] - mean(x)) * (y[i] - mean(y))).
     * See <A HREF="http://www.cquest.utoronto.ca/geog/ggr270y/notes/not05efg.html"></A>.
     */
    public static double covariance(DoubleArrayList data1, DoubleArrayList data2) {
        int size = data1.size();
	if (size != data2.size() || size == 0) {
            throw new IllegalArgumentException();
        }
	double[] elements1 = data1.elements();
	double[] elements2 = data2.elements();
        double sumx = elements1[0];
        double sumy = elements2[0];
        double Sxy = 0.0;
        for (int i = 1; i < size; ++i) {
            sumx += elements1[i];
            Sxy += (elements1[i] - sumx/(i + 1))*(elements2[i] - sumy / i);
            sumy += elements2[i];
            // Exercise for the reader: Why does this give us the right answer?
        }
        return Sxy / (size - 1);
    }
    
    /*
     * Both covariance versions yield the same results but the one above is faster
     */
    private static double covariance2(DoubleArrayList data1, DoubleArrayList data2) {
        int size = data1.size();
        double mean1 = Descriptive.mean(data1);
	double mean2 = Descriptive.mean(data2);
	double covariance = 0.0D;
	for (int i = 0; i < size; i++) {
            covariance += (data1.get(i) - mean1) * (data2.get(i) - mean2);
	}
        return covariance / (double) (size-1);
    }
    
    /*
     * Durbin-Watson computation.
     */
    public static double durbinWatson(DoubleArrayList data) {
        int size = data.size();
        if (size < 2) {
            throw new IllegalArgumentException("Data sequence must contain at least two values.");
        }

	double[] elements = data.elements();
	double run = 0;
	double run_sq = 0;
	run_sq = elements[0] * elements[0];
	for(int i = 1; i < size; ++i) {
            run += (elements[i] - elements[i-1]) * (elements[i] - elements[i-1]);
            run_sq += elements[i] * elements[i];
	}
        return run / run_sq;
    }
    
    /*
     * Computes the frequency (count) of each distinct value in the given sorted data. After this
     * call returns both distinctValues and frequencies have a new size (which is equal for both),
     * which is the number of distinct values in the sorted data. Distinct values are filled into
     * distinctValues, starting at index 0. The frequency of each distinct value is filled into 
     * frequencies, starting at index 0. As a result, the smallest distinct value (and its frequency)
     * can be found at index 0, the second smallest distinct value (and its frequency) at index 1, ...,
     * the largest distinct value (and its frequency) at index distinctValues.size() - 1.
     * Example:
     * elements = (5,6,6,7,8,8) --> distinctValues = (5,6,7,8), frequencies = (1,2,1,2)
     * @param sortedData the data; must be sorted ascending.
     * @param distinctValues a list to be filled with the distinct values; can have any size.
     * @param frequencies      a list to be filled with the frequencies; can have any size; set this
     *      parameter to NULL to ignore it.
     */
    public static void frequencies(DoubleArrayList sortedData, DoubleArrayList distinctValues,
            IntArrayList frequencies) {
        distinctValues.clear();
        if (frequencies!=null) {
            frequencies.clear();
        }

	double[] sortedElements = sortedData.elements();
	int size = sortedData.size();
	int i = 0;
        while (i < size) {
		double element = sortedElements[i];
		int cursor = i;
                // determine run length (number of equal elements)
		while ((++i < size)  &&  (sortedElements[i] == element));
                int runLength = i - cursor;
		distinctValues.add(element);
		if (frequencies!=null) {
                    frequencies.add(runLength);
                }
        }
    }
    
    /*
     * Returns the geometric mean of a data sequence. Note that for a geometric mean to be meaningful,
     * the minimum of the data sequence must not be less or equal to zero. The geometric mean is given
     * by pow(Product( data[i] ), 1 / size), which is equivalent to Math.exp(Sum(Log(data[i])) / size).
     */
    public static double geometricMean(int size, double sumOfLogarithms) {
        return Math.exp(sumOfLogarithms / size);
    }
    
    /*
     * Returns the geometric mean of a data sequence. Note that for a geometric mean to be meaningful,
     * the minimum of the data sequence must not be less or equal to zero. The geometric mean is given
     * by pow(Product(data[i]), 1 / data.size()). This method tries to avoid overflows at the expense
     * of an equivalent but somewhat slow definition: geo = Math.exp(Sum(Log(data[i])) / data.size()).
     */
    public static double geometricMean(DoubleArrayList data) {
        return geometricMean(data.size(), sumOfLogarithms(data, 0, data.size() - 1));
    }
    
    /*
     * Returns the harmonic mean of a data sequence.
     * @param size the number of elements in the data sequence.
     * @param sumOfInversions Sum( 1.0 / data[i]).
     */
    public static double harmonicMean(int size, double sumOfInversions) {
        return size / sumOfInversions;
    }
    
    /*
     * Incrementally maintains and updates minimum, maximum, sum and sum of squares of a data sequence.
     * Assume we have already recorded some data sequence elements and know their minimum, maximum, sum
     * and sum of squares. Assume further, we are to record some more elements and to derive updated 
     * values of minimum, maximum, sum and sum of squares. This method computes those updated values
     * without needing to know the already recorded elements. This is interesting for interactive online
     * monitoring and/or applications that cannot keep the entire huge data sequence in memory.
     * Definition of sumOfSquares: sumOfSquares(n) = Sum (data[i] * data[i]).
     * @param data the additional elements to be incorporated into min, max, etc.
     * @param from the index of the first element within data to consider.
     * @param to the index of the last element within data to consider. The method incorporates elements
     *      data[from], ..., data[to].
     * @param inOut the old values in the following format:
     *      inOut[0] is the old minimum.
     *      inOut[1] is the old maximum.
     *      inOut[2] is the old sum.
     *      inOut[3] is the old sum of squares.
     *      If no data sequence elements have so far been recorded set the values as follows:
     *      inOut[0] = Double.POSITIVE_INFINITY as the old minimum.
     *      inOut[1] = Double.NEGATIVE_INFINITY as the old maximum.
     *      inOut[2] = 0.0 as the old sum.
     *      inOut[3] = 0.0 as the old sum of squares.
     * @return the updated values filled into the inOut array.
     */
    public static void incrementalUpdate(DoubleArrayList data, int from, int to, double[] inOut) {
        checkRangeFromTo(from, to, data.size());
        // read current values
	double min = inOut[0];
	double max = inOut[1];
	double sum = inOut[2];
	double sumSquares = inOut[3];
        double[] elements = data.elements();

	for ( ; from <= to; from++) {
            sum += elements[from];
            sumSquares += elements[from] * elements[from];
		if (elements[from] < min) {
                    min = elements[from];
                }
		if (elements[from] > max) {
                    max = elements[from];
                }
        }
        
        // store new values
	inOut[0] = min;
	inOut[1] = max;
	inOut[2] = sum;
	inOut[3] = sumSquares;
        // At this point of return the following postcondition holds:
	// data.size() - from elements have been consumed by this call.
    }
    
    /*
     * Incrementally maintains and updates various sums of powers of the form Sum(data[i]^k). Assume
     * we have already recorded some data sequence elements data[i] and know the values of 
     * Sum(data[i]^from), Sum(data[i]^from + 1), ..., Sum(data[i]^to). Assume further, we are to
     * record some more elements and to derive updated values of these sums. This method computes
     * those updated values without needing to know the already recorded elements. This is
     * interesting for interactive online monitoring and/or applications that cannot keep the entire
     * huge data sequence in memory. For example, the incremental computation of moments is based upon
     * such sums of powers: The moment of k-th order with constant c of a data sequence, is given by
     * Sum((data[i] - c)^k) / data.size(). It can incrementally be computed by using the equivalent
     * formula moment(k,c) = m(k,c) / data.size(), where m(k,c) = Sum( -1^i * b(k,i) * c^i *
     * sumOfPowers(k - i)) for i = 0 .. k, and b(k,i) = cern.jet.math.Arithmetic#binomial(long,long)
     * and sumOfPowers(k) = Sum(data[i]^k).
     * @param data the additional elements to be incorporated into min, max, etc.
     * @param from the index of the first element within data to consider.
     * @param to the index of the last element within data to consider. The method incorporates
     *      elements data[from], ..., data[to].
     * @param inOut the old values of the sums in the following format:
     *      sumOfPowers[0] is the old Sum(data[i]^fromSumIndex).
     *      sumOfPowers[1] is the old Sum(data[i]^fromSumIndex + 1).
     *      sumOfPowers[toSumIndex - fromSumIndex] is the old Sum(data[i]^toSumIndex).
     *      If no data sequence elements have so far been recorded set all old values of the sums to 0.0.
     * @return the updated values filled into the sumOfPowers array.
     */
    public static void incrementalUpdateSumsOfPowers(DoubleArrayList data, int from, int to,
            int fromSumIndex, int toSumIndex, double[] sumOfPowers) {
        int lastIndex = toSumIndex - fromSumIndex;
	if (from > data.size() || lastIndex + 1 > sumOfPowers.length) {
            throw new IllegalArgumentException();
        }
        // optimized for common parameters
	if (fromSumIndex == 1) { // handle quicker
            if (toSumIndex == 2) {
                double[] elements = data.elements();
                double sum = sumOfPowers[0];
                double sumSquares = sumOfPowers[1];
                for (int i = from - 1; ++i <= to; ) {
                    sum += elements[i];
                    sumSquares += elements[i] * elements[i];
                }
                sumOfPowers[0] += sum;
                sumOfPowers[1] += sumSquares;
                return;
            } else if (toSumIndex == 3) {
                double[] elements = data.elements();
                double sum = sumOfPowers[0];
                double sumSquares = sumOfPowers[1];
                double sum_xxx = sumOfPowers[2];
                for (int i = from - 1; ++i <= to; ) {
                    sum += elements[i];
                    sumSquares += elements[i] * elements[i];
                    sum_xxx += elements[i] * elements[i] * elements[i];
                }
                sumOfPowers[0] += sum;
                sumOfPowers[1] += sumSquares;
                sumOfPowers[2] += sum_xxx;
                return;
            } else if (toSumIndex == 4) { // handle quicker
                double[] elements = data.elements();
                double sum = sumOfPowers[0];
                double sumSquares = sumOfPowers[1];
                double sum_xxx = sumOfPowers[2];
                double sum_xxxx = sumOfPowers[3];
                for (int i = from - 1; ++i <= to; ) {
                    sum += elements[i];
                    sumSquares += elements[i] * elements[i];
                    sum_xxx += elements[i] * elements[i] * elements[i];
                    sum_xxxx += elements[i] * elements[i] * elements[i] * elements[i];
                }
                sumOfPowers[0] += sum;
                sumOfPowers[1] += sumSquares;
                sumOfPowers[2] += sum_xxx;
                sumOfPowers[3] += sum_xxxx;
                return;
            }
        }
        if (fromSumIndex == toSumIndex || (fromSumIndex >= -1 && toSumIndex <= 5)) {
            for (int i = fromSumIndex; i <= toSumIndex; i++) {
                sumOfPowers[i - fromSumIndex] += sumOfPowerDeviations(data, i, 0.0, from, to);
            }
            return;
        }
        
        // now the most general case: optimized for maximum speed, but still not quite quick
	double[] elements = data.elements();

	for (int i = from - 1; ++i <= to; ) {
            double element = elements[i];
            double pow = Math.pow(element, fromSumIndex);
            int j = 0;
            
            for (int m = lastIndex; --m >= 0; ) {
                sumOfPowers[j++] += pow;
                pow *= element;
            }
            sumOfPowers[j] += pow;
        }

	/*
         * At this point of return the following postcondition holds: data.size()-fromIndex elements
         * have been consumed by this call.
         */
    }
    
    /*
     * Incrementally maintains and updates sum and sum of squares of a weighted data sequence. Assume
     * we have already recorded some data sequence elements and know their sum and sum of squares.
     * Assume further, we are to record some more elements and to derive updated values of sum and sum 
     * of squares. This method computes those updated values without needing to know the already
     * recorded elements. This is interesting for interactive online monitoring and/or applications
     * that cannot keep the entire huge data sequence in memory.
     * Definition of sum: sum = Sum ( data[i] * weights[i] ).
     * Definition of sumOfSquares: sumOfSquares = Sum ( data[i] * data[i] * weights[i] ).
     * @param data the additional elements to be incorporated into min, max, etc.
     * @param weights the weight of each element within data.
     * @param from the index of the first element within data (and weights) to consider.
     * @param to the index of the last element within data (and weights) to consider.
     * The method incorporates elements data[from], ..., data[to].
     * @param inOut the old values in the following format:
     *      inOut[0] is the old sum.
     *      inOut[1] is the old sum of squares.
     *      If no data sequence elements have so far been recorded set the values as follows:
     *      inOut[0] = 0.0 as the old sum.
     *      inOut[1] = 0.0 as the old sum of squares.
     * @return the updated values filled into the inOut array.
     */
    public static void incrementalWeightedUpdate(DoubleArrayList data, DoubleArrayList weights,
            int from, int to, double[] inOut) {
        checkRangeFromTo(from, to, data.size());
	if (data.size() != weights.size()) {
            throw new IllegalArgumentException("from = " + from + ", to = " + to + 
                    ", data.size() = " + data.size() + ", weights.size() = " + weights.size());
        }
        // read current values
	double sum = inOut[0];
	double sumOfSquares = inOut[1];
        double[] elements = data.elements();
	double[] w = weights.elements();
	
	for (int i = from - 1; ++i <= to; ) {
            sum += (elements[i] * w[i]);
            sumOfSquares += (elements[i] * elements[i] * w[i]);
	}
        // store new values
	inOut[0] = sum;
	inOut[1] = sumOfSquares;
        // At this point of return the following postcondition holds:
	// data.size()-from elements have been consumed by this call.
    }
    
    /*
     * Returns the kurtosis (aka excess) of a data sequence.
     * @param moment4 the fourth central moment, which is moment(data, 4, mean).
     * @param standardDeviation the standardDeviation.
     */
    public static double kurtosis(double moment4, double standardDeviation) {
        return -3 + moment4 / (standardDeviation * standardDeviation * standardDeviation * standardDeviation);
    }
    
    /*
     * Returns the kurtosis (aka excess) of a data sequence, which is 
     * -3 + moment(data, 4, mean) / standardDeviation^4.
     */
    public static double kurtosis(DoubleArrayList data, double mean, double standardDeviation) {
        return kurtosis(moment(data, 4, mean), standardDeviation);
    }
    
    /*
     * Returns the lag-1 autocorrelation of a dataset; Note that this method has semantics different
     * from autoCorrelation(..., 1).
     */
    public static double lag1(DoubleArrayList data, double mean) {
        double[] elements = data.elements();
	double q = 0.0;
	double v = (elements[0] - mean) * (elements[0] - mean);

	for (int i = 1; i < data.size(); i++) {
            double delta0 = (elements[i - 1] - mean);
            double delta1 = (elements[i] - mean);
            q += (delta0 * delta1 - q)/(i + 1);
            v += (delta1 * delta1 - v)/(i + 1);
	}
        return q / v;
    }
    
    /*
     * Returns the largest member of a data sequence.
     */
    public static double max(DoubleArrayList data) {
        if (data.size() == 0) {
            throw new IllegalArgumentException();
        }
	
	double[] elements = data.elements();
	double max = elements[data.size() - 1];
	for (int i = data.size() - 1; --i >= 0; ) {
            if (elements[i] > max) {
                max = elements[i];
            }
        }
        return max;
    }
    
    /*
     * Returns the arithmetic mean of a data sequence; That is Sum( data[i] ) / data.size().
     */
    public static double mean(DoubleArrayList data) {
        return sum(data) / data.size();
    }
    
    /*
     * Returns the mean deviation of a dataset. That is Sum(Math.abs(data[i] - mean)) / data.size()).
     */
    public static double meanDeviation(DoubleArrayList data, double mean) {
        double[] elements = data.elements();
	double sum = 0;
	for (int i = data.size(); --i >= 0; ) {
            sum += Math.abs(elements[i] - mean);
        }
	return sum/data.size();
    }
    
    /*
     * Returns the median of a sorted data sequence.
     * @param sortedData the data sequence; MUST be sorted ascending.
     */
    public static double median(DoubleArrayList sortedData) {
        return quantile(sortedData, 0.5);
    }
    
    /*
     * Returns the smallest member of a data sequence.
     */
    public static double min(DoubleArrayList data) {
        if (data.size() == 0) {
            throw new IllegalArgumentException();
        }
	
	double[] elements = data.elements();
	double min = elements[data.size() - 1];
	for (int i = data.size()-1; --i >= 0; ) {
            if (elements[i] < min) {
                min = elements[i];
            }
	}
        return min;
    }
    
    /*
     * Returns the moment of k-th order with constant c of a data sequence, which is 
     * Sum( (data[i]-c)^k ) / data.size().
     * @param sumOfPowers sumOfPowers[m] == Sum( data[i]^m) ) for m = 0,1,..,k as returned
     *      by method incrementalUpdateSumsOfPowers(DoubleArrayList,int,int,int,int,double[]).
     *      In particular there must hold sumOfPowers.length == k+1.
     * @param size the number of elements of the data sequence.
     */
    public static double moment(int k, double c, int size, double[] sumOfPowers) {
        double sum = 0.0;
        int sign = 1;
	for (int i = 0; i <= k; i++) {
            double y;
            if (i == 0) {
                y = 1;
            } else if (i == 1) {
                y = c;
            } else if (i == 2) {
                y = c * c;
            } else if (i == 3) {
                y = c * c * c;
            } else {
                y = Math.pow(c, i);
            }
            sum += sign * Arithmetic.binomial(k, i) * y * sumOfPowers[k - i];
            sign = -sign;
        }
        return sum / size;
    }
    
    /*
     * Returns the moment of k-th order with constant c of a data sequence, which is 
     * Sum( (data[i] - c)^k ) / data.size().
     */
    public static double moment(DoubleArrayList data, int k, double c) {
        return sumOfPowerDeviations(data, k, c) / data.size();
    }
    
    /*
     * Returns the pooled mean of two data sequences. That is 
     * (size1 * mean1 + size2 * mean2) / (size1 + size2).
     * @param size1 the number of elements in data sequence 1.
     * @param mean1 the mean of data sequence 1.
     * @param size2 the number of elements in data sequence 2.
     * @param mean2 the mean of data sequence 2.
     */
    public static double pooledMean(int size1, double mean1, int size2, double mean2) {
        return (size1 * mean1 + size2 * mean2) / (size1 + size2);
    }
    
    /*
     * Returns the pooled variance of two data sequences. That is,
     * (size1 * variance1 + size2 * variance2) / (size1 + size2).
     * @param size1 the number of elements in data sequence 1.
     * @param variance1 the variance of data sequence 1.
     * @param size2 the number of elements in data sequence 2.
     * @param variance2 the variance of data sequence 2.
     */
    public static double pooledVariance(int size1, double variance1, int size2, double variance2) {
	return (size1 * variance1 + size2 * variance2) / (size1 + size2);
    }
    
    /*
     * Returns the product, which is Prod( data[i] ). In other words: 
     * data[0]*data[1]*...*data[data.size()-1]. This method uses the equivalent definition:
     * prod = pow( exp( Sum( Log(x[i]) ) / size(), size()).
     */
    public static double product(int size, double sumOfLogarithms) {
	return Math.pow( Math.exp(sumOfLogarithms / size), size);
    }
    
    /*
     * Returns the product of a data sequence, which is Prod( data[i] ). In other words:
     * data[0]*data[1]*...*data[data.size()-1]. Note that you may easily get numeric overflows.
     */
    public static double product(DoubleArrayList data) {
        double[] elements = data.elements();
        double product = 1;
	for (int i = data.size(); --i >= 0; ) {
            product *= elements[i];
        }
        return product;
    }
    
    /*
     * Returns the phi-quantile; that is, an element elem for which holds that phi percent of 
     * data elements are less than elem. The quantile need not necessarily be contained in the
     * data sequence, it can be a linear interpolation.
     * @param sortedData the data sequence; MUST be sorted ascending.
     * @param phi the percentage; must satisfy 0 <= phi <= 1.
     */
    public static double quantile(DoubleArrayList sortedData, double phi) {
        double[] sortedElements = sortedData.elements();
	double index = phi * (sortedData.size() - 1);
	int lhs = (int) index;
	double delta = index - lhs;
	double result;

	if (sortedData.size() == 0) {
            return 0.0;
        }
        if (lhs == sortedData.size() - 1) {
            result = sortedElements[lhs];
	} else {
            result = (1 - delta) * sortedElements[lhs] + delta * sortedElements[lhs + 1];
	}
        return result;
    }
    
    /*
     * Returns how many percent of the elements contained in the receiver are <= element. Does
     * linear interpolation if the element is not contained but lies in between two contained elements.
     * @param sortedList the list to be searched (must be sorted ascending).
     * @param element the element to search for.
     * @return the percentage phi of elements <= element (0.0 <= phi <= 1.0).
     */
    public static double quantileInverse(DoubleArrayList sortedList, double element) {
        return rankInterpolated(sortedList, element) / sortedList.size();
    }
    
    /*
     * Returns the quantiles of the specified percentages. The quantiles need not necessarily be
     * contained in the data sequence, it can be a linear interpolation.
     * @param sortedData the data sequence; MUST be sorted ascending.
     * @param percentages the percentages for which quantiles are to be computed.
     * Each percentage must be in the interval [0.0,1.0].
     * @return the quantiles.
     */
    public static DoubleArrayList quantiles(DoubleArrayList sortedData, DoubleArrayList percentages) {
        DoubleArrayList quantiles = new DoubleArrayList(percentages.size());
        for (int i = 0; i < percentages.size(); i++) {
            quantiles.add( quantile(sortedData, percentages.get(i)) );
	}
        return quantiles;
    }
    
    /*
     * Returns the linearly interpolated number of elements in a list less or equal to a given element.
     * The rank is the number of elements <= element. Ranks are of the form <tt>{0, 1, 2,..., 
     * sortedList.size(). If no element is <= element, then the rank is zero. If the element lies in
     * between two contained elements, then linear interpolation is used and a non integer value is returned.
     * @param sortedList the list to be searched (must be sorted ascending).
     * @param element the element to search for.
     * @return the rank of the element.
     */
    public static double rankInterpolated(DoubleArrayList sortedList, double element) {
        int index = sortedList.binarySearch(element);
	if (index >= 0) { // element found
            // skip to the right over multiple occurances of element.
            int to = index + 1;
            while (to < sortedList.size() && sortedList.get(to) == element){
                to++;
            }
            return to;
	}
        // element not found
	int insertionPoint = -index - 1;
	if (insertionPoint == 0 || insertionPoint == sortedList.size()) {
            return insertionPoint;
        }

	double from = sortedList.get(insertionPoint - 1);
	double to = sortedList.get(insertionPoint);
	return insertionPoint + ((element - from) / (to - from));
    }
    
    /*
     * Returns the RMS (Root-Mean-Square) of a data sequence. That is 
     * Math.sqrt(Sum( data[i]*data[i] ) / data.size()). The RMS of data sequence is the 
     * square-root of the mean of the squares of the elements in the data sequence. It is a
     * measure of the average "size" of the elements of a data sequence.
     * @param sumOfSquares sumOfSquares(data) == Sum( data[i]*data[i] ) of the data sequence.
     * @param size the number of elements in the data sequence.
     */
    public static double rms(int size, double sumOfSquares) {
        return Math.sqrt(sumOfSquares / size);
    }
    
    /*
     * Returns the sample kurtosis (aka excess) of a data sequence. Ref: R.R. Sokal, F.J. Rohlf,
     * Biometry: the principles and practice of statistics in biological research (W.H. Freeman
     * and Company, New York, 1998, 3rd edition), p. 114-115.
     * @param size the number of elements of the data sequence.
     * @param moment4 the fourth central moment, which is moment(data,4,mean).
     * @param sampleVariance the sample variance.
     */
    public static double sampleKurtosis(int size, double moment4, double sampVar) {
        double m4 = moment4*size;    // (y-ymean)^4
        return m4 * size * (size + 1) / ((size - 1) * (size - 2) * (size - 3) * sampVar * sampVar)
                - 3.0 * (size - 1) * (size - 1) / ((size - 2) * (size - 3));
    }
    
    /*
     * Returns the sample kurtosis (aka excess) of a data sequence.
     */
    public static double sampleKurtosis(DoubleArrayList data, double mean, double sampleVariance) {
        return sampleKurtosis(data.size(),moment(data, 4, mean), sampleVariance);
    }
    
    /*
     * Return the standard error of the sample kurtosis. Ref: R.R. Sokal, F.J. Rohlf, Biometry: the
     * principles and practice of statistics in biological research (W.H. Freeman and Company, 
     * New York, 1998, 3rd edition), p. 138.
     * @param size the number of elements of the data sequence.
     */
    public static double sampleKurtosisStandardError(int size) {
        return Math.sqrt(24.0 * size * (size - 1) * (size - 1) / ((size - 3) * 
                (size - 2) * (size + 3) * (size + 5)));
    }
    
    /*
     * Returns the sample skew of a data sequence. Ref: R.R. Sokal, F.J. Rohlf, Biometry: the 
     * principles and practice of statistics in biological research (W.H. Freeman and Company,
     * New York, 1998, 3rd edition), p. 114-115.
     * @param size the number of elements of the data sequence.
     * @param moment3 the third central moment, which is moment(data, 3, mean).
     * @param sampleVariance the sample variance.
     */
    public static double sampleSkew(int size, double moment3, double sampleVariance) {
        double s = Math.sqrt(sampleVariance); // sqrt( (y-ymean)^2/(n-1) )
        return (size * moment3 * size) / ((size - 1) * (size - 2) * s * s * s);
    }
    
    /*
     * Returns the sample skew of a data sequence.
     */
    public static double sampleSkew(DoubleArrayList data, double mean, double sampleVariance) {
        return sampleSkew(data.size(), moment(data, 3, mean), sampleVariance);
    }
    
    /*
     * Return the standard error of the sample skew. Ref: R.R. Sokal, F.J. Rohlf, Biometry: the 
     * principles and practice of statistics in biological research (W.H. Freeman and Company,
     * New York, 1998, 3rd edition), p. 138.
     * @param size the number of elements of the data sequence.
     */
    public static double sampleSkewStandardError(int size) {
        return Math.sqrt(6.0 * size * (size - 1) / ((size - 2) * (size + 1) * (size + 3)));
    }
    
    /*
     * Returns the sample standard deviation. Ref: R.R. Sokal, F.J. Rohlf, Biometry: the 
     * principles and practice of statistics in biological research (W.H. Freeman and 
     * Company, New York, 1998, 3rd edition), p. 53.
     * @param size the number of elements of the data sequence.
     * @param sampleVariance the sample variance.
     */
    public static double sampleStandardDeviation(int size, double sampleVariance) {
        double Cn;
        // It needs to be multiplied by this correction factor.
        if (size > 30) {
            Cn = 1 + 1.0 / (4 * (size - 1)); // Cn = 1+1/(4*(n-1));
        } else {
            Cn = Math.sqrt((size - 1) * 0.5) * Gamma.gamma((size - 1) * 0.5) / Gamma.gamma(size * 0.5);
        }
        return Cn * Math.sqrt(sampleVariance);
    }
    
    /*
     * Returns the sample variance of a data sequence. That is (sumOfSquares - mean*sum) /
     * (size - 1) with mean = sum / size.
     * @param size the number of elements of the data sequence.
     * @param sum == Sum( data[i] ).
     * @param sumOfSquares == Sum( data[i]*data[i] ).
     */
    public static double sampleVariance(int size, double sum, double sumOfSquares) {
        return (sumOfSquares - (sum / size) * sum) / (size - 1);
    }
    
    /*
     * Returns the sample variance of a data sequence. That is, Sum( (data[i]-mean)^2 ) /
     * (data.size()-1).
     */
    public static double sampleVariance(DoubleArrayList data, double mean) {
        double[] elements = data.elements();
	double sum = 0.0;
	// find the sum of the squares 
	for (int i = data.size(); --i >= 0; ) {
            sum += (elements[i] - mean) * (elements[i] - mean);
        }
        return sum / (data.size() - 1);
    }
    
    /*
     * Returns the sample weighted variance of a data sequence. That is, 
     * (sumOfSquaredProducts - sumOfProducts * sumOfProducts / sumOfWeights) / (sumOfWeights - 1).
     * @param sumOfWeights == Sum( weights[i] ).
     * @param sumOfProducts == Sum( data[i] * weights[i] ).
     * @param sumOfSquaredProducts == Sum( data[i] * data[i] * weights[i] ).
     */
    public static double sampleWeightedVariance(double sumOfWeights, double sumOfProducts,
            double sumOfSquaredProducts) {
        return (sumOfSquaredProducts  -  sumOfProducts * sumOfProducts / sumOfWeights) / (sumOfWeights - 1);
    }
    
    /*
     * Returns the skew of a data sequence.
     * @param moment3 the third central moment, which is moment(data, 3, mean).
     * @param standardDeviation the standardDeviation.
     */
    public static double skew(double moment3, double standardDeviation) {
        return moment3 / (standardDeviation * standardDeviation * standardDeviation);
    }
    
    /*
     * Returns the skew of a data sequence, which is moment(data, 3, mean) / standardDeviation^3.
     */
    public static double skew(DoubleArrayList data, double mean, double standardDeviation) {
	return skew(moment(data, 3, mean), standardDeviation);
    }
    
    /*
     * Splits (partitions) a list into sublists such that each sublist contains the elements with
     * a given range. splitters = (a,b,c,...,y,z) defines the ranges [-inf,a), [a,b), [b,c),
     * ..., [y,z), [z,inf]. Examples:
     * data = (1,2,3,4,5,8,8,8,10,11).
     * splitters=(2,8) yields 3 bins: (1), (2,3,4,5) (8,8,8,10,11).
     * splitters=() yields 1 bin: (1,2,3,4,5,8,8,8,10,11).
     * splitters=(-5) yields 2 bins: (), (1,2,3,4,5,8,8,8,10,11).
     * splitters=(100) yields 2 bins: (1,2,3,4,5,8,8,8,10,11), ().
     * @param sortedList the list to be partitioned (must be sorted ascending).
     * @param splitters the points at which the list shall be partitioned (must be sorted ascending).
     * @return the sublists (an array with length == splitters.size() + 1. Each sublist is
     *      returned sorted ascending.
     */
    public static DoubleArrayList[] split(DoubleArrayList sortedList, DoubleArrayList splitters) {
        // assertion: data is sorted ascending.
	// assertion: splitValues is sorted ascending.
	DoubleArrayList[] bins = new DoubleArrayList[splitters.size() + 1];
	for (DoubleArrayList dA: bins) {
            dA = new DoubleArrayList();
        }
        int nextStart = 0;
	int i = 0;
        
        while (nextStart < sortedList.size() && i < splitters.size()) {
            double splitValue = splitters.get(i);
            int index = sortedList.binarySearch(splitValue);
            if (index < 0) { // splitValue not found
                int insertionPosition = -index - 1;
                bins[i].addAllOfFromTo(sortedList, nextStart, insertionPosition - 1);
                nextStart = insertionPosition;
            } else { // splitValue found
                /*
                 * For multiple identical elements ("runs"), binarySearch does not define which of
                 * all valid indexes is returned. Thus, skip over to the first element of a run.
                 */
                do {
                    index--;
                } while (index >= 0 && sortedList.get(index) == splitValue);
                
                bins[i].addAllOfFromTo(sortedList, nextStart, index);
                nextStart = index + 1;
            }
            i++;
        }
        // now fill the remainder
        bins[splitters.size()].addAllOfFromTo(sortedList, nextStart, sortedList.size() - 1);
        return bins;
    }
    
    /*
     * Returns the standard deviation from a variance.
     */
    public static double standardDeviation(double variance) {
        return Math.sqrt(variance);
    }
    
    /*
     * Returns the standard error of a data sequence. That is Math.sqrt(variance / size).
     * @param size the number of elements in the data sequence.
     * @param variance the variance of the data sequence.
     */
    public static double standardError(int size, double variance) {
        return Math.sqrt(variance / size);
    }
    
    /*
     * Modifies a data sequence to be standardized. Changes each element data[i] as follows:
     * data[i] = (data[i] - mean) / standardDeviation.
     */
    public static void standardize(DoubleArrayList data, double mean, double standardDeviation) {
        double[] elements = data.elements();
        for (int i = data.size(); --i >= 0; ) {
            elements[i] = (elements[i] - mean) / standardDeviation;
        }
    }
    
    /*
     * Returns the sum of a data sequence. That is Sum( data[i] ).
     */
    public static double sum(DoubleArrayList data) {
	return sumOfPowerDeviations(data, 1, 0.0);
    }
    
    /*
     * Returns the sum of inversions of a data sequence, which is Sum( 1.0 / data[i]).
     * @param data the data sequence.
     * @param from the index of the first data element (inclusive).
     * @param to the index of the last data element (inclusive).
     */
    public static double sumOfInversions(DoubleArrayList data, int from, int to) {
	return sumOfPowerDeviations(data, -1, 0.0, from, to);
    }
    
    /*
     * Returns the sum of logarithms of a data sequence, which is Sum( Log(data[i]).
     * @param data the data sequence.
     * @param from the index of the first data element (inclusive).
     * @param to the index of the last data element (inclusive).
     */
    public static double sumOfLogarithms(DoubleArrayList data, int from, int to) {
        double[] elements = data.elements();
	double logsum = 0.0;
	for (int i = from - 1; ++i <= to; ) {
            logsum += Math.log(elements[i]);
        }
	return logsum;
    }
    
    /*
     * Returns Sum( (data[i]-c)^k ); optimized for common parameters like c == 0.0 and/or
     * k == -2 .. 4.
     */
    public static double sumOfPowerDeviations(DoubleArrayList data, int k, double c) {
        return sumOfPowerDeviations(data,k,c,0,data.size()-1);
    }
    
    /*
     * Returns Sum( (data[i]-c)^k ) for all i = from .. to; optimized for common parameters like
     * c == 0.0 and/or k == -2 .. 5.
     */
    public static double sumOfPowerDeviations(final DoubleArrayList data, final int k, 
            final double c, final int from, final int to) {
        final double[] elements = data.elements();
        double sum = 0.0;
	int i;
	switch (k) { // optimized for speed
            case -2:
                if (c == 0.0) {
                    for (i = from - 1; ++i <= to; ) {
                        sum += 1 / (elements[i] * elements[i]);
                    }
                } else {
                    for (i = from - 1; ++i <= to; ) {
                        sum += 1 / ((elements[i] - c) * (elements[i] - c));
                    }
                }
                break;
            case -1:
                if (c == 0.0) {
                    for (i = from - 1; ++i <= to; ) {
                        sum += 1 / (elements[i]);
                    }
                } else {
                    for (i = from - 1; ++i <= to; ) {
                        sum += 1 / (elements[i] - c);
                    }
                }
                break;
            case 0:
                sum += to - from + 1;
                break;
            case 1:
                if (c == 0.0) {
                    for (i = from - 1; ++i <= to; ) {
                        sum += elements[i];
                    }
                } else {
                    for (i = from - 1; ++i <= to; ) {
                        sum += elements[i] - c;
                    }
                }
                break;
            case 2:
                if (c == 0.0) {
                    for (i = from - 1; ++i <= to; ) {
                        sum += elements[i] * elements[i];
                    }
                } else {
                    for (i = from - 1; ++i <= to; ) {
                        sum += (elements[i] - c) * (elements[i] - c);
                    }
                }
                break;
            case 3:
                if (c == 0.0) {
                    for (i = from - 1; ++i <= to; ) { 
                        sum += elements[i] * elements[i] * elements[i]; 
                    }
                } else {
                    for (i=from-1; ++i<=to; ) {
                        sum += (elements[i] - c) * (elements[i] - c) * (elements[i] - c); 
                    }
                }
                break;
            case 4:
                if (c==0.0) {
                    for (i=from-1; ++i<=to; ) {
                        sum += elements[i] * elements[i] * elements[i] * elements[i];
                    }
                } else {
                    for (i=from-1; ++i<=to; ) {
                        sum += (elements[i] - c) * (elements[i] - c) * (elements[i] - c) 
                                * (elements[i] - c);
                    }
                }
                break;
            case 5:
                if (c == 0.0) {
                    for (i = from - 1; ++i <= to; ) {
                        sum += elements[i] * elements[i] * elements[i] * elements[i] 
                                * elements[i];
                    }
                } else {
                    for (i=from-1; ++i<=to; ) {
                        sum += (elements[i] - c) * (elements[i] - c) * (elements[i] - c)
                                * (elements[i] - c) * (elements[i] - c);
                    }
                }
                break;
            default:
			for (i=from-1; ++i<=to; ) sum += Math.pow(elements[i]-c, k);
			break;
	}
	return sum;
    }
    
    /*
     * Returns the sum of powers of a data sequence, which is Sum( data[i]^k ).
     */
    public static double sumOfPowers(DoubleArrayList data, int k) {
        return sumOfPowerDeviations(data, k, 0);
    }
    
    /*
     * Returns the sum of squared mean deviation of of a data sequence. That is 
     * variance * (size - 1) == Sum( (data[i] - mean)^2 ).
     * @param size the number of elements of the data sequence. 
     * @param variance the variance of the data sequence.
     */
    public static double sumOfSquaredDeviations(int size, double variance) {
        return variance * (size - 1);
    }
    
    /*
     * Returns the sum of squares of a data sequence. That is Sum( data[i] * data[i] ).
     */
    public static double sumOfSquares(DoubleArrayList data) {
        return sumOfPowerDeviations(data, 2, 0.0);
    }
    
    /*
     * Returns the trimmed mean of a sorted data sequence.
     * @param sortedData the data sequence; MUST be sorted ascending.
     * @param mean the mean of the (full) sorted data sequence.
     * @left the number of leading elements to trim.
     * @right the number of trailing elements to trim.
     */
    public static double trimmedMean(DoubleArrayList sortedData, double mean, int left, int right) {
        int N = sortedData.size();
        int N0 = sortedData.size();
	if (sortedData.size() == 0) {
            throw new IllegalArgumentException("Empty data.");
        }
	if (left + right >= sortedData.size()) {
            throw new IllegalArgumentException("Not enough data.");
        }

	double[] sortedElements = sortedData.elements();
	for(int i = 0; i < left; ++i) {
            mean += (mean-sortedElements[i]) / (--N);
        }
	for(int i = 0; i < right; ++i) {
            mean += (mean - sortedElements[N0 - 1 - i]) / (--N);
        }
	return mean;
    }
    
    /*
     * Returns the variance from a standard deviation.
     */
    public static double variance(double standardDeviation) {
        return standardDeviation * standardDeviation;
    }
    
    /*
     * Returns the variance of a data sequence. That is (sumOfSquares - mean*sum) /
     * size with mean = sum / size.
     * @param size the number of elements of the data sequence.
     * @param sum == Sum( data[i] ).
     * @param sumOfSquares == Sum( data[i] * data[i] ).
     */
    public static double variance(int size, double sum, double sumOfSquares) {
        double mean = sum / size;
	return (sumOfSquares - mean * sum) / size;
    }
    
    /*
     * Returns the weighted mean of a data sequence. That is Sum(data[i] * weights[i]) /
     * Sum( weights[i] ).
     */
    public static double weightedMean(DoubleArrayList data, DoubleArrayList weights) {
        if (data.size() != weights.size() || data.size() == 0) {
            throw new IllegalArgumentException();
        }
	
	double[] elements = data.elements();
	double[] theWeights = weights.elements();
	double sum = 0.0;
	double weightsSum = 0.0;
	for (int i = data.size(); --i >= 0; ) {
            sum += elements[i] * theWeights[i];
            weightsSum += theWeights[i];
	}
        return sum / weightsSum;
    }
    
    /*
     * Returns the weighted RMS (Root-Mean-Square) of a data sequence. That is 
     * Sum( data[i] * data[i] * weights[i]) / Sum( data[i] * weights[i] ), or in other words
     * sumOfProducts / sumOfSquaredProducts.
     * @param sumOfProducts == Sum( data[i] * weights[i] ).
     * @param sumOfSquaredProducts == Sum( data[i] * data[i] * weights[i] ).
     */
    public static double weightedRMS(double sumOfProducts, double sumOfSquaredProducts) {
	return sumOfProducts / sumOfSquaredProducts;
    }
    
    /*
     * Returns the winsorized mean of a sorted data sequence.
     * @param sortedData the data sequence; MUST be sorted ascending.
     * @param mean the mean of the (full) sorted data sequence.
     * @left the number of leading elements to trim.
     * @right the number of trailing elements to trim.
     */
    public static double winsorizedMean(DoubleArrayList sortedData, double mean, int left,
            int right) {
	if (sortedData.size() == 0) {
            throw new IllegalArgumentException("Empty data.");
        }
	if (left + right >= sortedData.size()) {
            throw new IllegalArgumentException("Not enough data.");
        }

	double[] sortedElements = sortedData.elements();
        double leftElement = sortedElements[left];
	for(int i = 0; i < left; ++i) {
            mean += (leftElement - sortedElements[i]) / sortedData.size();
        }
        double rightElement = sortedElements[sortedData.size() - 1 - right];
	for(int i = 0; i < right; ++i) {
            mean += (rightElement - sortedElements[sortedData.size() - 1 - i]) / sortedData.size();
        }
        return mean;
    }
}