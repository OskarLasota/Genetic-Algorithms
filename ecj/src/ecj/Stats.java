package ecj;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ec.EvolutionState;
import ec.Individual;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleStatistics;
import ec.util.Output;
import ec.util.Parameter;
import ec.util.QuickSort;
import ec.util.SortComparator;

public class Stats extends SimpleStatistics {
    public static final String P_PARETO_FRONT_FILE = "front";
    public static final String P_SILENT_FRONT_FILE = "silent.front";
    public boolean silentFront;

    /** The pareto front log */
    public int frontLog = 0;  // stdout by default
    /**
	 * 
	 */



	@Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);




        silentFront = state.parameters.getBoolean(base.push(P_SILENT), null, false);
        // yes, we're stating it a second time.  It's correct logic.
        silentFront = state.parameters.getBoolean(base.push(P_SILENT_FRONT_FILE), null, silentFront);

        File frontFile = state.parameters.getFile(base.push(P_PARETO_FRONT_FILE),null);

        if (silentFront)
        {
        	frontLog = Output.NO_LOGS;
        }
        else if (frontFile!=null)
        {
        	try
        	{
        		frontLog = state.output.addLog(frontFile, !compress, compress);
        	}
        	catch (IOException i)
        	{
        		state.output.fatal("An IOException occurred while trying to create the log " + frontFile + ":\n" + i);
        	}
        }
        else state.output.warning("No Pareto Front statistics file specified, printing to stdout at end.", base.push(P_PARETO_FRONT_FILE));
	}



	/** Logs the best individual of the run. */
	public void finalStatistics(final EvolutionState state, final int result)
	{		bypassFinalStatistics(state, result);  // just call super.super.finalStatistics(...)

		if (doFinal) state.output.println("\n\n\n PARETO FRONTS", statisticslog);
		for (int s = 0; s < state.population.subpops.length; s++)
		{
			MultiObjectiveFitness typicalFitness = (MultiObjectiveFitness)(state.population.subpops[s].individuals[0].fitness);
			if (doFinal) state.output.println("\n\nPareto Front of Subpopulation " + s, statisticslog);

			// build front
			ArrayList front = typicalFitness.partitionIntoParetoFront(state.population.subpops[s].individuals, null, null);

			// sort by objective[0]
			Object[] sortedFront = front.toArray();
			QuickSort.qsort(sortedFront, new SortComparator()
			{
				public boolean lt(Object a, Object b)
				{
					return (((MultiObjectiveFitness) (((Individual) a).fitness)).getObjective(0) < 
							(((MultiObjectiveFitness) ((Individual) b).fitness)).getObjective(0));
				}

				public boolean gt(Object a, Object b)
				{
					return (((MultiObjectiveFitness) (((Individual) a).fitness)).getObjective(0) > 
					((MultiObjectiveFitness) (((Individual) b).fitness)).getObjective(0));
				}
			});

			// print out front to statistics log
			if (doFinal)
				for (int i = 0; i < sortedFront.length; i++)
					((Individual)(sortedFront[i])).printIndividualForHumans(state, statisticslog);

			// write short version of front out to disk
			if (!silentFront)
			{
				if (state.population.subpops.length > 1){
					state.output.println("Subpopulation " + s, frontLog);
				}
				for (int i = 0; i < sortedFront.length; i++)
				{
					Individual ind = (Individual)(sortedFront[i]);
					MultiObjectiveFitness mof = (MultiObjectiveFitness) (ind.fitness);
					double[] objectives = mof.getObjectives();

					String line = "";
					for (int f = 0; f < objectives.length; f++)
						line += (objectives[f] + " ");
					state.output.println(line, frontLog);
				}
			}
		}
	}
}

    

