package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages;

import java.io.ObjectInputStream;
import java.io.Serializable;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.ut2004.teamcomm.bot.UT2004TCClient;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

public class TCMessageData implements IWorldChangeEvent, IWorldEvent, Serializable {

	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = -8784475440806886083L;
	
	private long simTime;
	
	private IToken messageType;
	
	public TCMessageData() {
		this.simTime = 0;
		setMessageType(Tokens.get(this.getClass().getName()));
	}
	
	public TCMessageData(IToken messageType) {
		this.simTime = 0;
		setMessageType(messageType);
	}
	
	@SuppressWarnings("unused")
	private void readObject(ObjectInputStream ois) {
		try {
			ois.defaultReadObject();			
			if (this.messageType != null) {
				this.messageType = Tokens.get(this.messageType.getToken());
			}
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			throw new RuntimeException("Failed to deserialize the object.", e);
		}
	}
	
	public TCMessageData(long simTime) {
		this.simTime = simTime;
	}
	
	public TCMessageData(IToken messageType, long simTime) {
		this.messageType = messageType;		
		this.simTime = simTime;
	}
	
	public IToken getMessageType() {
		return messageType;
	}

	public void setMessageType(IToken messageType) {
		this.messageType = messageType;
	}

	@Override
	public long getSimTime() {
		return simTime;
	}
	
	/**
	 * INJECTOR - To be used (auto-filled) by {@link UT2004TCClient} only !!!
	 * 
	 * @param simTime
	 */
	public void setSimTime(long simTime) {
		this.simTime = simTime;
	}

}
