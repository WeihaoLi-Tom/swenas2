import ch.aplu.jcardgame.Card;

import java.util.List;
import java.util.stream.Collectors;

public class Logger {
    public StringBuilder logResult = new StringBuilder();
    public Score score;


    public Logger(Score score) {
        this.score = score;
    }


    public void addCardPlayedToLog(int player, Card selectedCard) {
        if (selectedCard == null) {
            logResult.append("P" + player + "-SKIP,");
        } else {
            Rank cardRank = (Rank) selectedCard.getRank();
            Suit cardSuit = (Suit) selectedCard.getSuit();
            logResult.append("P" + player + "-" + cardRank.getRankCardLog() + cardSuit.getSuitShortHand() + ",");
        }
    }

    public void addRoundInfoToLog(int roundNumber) {
        logResult.append("Round" + roundNumber + ":");
    }

    public void addEndOfRoundToLog() {
        logResult.append("Score:");
        for (int i = 0; i < score.scores.length; i++) {
            logResult.append(score.scores[i] + ",");
        }
        logResult.append("\n");
//        System.out.println("addendrountolog");
    }

    public void addEndOfGameToLog(List<Integer> winners) {
        logResult.append("EndGame:");
        for (int i = 0; i < score.scores.length; i++) {
            logResult.append(score.scores[i] + ",");
        }
        logResult.append("\n");
        logResult.append("Winners:" + String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toList())));
    }


}
