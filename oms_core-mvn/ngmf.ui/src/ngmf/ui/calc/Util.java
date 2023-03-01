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
package ngmf.ui.calc;

import javax.swing.table.TableModel;

/**
 *
 * @author od
 */
public class Util {
    
    /** Creates a new instance of Util */
    private Util() {
    }
    
    static int findColumn(TableModel model, String name) {
        for (int i = 0; i<model.getColumnCount(); i++) {
            if (name.equals(model.getColumnName(i))) {
                return i;
            }
        }
        return -1;
    }
    
}
