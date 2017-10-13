package cz.cuni.amis.nb.pogamut.unreal.timeline.widgets;

import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLEntity;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Honza
 */
public class StripWidget extends Widget {
	protected TLEntity entity;
	StripWidget(Scene scene, TLEntity entity, Border border) {
		super(scene);

		this.entity = entity;
		this.setBorder(border);
	}
}
