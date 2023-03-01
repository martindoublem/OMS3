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

public abstract class AbstractCollection extends PersistentObject {
    /*
     * Makes this class non instantiable, but still let's others inherit from it.
     */
    protected AbstractCollection() {}
    
    /*
     * Removes all elements from the receiver. The receiver will be empty after this call returns.
     */
    public abstract void clear();
    
    /*
     * Tests if the receiver has no elements.
     * @return true if the receiver has no elements; false otherwise.
     */
    public boolean isEmpty() {
	return size() == 0;
    }
    
    /*
     * Returns the number of elements contained in the receiver.
     * @returns  the number of elements contained in the receiver.
     */
    public abstract int size();
    
    /*
     * Returns a java.util.ArrayList containing all the elements in the receiver.
     */
    public abstract java.util.ArrayList toList();
    
    /*
     * Returns a string representation of the receiver, containing the String
     * representation of each element.
     */
    public String toString() {
	return toList().toString();
    }
}