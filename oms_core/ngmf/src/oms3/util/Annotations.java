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
package oms3.util;

import java.lang.reflect.Field;
import oms3.annotations.*;

/**
 * Annotation utilities.
 * @author od
 */
public class Annotations {

    private Annotations() {
    }

    /** Checks if all roles are played.
     *
     * @param role the role to check.
     * @param r the roles that all have to be played
     * @return true or false.
     */
    public static boolean playsAll(Role role, String... r) {
        if (role == null) {
            return false;
        }
        for (String s : r) {
            if (!role.value().contains(s)) {
                return false;
            }
        }
        return true;
    }

    /** Checks if one role is played.
     *
     * @param role the role to check
     * @param r the expected role
     * @return true or false.
     */
    public static boolean plays(Role role, String r) {
        if (r == null) {
            throw new IllegalArgumentException("null role");
        }
        if (role == null) {
            return false;
        }
        return role.value().contains(r);
    }

    /**
     * Checks if a field is tagged as 'In'
     *
     * @param f the field to check
     * @return true is 'In', false otherwise.
     */
    public static boolean isIn(Field f) {
        return f.getAnnotation(In.class) != null;
    }

    /** Checks if a field is tagged as 'Out'
     *
     * @param f the field to check.
     * @return true if 'Out', false otherwise.
     */
    public static boolean isOut(Field f) {
        return f.getAnnotation(Out.class) != null;
    }

    /** Checks if a field is tagged as 'Out' and 'In'
     *
     * @param f the field to check.
     * @return true if 'Out' and 'In', false otherwise.
     */
    public static boolean isInOut(Field f) {
        return isIn(f) && isOut(f);
    }

    /** Check if a certain value is in range
     * 
     * @param range range info
     * @param val the value to check;
     * @return true if it is in range, false otherwise.
     */
    public static boolean inRange(Range range, double val) {
        return val >= range.min() && val <= range.max();
    }
    
}
