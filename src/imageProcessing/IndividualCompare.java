package imageProcessing;

import java.util.Comparator;

public class IndividualCompare implements Comparator<Individual>  {

	@Override
	public int compare(Individual arg0, Individual arg1) {
		if(arg0.getFitness() > arg1.getFitness()) {
			return 1;
		}else if(arg0.getFitness() == arg1.getFitness()) {
			return 0;
		}else {
			return -1;
		}
	}

}
