public enum Suit {
    SPADES("S"), HEARTS("H"), DIAMONDS("D"), CLUBS("C");//黑红方梅
    private String suitShortHand = "";

    Suit(String shortHand) {
        this.suitShortHand = shortHand;
    }

    public String getSuitShortHand() {
        return suitShortHand;
    }
}