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
package oms3;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import oms3.Notification.DataflowEvent;

/**
 * Field Access.
 *
 * @author od
 *
 */
class FieldAccess implements Access {

    Notification ens;
    Field field;
    Object comp;
    FieldContent data;
    FieldInvoker f;
    private static final Logger log = Logger.getLogger("oms3.sim");

    FieldAccess(Object target, Field field, Notification ens) {
        this.field = field;
        this.comp = target;
        this.ens = ens;
        f = Utils.fieldInvoker(target, field);
    }

    // called on 'out' access.
    @Override
    public FieldContent getData() {
        if (data == null) {
            data = new FieldContent();
        }
        return data;
    }

    // called in 'in' access
    @Override
    public void setData(FieldContent data) {
        // allow setting in field once only.
        // cannot have multiple sources for one @In !
        if (this.data != null) {
            throw new ComponentException("Attempt to set @In field twice: " + comp + "." + field.getName());
        }
        this.data = data;
    }

    /**
     * Checks if this object is in a valid state.
     *
     * @return
     */
    @Override
    public boolean isValid() {
        return data != null && field.isAccessible();
    }

    /**
     * a field is receiving a new value (in)
     *
     * @throws java.lang.Exception
     */
    @Override
    public void in() throws Exception {
        if (data == null) {
            if (log.isLoggable(Level.WARNING)) {
                log.warning("@In not connected : " + toString() + ", using default value.");
            }
            return;
        }
        Object val = data.getValue();
        // fire only if there is a listener
        if (ens.shouldFire()) {
            DataflowEvent e = new DataflowEvent(ens.getController(), this, val);
            ens.fireIn(e);
            // the value might be altered
            val = e.getValue();
        }

        // type conversion
        if (val != null && field.getType() != val.getClass() && !field.getType().isAssignableFrom(val.getClass())) {
            // default type conversion fails, we need to convert.
            // this will use the Conversions SPI.
            val = Conversions.convert(val, field.getType());
        }
        setFieldValue(val);
    }

    /**
     * a field is sending a new value (out)
     *
     * @return the object value in out
     * @throws java.lang.Exception
     */
    @Override
    public void out() throws Exception {
        Object val = getFieldValue();
        if (ens.shouldFire()) {
            DataflowEvent e = new DataflowEvent(ens.getController(), this, val);
            ens.fireOut(e);
            // the value might be altered
            val = e.getValue();
        }
        // if data==null this unconsumed @Out, its OK but we do not want to set it.
        if (data != null) {
            data.setValue(val);
        }
    }

    /**
     * Get the command belonging to this Object
     *
     * @return the command object
     */
    @Override
    public Object getComponent() {
        return comp;
    }

    /**
     * Get the Field
     *
     * @return the field object.
     */
    @Override
    public Field getField() {
        return field;
    }

    /**
     * Get directly the value of the component field.
     *
     * @return the field value object
     * @throws Exception if there is illegal access ot type
     */
    @Override
    final public Object getFieldValue() throws Exception {
        return f.getValue();
    }

    /**
     * Set directly the value of a component field.
     *
     * @param o the new value to set
     * @throws Exception if there is illegal access ot type
     */
    @Override
    final public void setFieldValue(Object o) throws Exception {
        f.setValue(o);
    }

    @Override
    public String toString() {
        return comp + "%" + field.getName() + " - " + (data == null ? null : data.getValue());
    }
}
