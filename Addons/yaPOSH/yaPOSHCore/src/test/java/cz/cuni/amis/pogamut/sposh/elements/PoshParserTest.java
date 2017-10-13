package cz.cuni.amis.pogamut.sposh.elements;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;

import cz.cuni.amis.pogamut.sposh.exceptions.DuplicateNameException;

/**
 * Test some examples of correct and incorrect posh plans.
 * @author Honza
 */
public class PoshParserTest extends PlanTest {
    private PoshPlan testPoshPlan(String testName, String relativeResourcePath) throws IOException, ParseException {
        System.out.println("Test: " + testName);

        String plan = loadPlan(relativeResourcePath);
        PoshParser parser = new PoshParser(new StringReader(plan));
        return parser.parsePlan();
    }

    @Test
    public void testAttackBot() throws Exception {
        testPoshPlan(getMethodName(), "testplans/joanna/attackbot.lap");
    }

    @Test
    public void testEducateMeMonk() throws Exception {
        testPoshPlan(getMethodName(), "testplans/joanna/educate-me+monk.lap");
    }

    @Test
    public void testPoshBotFollow() throws Exception {
        testPoshPlan(getMethodName(), "testplans/joanna/poshbotfollow.lap");
    }

    @Test
    public void testSheepDog() throws Exception {
        testPoshPlan(getMethodName(), "testplans/joanna/sheep-dog.lap");
    }

    @Test
    public void testStayGroom() throws Exception {
        testPoshPlan(getMethodName(), "testplans/joanna/stay-groom.lap");
    }

    @Test
    public void testCVarsPresnet() throws Exception {
        testPoshPlan(getMethodName(), "testplans/vars/CompVarTest.lap");
    }

    @Test
    public void testCVarsNotPresnet() throws Exception {
        try {
            testPoshPlan(getMethodName(), "testplans/vars/CompVarFail.lap");
        } catch (ParseException ex) {
            if (ex.getMessage().contains("<VARIABLE>")) {
                return;
            }
            return;
        }
        fail("Should throw exception, no parameters");
    }

    @Test
    public void testCVarsMissingDefault() throws Exception {
        try {
            testPoshPlan(getMethodName(), "testplans/vars/CompVarMissingDefault.lap");
        } catch (ParseException ex) {
            if (ex.getMessage().contains("\"=\"")) {
                return;
            }
            return;
        }
        fail("Should throw exception, no default value for parameter");
    }

    @Test
    public void testAPVarsMissingParameters() throws Exception {
        try {
            testPoshPlan(getMethodName(), "testplans/vars/APVarsMissingParameters.lap");
        } catch (ParseException ex) {
            if (ex.getMessage().contains("\"murder\"")) {
                return;
            }
            return;
        }
        fail("Should throw exception, no default value for parameter");
    }

    @Test
    public void testAPVarsParameters() throws Exception {
        testPoshPlan(getMethodName(), "testplans/vars/APVarsParameters.lap");
    }

    @Test
    public void testSenseVarsInComp() throws Exception {
        testPoshPlan(getMethodName(), "testplans/vars/SenseVarsInComp.lap");
    }

    @Test
    public void testAPActionVar() throws Exception {
        testPoshPlan(getMethodName(), "testplans/vars/APActionVars.lap");
    }

    @Test
    public void testCompDuplicateVars() throws Exception {
        try {
            testPoshPlan(getMethodName(), "testplans/vars/CompDuplicateVars.lap");
        } catch (ParseException ex) {
            if (ex.getMessage().contains("$duplicateVariable")) {
                return;
            }
        }
        fail("Should throw exception, competence has declared two $duplicateVariable variables.");
    }

    // call comptence with variables, basically same as calling actions
    @Test
    public void testCallC() throws Exception {
        testPoshPlan(getMethodName(), "testplans/vars/CallC.lap");
    }

    // call comptence with variables, basically same as calling actions
    @Test
    public void testCallPrimitiveFromC() throws Exception {
        testPoshPlan(getMethodName(), "testplans/vars/CallPrimitiveFromC.lap");
    }

    // call comptence with variables, basically same as calling actions
    @Test
    public void testCallCFromDC() throws Exception {
        testPoshPlan(getMethodName(), "testplans/vars/CallCFromDC.lap");
    }

    @Test
    public void testCallPrimitiveFromDC() throws Exception {
        testPoshPlan(getMethodName(), "testplans/vars/CallPrimitiveFromDC.lap");
    }

    @Test
    public void testSenseTriggerVars() throws Exception {
        testPoshPlan(getMethodName(), "testplans/vars/SenseTriggerVars.lap");
    }

    @Test
    public void testMissingVars() throws Exception {
        try {
            testPoshPlan(getMethodName(), "testplans/vars/MissingVars.lap");
        } catch (ParseException ex) {
            if (ex.getMessage().contains("$var1") &
                    ex.getMessage().contains("Encountered")) {
                return;
            }
        }
        fail("Should throw exception, missing \"vars\" keyword.");
    }

    @Test
    public void testActionNamedVariable() throws Exception {
        testPoshPlan(getMethodName(), "testplans/vars/ActionNamedVariable.lap");
    }

    @Test
    public void testNamedParameterDuplicate() throws Exception {
        try {
            testPoshPlan(getMethodName(), "testplans/vars/NamedParameterDuplicate.lap");
        } catch (ParseException ex) {
            if (ex.getMessage().contains("\"$variable\"") &
                    ex.getMessage().contains("already") &
                    ex.getMessage().contains("defined")) {
                return;
            }
        }
        fail("Should throw exception, two named parameters were defined.");
    }

    @Test
    public void testNamedCallTest() throws Exception {
        testPoshPlan(getMethodName(), "testplans/vars/NamedCallTest.lap");
    }

    @Test
    public void testTestStringVariable() throws Exception {
        testPoshPlan(getMethodName(), "testplans/vars/TestStringVariable.lap");
    }

    @Test(expected=ParseException.class)
    public void twoDblQuotes() throws Exception {
        testPoshPlan(getMethodName(), "testplans/vars/VarStringFail.lap");
    }

    // Check that names with . in them are OK
    @Test
    public void testNames() throws Exception {
        testPoshPlan(getMethodName(), "testplans/001Names.lap");
    }
    
    @Test(expected=DuplicateNameException.class)
    public void testDuplicateDriveNames() throws Exception {
        testPoshPlan(getMethodName(), "testplans/002DuplicateDriveNames.lap");
    }

    @Test(expected=DuplicateNameException.class)
    public void testDuplicateCENames() throws Exception {
        testPoshPlan(getMethodName(), "testplans/003DuplicateCENames.lap");
    }

    @Test(expected=DuplicateNameException.class)
    public void testSequenceAndCompetenceWithSameName() throws Exception {
        testPoshPlan(getMethodName(), "testplans/004-AP-name-duplicated-in-C.lap");
    }

    @Test(expected=DuplicateNameException.class)
    public void testCompetenceAndSequenceWithSameName() throws Exception {
        testPoshPlan(getMethodName(), "testplans/005-C-name-duplicated-in-AP.lap");
    }
    
    @Test
    public void testADUsed() throws Exception {
    	PoshPlan plan = testPoshPlan("adoptCompetence-1", "testplans/006-AD-used.lap");
    	assertTrue("AD adoptCompetence1 not present!", plan.getAD("adoptCompetence1") != null);
    	assertTrue("AD adoptCompetence2 not present!", plan.getAD("adoptCompetence2") != null);
    }
    
    @Test
    public void testADUsed2() throws Exception {
    	PoshPlan plan = testPoshPlan("adoptCompetence-2", "testplans/007-AD-used2.lap");
    	assertTrue("AD adoptCompetence1 not present!", plan.getAD("adoptCompetence1") != null);
    	assertTrue("AD adoptCompetence2 not present!", plan.getAD("adoptCompetence2") != null);
    	assertTrue("AD adoptAP1 not present!", plan.getAD("adoptAP1") != null);
    	assertTrue("AD adoptAP2 not present!", plan.getAD("adoptAP2") != null);
    	assertTrue("AD adoptAction1 not present!", plan.getAD("adoptAction1") != null);
    }
    
    @Test
    public void senseBooleanOperand() throws IOException, ParseException {
    	PoshPlan plan = testPoshPlan("Boolean operand true recognized", "testplans/008-Sense-BooleanOperand.lap");
        DriveCollection dc = plan.getDriveCollection();
        List<DriveElement> drives = dc.getDrives();
        
    	assertEquals(Boolean.TRUE, drives.get(0).getTrigger().get(0).getOperand());
    	assertEquals(Boolean.TRUE, drives.get(1).getTrigger().get(0).getOperand());
    	assertEquals(Boolean.TRUE, drives.get(2).getTrigger().get(0).getOperand());
    	
      	assertEquals(Boolean.FALSE, drives.get(3).getTrigger().get(0).getOperand());
    	assertEquals(Boolean.FALSE, drives.get(4).getTrigger().get(0).getOperand());
    	assertEquals(Boolean.FALSE, drives.get(5).getTrigger().get(0).getOperand());
}

}

