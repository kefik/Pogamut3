package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.utils.SafeEquals;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;

public class TCMessage implements IWorldChangeEvent, IWorldEvent, Serializable {

	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = 1656106303299952920L;
	
	private long simTime;
	private IToken messageType;
	
	/**
	 * We have to transport messages as byte[] because the server would not have concrete message classes available for deserialization... 
	 */
	private byte[] messageData;
	
	private UnrealId source;
	
	private TCRecipient target;
	
	private boolean excludeMyselfIfApplicable;
	
	private int channelId;
	
	private UnrealId targetId;
	
	private transient boolean resolved = false;
	private transient Serializable message = null;
	
	public TCMessage(UnrealId source, TCRecipient target, boolean excludeMyselfIfApplicable, IToken messageType, Serializable message, long simTime) {
		this.source = source;
		this.target = target;
		this.excludeMyselfIfApplicable = excludeMyselfIfApplicable;
		this.messageType = messageType;
		this.message = message;
		
		// TODO: use {@link ObjectManager} and pool instances!
		if (message == null) {
			this.messageData = null; 
		} else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutput out = null;
			try {
				out = new ObjectOutputStream(bos);
				out.writeObject(message);			
				this.messageData = bos.toByteArray();
			} catch (IOException e) {
				throw new PogamutException("Failed to serialize: " + message, e, this);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
					}
				}
				out = null;
				if (bos != null) {
					try {
						bos.close();
					} catch (IOException e) {
					}
					bos = null;
				}
			}
		}
		
		this.simTime = simTime;
	}
	
	private void readObject(ObjectInputStream ois) {
		try {
			ois.defaultReadObject();			
			if (this.messageType != null) {
				this.messageType = Tokens.get(this.messageType.getToken());
			}
			if (this.source != null) {
				this.source = UnrealId.get(source.getStringId());
			}
			
			this.resolved = false;
			this.message = null;
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			throw new RuntimeException("Failed to deserialize the object.", e);
		}
	}
	
	public IToken getMessageType() {
		return messageType;
	}
	
	public boolean isMessageType(String type) {
		return SafeEquals.equals(this.messageType, Tokens.get(type));
	}
	
	public boolean isMessageType(IToken type) {
		return SafeEquals.equals(this.messageType, type);
	}
	
	/**
	 * Attempts to deserialize {@link #messageData} and returns them.
	 * 
	 * Throws {@link PogamutException} if unsuccessful.
	 * 
	 * @return
	 * @throws PogamutException
	 */
	public Serializable getMessage() throws PogamutException {
		if (this.messageData == null) return null;
		if (this.message == null) {
			// DESERIALIZE
			// TODO: use {@link ObjectManager} and pool instances!
			ByteArrayInputStream bis = new ByteArrayInputStream(messageData);
			ObjectInput in = null;
			try {
			  in = new ObjectInputStream(bis);
			  message = (Serializable)in.readObject(); 		  
			} catch (Exception e) {
				throw new PogamutException("Failed to deserialize TCMessage of type " + messageType.getToken() + "...", e, this);
			} finally {
			  if (bis != null) {
				  try {
					bis.close();
				} catch (IOException e) {
				}
				  bis = null;
			  }
			  if (in != null) {
				  try {
					in.close();
				} catch (IOException e) {
				}
				in = null;
			  }
			}
		}
		return message;
	}

	public Serializable getData() {
		return messageData;
	}

	public TCRecipient getTarget() {
		return target;
	}
	
	public boolean isTarget(TCRecipient target) {
		return SafeEquals.equals(this.target, target);
	}
		
	public boolean isExcludeMyselfIfApplicable() {
		return excludeMyselfIfApplicable;
	}

	public UnrealId getSource() {
		return source;
	}
	
	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public UnrealId getTargetId() {
		return targetId;
	}

	public void setTargetId(UnrealId targetId) {
		this.targetId = targetId;
	}

	/**
	 * CLIENT SUPPORT for implementation of TCMessage filters, you can check whether this message has already been resolved by other filter. 
	 * @return
	 */
	public boolean isResolved() {
		return resolved;
	}

	/**
	 * CLIENT SUPPORT for implementation of TCMessage filters, you can mark this message as "resolved" within your logic to let your further filters know it was handled...
	 */
	public void markResolved() {
		this.resolved = true;
	}

	@Override
	public long getSimTime() {
		return simTime;
	}
	
	@Override
	public String toString() {
		return "TCMessage[from=" + (source == null ? "NULL" : source.getStringId()) + ", messageType=" + (messageType == null ? "NULL" : messageType.getToken()) + ", target=" + target + "]";
	}
	
}
