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

/*
 * Array manipulations; complements java.util.Arrays.
 * @see java.util.Arrays
 * @see cern.colt.Sorting
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 03-Jul-99
 */
public class Arrays extends Object {
    
    /*
     * Makes this class non instantiable, but still let's others inherit from it.
     */
    protected Arrays() {}
    
    /*
     * Ensures that a given array can hold up to minCapacity elements. Returns the identical array
     * if it can hold at least the number of elements specified. Otherwise, returns a new array with
     * increased capacity containing the same elements, ensuring that it can hold at least the number
     * of elements specified by the minimum capacity argument.
     * @param   minCapacity   the desired minimum capacity.
     */
    public static byte[] ensureCapacity(byte[] array, int minCapacity) {
        int oldCapacity = array.length;
	byte[] newArray;
	if (minCapacity > oldCapacity) {
            int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            newArray = new byte[newCapacity];
	    System.arraycopy(array, 0, newArray, 0, oldCapacity);
        } else {
            newArray = array;
	}
	return newArray;
    }
    
    /*
     * Ensures that a given array can hold up to minCapacity elements. Returns the identical array
     * if it can hold at least the number of elements specified. Otherwise, returns a new array with
     * increased capacity containing the same elements, ensuring that it can hold at least the number
     * of elements specified by the minimum capacity argument.
     * @param   minCapacity   the desired minimum capacity.
     */
    public static char[] ensureCapacity(char[] array, int minCapacity) {
        int oldCapacity = array.length;
	char[] newArray;
	if (minCapacity > oldCapacity) {
	    int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            newArray = new char[newCapacity];
	    System.arraycopy(array, 0, newArray, 0, oldCapacity);
	} else {
            newArray = array;
	}
	return newArray;
    }
    
    /*
     * Ensures that a given array can hold up to minCapacity elements. Returns the identical array
     * if it can hold at least the number of elements specified. Otherwise, returns a new array with
     * increased capacity containing the same elements, ensuring that it can hold at least the number
     * of elements specified by the minimum capacity argument.
     * @param   minCapacity   the desired minimum capacity.
     */
    public static double[] ensureCapacity(double[] array, int minCapacity) {
	int oldCapacity = array.length;
	double[] newArray;
	if (minCapacity > oldCapacity) {
	    int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            newArray = new double[newCapacity];
	    System.arraycopy(array, 0, newArray, 0, oldCapacity);
	} else {
            newArray = array;
	}
	return newArray;
    }
    
    /*
     * Ensures that a given array can hold up to minCapacity elements. Returns the identical array
     * if it can hold at least the number of elements specified. Otherwise, returns a new array with
     * increased capacity containing the same elements, ensuring that it can hold at least the number
     * of elements specified by the minimum capacity argument.
     * @param   minCapacity   the desired minimum capacity.
     */
    public static float[] ensureCapacity(float[] array, int minCapacity) {
	int oldCapacity = array.length;
	float[] newArray;
	if (minCapacity > oldCapacity) {
	    int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            newArray = new float[newCapacity];
	    System.arraycopy(array, 0, newArray, 0, oldCapacity);
	} else {
            newArray = array;
	}
	return newArray;
    }
    
    /*
     * Ensures that a given array can hold up to minCapacity elements. Returns the identical array
     * if it can hold at least the number of elements specified. Otherwise, returns a new array with
     * increased capacity containing the same elements, ensuring that it can hold at least the number
     * of elements specified by the minimum capacity argument.
     * @param   minCapacity   the desired minimum capacity.
     */
    public static int[] ensureCapacity(int[] array, int minCapacity) {
	int oldCapacity = array.length;
	int[] newArray;
	if (minCapacity > oldCapacity) {
	    int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }		
	    newArray = new int[newCapacity];
	    System.arraycopy(array, 0, newArray, 0, oldCapacity);
	} else {
            newArray = array;
	}
	return newArray;
    }
    
    /*
     * Ensures that a given array can hold up to minCapacity elements. Returns the identical array
     * if it can hold at least the number of elements specified. Otherwise, returns a new array with
     * increased capacity containing the same elements, ensuring that it can hold at least the number
     * of elements specified by the minimum capacity argument.
     * @param   minCapacity   the desired minimum capacity.
     */
    public static long[] ensureCapacity(long[] array, int minCapacity) {
	int oldCapacity = array.length;
	long[] newArray;
	if (minCapacity > oldCapacity) {
	    int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            newArray = new long[newCapacity];
	    System.arraycopy(array, 0, newArray, 0, oldCapacity);
	} else {
            newArray = array;
	}
	return newArray;
    }
    
    /*  
     * Ensures that a given array can hold up to minCapacity elements. Returns the identical array
     * if it can hold at least the number of elements specified. Otherwise, returns a new array with
     * increased capacity containing the same elements, ensuring that it can hold at least the number
     * of elements specified by the minimum capacity argument.
     * @param   minCapacity   the desired minimum capacity.
     */
    public static Object[] ensureCapacity(Object[] array, int minCapacity) {
	int oldCapacity = array.length;
	Object[] newArray;
	if (minCapacity > oldCapacity) {
	    int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            newArray = new Object[newCapacity];
	    System.arraycopy(array, 0, newArray, 0, oldCapacity);
	} else {
            newArray = array;
	}
	return newArray;
    }
    
    /*
     * Ensures that a given array can hold up to minCapacitY elements. Returns the identical array
     * if it can hold at least the number of elements specified. Otherwise, returns a new array with
     * increased capacity containing the same elements, ensuring that it can hold at least the number
     * of elements specified by the minimum capacity argument.
     * @param   minCapacity   the desired minimum capacity.
     */
    public static short[] ensureCapacity(short[] array, int minCapacity) {
	int oldCapacity = array.length;
	short[] newArray;
	if (minCapacity > oldCapacity) {
	    int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            newArray = new short[newCapacity];
	    System.arraycopy(array, 0, newArray, 0, oldCapacity);
	} else {
            newArray = array;
	}
	return newArray;
    }
    
    /*
     * Ensures that a given array can hold up to minCapacitY elements. Returns the identical array
     * if it can hold at least the number of elements specified. Otherwise, returns a new array with
     * increased capacity containing the same elements, ensuring that it can hold at least the number
     * of elements specified by the minimum capacity argument.
     * @param   minCapacity   the desired minimum capacity.
     */
    public static boolean[] ensureCapacity(boolean[] array, int minCapacity) {
	int oldCapacity = array.length;
	boolean[] newArray;
	if (minCapacity > oldCapacity) {
	    int newCapacity = (oldCapacity * 3)/2 + 1;
            if (newCapacity < minCapacity) {
		newCapacity = minCapacity;
            }
            newArray = new boolean[newCapacity];
	    System.arraycopy(array, 0, newArray, 0, oldCapacity);
	} else {
            newArray=array;
	}
	return newArray;
    }
    
    /*
     * Returns a string representation of the specified array. The string representation consists
     * of a list of the arrays's elements, enclosed in square brackets ("[]").  Adjacent elements
     * are separated by the characters ", " (comma and space).
     * @return a string representation of the specified array.
     */
    public static String toString(byte[] array) {
	StringBuffer buf = new StringBuffer();
	buf.append("[");
	int maxIndex = array.length - 1;
	for (int i = 0; i <= maxIndex; i++) {
	    buf.append(array[i]);
	    if (i < maxIndex) {
                buf.append(", ");
            }
	}
	buf.append("]");
	return buf.toString();
    }
    
    /*
     * Returns a string representation of the specified array. The string representation consists
     * of a list of the arrays's elements, enclosed in square brackets ("[]").  Adjacent elements
     * are separated by the characters ", " (comma and space).
     * @return a string representation of the specified array.
     */
    public static String toString(char[] array) {
	StringBuffer buf = new StringBuffer();
	buf.append("[");
	int maxIndex = array.length - 1;
	for (int i = 0; i <= maxIndex; i++) {
	    buf.append(array[i]);
	    if (i < maxIndex) { 
                buf.append(", ");
            }
	}
	buf.append("]");
	return buf.toString();
    }
    
    /*
     * Returns a string representation of the specified array. The string representation consists
     * of a list of the arrays's elements, enclosed in square brackets ("[]").  Adjacent elements
     * are separated by the characters ", " (comma and space).
     * @return a string representation of the specified array.
     */
    public static String toString(double[] array) {
	StringBuffer buf = new StringBuffer();
	buf.append("[");
	int maxIndex = array.length - 1;
	for (int i = 0; i <= maxIndex; i++) {
	    buf.append(array[i]);
	    if (i < maxIndex) {
		buf.append(", ");
            }
	}
	buf.append("]");
	return buf.toString();
    }
    
    /*
     * Returns a string representation of the specified array. The string representation consists
     * of a list of the arrays's elements, enclosed in square brackets ("[]").  Adjacent elements
     * are separated by the characters ", " (comma and space).
     * @return a string representation of the specified array.
     */
    public static String toString(float[] array) {
	StringBuffer buf = new StringBuffer();
	buf.append("[");
	int maxIndex = array.length - 1;
	for (int i = 0; i <= maxIndex; i++) {
	    buf.append(array[i]);
	    if (i < maxIndex) {
		buf.append(", ");
            }
	}
	buf.append("]");
	return buf.toString();
    }
    
    /*
     * Returns a string representation of the specified array. The string representation consists
     * of a list of the arrays's elements, enclosed in square brackets ("[]").  Adjacent elements
     * are separated by the characters ", " (comma and space).
     * @return a string representation of the specified array.
     */
    public static String toString(int[] array) {
	StringBuffer buf = new StringBuffer();
	buf.append("[");
	int maxIndex = array.length - 1;
	for (int i = 0; i <= maxIndex; i++) {
	    buf.append(array[i]);
	    if (i < maxIndex) {
		buf.append(", ");
            }
	}
	buf.append("]");
	return buf.toString();
    }
    
    /*
     * Returns a string representation of the specified array. The string representation consists
     * of a list of the arrays's elements, enclosed in square brackets ("[]").  Adjacent elements
     * are separated by the characters ", " (comma and space).
     * @return a string representation of the specified array.
     */
    public static String toString(long[] array) {
	StringBuffer buf = new StringBuffer();
	buf.append("[");
	int maxIndex = array.length - 1;
	for (int i = 0; i <= maxIndex; i++) {
	    buf.append(array[i]);
	    if (i < maxIndex) {
		buf.append(", ");
            }
	}
	buf.append("]");
	return buf.toString();
    }
    
    /*
     * Returns a string representation of the specified array. The string representation consists
     * of a list of the arrays's elements, enclosed in square brackets ("[]").  Adjacent elements
     * are separated by the characters ", " (comma and space).
     * @return a string representation of the specified array.
     */
    public static String toString(Object[] array) {
	StringBuffer buf = new StringBuffer();
	buf.append("[");
	int maxIndex = array.length - 1;
	for (int i = 0; i <= maxIndex; i++) {
	    buf.append(array[i]);
	    if (i < maxIndex) {
		buf.append(", ");
            }
	}
	buf.append("]");
	return buf.toString();
    }
    
    /*
     * Returns a string representation of the specified array. The string representation consists
     * of a list of the arrays's elements, enclosed in square brackets ("[]").  Adjacent elements
     * are separated by the characters ", " (comma and space).
     * @return a string representation of the specified array.
     */
    public static String toString(short[] array) {
	StringBuffer buf = new StringBuffer();
	buf.append("[");
	int maxIndex = array.length - 1;
	for (int i = 0; i <= maxIndex; i++) {
	    buf.append(array[i]);
	    if (i < maxIndex) {
		buf.append(", ");
            }
	}
	buf.append("]");
	return buf.toString();
    }
    
    /*
     * Returns a string representation of the specified array. The string representation consists
     * of a list of the arrays's elements, enclosed in square brackets ("[]").  Adjacent elements
     * are separated by the characters ", " (comma and space).
     * @return a string representation of the specified array.
     */
    public static String toString(boolean[] array) {
	StringBuffer buf = new StringBuffer();
	buf.append("[");
	int maxIndex = array.length - 1;
	for (int i = 0; i <= maxIndex; i++) {
	    buf.append(array[i]);
	    if (i < maxIndex) {
		buf.append(", ");
            }
	}
	buf.append("]");
	return buf.toString();
    }
    
    /*
     * Ensures that the specified array cannot hold more than maxCapacity elements. An application
     * can use this operation to minimize array storage. Returns the identical array if 
     * array.length <= maxCapacity. Otherwise, returns a new array with a length of maxCapacity
     * containing the first maxCapacity elements of array.
     * @param   maxCapacity   the desired maximum capacity.
     */
    public static byte[] trimToCapacity(byte[] array, int maxCapacity) {
	if (array.length > maxCapacity) {
	    byte oldArray[] = array;
	    array = new byte[maxCapacity];
	    System.arraycopy(oldArray, 0, array, 0, maxCapacity);
	}
	return array;
    }
    
    /*
     * Ensures that the specified array cannot hold more than maxCapacity elements. An application
     * can use this operation to minimize array storage. Returns the identical array if 
     * array.length <= maxCapacity. Otherwise, returns a new array with a length of maxCapacity
     * containing the first maxCapacity elements of array.
     * @param   maxCapacity   the desired maximum capacity.
     */
    public static char[] trimToCapacity(char[] array, int maxCapacity) {
	if (array.length > maxCapacity) {
	    char oldArray[] = array;
	    array = new char[maxCapacity];
	    System.arraycopy(oldArray, 0, array, 0, maxCapacity);
	}
	return array;
    }
    
    /*
     * Ensures that the specified array cannot hold more than maxCapacity elements. An application
     * can use this operation to minimize array storage. Returns the identical array if 
     * array.length <= maxCapacity. Otherwise, returns a new array with a length of maxCapacity
     * containing the first maxCapacity elements of array.
     * @param   maxCapacity   the desired maximum capacity.
     */
    public static double[] trimToCapacity(double[] array, int maxCapacity) {
	if (array.length > maxCapacity) {
	    double oldArray[] = array;
	    array = new double[maxCapacity];
	    System.arraycopy(oldArray, 0, array, 0, maxCapacity);
	}
	return array;
    }
    
    /*
     * Ensures that the specified array cannot hold more than maxCapacity elements. An application
     * can use this operation to minimize array storage. Returns the identical array if 
     * array.length <= maxCapacity. Otherwise, returns a new array with a length of maxCapacity
     * containing the first maxCapacity elements of array.
     * @param   maxCapacity   the desired maximum capacity.
     */
    public static float[] trimToCapacity(float[] array, int maxCapacity) {
	if (array.length > maxCapacity) {
	    float oldArray[] = array;
	    array = new float[maxCapacity];
	    System.arraycopy(oldArray, 0, array, 0, maxCapacity);
	}
	return array;
    }
    
    /*
     * Ensures that the specified array cannot hold more than maxCapacity elements. An application
     * can use this operation to minimize array storage. Returns the identical array if 
     * array.length <= maxCapacity. Otherwise, returns a new array with a length of maxCapacity
     * containing the first maxCapacity elements of array.
     * @param   maxCapacity   the desired maximum capacity.
     */
    public static int[] trimToCapacity(int[] array, int maxCapacity) {
	if (array.length > maxCapacity) {
	    int oldArray[] = array;
	    array = new int[maxCapacity];
	    System.arraycopy(oldArray, 0, array, 0, maxCapacity);
	}
	return array;
    }
    
    /*
     * Ensures that the specified array cannot hold more than maxCapacity elements. An application
     * can use this operation to minimize array storage. Returns the identical array if 
     * array.length <= maxCapacity. Otherwise, returns a new array with a length of maxCapacity
     * containing the first maxCapacity elements of array.
     * @param   maxCapacity   the desired maximum capacity.
     */
    public static long[] trimToCapacity(long[] array, int maxCapacity) {
	if (array.length > maxCapacity) {
	    long oldArray[] = array;
	    array = new long[maxCapacity];
	    System.arraycopy(oldArray, 0, array, 0, maxCapacity);
	}
	return array;
    }
    
    /*
     * Ensures that the specified array cannot hold more than maxCapacity elements. An application
     * can use this operation to minimize array storage. Returns the identical array if 
     * array.length <= maxCapacity. Otherwise, returns a new array with a length of maxCapacity
     * containing the first maxCapacity elements of array.
     * @param   maxCapacity   the desired maximum capacity.
     */
    public static Object[] trimToCapacity(Object[] array, int maxCapacity) {
	if (array.length > maxCapacity) {
	    Object oldArray[] = array;
	    array = new Object[maxCapacity];
	    System.arraycopy(oldArray, 0, array, 0, maxCapacity);
	}
	return array;
    }
    
    /*
     * Ensures that the specified array cannot hold more than maxCapacity elements. An application
     * can use this operation to minimize array storage. Returns the identical array if 
     * array.length <= maxCapacity. Otherwise, returns a new array with a length of maxCapacity
     * containing the first maxCapacity elements of array.
     * @param   maxCapacity   the desired maximum capacity.
     */
    public static short[] trimToCapacity(short[] array, int maxCapacity) {
	if (array.length > maxCapacity) {
	    short oldArray[] = array;
	    array = new short[maxCapacity];
	    System.arraycopy(oldArray, 0, array, 0, maxCapacity);
	}
	return array;
    }
    
    /*
     * Ensures that the specified array cannot hold more than maxCapacity elements. An application
     * can use this operation to minimize array storage. Returns the identical array if 
     * array.length <= maxCapacity. Otherwise, returns a new array with a length of maxCapacity
     * containing the first maxCapacity elements of array.
     * @param   maxCapacity   the desired maximum capacity.
     */
    public static boolean[] trimToCapacity(boolean[] array, int maxCapacity) {
	if (array.length > maxCapacity) {
	    boolean oldArray[] = array;
	    array = new boolean[maxCapacity];
	    System.arraycopy(oldArray, 0, array, 0, maxCapacity);
	}
	return array;
    }
}