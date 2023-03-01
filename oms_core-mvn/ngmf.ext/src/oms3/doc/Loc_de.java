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
package oms3.doc;

import java.util.ListResourceBundle;

/**
 * Localization for DB5 documents.
 * 
 * @author od
 */
public class Loc_de extends ListResourceBundle {

    static private final Object[][] contents = {
        {"date_format", "EEE, d.MM.yyyy HH:mm:ss z"},
        {"subtitle", "Simulation, Modell, and Parameter Dokumentation"},
        {"parameterset", "Model Parameter"},
        {"model", "Modell Komponente"},
        {"sub", "Subkomponenten"},
        {"component", "Komponente"},
        {"keyword", "Keyword"},
        {"parameter", "Parameter"},
        {"variable", "Variable"},
        {"name", "Name"},
        {"author", "Autor"},
        {"version", "Version"},
        {"source", "Quellcode"},
        {"license", "Lizenz"},
        {"var_in", "Variablen (In)"},
        {"var_out", "Variablen (Out)"},
        {"bibliography", "Bibliographie"},
        {"further", "Weiterfuehrende Dokumentation"},
    };

    @Override
    public Object[][] getContents() {
        return contents;
    }
}
