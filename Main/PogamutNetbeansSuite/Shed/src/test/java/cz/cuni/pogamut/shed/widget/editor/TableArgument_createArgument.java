/*
 * Copyright (C) 2013 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.pogamut.shed.widget.editor;

import cz.cuni.amis.pogamut.sposh.elements.Arguments.Argument;
import cz.cuni.amis.pogamut.sposh.engine.VariableContext;
import cz.cuni.amis.pogamut.sposh.executor.ParamInfo;
import org.junit.*;
import static org.junit.Assert.*;

public class TableArgument_createArgument {

    private VariableContext ctx;

    @Before
    public void setUp() {
        ctx = new VariableContext();
        ctx.put("$variable", "Value");
    }

    @After
    public void tearDown() {
        ctx = null;
    }

    @Test
    public void variableArgument() {
        TableArgument tabArg = new TableArgument("$arg", "$variable", null);
        Argument arg = tabArg.createArgument(ctx);
        assertEquals("$variable", arg.getParameterVariable());
    }

    @Test(expected = IllegalStateException.class)
    public void nonexistentVariableArgument() {
        TableArgument tabArg = new TableArgument("$arg", "$nonexistent", null);
        tabArg.createArgument(ctx);
    }

    @Test(expected = IllegalStateException.class)
    public void blankArgument() {
        TableArgument tabArg = TableArgumentFactory.createBlank(new ParamInfo("name", ParamInfo.Type.INT, int.class.getName()));
        tabArg.createArgument(ctx);
    }

    @Test
    public void valueArgument() {
        TableArgument tabArg = new TableArgument("$arg", "\"value\"", null);
        Argument arg = tabArg.createArgument(ctx);

        assertNull(arg.getParameterVariable());
        assertEquals("value", arg.getValue());
    }

    @Test(expected=IllegalStateException.class)
    public void unparsableArgument() {
        TableArgument tabArg = new TableArgument("$arg", "ah\"oj1456", null);
        tabArg.createArgument(ctx);
    }
}
