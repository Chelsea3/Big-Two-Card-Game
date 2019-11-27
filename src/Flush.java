
/**
 * This is a subclass of Hand class.
 * It is used to model the hand of Flush (5 same suit cards).
 * @author yutong
 *
 */
public class Flush extends Hand {

	/**
	 * Constructor to create a Flush hand by calling its superclass constructor
	 * @param player
	 *              the active player that gives this hand.
	 * @param cards
	 *              the card that composes this hand.
	 */
	public Flush(CardGamePlayer player, CardList cards) {
		super(player, cards);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * This is the overridden method of its superclass abstract method getType().
	 * If the hand composed is a valid Flush, then it returns the type that constructed
	 * or it will return "".
	 * @see Hand#getType()
	 */
	public String getType() {
		if (isValid())
			return "Flush";
		return "";
	}
	
	/* (non-Javadoc)
	 * This is the overridden method of its superclass abstract method isValid().
	 * If the hand composed is a valid hand of the same type as the class name(Flush), 
	 * then it returns true, or it will return false.
	 * @see Hand#isValid()
	 */
	public boolean isValid() {
		if (this.size()!=5) {
			return false;
		}
		int j=0;
		for (int i=0; i<this.size()-1; i++) {
			if (this.getCard(i).getSuit()!=this.getCard(i+1).getSuit()) {
				return false;
			}
			if ((this.getCard(i).getRank()+11)%13==(this.getCard(i+1).getRank()+11)%13-1) {
				j++;
			}
		}
		if (j==4) {
			return false;
		}
		return true;
	}
}
