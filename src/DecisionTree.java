import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.*;

public class DecisionTree {

	//private static final variables
	private static final String FILE = "./voting-data.tsv";
	private static final int NUM_ISSUES = 10;
	private static final int NUM_PEOPLE = 430;
	private static final String YEA = "+";
	private static final String NEA = "-";
	private static final String OTH = ".";
	private static final String REP = "R";
	private static final String DEM = "D";
	private static final String[] VK = {"+", "-", "."};
	private static final int CORRECT = 0;
	private static final int INCORRECT = 1;

	//private fields
	private String[][] data;							// Has dimensionality (NUM_PEOPLE)x(NUM_ISSUES + 1) & the last column is the label democrat or republican

	/**
	 * Constructor for the DecisionTree class
	 */


	/*****************************************************
	 * 					PRIVATE METHODS
	 *****************************************************/

	// Initializes and populates the data matrix from file.
	private void readData(){
		data = new String[NUM_PEOPLE][NUM_ISSUES+1];
		try{
			int personId = 0;
			Scanner scan = new Scanner(new File(FILE));
			while(scan.hasNextLine()){
				assert(personId < NUM_PEOPLE);
				String line = scan.nextLine();
				String[] info = line.split("\t");
				assert(info.length == 3);

				// Store the voting record for this particular person
				String votingRecord = info[2];
				assert(votingRecord.length() == NUM_ISSUES);
				int i;
				for(i = 0; i < votingRecord.length(); i++){
					String vote = votingRecord.substring(i,i+1);
					if(vote.equals("+")){
						data[personId][i] = YEA;
					}
					else if(vote.equals("-")){
						data[personId][i] = NEA;
					}
					else if(vote.equals(".")){
						data[personId][i] = OTH;
					}
					else{
						System.out.println("Found unknown token");
					}
				}
				assert(i < data[personId].length);

				// Store the label (democrat or republican) for this person
				if(info[1].equals("D")){
					data[personId][i] = DEM;
				}
				else if(info[1].equals("R")){
					data[personId][i] = REP;
				}
				else{
					System.out.println("Found neither D nor R");
				}
				personId++;
			}
			scan.close();
		}
		catch(FileNotFoundException e){
			System.out.println(e);
		}
	}

	/**
	 * Log2() calculator
	 *
	 * @param double 	x
	 * @return double	calculation of log2(x)
	 */
	private static double log2(double x){

		//change of base to log2
		return (double)((Math.log(x))/(Math.log(2)));
	}

	/**
	 * Boolean Entropy of a random variable
	 *
	 * @param double	probability of random variable
	 * @return double	entropy
	 */
	private static double B(double prob){

		double calc = 0;

		//stop log of 0 errors -> if the probability is 1, then assume that it is .9999 and if it is 0, then assume .0001
		if(prob == 1) {
			prob = 0.9999;
		}

		if(prob == 0) {
			prob = 0.0001;
		}

		else {

			double a = 1 - prob;
			double b = log2(prob);
			double c = log2(a);
			calc = (prob * b + a * c);
		}

		return -(calc);
	}

	/**
	 * Calculates the expected entropy remaining after testing attribute A
	 *
	 * @param Attribute		A (the given attribute being evaluated)
	 * @param int			dems (total number of democrat examples in a data set)
	 * @param int			reps (total number of republican examples in a data set)
	 * @return double		exEnt (expected entropy)
	 */
	private static double remainder(Attribute A, double dems, double reps) {

		//dem variables
		double dem_yea = A.getPlusDem();
		double dem_nea = A.getMinusDem();
		double dem_dot = A.getDotDem();

		//rep variables
		double rep_yea = A.getPlusRep();
		double rep_nea = A.getMinusRep();
		double rep_dot = A.getDotRep();

		//calculate the YEAs
		double temp_yea;
		double shell = (dem_yea+rep_yea);
		if(shell == 0) {
			temp_yea = 0;
		}

		else {
			temp_yea = (dem_yea)/shell;
		}

		double b_yea = B(temp_yea);
		double coef_yea = (dem_yea + rep_yea)/(dems+reps);
		double yea = b_yea * coef_yea;

		//calculate the NAEs
		double temp_nea;
		double shell2 = (dem_nea+rep_nea);
		if(shell2 == 0) {
			temp_nea = 0;
		}

		else {
			temp_nea = (dem_nea)/shell2;
		}

		double b_nea = B(temp_nea);
		double coef_nea = (dem_nea + rep_nea)/(dems+reps);
		double nea = b_nea * coef_nea;

		//calculate the dots
		double temp_dot;
		double shell3 = (dem_dot+rep_dot);
		if(shell3 == 0) {
			temp_dot = 0;
		}

		else {
			temp_dot = (dem_dot)/shell3;
		}

		double b_dot = B(temp_dot);
		double coef_dot = (dem_dot + rep_dot)/(dems+reps);
		double dot = b_dot * coef_dot;

		return yea + nea + dot;
	}

	/**
	 * Calculates the total information gain (expected reduction in entropy)
	 *
	 * @param Attribute	A
	 * @param int		dems
	 * @param int		reps
	 * @return double	gain
	 */
	private static double informationGain(Attribute A, double dems, double reps) {

		double prob = (dems)/(dems + reps);
		double b = B(prob);
		double remainder = remainder(A, dems, reps);

		return b - remainder;
	}

	/**
	 * Chooses an attribute to split on using information gain
	 *
	 * @param HashMap<Integer, Attribute>	attributes
	 * @param int 							dems
	 * @param int 							reps
	 * @return int							a
	 */
	private static int chooseA(Attribute[] attributes, double dems, double reps) {

		double max = -100000000;
		int maxAtt = 0;
		double infoGain;

		for(int i = 0; i < attributes.length; i++) {

			infoGain = informationGain(attributes[i], dems, reps);

			if(infoGain >= max) {
				max = infoGain;
				maxAtt = i;
			}
		}
		return maxAtt;
	}

	/**
	 * Safely removes a given value from a HashMap and reindexes
	 *
	 * @param Attribute[]		attributes
	 * @param int 				index
	 * @return Attribute[]		array
	 */
	private static Attribute[] removeSafe(Attribute[] attributes, int index) {

		int size = attributes.length;
		Attribute[] array = new Attribute[size-1];
		for(int i = 0; i < size; i++) {

			if(i < index) {
				array[i] = attributes[i];
			}
			else if(i > index){
				array[i-1] = attributes[i];
			}
		}
		return array;
	}

	/**
	 * Initialize everything
	 *
	 * @param DecisionTree					dTree
	 * @param String[][]					dataSet
	 * @param Attriubute[]					attributes
	 * @param HashMap<Integer, Example>		examples
	 */
	private static void init(DecisionTree dTree, String[][] dataSet, Attribute[] attributes, HashMap<Integer, Example> examples) {

		//fill examples from entire set
		for(int i = 0; i < NUM_PEOPLE; i++){
			Example exmpl = new Example();
			exmpl.setVotes(dataSet[i]);
			examples.put(i, exmpl);
		}

		//fill attributes for entire set with empty attribute objects
		for(int i = 0; i < NUM_ISSUES; i++) {
			attributes[i] = new Attribute(i);
		}

		//set the attributes
		setAttribute(attributes, examples);
	}

	/**
	 * Gets the positive and negative examples for all distinct values of an Attribute A
	 *
	 * @param Attribute							a
	 * @param HashMap<Integer, String[]>		examples
	 */
	private static void setAttribute(Attribute[] a, HashMap<Integer, Example> examples) {

		String[] xmpl;

		for(int i = 0; i < a.length; i++) {

			a[i].reset();

			for(int j = 0; j < examples.size(); j++) {

				//set to the current example
				xmpl = examples.get(j).getVotes();

				//look for Democrats
				if(xmpl[NUM_ISSUES].equals(DEM)) {

					//look for YEAs
					if(xmpl[i].equals(YEA)) {
						a[i].itrPlusDem();
					}

					//look for NEAs
					else if(xmpl[i].equals(NEA)) {
						a[i].itrMinusDem();
					}

					//look for NEAs
					else if(xmpl[i].equals(OTH)) {
						a[i].itrDotDem();
					}
				}

				//look for Republicans
				else if(xmpl[NUM_ISSUES].equals(REP)) {

					//look for YEAs
					if(xmpl[i].equals(YEA)) {
						a[i].itrPlusRep();
					}

					//look for NEAs
					else if(xmpl[i].equals(NEA)) {
						a[i].itrMinusRep();
					}

					//look for NEAs
					else if(xmpl[i].equals(OTH)) {
						a[i].itrDotRep();
					}
				}
			}
		}
	}

	/**
	 * Calculates the majority label from a set of examples
	 *
	 * @param HashMap<Integer, Example>		examples
	 * @return String						label
	 */
	private static String majorityLabel(HashMap<Integer, Example> example_set) {

		int num_dem = 0;
		int num_rep = 0;

		//loop through the examples
		for(int i = 0; i < example_set.size(); i++) {

			//if an example is labeled democrat
			if(example_set.get(i).getLabel().equals(DEM)) {
				num_dem++;
			}

			//if it is labeled republican
			else {
				num_rep++;
			}
		}

		//return the greater of the 2, returning republican upon tie break
		if(num_rep >= num_dem) {
			return REP;
		}

		else {
			return DEM;
		}
	}

	/**
	 * Checks if all examples in a set have the same label
	 *
	 * @param HashMap<Integer, Example>		examples
	 * @return boolean						true or false
	 */
	private static boolean sameLabel(HashMap<Integer, Example> examples) {

		boolean same = false;
		String compLabel;
		String label;
		int i = 0;

		if(examples.size() == 1) {
			return true;
		}

		else {

			//loop through the examples and check for equality
			do {

				//current label
				label = examples.get(i).getLabel();
				compLabel = examples.get(i+1).getLabel();

				if(label.equals(compLabel)) {
					same = true;
					i++;
				}

				else {
					same = false;
				}

			  //while all labels are the same and i is less than the size of the example set
			} while(same == true && i < examples.size()-1);
		}

		return same;
	}

	/**
	 * Decision-Tree-Learning (DTL)
	 *
	 * @param HashMap<Integer, String[]>	examples
	 * @param HashMap<Integer, Attribute>	attributes
	 * @param HashMap<Integer, String[]>	parent_examples
	 */
	private Node DTL(HashMap<Integer, Example> examples, Attribute[] attributes_set, HashMap<Integer, Example> parent_examples) {

		//set attributes
		setAttribute(attributes_set, examples);

		//going to hold the totals for each label
		HashMap<Integer, Example> sk = new HashMap<Integer, Example>();
		Node node;
		String[] votes;
		int num_reps = 0;
		int num_dems = 0;

		//BASE CASES
		//if examples is empty -> return the most common label among the parent labels
		if(examples.isEmpty()) {

			//if parent_examples is also empty
			if(parent_examples.isEmpty()) {
				return new Node("Not enough INFORMAAATTTIONNNN");
			}

			//return the majority label
			else {

				//System.out.println("BASE CASE");
				return new Node(majorityLabel(parent_examples));
			}
		}

		//if all examples have the same label -> return that label
		else if(sameLabel(examples)) {

			//return the label of any example because they are all the same
			return new Node(examples.get(0).getLabel());
		}

		//if all attributes are empty -> return the majority label amongst the set of examples
		else if(attributes_set.length == 0) {

			return new Node(majorityLabel(examples));
		}

		//REGULAR CASE
		else {

			//set total number of Republicans and Democrates
			for(int i = 0; i < examples.size(); i++) {
				if(examples.get(i).getLabel().equals(DEM)) {
					num_dems += 1;
				}

				else {
					num_reps += 1;
				}
			}

			//chose the attribute with the greatest information gain
			int a = chooseA(attributes_set, num_dems, num_reps);

			//make a new node for attribute A
			node = new Node(attributes_set[a].getName());
			node.attribute_num = attributes_set[a].getName();
			int attribute_index = node.attribute_num;
			int index = 0;

			//iterate over each distinct value for an attribute and recurse over it
			for(int i = 0; i < VK.length; i++) {

				//e : e in examples and e.A = vk (examples where A = vk)
				for(int j = 0; j < examples.size(); j++) {

					//get the array of votes for each example
					votes = examples.get(j).getVotes();

					if(votes[attribute_index].equals(VK[i])) {
						sk.put(index, examples.get(j));
						index++;
					}
				}

				//remove the attribute from the list of attributes
				Attribute[] atts = removeSafe(attributes_set, a);

				//recurse
				Node child = DTL(sk, atts, examples);
				node.addChild(child, VK[i]);
			}
		}
		return node;
	}

	/**
	 * Prints a decision tree
	 *
	 * @param DecisionTree	tree
	 */
	private static void printDTree(Node tree, String str) {

		//stack to add nodes to for print
		ArrayList<Node> visited = new ArrayList<Node>();
		ArrayList<Node> children = tree.getChildren();

		String temp = str + "\t";

		//prints the tree
		System.out.println(temp + tree.name);

		//move down the tree
		for(int i = 0; i < children.size(); i++) {
			Node v = children.get(i);

			if(v != null && visited.contains(v) == false) {
				printDTree(v, temp);
				visited.add(v);
			}
		}
	}

	/**
	 * Given a path, evaluates the decision
	 *
	 * @param String[]		example
	 * @param Node			tree
	 * @return String 		DEM or REP
	 */
	private static String evaluatePath(String[] example_array, Node tree) {

		//initialize answer
		String answer = "";

		//array for child nodes and visited
		ArrayList<Node> children = tree.getChildren();
		ArrayList<Node> visited = new ArrayList<Node>();

		//for each child of a node
		for(int i = 0; i < children.size(); i++) {

			//if the child is not a label
			if(tree.attribute_num >= 0) {

				//get each child
				Node v = children.get(i);
				String temp = v.name.substring(0, 1);
				String att = example_array[tree.attribute_num];

				//if the child is not null and it is the same as the vote type
				if(v != null && temp.equals(att) && visited.contains(v) == false) {

					//if v is a decision node
					if(v.attribute_num == -1) {
						answer = v.name.substring(2, 3);
						return answer;
					}

					//recurse and add to visited
					answer = evaluatePath(example_array, v);
					visited.add(v);
				}
			}
		}

		return answer;
	}

	/**
	 * Averages the scores of a test run for 43 examples
	 *
	 * @param String[][] 	test set
	 * @param Node			tree
	 * @return double[] 	correct/incorrect
	 */
	private static double[] averageRuns(String[][] test_set, Node tree) {

		//going to contain our answer
		double[] results = new double[2];
		String my_answer;
		String real_answer;

		//loop through the test_set running an evaluation on each one
		for(int i = 0; i < test_set.length; i++) {
			real_answer = test_set[i][NUM_ISSUES];
			my_answer = evaluatePath(test_set[i], tree);

			//test for equality
			if(real_answer.equals(my_answer)) {
				results[CORRECT] += 1;
			}

			else {
				results[INCORRECT] += 1;
			}
		}

		return results;
	}

	//main
	public static void main(String[] args) {

		//new DecisionTree object to access the data set
		HashMap<Integer, Example> examples = new HashMap<Integer, Example>();
		HashMap<Integer, Example> parent_examples = new HashMap<Integer, Example>();
		Attribute[] attributes = new Attribute[NUM_ISSUES];
		String[][][] test_set = new String[10][43][NUM_ISSUES + 1];
		double[][] result_set = new double[10][2];
		DecisionTree dTree = new DecisionTree();

		//going to be used for calculations of the average correctness
		int index;
		double num_corr = 0;
		double num_incorr = 0;
		double total = 0;

		//read data into array
		dTree.readData();
		String[][] dataSet = dTree.data;

		//fill a 3D array with the data sets
		for(int i = 0; i < 10; i++) {
			index = i * 43;
			for(int j = 0; j < 43; j++) {
				int jIndex = index + j;
				for(int k = 0; k < NUM_ISSUES + 1; k++) {
					test_set[i][j][k] = dataSet[jIndex][k];
				}
			}
		}

		//initialize the attributes and examples sets
		init(dTree, dataSet, attributes, examples);

		//start clock
		long tStart = System.currentTimeMillis();

		//initialize the data set from the tree to a 2D array of ints
		System.out.println("\n***BEGIN***\n");

		//make the tree
		Node root = dTree.DTL(examples, attributes, parent_examples);
		//printDTree(root, "");

		//loop through calculating the averages for each part of the test set
		for(int i = 0; i < test_set.length; i++) {
			result_set[i] = averageRuns(test_set[i], root);
		}

		//stop clock and see how long it took
		long tEnd = System.currentTimeMillis();
		long tDelta = tEnd - tStart;
		double elapsedSeconds = tDelta / 1000.0;

		//make totals for averages
		for(int j = 0; j < result_set.length; j++) {
			num_corr += result_set[j][CORRECT];
			num_incorr += result_set[j][INCORRECT];
			total = total + result_set[j][CORRECT] + result_set[j][INCORRECT];
		}

		//print to user
		double average = (num_corr / (total)) * 100;
		System.out.println("CORRECT: " + num_corr + " runs,\nINCORRECT: " + num_incorr + " runs,\nTOTAL: " + total + " runs,\nAVERAGE CORRECT RUNS(%): " + average + "%,\nELAPSED TIME(sec): " + elapsedSeconds + ".");
		System.out.println("\n***END***");
	}
}
