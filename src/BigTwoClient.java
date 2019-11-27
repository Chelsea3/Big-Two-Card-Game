import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * This class is used to model the client of the Big Two Game
 * @author yutong
 *
 */
public class BigTwoClient implements CardGame, NetworkGame {
	private int numOfPlayers=0;
	private Deck deck;
	private ArrayList<CardGamePlayer> playerList;
	private ArrayList<Hand> handsOnTable;
	private int playerID;
	private String playerName;
	private String serverIP="127.0.0.1";
	private int serverPort=2396;
	private Socket sock;
	private ObjectOutputStream oos;
	private int currentIdx;
//	private BigTwoConsole bigTwoConsole;
	private BigTwoTable table;
	public static boolean cont; // public static variable for checkMove(),
								//when the handsOnTable is not empty, it becomes true
	private boolean isConnect=false;
	/**
	 * Constructor of the BigTwo class.
	 * It initialize four players and BigTwoTable.
	 */
	public BigTwoClient() {
		cont=false;
		playerList=new ArrayList<CardGamePlayer>();
		handsOnTable=new ArrayList<Hand>();
		for (int i=0; i<4; i++) {
			playerList.add(new CardGamePlayer());
		}
		table=new BigTwoTable(this);
		makeConnection();
	}
	/**
	 * @return whether the client is connected to the server
	 */
	public boolean isCon() {
		return isConnect;
	}
	
	/* (non-Javadoc)
	 * implemented from CardGame interface
	 * public getter to get the player number
	 * @return the number of player
	 * @see CardGame#getNumOfPlayers()
	 */
	public int getNumOfPlayers() { return playerList.size(); }

	/* (non-Javadoc)
	 * implemented from CardGame interface
	 * get the private instance variable deck
	 * @return a Deck object which represent the card deck.
	 * @see CardGame#getDeck()
	 */
	public Deck getDeck() {return deck;}
	
	/* (non-Javadoc)
	 * implemented from CardGame interface
	 * get the players who are playing the game, and it has 4 elements in the list
	 * @return an ArrayList of CardGamePlayer object which contains 4 players in it.
	 * @see CardGame#getPlayerList()
	 */
	public ArrayList<CardGamePlayer> getPlayerList() { return playerList; }
	
	/* (non-Javadoc)
	 * implemented from CardGame interface
	 * get the hands played on table
	 * @return an ArrayList of Hand object which contains all the hands played on table
	 *         from the beginning of the big two game.
	 * @see CardGame#getHandsOnTable()
	 */
	public ArrayList<Hand> getHandsOnTable() { return handsOnTable; } // Hand class
	
	/* (non-Javadoc)
	 * implemented from CardGame interface
	 * get the current player id
	 * @return an integer representing the index of the active player.
	 * @see CardGame#getCurrentIdx()
	 */
	public int getCurrentIdx() { return currentIdx; }
	
	/* (non-Javadoc)
	 * implemented from CardGame interface
	 * This starts the game with delivering cards in deck to four players. 
	 * @param deck
	 *            a Deck object initialized in main and already shuffled.
	 * @see CardGame#start(Deck)
	 */
	public void start(Deck deck) {
		deck = (BigTwoDeck) deck;
		handsOnTable.clear();
		int j=0;
		Card card=new Card(0,2);
		for (int i=0; i<4; i++) {
			playerList.get(i).removeAllCards();
		}
		while (j<52) {
			for (int i=0; i<4; i++) {
				playerList.get(i).addCard(deck.getCard(j));
				if (deck.getCard(j).getRank()==card.getRank() && deck.getCard(j).getSuit()==card.getSuit()) {
					currentIdx=i;
				}
				j++;
			}
		}
//		bigTwoConsole.setActivePlayer(currentIdx);
		table.setActivePlayer(playerID);
		for (int i=0; i<4; i++) {
			playerList.get(i).sortCardsInHand();
		}
//		bigTwoConsole.repaint();
		table.repaint();
		table.clearMsgArea();
		table.printMsg(" -----------------WELCOME TO BIGTWO!-----------------");
		table.printMsg(playerList.get(currentIdx).getName()+"'s turn");
	}
	
	/* (non-Javadoc)
	 * implemented from CardGame interface
	 * call checkMove to corresponding with the play and pass button to make a move.
	 * @param playerID
	 * 				  specify which player is acting
	 * @param cardIdx[]
	 * 				  specify the list of cards the player selected
	 * @see CardGame#makeMove(int, int[])
	 */
	public void makeMove(int playerID, int[] cardIdx) {
		CardGameMessage makemove=new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx);
		sendMessage(makemove);
	}
	
	/* (non-Javadoc)
	 * implemented from CardGame interface
	 * It is the main BigTwo logic. Every time we press the button, we will call checkMove.
	 * It checks whether the player selected a legal hand, and if it is legal, play the hand.
	 * After each time the player played a hand, check if the game ends.
	 * If the game doesn't end, move the active player to the next.
	 * @see CardGame#checkMove(int, int[])
	 */
	public void checkMove(int playerID, int[] cardIdx) {
		boolean stop=false;
		if (cont==false) {
			if (cardIdx==null) {
				table.printMsg("Not a legal move!!!");
				return;
			}
			if (playerList.get(playerID).play(cardIdx).contains(new Card(0,2))) {
				if (playerList.get(playerID).play(cardIdx).size()>=1 && playerList.get(playerID).play(cardIdx).size()<=5) {
					Hand hand0=BigTwoClient.composeHand(playerList.get(playerID), playerList.get(playerID).play(cardIdx));
					if (hand0!=null) {
						playerList.get(playerID).removeCards(hand0);
						handsOnTable.add(hand0);
						Hand lastHandOnTable = (handsOnTable.isEmpty()) ? null : handsOnTable.get(handsOnTable.size() - 1);
						if (lastHandOnTable!=null) {
							table.printMsg(" {" + lastHandOnTable.getType() + "} "+lastHandOnTable);
						}
						if (endOfGame()) {
							table.disable();
							table.printMsg("");
							table.printMsg("Game ends");
							for (int n=0; n<4; n++) {
								if (n!=currentIdx) {
									table.printMsg(playerList.get(n).getName()+" has "+playerList.get(n).getNumOfCards()+" cards in hand.");
								}
								else {
									table.printMsg(playerList.get(currentIdx).getName()+" wins the game.");
								}
							}
							table.repaint();
							table.reset();
							BigTwoClient.cont=false;
							table.paintEnd();
							CardGameMessage makeready = new CardGameMessage(CardGameMessage.READY, -1, null);
							sendMessage(makeready);
							return;
						}
						currentIdx=(currentIdx+1)%4;
						table.setActivePlayer(currentIdx);
						table.printMsg(playerList.get(currentIdx).getName()+"'s turn:");
						playerList.get(currentIdx).sortCardsInHand();
						table.resetSelected();
						table.repaint();
						cont=true;
						return;
					}
				}
			}
			if (cont==false) {
				table.printMsg("Not a legal move!!!");
				table.resetSelected();
				return;
			}
		}
		if (!stop && cont) {
			table.setActivePlayer(playerID);
			playerList.get(playerID).getCardsInHand().sort();
			table.repaint();
			if (playerList.get(playerID)!=handsOnTable.get(handsOnTable.size()-1).getPlayer() && cardIdx==null) {
				table.printMsg("{pass}");
				currentIdx=(currentIdx+1)%4;
				table.setActivePlayer(currentIdx);
				table.printMsg(playerList.get(currentIdx).getName()+"'s turn:");
				playerList.get(currentIdx).getCardsInHand().sort();
				table.resetSelected();
				table.repaint();
				if (endOfGame()) {
					table.disable();
					table.printMsg("");
					table.printMsg("Game ends");
					for (int n=0; n<4; n++) {
						if (n!=currentIdx) {
							table.printMsg(playerList.get(n).getName()+" has "+playerList.get(n).getNumOfCards()+" cards in hand.");
						}
						else {
							table.printMsg(playerList.get(currentIdx).getName()+" wins the game.");
						}
					}
					table.repaint();
					table.reset();
					BigTwoClient.cont=false;
					table.paintEnd();
					CardGameMessage makeready = new CardGameMessage(CardGameMessage.READY, -1, null);
					sendMessage(makeready);
					return;
				}
				return;
			}
			else if (playerList.get(playerID)==handsOnTable.get(handsOnTable.size()-1).getPlayer() && cardIdx==null) {
				table.printMsg("Not a legal move!!!");
				table.resetSelected();
				return;
			}
			else if (playerList.get(playerID)==handsOnTable.get(handsOnTable.size()-1).getPlayer() && cardIdx!=null) {
				Hand hand0=BigTwoClient.composeHand(playerList.get(playerID), playerList.get(playerID).play(cardIdx));
				if (hand0!=null) {
					playerList.get(playerID).removeCards(hand0);
					handsOnTable.add(hand0);
					Hand lastHandOnTable = (handsOnTable.isEmpty()) ? null : handsOnTable.get(handsOnTable.size() - 1);
					if (lastHandOnTable!=null) {
						table.printMsg(" {" + lastHandOnTable.getType() + "} "+lastHandOnTable);
					}
					if (endOfGame()) {
						table.disable();
						table.printMsg("");
						table.printMsg("Game ends");
						for (int n=0; n<4; n++) {
							if (n!=currentIdx) {
								table.printMsg(playerList.get(n).getName()+" has "+playerList.get(n).getNumOfCards()+" cards in hand.");
							}
							else {
								table.printMsg(playerList.get(currentIdx).getName()+" wins the game.");
							}
						}
						table.repaint();
						table.reset();
						BigTwoClient.cont=false;
						table.paintEnd();
						CardGameMessage makeready = new CardGameMessage(CardGameMessage.READY, -1, null);
						sendMessage(makeready);
						return;
					}
					currentIdx=(currentIdx+1)%4;
					table.setActivePlayer(currentIdx);
					table.printMsg(playerList.get(currentIdx).getName()+"'s turn:");
					playerList.get(currentIdx).getCardsInHand().sort();
					table.resetSelected();
					table.repaint();
					return;
				}
			}
			else {
				if (playerList.get(playerID).play(cardIdx).size()>=1 && playerList.get(playerID).play(cardIdx).size()<=5) {
					Hand hand1=BigTwoClient.composeHand(playerList.get(playerID), playerList.get(playerID).play(cardIdx));
					if (hand1==null) {
						table.printMsg("Not a legal move!!!");
						return;
					}
					if (hand1!=null) {
						if (hand1.beats(handsOnTable.get(handsOnTable.size()-1))) {
							playerList.get(playerID).removeCards(hand1);
							handsOnTable.add(hand1);
							Hand lastHandOnTable = (handsOnTable.isEmpty()) ? null : handsOnTable.get(handsOnTable.size() - 1);
							if (lastHandOnTable!=null) {
								table.printMsg(" {" + lastHandOnTable.getType() + "} "+lastHandOnTable);
							}
							if (endOfGame()) {
								table.disable();
								table.printMsg("");
								table.printMsg("Game ends");
								for (int n=0; n<4; n++) {
									if (n!=currentIdx) {
										table.printMsg(playerList.get(n).getName()+" has "+playerList.get(n).getNumOfCards()+" cards in hand.");
									}
									else {
										table.printMsg(playerList.get(currentIdx).getName()+" wins the game.");
									}
								}
								table.repaint();
								table.reset();
								BigTwoClient.cont=false;
								table.paintEnd();
								CardGameMessage makeready = new CardGameMessage(CardGameMessage.READY, -1, null);
								sendMessage(makeready);
								return;
							}
							currentIdx=(currentIdx+1)%4;
							table.setActivePlayer(currentIdx);
							table.printMsg(playerList.get(currentIdx).getName()+"'s turn:");
							playerList.get(currentIdx).getCardsInHand().sort();
							table.resetSelected();
							table.repaint();
							return;
						}
						else 
							table.printMsg("Not a legal move!!!");
					}
				}
			}
		}
	} //Big Two Game logic
	
	/* (non-Javadoc)
	 * implemented from CardGame interface
	 * This checks whether the game is at the end (the current player doesn't have any cards)
	 * @return boolean true to represent the game is at the end.
	 * @see CardGame#endOfGame()
	 */
	public boolean endOfGame() {
		if (playerList.get(currentIdx).getNumOfCards()==0) {
				return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * implemented from NetworkGame interface, a getter for playerID
	 * @see NetworkGame#getPlayerID()
	 */
	public int getPlayerID() {
		return playerID;
	}
	
	/* (non-Javadoc)
	 * implemented from NetworkGame interface, a setter for the playerID
	 * @see NetworkGame#setPlayerID(int)
	 */
	public void setPlayerID(int playerID) {
		this.playerID=playerID;
	}
	
	/* (non-Javadoc)
	 * implemented from NetworkGame interface, a getter for playerName
	 * @see NetworkGame#getPlayerName()
	 */
	public String getPlayerName() {
		return playerName;
	}
	
	/* (non-Javadoc)
	 * implemented from NetworkGame interface, a setter for playerName
	 * @see NetworkGame#setPlayerName(java.lang.String)
	 */
	public void setPlayerName (String playerName) {
		this.playerName=playerName;
	}
	
	/* (non-Javadoc)
	 * implemented from NetworkGame interface, a getter for serverIP
	 * @see NetworkGame#getServerIP()
	 */
	public String getServerIP() {
		return serverIP;
	}
	
	/* (non-Javadoc)
	 * implemented from NetworkGame interface, a setter for serverIP
	 * @see NetworkGame#setServerIP(java.lang.String)
	 */
	public void setServerIP(String serverIP) {
		this.serverIP=serverIP;
	}
	
	/* (non-Javadoc)
	 * implemented from NetworkGame interface, a getter for serverPort
	 * @see NetworkGame#getServerPort()
	 */
	public int getServerPort() {
		return serverPort;
	}
	
	/* (non-Javadoc)
	 * implemented from NetworkGame interface, a setter for serverPort
	 * @see NetworkGame#setServerPort(int)
	 */
	public void setServerPort(int serverPort) {
		this.serverPort=serverPort;
	}
	
	/* (non-Javadoc)
	 * implemented from NetworkGame interface. It makes client connect to the server.
	 * @see NetworkGame#makeConnection()
	 */
	public synchronized void makeConnection() {
		String name=JOptionPane.showInputDialog("Player name: ");
		playerName=name;
		if (playerName!=null) {
			try {
				sock=new Socket(serverIP, serverPort);
				try {
					oos=new ObjectOutputStream(sock.getOutputStream());
					Thread receiveMsg=new Thread(new ServerHandler()); //receiveMsg.start();
					receiveMsg.start();
					sendMessage(new CardGameMessage(CardGameMessage.JOIN, -1, playerName));
					sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
					isConnect=true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		else {
			table.printMsg("connection failed, select \"connect\" to connect to server.");
		}
	}
	
	/* (non-Javadoc)
	 * implemented from NetworkGame interface. It deals with different kind of message and parse them.
	 * @see NetworkGame#parseMessage(GameMessage)
	 */
	public synchronized void parseMessage(GameMessage message) {
		if (message.getType()==CardGameMessage.PLAYER_LIST) {
			playerID=message.getPlayerID();
			table.printMsg("Player "+(playerID+1)+" is connected.");
			for (int i=0; i<playerList.size(); i++) {
				playerList.get(i).setName(((String[])message.getData())[i]);
			}
		}
		else if (message.getType()==CardGameMessage.JOIN) {
			playerList.get(message.getPlayerID()).setName((String) message.getData());
			numOfPlayers++;
		}
		else if (message.getType()==CardGameMessage.FULL) {
			table.printMsg("The server is full and cannot join the game");
			isConnect=false;
		}
		else if (message.getType()==CardGameMessage.QUIT) {
			table.printMsg(playerList.get(message.getPlayerID()).getName() + " (" + (String)message.getData() + ") left the game.");
			playerList.get(message.getPlayerID()).setName(null);
			message.setPlayerID(-1);
			if (!endOfGame()) {
				table.disable();
			}
			numOfPlayers--;
			CardGameMessage makeready = new CardGameMessage(CardGameMessage.READY, -1, null);
			sendMessage(makeready);
		}
		else if (message.getType()==CardGameMessage.READY) {
			table.printMsg(playerList.get(message.getPlayerID()).getName()+" is ready.");
		}
		else if (message.getType()==CardGameMessage.START) {
			table.reset();
			table.printMsg("Let's start!");
			start((BigTwoDeck) message.getData());
		}
		else if (message.getType()==CardGameMessage.MOVE) {
			checkMove(message.getPlayerID(), (int[]) message.getData());
		}
		else if (message.getType()==CardGameMessage.MSG) {
			table.printToChat((String) message.getData());
		}
		table.repaint();
	}
	
	/* (non-Javadoc)
	 * implemented from NetworkGame interface. It sends message to the server.
	 * @see NetworkGame#sendMessage(GameMessage)
	 */
	public void sendMessage(GameMessage message) {
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 
	 * an inner class that implements the Runnable interface, and it handles the massages received from server
	 * @author yutong
	 *
	 */
	public class ServerHandler implements Runnable{
		ObjectInputStream reader;	
		public ServerHandler() {
			try {
				reader=new ObjectInputStream(sock.getInputStream());
			}catch (Exception ex) {
				ex.getStackTrace();
			}
		}
		public synchronized void run () {
			CardGameMessage message;
			try {
				while ((message=(CardGameMessage) reader.readObject())!=null) {
					parseMessage(message);
				}
			}catch (Exception ex) {
				ex.getStackTrace();
				table.reset();
				isConnect=false;
				makeConnection();
			}
		}
	}
	
	/**
	 * Create an instance of BigTwo class and BigTwoDeck class, and initialize them.
	 * Call the start method to start the game.
	 * @param args
	 *            not being used in this application
	 */
	public static void main (String[] args) {
		BigTwoClient game=new BigTwoClient();
	}
	
	/**
	 * Compose a hand from the selected cards.
	 * 
	 * @param player
	 *              a CardGamePlayer object representing who is playing these cards.
	 * @param cards
	 *              a CardList object containing the selected cards index.
	 * @return a valid hand composed of all the cards in CardList
	 *         if there is no valid hand can be composed, return null.
	 */
	public static Hand composeHand (CardGamePlayer player, CardList cards) {
		int len=cards.size();
		if (len==1) {
			Single hand=new Single(player, cards);
			if (hand.getType()=="Single") {
				return hand;
			}
		}// Hand class instance
		else if (len==2) {
			Pair hand=new Pair(player, cards);
			if (hand.getType()=="Pair") {
				return hand;
			}
		}	
		else if(len==3) {
			Triple hand=new Triple(player, cards);
			if (hand.getType()=="Triple")
				return hand;
		}
		else if (len==5) {
			Straight hand=new Straight(player, cards);
			if (hand.getType()=="Straight") {
				return hand;
			}
			Flush hand1=new Flush(player, cards);
			if (hand1.getType()=="Flush") { 
				return hand1;
			}
			FullHouse hand2=new FullHouse(player, cards);
			if (hand2.getType()=="FullHouse") { 
				return hand2;
			}
			Quad hand3=new Quad(player, cards);
			if (hand3.getType()=="Quad") { 
				return hand3;
			}
			StraightFlush hand4=new StraightFlush(player, cards);
			if (hand4.getType()=="StraightFlush") { 
				return hand4;
			}
		}
		return null;
	}
}
