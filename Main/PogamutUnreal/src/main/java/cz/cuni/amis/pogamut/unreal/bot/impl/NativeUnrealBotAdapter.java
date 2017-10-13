package cz.cuni.amis.pogamut.unreal.bot.impl;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.introspection.IntrospectionException;
import cz.cuni.amis.introspection.Property;
import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.exceptions.AgentException;
import cz.cuni.amis.pogamut.base.agent.exceptions.CantStartJMXException;
import cz.cuni.amis.pogamut.base.agent.exceptions.JMXAlreadyEnabledException;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.state.impl.AgentStateStarted;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.ILogCategories;
import cz.cuni.amis.pogamut.base.utils.logging.ILogPublisher;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.unreal.bot.IUnrealBot;
import cz.cuni.amis.pogamut.unreal.communication.messages.gbinfomessages.IPlayer;
import cz.cuni.amis.pogamut.unreal.server.IUnrealServer;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.ImmutableFlag;

/**
 * IUnrealAgent adapter for native players. Not all operations are supported.
 * @author ik
 */
public abstract class NativeUnrealBotAdapter implements IUnrealBot {

	private IAgentLogger logger = new IAgentLogger() {
        private LogCategory single = new LogCategory("default");

        private ILogCategories categories = new ILogCategories() {

        	@Override
            public boolean hasCategory(String name) {
        		if (name == null) return false;
        		if (name.equalsIgnoreCase("default")) return true;
                return false;
            }

        	@Override
            public Map<String, LogCategory> getCategories() {
        		HashMap<String, LogCategory> categories = new HashMap<String, LogCategory>();
        		categories.put("default", single);
        		return Collections.unmodifiableMap(categories);
            }

        	@Override
            public String[] getCategoryNames() {
                return new String[]{"default"};
            }

        	@Override
            public String[] getCategoryNamesSorted() {
                return new String[]{"default"};
            }

        	@Override
            public LogCategory getCategory(String name) {
        		if (name == null) throw new UnsupportedOperationException("Not supported for the native bot.");
        		if (name.equals("default")) return single;
                throw new UnsupportedOperationException("Not supported for the native bot.");
            }

        	@Override
            public void setLevel(Level newLevel) {
        		single.setLevel(newLevel);
            }

			@Override
			public void addLogCategory(String name, LogCategory category) {
				throw new UnsupportedOperationException("Not supported for the native bot.");
			}
        };

		@Override
		public void addDefaultConsoleHandler() {
			throw new UnsupportedOperationException("Not supported for the native bot.");
		}

		@Override
		public Handler addDefaultFileHandler(File file) {
			throw new UnsupportedOperationException("Not supported for the native bot.");
		}

		@Override
		public void addDefaultHandler(Handler handler) {
			throw new UnsupportedOperationException("Not supported for the native bot.");
		}

		@Override
		public Handler addDefaultPublisher(ILogPublisher publisher) {
			throw new UnsupportedOperationException("Not supported for the native bot.");
		}

		@Override
		public void addToAllCategories(ILogPublisher logPublisher) {
			single.addHandler(logPublisher);
		}

		@Override
		public void addToAllCategories(Handler handler) {
			single.addHandler(handler);
			
		}

		@Override
		public IAgentId getAgentId() {
			return agentId;
		}

		@Override
		public Map<String, LogCategory> getCategories() {
			return categories.getCategories();
		}

		@Override
		public LogCategory getCategory(IComponent component) {
			return categories.getCategory(component.getComponentId().getToken());
		}

		@Override
		public LogCategory getCategory(String name) {
			return categories.getCategory(name);
		}

		@Override
		public void setLevel(Level newLevel) {
			categories.setLevel(newLevel);
		}

		@Override
		public void enableJMX(MBeanServer mBeanServer, ObjectName parent)
				throws JMXAlreadyEnabledException, CantStartJMXException {
			throw new UnsupportedOperationException("Not supported for the native bot.");
		}

		@Override
		public Integer getNetworkLoggerPort() {
			throw new UnsupportedOperationException("Not supported for the native bot.");
		}

                @Override
		public String getNetworkLoggerHost() {
			throw new UnsupportedOperationException("Not supported for the native bot.");
		}

		@Override
		public void addDefaultNetworkHandler() {
			throw new UnsupportedOperationException("Not supported for the native bot.");
		}

		@Override
		public boolean isDefaultConsoleHandler() {
			throw new UnsupportedOperationException("Not supported for the native bot.");
		}

		@Override
		public boolean isDefaultNetworkHandler() {
			throw new UnsupportedOperationException("Not supported for the native bot.");
		}

		@Override
		public void removeDefaultConsoleHandler() {
			throw new UnsupportedOperationException("Not supported for the native bot.");
		}

		@Override
		public void removeDefaultHandler(Handler handler) {
			throw new UnsupportedOperationException("Not supported for the native bot.");
		}

		@Override
		public void removeDefaultNetworkHandler() {
			throw new UnsupportedOperationException("Not supported for the native bot.");
		}

		@Override
		public void removeFromAllCategories(Handler handler) {
			throw new UnsupportedOperationException("Not supported for the native bot.");
		}

		@Override
		public Handler getDefaultConsoleHandler() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Handler getDefaultNetworkHandler() {
			// TODO Auto-generated method stub
			return null;
		}
     
    };
	
    protected IPlayer player = null;
    protected IUnrealServer server = null;
    protected IAct act;
    protected IWorldView worldView = null;
    /**
     * Configuration of this bot.
     */
   // protected ConfigChange config = null;
    private Flag<IAgentState> state = new Flag<IAgentState>(new AgentStateStarted("Native bot is running."));
    private IAgentId agentId; 

    public NativeUnrealBotAdapter(IPlayer player, IUnrealServer server, IAct act, IWorldView worldView) {
        this.player = player;
        this.agentId = new AgentId(player.getName());
        this.server = server;
        this.act = act;
        this.worldView = worldView;
    }

    @Override
    public IAct getAct() {
        throw new UnsupportedOperationException("Native bots cannot be controled from Java.");
    }
    
    @Override
    public IWorldView getWorldView() {
    	return worldView;
    }
    
    @Override
    public IAgentId getComponentId() {
    	return agentId;
    }

    @Override
    public String getName() {
        return agentId.getName().getFlag();
    }

    @Override
    public IAgentLogger getLogger() {
        return this.logger;
    }
    
    @Override
	public IComponentBus getEventBus() {
		throw new UnsupportedOperationException("Not supported for the native bot.");
	}

    @Override
    public ImmutableFlag<IAgentState> getState() {
        return state.getImmutable();
    }

    @Override
    public void start() throws AgentException {
        throw new UnsupportedOperationException("Native bot is already started.");
    }
    
    @Override
    public void startPaused() throws AgentException {
        throw new UnsupportedOperationException("Native bot is already started.");
    }

    @Override
    public void pause() throws AgentException {
        throw new UnsupportedOperationException("Not supported for the native bot.");
    }


    @Override
    public void resume() throws AgentException {
        throw new UnsupportedOperationException("Not supported for the native bot.");
    }

    @Override
    public void kill() {
        stop();
    }

    private Folder folder = new Folder("empty") {
        private Folder[]  subfolders = new Folder[]{};
        private Property[]  properties = new Property[]{};

        @Override
        public Folder[] getFolders() throws IntrospectionException {
            return subfolders;
        }

        @Override
        public Property[] getProperties() throws IntrospectionException {
            return properties;
        }
    };
    
    @Override
    public Folder getIntrospection() {
       return folder;
    }

    @Override
    public Location getLocation() {
        return player.getLocation();
    }

    @Override
    public Velocity getVelocity() {
        return player.getVelocity();
    }

    @Override
    public Rotation getRotation() {
        return player.getRotation();
    }

/* TODO
    @Override
    public void setBoolConfigure(BoolBotParam param, boolean value) {
        try {
            Configuration configuration = new Configuration();
            configuration.setId(player.getId());
            ConfigChange confCh = getConfig();
            if (confCh != null) {
                configuration.copy(confCh);
                param.setField(confCh, value);
            }
            param.set(configuration, value);
            act.act(configuration);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean getBoolConfigure(BoolBotParam param) {
        try {
            ConfigChange confCh = getConfig();
            if (confCh != null) {
                return param.get(getConfig());
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false; // TODO
        }
    }

    protected ConfigChange getConfig() {
        if (config == null) {
            for (ConfigChange c : worldView.getAll(ConfigChange.class).values()) {
                if (c.getBotId().equals(player.getId())) {
                    config = c;
                    break;
                }
            }
        }
        return config;
    }
*/
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (!(other instanceof NativeUnrealBotAdapter)) return false;
        NativeUnrealBotAdapter otherAdapter = (NativeUnrealBotAdapter) other;
        return this.agentId.equals(otherAdapter.getComponentId());
    }
    
    @Override
	public int hashCode() {
    	return this.agentId.hashCode();
    }

}
