package cz.cuni.amis.pogamut.sposh.executor;

import cz.cuni.amis.pogamut.sposh.elements.EnumValue;
import cz.cuni.amis.pogamut.sposh.executor.ParamInfo.Type;
import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;

public class ParamInfoTest extends Assert {

    @Test(expected = IllegalArgumentException.class)
    public void representsExact() {
        ParamInfo.Type.findType("non.existent");
    }

    @Test(expected = IllegalArgumentException.class)
    public void existingNonrepresentingClass() {
        Type.findType(BigInteger.class.getName());
    }

    @Test
    public void representsboolean() {
        assertEquals(Type.BOOLEAN, Type.findType("boolean"));
    }
    
    @Test
    public void representsBoolean() {
        assertEquals(Type.BOOLEAN, Type.findType("java.lang.Boolean"));
    }
    
    @Test
    public void representsInt() {
        assertEquals(Type.INT, Type.findType("int"));
    }

    @Test
    public void representsInteger() {
        assertEquals(Type.INT, Type.findType("java.lang.Integer"));
    }

    @Test
    public void representsdouble() {
        assertEquals(Type.DOUBLE, Type.findType("double"));
    }

    @Test
    public void representsDouble() {
        assertEquals(Type.DOUBLE, Type.findType("java.lang.Double"));
    }

    @Test
    public void representsString() {
        assertEquals(Type.STRING, Type.findType("java.lang.String"));
    }

    @Test
    public void representsSomeEnum() {
        assertEquals(Type.ENUM, Type.findType(TestEnum.class.getName()));
    }

    @Test
    public void stringIsAssignableToSTRING() {
        ParamInfo paramInfo = new ParamInfo("$name", Type.STRING, String.class.getName());
        assertTrue(paramInfo.isValueAssignable("Hello"));
    }

    @Test
    public void incompatibleAssignemntToSTRING() {
        ParamInfo paramInfo = new ParamInfo("$name", Type.STRING, String.class.getName());
        assertFalse(paramInfo.isValueAssignable(15));
    }
    
    @Test
    public void booleanAssignableToBOOLEAN() {
        ParamInfo paramInfo = new ParamInfo("$name", Type.BOOLEAN, int.class.getName());
        assertTrue(paramInfo.isValueAssignable(true));
        assertTrue(paramInfo.isValueAssignable(false));
    }
    
    @Test
    public void BooleanAssignableToBOOLEAN() {
        ParamInfo paramInfo = new ParamInfo("$name", Type.BOOLEAN, Integer.class.getName());
        assertTrue(paramInfo.isValueAssignable(Boolean.TRUE));
        assertTrue(paramInfo.isValueAssignable(Boolean.FALSE));
    }
    
    @Test
    public void incompatibleAssignemntToBOOLEAN() {
        ParamInfo paramInfo = new ParamInfo("$name", Type.BOOLEAN, int.class.getName());
        assertFalse(paramInfo.isValueAssignable(1.1));
        assertFalse(paramInfo.isValueAssignable(new Double(1.1)));
        assertFalse(paramInfo.isValueAssignable(1.1f));
        assertFalse(paramInfo.isValueAssignable(new Float(1.1)));
        assertFalse(paramInfo.isValueAssignable(99));
        assertFalse(paramInfo.isValueAssignable(new Integer(99)));
        assertFalse(paramInfo.isValueAssignable("true"));
        assertFalse(paramInfo.isValueAssignable("false"));
    }

    @Test
    public void intAssignableToINT() {
        ParamInfo paramInfo = new ParamInfo("$name", Type.INT, int.class.getName());
        assertTrue(paramInfo.isValueAssignable(15));
    }
    
    @Test
    public void intAssignableToINTeger() {
        ParamInfo paramInfo = new ParamInfo("$name", Type.INT, Integer.class.getName());
        assertTrue(paramInfo.isValueAssignable(15));
    }
    
    @Test
    public void integerAssignableToINT() {
        ParamInfo paramInfo = new ParamInfo("$name", Type.INT, int.class.getName());
        assertTrue(paramInfo.isValueAssignable(new Integer(99)));
    }

    @Test
    public void integerAssignableToINTeger() {
        ParamInfo paramInfo = new ParamInfo("$name", Type.INT, Integer.class.getName());
        assertTrue(paramInfo.isValueAssignable(new Integer(99)));
    }
    
    @Test
    public void enumAssignableToEnum() {
        ParamInfo paramInfo = new ParamInfo("$name", Type.ENUM, TestEnum.class.getName());
        assertTrue(paramInfo.isValueAssignable(new EnumValue(TestEnum.class.getName() + '.' + TestEnum.A.name())));
    }

    @Test
    public void stringNotAssignableToEnum() {
        ParamInfo paramInfo = new ParamInfo("$name", Type.ENUM, TestEnum.class.getName());
        assertFalse(paramInfo.isValueAssignable("Hello"));
    }
    
    @Test
    public void wrongEnumNotAssignableToEnum() {
        ParamInfo paramInfo = new ParamInfo("$name", Type.ENUM, TestEnum.class.getName());
        assertFalse(paramInfo.isValueAssignable(new EnumValue("cz.team.OUR")));
    }
    
    @Test
    public void nilAssignableToEverything() {
        ParamInfo paramInfo = new ParamInfo("$name", Type.INT, Integer.class.getName());
        assertTrue(paramInfo.isValueAssignable(null));
        
        paramInfo = new ParamInfo("$name", Type.DOUBLE, Double.class.getName());
        assertTrue(paramInfo.isValueAssignable(null));
        
        paramInfo = new ParamInfo("$name", Type.STRING, String.class.getName());
        assertTrue(paramInfo.isValueAssignable(null));
        
        paramInfo = new ParamInfo("$name", Type.ENUM, TestEnum.class.getName());
        assertTrue(paramInfo.isValueAssignable(null));
    }
}

enum TestEnum {

    A, B, C;
}
