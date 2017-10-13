package cz.cuni.amis.nb.pogamut.unreal.timeline.view;

import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLDatabase;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLEntity;
import cz.cuni.amis.utils.flag.FlagListener;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Slider under map in timeline. Used to change current time and is updated
 * according to current time of {{@link TLDatabase}}.
 * 
 * @author Honza
 */
class TLSlider extends JSlider implements ChangeListener {

    private TLDatabase db;
    private final TLDatabase.Adapter timeListener = new TLDatabase.Adapter() {

        /**
         * When current time changes, update position of cursor
         */
        @Override
        public void currentTimeChanged(long previousCurrentTime, long currentTime) {
            long timeframe = (currentTime - db.getStartTime());

            removeChangeListener(TLSlider.this);
            setValue((int) timeframe);
            addChangeListener(TLSlider.this);
        }

        /*
         * When end time changes, update number of ticks
         * in slider representing elapsed time of db
         */
        @Override
        public void endTimeChanged(long previousEndTime, long endTime) {
            long timeframe = (endTime - db.getStartTime());
            setMaximum((int) timeframe);
        }
    };

    public TLSlider(final TLDatabase db) {
        super(0, (int) (db.getElapsedTime()), (int) (db.getDeltaTime()));

        this.db = db;

        addChangeListener(this);


        this.db.addDBListener(timeListener);
    }

    /**
     * Take value from source aka slider. If value of slider is not equal to
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        TLSlider source = (TLSlider) e.getSource();
        int value = source.getValue();

        if ((value + db.getStartTime()) != db.getCurrentTime()) {
            db.setCurrentTime(value + db.getStartTime());
        }
    }
}
