package cz.cuni.amis.nb.pogamut.unreal.timeline.view;

import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLDatabase;
import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JToolBar;

/**
 * Timeline toolbar, shown next to switch between MV elements. Used for buttons like
 * play timeline, ect.
 * It also spawns timer task ect.
 * TODO: move timer to controller
 * @author Honza
 */
class TLToolbar extends JToolBar {
    private static final long REPLAY_PERIOD = 200;
    private TLDatabase db;
    private Timer timer = null;
    private PlayTask playTask = null;

    // Action for playing timeline
    private final AbstractAction startPlaying = new AbstractAction("Play") {

        @Override
        public void actionPerformed(ActionEvent e) {
            // we are already playing, do nothing
            if (playTask != null) {
                return;
            }
            playTask = new PlayTask();
            getTimer().schedule(playTask, REPLAY_PERIOD, REPLAY_PERIOD);
        }
    };

    // Action for stopping playback of timeline
    private final AbstractAction stopPlayback = new AbstractAction("Stop") {

        @Override
        public void actionPerformed(ActionEvent e) {
            // nothing is played.
            if (playTask == null) {
                return;
            }

            // if something is played
            playTask.cancel();
            playTask = null;
        }
    };

    public TLToolbar(TLDatabase db) {
        super();

        this.db = db;

        add(startPlaying);
        add(stopPlayback);
    }

    protected TLDatabase getDB() {
        return db;
    }



    private Timer getTimer() {
        if (timer == null) {
            timer = new Timer("PlayTimelineThread");
        }
        return timer;
    }

    
    private class PlayTask extends TimerTask {

        private long last = 0;
        
        @Override
        public void run() {
            if (last == 0) {
                this.last = Calendar.getInstance().getTimeInMillis();
            }
            long current = Calendar.getInstance().getTimeInMillis();

            long taskDelta =  current - last;
            long newDBCurrent = db.getCurrentTime() + taskDelta;

            last = current;

            if (newDBCurrent > db.getEndTime()) {
                this.cancel();
                getTimer().purge();
                playTask = null;
                return;
            }
            db.setCurrentTime(newDBCurrent);
        }

    }

}
