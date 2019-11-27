
/**
 * This class is a subclass of Card class and is used to model the card player played.
 * 
 * @author yutong
 *
 */
public class BigTwoCard extends Card {

	/**
	 * Constructor to create a BigTwoCard object, and it calls the constructor of Card class.
	 * 
	 * @param suit
	 *            an integer representing the index of the suit(0-3)
	 * @param rank
	 *            an integer representing the index of the rank(0-12)
	 *            
	 */
	public BigTwoCard(int suit, int rank) {
		super(suit, rank);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * This is the overridden method of its superclass
	 * It compares a single card to a single card with the rule:
	 * 2 is the biggest, 3 is the smallest; spade>heart>club>diamond; first rank, then suit.
	 * @see Card#compareTo(Card)
	 */
	public int compareTo(Card card) {
		if ((this.getRank()+11)%13 > (card.getRank()+11)%13) {
			return 1;
		} else if ((this.getRank()+11)%13 < (card.getRank()+11)%13) {
			return -1;
		} else if (this.suit > card.suit) {
			return 1;
		} else if (this.suit < card.suit) {
			return -1;
		} else {
			return 0;
		}
	}
}
