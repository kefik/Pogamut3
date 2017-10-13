package cz.cuni.amis.utils.flag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.utils.token.Token;

public class Test03_Serialization {

	@Test
	public void test03() {
		System.out.println("Testing de/serialization of the Flag...");
		
		Flag<Boolean> flag1 = new Flag<Boolean>(true);
		Flag<Boolean> flag2 = new Flag<Boolean>(false);
		
		try {
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			ObjectOutputStream output = new ObjectOutputStream(bytesOut);
			
			output.writeObject(flag1);
			output.writeObject(flag2);
			
			byte[] byteArray = bytesOut.toByteArray();
			
			ByteArrayInputStream bytesIn = new ByteArrayInputStream(byteArray);
			
			ObjectInputStream input = new ObjectInputStream(bytesIn);
			
			Flag<Boolean> flag1_read = (Flag)input.readObject();
			Flag<Boolean> flag2_read = (Flag)input.readObject();
			
			Assert.assertTrue("flag1_read should have value 'true'", flag1_read.getFlag().equals(true));
			Assert.assertTrue("flag2_read should have value 'true'", flag2_read.getFlag().equals(false));
			
			System.out.println("Flags read OK");
			
			FlagListenerMock<Boolean> fl1 = new FlagListenerMock<Boolean>();
	        FlagListenerMock<Boolean> fl2 = new FlagListenerMock<Boolean>();
	        FlagListenerMock<Boolean> fl3 = new FlagListenerMock<Boolean>();
	        
	        flag1_read.addListener(fl1);
	        flag1_read.setFlag(false);
	        flag1_read.setFlag(false);
	        
	        flag1_read.addListener(fl2);
	        flag1_read.setFlag(true);
	        flag1_read.setFlag(true);
	        
	        flag1_read.addListener(fl3);
	        flag1_read.setFlag(false);
	        flag1_read.setFlag(false);
	        
	        fl1.checkValuesInOrder("FlagListener1", new Boolean[]{ false, true, false });
	        fl2.checkValuesInOrder("FlagListener2", new Boolean[]{true, false});
	        fl3.checkValuesInOrder("FlagListener3", new Boolean[]{false});
	        
	        flag1_read.removeListener(fl1);
	        flag1_read.setFlag(true);
	        flag1_read.setFlag(true);

	        flag1_read.removeListener(fl2);
	        flag1_read.setFlag(false);
	        flag1_read.setFlag(false);
	        
	        flag1_read.removeListener(fl3);
	        flag1_read.setFlag(true);
	        flag1_read.setFlag(true);
	        
	        fl1.checkValuesInOrder("FlagListener1", new Boolean[0]);
	        fl2.checkValuesInOrder("FlagListener2", new Boolean[]{true});
	        fl3.checkValuesInOrder("FlagListener3", new Boolean[]{true,false});
	        
	        System.out.println("Flag1_read still functional - listeners fire as expected.");
			
		} catch (IOException e) {
			e.printStackTrace();
			Assert.assertTrue("IOException!", false);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			Assert.assertTrue("Class not found exception!", false);
		}
		
		System.out.println("---/// TEST OK ///---");
	}
	
}
