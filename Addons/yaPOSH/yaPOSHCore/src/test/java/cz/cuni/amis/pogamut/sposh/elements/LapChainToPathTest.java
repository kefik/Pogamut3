package cz.cuni.amis.pogamut.sposh.elements;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

/**
 * Test conversion of {@link LapChain} to {@link LapPath}.
 *
 * @author Honza
 */
public class LapChainToPathTest extends PlanTest {

    private PoshPlan plan;
    private DriveCollection dc;

    @Before
    public void init() throws IOException, ParseException {
        plan = parsePlan("testplans/LapPath-00-variouspaths.lap");
        dc = plan.getDriveCollection();
    }

    private LapPath getDrivePath(int driveId) {
        return LapPath.DRIVE_COLLECTION_PATH.concat(LapType.DRIVE_ELEMENT, driveId);
    }

    private TriggeredAction getActionPatternAction(int actionPatternId, int actionId) {
        return plan.getActionPattern(actionPatternId).getAction(actionId);
    }

    private TriggeredAction getChoiceAction(int competenceId, int choiceId) {
        return plan.getCompetence(competenceId).getChoice(choiceId).getAction();
    }

    private TriggeredAction getDriveAction(int driveId) {
        return dc.getDrive(driveId).getAction();
    }

    @Test
    public void emptyChainGivesDC() {
        LapChain emptyChain = new LapChain();

        assertEquals(LapPath.DRIVE_COLLECTION_PATH, emptyChain.toPath());
    }

    @Test
    public void senseChain() {
        LapPath sensePath = getDrivePath(3).concat(LapType.ACTION, 0).
                concat(LapType.COMPETENCE, 1).concat(LapType.COMPETENCE_ELEMENT, 1).concat(LapType.SENSE, 1);
        LapChain senseChain = new LapChain().derive(getDriveAction(3), plan.getCompetence(1)).derive(plan.getCompetence(1).getChoice(1).getTrigger().get(1));
        
        assertEquals(sensePath, senseChain.toPath());
    }
    
    @Test
    public void driveActionChain() {
        int driveId = 1;
        LapPath drivePath = getDrivePath(driveId).concat(LapType.ACTION, 0);
        TriggeredAction secondDriveAction = getDriveAction(driveId);
        LapChain driveActionChain = new LapChain().derive(secondDriveAction);

        assertEquals(drivePath, driveActionChain.toPath());
    }

    @Test
    public void actionPatternChain() {
        int driveId = 2;
        int actionPatternId = 0;
        LapPath actionPatternPath = getDrivePath(driveId).concat(LapType.ACTION, 0).concat(LapType.ACTION_PATTERN, actionPatternId);

        TriggeredAction secondDriveAction = getDriveAction(driveId);
        ActionPattern referencedActionPattern = plan.getActionPattern(actionPatternId);
        assertEquals(referencedActionPattern.getName(), secondDriveAction.getName());
        LapChain actionPatternChain = new LapChain().derive(secondDriveAction, referencedActionPattern);

        assertEquals(actionPatternPath, actionPatternChain.toPath());
    }

    @Test
    public void competenceChain() {
        int driveId = 3;
        int competenceId = 1;
        LapPath actionPatternPath = getDrivePath(driveId).concat(LapType.ACTION, 0).concat(LapType.COMPETENCE, competenceId);

        TriggeredAction thirdDriveAction = getDriveAction(driveId);
        Competence referencedCompetence = plan.getCompetence(competenceId);
        assertEquals(referencedCompetence.getName(), thirdDriveAction.getName());
        LapChain competenceChain = new LapChain().derive(thirdDriveAction, referencedCompetence);

        assertEquals(actionPatternPath, competenceChain.toPath());
    }

    @Test
    public void adoptChain() {
        int driveId = 4;
        int adoptId = 0;
        LapPath adoptPath = getDrivePath(driveId).concat(LapType.ACTION, 0).concat(LapType.ADOPT, adoptId);

        TriggeredAction driveAction = getDriveAction(driveId);
        Adopt adopt = plan.getAdopt(adoptId);
        assertEquals(adopt.getName(), driveAction.getName());
        LapChain adoptChain = new LapChain().derive(driveAction, adopt);

        assertEquals(adoptPath, adoptChain.toPath());
    }

    @Test
    public void longChain() {
        LapPath longPath = getDrivePath(0).concat(LapType.ACTION, 0).
                concat(LapType.COMPETENCE, 0).concat(LapType.COMPETENCE_ELEMENT, 0).concat(LapType.ACTION, 0).
                concat(LapType.COMPETENCE, 2).concat(LapType.COMPETENCE_ELEMENT, 0).concat(LapType.ACTION, 0).
                concat(LapType.ACTION_PATTERN, 1).concat(LapType.ACTION, 3);

        assertEquals("ac4", longPath.<TriggeredAction>traversePath(plan).getName());

        LapChain longChain = new LapChain().derive(getDriveAction(0), plan.getCompetence(0)).
                derive(getChoiceAction(0, 0), plan.getCompetence(2)).
                derive(getChoiceAction(2, 0), plan.getActionPattern(1)).
                derive(getActionPatternAction(1, 3));

        assertEquals(longPath, longChain.toPath());
    }
}
