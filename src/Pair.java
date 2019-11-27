/**
 * This is a subclass of Hand class.
 * It is used to model the hand of Pair ( 2 same rank cards).
 * @author yutong
 *
 */
public class Pair extends Hand {

	/**
	 * Constructor to create a Pair hand by calling its superclass constructor
	 * @param player
	 *              the active player that gives this hand.
	 * @param cards
	 *              the card that composes this hand.
	 */
	public Pair(CardGamePlayer player, CardList cards) {
		super(player, cards);
		// TODO Auto-generated constructor stub
	}
	/* (non-Javadoc)
	 * This is the overridden method of its superclass abstract method getType().
	 * If the hand composed is a valid Pair, then it returns the type that constructed
	 * or it will return "".
	 * @see Hand#getType()
	 */
	public String getType() {
		if (isValid())
			return "Pair";
		return "";
	}
	/* (non-Javadoc)
	 * This is the overridden method of its superclass abstract method isValid().
	 * If the hand composed is a valid hand of the same type as the class name(Pair), 
	 * then it returns true, or it will return false.
	 * @see Hand#isValid()
	 */
	public boolean isValid() {
		if (this.size()==2) {
			int rank=this.getCard(0).getRank();
			for (int i=1; i<this.size(); i++) {
				if (this.getCard(i).getRank()==rank && rank>=0 &&rank<13)
					return true;
			}
		}
		return false;
	}
}
