import ch.aplu.jcardgame.Card;
import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;
import ch.aplu.jgamegrid.TextActor;

import java.awt.*;
import java.util.List;

public class Score {
    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(575, 25),
            // new Location(650, 575)
            new Location(575, 575)
    };
    private final CountingUpGame game;
    public int nbPlayers = 4;
    public Actor[] scoreActors = {null, null, null, null};
    public int[] scores = new int[nbPlayers];
    Font bigFont = new Font("Arial", Font.BOLD, 36);


    public Score(CountingUpGame game) {
        this.game = game;
    }


    public void calculateScoreEndOfRound(int player, List<Card> cardsPlayed) {

        int totalScorePlayed = 0;
        for (Card card : cardsPlayed) {
            Rank rank = (Rank) card.getRank();
            totalScorePlayed += rank.getScoreCardValue();
        }
        scores[player] += totalScorePlayed;
        System.out.println("Player " + player + " New Score: " + scores[player]);
    }

    public void calculateNegativeScoreEndOfGame(int player, List<Card> cardsInHand) {
        System.out.println("Hi,i lost score");
        int totalScorePlayed = 0;
        for (Card card : cardsInHand) {
            Rank rank = (Rank) card.getRank();
            totalScorePlayed -= rank.getScoreCardValue();
        }
        scores[player] += totalScorePlayed;
    }


    public void initScores() {
        for (int i = 0; i < nbPlayers; i++) {
            scores[i] = 0;
        }
    }

    public void updateScores() {
        for (int i = 0; i < nbPlayers; i++) {
        }
    }

    public void initScore() {
        for (int i = 0; i < nbPlayers; i++) {
            // scores[i] = 0;
            String text = "[" + scores[i] + "]";
            scoreActors[i] = new TextActor(text, Color.WHITE, game.bgColor, bigFont);
            game.addActor(scoreActors[i], scoreLocations[i]);
        }
    }


    public void updateScore(int player) {
        game.removeActor(scoreActors[player]);
        int displayScore = scores[player] >= 0 ? scores[player] : 0;
        String text = "P" + player + "[" + displayScore + "]";
        scoreActors[player] = new TextActor(text, Color.WHITE, game.bgColor, bigFont);
        game.addActor(scoreActors[player], scoreLocations[player]);
//        System.out.println("Scores updated: " + Arrays.toString(scores));

    }


}







