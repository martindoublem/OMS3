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

public interface ShortComparator {
    
    /*
     * Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as
     * the first argument is less than, equal to, or greater than the second. The  implementor must 
     * ensure that sgn(compare(x, y)) == -sgn(compare(y, x)) for all x and y. (This implies that 
     * compare(x, y) must throw an exception if and only if compare(y, x) throws an exception.) The
     * implementor must also ensure that the relation is transitive: ((compare(x, y) > 0) &&
     * (compare(y, z) > 0)) implies compare(x, z) > 0. Finally, the implementer must ensure that 
     * compare(x, y) == 0 implies that sgn(compare(x, z)) == sgn(compare(y, z)) for all z.
     * @return a negative integer, zero, or a positive integer as the first argument is less
     *      than, equal to, or greater than the second. 
     */
    int compare(short o1, short o2);
    
    /*
     * Indicates whether some other object is "equal to" this Comparator. This method must obey the 
     * general contract of Object.equals(Object). Additionally, this method can return true only if
     * the specified Object is also a comparator and it imposes the same ordering as this comparator. 
     * Thus, comp1.equals(comp2) implies that sgn(comp1.compare(o1, o2)) == sgn(comp2.compare(o1, o2))
     * for every element o1 and o2. Note that it is always safe not to override Object.equals(Object).
     * However, overriding this method may, in some cases, improve performance by allowing programs to
     * determine that two distinct Comparators impose the same order.
     * @param   obj   the reference object with which to compare.
     * @return  true only if the specified object is also a comparator and it imposes the same
     *      ordering as this comparator.
     * @see     java.lang.Object#equals(java.lang.Object)
     * @see java.lang.Object#hashCode()
     */
    boolean equals(Object obj);
}