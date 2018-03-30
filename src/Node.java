import java.util.*;

public class Node {
	
	private static final String YEA = "+";
	private static final String NEA = "-";
	private static final String OTH = ".";
	
	//holds all of the parent examples for a node
	public Node parent, plus, minus, dot;		//mappings to parent and child (up-vote, down-vote, and undecided) nodes for each node	
	public String name;
	public int attribute_num;
	
	
	/**
	 * Constructor for the Node class
	 * 
	 * @param int	attribute number
	 */
	public Node(int a) {
		this.name = "Issue: " + (a);
		this.attribute_num = a;
		this.dot = null;
		this.minus = null;
		this.plus = null;
		
	}
	
	/**
	 * Constructor for when it is a decision node
	 * 
	 * @param str
	 */
	public Node(String str) {
		this.name = str;
		this.attribute_num = -1;
	}
	
	/**
	 * Gets the children for a node
	 * 
	 * @return ArrayList<Node>	children
	 */
	public ArrayList<Node> getChildren() {
		
		ArrayList<Node> children = new ArrayList<Node>();
		
		if(this.plus != null) {
			children.add(this.plus);
		}
		
		if(this.minus != null) {
			children.add(this.minus);
		}
		
		if(this.dot != null) {
			children.add(this.dot);
		}
		
		return children;
	}

	/**
	 * Adds a child node to a node
	 * 
	 * @param Node		child (link to a child node)
	 * @param String	vote (label of node)
	 */
	public void addChild(Node child, String vote) {
		
		if(vote.equals(YEA)) {
			child.name = YEA + " " + child.name;
			this.plus = child;
		}
		
		else if(vote.equals(NEA)) {
			child.name = NEA + " " + child.name;
			this.minus = child;
		}
		
		else if(vote.equals(OTH)) {
			child.name = OTH + " " + child.name;
			this.dot = child;
		}
		
		//set parent of child to the current node
		child.parent = this;
	}	
}
