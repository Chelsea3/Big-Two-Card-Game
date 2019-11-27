import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class is to build the GUI for the BigTwo Game, and also handle all the user actions.
 * It implemented CardGameTable interface
 * @author yutong
 *
 */
public class BigTwoTable implements CardGameTable {
	
	private BigTwoClient game;
	private boolean[] selected;
	private int activePlayer;
	private JFrame frame;
	private JPanel bigTwoPanel;
	private JButton playButton;
	private JButton passButton;
	private JTextArea msgArea;
	private JTextArea chatArea;
	private JTextField chatMsg;
	private JLabel chat;
	private Image[][] cardImages;
	private Image cardBackImage;
	private Image[] avatars;
	
	/**
	 * public constructor of BigTwoTable
	 * It set the frame and the inner panel of the table, and associate the CardGame game with the table
	 * @param game
	 * 			  this game is the CardGame will are handling
	 */
	public BigTwoTable(BigTwoClient game) {
		this.game=game;
		selected = new boolean[13];
		
		Font font=new Font("Calibri", 1, 30);
		
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Big Two");
		frame.setFont(font);
		
		bigTwoPanel = new BigTwoPanel();
		frame.add(bigTwoPanel, BorderLayout.CENTER);;
		
		JPanel eastP=new JPanel();
		eastP.setLayout(new BoxLayout(eastP, BoxLayout.Y_AXIS));
		msgArea = new JTextArea(10,30); //the size will be modified
		Font myfont=new Font("TimesRoman", 0, 20);
		msgArea.setFont(myfont);
		msgArea.setForeground(Color.BLACK);
		chatArea = new JTextArea(10,30); // the size will be modified
		chatArea.setFont(myfont);
		chatArea.setForeground(Color.BLACK);
		JScrollPane scrollPane=new JScrollPane(msgArea);
		JScrollPane scrollChat=new JScrollPane(chatArea);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollChat.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollChat.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		eastP.add(scrollPane);
		eastP.add(scrollChat);
		frame.add(eastP, BorderLayout.EAST);
		
		playButton=new JButton("PLAY");
		playButton.setFont(font);
		playButton.addActionListener(new PlayButtonListener());	
		passButton=new JButton("PASS");
		passButton.setFont(font);
		passButton.addActionListener(new PassButtonListener());
		chat = new JLabel("Message: ");
		chat.setFont(font);
		chat.setForeground(Color.WHITE); //change the color
		chatMsg=new JTextField(20);
		chatMsg.setFont(font);
		chatMsg.addActionListener(new EnterListener()); //implement enterlistener
		JPanel southP=new JPanel(new FlowLayout(1, 50, 5));
		southP.add(playButton);
		southP.add(passButton);
		southP.add(chat);
		southP.add(chatMsg);
		southP.setBackground(Color.GRAY);
		frame.add(southP, BorderLayout.SOUTH);
		
		cardImages = new Image[4][13];
		char[] rank= {'a','2','3','4','5','6','7','8','9','t','j','q','k'};
		char[] suit= {'d','c','h','s'};
		for (int i=0; i<4; i++) {
			for (int j=0; j<13; j++) {
				cardImages[i][j]=new ImageIcon("cards\\"+rank[j]+suit[i]+".gif").getImage();
			}
		}
		cardBackImage=new ImageIcon("cards\\back.jpg").getImage();
	
		avatars=new Image[4];
		for (int i=0; i<4; i++) {
			avatars[i]=new ImageIcon("cards\\player"+(i+1)+".png").getImage();
		}
		
		JMenuBar bar=new JMenuBar();
		JMenu menu = new JMenu("Menu");
		menu.setFont(myfont);
		JMenuItem connect = new JMenuItem("Connect");
		connect.setFont(myfont);
		connect.addActionListener(new ConnectMenuListener());
		JMenuItem quit = new JMenuItem("Quit");
		quit.setFont(myfont);
		quit.addActionListener(new QuitMenuListener());
		menu.add(connect);
		menu.add(quit);
		bar.add(menu);
		frame.setJMenuBar(bar);
		
		frame.setMinimumSize(new Dimension(1500, 1000));
		frame.setVisible(true);
	}
	
	/* (non-Javadoc)
	 * implemented from CardGameTable
	 * set the player with currentIdx as activePlayer
	 * @param activePlayer
	 * 					  the activePlayer with currenIdx
	 * @see CardGameTable#setActivePlayer(int)
	 */
	public void setActivePlayer(int activePlayer) {
		this.activePlayer=activePlayer;
	}
	
	/* (non-Javadoc)
	 * implemented from CardGameTable
	 * get the indices of the cards selected by the activePlayer
	 * @return an int array of index of selected cards
	 * @see CardGameTable#getSelected()
	 */
	public int[] getSelected() {
		int s=0;
		for (int i=0; i<selected.length; i++) {
			if (selected[i]) {
				s++;
			}
		}
		if (s==0) {
			return null;
		}
		int[] selectedCards=new int[s];
		int j=0;
		for (int i=0; i<selected.length; i++) {
			if (selected[i]==true) {
				selectedCards[j]=i;
				j++;
			}
			s++;
		}
		return selectedCards;
	}
	
	/* (non-Javadoc)
	 * implemented from CardGameTable
	 * set all the cards to the state of unselected
	 * @see CardGameTable#resetSelected()
	 */
	public void resetSelected() {
		for (int i=0; i < selected.length; i++) {
			selected[i]=false;
		}
	}
	
	/* (non-Javadoc)
	 * implemented from CardGameTable
	 * repaint the BigTwoTable
	 * @see CardGameTable#repaint()
	 */
	public void repaint() {
		if (game.getCurrentIdx()==game.getPlayerID()) {
			enable();
		}
		else
			disable();
		frame.repaint();
	}
	
	/**
	 * It paint the end game dialog by using JOptionPane
	 * it also send a message of type ready to the server for restarting the game
	 */
	public void paintEnd() {
		 if (game.endOfGame()) {
			 String endMsg = "";
	         for (int i = 0; i < game.getNumOfPlayers(); i++) {
	             if (game.getPlayerList().get(i).getCardsInHand().size() != 0) {
	                 if (i == game.getPlayerID()) {
	                     endMsg += "You have ";
	                 } else {
	                     endMsg += game.getPlayerList().get(i).getName() + " has ";
	                 }
	                 endMsg += game.getPlayerList().get(i).getCardsInHand().size() + " cards.\n";
	             } else {
	                 if (i == game.getPlayerID()) {
	                     endMsg += "You win!\n";
	                 } else {
	                     endMsg += game.getPlayerList().get(i).getName() + " wins.\n";
	                 }
	             }
	         }
	         JOptionPane.showMessageDialog(frame, endMsg, "Game Ends", JOptionPane.INFORMATION_MESSAGE);
	         reset();
	         game.sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
		 }
	}
	
	/* (non-Javadoc)
	 * implemented from CardGameTable
	 * print the message of card game state to the msgArea TextPanel.
	 * @see CardGameTable#printMsg(java.lang.String)
	 */
	public void printMsg (String msg) {
		msgArea.append(msg+"\n");
	}
	
	/* (non-Javadoc)
	 * implemented from CardGameTable
	 * clear all the message in msgArea by setting the msgArea to blank
	 * @see CardGameTable#clearMsgArea()
	 */
	public void clearMsgArea() {
		msgArea.setText("");
	}
	
	public void printToChat (String chat) {
		chatArea.append(chat+"\n");
	}
	
	public void clearChatArea() {
		chatArea.setText("");
	}
	
	/* (non-Javadoc)
	 * implemented from CardGameTable
	 * set all the components in the BigTwoTable to original state.
	 * reset the list of selected cards, clear the message area, and enable user interactions.
	 * @see CardGameTable#reset()
	 */
	public void reset() {
		resetSelected();
		clearMsgArea();
		clearChatArea();
		for (int i=0; i<game.getNumOfPlayers(); i++) {
			game.getPlayerList().get(i).removeAllCards();
		}
		enable();
	}
	
	/* (non-Javadoc)
	 * implemented from CardGameTable
	 * enable the user to click on the components
	 * @see CardGameTable#enable()
	 */
	public void enable() {
		playButton.setEnabled(true);
		passButton.setEnabled(true);
		for (Component c: bigTwoPanel.getComponents()) {
			c.setEnabled(true);
		}
	}
	
	/* (non-Javadoc)
	 * implemented from CardGameTable
	 * disable the user to click on the components
	 * @see CardGameTable#disable()
	 */
	public void disable() {
		playButton.setEnabled(false);
		passButton.setEnabled(false);
		for (Component c: bigTwoPanel.getComponents()) {
			c.setEnabled(false);
		}
	}
	
	/**
	 * The inner class extends JPanel, representing the main panel of BigTwo game - cards and player panel
	 * @author yutong
	 *
	 */
	class BigTwoPanel extends JPanel implements MouseListener {
		/**
		 * The public constructor to register the MouseListener to BigTwoPanel
		 */
		public BigTwoPanel() {
			this.addMouseListener(this);
		}
		
		/* (non-Javadoc)
		 * overridden form JPanel class to paint the components in BigTwoPanel, namely the player image,
		 * card images, card back image.
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		public void paintComponent(Graphics g) {
			Graphics2D g2d=(Graphics2D) g;
			Font gfont=new Font("Helvetica",Font.BOLD,18);
			g2d.setFont(gfont);
			Image background=new ImageIcon("cards\\background.jpg").getImage();
			g2d.drawImage(background, 0, 0, null);
			g2d.setColor(Color.WHITE);
			
			for (int j=0; j<4; j++) {
				if (game.getNumOfPlayers()>0 && game.getPlayerList().get(j).getName()!=null) {
					int cardsX=avatars[0].getWidth(this)+50;
					int cardsY=30*(j+1)+avatars[0].getHeight(this)*j+25;
					g2d.drawImage(avatars[j], 10, 30*(j+1)+avatars[0].getHeight(this)*j, this);
					Color color = (j == game.getCurrentIdx() ? Color.BLUE : Color.WHITE);
                    g2d.setColor(color);
					if (j == game.getPlayerID()) {
                        g2d.drawString(game.getPlayerList().get(j).getName()+"(You)", 10, 30*(j+1)+avatars[0].getHeight(this)*j);
                    } else {					
                    	g2d.drawString(game.getPlayerList().get(j).getName(), 10, 30*(j+1)+avatars[0].getHeight(this)*j);
                    }
					if (j==game.getPlayerID()) {
						for (int i=0; i<game.getPlayerList().get(j).getNumOfCards(); i++) {
							int suit = game.getPlayerList().get(j).getCardsInHand().getCard(i).getSuit();
							int rank = game.getPlayerList().get(j).getCardsInHand().getCard(i).getRank();
							if(!selected[i]){
								g2d.drawImage(cardImages[suit][rank], cardsX, cardsY, this);
							}else{
								g2d.drawImage(cardImages[suit][rank], cardsX, cardsY-10, this);
							}
							cardsX=cardsX+cardBackImage.getWidth(this)/2;
						}
					}
					else {
						for (int i=0; i<game.getPlayerList().get(j).getNumOfCards(); i++) {
							g2d.drawImage(cardBackImage, cardsX, cardsY, this);
							cardsX=cardsX+cardBackImage.getWidth(this)/2;
						}
					}
				}
			}
			
			if (game.getHandsOnTable().size()>0) {
				Hand lastHand = game.getHandsOnTable().get(game.getHandsOnTable().size()-1);
				int cardsX=avatars[0].getWidth(this)+50;
				int cardsY=frame.getHeight()*4/5-(frame.getHeight()/5-cardBackImage.getHeight(this))/2;
				g2d.setColor(Color.WHITE);
				g2d.drawString("Played by:", 10, cardsY+15);
				g2d.drawString(lastHand.getPlayer().getName(), 10, cardsY+35);
				for (int i=0; i<lastHand.size(); i++) {
					g2d.drawImage(cardImages[lastHand.getCard(i).getSuit()][lastHand.getCard(i).getRank()], cardsX, cardsY, this);
					cardsX=cardsX+cardBackImage.getWidth(this)/2;
				}
			}
		}
		
		/* (non-Javadoc)
		 * implemented from MouseListener interface
		 * handle the mouse click event
		 * get the index of the card selected by clicking on the card
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent evt) {
			
			if (evt.getButton()==MouseEvent.BUTTON1) {
				int x=evt.getX();
				int y=evt.getY();
				int cardsX=(game.getPlayerList().get(activePlayer).getNumOfCards()+1)*cardBackImage.getWidth(this)/2+42+avatars[0].getWidth(this);
				int cardsY=30*(activePlayer+1)+avatars[0].getHeight(this)*activePlayer+25;
				if (x<=cardsX) {
					int n=game.getPlayerList().get(activePlayer).getNumOfCards()-((cardsX-x)/(cardBackImage.getWidth(this)/2));
					if (n==game.getPlayerList().get(activePlayer).getNumOfCards()) {
						n=game.getPlayerList().get(activePlayer).getNumOfCards()-1;
					}
					if (!selected[n]) {
						if (y>=cardsY && y<=cardsY+cardBackImage.getHeight(this)) {
							selected[n]=true;
						}
					}
					else if (selected[n]) {
						if (y>=cardsY-10 && y<=cardsY+cardBackImage.getHeight(this)-10) {
							selected[n]=false;
						}
						else if (y>cardsY+cardBackImage.getHeight(this)-10 && y<=cardsY+cardBackImage.getHeight(this) && selected[n-1]==false && n>0) {
							selected[n-1]=true;
						}
					}
				}
			}
			frame.repaint();
		}
		
		/* (non-Javadoc)
		 * implement the abstract method from MouseListener interface
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent evt) {}
		/* (non-Javadoc)
		 * implement the abstract method from MouseListener interface
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent evt) {}
		/* (non-Javadoc)
		 * implement the abstract method from MouseListener interface
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		public void mouseExited(MouseEvent evt) {}
		/* (non-Javadoc)
		 * implement the abstract method from MouseListener interface
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		public void mouseEntered(MouseEvent evt) {}
	}
	
	/**
	 * an inner class implements the ActionListener interface
	 * When the play button is clicked, the makeMove function will be called.
	 * @author yutong
	 *
	 */
	class PlayButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			game.makeMove(activePlayer, getSelected());
			resetSelected();
		}
	}
	
	/**
	 * an inner class implements the ActionListener interface
	 * when the pass button is clicked, the makeMove function will be called, but the argument of makeMove
	 * is a null array.
	 * @author yutong
	 *
	 */
	class PassButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			resetSelected();
			game.makeMove(game.getPlayerID(), getSelected());
		}
	}
	
	/**
	 * an inner class implements the ActionListener interface
	 * It handles the restart memu item.
	 * when the restart is clicked, the game will reset all the components and restart a new game
	 * @author yutong
	 *
	 */
	class ConnectMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			if (!(game.isCon()))
				game.makeConnection();
			else
				printMsg("Already connected.");
		}
	}
	
	/**
	 * an inner class implements the ActionListener interface
	 * It handles the quit menu item
	 * when the quit is clicked, the game will quit the GUI
	 * @author yutong
	 *
	 */
	class QuitMenuListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			System.exit(0);
		}
	}
	
	/**
	 * an inner class implements the ActionListener interface
	 * it handles the enter key for the message area, and send the message to the server
	 * @author yutong
	 *
	 */
	class EnterListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			String message=chatMsg.getText();
			if (message!="") {
				CardGameMessage sendMsg = new CardGameMessage(CardGameMessage.MSG, -1, message);
				game.sendMessage(sendMsg);
				chatMsg.setText("");
			}
		}
	}
}