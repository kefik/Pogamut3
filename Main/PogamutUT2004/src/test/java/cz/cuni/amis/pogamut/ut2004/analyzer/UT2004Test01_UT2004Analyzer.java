package cz.cuni.amis.pogamut.ut2004.analyzer;

import org.junit.Test;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004AnalyzerFactory;
import cz.cuni.amis.pogamut.ut2004.test.UT2004Test;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004AnalyzerRunner;

/**
 * Tests guice factory.
 */
public class UT2004Test01_UT2004Analyzer extends UT2004Test {

    @Test
    public void test01_AnalyzerConnect() {
    	UT2004AnalyzerRunner<IUT2004Analyzer, UT2004AnalyzerParameters> runner = new UT2004AnalyzerRunner<IUT2004Analyzer, UT2004AnalyzerParameters>(
    		new UT2004AnalyzerFactory(
    			new UT2004AnalyzerModule()
    		)
    	);
    	IUT2004Analyzer analyzer = runner.startAgent();
    	System.out.println("Analyzer is running...");
        System.out.println("---/// TEST OK ///---");
        try {
        	analyzer.stop();
        } finally {
        	Pogamut.getPlatform().close();
        }
    }

}

    