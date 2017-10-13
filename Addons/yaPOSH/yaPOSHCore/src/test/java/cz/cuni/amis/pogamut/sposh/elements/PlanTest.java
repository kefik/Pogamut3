package cz.cuni.amis.pogamut.sposh.elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.junit.Assert;

/**
 * Contains various methods that are useful in other tests of POSH, especially loading of
 * plans.
 *
 * @author Honza
 */
public abstract class PlanTest extends Assert {

    public String loadPlan(String relativeResourcePath) throws IOException {
        String resourcePath = this.getClass().getPackage().getName().replace('.', '/') + '/' + relativeResourcePath;

        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);

        if (is == null) {
            fail("Unable to open resource \"" + resourcePath + "\"");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
            sb.append('\n');
        }

        reader.close();

        return sb.toString();
    }

    public PoshPlan parsePlan(String relativeResourcePath) throws IOException, ParseException {
        String plan = loadPlan(relativeResourcePath);
        PoshParser parser = new PoshParser(new StringReader(plan));
        return parser.parsePlan();
    }
    
    public String getMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }
    
}
