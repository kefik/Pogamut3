package cz.cuni.amis.utils.token;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.Assert;

import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;

public class Test02_Serialization extends BaseTest {

	@Test
	public void test01() {
		Tokens.restart();
		
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		
		Token token1 = Tokens.get("hi");
		Token token2 = Tokens.get("hello");
		Token token3 = Tokens.get("hi");
		Token token4 = Tokens.get("hello");
		
		try {
			ObjectOutputStream output = new ObjectOutputStream(bytesOut);
			
			output.writeObject(token1);
			output.writeObject(token2);
			output.writeObject(token3);
			output.writeObject(token4);
			
			byte[] byteArray = bytesOut.toByteArray();
			
			ByteArrayInputStream bytesIn = new ByteArrayInputStream(byteArray);
			
			ObjectInputStream input = new ObjectInputStream(bytesIn);
			
			Token token1_read = (Token)input.readObject();
			Assert.assertTrue("there should be another token in the stream", token1_read != null);
			Token token2_read = (Token)input.readObject();
			Assert.assertTrue("there should be another token in the stream", token2_read != null);
			Token token3_read = (Token)input.readObject();
			Assert.assertTrue("there should be another token in the stream", token3_read != null);
			Token token4_read = (Token)input.readObject();
			Assert.assertTrue("there should be another token in the stream", token4_read != null);
			
			Assert.assertTrue("read token1 should be equal", token1.equals(token1_read));
			Assert.assertTrue("read token2 should be equal", token2.equals(token2_read));
			Assert.assertTrue("read token3 should be equal", token3.equals(token3_read));
			Assert.assertTrue("read token4 should be equal", token4.equals(token4_read));
			
			Assert.assertTrue("read token1 should have the same hash code", token1.hashCode() == token1_read.hashCode());
			Assert.assertTrue("read token2 should have the same hash code", token2.hashCode() == token2_read.hashCode());
			Assert.assertTrue("read token3 should have the same hash code", token3.hashCode() == token3_read.hashCode());
			Assert.assertTrue("read token4 should have the same hash code", token4.hashCode() == token4_read.hashCode());
			
			Assert.assertTrue("read token1 should represent the same string", token1.getToken().equals(token1_read.getToken()));
			Assert.assertTrue("read token2 should represent the same string", token2.getToken().equals(token2_read.getToken()));
			Assert.assertTrue("read token3 should represent the same string", token3.getToken().equals(token3_read.getToken()));
			Assert.assertTrue("read token4 should represent the same string", token4.getToken().equals(token4_read.getToken()));			
			
		} catch (IOException e) {
			e.printStackTrace();
			Assert.assertTrue("IOException!", false);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			Assert.assertTrue("Class not found exception!", false);
		}
		
		testOk();
	}
	
}
