import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
	CSC 421 Intro to AI
	Assignment 1 
	Cole Boothman St.ID V00808231
*/

public class Search {
	Problem problem;
	
	public Search(Problem problem) { this.problem = problem; }
	
	//Tree-search methods
	public String BreadthFirstTreeSearch() {
		return TreeSearch(new FrontierFIFO());
	}
	
	public String DepthFirstTreeSearch() {
		return TreeSearch(new FrontierLIFO());
	}
	
	public String UniformCostTreeSearch() {
		return TreeSearch(new FrontierPriorityQueue(new ComparatorG()));
	}	

	public String GreedyBestFirstTreeSearch() {
		return TreeSearch(new FrontierPriorityQueue(new ComparatorH(problem)));
	}
	
	public String AstarTreeSearch() {
		return TreeSearch(new FrontierPriorityQueue(new ComparatorF(problem)));
	}
	
	//Graph-search methods
	public String BreadthFirstGraphSearch() {
		return GraphSearch(new FrontierFIFO());
	}
	
	public String DepthFirstGraphSearch() {
		return GraphSearch(new FrontierLIFO());
	}
	
	public String UniformCostGraphSearch() {
		return GraphSearch(new FrontierPriorityQueue(new ComparatorG()));
	}	

	public String GreedyBestFirstGraphSearch() {
		return GraphSearch(new FrontierPriorityQueue(new ComparatorH(problem)));
	}
	
	public String AstarGraphSearch() {
		return GraphSearch(new FrontierPriorityQueue(new ComparatorF(problem)));
	}

	
	//Iterative deepening, tree-search and graph-search
	public String IterativeDeepeningTreeSearch() {
		// We increase our depth limit by 1 each time until we find a solution,
		// ie. "iteratively"
		int limit = 0;
		while (true) {
			String result = TreeSearchDepthLimited(new FrontierLIFO(), limit);
			// if result is a solution, return solution
			if (result != null) {
				return result;
			}
			limit++;
		}
	}
	
	public String IterativeDeepeningGraphSearch() {
		// We increase our depth limit by 1 each time until we find a solution.
		int limit = 0;
		while (true) {
			String result = GraphSearchDepthLimited(new FrontierLIFO(), limit);
			// if result is a solution, return solution
			if (result != null) {
				return result;
			}
			limit++;
		}	
	}
	
	//For statistics purposes
	int cnt; //count expansions
	List<Node> node_list; //store all nodes ever generated
	Node initialNode; //initial node based on initial state
	
	private String TreeSearch(Frontier frontier) {
		cnt = 0; 
		node_list = new ArrayList<Node>();
		
		initialNode = MakeNode(problem.initialState); 
		node_list.add( initialNode );
		
		frontier.insert( initialNode );
		while(true) {
			
			if(frontier.isEmpty())
				return null;
			
			Node node = frontier.remove();
			
			if( problem.goal_test(node.state) ) {
				printTree(initialNode);
				return Solution(node);
			}
			
			
			frontier.insertAll(Expand(node,problem));
			cnt++;
		}
	}

	private String GraphSearch(Frontier frontier) {
		cnt = 0; 
		node_list = new ArrayList<Node>();
		
		initialNode = MakeNode(problem.initialState); 
		node_list.add( initialNode );
		
		Set<Object> explored = new HashSet<Object>(); //empty set
		frontier.insert( initialNode );
		while(true) {
			
			if(frontier.isEmpty())
				return null;
			
			Node node = frontier.remove();
			
			if( problem.goal_test(node.state) ) {
				printTree(initialNode);
				return Solution(node);
			}
		
			if( !explored.contains(node.state) ) {
				explored.add(node.state);
				frontier.insertAll(Expand(node,problem));
				cnt++;
			}
		}
	}
	
	private String TreeSearchDepthLimited(Frontier frontier, int limit) {
		cnt = 0;
		node_list = new ArrayList<Node>();

		// Initialize frontier to use initial problem state
		initialNode = MakeNode(problem.initialState); 
		frontier.insert(initialNode);

		while(true) {
			// If frontier empty return failure
			if(frontier.isEmpty()) 
				return null;

			// Remove node from frontier
			Node node = frontier.remove();

			// If node contains goal state then return corresponding solution
			if(problem.goal_test(node.state)) 
				return Solution(node);

			// If depth of node is less than limit, expand node and add results to frontier
			if (node.depth < limit) 
				frontier.insertAll(Expand(node,problem));
				cnt++;
		}
	}

	private String GraphSearchDepthLimited(Frontier frontier, int limit) {
		cnt = 0; 
		node_list = new ArrayList<Node>();
		// Initialize frontier to use initial problem state
		initialNode = MakeNode(problem.initialState); 
		node_list.add( initialNode );
		
		Set<Object> explored = new HashSet<Object>(); //empty set for explored nodes
		frontier.insert( initialNode );
		while(true) {
			// If frontier empty return failure
			if(frontier.isEmpty())
				return null;
			// Remove node from frontier
			Node node = frontier.remove();
			// If node contains goal state then return corresponding solution
			if( problem.goal_test(node.state) )
				return Solution(node);
			// If depth of node is less than limit and node is not in explored, 
			//  expand node and add results to frontier
			if( !explored.contains(node.state) && node.depth < limit) {
				explored.add(node.state);
				frontier.insertAll(Expand(node,problem));
				cnt++;
			}
		}	
	}

	private Node MakeNode(Object state) {
		Node node = new Node();
		node.state = state;
		node.parent_node = null;
		node.path_cost = 0;
		node.depth = 0;
		return node;
	}
	
	private Set<Node> Expand(Node node, Problem problem) {
		node.order = cnt;
		
		Set<Node> successors = new HashSet<Node>(); //empty set
		Set<Object> successor_states = problem.getSuccessors(node.state);
		
		for(Object result : successor_states) {
			Node s = new Node();
			s.state = result;
			s.parent_node = node;
			s.path_cost = node.path_cost + problem.step_cost(node.state, result); 
			s.depth = node.depth + 1; 
			successors.add(s);
			
			node_list.add( s );
		}
		
		return successors;
	}
	
	//Create a string to print solution. 
	private String Solution(Node node) {
		
		String solution_str = "(cost=" + node.path_cost + ", expansions=" + cnt + ")\t";
		
		Deque<Object> solution = new ArrayDeque<Object>();
		do {
			solution.push(node.state);
			node = node.parent_node;
		} while(node != null);
		
		while(!solution.isEmpty())
			solution_str += solution.pop() + " ";
		
		return solution_str;
	}

	// Prints each node in tree and statistics (heuristic, heuristic sum etc)
	private void printTree(Node node) {
		// Heurstic stats
		double g = node.path_cost;
		double h = problem.h(node.state);
		double f = node.path_cost + problem.h(node.state);
		
		// Add tab characters for the depth of each node.
		for (int i=0; i<node.depth; i++) {
			System.out.print("\t");
		}
		// Print current node stats
		// if node order is -1, means not expanded
		System.out.println(node.state + "(g=" + g + ", h=" + h + ", f=" + f + 
			(node.order != -1 ? " order=" + node.order : "") + ")");

		// For each node in the list, if the node's parent is the one we are currently printing,
		// we should print this node as well.
        for (Node m : node_list) {
            if (m.parent_node == node) {
                this.printTree(m);
            }
        }
	}
}
