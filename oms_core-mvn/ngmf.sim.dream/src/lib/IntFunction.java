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
 * Copyright 1999 CERN - European Organization for Nuclear Research. Permission to use,
 * copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all 
 * copies and that both that copyright notice and this permission notice appear in supporting
 * documentation. CERN makes no representations about the suitability of this software for
 * any purpose. It is provided "as is" without expressed or implied warranty.
 */
/*
 * Interface that represents a function object: a function that takes 
 * a single argument and returns a single value.
 */
public interface IntFunction {
    /*
     * Applies a function to an argument.
     * @param argument   argument passed to the function.
     * @return the result of the function.
     */
    abstract public int apply(int argument);
}