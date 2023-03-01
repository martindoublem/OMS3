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
 * Abstract base class for resizable lists holding float elements; abstract.
 * First see the package summary and javadoc tree view to get the broad picture.
 */
public abstract class AbstractFloatList extends AbstractList {
    
    /*
     * The size of the list. This is a READ_ONLY variable for all methods but setSizeRaw(int newSize)!
     * If you violate this principle in subclasses, you should exactly know what you are doing.
     * @serial
     */
    protected int size;
    
    /*
     * Makes this class non instantiable, but still let's others inherit from it.
     */
    protected AbstractFloatList() {}
    
    /*
     * Appends the specified element to the end of this list.
     * @param element element to be appended to this list.
     */
    public void add(float element) {
	beforeInsert(size, element);
    }
    
    /*
     * Appends the part of the specified list between from (inclusive) and to (inclusive)
     * to the receiver.
     * @param other the list to be added to the receiver.
     * @param from the index of the first element to be appended (inclusive).
     * @param to the index of the last element to be appended (inclusive).
     * @exception IndexOutOfBoundsException index is out of range (other.size() > 0 &&
     *      (from < 0 || from > to || to >= other.size())).
     */
    public void addAllOfFromTo(AbstractFloatList other, int from, int to) {
	beforeInsertAllOfFromTo(size, other, from, to);
    }
    
    /*
     * Inserts the specified element before the specified position into the receiver. Shifts
     * the element currently at that position (if any) and any subsequent elements to the right.
     * @param index index before which the specified element is to be inserted (must be in [0,size]).
     * @param element element to be inserted.
     * @exception IndexOutOfBoundsException index is out of range (index < 0 || index > size()).
     */
    public void beforeInsert(int index, float element) {
	beforeInsertDummies(index, 1);
	set(index, element);
    }
    
    /*
     * Inserts the part of the specified list between otherFrom (inclusive) and otherTo (inclusive)
     * before the specified position into the receiver. Shifts the element currently at that
     * position (if any) and any subsequent elements to the right.
     * @param index index before which to insert first element from the specified list
     *      (must be in [0,size])..
     * @param other list of which a part is to be inserted into the receiver.
     * @param from the index of the first element to be inserted (inclusive).
     * @param to the index of the last element to be inserted (inclusive).
     * @exception IndexOutOfBoundsException index is out of range (other.size() > 0 &&
     * (from < 0 || from > to || to >= other.size())).
     * @exception IndexOutOfBoundsException index is out of range (index < 0 || index > size()).
     */
    public void beforeInsertAllOfFromTo(int index, AbstractFloatList other, int from, int to) {
	int length = to - from + 1;
	this.beforeInsertDummies(index, length);
	this.replaceFromToWithFrom(index, index + length - 1, other, from);
    }
    
    /*
     * Inserts length dummy elements before the specified position into the receiver. Shifts
     * the element currently at that position (if any) and any subsequent elements to the right.
     * This method must set the new size to be size() + length.
     * @param index index before which to insert dummy elements (must be in [0, size])..
     * @param length number of dummy elements to be inserted.
     * @throws IndexOutOfBoundsException if index < 0 || index > size().
     */
    protected void beforeInsertDummies(int index, int length) {
	if (index > size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
	if (length > 0) {
            ensureCapacity(size + length);
            setSizeRaw(size + length);
            replaceFromToWithFrom(index + length, size - 1, this, index);
	}
    }
    
    /*
     * Searches the receiver for the specified value using the binary search algorithm. The 
     * receiver must be sorted (as by the sort method) prior to making this call. If it is 
     * not sorted, the results are undefined: in particular, the call may enter an infinite
     * loop. If the receiver contains multiple elements equal to the specified object, there
     * is no guarantee which instance will be found.
     * @param key the value to be searched for.
     * @return index of the search key, if it is contained in the receiver; otherwise, 
     *      (-(insertion point) - 1).  The insertion point is defined as the the point at 
     *      which the value would be inserted into the receiver: the index of the first
     *	    element greater than the key, or receiver.size(), if all elements in the 
     *      receiver are less than the specified key. Note that this guarantees that the
     *      return value will be >= 0 if and only if the key is found.
     * @see java.util.Arrays
     */
    public int binarySearch(float key) {
	return this.binarySearchFromTo(key, 0, size - 1);
    }
    
    /*
     * Searches the receiver for the specified value using the binary search algorithm. The
     * receiver must be sorted (as by the sort method) prior to making this call. If it is not
     * sorted, the results are undefined: in particular, the call may enter an infinite loop.
     * If the receiver contains multiple elements equal to the specified object, there is no
     * guarantee which instance will be found.
     * @param key the value to be searched for.
     * @param from the leftmost search position, inclusive.
     * @param to the rightmost search position, inclusive.
     * @return index of the search key, if it is contained in the receiver; otherwise, 
     *      (-(insertion point) - 1). The insertion point is defined as the the point at which
     *      the value would be inserted into the receiver: the index of the first element 
     *      greater than the key, or receiver.size(), if all elements in the receiver are less
     *      than the specified key. Note that this guarantees that the return value will be
     *      >= 0 if and only if the key is found.
     * @see java.util.Arrays
     */
    public int binarySearchFromTo(float key, int from, int to) {
	int low = from;
	int high = to;
	while (low <= high) {
            int mid =(low + high) / 2;
            float midVal = get(mid);
            if (midVal < key) {
                low = mid + 1;
            } else if (midVal > key) {
                high = mid - 1;
            } else {
                return mid;
            } // key found
	}
	return -(low + 1);  // key not found.
    }
    
    /*
     * @return  a deep copy of the receiver.
     */
    public Object clone() {
	return partFromTo(0, size - 1);
    }
    
    /*
     * Returns true if the receiver contains the specified element.
     * @param element element whose presence in the receiver is to be tested.
     */
    public boolean contains(float elem) {
	return indexOfFromTo(elem, 0, size - 1) >= 0;
    }
    
    /*
     * Deletes the first element from the receiver that is identical to the specified element.
     * Does nothing, if no such matching element is contained.
     * @param element the element to be deleted.
     */
    public void delete(float element) {
	int index = indexOfFromTo(element, 0, size - 1);
	if (index >= 0) {
            remove(index);
        }
    }
    
    /*
     * Returns the elements currently stored, possibly including invalid elements between size
     * and capacity. WARNING: For efficiency reasons and to keep memory usage low, this method
     * may decide not to copy the array. So if subsequently you modify the returned array
     * directly via the [] operator, be sure you know what you're doing.
     * @return the elements currently stored.
     */
    public float[] elements() {
	float[] myElements = new float[size];
	for (int i = size; --i >= 0; ) {
            myElements[i] = getQuick(i);
        }
	return myElements;
    }
    
    /*
     * Sets the receiver's elements to be the specified array. The size and capacity of the
     * list is the length of the array. WARNING: For efficiency reasons and to keep memory
     * usage low, this method may decide not to copy the array. So  if subsequently you modify
     * the returned array directly via the [] operator, be sure you know what you're doing.
     * @param elements the new elements to be stored.
     * @return the receiver itself.
     */
    public AbstractFloatList elements(float[] elements) {
	clear();
	addAllOfFromTo(new FloatArrayList(elements), 0, elements.length - 1);
	return this;
    }
    
    /*
     * Ensures that the receiver can hold at least the specified number of elements without
     * needing to allocate new internal memory. If necessary, allocates new internal memory
     * and increases the capacity of the receiver.
     * @param   minCapacity   the desired minimum capacity.
     */
    public abstract void ensureCapacity(int minCapacity);
    
    /*
     * Compares the specified Object with the receiver. Returns true if and only if the 
     * specified Object is also an ArrayList of the same type, both Lists have the same 
     * size, and all corresponding pairs of elements in the two Lists are identical. In 
     * other words, two Lists are defined to be equal if they contain the same  elements
     * in the same order.
     * @param otherObj the Object to be compared for equality with the receiver.
     * @return true if the specified Object is equal to the receiver.
     */
    public boolean equals(Object otherObj) { //delta
	if (! (otherObj instanceof AbstractFloatList)) {
            return false;
        }
	if (this == otherObj) {
            return true;
        }
	if (otherObj == null) {
            return false;
        }
	AbstractFloatList other = (AbstractFloatList) otherObj;
	if (size() != other.size()) {
            return false;
        }
        for (int i = size(); --i >= 0; ) {
	    if (getQuick(i) != other.getQuick(i)) { 
                return false;
            }
	}
	return true;
    }
    
    /*
     * Sets the specified range of elements in the specified array to the specified value.
     * @param from the index of the first element (inclusive) to be filled with the specified value.
     * @param to the index of the last element (inclusive) to be filled with the specified value.
     * @param val the value to be stored in the specified elements of the receiver.
     */
    public void fillFromToWith(int from, int to, float val) {
	checkRangeFromTo(from, to, this.size);
	for (int i = from; i <= to; ) {
            setQuick(i++,val);
        } 
    }
    
    /*
     * Applies a procedure to each element of the receiver, if any. Starts at index 0, moving rightwards.
     * @param procedure    the procedure to be applied. Stops iteration if the procedure returns false,
     *      otherwise continues. 
     * @return false if the procedure stopped before all elements where iterated over, true otherwise. 
     */
    public boolean forEach(FloatProcedure procedure) {
	for (int i = 0; i < size;) {
            if ( !procedure.apply(get(i++))) {
                return false;
            }
        }
	return true;
    }
    
    /*
     * Returns the element at the specified position in the receiver.
     * @param index index of element to return.
     * @exception IndexOutOfBoundsException index is out of range (index < 0 || index >= size()).
     */
    public float get(int index) {
	if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
	return getQuick(index);
    }
    
    /*
     * Returns the element at the specified position in the receiver; WARNING: Does not check
     * preconditions. Provided with invalid parameters this method may return invalid elements
     * without throwing any exception! You should only use this method when you are absolutely
     * sure that the index is within bounds. Precondition (unchecked): index >= 0 && index < size().
     * This method is normally only used internally in large loops where bounds are explicitly
     * checked before the loop and need no be rechecked within the loop. However, when 
     * desperately, you can give this method public visibility in subclasses.
     * @param index index of element to return.
     */
    protected abstract float getQuick(int index);
    
    /*
     * Returns the index of the first occurrence of the specified element. Returns -1 if the
     * receiver does not contain this element.
     * @param   element  the element to be searched for.
     * @return  the index of the first occurrence of the element in the receiver; returns -1
     *      if the element is not found.
     */
    public int indexOf(float element) { //delta
	return indexOfFromTo(element, 0, size - 1);
    }
    
    /*
     * Returns the index of the first occurrence of the specified element. Returns -1 if the
     * receiver does not contain this element. Searches between from, inclusive and to, inclusive.
     * Tests for identity.
     * @param element element to search for.
     * @param from the leftmost search position, inclusive.
     * @param to the rightmost search position, inclusive.
     * @return  the index of the first occurrence of the element in the receiver; returns -1
     *      if the element is not found.
     * @exception IndexOutOfBoundsException index is out of range (size() > 0 && 
     *      (from < 0 || from > to || to >= size())).
     */
    public int indexOfFromTo(float element, int from, int to) {
	checkRangeFromTo(from, to, size);
        for (int i = from ; i <= to; i++) {
	    if (element == getQuick(i)) {
                return i;
            } //found
	}
	return -1; //not found
    }
    
    /*
     * Returns the index of the last occurrence of the specified element. Returns -1 if the 
     * receiver does not contain this element.
     * @param   element   the element to be searched for.
     * @return  the index of the last occurrence of the element in the receiver; returns -1
     *      if the element is not found.
     */
    public int lastIndexOf(float element) {
	return lastIndexOfFromTo(element, 0, size - 1);
    }
    
    /*
     * Returns the index of the last occurrence of the specified element. Returns -1 if the
     * receiver does not contain this element. Searches beginning at to, inclusive until from,
     * inclusive. Tests for identity.
     * @param element element to search for.
     * @param from the leftmost search position, inclusive.
     * @param to the rightmost search position, inclusive.
     * @return  the index of the last occurrence of the element in the receiver; returns -1
     *      if the element is not found.
     * @exception IndexOutOfBoundsException index is out of range (size() > 0 &&
     *      (from < 0 || from > to || to >= size())).
     */
    public int lastIndexOfFromTo(float element, int from, int to) {
	checkRangeFromTo(from, to, size());
        for (int i = to ; i >= from; i--) {
	    if (element == getQuick(i)) {
                return i;
            } //found
	}
	return -1; //not found
    }
    
    /*
     * Sorts the specified range of the receiver into ascending order. The sorting algorithm is
     * a modified mergesort (in which the merge is omitted if the highest element in the low 
     * sublist is less than the lowest element in the high sublist). This algorithm offers 
     * guaranteed n*log(n) performance, and can approach linear performance on nearly sorted lists.
     * You should never call this method unless you are sure that this particular sorting algorithm
     * is the right one for your data set. It is generally better to call sort() or sortFromTo(...)
     * instead, because those methods automatically choose the best sorting algorithm.
     * @param from the index of the first element (inclusive) to be sorted.
     * @param to the index of the last element (inclusive) to be sorted.
     * @exception IndexOutOfBoundsException index is out of range (size() > 0 && 
     *      (from < 0 || from > to || to >= size())).
     */
    public void mergeSortFromTo(int from, int to) {
	int mySize = size();
	checkRangeFromTo(from, to, mySize);
        float[] myElements = elements();
	Sorting.mergeSort(myElements, from, to + 1);
	elements(myElements);
	setSizeRaw(mySize);
    }
    
    /*
     * Sorts the receiver according to the order induced by the specified comparator. All elements
     * in the range must be mutually comparable by the specified comparator (that is, 
     * c.compare(e1, e2) must not throw a ClassCastException for any elements e1 and e2 in the range).
     * This sort is guaranteed to be stable: equal elements will not be reordered as a result of the
     * sort. The sorting algorithm is a modified mergesort (in which the merge is omitted if the
     * highest element in the low sublist is less than the lowest element in the high sublist). This
     * algorithm offers guaranteed n*log(n) performance, and can approach linear performance on nearly
     * sorted lists.
     * @param from the index of the first element (inclusive) to be sorted.
     * @param to the index of the last element (inclusive) to be sorted.
     * @param c the comparator to determine the order of the receiver.
     * @throws ClassCastException if the array contains elements that are not mutually comparable using
     *      the specified comparator.
     * @throws IllegalArgumentException if fromIndex > toIndex
     * @throws ArrayIndexOutOfBoundsException if fromIndex < 0 or toIndex > a.length
     * @see Comparator
     * @exception IndexOutOfBoundsException index is out of range (size() > 0 &&
     *      (from < 0 || from > to || to >= size())).
     */
    public void mergeSortFromTo(int from, int to, FloatComparator c) {
	int mySize = size();
	checkRangeFromTo(from, to, mySize);
        float[] myElements = elements();
	Sorting.mergeSort(myElements, from, to + 1, c);
	elements(myElements);
	setSizeRaw(mySize);
    }
    
    /*
     * Returns a new list of the part of the receiver between from, inclusive, and to, inclusive.
     * @param from the index of the first element (inclusive).
     * @param to the index of the last element (inclusive).
     * @return a new list
     * @exception IndexOutOfBoundsException index is out of range (size() > 0 &&
     *      (from < 0 || from > to || to >= size())).
     */
    public AbstractFloatList partFromTo(int from, int to) {
	checkRangeFromTo(from, to, size);
        int length = to - from + 1;
	FloatArrayList part = new FloatArrayList(length);
	part.addAllOfFromTo(this, from, to);
	return part;
    }
    
    /*
     * Sorts the specified range of the receiver into ascending numerical order. The sorting
     * algorithm is a tuned quicksort, adapted from Jon L. Bentley and M. Douglas McIlroy's
     * "Engineering a Sort Function", Software-Practice and Experience, Vol. 23(11) P.
     * 1249-1265 (November 1993). This algorithm offers n*log(n) performance on many data
     * sets that cause other quicksorts to degrade to quadratic performance. You should never
     * call this method unless you are sure that this particular sorting algorithm is the 
     * right one for your data set. It is generally better to call sort() or sortFromTo(...)
     * instead, because those methods automatically choose the best sorting algorithm.
     * @param from the index of the first element (inclusive) to be sorted.
     * @param to the index of the last element (inclusive) to be sorted.
     * @exception IndexOutOfBoundsException index is out of range (size() > 0 &&
     *      (from < 0 || from > to || to >= size())).
     */
    public void quickSortFromTo(int from, int to) {
	int mySize = size();
	checkRangeFromTo(from, to, mySize);
        float[] myElements = elements();
	java.util.Arrays.sort(myElements, from, to + 1);
	elements(myElements);
	setSizeRaw(mySize);
    }
    
    /*
     * Sorts the receiver according to the order induced by the specified comparator. All elements
     * in the range must be mutually comparable by the specified comparator (that is, 
     * c.compare(e1, e2) must not throw a ClassCastException for any elements e1 and e2 in the
     * range). The sorting algorithm is a tuned quicksort, adapted from Jon L. Bentley and M. 
     * Douglas McIlroy's "Engineering a Sort Function", Software-Practice and Experience, Vol. 
     * 23(11) P. 1249-1265 (November 1993). This algorithm offers n*log(n) performance on many data
     * sets that cause other quicksorts to degrade to quadratic performance.
     * @param from the index of the first element (inclusive) to be sorted.
     * @param to the index of the last element (inclusive) to be sorted.
     * @param c the comparator to determine the order of the receiver.
     * @throws ClassCastException if the array contains elements that are not mutually comparable
     *      using the specified comparator.
     * @throws IllegalArgumentException if fromIndex < toIndex
     * @throws ArrayIndexOutOfBoundsException if fromIndex < 0 or toIndex > a.length
     * @see Comparator
     * @exception IndexOutOfBoundsException index is out of range (size() > 0 &&
     *      (from < 0 || from > to || to >= size())).
     */
    public void quickSortFromTo(int from, int to, FloatComparator c) {
	int mySize = size();
	checkRangeFromTo(from, to, mySize);
        float[] myElements = elements();
	Sorting.quickSort(myElements, from, to + 1,c);
	elements(myElements);
	setSizeRaw(mySize);
    }
    
    /*
     * Removes from the receiver all elements that are contained in the specified list. Tests 
     * for identity.
     * @param other the other list.
     * @return true if the receiver changed as a result of the call.
     */
    public boolean removeAll(AbstractFloatList other) {
	if (other.size() == 0) {
            return false;
        } //nothing to do
	int limit = other.size() - 1;
	int j = 0;
        for (int i = 0; i < size ; i++) {
            if (other.indexOfFromTo(getQuick(i), 0, limit) < 0) {
                setQuick(j++, getQuick(i));
            }
	}
        boolean modified = (j != size);
	setSize(j);
	return modified;
    }
    
    /*
     * Removes from the receiver all elements whose index is between from, inclusive and to,
     * inclusive. Shifts any succeeding elements to the left (reduces their index). This call
     * shortens the list by (to - from + 1) elements.
     * @param from index of first element to be removed.
     * @param to index of last element to be removed.
     * @exception IndexOutOfBoundsException index is out of range (size() > 0 &&
     *      (from < 0 || from > to || to >= size())).
     */
    public void removeFromTo(int from, int to) {
	checkRangeFromTo(from, to, size);
	int numMoved = size - to - 1;
	if (numMoved > 0) {
		replaceFromToWithFrom(from, from - 1 + numMoved, this, to + 1);
	}
	int width = to - from + 1;
	if (width>0) {
            setSizeRaw(size - width);
        }
    }
    
    /*
     * Replaces a number of elements in the receiver with the same number of elements of another
     * list. Replaces elements in the receiver, between from (inclusive) and to (inclusive), with
     * elements of other, starting from otherFrom (inclusive).
     * @param from the position of the first element to be replaced in the receiver
     * @param to the position of the last element to be replaced in the receiver
     * @param other list holding elements to be copied into the receiver.
     * @param otherFrom position of first element within other list to be copied.
     */
    public void replaceFromToWithFrom(int from, int to, AbstractFloatList other, int otherFrom) {
	int length = to - from + 1;
	if (length > 0) {
            checkRangeFromTo(from, to, size());
            checkRangeFromTo(otherFrom, otherFrom + length - 1, other.size());
            if (from <= otherFrom) {
                for ( ; --length >= 0; ) {
                    setQuick(from++, other.getQuick(otherFrom++));
                }
            } else {
                int otherTo = otherFrom + length - 1;
                for ( ; --length >= 0; ) {
                    setQuick(to--, other.getQuick(otherTo--));
                }
            }
        }
    }
    
    /*
     * Replaces the part between from (inclusive) and to (inclusive) with the other list's part
     * between otherFrom and otherTo. Both parts need not be of the same size (part A can both be
     * smaller or larger than part B). Parts may overlap. Receiver and other list may (but most
     * not) be identical. If from > to, then inserts other part before from.
     * @param from the first element of the receiver (inclusive)
     * @param to the last element of the receiver (inclusive)
     * @param other the other list (may be identical with receiver)
     * @param otherFrom the first element of the other list (inclusive)
     * @param otherTo the last element of the other list (inclusive)
     * Examples:
     * a=[0, 1, 2, 3, 4, 5, 6, 7]
     * b=[50, 60, 70, 80, 90]
     * a.R(...)=a.replaceFromToWithFromTo(...)
     *
     * a.R(3,5,b,0,4)-->[0, 1, 2, 50, 60, 70, 80, 90, 6, 7]
     * a.R(1,6,b,0,4)-->[0, 50, 60, 70, 80, 90, 7]
     * a.R(0,6,b,0,4)-->[50, 60, 70, 80, 90, 7]
     * a.R(3,5,b,1,2)-->[0, 1, 2, 60, 70, 6, 7]
     * a.R(1,6,b,1,2)-->[0, 60, 70, 7]
     * a.R(0,6,b,1,2)-->[60, 70, 7]
     * a.R(5,3,b,0,4)-->[0, 1, 2, 3, 4, 50, 60, 70, 80, 90, 5, 6, 7]
     * a.R(5,0,b,0,4)-->[0, 1, 2, 3, 4, 50, 60, 70, 80, 90, 5, 6, 7]
     * a.R(5,3,b,1,2)-->[0, 1, 2, 3, 4, 60, 70, 5, 6, 7]
     * a.R(5,0,b,1,2)-->[0, 1, 2, 3, 4, 60, 70, 5, 6, 7]
     *
     * Extreme cases:
     * a.R(5,3,b,0,0)-->[0, 1, 2, 3, 4, 50, 5, 6, 7]
     * a.R(5,3,b,4,4)-->[0, 1, 2, 3, 4, 90, 5, 6, 7]
     * a.R(3,5,a,0,1)-->[0, 1, 2, 0, 1, 6, 7]
     * a.R(3,5,a,3,5)-->[0, 1, 2, 3, 4, 5, 6, 7]
     * a.R(3,5,a,4,4)-->[0, 1, 2, 4, 6, 7]
     * a.R(5,3,a,0,4)-->[0, 1, 2, 3, 4, 0, 1, 2, 3, 4, 5, 6, 7]
     * a.R(0,-1,b,0,4)-->[50, 60, 70, 80, 90, 0, 1, 2, 3, 4, 5, 6, 7]
     * a.R(0,-1,a,0,4)-->[0, 1, 2, 3, 4, 0, 1, 2, 3, 4, 5, 6, 7]
     * a.R(8,0,a,0,4)-->[0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3, 4]
     */
    public void replaceFromToWithFromTo(int from, int to, AbstractFloatList other,
            int otherFrom, int otherTo) {
	if (otherFrom > otherTo) {
            throw new IndexOutOfBoundsException("otherFrom: " + otherFrom + ", otherTo: " + otherTo);
	}
        if ((this == other) && (to - from != otherTo - otherFrom)) { // avoid stumbling over my own feet
            replaceFromToWithFromTo(from, to, partFromTo(otherFrom, otherTo), 0, otherTo-otherFrom);
            return;
	}
        int length = otherTo - otherFrom + 1;
	int diff = length;
	int theLast = from - 1;
        if (to >= from) {
            diff -= (to - from + 1);
            theLast = to;
	}
        if (diff > 0) {
            beforeInsertDummies(theLast + 1, diff);
	}
	else {
            if (diff < 0) {
                removeFromTo(theLast + diff, theLast - 1);
            }
	}
        if (length > 0) {
            replaceFromToWithFrom(from, from + length - 1, other, otherFrom);
	}
    }
    
    /*
     * Replaces the part of the receiver starting at from (inclusive) with all the elements of the
     * specified collection. Does not alter the size of the receiver. Replaces exactly 
     * Math.max(0,Math.min(size()-from, other.size())) elements.
     * @param from the index at which to copy the first element from the specified collection.
     * @param other Collection to replace part of the receiver
     * @exception IndexOutOfBoundsException index is out of range (index < 0 || index >= size()).
     */
    public void replaceFromWith(int from, java.util.Collection other) {
	checkRange(from, size());
	java.util.Iterator e = other.iterator();
	int index = from;
	int limit = Math.min(size() - from, other.size());
	for (int i = 0; i < limit; i++) {
	    set(index++, ((Number) e.next()).floatValue()); //delta
        }
    }
    
    /*
     * Retains (keeps) only the elements in the receiver that are contained in the specified other list.
     * In other words, removes from the receiver all of its elements that are not contained in the
     * specified other list. 
     * @param other the other list to test against.
     * @return true if the receiver changed as a result of the call.
     */
    public boolean retainAll(AbstractFloatList other) {
	if (other.size() == 0) {
            if (size == 0) {
                return false;
            }
            setSize(0);
            return true;
	}
        int limit = other.size() - 1;
	int j = 0;
	for (int i = 0; i < size ; i++) {
            if (other.indexOfFromTo(getQuick(i), 0, limit) >= 0) {
                setQuick(j++, getQuick(i));
            }
	}
        boolean modified = (j != size);
	setSize(j);
	return modified;
    }
    
    /*
     * Reverses the elements of the receiver. Last becomes first, second last becomes second
     * first, and so on.
     */
    public void reverse() {
	float tmp;
	int limit = size() / 2;
	int j = size() - 1;
        for (int i = 0; i < limit; ) { //swap
            tmp = getQuick(i);
            setQuick(i++, getQuick(j));
            setQuick(j--, tmp);
	}
    }
    
    /*
     * Replaces the element at the specified position in the receiver with the specified element.
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     * @throws IndexOutOfBoundsException if index < 0 || index >= size().
     */
    public void set(int index, float element) {
	if (index >= size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
	setQuick(index, element);
    }
    
    /*
     * Replaces the element at the specified position in the receiver with the specified element; 
     * WARNING: Does not check preconditions. Provided with invalid parameters this method may 
     * access invalid indexes without throwing any exception! You should only use this method
     * when you are absolutely sure that the index is within bounds. Precondition (unchecked):
     * index >= 0 && index < size(). This method is normally only used internally in large loops
     * where bounds are explicitly checked before the loop and need no be rechecked within the 
     * loop. However, when desperately, you can give this method public visibility in subclasses.
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     */
    protected abstract void setQuick(int index, float element);
    
    /*
     * Sets the size of the receiver without modifying it otherwise. This method should not 
     * release or allocate new memory but simply set some instance variable like size. If your
     * subclass overrides and delegates size changing methods to some other object, you must 
     * make sure that those overriding methods not only update the size of the delegate but
     * also of this class. For example:
     * public DatabaseList extends AbstractFloatList {
     *    ...
     *    public void removeFromTo(int from, int to) {
     *        myDatabase.removeFromTo(from, to);
     *        this.setSizeRaw(size - (to - from + 1));
     *    }
     * }
     */
    protected void setSizeRaw(int newSize) {
	size = newSize;
    }
    
    /*
     * Randomly permutes the part of the receiver between from (inclusive) and to (inclusive). 
     * @param from the index of the first element (inclusive) to be permuted.
     * @param to the index of the last element (inclusive) to be permuted.
     * @exception IndexOutOfBoundsException index is out of range (size() > 0 &&
     *      (from < 0 || from > to || to >= size())).
     */
    public void shuffleFromTo(int from, int to) {
	checkRangeFromTo(from, to, size());
        Uniform gen = new Uniform(new DRand(new java.util.Date()));
	for (int i = from; i < to; i++) { 
            int random = gen.nextIntFromTo(i, to);
            float tmp = getQuick(random);
            setQuick(random,getQuick(i)); 
            setQuick(i,tmp); 
	}  
    }
    
    /*
     * @returns  the number of elements contained in the receiver.
     */
    public int size() {
	return size;
    }
    
    /*
     * Returns a list which is a concatenation of times multiplied by the receiver.
     * @param times the number of times the receiver shall be copied.
     */
    public AbstractFloatList times(int times) {
	AbstractFloatList newList = new FloatArrayList(times * size());
	for (int i = times; --i >= 0; ) {
            newList.addAllOfFromTo(this, 0, size() - 1);
	}
	return newList;
    }
    
    /*
     * Returns a java.util.ArrayList containing all the elements in the receiver.
     */
    public java.util.ArrayList toList() {
	int mySize = size();
	java.util.ArrayList list = new java.util.ArrayList(mySize);
	for (int i = 0; i < mySize; i++) {
            list.add(new Float(get(i)));
        }
	return list;
    }
    
    /*
     * Returns a string representation of the receiver, containing
     * the String representation of each element.
     */
    public String toString() {
	return Arrays.toString(partFromTo(0, size() - 1).elements());
    }
}