// CountingUpGame.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import org.checkerframework.checker.units.qual.C;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class CountingUpGame extends CardGame implements GGKeyListener {



    final String trumpImage[] = {"bigspade.gif", "bigheart.gif", "bigdiamond.gif", "bigclub.gif"};

    static public final int seed = 30008;
    static final Random random = new Random(seed);
    private Properties properties;
    private StringBuilder logResult = new StringBuilder();
    private List<List<String>> playerAutoMovements = new ArrayList<>();

    public boolean rankGreater(Card card1, Card card2) {
        return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
    }
// new in -----------------------------------------------------------------------------------------------------------------------
    public CardDealer dealer = new CardDealer(properties);
    public Logger logger = new Logger();

    public Score score = new Score();
    // new in -----------------------------------------------------------------------------------------------------------------------
    private final String version = "1.0";
    public final int nbPlayers = 4;
    public final int nbStartCards = 13;
    public final int nbRounds = 3;
    private final int handWidth = 400;
    private final int trickWidth = 40;
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };
    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(575, 25),
            // new Location(650, 575)
            new Location(575, 575)
    };
    private Actor[] scoreActors = {null, null, null, null};
    private final Location trickLocation = new Location(350, 350);
    private final Location textLocation = new Location(350, 450);
    private int thinkingTime = 2000;
    private int delayTime = 600;
    private Hand[] hands;
    private Location hideLocation = new Location(-500, -500);

    public void setStatus(String string) {
        setStatusText(string);
    }

    private int[] scores = new int[nbPlayers];

    private boolean isWaitingForPass = false;
    private boolean passSelected = false;
    private int[] autoIndexHands = new int [nbPlayers];
    private boolean isAuto = false;

    Font bigFont = new Font("Arial", Font.BOLD, 36);

    private void initScore() {
        for (int i = 0; i < nbPlayers; i++) {
            // scores[i] = 0;
            String text = "[" + String.valueOf(scores[i]) + "]";
            scoreActors[i] = new TextActor(text, Color.WHITE, bgColor, bigFont);
            addActor(scoreActors[i], scoreLocations[i]);
        }
    }



    private void updateScore(int player) {
        removeActor(scoreActors[player]);
        int displayScore = scores[player] >= 0 ? scores[player] : 0;
        String text = "P" + player + "[" + String.valueOf(displayScore) + "]";
        scoreActors[player] = new TextActor(text, Color.WHITE, bgColor, bigFont);
        addActor(scoreActors[player], scoreLocations[player]);
    }

    private void initScores() {
        for (int i = 0; i < nbPlayers; i++) {
            scores[i] = 0;
        }
    }


    private Card selected;

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (isWaitingForPass && keyEvent.getKeyChar() == '\n') {
            passSelected = true;
        }
        return false;
    }

    @Override
    public boolean keyReleased(KeyEvent keyEvent) {
        return false;
    }

    private void initGame() {
        hands = new Hand[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            hands[i] = new Hand(deck);
        }
        dealer.dealingOut(hands, nbPlayers, nbStartCards);
        for (int i = 0; i < nbPlayers; i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, false);
        }
        // Set up human player for interaction
        CardListener cardListener = new CardAdapter()  // Human Player plays card
        {
            public void leftDoubleClicked(Card card) {
                selected = card;
                hands[0].setTouchEnabled(false);
            }
        };
        hands[0].addCardListener(cardListener);
        // graphics
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(90 * i);
            // layouts[i].setStepDelay(10);
            hands[i].setView(this, layouts[i]);
            hands[i].setTargetArea(new TargetArea(trickLocation));
            hands[i].draw();
        }
    }


    private int playerIndexWithAceClub() {
        for (int i = 0; i < nbPlayers; i++) {
            Hand hand = hands[i];
            List<Card> cards = hand.getCardsWithRank(Rank.ACE);
            if (cards.size() == 0) {
                continue;
            }
            for (Card card: cards) {
                if (card.getSuit() == Suit.CLUBS) {
                    return i;
                }
            }
        }

        return 0;
    }

    private void playGame() {
        // End trump suit
        Hand playingArea = null;
        int winner = 0;
        int roundNumber = 1;
        for (int i = 0; i < nbPlayers; i++) updateScore(i);
        boolean isContinue = true;
        int skipCount = 0;
        List<Card>cardsPlayed = new ArrayList<>();
        playingArea = new Hand(deck);
        logger.addRoundInfoToLog(roundNumber);

        int nextPlayer = playerIndexWithAceClub();
        while(isContinue) {
            selected = null;
            boolean finishedAuto = false;
            if (isAuto) {
                int nextPlayerAutoIndex = autoIndexHands[nextPlayer];
                List<String> nextPlayerMovement = playerAutoMovements.get(nextPlayer);
                String nextMovement = "";

                if (nextPlayerMovement.size() > nextPlayerAutoIndex) {
                    nextMovement = nextPlayerMovement.get(nextPlayerAutoIndex);
                    nextPlayerAutoIndex++;

                    autoIndexHands[nextPlayer] = nextPlayerAutoIndex;
                    Hand nextHand = hands[nextPlayer];

                    if (nextMovement.equals("SKIP")) {
                        setStatusText("Player " + nextPlayer + " skipping...");
                        delay(thinkingTime);
                        selected = null;
                    } else {
                        setStatusText("Player " + nextPlayer + " thinking...");
                        delay(thinkingTime);
                        selected = dealer.getCardFromList(nextHand.getCardList(), nextMovement);
                    }
                } else {
                    finishedAuto = true;
                }
            }

            if (!isAuto || finishedAuto){
                if (0 == nextPlayer) {
                    hands[0].setTouchEnabled(true);
                    isWaitingForPass = true;
                    passSelected = false;
                    setStatus("Player 0 double-click on card to follow or press Enter to pass");
                    while (null == selected && !passSelected) delay(delayTime);
                    isWaitingForPass = false;
                } else {
                    setStatusText("Player " + nextPlayer + " thinking...");
                    delay(thinkingTime);
                    selected = dealer.getRandomCardOrSkip(hands[nextPlayer].getCardList());
                    if (selected == null) {
                        setStatusText("Player " + nextPlayer + " skipping...");
                        delay(thinkingTime);
                    }
                }
            }

            // Follow with selected card

            playingArea.setView(this, new RowLayout(trickLocation, (playingArea.getNumberOfCards() + 2) * trickWidth));
            playingArea.draw();
            logger.addCardPlayedToLog(nextPlayer, selected);
            if (selected != null) {
                skipCount = 0;
                cardsPlayed.add(selected);
                selected.setVerso(false);  // In case it is upside down
                // Check: Following card must follow suit if possible

                // End Check
                selected.transfer(playingArea, true); // transfer to trick (includes graphic effect)
                delay(delayTime);
                // End Follow
            } else {
                skipCount++;
            }

            if (skipCount == nbPlayers - 1) {
                playingArea.setView(this, new RowLayout(hideLocation, 0));
                playingArea.draw();
                winner = (nextPlayer + 1) % nbPlayers;
                skipCount = 0;
                score.calculateScoreEndOfRound(winner, cardsPlayed);
                updateScore(winner);
                logger.addEndOfRoundToLog();
                roundNumber++;
                logger.addRoundInfoToLog(roundNumber);
                cardsPlayed = new ArrayList<>();
                delay(delayTime);
                playingArea = new Hand(deck);
            }

            isContinue = hands[0].getNumberOfCards() > 0 && hands[1].getNumberOfCards() > 0 &&
                    hands[2].getNumberOfCards() > 0 && hands[3].getNumberOfCards() > 0;
            if (!isContinue) {
                winner = nextPlayer;
                score.calculateScoreEndOfRound(winner, cardsPlayed);
                logger.addEndOfRoundToLog();
            } else {
                nextPlayer = (nextPlayer + 1) % nbPlayers;
            }
            delay(delayTime);
        }

        for (int i = 0; i < nbPlayers; i++) {
            score.calculateNegativeScoreEndOfGame(i, hands[i].getCardList());
            updateScore(i);
        }
    }

    private void setupPlayerAutoMovements() {
        String player0AutoMovement = properties.getProperty("players.0.cardsPlayed");
        String player1AutoMovement = properties.getProperty("players.1.cardsPlayed");
        String player2AutoMovement = properties.getProperty("players.2.cardsPlayed");
        String player3AutoMovement = properties.getProperty("players.3.cardsPlayed");

        String[] playerMovements = new String[] {"", "", "", ""};
        if (player0AutoMovement != null) {
            playerMovements[0] = player0AutoMovement;
        }

        if (player1AutoMovement != null) {
            playerMovements[1] = player1AutoMovement;
        }

        if (player2AutoMovement != null) {
            playerMovements[2] = player2AutoMovement;
        }

        if (player3AutoMovement != null) {
            playerMovements[3] = player3AutoMovement;
        }

        for (int i = 0; i < playerMovements.length; i++) {
            String movementString = playerMovements[i];
            List<String> movements = Arrays.asList(movementString.split(","));
            playerAutoMovements.add(movements);
        }
    }

    public String runApp() {
        setTitle("CountingUpGame (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        initScores();
        initScore();
        addKeyListener(this);
        setupPlayerAutoMovements();
        initGame();
        playGame();

        for (int i = 0; i < nbPlayers; i++) updateScore(i);
        int maxScore = 0;
        for (int i = 0; i < nbPlayers; i++) if (scores[i] > maxScore) maxScore = scores[i];
        List<Integer> winners = new ArrayList<Integer>();
        for (int i = 0; i < nbPlayers; i++) if (scores[i] == maxScore) winners.add(i);
        String winText;
        if (winners.size() == 1) {
            winText = "Game over. Winner is player: " +
                    winners.iterator().next();
        } else {
            winText = "Game Over. Drawn winners are players: " +
                    String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList()));
        }
        addActor(new Actor("sprites/gameover.gif"), textLocation);
        setStatusText(winText);
        refresh();
        logger.addEndOfGameToLog(winners);

        return logResult.toString();
    }

    public CountingUpGame(Properties properties) {
        super(700, 700, 30);
        this.properties = properties;
        this.dealer = new CardDealer(properties);
        this.logger = new Logger();
        this.score  = new Score();
        isAuto = Boolean.parseBoolean(properties.getProperty("isAuto"));
        thinkingTime = Integer.parseInt(properties.getProperty("thinkingTime", "200"));
        delayTime = Integer.parseInt(properties.getProperty("delayTime", "100"));
    }
}

