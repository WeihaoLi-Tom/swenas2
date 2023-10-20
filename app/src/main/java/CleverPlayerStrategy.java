import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import ch.aplu.jcardgame.Hand;

public class CleverPlayerStrategy implements IPlayerStrategy, IObserver {
    private static final int DEPTH_LIMIT = 5;
    private HashMap<Player, ArrayList<Card>> cardsMemory = new HashMap<>();

    CleverPlayerStrategy() {
        CountingUpGame.Instance().addObserver(this);
    }
    private Rank getCurrentHighestRank(HashMap<Player, ArrayList<Card>> cardsMemory, Player cleverPlayer) {

        int[] rankCounts = new int[Rank.values().length];

        // First, count all cards in the memory
        for (Player player : cardsMemory.keySet()) {
            ArrayList<Card> cards = cardsMemory.get(player);
            for (Card card : cards) {
                if (card != null) {
                    rankCounts[((Rank) card.getRank()).ordinal()]++;
                }
            }
        }

        // Then, add the cards from the clever player's hand to the count
        if (cleverPlayer != null && cleverPlayer.getPlayerType() == Player.PlayerType.CLEVER) {
            List<Card> cleverCards = cleverPlayer.hand.getCardList();
            for (Card card : cleverCards) {
                rankCounts[((Rank) card.getRank()).ordinal()]++;
            }
        }

        Rank highestRank = null;
        int highestValue = Integer.MIN_VALUE;
        for (Rank rank : Rank.values()) {
            int rankValue = rank.getRankCardValue();
            if (rankCounts[rank.ordinal()] < 4 && rankValue > highestValue) {
                highestValue = rankValue;
                highestRank = rank;
            }
        }
        System.out.println(highestRank);
        return highestRank;
    }




    private Rank getHighestRankInHand(List<Card> hand) {
        int highestValue = Integer.MIN_VALUE;
        Rank highestRank = null;
        for (Card card : hand) {
            Rank rank = (Rank) card.getRank();
            int rankValue = rank.getRankCardValue();
            if (rankValue > highestValue) {
                highestValue = rankValue;
                highestRank = rank;
            }
        }
        return highestRank;
    }


    private boolean isOnlyPlayerWithHighestRankCard(Card card, HashMap<Player, ArrayList<Card>> cardsMemory) {
//        System.out.println("Checking if " + card + " is the only card of its rank played by any player.");

        for (Player player : cardsMemory.keySet()) {
//            System.out.println("Player: " + player + " has played: " + cardsMemory.get(player));
            for (Card otherCard : cardsMemory.get(player)) {
                if (otherCard != null && otherCard.getRank().equals(card.getRank())) {
//                    System.out.println("Player: " + player + " has already played a card of rank: " + otherCard.getRank());
                    return false;
                }
            }
        }
        return true;
    }



    @Override
    public Card PickCardToPlay(Player p) {
        List<Card> hand = new ArrayList<>(p.hand.getCardList());
        Rank highestRankInHand = getHighestRankInHand(hand);
        Rank currentHighestRankOnField = getCurrentHighestRank(cardsMemory,p);

        double bestScore = Double.NEGATIVE_INFINITY;
        Card bestCard = null;

        ArrayList<Card> mergedList = new ArrayList<>();
        for (List<Card> l : cardsMemory.values()) mergedList.addAll(l);
        Iterator<Card> iterator = mergedList.iterator();
        while (iterator.hasNext()) {
            Card card = iterator.next();
            if (card == null) {
                iterator.remove();
            }
        }

        for (Card card : hand) {
            if (!ValidationFacade.getInstance().isValidCardToPlay(card)) {
                continue;
            }

            if (card.getRank().equals(highestRankInHand) && highestRankInHand.getRankCardValue() <= currentHighestRankOnField.getRankCardValue()) {
                continue;
            }

            List<Card> simulatedHand = new ArrayList<>(hand);
            simulatedHand.remove(card);
            List<Card> simulatedPlayedCards = new ArrayList<>(mergedList);
            simulatedPlayedCards.add(card);
            double currentScore;
            if (card.getRank().equals(getCurrentHighestRank(cardsMemory,p)) && isOnlyPlayerWithHighestRankCard(card, cardsMemory)) {
                currentScore = minimax(DEPTH_LIMIT, false, simulatedHand, simulatedPlayedCards, mergedList, cardsMemory,p) + 100;
            } else {
                currentScore = minimax(DEPTH_LIMIT, false, simulatedHand, simulatedPlayedCards, mergedList, cardsMemory,p);
            }
            if (currentScore > bestScore) {
                bestScore = currentScore;
                bestCard = card;
            }
        }

        return bestCard;
    }

    private double minimax(int depth, boolean isMaximizingPlayer, List<Card> hand, List<Card> playedCards, List<Card> mergedList, HashMap<Player, ArrayList<Card>> cardsMemory, Player p) {
        if (depth == 0) {
            return evaluateHandScore(hand);
        }

        Rank currentHighestRank = getCurrentHighestRank(cardsMemory,p);

        if (isMaximizingPlayer) {
            double maxEval = Double.NEGATIVE_INFINITY;
            for (Card card : hand) {
                if (ValidationFacade.getInstance().isValidCardToPlay(card)) {
                    List<Card> newHand = new ArrayList<>(hand);
                    newHand.remove(card);
                    List<Card> newPlayedCards = new ArrayList<>(playedCards);
                    newPlayedCards.add(card);
                    double eval;
                    if (card.getRank().equals(currentHighestRank) && isOnlyPlayerWithHighestRankCard(card, cardsMemory)) {
                        eval = minimax(depth - 1, false, newHand, newPlayedCards, mergedList, cardsMemory,p) + 0;
                    } else {
                        eval = minimax(depth - 1, false, newHand, newPlayedCards, mergedList, cardsMemory,p);
                    }
                    maxEval = Math.max(maxEval, eval);
                }
            }

            if (maxEval == Double.NEGATIVE_INFINITY) {
                return -1000;
            }

            return maxEval;

        } else {
            double minEval = Double.POSITIVE_INFINITY;
            for (Card card : hand) {
                List<Card> newHand = new ArrayList<>(hand);
                newHand.remove(card);
                List<Card> newPlayedCards = new ArrayList<>(playedCards);
                newPlayedCards.add(card);
                double eval = minimax(depth - 1, true, newHand, newPlayedCards, mergedList, cardsMemory,p);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }

    private double evaluateHandScore(List<Card> hand) {
        double score = 0;
        for (Card card : hand) {
            score += evaluateCardScore(card, hand);
        }
        return score;
    }

    private double evaluateCardScore(Card card, List<Card> hand) {
        double score = 0;
        if (((Rank) card.getRank()).getRankCardValue() >= 10 || ((Rank) card.getRank()).getRankCardValue() == 1) {
            score += 10;
        } else {
            score += ((Rank) card.getRank()).getRankCardValue();
        }

        ArrayList<Card> mergedList = new ArrayList<>();
        for (List<Card> l : cardsMemory.values()) mergedList.addAll(l);

        Iterator<Card> iterator = mergedList.iterator();
        while (iterator.hasNext()) {
            Card cardx = iterator.next();
            if (cardx == null) {
                iterator.remove();
            }
        }

        for (Card cd : mergedList) {
            if (cd.getSuit() == card.getSuit()) {
                score += 0.5;
            }
        }
        return score;
    }

    @Override
    public void response(IObserverable subject) {
        if (cardsMemory.containsKey(CountingUpGame.Instance().getNextPlayer())) {
            cardsMemory.get(CountingUpGame.Instance().getNextPlayer()).add(CountingUpGame.Instance().getSelectedCard());
        } else {
            cardsMemory.put(CountingUpGame.Instance().getNextPlayer(), new ArrayList<Card>());
            cardsMemory.get(CountingUpGame.Instance().getNextPlayer()).add(CountingUpGame.Instance().getSelectedCard());
        }
    }
}

