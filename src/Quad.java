
/**
 * This is a subclass of Hand class.
 * It is used to model the hand of Quad (4 same rank with 1 other rank cards).
 * @author yutong
 *
 */
public class Quad extends Hand {

	/**
	 * Constructor to create a Quad hand by calling its superclass constructor
	 * @param player
	 *              the active player that gives this hand.
	 * @param cards
	 *              the card that composes this hand.
	 */
	public Quad(CardGamePlayer player, CardList cards) {
		super(player, cards);
		// TODO Auto-generated constructor stub
	}
	/* (non-Javadoc)
	 * This is the overridden method of its superclass abstract method getType().
	 * If the hand composed is a valid Quad, then it returns the type that constructed
	 * or it will return "".
	 * @see Hand#getType()
	 */
	public String getType() {
		if (isValid())
			return "Quad";
		return "";
	}
	/* (non-Javadoc)
	 * This is the overridden method of its superclass abstract method isValid().
	 * If the hand composed is a valid hand of the same type as the class name(Quad), 
	 * then it returns true, or it will return false.
	 * @see Hand#isValid()
	 */
	public boolean isValid() {
		if (this.size()!=5) {
			return false;
		}
		this.sort();
		int j=0;
		for (int i=0; i<this.size()-1; i++) {
			if (this.getCard(i).getRank()==this.getCard(i+1).getRank()) {
				j++;
			}
		}
		if (j==3)
			return true;
		return false;
	}
}
