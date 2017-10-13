package cz.cuni.amis.pogamut.base.utils.logging;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.module.IAgentLogic;
import cz.cuni.amis.pogamut.base.agent.module.LogicModule;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestAgents {

	public static class NetworkLoggingAgent extends AbstractAgent {

		public IAgentLogic myLogic = new IAgentLogic() {

			@Override
			public long getLogicInitializeTime() {
				return 1000;
			}

			@Override
			public long getLogicShutdownTime() {
				return 1000;
			}

			@Override
			public void logic() {
				NetworkLoggingAgent.this.getLogger().getCategory("my-log").finest("I'm alive! FINEST"+this.toString());
				NetworkLoggingAgent.this.getLogger().getCategory("my-log").finer("I'm alive! FINER");
				NetworkLoggingAgent.this.getLogger().getCategory("my-log").fine("I'm alive! FINE");
				NetworkLoggingAgent.this.getLogger().getCategory("my-log").info("I'm alive! INFO");
				NetworkLoggingAgent.this.getLogger().getCategory("my-log").warning("I'm alive! WARNING");
				NetworkLoggingAgent.this.getLogger().getCategory("my-log").severe("I'm alive! SEVERE");
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
			}

			@Override
			public void logicInitialize(LogicModule logicModule) {
			}

			@Override
			public void logicShutdown() {
			}

			@Override
			public void beforeFirstLogic() {
			}
			
		};
		
		LogicModule logic;
		
		public NetworkLoggingAgent(IAgentId agentId, IComponentBus eventBus, IAgentLogger logger) {
			super(agentId, eventBus, logger);
			logger.addDefaultNetworkHandler();
			logic = new LogicModule(this, myLogic);
		}
		
	}
	
	public static NetworkLoggingAgent createNewAgent() {
		IAgentId agentId = new AgentId();
		IAgentLogger agentLogger = new AgentLogger(agentId);
                agentLogger.setLevel(Level.ALL);
		IComponentBus bus = new ComponentBus(agentLogger);
		return new NetworkLoggingAgent(agentId, bus, agentLogger);
	}
	
	public static void main(String[] args) {
	
		NetworkLoggingAgent agent1 = createNewAgent();
		NetworkLoggingAgent agent2 = createNewAgent();
		NetworkLoggingAgent agent3 = createNewAgent();
                NetworkLoggingAgent agent4 = createNewAgent();
		
		agent1.start();
		agent2.start();
		agent3.start();
                agent4.start();
		
		///////////////////////////////////////////////
		
	        IAgentId agent1Id = agent1.getComponentId();
		IAgentId agent2Id = agent2.getComponentId();
		IAgentId agent3Id = agent3.getComponentId();
                IAgentId agent4Id = agent4.getComponentId();

                //When an agent is removed, we don't want to read its logs anymore...
                boolean a1logged=true;
                boolean a2logged=true;
                boolean a3logged=true;
                boolean a4logged=true;


            try {
            Socket cl1 = new Socket(InetAddress.getByName("localhost"), 12345);
            new ObjectOutputStream(cl1.getOutputStream()).writeObject(agent1Id);

            Socket cl2 = new Socket(InetAddress.getByName("localhost"), 12345);
            new ObjectOutputStream(cl2.getOutputStream()).writeObject(agent2Id);

            Socket cl3 = new Socket(InetAddress.getByName("localhost"), 12345);
            new ObjectOutputStream(cl3.getOutputStream()).writeObject(agent3Id);

            Socket cl4 = new Socket(InetAddress.getByName("localhost"), 12345);
            new ObjectOutputStream(cl4.getOutputStream()).writeObject(agent4Id);

            int i=0;
            while (true) {
                try {
                    if(a1logged)
                    for(NetworkLogEnvelope envelope: (LinkedBlockingQueue<NetworkLogEnvelope>)(new ObjectInputStream(cl1.getInputStream()).readObject())){
                        System.out.println("client 1's log: "+envelope.toString());
                    }

                    if(a2logged)
                    for(NetworkLogEnvelope envelope: (LinkedBlockingQueue<NetworkLogEnvelope>)(new ObjectInputStream(cl2.getInputStream()).readObject())){
                        System.out.println("client 2's log: "+envelope.toString());
                    }

                    if(a3logged)
                    for(NetworkLogEnvelope envelope: (LinkedBlockingQueue<NetworkLogEnvelope>)(new ObjectInputStream(cl3.getInputStream()).readObject())){
                        System.out.println("client 3's log: "+envelope.toString());
                    }

                    if(a4logged)
                    for(NetworkLogEnvelope envelope: (LinkedBlockingQueue<NetworkLogEnvelope>)(new ObjectInputStream(cl4.getInputStream()).readObject())){
                        System.out.println("client 4's log: "+envelope.toString());
                    }
                    /* Testing removing agent...it works it seems.
                    i++;
                    if(i>=20)
                    {
                        NetworkLogManager.getNetworkLogManager().removeAgent(agent4Id);
                        a4logged=false;
                        NetworkLogManager.getNetworkLogManager().removeAgent(agent3Id);
                        a3logged=false; 
                        NetworkLogManager.getNetworkLogManager().removeAgent(agent2Id);
                        a2logged=false;
                        i=-500;
                    }
                    else{System.out.println(i);}*/


                } catch (StreamCorruptedException e) {System.out.println(e.toString());break;
                } catch (ClassNotFoundException e){ System.out.println(e.toString());break;
                } 

            }
            System.out.println("logging finished");


        } catch (UnknownHostException ex) {
            Logger.getLogger(TestAgents.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestAgents.class.getName()).log(Level.SEVERE, null, ex);
        }

		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		agent1.kill();
		//agent2.kill();
		//agent3.kill();
		
		// kill threads that are receiving logs from agents
		
		Pogamut.getPlatform().close();
		
		
	}
	
}
