
/**
 * This is a subclass of Hand class.
 * It is used to model the hand of FullHouse (3 same rank with 2 other same rank cards).
 * @author yutong
 *
 */
public class FullHouse extends Hand {

	/**
	 * Constructor to create a FullHouse hand by calling its superclass constructor
	 * @param player
	 *              the active player that gives this hand.
	 * @param cards
	 *              the card that composes this hand.
	 */
	public FullHouse(CardGamePlayer player, CardList cards) {
		super(player, cards);
		// TODO Auto-generated constructor stub
	}
	/* (non-Javadoc)
	 * This is the overridden method of its superclass abstract method getType().
	 * If the hand composed is a valid FullHouse, then it returns the type that constructed
	 * or it will return "".
	 * @see Hand#getType()
	 */
	public String getType() {
		if (isValid())
			return "FullHouse";
		return "";
	}
	/* (non-Javadoc)
	 * This is the overridden method of its superclass abstract method isValid().
	 * If the hand composed is a valid hand of the same type as the class name(FullHouse), 
	 * then it returns true, or it will return false.
	 * @see Hand#isValid()
	 */
	public boolean isValid() {
		if (this.size()!=5) {
			return false;
		}
		this.sort();
		if (this.getCard(2).getRank()==this.getCard(0).getRank() && this.getCard(3).getRank()==this.getCard(4).getRank()) {
			return true;
		}
		else if (this.getCard(2).getRank()==this.getCard(4).getRank() && this.getCard(1).getRank()==this.getCard(0).getRank()) {
			return true;
		}
		return false;
	}
}
