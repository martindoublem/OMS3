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

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/*
 * Provides methods to randomly shuffle arrays or lists using a random stream.
 */
public class RandomPermutation {
    
    private static final int SHUFFLE_THRESHOLD = 5;
    
    /*
     * Initializes array with the first n positive integers in natural order as array[i - 1] = i, for
     * i = 1,..., n. The size of array must be at least n.
     * @param array the array to initialize.
     * @param n number of elements initialized.
     */
    public static void init (byte[] array, int n) {
        for (byte k = 1; k <= n; k++) {
            array[k - 1] = k;
        }
    }
    
    /*
     * Similar to #init init(byte[], int).
     * @param array the array to initialize.
     * @param n number of elements initialized.
     */
    public static void init (short[] array, int n) {
        for (short k = 1; k <= n; k++) {
            array[k-1] = k;
        }
    }
    
    /*
     * Similar to #init init(byte[], int).
     * @param array the array to initialize.
     * @param n number of elements initialized.
     */
    public static void init (int[] array, int n) {
        for (int k = 1; k <= n; k++) {
            array[k - 1] = k;
        }
    }
    
    /*
     * Similar to #init init(byte[], int).
     * @param array the array to initialize.
     * @param n number of elements initialized.
     */
    public static void init (long[] array, int n) {
        for (int k = 1; k <= n; k++) {
            array[k - 1] = k;
        }
    }
    
    /*
     * Similar to #init init(byte[], int).
     * @param array the array to initialize.
     * @param n number of elements initialized.
     */
    public static void init (float[] array, int n) {
        for (int k = 1; k <= n; k++) {
            array[k - 1] = k;
        }
    }
    
    /*
     * Similar to #init init(byte[], int).
     * @param array the array to initialize.
     * @param n number of elements initialized.
     */
    public static void init (double[] array, int n) {
        for (int k = 1; k <= n; k++) {
            array[k - 1] = k;
        }
    }
    
    @SuppressWarnings("unchecked")
    /*
     * Same as java.util.Collections.shuffle(List<> Random), but uses a RandomStream instead of 
     * java.util.Random.
     * @param list the list being shuffled.
     * @param stream the random stream used to generate integers.
     */
    public static void shuffle (List<?> list, RandomStream stream) {
        // The implementation is inspired from Sun's Collections.shuffle
        final int size = list.size ();
        if (size < SHUFFLE_THRESHOLD || list instanceof RandomAccess) {
            for (int i = size; i > 1; i--) {
                Collections.swap(list, i - 1, stream.nextInt(0, i - 1));
            }
        } else {
            final Object arr[] = list.toArray();
            
            // Shuffle array
            shuffle(arr, stream);
            // Dump array back into list
            final ListIterator it = list.listIterator();
            for (Object element : arr) {
                it.next();
                it.set(element);
            }
        }
    }
    
    /*
     * Randomly permutes array using stream. This method permutes the whole array.
     * @param array the array being shuffled.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (Object[] array, RandomStream stream) {
        final int size = array.length;
        for (int i = size - 1; i > 0; i--) {
            final int j = stream.nextInt(0, i);
            final Object tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }
    
    /*
     * Randomly permutes array using stream. This method permutes the whole array.
     * @param array the array being shuffled.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (byte[] array, RandomStream stream) {
        final int size = array.length;
        for (int i = size - 1; i > 0; i--) {
            final int j = stream.nextInt(0, i);
            final byte tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }
    
    /*
     * Similar to #shuffle shuffle(byte[], RandomStream).
     * @param array the array being shuffled.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (short[] array, RandomStream stream) {
        final int size = array.length;
        for (int i = size - 1; i > 0; i--) {
            final int j = stream.nextInt(0, i);
            final short tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }
    
    /*
     * Similar to #shuffle shuffle(byte[], RandomStream).
     * @param array the array being shuffled.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (int[] array, RandomStream stream) {
        final int size = array.length;
        for (int i = size - 1; i > 0; i--) {
            final int j = stream.nextInt(0, i);
            final int tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }
    
    /*
     * Similar to #shuffle shuffle(byte[], RandomStream).
     * @param array the array being shuffled.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (long[] array, RandomStream stream) {
        final int size = array.length;
        for (int i = size - 1; i > 0; i--) {
            final int j = stream.nextInt(0, i);
            final long tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }
    
    /*
     * Similar to #shuffle shuffle(byte[], RandomStream).
     * @param array the array being shuffled.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (char[] array, RandomStream stream) {
        final int size = array.length;
        for (int i = size - 1; i > 0; i--) {
            final int j = stream.nextInt(0, i);
            final char tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }
    
    /*
     * Similar to #shuffle shuffle(byte[], RandomStream).
     * @param array the array being shuffled.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (boolean[] array, RandomStream stream) {
        final int size = array.length;
        for (int i = size - 1; i > 0; i--) {
            final int j = stream.nextInt(0, i);
            final boolean tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }
    
    /*
     * Similar to #shuffle shuffle(byte[], RandomStream).
     * @param array the array being shuffled.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (float[] array, RandomStream stream) {
        final int size = array.length;
        for (int i = size - 1; i > 0; i--) {
            final int j = stream.nextInt(0, i);
            final float tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }
    
    /*
     * Similar to #shuffle shuffle(byte[], RandomStream).
     * @param array the array being shuffled.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (double[] array, RandomStream stream) {
        final int size = array.length;
        for (int i = size - 1; i > 0; i--) {
            final int j = stream.nextInt(0, i);
            final double tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }
    
    @SuppressWarnings("unchecked")
    /*
     * Partially permutes list as follows using stream: draws the first k new elements of list randomly
     * among the n old elements of list, assuming that k <= n = list.size(). In other words, k elements
     * are selected at random without replacement from the n list entries and are placed in the first 
     * k positions, in random order.
     * @param list the list being shuffled.
     * @param k number of elements selected.
     * @param stream the random stream used to generate integers.
     */
    public static void shuffle (List<?> list, int k, RandomStream stream) {
        
        // @precondition 0 <= k <= n <= size.
        // The implementation is inspired from Sun's Collections.shuffle
        if (k < 0 || k > list.size()) {
            throw new IllegalArgumentException("k must be   0 <= k <= list.size()");
        }
        if (0 == k) {
            return;
        }
        if (list.size() < SHUFFLE_THRESHOLD || list instanceof RandomAccess) {
            for (int i = 0; i < k; i++) {
                // Get random j in {i,...,n-1} and interchange a[i] with a[j].
                Collections.swap(list, i, stream.nextInt(i, list.size() - 1));
            }
        } else {
            final Object arr[] = list.toArray();
            // Shuffle array
            shuffle(arr, list.size(), k, stream);
            // Dump array back into list
            final ListIterator it = list.listIterator();
            for (Object element : arr) {
                it.next();
                it.set(element);
            }
        }
    }
    
    /*
     * Partially permutes array as follows using stream: draws the new k elements, array[0] to array[k-1],
     * randomly among the old n elements, array[0] to array[n-1], assuming that k <= n <= array.length.
     * In other words, k elements are selected at random without replacement from the first n array 
     * elements and are placed in the first k positions, in random order.
     * @param array the array being shuffled.
     * @param n selection amongst the first n elements.
     * @param k number of elements selected.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (Object[] array, int n, int k, RandomStream stream) {
        // @precondition 0 <= k <= n <= a.length.
        if (k < 0 || k > n) {
            throw new IllegalArgumentException("k must be   0 <= k <= n");
        }
        for (int i = 0; i < k; i++) {
            // Get random j in {i,...,n-1} and interchange a[i] with a[j].
            int j = stream.nextInt(i, n-1);
            Object temp = array[j];
            array[j] = array[i];
            array[i] = temp;
        }
    }
    
    /*
     * Similar to #shuffle shuffle(Object[], n, k, RandomStream).
     * @param array the array being shuffled.
     * @param n selection amongst the first n elements.
     * @param k number of elements selected.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (byte[] array, int n, int k, RandomStream stream) {
        // @precondition 0 <= k <= n <= a.length.
        if (k < 0 || k > n) {
            throw new IllegalArgumentException("k must be   0 <= k <= n");
        }
        for (int i = 0; i < k; i++) {
            // Get random j in {i,...,n-1} and interchange a[i] with a[j].
            int j = stream.nextInt(i, n-1);
            byte temp = array[j];
            array[j] = array[i];
            array[i] = temp;
        }
    }
    
    /*
     * Similar to #shuffle shuffle(Object[], n, k, RandomStream).
     * @param array the array being shuffled.
     * @param n selection amongst the first n elements.
     * @param k number of elements selected.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (short[] array, int n, int k, RandomStream stream) {
        // @precondition 0 <= k <= n <= a.length.
        if (k < 0 || k > n) {
            throw new IllegalArgumentException("k must be   0 <= k <= n");
        }
        for (int i = 0; i < k; i++) {
            // Get random j in {i,...,n-1} and interchange a[i] with a[j].
            int j = stream.nextInt(i, n - 1);
            short temp = array[j];
            array[j] = array[i];
            array[i] = temp;
        }
    }
    
    /*
     * Similar to #shuffle shuffle(Object[], n, k, RandomStream).
     * @param array the array being shuffled.
     * @param n selection amongst the first n elements.
     * @param k number of elements selected.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (int[] array, int n, int k, RandomStream stream) {
        // @precondition 0 <= k <= n <= a.length.
        if (k < 0 || k > n) {
            throw new IllegalArgumentException("k must be   0 <= k <= n");
        }
        for (int i = 0; i < k; i++) {
            // Get random j in {i,...,n-1} and interchange a[i] with a[j].
            int j = stream.nextInt(i, n-1);
            int temp = array[j];
            array[j] = array[i];
            array[i] = temp;
      }
   }
    
    /*
     * Similar to #shuffle shuffle(Object[], n, k, RandomStream).
     * @param array the array being shuffled.
     * @param n selection amongst the first n elements.
     * @param k number of elements selected.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (long[] array, int n, int k, RandomStream stream) {
        // @precondition 0 <= k <= n <= a.length.
        if (k < 0 || k > n) {
            throw new IllegalArgumentException("k must be   0 <= k <= n");
        }
        for (int i = 0; i < k; i++) {
            // Get random j in {i,...,n-1} and interchange a[i] with a[j].
            int j = stream.nextInt(i, n - 1);
            long temp = array[j];
            array[j] = array[i];
            array[i] = temp;
        }
    }
    
    /*
     * Similar to #shuffle shuffle(Object[], n, k, RandomStream).
     * @param array the array being shuffled.
     * @param n selection amongst the first n elements.
     * @param k number of elements selected.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (char[] array, int n, int k, RandomStream stream) {
        // @precondition 0 <= k <= n <= a.length.
        if (k < 0 || k > n) {
            throw new IllegalArgumentException("k must be   0 <= k <= n");
        }
        for (int i = 0; i < k; i++) {
            // Get random j in {i,...,n-1} and interchange a[i] with a[j].
            int j = stream.nextInt(i, n - 1);
            char temp = array[j];
            array[j] = array[i];
            array[i] = temp;
        }
    }
    
    /*
     * Similar to #shuffle shuffle(Object[], n, k, RandomStream).
     * @param array the array being shuffled.
     * @param n selection amongst the first n elements.
     * @param k number of elements selected.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (boolean[] array, int n, int k, RandomStream stream) {
        // @precondition 0 <= k <= n <= a.length.
        if (k < 0 || k > n) {
            throw new IllegalArgumentException("k must be   0 <= k <= n");
        }
        for (int i = 0; i < k; i++) {
            // Get random j in {i,...,n-1} and interchange a[i] with a[j].
            int j = stream.nextInt(i, n-1);
            boolean temp = array[j];
            array[j] = array[i];
            array[i] = temp;
        }
    }
    
    /*
     * Similar to #shuffle shuffle(Object[], n, k, RandomStream).
     * @param array the array being shuffled.
     * @param n selection amongst the first n elements.
     * @param k number of elements selected.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (float[] array, int n, int k, RandomStream stream) {
        // @precondition 0 <= k <= n <= a.length.
        if (k < 0 || k > n) {
            throw new IllegalArgumentException("k must be   0 <= k <= n");
        }
        for (int i = 0; i < k; i++) {
            // Get random j in {i,...,n-1} and interchange a[i] with a[j].
         int j = stream.nextInt(i, n - 1);
         float temp = array[j];
         array[j] = array[i];
         array[i] = temp;
      }
   }
    
    /*
     * Similar to #shuffle shuffle(Object[], n, k, RandomStream).
     * @param array the array being shuffled.
     * @param n selection amongst the first n elements.
     * @param k number of elements selected.
     * @param stream the random stream used to generate random numbers.
     */
    public static void shuffle (double[] array, int n, int k, RandomStream stream) {
        // @precondition 0 <= k <= n <= a.length.
        if (k < 0 || k > n) {
            throw new IllegalArgumentException("k must be   0 <= k <= n");
        }
        for (int i = 0; i < k; i++) {
            // Get random j in {i,...,n-1} and interchange a[i] with a[j].
            int j = stream.nextInt(i, n - 1);
            double temp = array[j];
            array[j] = array[i];
            array[i] = temp;
        }
    }
}