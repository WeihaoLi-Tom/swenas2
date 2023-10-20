import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BasicPlayerStrategy implements IPlayerStrategy {

    @Override
    public Card PickCardToPlay(Player p) {
        Card sameSuitGreaterRank = null;
        List<Card> difSuitEqualRank = new ArrayList<>();
        Card selectedCard = null;
        Card lastPlayerCard = CountingUpGame.Instance().getLastPlayedCard();

        ArrayList<Card> tempList = new ArrayList<>(p.hand.getCardList());

        for (Card card : tempList) {
            if (card.getSuit() == lastPlayerCard.getSuit()) {
                if (Rank.isRankGreater(card, lastPlayerCard) && (sameSuitGreaterRank == null || Rank.isRankGreater(sameSuitGreaterRank, card))) {
                    sameSuitGreaterRank = card;
                }
            } else {
                if (card.getRank() == lastPlayerCard.getRank()) {
                    difSuitEqualRank.add(card);
                }
            }
        }

        if (!difSuitEqualRank.isEmpty()) {
            //randomly pick a card from the list
            Random random = new Random();
            int x = random.nextInt(difSuitEqualRank.size());
            selectedCard = difSuitEqualRank.get(x);

        } else if (sameSuitGreaterRank != null) {
            selectedCard = sameSuitGreaterRank;
//            return selectedCard;
        } else if (ValidationFacade.getInstance().isValidCardToPlay(selectedCard)) {
            return null;
        }

        return selectedCard;
    }
}
