package ecj;

import ec.EvolutionState;
import ec.simple.SimpleStatistics;
import ec.util.Parameter;

public class Stats extends SimpleStatistics {

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);
        
    }
    
    
    /**
     * Logs the best individual of the run.
     */
    @Override
    public void finalStatistics(final EvolutionState state, final int result) {
    	System.out.println("hi " + result);
    
    }
	
}
