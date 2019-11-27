
/**
 * This is a subclass of Hand class.
 * It is used to model the hand of Triple (3 same rank cards).
 * @author yutong
 *
 */
public class Triple extends Hand {

	/**
	 * Constructor to create a Triple hand by calling its superclass constructor
	 * @param player
	 *              the active player that gives this hand.
	 * @param cards
	 *              the card that composes this hand.
	 */
	public Triple(CardGamePlayer player, CardList cards) {
		super(player, cards);
		// TODO Auto-generated constructor stub
	}
	/* (non-Javadoc)
	 * This is the overridden method of its superclass abstract method getType().
	 * If the hand composed is a valid Triple, then it returns the type that constructed
	 * or it will return "".
	 * @see Hand#getType()
	 */
	public String getType() {
		if (isValid())
			return "Triple";
		return "";
	}
	/* (non-Javadoc)
	 * This is the overridden method of its superclass abstract method isValid().
	 * If the hand composed is a valid hand of the same type as the class name(Triple), 
	 * then it returns true, or it will return false.
	 * @see Hand#isValid()
	 */
	public boolean isValid() {
		if (this.size()!=3) {
			return false;
		}
		int rank=this.getCard(0).getRank();
		for (int i=1; i<this.size(); i++) {
			if (this.getCard(i).getRank()!=rank || rank<0 || rank>=13)
				return false;
		}
		return true;
	}
}
