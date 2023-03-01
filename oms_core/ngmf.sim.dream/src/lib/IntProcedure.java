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

public interface IntProcedure {
    /*
     * Applies a procedure to an argument. Optionally can return a boolean flag to inform
     * the object calling the procedure. Example: forEach() methods often use procedure 
     * objects. To signal to a forEach() method whether iteration should continue normally
     * or terminate (because for example a matching element has been found), a procedure 
     * can return false to indicate termination and true to indicate continuation.
     * @param element   element passed to the procedure.
     * @return a flag  to inform the object calling the procedure.
     */
    abstract public boolean apply(int element);
}