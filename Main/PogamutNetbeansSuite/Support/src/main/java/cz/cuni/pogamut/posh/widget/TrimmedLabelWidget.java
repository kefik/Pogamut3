/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.pogamut.posh.widget;

import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import org.netbeans.api.visual.widget.LabelWidget;

/**
 *
 * @author Honza
 */
public class TrimmedLabelWidget extends LabelWidget {
	private String originalText;
	private double maxWidth;

	public TrimmedLabelWidget(PoshScene scene, double maxWidth) {
		super(scene);

		this.maxWidth = maxWidth;
	}


	@Override
	public void setLabel(String text) {
		originalText = text;
		
		super.setLabel(getTrimmedText(text, getGraphics(), maxWidth));
	}

	public String getText() {
		return originalText;
	}
	
	private String getTrimmedText(String text, Graphics2D g, double maxWidth) {
		FontRenderContext frc = g.getFontRenderContext();
		Rectangle2D rect = getFont().getStringBounds(text, frc);

		if (rect.getWidth() <= maxWidth)
			return text;

		text += "...";
		do {
			text = text.replaceFirst(".\\.\\.\\.$", "...");

			rect = getFont().getStringBounds(text, frc);
		} while (rect.getWidth() > maxWidth && text.length() > 4);

		return text;
	}

}
