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

public abstract class AbstractList extends AbstractCollection {
    
    /*
     * Makes this class non instantiable, but still let's others inherit from it.
     */
    protected AbstractList() {}
    
    /* 
     * Appends all of the elements of the specified Collection to the receiver.
     * @exception ClassCastException if an element in the collection is not of the same
     *      parameter type of the receiver.
     */
    public void addAllOf(java.util.Collection collection) {
	this.beforeInsertAllOf(size(), collection);
    }
    
    /*
     * Inserts all elements of the specified collection before the specified position into the receiver. 
     * Shifts the element currently at that position (if any) and any subsequent elements to the right 
     * (increases their indices). 
     * @param index index before which to insert first element from the specified collection.
     * @param collection the collection to be inserted
     * @exception ClassCastException if an element in the collection is not of the same parameter type
     *      of the receiver.
     * @throws IndexOutOfBoundsException if index < 0 || index > size().
     */
    public void beforeInsertAllOf(int index, java.util.Collection collection) {
	this.beforeInsertDummies(index, collection.size());
	this.replaceFromWith(index, collection);
    }
    
    /*
     * Inserts length dummy elements before the specified position into the receiver. Shifts the
     * element currently at that position (if any) and any subsequent elements to the right. This
     * method must set the new size to be size() + length.
     * @param index index before which to insert dummy elements (must be in [0,size])..
     * @param length number of dummy elements to be inserted.
     * @throws IndexOutOfBoundsException if index < 0 || index > size().
     */
    protected abstract void beforeInsertDummies(int index, int length);
    
    /*
     * Checks if the given index is in range.
     */
    protected static void checkRange(int index, int theSize) {
	if (index >= theSize || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + theSize);
        }
    }
    
    /*
     * Checks if the given range is within the contained array's bounds.
     * @throws IndexOutOfBoundsException if to != from - 1 || from < 0 || from > to || to >= size().
     */
    protected static void checkRangeFromTo(int from, int to, int theSize) {
        if (to == from - 1) {
            return;
        }
	if (from < 0 || from > to || to >= theSize) {
            throw new IndexOutOfBoundsException("from: " + from + ", to: " + to + ", size = " + theSize);
        }
    }
    
    /*
     * Removes all elements from the receiver. The receiver will be empty after this call returns,
     * but keep its current capacity.
     */
    public void clear() {
	removeFromTo(0, size() - 1);
    }
    
    /*
     * Sorts the receiver into ascending order. This sort is guaranteed to be stable: equal elements
     * will not be reordered as a result of the sort. The sorting algorithm is a modified mergesort
     * (in which the merge is omitted if the highest element in the low sublist is less than the
     * lowest element in the high sublist).  This algorithm offers guaranteed n*log(n) performance,
     * and can approach linear performance on nearly sorted lists. You should never call this method
     * unless you are sure that this particular sorting algorithm is the right one for your data set.
     * It is generally better to call sort() or sortFromTo(...) instead, because those methods 
     * automatically choose the best sorting algorithm.
     */
    public final void mergeSort() {
	mergeSortFromTo(0, size() - 1);
    }
    
    /*
     * Sorts the receiver into ascending order. This sort is guaranteed to be stable: equal elements
     * will not be reordered as a result of the sort. The sorting algorithm is a modified mergesort 
     * (in which the merge is omitted if the highest element in the low sublist is less than the
     * lowest element in the high sublist). This algorithm offers guaranteed n*log(n) performance, 
     * and can approach linear performance on nearly sorted lists. You should never call this method
     * unless you are sure that this particular sorting algorithm is the right one for your data set.
     * It is generally better to call sort() or sortFromTo(...) instead, because those methods automatically
     * choose the best sorting algorithm.
     * @param from the index of the first element (inclusive) to be sorted.
     * @param to the index of the last element (inclusive) to be sorted.
     * @throws IndexOutOfBoundsException if (from < 0 || from > to || to >= size()) && to != from - 1.
     */
    public abstract void mergeSortFromTo(int from, int to);
    
    /*
     * Sorts the receiver into ascending order. The sorting algorithm is a tuned quicksort, adapted
     * from Jon L. Bentley and M. Douglas McIlroy's "Engineering a Sort Function", Software-Practice
     * and Experience, Vol. 23(11) P. 1249-1265 (November 1993). This algorithm offers n*log(n)
     * performance on many data sets that cause other quicksorts to degrade to quadratic performance.
     * You should never call this method unless you are sure that this particular sorting algorithm
     * is the right one for your data set. It is generally better to call sort() or sortFromTo(...)
     * instead, because those methods automatically choose the best sorting algorithm.
     */
    public final void quickSort() {
	quickSortFromTo(0, size() - 1);
    }
    
    /*
     * Sorts the specified range of the receiver into ascending order.  The sorting algorithm is a
     * tuned quicksort, adapted from Jon L. Bentley and M. Douglas McIlroy's "Engineering a Sort 
     * Function", Software-Practice and Experience, Vol. 23(11) P. 1249-1265 (November 1993). This
     * algorithm offers n*log(n) performance on many data sets that cause other quicksorts to
     * degrade to quadratic performance. You should never call this method unless you are sure that
     * this particular sorting algorithm is the right one for your data set. It is generally better
     * to call sort() or sortFromTo(...) instead, because those methods automatically choose the 
     * best sorting algorithm.
     * @param from the index of the first element (inclusive) to be sorted.
     * @param to the index of the last element (inclusive) to be sorted.
     * @throws IndexOutOfBoundsException if (from < 0 || from > to || to >= size()) && to != from - 1.
     */ 
    public abstract void quickSortFromTo(int from, int to);
    
    /*
     * Removes the element at the specified position from the receiver. Shifts any subsequent elements
     * to the left.
     * @param index the index of the element to removed.
     * @throws IndexOutOfBoundsException if index < 0 || index >= size().
     */
    public void remove(int index) {
	removeFromTo(index, index);
    }
    
    /*
     * Removes from the receiver all elements whose index is between from, inclusive and to, inclusive.
     * Shifts any succeeding elements to the left (reduces their index). This call shortens the list by
     * (to - from + 1) elements.
     * @param from index of first element to be removed.
     * @param to index of last element to be removed.
     * @throws IndexOutOfBoundsException if (from < 0 || from > to || to >= size()) && to != from - 1.
     */
    public abstract void removeFromTo(int fromIndex, int toIndex);
    
    /*
     * Replaces the part of the receiver starting at from (inclusive) with all the elements of the
     * specified collection. Does not alter the size of the receiver. Replaces exactly
     * Math.max(0, Math.min(size() - from, other.size())) elements.
     * @param from the index at which to copy the first element from the specified collection.
     * @param other Collection to replace part of the receiver
     * @throws IndexOutOfBoundsException if index < 0 || index >= size().
     */
    public abstract void replaceFromWith(int from, java.util.Collection other);
    
    /*
     * Reverses the elements of the receiver. Last becomes first, second last becomes second first, and so on.
     */
    public abstract void reverse();
    
    /*
     * Sets the size of the receiver. If the new size is greater than the current size, new null
     * or zero items are added to the end of the receiver. If the new size is less than the current
     * size, all components at index newSize and greater are discarded. This method does not release
     * any superfluous internal memory. Use method trimToSize to release superfluous internal memory.
     * @param newSize the new size of the receiver.
     * @throws IndexOutOfBoundsException if newSize < 0.
     */
    public void setSize(int newSize) {
	if (newSize < 0) {
            throw new IndexOutOfBoundsException("newSize: " + newSize);
        }
        int currentSize = size();
	if (newSize != currentSize) {
            if (newSize > currentSize) {
                beforeInsertDummies(currentSize, newSize - currentSize);
            } else if (newSize < currentSize) {
                removeFromTo(newSize, currentSize - 1);
            }
	}
    }
    
    /*
     * Randomly permutes the receiver. After invocation, all elements will be at random positions.
     */
    public final void shuffle() {
	shuffleFromTo(0, size() - 1);
    }
    
    /*
     * Randomly permutes the receiver between from (inclusive) and to (inclusive). 
     * @param from the start position (inclusive)
     * @param to the end position (inclusive)
     * @throws IndexOutOfBoundsException if (from < 0 || from > to || to >= size()) && to != from - 1.
     */
    public abstract void shuffleFromTo(int from, int to);
    
    /*
     * Sorts the receiver into ascending order. The sorting algorithm is dynamically chosen according
     * to the characteristics of the data set. This implementation simply calls sortFromTo(...).
     * Override sortFromTo(...) if you can determine which sort is most appropriate for the given data set.
    */
    public final void sort() {
	sortFromTo(0, size() - 1);
    }
    
    /*
     * Sorts the specified range of the receiver into ascending order. The sorting algorithm is
     * dynamically chosen according to the characteristics of the data set. This default 
     * implementation simply calls quickSort. Override this method if you can determine which sort
     * is most appropriate for the given data set.
     * @param from the index of the first element (inclusive) to be sorted.
     * @param to the index of the last element (inclusive) to be sorted.
     * @throws IndexOutOfBoundsException if (from < 0 || from  to || to >= size()) && to != from - 1.
     */
    public void sortFromTo(int from, int to) {
	quickSortFromTo(from, to);
    }
    
    /*
     * Trims the capacity of the receiver to be the receiver's current size. Releases any superfluous
     * internal memory. An application can use this operation to minimize the storage of the receiver.
     * This default implementation does nothing. Override this method in space efficient implementations.
     */
    public void trimToSize() {}
}