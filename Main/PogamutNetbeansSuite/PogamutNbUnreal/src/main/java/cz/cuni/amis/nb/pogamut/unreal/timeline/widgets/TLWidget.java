package cz.cuni.amis.nb.pogamut.unreal.timeline.widgets;

import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLEntity;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 * Ancestor fo all widgets in timeline.
 * @author Honza
 */
public abstract class TLWidget extends Widget {
	/**
	 * How many milisec equals one pixel
	 */
	public static final int zoomFactor = 100;
	public static final int LEFT_MARGIN = 80;
	protected TLEntity entity;

	protected TLWidget(Scene scene, TLEntity entity) {
		super(scene);

		this.entity = entity;
	}

    /**
     * Get offset from the start of db to start of entity this widget belongs to.
     * @return
     */
	final protected int getStartOffset() {
		long dbStartMilis = entity.getDatabase().getStartTime();
		int delta = (int) (entity.getStartTime() - dbStartMilis) / zoomFactor;

//		System.out.println("getStartOffset " + delta);
		return delta + LEFT_MARGIN;
	}

    /**
     * Get offset from the start of the entity this widget represents to
     * the passed ms.
     * @param ms
     * @return
     */
	final protected int getOffsetFromStart(long ms) {
		long delta = ms - entity.getStartTime();

		int offset = (int) (delta / zoomFactor);

//		System.out.println("getOffsetFromStart " + offset + "( " + ms + " - " + entity.getStartTimestamp() + ")");

		return offset;
	}

	final protected int getTimeframeLength(long startTS, long endTS) {
		return getTimeframeLength(endTS - startTS);
	}

	final protected int getTimeframeLength(long delta) {
		return (int) (delta / zoomFactor);
	}

	final protected long getEntityTimeframe() {
		return entity.getEndTime() - entity.getStartTime();
	}

}
