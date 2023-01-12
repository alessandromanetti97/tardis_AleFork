package tardis.implementation.jbse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jbse.bc.ClassFile;
import jbse.bc.ClassFileJavassist;
import jbse.bc.Signature;
import jbse.mem.Clause;
import jbse.mem.ClauseAssumeClassInitialized;
import jbse.mem.ClauseAssumeClassNotInitialized;
import jbse.mem.Frame;
import jbse.mem.State;
import jbse.mem.State.Phase;
import jbse.mem.exc.FrozenStateException;
import jbse.mem.exc.ThreadStackEmptyException;
import jbse.tree.StateTree.BranchPoint;

public class PathConditionTracker {
	
	//MYCHANGES
	
	private Map <Clause, List<Signature>> stackPerClausola = new HashMap<>();
			private int currentPathConditionSize;

			public int getLivelloDiAnnidamento(Clause c) {
				if (stackPerClausola.containsKey(c)) {
					return stackPerClausola.get(c).size();  //.get(c).size();
				} else {
					return 0;
					//throw new NoTargetClauseException(); 
				}		
			}
			
			public String getClasse(Clause c) {
				if (stackPerClausola.containsKey(c)) {
					List<Signature> stackData = stackPerClausola.get(c);
					if (stackData.size() > 0) {
						return stackData.get(stackData.size() - 1).getClassName();
					}
				} 
				return null; 
				//throw new NoTargetClauseException();		
			}
			
			//* DECEMBER CHANGES 
			public String getMetodo(Clause c) {
				if (stackPerClausola.containsKey(c)) {
					List<Signature> stackData = stackPerClausola.get(c);
					return stackData.get(stackData.size() - 1).getName();
				} else {
					return null;
					//throw new NoTargetClauseException();
				}		
			}
			
			public List <Signature> getListaSignature (Clause c){
				if (stackPerClausola.containsKey(c)) {
					return stackPerClausola.get(c);
				} else {
					return null;
				}
			}
			//ENDS*/
			
			
			public void atStepPre(State currentState) {
				currentPathConditionSize = currentState.getPathCondition().size(); 
				
			}

			public void atStepPost(State currentState) throws ThreadStackEmptyException, FrozenStateException { //Handle exception!!!!!!
				if (currentState.phase() == Phase.POST_INITIAL) {
					
					int newClauses = currentState.getPathCondition().size() - currentPathConditionSize;
					List<Clause> pc = currentState.getPathCondition();
					
					for (int i = 0; i < newClauses; i++) {
						Clause c = pc.get(pc.size() - 1 - i);
						if (!(c instanceof ClauseAssumeClassInitialized) && !(c instanceof ClauseAssumeClassNotInitialized)) {
							stackPerClausola.put(c, getSignatures(currentState));
							//metodiDelleClausole.put(c, currentState.getCurrentMethodSignature().getName());	//METODI
							//classiDelleClausole.put(c, currentState.getCurrentClass().getClassName());		//CLASSI
						}
					}
				}				
			} 
			
			private List<Signature> getSignatures(State currentState) throws FrozenStateException {
				ArrayList<Signature> stackData = new ArrayList<>();
				List<Frame> stack = currentState.getStack();
				for (Frame f: stack) {
					stackData.add(f.getMethodSignature()); 
				}
				return stackData;
			}

			public void atBacktrackPost(State currentState) throws ThreadStackEmptyException, FrozenStateException { //Handle exception!!!!!
				if (currentState.phase() == Phase.POST_INITIAL) {
					List<Clause> pc = currentState.getPathCondition();
					for (int i = pc.size() - 1; i >= 0; i--) {
						Clause c = pc.get(i);
						if (stackPerClausola.containsKey(c)  /*metodiDelleClausole.containsKey(c)*/) {
							break;
						}
						if (!(c instanceof ClauseAssumeClassInitialized) && !(c instanceof ClauseAssumeClassNotInitialized)) {
							stackPerClausola.put(c, getSignatures(currentState));
							//livelliDiAnnidamento.put(c , currentState.getStackSize() - 1);
							//metodiDelleClausole.put(c, currentState.getCurrentMethodSignature().getName()); //METODI
							//classiDelleClausole.put(c, currentState.getCurrentClass().getClassName()); //CLASSI
						}	
					}	
				}
			}


}
