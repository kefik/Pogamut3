package cz.cuni.amis.nb.pogamut.unreal.timeline.widgets;

import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLEntity;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLLogRecorder;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.Widget;

/**
 * Widget representing entity.
 * Consists from basic strip representing the entity and 
 * multiple other for each <tt>LogCategory</tt> it encounters.
 * 
 * @author Honza
 */
public class EntityWidget extends Widget {

    /**
     * Entity that this widget represents.
     */
    TLEntity entity;
    /**
     * Basic strip of widget, consists from name of entity and longer strip
     * representing time, when entity was active.
     */
    EntityAxisStrip stripWidget;
    /**
     * List of widgets that represents every log entity provides.
     */
    ArrayList<LogCategoryWidget> logCategoryWidgets = new ArrayList<LogCategoryWidget>();
    /**
     * Called when end time of entity has changed
     * <p>
     * When range of entity is changed, change length of strip widget
     * representing the entity in the scene and length of strip widgets
     * representing log categories.
     * @param newEndTime
     */
    private TLEntity.Listener updateEndTimeListener = new TLEntity.Adapter() {

        @Override
        public void endTimeChanged(TLEntity entity, long previousEndTime, long endTime) {
            assert SwingUtilities.isEventDispatchThread();

            stripWidget.updateStripRange();
            for (LogCategoryWidget logWidget : logCategoryWidgets) {
                logWidget.updateWidget();
            }
            getScene().validate();

        }
    };
    /**
     * When new log recorder is added to the entity, add the widget through this listener
     */
    private TLEntity.Listener addLogRecorderListener = new TLEntity.Adapter() {

        @Override
        public void logRecorderAdded(TLEntity entity, TLLogRecorder recorder) {
            assert SwingUtilities.isEventDispatchThread();
            addLogCategoryWidget(recorder);
        }
    };

    public EntityWidget(TLScene scene, TLEntity entity) {
        super(scene);

        this.entity = entity;

        setLayout(LayoutFactory.createVerticalFlowLayout());
        // create a strip widget
        this.stripWidget = new EntityAxisStrip(scene, entity);
        this.addChild(stripWidget);

        entity.addListener(updateEndTimeListener);
        entity.addListener(addLogRecorderListener);

        for (TLLogRecorder logRecorder : entity.getLogRecorders()) {
            this.addLogCategoryWidget(logRecorder);
        }
        scene.validate();
    }

    /**
     * Add new widget for <tt>TLLogRecorder</tt> and assign a listener.
     * @param newCategory
     */
    public void addLogCategoryWidget(TLLogRecorder newCategory) {
        assert SwingUtilities.isEventDispatchThread();

        final LogCategoryWidget lcWidget = new LogCategoryWidget((TLScene) getScene(), newCategory);

        this.logCategoryWidgets.add(lcWidget);

        addChild(lcWidget);
        getScene().validate();
    }
}
