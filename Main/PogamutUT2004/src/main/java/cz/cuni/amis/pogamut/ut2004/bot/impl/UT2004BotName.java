package cz.cuni.amis.pogamut.ut2004.bot.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.utils.SafeEquals;

/**
 * Provides abstraction over the {@link UT2004Bot} name in the game allowing to append "extra infos" to it.
 * @author Jimmy
 */
public class UT2004BotName {

	private UT2004Bot bot;
	
	private String base;

	private Map<String, String> infos = new HashMap<String, String>();
	
	private String nullKeyValue = null;
	
	public UT2004BotName(UT2004Bot bot, String base) {
		this.bot = bot;
		this.base = base;
	}
	
	public void setNameBase(String base) {
		this.base = base;
		updateName();
	}
	
	/**
	 * Assings 'null' key.
	 * @param tag
	 */
	public void setTag(String tag) {
		if (tag == null) deleteTag();
		else setInfo(null, tag);
	}
	
	/**
	 * Removes 'null' key.
	 */
	public void deleteTag() {
		deleteInfo(null);
	}
	
	/**
	 * Alias for {@link #setInfo(String, String)}(null, value), i.e., assigning 'value' to 'NULL KEY'.
	 * @param value
	 */
	public void setInfo(String value) {
		setInfo(null, value);
	}
	
	/**
	 * @param key CAN BE NULL 
	 * @param value CAN BE NULL (null == delete the key)
	 */
	public void setInfo(String key, String value) {
		if (value == null) {
			deleteInfo(key);
			return;
		}
		if (key == null) {
			if (SafeEquals.equals(value, nullKeyValue)) return;
			nullKeyValue = value;
		} else {
			String oldValue = infos.get(key);
			if (SafeEquals.equals(oldValue, value)) return;
			infos.put(key, value);
		}
		updateName();
	}
	
	/**
	 * @param key CAN BE NULL
	 */
	public void deleteInfo(String key) {
		if (key == null) {
			if (nullKeyValue == null) return;
			nullKeyValue = null;
			updateName();
		} else {
			if (infos.remove(key) != null) {
				updateName();
			}
		}
	}
	
	public void updateName() {
		List<String> keys = new ArrayList<String>(infos.keySet());
		Collections.sort(keys);
		StringBuffer name = new StringBuffer();
		name.append(base);
		if (nullKeyValue != null) { 
			name.append(" [");
			name.append(nullKeyValue);			
			name.append("]");
		}
		for (String key : keys) {
			String value = infos.get(key);
			name.append(" [");
			name.append(key);
			if (value != null) {
				name.append(": ");
				name.append(value);
			}
			name.append("]");
		}
		bot.getAct().act(new Configuration().setName(name.toString()));
	}
	
}
