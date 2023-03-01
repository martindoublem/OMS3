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

public class DoubleArrayList extends AbstractDoubleList {
    protected double[] elements;
    
    public DoubleArrayList() {
	this(10);
    }
    
    public DoubleArrayList(double[] elements) {
	elements(elements);
    }
    
    public DoubleArrayList(int initialCapacity) {
	this(new double[initialCapacity]);
	setSizeRaw(0);
    }
    
    public void add(double element) {
	// overridden for performance only.  
	if (size == elements.length) {
            ensureCapacity(size + 1);
        } 
	elements[size++] = element;
    }
    
    public void beforeInsert(int index, double element) {
	// overridden for performance only.
	if (size == index) {
            add(element);
            return;
	}
	if (index > size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
	ensureCapacity(size + 1);
	System.arraycopy(elements, index, elements, index + 1, size - index);
	elements[index] = element;
	size++;
    }
    
    public int binarySearchFromTo(double key, int from, int to) {
	return Sorting.binarySearchFromTo(this.elements,key,from,to);
    }
    
    public Object clone() {
	// overridden for performance only.
	DoubleArrayList clone = new DoubleArrayList((double[]) elements.clone());
	clone.setSizeRaw(size);
	return clone;
    }
    
    public DoubleArrayList copy() {
	return (DoubleArrayList) clone();
    }
    
    public double[] elements() {
	return elements;
    }
    
    public AbstractDoubleList elements(double[] elements) {
	this.elements = elements;
	this.size = elements.length;
	return this;
    }
    
    public void ensureCapacity(int minCapacity) {
        elements = Arrays.ensureCapacity(elements, minCapacity);
    }
    
    public boolean equals(Object otherObj) { //delta
	// overridden for performance only.
	if ( !(otherObj instanceof DoubleArrayList) ) {
            return super.equals(otherObj);
        }
	if (this == otherObj) {
            return true;
        }
	if (otherObj == null) {
            return false;
        }
	DoubleArrayList other = (DoubleArrayList) otherObj;
	if (size() != other.size()) {
            return false;
        }
        double[] theElements = elements();
	double[] otherElements = other.elements();
	for (int i = size(); --i >= 0; ) {
	    if (theElements[i] != otherElements[i]){
                return false;
            }
	}
	return true;
    }
    
    public boolean forEach(DoubleProcedure procedure) {
	// overridden for performance only.
	double[] theElements = elements;
	int theSize = size;
        for (int i = 0; i < theSize;) {
            if ( !procedure.apply(theElements[i++]) ) {
                return false;
            }
        }
	return true;
    }
    
    public double get(int index) {
	// overridden for performance only.
	if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
	return elements[index];
    }
    
    public double getQuick(int index) {
	return elements[index];
    }
    
    public int indexOfFromTo(double element, int from, int to) {
	// overridden for performance only.
	if (size == 0) {
            return -1;
        }
	checkRangeFromTo(from, to, size);
        double[] theElements = elements;
	for (int i = from ; i <= to; i++) {
            if (element == theElements[i]) {
                return i;
            } //found
	}
	return -1; //not found
    }
    
    public int lastIndexOfFromTo(double element, int from, int to) {
	// overridden for performance only.
	if (size == 0) {
            return -1;
        }
	checkRangeFromTo(from, to, size);
        double[] theElements = elements;
	for (int i = to ; i >= from; i--) {
	    if (element == theElements[i]) {
                return i;
            } //found
	}
	return -1; //not found
    }
    
    public AbstractDoubleList partFromTo(int from, int to) {
	if (size == 0) {
            return new DoubleArrayList(0);
        }
        checkRangeFromTo(from, to, size);
        double[] part = new double[to - from + 1];
	System.arraycopy(elements, from, part, 0, to - from + 1);
	return new DoubleArrayList(part);
    }
    
    public boolean removeAll(AbstractDoubleList other) {
	// overridden for performance only.
	if ( !(other instanceof DoubleArrayList) ) {
            return super.removeAll(other);
        }
	
	/*
         * There are two possibilities to do the thing
         * a) use other.indexOf(...)
	 * b) sort other, then use other.binarySearch(...)
         * Let's try to figure out which one is faster. Let M=size, N=other.size, then
         * a) takes O(M*N) steps
         * b) takes O(N*logN + M*logN) steps (sorting is O(N*logN) and binarySearch is O(logN))
         * Hence, if N*logN + M*logN < M*N, we use b) otherwise we use a).
         */
	if (other.size() == 0) {
            return false;
        } //nothing to do
	int j = 0;
	double[] theElements = elements;
	double N = (double) other.size();
	double M = (double) size();
	if ( (N + M) * Arithmetic.log2(N) < M * N ) {
            // it is faster to sort other before searching in it
            DoubleArrayList sortedList = (DoubleArrayList) other.clone();
            sortedList.quickSort();
            for (int i = 0; i < size() ; i++) {
                if (sortedList.binarySearchFromTo(theElements[i], 0, other.size() - 1) < 0) {
                    theElements[j++] = theElements[i];
                }
            }
	} else {
            // it is faster to search in other without sorting
            for (int i = 0; i < size() ; i++) {
                if (other.indexOfFromTo(theElements[i], 0, other.size() - 1) < 0) {
                    theElements[j++] = theElements[i];
                }
            }
	}
        boolean modified = (j != size());
	setSize(j);
	return modified;
    }
    
    public void replaceFromToWithFrom(int from, int to, AbstractDoubleList other, int otherFrom) {
	// overridden for performance only.
	if ( !(other instanceof DoubleArrayList) ) {
            // slower
            super.replaceFromToWithFrom(from, to, other, otherFrom);
            return;
	}
	int length = to - from + 1;
	if (length > 0) {
            checkRangeFromTo(from, to, size());
            checkRangeFromTo(otherFrom, otherFrom + length - 1, other.size());
            System.arraycopy(((DoubleArrayList) other).elements, otherFrom, elements, from, length);
	}
    }
    
    public boolean retainAll(AbstractDoubleList other) {
	// overridden for performance only.
	if ( !(other instanceof DoubleArrayList)){
            return super.retainAll(other);
        }
        /*
         * There are two possibilities to do the thing
	 * a) use other.indexOf(...)
	 * b) sort other, then use other.binarySearch(...)
	 * Let's try to figure out which one is faster. Let M=size, N=other.size, then
	 * a) takes O(M*N) steps
	 * b) takes O(N*logN + M*logN) steps (sorting is O(N*logN) and binarySearch is O(logN))
         * Hence, if N*logN + M*logN < M*N, we use b) otherwise we use a).
        */
	int j=0;
	double[] theElements = elements;
	double N = (double) other.size();
	double M = (double) size();
	if ( (N + M) * Arithmetic.log2(N) < M * N ) {
            // it is faster to sort other before searching in it
            DoubleArrayList sortedList = (DoubleArrayList) other.clone();
            sortedList.quickSort();
            for (int i = 0; i < size(); i++) {
                if (sortedList.binarySearchFromTo(theElements[i], 0, other.size() - 1) >= 0) {
                    theElements[j++] = theElements[i];
                }
            }
        } else {
            // it is faster to search in other without sorting
            for (int i = 0; i < size(); i++) {
                if (other.indexOfFromTo(theElements[i], 0, other.size() - 1) >= 0) {
                    theElements[j++] = theElements[i];
                }
            }
	}
        boolean modified = (j != size());
	setSize(j);
	return modified;
    }
    
    public void reverse() {
	// overridden for performance only.
	double tmp;
	int j = size - 1;
        double[] theElements = elements;
	for (int i = 0; i< size / 2;) { //swap
            tmp = theElements[i];
            theElements[i++] = theElements[j];
            theElements[j--] = tmp;
	}
    }
    
    public void set(int index, double element) {
	// overridden for performance only.
	if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
	elements[index] = element;
    }
    
    public void setQuick(int index, double element) {
	elements[index] = element;
    }
    
    public void shuffleFromTo(int from, int to) {
	// overridden for performance only.
	if (size==0) {
            return;
        }
	checkRangeFromTo(from, to, size);
        Uniform gen = new Uniform(new DRand(new java.util.Date()));
	int random;
        double tmpElement;
	double[] theElements = elements;
	for (int i=from; i<to; i++) {
            random = gen.nextIntFromTo(i, to);
            tmpElement = theElements[random];
            theElements[random] = theElements[i];
            theElements[i] = tmpElement; 
	}  
    }
    
    public void trimToSize() {
	elements = Arrays.trimToCapacity(elements,size());
    }
}