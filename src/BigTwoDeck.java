
/**
 * This is a subclass of Deck and it is used to model the deck at the beginning of the game.
 * @author yutong
 *
 */
public class BigTwoDeck extends Deck {

	/* (non-Javadoc)
	 * This is the overridden method of its superclass.
	 * It added 52 cards to the deck and shuffle it to prepare to card delivery.
	 * @see Deck#initialize()
	 */
	public void initialize() {
		removeAllCards();
		for (int j = 0; j < 13; j++) {
			for (int i = 0; i < 4; i++) {
				BigTwoCard card = new BigTwoCard(i, j);
				this.addCard(card);
			}
		}
		this.shuffle();
	}

}
