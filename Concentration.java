//import statements
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.*;

//class representing a card
class Card {
  String suit;
  int rank;
  boolean faceUp;
  int x;
  int y;
  Color color;
  ArrayList<String> drawn = new ArrayList<String>(Arrays.asList("A", "2", "3", "4", "5",
      "6", "7", "8", "9", "10", "J", "Q", "K"));

  //constructor
  Card(String suit, int rank, boolean faceUp, int x, int y) {
    this.suit = suit;
    this.rank = rank;
    this.faceUp = faceUp;
    this.x = x;
    this.y = y;
    if (this.suit.equals("♥") || this.suit.equals("♦")) {
      this.color = Color.red;
    }
    else {
      this.color = Color.black;
    }
  }

  //method to draw a Card onto the given WorldScene
  public void draw(WorldScene world) {
    if (faceUp) {
      world.placeImageXY(new RectangleImage(30, 50, "solid", Color.white), x, y);
      world.placeImageXY(new TextImage(drawn.get(this.rank - 1), 16, color), x, y);
      world.placeImageXY(new TextImage(this.suit, 12, this.color), x - 12, y - 14);
      world.placeImageXY(new TextImage(this.suit, 12, this.color), x + 5, y + 16);
    }
    else {
      //world.placeImageXY(new FromFileImage(), x, y);
      world.placeImageXY(new RectangleImage(30, 50, "solid", Color.blue), x, y);
    } 
  }
}

//class that represents a Deck
class Deck {
  ArrayList<Card> deck;
  ArrayList<Card> flippedCards = new ArrayList<Card>();
  Random rand;
  ArrayList<String> suits = new ArrayList<String>(Arrays.asList("♥", "♣", "♠", "♦"));
  ArrayList<Integer> values = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9,
      10, 11, 12, 13));
  int matchesLeft = 26;
  int flips = 0;
  boolean hardMode = false;

  //constructor
  Deck() {
    this.rand = new Random();
    this.deck = makeShuffledDeck();
  }

  //Convenience constructor that allows the user to determine the Random
  Deck(Random rand) {
    this.rand = rand;
    this.deck = makeShuffledDeck();
  }
  
  //method that returns a shuffled deck
  ArrayList<Card> makeShuffledDeck() {
    ArrayList<Card> start = new ArrayList<Card>();
    ArrayList<Card> shuffle = new ArrayList<Card>();
    int y = 20;
    int x = 30;
    
    //creating the initial, ordered deck
    for (int suit = 0; suit < 4; suit ++) {
      for (int v = 0; v < 13; v++) {
        start.add(new Card(suits.get(suit), values.get(v), false, 0, 0));
      }
    }
    
    //shuffling the deck
    for (int row = 0; row < 4; row ++) {
      y += 70; 
      for (int col = 0; col < 13; col++) {
        Card addCard = start.remove(rand.nextInt(start.size()));
        shuffle.add(new Card(addCard.suit, addCard.rank, false, x, y));
        x += 40;
      }
      x = 30;
    }
    return shuffle;
  }
  
  //EFFECT: if another flip is possible, this method changes the card's faceUp value to true,
  //adds it to this Deck's flippedCards, and increments the flips so far
  public void flip(int x, int y) {
    for (Card card : deck) {
      if (card.x <= x + 13 && card.x >= x - 13 && card.y <= y + 25 && card.y >= y - 25
          && flippedCards.size() < 2 && !flippedCards.contains(card)) {
        card.faceUp = true;
        flippedCards.add(card);
        flips += 1;
      }
    }   
  }
  
  //EFFECT: if they meet the criteria for a match, removes the flipped cards from the deck,
  // and decrements matchesLeft
  public void match() {
    if (flippedCards.size() == 2 && point(flippedCards.get(0), flippedCards.get(1))) {
      deck.remove(flippedCards.get(0));
      deck.remove(flippedCards.get(1));
      this.matchesLeft -= 1;
      flippedCards.clear();
    }
  }
  
  //returns if the two given cards meet the criteria for a match, by checking rank,
  //that they are not the same card, and suit
  public boolean point(Card c1, Card c2) {
    if (hardMode) {
      return c1.rank == c2.rank && !c1.suit.equals(c2.suit) && c1.color.equals(c2.color);
    }
    else {
      return c1.rank == c2.rank && !c1.suit.equals(c2.suit);
    }
  }
  
  //EFFECT: changes the value of faceUp to false for all cards left in the deck,
  //clears the flippedCards array
  public void faceDown() {
    for (Card card : deck) {
      card.faceUp = false;
      flippedCards.clear();
    }
  }

  //EFFECT: draws each card in the deck
  public void draw(WorldScene world) {
    for (Card card : deck) {
      card.draw(world);
    }
  }
}


//class that represents the game Concentration
class Concentration extends World {
  final int SCREEN_HEIGHT = 800;
  final int SCREEN_WIDTH = 1200;
  Deck gameDeck;
  int clock;
  int clicks;
  WorldScene gameWorld;

  //constructor
  Concentration() {
    this.gameDeck = new Deck();
  }

  //Convenience constructor that takes a Random to be used when constructing the Deck
  Concentration(Random rand) {
    this.gameDeck = new Deck(rand);
  }

  //method that draws the current gameWorld
  public WorldScene makeScene() {
    gameWorld = new WorldScene(SCREEN_WIDTH, SCREEN_HEIGHT);
    gameWorld.placeImageXY(new RectangleImage(SCREEN_WIDTH, SCREEN_HEIGHT, "solid",
        Color.lightGray), 0, 0);
    gameWorld.placeImageXY(new TextImage("Concentration!", 20, Color.blue), 100, 30);
    gameWorld.placeImageXY(new TextImage("Score: " + gameDeck.matchesLeft, 15, Color.blue),
        270, 20);
    gameWorld.placeImageXY(new TextImage("Clock: " + clock / 10 + " seconds", 15, Color.blue),
        260, 40);
    gameWorld.placeImageXY(new TextImage("Flips: " + gameDeck.flips, 20, Color.blue), 420, 15);
    gameWorld.placeImageXY(new TextImage("Remaining Flips: " + (300 - gameDeck.flips), 15,
        Color.blue), 420, 40);
    gameWorld.placeImageXY(new TextImage("Press 1 / 2 to switch to easy / hard mode"
        + "(will reset game)", 10, Color.blue), 150, 50);
    gameDeck.draw(gameWorld);
    return gameWorld;
  }

  //method that draws the ending scene of the game
  public WorldScene lastScene(String message) {
    gameWorld.placeImageXY(new TextImage(message, 60, Color.blue), 250, 350);
    return gameWorld;
  }
  
  //method that updates the world every tick
  //EFFECT: increments clock, periodically turns cards face down
  public void onTick() {
    clock ++;
    if (clock % 25 == 0) {
      this.gameDeck.faceDown();
    }
  }

  //method that updates world every click
  //EFFECT: flips card that has been clicked on, checks for matches, checks if the game has ended,
  // and increments clicks
  public void onMouseReleased(Posn pos) {
    this.gameDeck.flip(pos.x, pos.y);
    this.gameDeck.match();
    if (gameDeck.matchesLeft == 0) {
      this.endOfWorld("You won :)");
    }
    
    if (gameDeck.flips > 300) {
      this.endOfWorld("You lost!");
    }
    clicks ++ ;
  }

  //method that updates the world when keys are pressed
  //EFFECT: if the key pressed is r, resets the gameDeck
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.gameDeck = new Deck();
      clock = 0;
      clicks = 0;
    }
    
    if (key.equals("1")) {
      this.gameDeck = new Deck();
      this.gameDeck.hardMode = false;
      clock = 0;
      clicks = 0;
    }
    
    if (key.equals("2")) {
      this.gameDeck = new Deck();
      this.gameDeck.hardMode = true;
      clock = 0;
      clicks = 0;
    }
  }
}

//examples class
class ExamplesConcentration {
  Concentration testGame;
  Concentration testGame2;
  Concentration testGame3;
  Concentration newGame;
  Deck testDeck;
  Deck testDeck2;
  Deck testDeck3;
  Card AceHeart;
  Card AceDiamond;
  Card AceSpade;
  
  //initializes all fields
  void init() {
    testGame = new Concentration(new Random(12));
    testGame2 = new Concentration(new Random(15));
    testGame3 = new Concentration(new Random(12));
    newGame = new Concentration(new Random(15));
    testDeck = new Deck(new Random(12));
    testDeck2 = new Deck(new Random(15));
    testDeck3 = new Deck(new Random(12));
    AceHeart = new Card("♥", 1, false, 80, 30);
    AceDiamond = new Card("♦", 1, false, 80, 30);
    AceSpade = new Card("♠", 1, false, 80, 30);
  }
  
  //tests creating a shuffled deck (each time a new Deck object is created)
  boolean testMakeShuffledDeck(Tester t) {
    init();
    return t.checkExpect(testDeck2.deck.size(), 52)
        && t.checkExpect(testDeck2.deck.get(0), testGame2.gameDeck.deck.get(0))
        && t.checkFail(testDeck2, testDeck)
        && t.checkExpect(testDeck, new Deck(new Random(12)));
  }
  
  //tests the function to flip a card at a given position
  boolean testFlip(Tester t) {
    init();
    testDeck.flip(30,80);
    testDeck2.flip(30,80);
    testDeck2.flip(70,80);
    return t.checkExpect(testDeck.deck.get(0).faceUp, true)
        && t.checkExpect(testDeck.deck.get(1).faceUp, false)
        && t.checkExpect(testDeck2.deck.get(0).faceUp, true)
        && t.checkExpect(testDeck2.deck.get(1).faceUp, true)
        && t.checkExpect(testDeck.flips, 1)
        && t.checkExpect(testDeck2.flips, 2)
        && t.checkExpect(testDeck.flippedCards.size(), 1)
        && t.checkExpect(testDeck2.flippedCards.size(), 2);
  }
  
  // testing the function that determines if two cards match
  boolean testMatch(Tester t) {
    init();
    Card cardRemoved = testDeck.deck.get(0);
    Card cardRemoved2 = testDeck.deck.get(49);
    testDeck.flippedCards.add(testDeck.deck.get(0));
    testDeck.flippedCards.add(testDeck.deck.get(49));
    testDeck.match();
    testDeck.faceDown();
    Card cardStay = testDeck.deck.get(1);
    Card cardStay2 = testDeck.deck.get(5);
    testDeck.flippedCards.add(testDeck.deck.get(1));
    testDeck.flippedCards.add(testDeck.deck.get(5));
    testDeck.match();
    
    return t.checkExpect(testDeck.flippedCards.size(), 2)
        && t.checkExpect(testDeck.deck.contains(cardRemoved), false)
        && t.checkExpect(testDeck.deck.contains(cardRemoved2), false)
        && t.checkExpect(testDeck.matchesLeft, 25)
        && t.checkExpect(testDeck.deck.contains(cardStay), true)
        && t.checkExpect(testDeck.deck.contains(cardStay2), true);
  }
  
  //testing the method to determine if the match of two cards scores a point
  boolean testPoint(Tester t) {
    init();
    testDeck.hardMode = true;
    return t.checkExpect(testDeck.point(AceDiamond, AceHeart), true)
        && t.checkExpect(testDeck.point(AceHeart, AceSpade), false)
        && t.checkExpect(testGame.gameDeck.point(testGame.gameDeck.deck.get(0),
            testGame.gameDeck.deck.get(0)), false)
        && t.checkFail(testGame.gameDeck.deck.get(0), testGame.gameDeck.deck.get(2));
  }
  
  //tests method to turn faceUp cards back down
  boolean testFaceDown(Tester t) {
    init();
    testDeck.flippedCards.add(AceHeart);
    testDeck.flippedCards.add(AceDiamond);
    testDeck.faceDown();
    return t.checkExpect(testDeck.flippedCards.size(), 0);
  }
  
  //testing the makeScene function
  boolean testMakeScene(Tester t) {
    init();
    newGame.gameWorld = new WorldScene(1200, 800);
    newGame.gameWorld.placeImageXY(new RectangleImage(1200, 800, "solid", Color.lightGray), 0, 0);
    newGame.gameWorld.placeImageXY(new TextImage("Concentration!", 20, Color.blue), 100, 30);
    newGame.gameWorld.placeImageXY(new TextImage("Score: " + newGame.gameDeck.matchesLeft, 15,
        Color.blue), 270, 20);
    newGame.gameWorld.placeImageXY(new TextImage("Clock: " + newGame.clock / 10 + " seconds", 15,
        Color.blue), 260, 40);
    newGame.gameWorld.placeImageXY(new TextImage("Flips: " + newGame.gameDeck.flips, 20,
        Color.blue),420, 15);
    newGame.gameWorld.placeImageXY(new TextImage("Remaining Flips: " + (300
        - newGame.gameDeck.flips), 15, Color.blue), 420, 40);
    newGame.gameWorld.placeImageXY(new TextImage("Press 1 / 2 to switch to easy / hard mode"
        + "(will reset game)", 10, Color.blue), 150, 50);
    newGame.gameDeck.draw(newGame.gameWorld);
    testGame2.makeScene();
 
    testGame3.gameWorld = new WorldScene(1200, 800);
    testGame3.gameWorld.placeImageXY(new RectangleImage(1200, 800, "solid",
        Color.lightGray), 0, 0);
    testGame3.gameWorld.placeImageXY(new TextImage("Concentration!", 20, Color.blue), 100, 30);
    testGame3.gameWorld.placeImageXY(new TextImage("Score: " + testGame3.gameDeck.matchesLeft,
        15, Color.blue), 270, 20);
    testGame3.gameWorld.placeImageXY(new TextImage("Clock: " + testGame3.clock / 10
        + " seconds", 15, Color.blue), 260, 40);
    testGame3.gameWorld.placeImageXY(new TextImage("Flips: " + testGame3.gameDeck.flips, 20,
        Color.blue), 420, 15);
    testGame3.gameWorld.placeImageXY(new TextImage("Remaining Flips: " + (300
        - testGame3.gameDeck.flips), 15, Color.blue), 420, 40);
    testGame3.gameWorld.placeImageXY(new TextImage("Press 1 / 2 to switch to easy / hard mode"
        + "(will reset game)", 10, Color.blue), 150, 50);
    testGame3.gameDeck.draw(testGame3.gameWorld);
    testGame.makeScene();
    
    return t.checkExpect(testGame2.gameWorld, newGame.gameWorld)
        && t.checkExpect(testGame3.gameWorld, testGame.gameWorld);
  }
  
  // testing drawing a card
  boolean testDraw(Tester t) {
    init();
    WorldScene test = new WorldScene(100, 100);
    AceHeart.faceUp = true;
    AceHeart.draw(test);
    WorldScene test2 = new WorldScene(100,100);
    test2.placeImageXY(new RectangleImage(30, 50, "solid", Color.white), 80, 30);
    test2.placeImageXY(new TextImage("A", 16, Color.red), 80, 30);
    test2.placeImageXY(new TextImage("♥", 12, Color.red), 68, 16);
    test2.placeImageXY(new TextImage("♥", 12, Color.red), 85, 46);
    
    WorldScene test3 = new WorldScene(100,100);
    AceSpade.draw(test3);
    WorldScene test4 = new WorldScene(100,100);
    test4.placeImageXY(new RectangleImage(30, 50, "solid", Color.blue), 80, 30);
    
    return t.checkExpect(test, test2)
        && t.checkExpect(test3, test4);
  }
  
  //tests lastScene
  boolean testLastScene(Tester t) {
    init();
    testGame3.makeScene();
    testGame3.gameWorld.placeImageXY(new TextImage("You won :)", 60, Color.blue), 250, 350);
    testGame.makeScene();
    testGame.lastScene("You won :)");
    
    testGame2.makeScene();
    testGame2.gameWorld.placeImageXY(new TextImage("You lost!", 60, Color.blue), 250, 350);
    newGame.makeScene();
    newGame.lastScene("You lost!");
    
    return t.checkExpect(testGame.gameWorld, testGame3.gameWorld)
       && t.checkExpect(testGame2.gameWorld, newGame.gameWorld);
  }
  
  //testing onTick
  boolean testOnTick(Tester t) {
    init();
    testGame.gameDeck.flip(30, 80);
    for (int i = 0; i < 25; i++) {
      testGame.onTick();
    }
    testGame3.clock += 25;
    testGame3.gameDeck.flip(30,80);
    testGame3.gameDeck.faceDown();
    
    return t.checkExpect(testGame.clock, testGame3.clock)
        && t.checkExpect(testGame3.gameDeck, testGame.gameDeck);
  }
  
  
  //testing onMouseRelased
  boolean testOnMouseReleased(Tester t) {
    init();
    testGame.onMouseReleased(new Posn(30,80));
    testGame3.gameDeck.flip(30, 80);
    testGame3.clicks += 1;
    
    testGame2.onMouseReleased(new Posn(70,80));
    newGame.gameDeck.flip(70, 80);
    newGame.clicks += 1;
    
    return t.checkExpect(testGame.gameDeck.deck.get(0).faceUp, true)
        && t.checkExpect(testGame.gameDeck, testGame3.gameDeck)
        && t.checkExpect(testGame3.clicks, testGame.clicks)
        && t.checkExpect(testGame2.gameDeck.deck.get(1).faceUp, true)
        && t.checkExpect(testGame2.gameDeck, newGame.gameDeck)
        && t.checkExpect(testGame3.clicks, testGame.clicks);
  }
  
  //testing onKeyEvent (deck reset)
  boolean testOnKeyReset(Tester t) {
    init();
    testGame.clock = 25;
    testGame2.clock = 25;
    testGame.clicks = 5;
    testGame2.clicks = 5;
    testGame.onKeyEvent("s");
    newGame.onKeyEvent("m");
    testGame2.onKeyEvent("r");
    
    return t.checkExpect(testGame.gameDeck.deck.get(0), testGame3.gameDeck.deck.get(0))
        && t.checkExpect(testGame.gameDeck.deck.size(), testGame3.gameDeck.deck.size())
        && t.checkExpect(testGame2.gameDeck.deck.size(), newGame.gameDeck.deck.size())
        && t.checkExpect(testGame2.clock, 0)
        && t.checkExpect(testGame.clock, 25)
        && t.checkExpect(testGame2.clicks, 0)
        && t.checkExpect(testGame.clicks, 5);
  }
  
  //testing onKeyEvent for toggling game modes
  boolean testOnKeyMode(Tester t) {
    init();
    testGame.onKeyEvent("2");
    testGame.onKeyEvent("1");
    testGame2.onKeyEvent("2");
    return  t.checkExpect(testGame.gameDeck.hardMode, false)
        && t.checkExpect(testGame2.gameDeck.hardMode, true)
        && t.checkExpect(newGame.gameDeck.hardMode, false);
  }
  
  //testing bigBang, gameplay
  void testBigBang(Tester t) {
    init();
    Concentration testWorld = new Concentration(new Random(12));
    testWorld.bigBang(600, 400, .10);
  }
}