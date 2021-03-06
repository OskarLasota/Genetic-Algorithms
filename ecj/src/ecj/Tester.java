package ecj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import ec.vector.BitVectorIndividual;
import ec.vector.IntegerVectorIndividual;

public class Tester extends Problem implements SimpleProblemForm {


    private int[] cGenome;    


	
	public static void main(String[] args) {
		int amountOfChannels = 5;
		String[] argo = new String[4];
		argo[0] = "-file";
		argo[1] = "src/params/nsga2_0.params";
		argo[2] = "-p";
		argo[3] = "pop.subpop.0.size="+amountOfChannels;

		ec.Evolve.main(argo);
	}

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);
        //System.out.println("setup");
        // make dynamic changes here by creating new parameters
        
    }
	
	@Override
	public void evaluate(EvolutionState state, Individual indi, int subpopulation, int threadnum) {
        if(indi.evaluated) return;
        if (!(indi instanceof IntegerVectorIndividual)) {
            state.output.fatal("The individuals for this problem should be IntegerVectorIndividuals.");
        }
        int sum = 2;

        IntegerVectorIndividual temp = (IntegerVectorIndividual) indi;
        cGenome = temp.genome;
               
        /*
         * where is the result stored?
         * how do i put max budget?
         */

        
        double[] objectives = ((MultiObjectiveFitness) indi.fitness).getObjectives();
        //System.out.println(cGenome.length);
        objectives[0] = calculateCost(cGenome);    
        objectives[1] = calculatePenetration(cGenome);    
        objectives[2] = calculateEngagement(cGenome);  
        ((MultiObjectiveFitness) indi.fitness).setObjectives(state, objectives);
        indi.evaluated = true;
    
	}
	
	
	private double calculatePenetration(int[] solution){
		double pen =0;
		
		int[] avgPen = { 5000, 4000, 53000, 21100, 2120};
		
		for(int i=0; i<solution.length ; i++){
			//System.out.println("genome : " + solution[i]);
			pen += avgPen[i] * solution[i];
		}
		//System.out.println("pen : " + pen);
		return pen;
	}
	
	
	
	private double calculateEngagement(int[] solution){
		double sumEng = 0;
		double sumViews = 0;
		int[] views = { 5000, 4000, 53000, 21100, 2120};
		int[] likes = { 32, 52, 634, 412, 85 };
		int[] dislikes = { 43, 52, 21, 59, 2};
		int[] comments = { 10, 4, 5, 12, 2};
		
		for(int i=0; i<solution.length ; i++){
			if(cGenome[i]>0){
				sumEng += (likes[i] + dislikes[i] + comments[i]);
				sumViews += views[i];
			}
		}
		//System.out.println("engagement : " + sumViews/sumEng);
		return sumViews/sumEng;
	}

	private double calculateCost(int[] solution){
		double cost = 0;
		int[] avgCost = {100, 500, 300, 400, 700};
		int budget = 1000;
		for(int i =0; i<solution.length ; i++){ // 1, 0 , 1 , 0 , 1
			//System.out.println("cost : " + cost);
			cost += cGenome[i] * avgCost[i];
		}
		//System.out.println("cost : " + cost);
		return cost;
	}
	
	
}
