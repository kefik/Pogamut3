package cz.cuni.pogamut.shed.widget;

import cz.cuni.amis.pogamut.sposh.elements.EnumValue;
import cz.cuni.amis.pogamut.sposh.elements.ParseException;
import cz.cuni.amis.pogamut.sposh.elements.Result;
import cz.cuni.amis.pogamut.sposh.executor.ParamInfo;
import cz.cuni.amis.pogamut.sposh.executor.ParamInfo.Type;
import cz.cuni.pogamut.shed.presenter.IPresenter;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.PopupMenuProvider;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Exceptions;

/**
 * Ancestor of all widgets in the {@link ShedScene}. It provides common
 * infrastructure and helper methods for all widgets, e.g. {@link ShedWidget#createAcceptProviders() drag and drop},
 * display name ect.
 * <p/>
 * The widget is painted as colored rectangle with text drawn over it. * If size
 * of widget is too small, the text will be truncated (three dots will be
 * appended to the truncated text) so it fits.
 * <p/>
 * The origin of the widget ([0, 0]) is at the top left corner.
 *
 * @author HonzaH
 * @param <T> Type of element this widget is attached to.
 */
public class ShedWidget extends Widget implements PopupMenuProvider, IPresentedWidget {

    /**
     * Structure holding info about variable shown in the widget.
     */
    public final static class Variable {

        /**
         * Name of the variable, e.g. <tt>$team</tt>.
         */
        public final String name;
        /**
         * Type of the variable. If unknown/undefined, null.
         */
        public final ParamInfo.Type type;
        /**
         * Value shown to the user. If missing, then blank.
         */
        public final String valueString;
        /**
         * Error message about the variable. Can be type error (value of
         * variable is int, but it should be enum) or the value should reference
         * missing variable (<tt>$var=čmissingVar</tt>). Blank, if no error.
         */
        public final String errorMsg;

        public Variable(String name, Type type, String valueString, String errorMsg) {
            this.name = name;
            this.type = type;
            this.valueString = valueString;
            this.errorMsg = errorMsg;
        }

        public String toString() {
            String varInfo = this.name;
            if (!valueString.isEmpty()) {
                varInfo += '=';
                try {
                    Object varValue = Result.parseValue(valueString);
                    if (varValue instanceof EnumValue) {
                        varInfo += ((EnumValue) varValue).getSimpleName();
                    } else {
                        varInfo += valueString;
                    }
                } catch (ParseException ex) {
                    varInfo += valueString;
                }
            }
            if (!errorMsg.isEmpty()) {
                varInfo += ' ';
                varInfo += errorMsg;
            }
            
            return varInfo;
        }
    }
    /**
     * Distance between left border of the widget and first letter of the text
     */
    private final static int textOfs = 4;
    /**
     * Color of the widget
     */
    protected Color color;
    /**
     * Color of the border
     */
    protected Color borderColor;
    /**
     * Provider of actions for this widget, menu, accept and others.
     */
    protected IPresenter actionProvider;
    /**
     * Default width of {@link ShedWidget}.
     */
    protected static final int width = 240;
    /**
     * Default height of {@link ShedWidget}.
     */
    protected static final int height = 30;
    /**
     * Heading of the widget.
     */
    private String heading;
    private final static Font headingFont = new Font("Helvetica", Font.BOLD, 16);
    List<Variable> presentVars = new LinkedList<Variable>();
    private final static Font presentFont = new Font("Helvetica", Font.BOLD, 12);
    List<Variable> errorVars = new LinkedList<Variable>();
    private final static Font missingFont = new Font("Helvetica", Font.BOLD, 12);
    List<Variable> unusedVars = new LinkedList<Variable>();
    private final static Font unusedFont = new Font("Helvetica", Font.ITALIC, 12);
    private final static Font typeFont = new Font("Helvetica", Font.ITALIC, 8);
    private int intensity = 0;
    private static int BORDER_WIDTH = 4;
    /**
     * Width of strip on the right side that indicates that this node has a
     * breakpoint.
     */
    final static int BREAKPOINT_STRIP_WIDTH = 10;
    private ShowBreakpoint showBreakpointStrip = ShowBreakpoint.NONE;

    private enum ShowBreakpoint {

        NONE(Color.WHITE),
        SINGLE(new Color(240, 72, 72)),
        PERMANENT(new Color(178, 15, 15));
        private final Color color;

        private ShowBreakpoint(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

    /**
     * Create rectangle widget with specified title and color.
     *
     * @param scene Scene the widget belongs to
     * @param title Title of widget
     * @param color Colro of widget
     */
    ShedWidget(ShedScene scene, String displayName, Color color) {
        super(scene);

        assert displayName != null;

        this.heading = displayName;
        this.setToolTipText(this.heading);

        assert color != null;
        this.color = color;
        this.borderColor = color;
        getActions().addAction(ActionFactory.createPopupMenuAction(this));

        setPreferredBounds(null);
        setMinimumSize(new Dimension(ShedWidget.width, ShedWidget.height));
    }

    /**
     * Get display name of the widget
     *
     * @return display name
     */
    public final String getDisplayName() {
        return heading;
    }

    public final void setDisplayName(String newDisplayName) {
        this.heading = newDisplayName;
        this.setToolTipText(this.heading);
        this.revalidate(true);
    }

    void setBorderColor(Color newBorderColor) {
        this.borderColor = newBorderColor;
    }

    /**
     * Set list of lines that will be shown in bold font under title.
     *
     * @param newPresent New list representations of presentVars variables
     */
    public final void setPresent(List<Variable> newPresent) {
        presentVars.clear();
        presentVars.addAll(newPresent);
        revalidate();
    }

    /**
     * Set list of lines that will be shown in red bold font under presentVars
     * lines.
     *
     * @param newMissing New list representations of presentVars variables
     */
    public final void setError(List<Variable> newMissing) {
        errorVars.clear();
        errorVars.addAll(newMissing);
        revalidate();
    }

    /**
     * Set list of lines that will be shown in italic font under errorVars
     * lines.
     *
     * @param newUnused New list representations of errorVars variables
     */
    public final void setUnused(List<Variable> newUnused) {
        unusedVars.clear();
        unusedVars.addAll(newUnused);
        revalidate();
    }

    /**
     * Set intensity of red border, if 0, border will have color of widget, if
     * 100, border will be bright red. Used to show which elements of plan are
     * being executed.
     *
     * @param percent Intensity in percents, 0-100
     */
    public void setActiveIntensity(int percent) {
        intensity = percent;
        this.repaint();
    }

    @Override
    public final IPresenter getPresenter() {
        return actionProvider;
    }

    /**
     * Set new presenter of the widget. Upon creation of the widget, it should
     * be null.
     *
     * @param newPresenter New presenter.
     */
    public final void setPresenter(IPresenter newPresenter) {
        this.actionProvider = newPresenter;
    }

    /**
     * Show strip at the right side of the widget
     *
     * @since What color, dark red or light red
     */
    public void addBreakpoint(boolean single) {
        showBreakpointStrip = single ? ShowBreakpoint.SINGLE : ShowBreakpoint.PERMANENT;
        this.repaint();
    }

    /**
     * Remove colored strip on the right side of widget that was representing
     * the breakpoint.
     */
    public void removeBreakpoint() {
        showBreakpointStrip = ShowBreakpoint.NONE;
        this.repaint();
    }

    /**
     * Return menu associated with the widget (will be used as context menu).
     *
     * @param widget widget on which the popup menu is requested (always this)
     * @param localLocation Where was popup menu invoked, if null, invoked by
     * keyboard
     * @return Created menu or null, if no actions from {@link #getMenuActions()
     * }.
     */
    @Override
    public final JPopupMenu getPopupMenu(Widget widget, Point localLocation) {
        assert widget == this;

        Action[] actions = this.actionProvider.getMenuActions();
        if (actions == null) {
            return null;
        }
        JPopupMenu menu = new JPopupMenu();
        for (Action action : actions) {
            // Action is automatically fired since the item is created from it, no need for addActionListener()
            JMenuItem item = new JMenuItem(action);
            menu.add(item);
        }

        return menu;
    }

    @Override
    protected Rectangle calculateClientArea() {
        Graphics2D gr = getGraphics();
        if (gr == null) {
            throw new IllegalStateException("Trying to compute client area without graphics2D.");
        }
        FontMetrics titleFontMetrics = gr.getFontMetrics(headingFont);
        FontMetrics boldFontMetrics = gr.getFontMetrics(presentFont);
        FontMetrics plainFontMetrics = gr.getFontMetrics(missingFont);
        FontMetrics italicFontMetrics = gr.getFontMetrics(unusedFont);

        int widgetHeight = calculateHeadingY(gr);//titleFontMetrics.getHeight();
        widgetHeight += titleFontMetrics.getHeight();
        widgetHeight += boldFontMetrics.getHeight() * presentVars.size();
        widgetHeight += plainFontMetrics.getHeight() * errorVars.size();
        widgetHeight += italicFontMetrics.getHeight() * unusedVars.size();
        //widgetHeight += italicFontMetrics.getDescent() * 2;

        return new Rectangle(0, 0, ShedWidget.width, widgetHeight);
    }

    private int calculateHeadingY(Graphics2D gr) {
        FontMetrics titleFontMetrics = gr.getFontMetrics(headingFont);
        return titleFontMetrics.getLeading() + titleFontMetrics.getAscent();
    }

    @Override
    protected void paintWidget() {
        Graphics2D gr = getGraphics();
        Rectangle clientArea = getClientArea();

        paintBackground(gr, clientArea);

        if (showBreakpointStrip != ShedWidget.ShowBreakpoint.NONE) {
            gr.setColor(showBreakpointStrip.getColor());
            gr.fillRect(clientArea.width - BREAKPOINT_STRIP_WIDTH, clientArea.y, BREAKPOINT_STRIP_WIDTH, clientArea.height);
        }

        paintText(gr);
    }

    private void paintBackground(Graphics2D gr, Rectangle clientArea) {
        Color focusColor = Color.BLACK;
        Color drawBorderColor = getState().isObjectFocused() ? focusColor : this.borderColor;
        if (intensity > 0) {
            Color fullColor = Color.RED;

            drawBorderColor = new Color(
                    color.getRed() + (fullColor.getRed() * intensity - color.getRed() * intensity) / 100,
                    color.getGreen() + (fullColor.getGreen() * intensity - color.getGreen() * intensity) / 100,
                    color.getBlue() + (fullColor.getBlue() * intensity - color.getBlue() * intensity) / 100);
        }

        gr.setColor(drawBorderColor);
        gr.fillRect(clientArea.x, clientArea.y, clientArea.width, clientArea.height);
        gr.setColor(this.color);
        gr.fillRect(clientArea.x + BORDER_WIDTH, clientArea.y + BORDER_WIDTH, clientArea.width - 2 * BORDER_WIDTH, clientArea.height - 2 * BORDER_WIDTH);
    }

    private void paintText(Graphics2D gr) {
        int x = ShedWidget.textOfs;
        int y = calculateHeadingY(gr);

        y += drawLines(Color.BLACK, headingFont, Arrays.asList(heading), x, y, gr);
        y += drawLines(Color.BLACK, presentFont, convert(presentVars), x, y, gr);
        y += drawLines(Color.RED, missingFont, convert(errorVars), x, y, gr);
        drawLines(Color.BLACK, unusedFont, convert(unusedVars), x, y, gr);
    }

    private List<String> convert(List<Variable> vars) {
        List<String> lines = new LinkedList<String>();
        for (Variable var : vars) {
            lines.add(var.toString());
        }
        return lines;
    }

    /**
     * Draw lines of text.
     *
     * @param lines List of lines to draw
     * @param x Left coordinate of text inside the widget
     * @param y Bottom coordinate of first line of text
     * @return Height of painted lines
     */
    private int drawLines(Color fontColor, Font font, List<String> lines, int x, int y, Graphics2D gr) {
        gr.setColor(fontColor);
        gr.setFont(font);
        FontMetrics fontMetrics = gr.getFontMetrics(font);

        int linesHeight = 0;
        for (String missingLine : lines) {
            drawFittingString(x, y, missingLine, gr, fontMetrics);
            y += fontMetrics.getHeight();
            linesHeight += fontMetrics.getHeight();
        }
        return linesHeight;
    }

    private void drawFittingString(int x, int y, String text, Graphics2D gr, FontMetrics fm) {
        AffineTransform originalTransform = gr.getTransform();
        gr.translate(x, y);
        String fittingText = getFittingString(text, fm, ShedWidget.width);
        gr.drawString(fittingText, 0, 0);
        gr.setTransform(originalTransform);
    }

    /**
     * Get string that will be shorter than maxWidth. If Passed text is too long
     * return part of text with "..." at the end.
     *
     * @param text Text we want to fit into maxWidth
     * @param metrics font metrics to measure length of strings
     * @param maxWidth maximal length the returned string can fit into.
     * @return text if it fits into maxWidth, otherwise maximal
     * text.substring(0,X).concat("...") that will fit into maxWidth.
     */
    private String getFittingString(String text, FontMetrics metrics, int maxWidth) {
        if (metrics.stringWidth(text) < maxWidth) {
            return text;
        }

        for (int index = text.length() - 1; index > 0; index--) {
            String shorter = text.substring(0, index).concat("...");
            if (metrics.stringWidth(shorter) < maxWidth) {
                return shorter;
            }
        }
        return "...";
    }

    @Override
    public String toString() {
        return "ShedWidget: " + getDisplayName();
    }
}
