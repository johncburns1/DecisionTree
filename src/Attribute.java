public class Attribute {
	
	private int plusDem, minusDem, dotDem;
	private int plusRep, minusRep, dotRep, name;
	
	/**
	 * Constructor for the Attribute Class
	 */
	public Attribute(int i) {
		this.name = i;
		this.plusDem = 0;
		this.minusDem = 0;
		this.dotDem = 0;
		this.plusRep = 0;
		this.minusRep = 0;
		this.dotRep = 0;
	}
	
	/**
	 * Iterates Dem by 1
	 * 	
	 */
	public void itrPlusDem() {
		this.plusDem++;
	}
	
	/**
	 * Iterates Dem by 1
	 * 	
	 */
	public void itrMinusDem() {
		this.minusDem++;
	}
	
	/**
	 * Iterates Dem by 1
	 * 	
	 */
	public void itrDotDem() {
		this.plusDem++;
	}
	
	/**
	 * Iterates Rep by 1
	 * 
	 */
	public void itrPlusRep() {
		this.plusRep++;
	}
	
	/**
	 * Iterates Rep by 1
	 * 
	 */
	public void itrMinusRep() {
		this.minusRep++;
	}
	
	/**
	 * Iterates Rep by 1
	 * 
	 */
	public void itrDotRep() {
		this.dotRep++;
	}
	
	/**
	 * Get dem
	 * 
	 * @return int	dem
	 */
	public int getPlusDem() {
		return this.plusDem;
	}
	
	/**
	 * Get dem
	 * 
	 * @return int	dem
	 */
	public int getMinusDem() {
		return this.minusDem;
	}
	
	/**
	 * Get dem
	 * 
	 * @return int	dem
	 */
	public int getDotDem() {
		return this.dotDem;
	}
	
	/**
	 * Get rep
	 * 
	 * @return int	rep
	 */
	public int getPlusRep() {
		return this.plusRep;
	}
	
	/**
	 * Get rep
	 * 
	 * @return int	rep
	 */
	public int getMinusRep() {
		return this.minusRep;
	}
	
	/**
	 * Get rep
	 * 
	 * @return int	rep
	 */
	public int getDotRep() {
		return this.dotRep;
	}
	
	/**
	 * Sets all values to zero
	 * 
	 */
	public void reset() {
		this.plusDem = 0;
		this.minusDem = 0;
		this.dotDem = 0;
		this.plusRep = 0;
		this.minusRep = 0;
		this.dotRep = 0;
	}
	
	/**
	 * Gets the name of an attribute
	 * 
	 */
	public int getName() {
		return this.name;
	}
	
	/**
	 * Get total rep for an attribute
	 * 
	 * @return int	rep
	 */
	public int getTotalRep() {
		return this.getDotRep() + this.getMinusRep() + this.getPlusRep();
	}
	
	/**
	 * Get total rep for an attribute
	 * 
	 * @return int	rep
	 */
	public int getTotalDem() {
		return this.getDotDem() + this.getMinusDem() + this.getPlusDem();
	}
}
