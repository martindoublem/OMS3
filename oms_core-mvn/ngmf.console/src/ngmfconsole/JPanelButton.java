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

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import javax.swing.*;

/**
 * Panel Button
 *
 * @author Olaf David
 */
public final class JPanelButton extends JToggleButton {

    private static final long serialVersionUID = 1L;

    private JPanel panel;
    private Container window;

    private static final Color dimgray = new Color(105, 105, 105);
    private static final Stroke plusStroke = new BasicStroke(2);
    private static final int plusLength = 3;


    public JPanelButton(JPanel panel) {
        this("", null, panel);
    }


    public JPanelButton() {
        this("", null, null);
    }


    public JPanelButton(Icon icon, JPanel panel) {
        this("", icon, panel);
    }


    public JPanelButton(String text, Icon icon, JPanel panel) {
        super(text, icon, false);
        setPanel(panel);
    }


    @Override
    public void setText(String text) {
        String t = text == null ? null : text + "  ";
        super.setText(t);
    }


    public void setPanel(JPanel panel) {
        this.panel = panel;
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        Color c = g.getColor();
        Stroke s = g2.getStroke();
        g.setColor(isEnabled() ? dimgray : Color.LIGHT_GRAY);
        Insets i = getInsets();
        int newx = getWidth() - i.right - 2 * plusLength;
        int newy = (int) (getHeight() * 0.75);
        g.translate(newx, newy);
        g2.setStroke(plusStroke);
        g.drawLine(-plusLength, 0, plusLength, 0);
        if (!isSelected()) {
            g.drawLine(0, -plusLength, 0, plusLength);
        }
        g.translate(-newx, -newy);
        g.setColor(c);
        g2.setStroke(s);
    }


    @Override
    protected void fireItemStateChanged(ItemEvent event) {
        Container w = getWindow();
        boolean isSelected = isSelected();
        if (isSelected) {
            adjustWindow(w);
            w.requestFocus();
        }
        w.setVisible(isSelected);
        firePropertyChange("showPanel", !isSelected, isSelected);
    }


    private void adjustWindow(Container w) {
        Point p = new Point();
        SwingUtilities.convertPointToScreen(p, this);
        w.setLocation(p.x, p.y + getHeight());
    }


    private synchronized Container getWindow() {
        if (window == null) {
            window = createWindow();
        }
        return window;
    }


    private Container createWindow() {
        if (panel == null) {
            throw new RuntimeException("Internal Panel is not set");
        }
        Component root = SwingUtilities.getRoot(this);
        final JDialog win = new JDialog((Window) root);
        win.setUndecorated(true);
        root.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentMoved(ComponentEvent e) {
                if (isSelected()) {
                    adjustWindow(win);
                }
            }
        });
        win.getContentPane().add(panel);
        win.setFocusable(true);
        win.pack();
        return win;
    }
}
