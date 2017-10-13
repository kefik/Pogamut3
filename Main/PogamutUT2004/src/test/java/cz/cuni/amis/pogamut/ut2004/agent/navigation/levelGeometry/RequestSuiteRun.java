package cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class RequestSuiteRun<TResult> {
		protected String name;
		protected long startNanoTime;
		protected long endNanoTime;
		protected Map<RaycastRequest, TResult> requestToResultMap;
		protected final int REPETITIONS = 100;
		public RequestSuiteRun( String name, List<RaycastRequest> requests ) {
			this.name = name;
			requestToResultMap = new HashMap<RaycastRequest, TResult>();
			for (RaycastRequest request : requests ) {
				requestToResultMap.put( request, raycast( request ) );
			}
			
			startNanoTime = System.nanoTime();
			for (int i=0; i<REPETITIONS; ++i) {
				for (RaycastRequest request : requests ) {
					raycast( request );
				}
			}
			endNanoTime = System.nanoTime();
		}
		
		@Override
		public String toString() {
			return (
				name + ":\n" +
				"total          time: "+((endNanoTime-startNanoTime)/1000)+" us\n" +
				"average per request: " + ((endNanoTime-startNanoTime)/requestToResultMap.size()/REPETITIONS)+" ns"
			);
		}
		
		protected abstract TResult raycast(RaycastRequest request);
	}