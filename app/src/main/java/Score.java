import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import org.checkerframework.checker.units.qual.C;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Score {
    private int[] scores;
    public int nbPlayers =4;


    public void calculateScoreEndOfRound(int player, List<Card> cardsPlayed) {
        int totalScorePlayed = 0;
        for (Card card: cardsPlayed) {
            Rank rank = (Rank) card.getRank();
            totalScorePlayed += rank.getScoreCardValue();
        }
        scores[player] += totalScorePlayed;
    }

    public void calculateNegativeScoreEndOfGame(int player, List<Card> cardsInHand) {
        int totalScorePlayed = 0;
        for (Card card: cardsInHand) {
            Rank rank = (Rank) card.getRank();
            totalScorePlayed -= rank.getScoreCardValue();
        }
        scores[player] += totalScorePlayed;
    }

    public int getScore(int player) {
        return scores[player];
    }

    public void setScore(int player, int score) {
        scores[player] = score;
    }

    public int[] getAllScores() {
        return scores;
    }

    private void initScores() {
        for (int i = 0; i < nbPlayers; i++) {
            scores[i] = 0;
        }
    }

    private void updateScores() {
        for (int i = 0; i < nbPlayers; i++) {
        }
    }
}







