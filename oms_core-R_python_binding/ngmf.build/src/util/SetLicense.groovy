
package util;


String license = 
"""/*
 * \$Id:\$
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
"""

def vis

// Define closure
vis = { 
    it.eachDir(vis);
    it.eachFileMatch(~/.*\.java/) { 
        String t = it.text
        int pkgidx =  t.indexOf("package ")
        
        if (pkgidx !=-1) {
            if (pkgidx > 0) {
                t1 = license + t.substring(pkgidx);
            } else {
                t1 = license + t;
            }
            //            println t1
            new File(it.canonicalPath).write(t1) 
            println "OK: ${it.canonicalPath}"
        } else {
            println "NO PACKAGE: ${it.canonicalPath}"
        } 
    }
}

// Apply closure
vis(new File("/od/oms/oms_core/ngmf/src"))
vis(new File("/od/oms/oms_core/ngmf.ext/src"))
vis(new File("/od/oms/oms_core/ngmf.sim/src"))
vis(new File("/od/oms/oms_core/ngmf.ui/src"))

