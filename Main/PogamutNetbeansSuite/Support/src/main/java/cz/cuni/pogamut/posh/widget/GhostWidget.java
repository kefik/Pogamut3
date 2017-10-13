package cz.cuni.pogamut.posh.widget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 * Simple widget representing the dragged widget in the PoshScene during DnD
 * @author Honza
 */
class GhostWidget extends Widget {

	PoshScene scene;
	PoshWidget associatedWidget;
	Font headlineFont = new Font("Helvetica", Font.BOLD, 12);
	Font commentFont = new Font("Helvetica", Font.ITALIC, 10);
	protected LabelWidget headline;
	protected LabelWidget comment;

	/**
	 * Create a new GhostWidget based on information about original widget
	 * @param original widget we will use info (headline, comment) from.
	 */
	GhostWidget(PoshWidget original) {
		super(original.getPoshScene());

		associatedWidget = original;

		scene = original.getPoshScene();

		headline = new LabelWidget(original.getPoshScene(),
			original.getHeadlineText());
		comment = new LabelWidget(original.getPoshScene(),
			original.getCommentText());

		headline.setFont(headlineFont);
		comment.setFont(commentFont);

		this.setBorder(BorderFactory.createRoundedBorder(15, 15, 4, 4, original.getType().getColor(), Color.DARK_GRAY));
		this.setForeground(Color.BLACK);
		this.setPreferredLocation(original.getLocation());
		this.setMinimumSize(new Dimension(120, 10));
		this.setLayout(LayoutFactory.createVerticalFlowLayout());

		this.addChild(headline);
		this.addChild(comment);
	}
}
