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
package oms3.nap;

import java.util.Map;

/**
 * Annotation Handler.
 *
 * Implement this interface to handle annotations. The processor will
 * call this handler once with start(), then handle(..) n-times 
 * for n annotated lines,
 * and finally done() when done with this file.
 *
 * @author od
 */
public interface AnnotationHandler {

    public static final String VALUE = "value";

    /**
     * Called by the processor when starting processing annotations for a file.
     *
     * @param src the file that is processed as String.
     */
    void start(String src);

    /**
     * Handles a single line of annotated code.
     *
     * @param ann      annotations KVPs in a map. It is empty if there
     *                       is no annotation argument (tagging annotation), annValue.get("value") will
     *                       return a single value argument.
     * @param srcLine    the source line that follows the annotations, if there
     *                       is none (e.g. another annotation follows), the value will be null.
     */
    void handle(Map<String, Map<String, String>> ann, String srcLine);

    /**
     * Logging during parsing
     * @param msg the message to log
     */
    void log(String msg);

    /**
     * Done with file processing.
     * @throws Exception generic exception
     */
    void done() throws Exception;
}
