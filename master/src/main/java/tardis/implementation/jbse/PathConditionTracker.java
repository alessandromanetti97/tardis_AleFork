package tardis.implementation.jbse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jbse.mem.Clause;
import jbse.mem.ClauseAssumeClassInitialized;
import jbse.mem.ClauseAssumeClassNotInitialized;
import jbse.mem.State;
import jbse.mem.State.Phase;
import jbse.tree.StateTree.BranchPoint;

public class PathConditionTracker {
	
	//MYCHANGES
			private Map <Clause, Integer> livelliDiAnnidamento = new HashMap<>();
			private int currentPathConditionSize;
					
			public int getLivelloDiAnnidamento(Clause c) {
				if (livelliDiAnnidamento.containsKey(c)) {
					return livelliDiAnnidamento.get(c);
				} else {
					throw new NoTargetClauseException();
				}		
			}
			
			//CHANGES 14/06
			public Map <Clause, Integer> getLivelliDiAnnidamento(){
				return this.livelliDiAnnidamento;
			}

			public void atStepPre(State currentState) {
				currentPathConditionSize = currentState.getPathCondition().size();
				
			}

			public void atStepPost(State currentState) {
				if (currentState.phase() == Phase.POST_INITIAL) {
					
					int newClauses = currentState.getPathCondition().size() - currentPathConditionSize;
					List<Clause> pc = currentState.getPathCondition();
					for (int i=0; i<newClauses; i++) {
						Clause c = pc.get(pc.size() - 1 - i);
						if (!(c instanceof ClauseAssumeClassInitialized) && !(c instanceof ClauseAssumeClassNotInitialized)) {
							livelliDiAnnidamento.put(c , currentState.getStackSize() - 1);
							//System.out.println(livelliDiAnnidamento);
						}
					}
				}				
			} 
			
			public void atBacktrackPost(State currentState) {
				if (currentState.phase() == Phase.POST_INITIAL) {
					List<Clause> pc = currentState.getPathCondition();
					for (int i = pc.size() - 1; i >= 0; i--) {
						Clause c = pc.get(i);
						if (livelliDiAnnidamento.containsKey(c)) {
							break;
						}
						if (!(c instanceof ClauseAssumeClassInitialized) && !(c instanceof ClauseAssumeClassNotInitialized)) {
							livelliDiAnnidamento.put(c , currentState.getStackSize() - 1);
								//System.out.println(livelliDiAnnidamento);
						}	
					}	
				}
			}


}
