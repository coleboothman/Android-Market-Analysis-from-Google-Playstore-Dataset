import java.util.HashSet;
import java.util.Set;

public class ProblemCannibals extends Problem {
	
    static final int cannL = 0;
    static final int missL = 1;
    static final int boatL = 2;
    static final int cannR = 3;
    static final int missR = 4;
    static final int boatR = 5;
    
	boolean goal_test(Object state) {
        StateCannibals can_state = (StateCannibals) state;
        
        if (can_state.canArray[cannR]==3 && can_state.canArray[missR]==3 && can_state.canArray[boatR]==1)
            return true;
        else return false;
	}
  
    Set<Object> getSuccessors(Object state) {
    	
        Set<Object> set = new HashSet<Object>();
        StateCannibals can_state = (StateCannibals) state;
        
        //Let's create without any constraint, then remove the illegal ones
        StateCannibals successor_state;
        
        //one cannibal only from left to right
        successor_state = new StateCannibals(can_state);
        successor_state.canArray[cannL] -= 1;
        successor_state.canArray[cannR] += 1;
        successor_state.canArray[boatL] -= 1;
        successor_state.canArray[boatR] += 1;

        if (isValid(successor_state)) {
            set.add(successor_state);
        }

        //one cannibal only from right to left
        one_can_r_to_l = new StateCannibals(can_state);
        one_can_r_to_l.canArray[cannL] += 1;
        one_can_r_to_l.canArray[cannR] -= 1;
        one_can_r_to_l.canArray[boatL] += 1;
        one_can_r_to_l.canArray[boatR] -= 1;
        
        if (isValid(one_can_r_to_l)) {
            set.add(one_can_r_to_l);
        }
        
        //two cannibals from left to right
        two_can_l_to_r = new StateCannibals(can_state);
        two_can_l_to_r.canArray[cannL] -= 2;
        two_can_l_to_r.canArray[cannR] += 2;
        two_can_l_to_r.canArray[boatL] -= 1;
        two_can_l_to_r.canArray[boatR] += 1;
        
        //two cannibals from right to left 
        two_can_r_to_l = new StateCannibals(can_state);
        two_can_r_to_l.canArray[cannL] += 2;
        two_can_r_to_l.canArray[cannR] -= 2;
        two_can_r_to_l.canArray[boatL] += 1;
        two_can_l_to_r.canArray[boatR] -= 1;
        
        //one missionary only from left to right 
        one_mis_l_to_r = new StateCannibals(can_state);
        one_mis_l_to_r.canArray[missL] -= 1;
        one_mis_l_to_r.canArray[missR] += 1;
        one_mis_l_to_r.canArray[boatL] -= 1;
        one_mis_l_to_r.canArray[boatR] += 1;

        
        //one missionary only from right to left 
        one_mis_r_to_l = new StateCannibals(can_state);
        one_mis_r_to_l.canArray[missL] += 1;
        one_mis_r_to_l.canArray[missR] -= 1;
        one_mis_r_to_l.canArray[boatL] += 1;
        one_mis_r_to_l.canArray[boatR] -= 1;
        
        //two missionaries from left to right 
        two_mis_l_to_r = new StateCannibals(can_state);
        two_mis_l_to_r.canArray[missL] -= 2;
        two_mis_l_to_r.canArray[missR] += 2;
        two_mis_l_to_r.canArray[boatL] -= 1;
        two_mis_l_to_r.canArray[boatR] += 1;

        
        //two missionaries from right to left 
        two_mis_r_to_l = new StateCannibals(can_state);
        two_mis_r_to_l.canArray[missL] += 2;
        two_mis_r_to_l.canArray[missR] -= 2;
        two_mis_r_to_l.canArray[boatL] += 1;
        two_mis_r_to_l.canArray[boatR] -= 1;
        
        //one cannibal and one missionary from left to right 
        one_can_one_mis_l_to_r = new StateCannibals(can_state);
        one_can_one_mis_l_to_r.canArray[missL] -= 1;
        one_can_one_mis_l_to_r.canArray[missR] += 1; 
        one_can_one_mis_l_to_r.canArray[cannL] -= 1; 
        one_can_one_mis_l_to_r.canArray[cannR] += 1; 
        one_can_one_mis_l_to_r.canArray[boatL] -= 1; 
        one_can_one_mis_l_to_r.canArray[boatR] += 1; 
        
        //one cannibal and one missionary from right to left 
        one_can_one_mis_r_to_l = new StateCannibals(can_state);
        one_can_one_mis_r_to_l.canArray[missL] += 1;
        one_can_one_mis_r_to_l.canArray[missR] -= 1;
        one_can_one_mis_r_to_l.canArray[missL] += 1;
        one_can_one_mis_r_to_l.canArray[missL] += 1;
        one_can_one_mis_r_to_l.canArray[missL] += 1;
        one_can_one_mis_r_to_l.canArray[missL] += 1;
        
        return set;
    }
    
    private boolean isValid(StateCannibals state)
    {   
        //Checking to see if any element of the array is negative 
        for (int i=0; i<6; i++)
            if (state.canArray[i] < 0) return false;
        
        //Checking to see if the numbers of cannibals, missionaries, and boat 
        //are more then 3,3,1 respectively
        if (state.canArray[cannR] > 3 ||
                state.canArray[cannL] > 3 ||
                state.canArray[missR] > 3 ||
                state.canArray[missL] > 3 ||
                state.canArray[boatR] > 1 ||
                state.canArray[boatL] > 1) {
            return false;
        }
        
        //Now, checking if cannibals out number missionaries
        if (((state.canArray[cannR] > state.canArray[missR]) && state.canArray[missR] != 0) ||
        ((state.canArray[cannL] > state.canArray[missL]) && state.canArray[missL] != 0)) {
            return false;
        }

        return true;
    }
	
	double step_cost(Object fromState, Object toState) { return 1; }

	public double h(Object state) { return 0; }


	public static void main(String[] args) throws Exception {
		ProblemCannibals problem = new ProblemCannibals();
		int[] canArray = {3,3,1,0,0,0};
		problem.initialState = new StateCannibals(canArray); 
		
		Search search  = new Search(problem);
		
		System.out.println("BreadthFirstTreeSearch:\t\t" + search.BreadthFirstTreeSearch());

		System.out.println("BreadthFirstGraphSearch:\t" + search.BreadthFirstGraphSearch());
	}
}
