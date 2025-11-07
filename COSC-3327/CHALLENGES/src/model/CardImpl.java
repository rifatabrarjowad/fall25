package model;

import java.util.Objects;

public class CardImpl implements Card
{
    private Rank rank;
    private Suit suit;

    public CardImpl(Rank rank, Suit suit)
    {
        this.rank = rank;
        this.suit = suit;
    }

    @Override
    public Rank getRank()
    {
        return this.rank;
    }

    @Override
    public Suit getSuit()
    {
        return this.suit;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof Card)) return false;
        Card c = (Card) obj;
        return this.rank == c.getRank() && this.suit == c.getSuit();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(rank, suit);
    }

    @Override
    public String toString()
    {
        return rank + " of " + suit;
    }
}
