import ch.aplu.jcardgame.Card;

public enum Rank {
    ACE(1, 10), KING(13, 10), QUEEN(12, 10),
    JACK(11, 10), TEN(10, 10), NINE(9, 9),
    EIGHT(8, 8), SEVEN(7, 7), SIX(6, 6),
    FIVE(5, 5), FOUR(4, 4), THREE(3, 3),
    TWO(2, 2);

    private int rankCardValue = 1;
    private int scoreCardValue = 1;

    Rank(int rankCardValue, int scoreCardValue) {
        this.rankCardValue = rankCardValue;
        this.scoreCardValue = scoreCardValue;
    }

//    public static boolean isRankGreater(Card card1, Card card2) {
//        return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
//    }

    public static boolean isRankGreater(Card card1, Card card2) {
        return ((Rank) card1.getRank()).getRankCardValue() > ((Rank) card2.getRank()).getRankCardValue(); // Warning: Reverse rank order of cards (see comment on enum)
    }

    public int getRankCardValue() {
        return rankCardValue;
    }

    public int getScoreCardValue() {
        return scoreCardValue;
    }

    public String getRankCardLog() {
        return String.format("%d", rankCardValue);
    }

}
