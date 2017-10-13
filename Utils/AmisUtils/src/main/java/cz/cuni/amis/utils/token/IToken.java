package cz.cuni.amis.utils.token;

import java.io.Serializable;

import javax.management.MXBean;

@MXBean
public interface IToken extends Serializable {

	public String getToken();

	public long[] getIds();
	
}
