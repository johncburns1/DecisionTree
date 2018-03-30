
public class Example {

	private static final int NUM_ISSUES = 10;
	private static final String DEM = "D";
	private static final String REP = "R";
	private String[] votes;
	private String label;
	private int total_dem;
	private int total_rep;
	
	/**
	 * Constructor for an example
	 */
	public Example() {
		this.votes = null;
		this.label = null;
		this.total_dem = 0;
		this.total_rep = 0;
	}
	
	/**
	 * Sets the votes for an example
	 * 
	 * @param String[]	str
	 */
	public void setVotes(String[] str) {
		this.votes = str;
		this.label = votes[NUM_ISSUES];
		
		if(this.label.equals(DEM)) {
			this.total_dem = 1;
			this.total_rep = 0;
		}
		
		else if(this.label.equals(REP)){
			this.total_rep = 1;
			this.total_dem = 0;
		}
	}
	
	/**
	 * Gets the set of votes
	 * 
	 * @return String[]	votes
	 */
	public String[] getVotes() {
		return this.votes;
	}
	
	/**
	 * Gets the label 
	 * 
	 * @return String	label
	 */
	public String getLabel() {
		return this.label;
	}
	
	/**
	 * Gets the num of reps
	 * 
	 * @return int	reps
	 */
	public int getReps() {
		return this.total_rep;
	}
	
	/**
	 * Gets the num of dems 
	 * 
	 * @return int	dems
	 */
	public int getDems() {
		return this.total_dem;
	}
}
