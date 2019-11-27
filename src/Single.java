
/**
 * This is a subclass of Hand class.
 * It is used to model the hand of Single (1 cards).
 * @author yutong
 *
 */
public class Single extends Hand {

	/**
	 * Constructor to create a Single hand by calling its superclass constructor
	 * @param player
	 *              the active player that gives this hand.
	 * @param cards
	 *              the card that composes this hand.
	 */
	public Single(CardGamePlayer player, CardList cards) {
		super(player, cards);
//		System.out.println(player);
//		System.out.println(cards);
		// TODO Auto-generated constructor stub
	}
	/* (non-Javadoc)
	 * This is the overridden method of its superclass abstract method getType().
	 * If the hand composed is a valid Single, then it returns the type that constructed
	 * or it will return "".
	 * @see Hand#getType()
	 */
	public String getType() {
		if (isValid())
			return "Single";
		return "";
	}
	/* (non-Javadoc)
	 * This is the overridden method of its superclass abstract method isValid().
	 * If the hand composed is a valid hand of the same type as the class name(Single), 
	 * then it returns true, or it will return false.
	 * @see Hand#isValid()
	 */
	public boolean isValid() {
		if (this.size()==1) {
			for (int i=0; i<this.size(); i++) {
				if (this.getCard(i).getRank()>=0 && this.getCard(i).getRank()<13)
					return true;
			}
		}
		return false;
	}
	
}