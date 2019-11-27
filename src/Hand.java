
/**
 * This is the subclass of Hand class and is used to model a hand.
 * This is an abstract class because we don't need a instance of Hand.
 * Hand has other concrete subclasses modeling specific hand type.
 * @author yutong
 *
 */
public abstract class Hand extends CardList {

	/**
	 * Constructor to construct a hand object, and it is called in its subclass constructor
	 * @param player
	 *              the active player that gives this hand.
	 * @param cards
	 *              the card that composes this hand.
	 */
	public Hand(CardGamePlayer player, CardList cards) {
		this.player=player;
		for (int i=0; i<cards.size(); i++) {
			this.addCard(cards.getCard(i));
		}
	}
	
	private CardGamePlayer player;
	/**
	 * get the active player
	 * @return a CardGamePlayer that played the hand
	 */
	public CardGamePlayer getPlayer() { return player; }
	/**
	 * get the top card in a composed hand to be used in the compareTo(Card).
	 * @return a BigTwoCard object which is the biggest card in the hand.
	 */
	public BigTwoCard getTopCard() {
		if (this.getType()=="Single" || this.getType()=="Pair" ||this.getType()=="Triple" ||this.getType()=="Straight"||this.getType()=="StraightFlush"||this.getType()=="Flush") {
			this.sort();	
			return (BigTwoCard) this.getCard(this.size()-1);
		}
		else if (this.getType()=="FullHouse"||this.getType()=="Quad") {
			this.sort();
			return (BigTwoCard) this.getCard(2);
		}
		return null;
	}
	/**
	 * compare two hands to determine if the hand can beat the other one hand.
	 * @param hand
	 *            a hand that this hand is going to compare with.
	 * @return true if this hand beats the argument hand, false otherwise.
	 */
	boolean beats(Hand hand) {
		if (this.getType()==hand.getType()) {
			if (this.getTopCard().compareTo(hand.getTopCard())==1)
				return true;
		}
		else if (this.getType()=="Flush" && hand.getType()=="Straight")
			return true;
		else if (this.getType()=="FullHouse" && (hand.getType()=="Straight" || hand.getType()=="Flush"))
			return true;
		else if (this.getType()=="Quad" && (hand.getType()=="Straight" || hand.getType()=="Flush" || hand.getType()=="FullHouse"))
			return true;
		else if (this.getType()=="StraightFlush" && (hand.getType()=="Straight" || hand.getType()=="Flush" || hand.getType()=="FullHouse" || hand.getType()=="Quad"))
			return true;
		return false;
	}
	
	/**
	 * This is an abstract method that is going to be override in its subclasses.
	 * @return true, if the hand composed is a valid hand of the same type as the class name
	 *         false otherwise.
	 */
	public abstract boolean isValid();
	/**
	 * This is an abstract method that is going to be override in its subclasses.
	 * @return a string of type, if the hand composed is a valid hand of the class name type
	 *         "" otherwise.
	 */
	public abstract String getType();
}
