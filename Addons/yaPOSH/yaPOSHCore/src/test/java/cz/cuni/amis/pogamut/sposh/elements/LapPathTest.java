package cz.cuni.amis.pogamut.sposh.elements;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

public class LapPathTest extends PlanTest {

    private PoshPlan plan;
    private DriveCollection dc;

    private LapPath getDrivePath(int driveId) {
        return new LapPath().concat(LapType.PLAN, 0).concat(LapType.DRIVE_COLLECTION, 0).concat(LapType.DRIVE_ELEMENT, driveId);
    }

    @Before
    public void setUp() throws IOException, ParseException {
        this.plan = parsePlan("testplans/LapPath-00-variouspaths.lap");
        this.dc = plan.getDriveCollection();
    }

    @Test(expected = IllegalStateException.class)
    public void emptyPath() {
        LapPath path = new LapPath();
        path.traversePath(plan);
    }

    @Test
    public void planPath() {
        LapPath path = new LapPath().concat(LapType.PLAN, 0);
        PoshElement node = path.traversePath(plan);

        assertEquals(plan, node);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void illegalPlanPath() {
        LapPath path = new LapPath().concat(LapType.PLAN, 1);
        path.traversePath(plan);
    }

    @Test(expected = IllegalStateException.class)
    public void firstLinkMustBePlan() {
        LapPath path = new LapPath().concat(LapType.DRIVE_COLLECTION, 0);
        path.traversePath(plan);
    }

    @Test
    public void dcPath() {
        LapPath path = new LapPath().concat(LapType.PLAN, 0).concat(LapType.DRIVE_COLLECTION, 0);
        PoshElement dc = path.traversePath(plan);

        assertEquals(plan.getDriveCollection(), dc);
    }

    @Test(expected = IllegalStateException.class)
    public void secondLinkMustBeDC() {
        LapPath path = new LapPath().concat(LapType.PLAN, 0).concat(LapType.ACTION_PATTERN, 0);
        path.traversePath(plan);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void idOfDriveCollectionIsAlwaysZero() {
        LapPath path = new LapPath().concat(LapType.PLAN, 0).concat(LapType.DRIVE_COLLECTION, 2);
        path.traversePath(plan);
    }

    @Test
    public void driveCollectionGoalPath() {
        int goalSenseId = 1;
        LapPath path = new LapPath().concat(LapType.PLAN, 0).concat(LapType.DRIVE_COLLECTION, 0).concat(LapType.SENSE, goalSenseId);
        PoshElement goalSense = path.traversePath(plan);

        assertEquals(dc.getGoal().get(goalSenseId), goalSense);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void driveCollectionIllegalGoalPath() {
        int goalSenseId = 666;
        LapPath path = new LapPath().concat(LapType.PLAN, 0).concat(LapType.DRIVE_COLLECTION, 0).concat(LapType.SENSE, goalSenseId);
        path.traversePath(plan);
    }

    @Test(expected = IllegalStateException.class)
    public void illegalThirdLink() {
        LapPath path = new LapPath().concat(LapType.PLAN, 0).concat(LapType.DRIVE_COLLECTION, 0).concat(LapType.ACTION, 0);
        path.traversePath(plan);
    }

    @Test
    public void drivePath() {
        int driveId = 2;
        LapPath path = getDrivePath(driveId);
        PoshElement drive = path.traversePath(plan);

        assertEquals(dc.getDrives().get(driveId), drive);
    }

    @Test
    public void driveAction() {
        LapPath path = getDrivePath(1).concat(LapType.ACTION, 0);
        PoshElement action = path.traversePath(plan);

        assertEquals(dc.getDrives().get(1).getAction(), action);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void driveActionHasIndexZero() {
        LapPath path = getDrivePath(2).concat(LapType.ACTION, 1);
        path.traversePath(plan);
    }

    @Test
    public void driveTrigger() {
        int driveId = 1;
        int driveTriggerSenseId = 1;
        LapPath path = getDrivePath(driveId).concat(LapType.SENSE, driveTriggerSenseId);
        PoshElement sense = path.traversePath(plan);

        assertEquals(dc.getDrives().get(driveId).getTrigger().get(driveTriggerSenseId), sense);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void driveTriggerIndexOutOfBounds() {
        int driveId = 1;
        int driveTriggerSenseId = 3;
        LapPath path = getDrivePath(driveId).concat(LapType.SENSE, driveTriggerSenseId);
        path.traversePath(plan);
    }

    @Test(expected = IllegalStateException.class)
    public void illegalReference() {
        LapPath path = getDrivePath(2).concat(LapType.ACTION, 0).concat(LapType.DRIVE_COLLECTION, 0);
        path.traversePath(plan);
    }

    @Test
    public void actionPatternPath() {
        int apId = 0;
        LapPath path = getDrivePath(2).concat(LapType.ACTION, 0).concat(LapType.ACTION_PATTERN, apId);
        PoshElement ap = path.traversePath(plan);

        assertEquals(plan.getActionPatterns().get(apId), ap);
    }

    @Test(expected = IllegalStateException.class)
    public void wrongActionPatternForAction() {
        int apId = 1;
        LapPath path = getDrivePath(2).concat(LapType.ACTION, 0).concat(LapType.ACTION_PATTERN, apId);
        path.traversePath(plan);
    }

    @Test
    public void competencePath() {
        int competenceId = 1;
        LapPath path = getDrivePath(3).concat(LapType.ACTION, 0).concat(LapType.COMPETENCE, competenceId);
        PoshElement competence = path.traversePath(plan);

        assertEquals(plan.getCompetences().get(competenceId), competence);
    }

    @Test(expected = IllegalStateException.class)
    public void wrongCompetenceForAction() {
        int competenceId = 2;
        LapPath path = getDrivePath(0).concat(LapType.ACTION, 0).concat(LapType.COMPETENCE, competenceId);
        path.traversePath(plan);
    }

    @Test
    public void actionPatternActionPath() {
        int apId = 0;
        int actionId = 2;
        LapPath path = getDrivePath(2).concat(LapType.ACTION, 0).concat(LapType.ACTION_PATTERN, apId).concat(LapType.ACTION, actionId);
        PoshElement action = path.traversePath(plan);

        assertEquals(plan.getActionPatterns().get(apId).getActions().get(actionId), action);
    }

    @Test(expected = IllegalStateException.class)
    public void actionPatternIncorrectChildtype() {
        int apId = 0;
        LapPath path = getDrivePath(2).concat(LapType.ACTION, 0).concat(LapType.ACTION_PATTERN, apId).concat(LapType.SENSE, 0);
        path.traversePath(plan);
    }

    @Test
    public void actionPatternInActionPatternPath() {
        int firstApId = 0;
        int secondApId = 1;
        int actionId = 1;
        LapPath path = getDrivePath(2).concat(LapType.ACTION, 0).concat(LapType.ACTION_PATTERN, firstApId).concat(LapType.ACTION, actionId).concat(LapType.ACTION_PATTERN, secondApId);
        PoshElement secondAp = path.traversePath(plan);

        assertEquals(plan.getActionPatterns().get(secondApId), secondAp);
    }
    

    
    @Test
    public void choicePath() {
        int competenceId = 1;
        int choiceId = 2;
        LapPath path = getDrivePath(3).concat(LapType.ACTION, 0).concat(LapType.COMPETENCE, competenceId).concat(LapType.COMPETENCE_ELEMENT, choiceId);
        PoshElement choice = path.traversePath(plan);

        assertEquals(plan.getCompetences().get(competenceId).getChildDataNodes().get(choiceId), choice);
    }

    @Test(expected=IllegalStateException.class)
    public void competenceWrongLink() {
        int competenceId = 1;
        LapPath path = getDrivePath(3).concat(LapType.ACTION, 0).concat(LapType.COMPETENCE, competenceId).concat(LapType.DRIVE_ELEMENT, 0);
        path.traversePath(plan);
    }
    
    @Test
    public void choiceTriggerPath() {
        int competenceId = 1;
        int choiceId = 1;
        int choiceTriggerSenseId = 0;
        LapPath path = getDrivePath(3).concat(LapType.ACTION, 0).concat(LapType.COMPETENCE, competenceId).concat(LapType.COMPETENCE_ELEMENT, choiceId).concat(LapType.SENSE, choiceTriggerSenseId);
        PoshElement choiceTrigggerSense = path.traversePath(plan);

        assertEquals(plan.getCompetences().get(competenceId).getChildDataNodes().get(choiceId).getTrigger().get(choiceTriggerSenseId), choiceTrigggerSense);
    }

    @Test
    public void choiceActionPath() {
        int competenceId = 1;
        int choiceId = 1;
        LapPath path = getDrivePath(3).concat(LapType.ACTION, 0).concat(LapType.COMPETENCE, competenceId).concat(LapType.COMPETENCE_ELEMENT, choiceId).concat(LapType.ACTION, 0);
        PoshElement choiceAction = path.traversePath(plan);

        assertEquals(plan.getCompetences().get(competenceId).getChildDataNodes().get(choiceId).getAction(), choiceAction);
    }

    @Test
    public void appendPathTest() {
        LapPath expectedPath = new LapPath().concat(LapType.PLAN,0).concat(LapType.DRIVE_COLLECTION, 0).concat(LapType.DRIVE_ELEMENT,4).concat(LapType.SENSE, 1);

        LapPath first = new LapPath().concat(LapType.PLAN,0).concat(LapType.DRIVE_COLLECTION, 0);
        LapPath second = new LapPath().concat(LapType.DRIVE_ELEMENT,4).concat(LapType.SENSE, 1);
        
        LapPath concatPath = first.concat(second);
        
        assertEquals(expectedPath, concatPath);
    }
    
    @Test
    public void emptySubstring() {
        assertEquals(LapPath.EMPTY, LapPath.EMPTY.subpath(0, 0));
    }
    
    @Test
    public void rightSubstring() {
        LapPath wholePath = LapPath.EMPTY.concat(LapType.PLAN,0).concat(LapType.DRIVE_COLLECTION, 0).concat(LapType.DRIVE_ELEMENT,4).concat(LapType.SENSE, 1);
        LapPath rightSubpath = LapPath.EMPTY.concat(LapType.DRIVE_ELEMENT,4).concat(LapType.SENSE, 1);
        assertEquals(rightSubpath, wholePath.subpath(2, wholePath.length()));
    }

    @Test
    public void leftSubstring() {
        LapPath wholePath = LapPath.EMPTY.concat(LapType.PLAN,0).concat(LapType.DRIVE_COLLECTION, 0).concat(LapType.DRIVE_ELEMENT,4).concat(LapType.SENSE, 1);
        LapPath leftSubpath = LapPath.EMPTY.concat(LapType.PLAN,0).concat(LapType.DRIVE_COLLECTION, 0);
        assertEquals(leftSubpath, wholePath.subpath(0, wholePath.length() - 2));
    }

    @Test
    public void totalSubstring() {
        LapPath wholePath = LapPath.EMPTY.concat(LapType.PLAN,0).concat(LapType.DRIVE_COLLECTION, 0).concat(LapType.DRIVE_ELEMENT,4).concat(LapType.SENSE, 1);
        assertEquals(wholePath, wholePath.subpath(0, wholePath.length()));
    }

    @Test
    public void subpath() {
        LapPath whole = getDrivePath(8).concat(LapType.ACTION, 0).concat(LapType.COMPETENCE, 1).concat(LapType.COMPETENCE_ELEMENT, 5).concat(LapType.ACTION, 0);
        LapPath subpath = LapPath.EMPTY.concat(LapType.ACTION, 0).concat(LapType.COMPETENCE, 1);
        assertEquals(subpath, whole.subpath(3, whole.length() - 2));
    }
    
}
