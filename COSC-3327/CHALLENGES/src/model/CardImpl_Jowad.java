package model;

import java.util.Objects;

public class CardImpl_Jowad implements Card {
    private final Rank rank;
    private final Suit suit;

    public CardImpl_Jowad(Rank rank, Suit suit) {
        this.rank = Objects.requireNonNull(rank, "rank");
        this.suit  = Objects.requireNonNull(suit, "suit");
    }

    @Override
    public Rank getRank() {
        return rank;
    }

    @Override
    public Suit getSuit() {
        return suit;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Card)) return false;
        Card other = (Card) obj;
        return rank == other.getRank() && suit == other.getSuit();
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, suit);
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
