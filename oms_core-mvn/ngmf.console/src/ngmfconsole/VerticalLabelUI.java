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
package ngmfconsole;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;

/**
 * A UI delegate for JLabel that rotates the label 90 degree <P> Extends {@link BasicLabelUI}.
 * <P> The only difference between the appearance of labels in the Basic and
 * Metal {@literal L&Fs} is the manner in which disabled text is painted. As VerticalLabelUI
 * does not override the method paintDisabledText, this class can be adapted for
 * Metal {@literal L&F} by extending MetalLabelUI instead of BasicLabelUI. <P> No other
 * changes are required.
 *
 * @author Darryl
 */
public class VerticalLabelUI extends BasicLabelUI {

    private boolean clockwise = false;
    // see comment in BasicLabelUI
    Rectangle verticalViewR = new Rectangle();
    Rectangle verticalIconR = new Rectangle();
    Rectangle verticalTextR = new Rectangle();
    protected static VerticalLabelUI verticalLabelUI = new VerticalLabelUI();
    private final static VerticalLabelUI SAFE_VERTICAL_LABEL_UI = new VerticalLabelUI();

    /**
     * Constructs a
     * <code>VerticalLabelUI</code> with the default anticlockwise rotation
     */
    public VerticalLabelUI() {
    }

    /**
     * Constructs a
     * <code>VerticalLabelUI</code> with the desired rotation. <P>
     *
     * @param clockwise true to rotate clockwise, false for anticlockwise
     */
    public VerticalLabelUI(boolean clockwise) {
        this.clockwise = clockwise;
    }

    /**
     * @param c the component
     * @return the component UI
     * @see ComponentUI#createUI(javax.swing.JComponent)
     */
    public static ComponentUI createUI(JComponent c) {
        if (System.getSecurityManager() != null) {
            return SAFE_VERTICAL_LABEL_UI;
        } else {
            return verticalLabelUI;
        }
    }

    /**
     * Overridden to always return -1, since a vertical label does not have a
     * meaningful baseline.
     *
     * @see ComponentUI#getBaseline(JComponent, int, int)
     */
    @Override
    public int getBaseline(JComponent c, int width, int height) {
        super.getBaseline(c, width, height);
        return -1;
    }

    /**
     * Overridden to always return Component.BaselineResizeBehavior.OTHER, since
     * a vertical label does not have a meaningful baseline
     *
     * @see ComponentUI#getBaselineResizeBehavior(javax.swing.JComponent)
     */
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(JComponent c) {
        super.getBaselineResizeBehavior(c);
        return Component.BaselineResizeBehavior.OTHER;
    }

    /**
     * Transposes the view rectangles as appropriate for a vertical view before
     * invoking the super method and copies them after they have been altered by {@link SwingUtilities#layoutCompoundLabel(FontMetrics, String,
     * Icon, int, int, int, int, Rectangle, Rectangle, Rectangle, int)}
     */
    @Override
    protected String layoutCL(JLabel label, FontMetrics fontMetrics, String text, Icon icon, Rectangle viewR, Rectangle iconR,
            Rectangle textR) {

        transposeRectangle(viewR, verticalViewR);
        transposeRectangle(iconR, verticalIconR);
        transposeRectangle(textR, verticalTextR);

        text = super.layoutCL(label, fontMetrics, text, icon, verticalViewR, verticalIconR, verticalTextR);

        copyRectangle(verticalViewR, viewR);
        copyRectangle(verticalIconR, iconR);
        copyRectangle(verticalTextR, textR);
        return text;
    }

    /**
     * Transforms the Graphics for vertical rendering and invokes the super
     * method.
     */
    @Override
    public void paint(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        if (clockwise) {
            g2.rotate(Math.PI / 2, c.getSize().width / 2, c.getSize().width / 2);
        } else {
            g2.rotate(-Math.PI / 2, c.getSize().height / 2, c.getSize().height / 2);
        }
        super.paint(g2, c);
    }

    /**
     * Returns a Dimension appropriate for vertical rendering
     *
     * @see ComponentUI#getPreferredSize(javax.swing.JComponent)
     */
    @Override
    public Dimension getPreferredSize(JComponent c) {
        return transposeDimension(super.getPreferredSize(c));
    }

    /**
     * Returns a Dimension appropriate for vertical rendering
     *
     * @see ComponentUI#getMaximumSize(javax.swing.JComponent)
     */
    @Override
    public Dimension getMaximumSize(JComponent c) {
        return transposeDimension(super.getMaximumSize(c));
    }

    /**
     * Returns a Dimension appropriate for vertical rendering
     *
     * @see ComponentUI#getMinimumSize(javax.swing.JComponent)
     */
    @Override
    public Dimension getMinimumSize(JComponent c) {
        return transposeDimension(super.getMinimumSize(c));
    }

    private Dimension transposeDimension(Dimension from) {
        return new Dimension(from.height, from.width + 2);
    }

    private Rectangle transposeRectangle(Rectangle from, Rectangle to) {
        if (to == null) {
            to = new Rectangle();
        }
        to.x = from.y;
        to.y = from.x;
        to.width = from.height;
        to.height = from.width;
        return to;
    }

    private Rectangle copyRectangle(Rectangle from, Rectangle to) {
        if (to == null) {
            to = new Rectangle();
        }
        to.x = from.x;
        to.y = from.y;
        to.width = from.width;
        to.height = from.height;
        return to;
    }
}
