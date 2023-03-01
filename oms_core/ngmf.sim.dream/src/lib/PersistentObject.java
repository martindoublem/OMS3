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

public abstract class PersistentObject extends Object implements java.io.Serializable, Cloneable {
    public static final long serialVersionUID = 1020L;
    
    protected PersistentObject() {
    }
    
    /*
     * Returns a copy of the receiver. This default implementation does not nothing except 
     * making the otherwise protected clone method public.
     * @return a copy of the receiver.
     */
    public Object clone() {
        try {
            return super.clone();
	} catch (CloneNotSupportedException exc) {
            throw new InternalError(); //should never happen since we are cloneable
	}
    }
}