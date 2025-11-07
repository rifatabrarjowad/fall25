package model;

import java.util.*;
import combinatorics.Permutation;
import combinatorics.PermutationImpl_Kart;

/**
 * Concrete deck built from a top-to-bottom list of 52 distinct cards.
 * Uses DeckAbstract’s fields:
 *   - orderingOfStandardIndices : Permutation<Integer>
 *   - indexOfTopCardInStandardOrdering : int
 *   - cardsLeft : int
 *
 * Do NOT add any instance variables to this class.
 */
public class DeckImpl_Skeleton extends DeckAbstract {

    // ----- Standard 52-card ordering used as reference (H, C, D, S; A..K) -----
    // Your professor’s slide shows standardOrderingCards.get(0) == A♥ and
    // standardOrderingCards.get(51) == K♠.
    private static final List<Card> STANDARD_DECK = buildStandardOrdering();

    private static List<Card> buildStandardOrdering() {
        List<Card> deck = new ArrayList<>(52);
        // Hearts, Clubs, Diamonds, Spades — each ACE..KING
        addSuit(deck, Suit.HEARTS);
        addSuit(deck, Suit.CLUBS);
        addSuit(deck, Suit.DIAMONDS);
        addSuit(deck, Suit.SPADES);
        return deck;
    }

    private static void addSuit(List<Card> deck, Suit s) {
        deck.add(new CardImpl(Rank.ACE,   s));
        deck.add(new CardImpl(Rank.TWO,   s));
        deck.add(new CardImpl(Rank.THREE, s));
        deck.add(new CardImpl(Rank.FOUR,  s));
        deck.add(new CardImpl(Rank.FIVE,  s));
        deck.add(new CardImpl(Rank.SIX,   s));
        deck.add(new CardImpl(Rank.SEVEN, s));
        deck.add(new CardImpl(Rank.EIGHT, s));
        deck.add(new CardImpl(Rank.NINE,  s));
        deck.add(new CardImpl(Rank.TEN,   s));
        deck.add(new CardImpl(Rank.JACK,  s));
        deck.add(new CardImpl(Rank.QUEEN, s));
        deck.add(new CardImpl(Rank.KING,  s));
    }

    // ------------------------- Constructor -------------------------
    // EX: cardOrdering = [KH, JC, ...] (top -> bottom)
    public DeckImpl_Skeleton(List<Card> cardOrdering) {
        // assertions like on your slide
        assert cardOrdering != null : "cardOrdering is null!";
        assert cardOrdering.size() == 52 : "!52";
        assert noDuplicates(cardOrdering) : "duplicates!";

        this.cardsLeft = 52;

        // Map each given card (top->bottom) to its index in the standard deck.
        // Domain = {0..51}; Image[i] = standardIndex(cardOrdering.get(i))
        List<Integer> domain = new ArrayList<>(52);
        List<Integer> image  = new ArrayList<>(52);

        // Build lookup from Card -> index in STANDARD_DECK
        Map<Card, Integer> stdIndex = new HashMap<>(64);
        for (int i = 0; i < STANDARD_DECK.size(); i++) {
            stdIndex.put(STANDARD_DECK.get(i), i);
        }

        for (int i = 0; i < 52; i++) {
            Card c = cardOrdering.get(i);
            Integer idx = stdIndex.get(c);
            if (idx == null) {
                throw new IllegalArgumentException("Card not in standard deck: " + c);
            }
            domain.add(i);
            image.add(idx);
        }

        this.orderingOfStandardIndices = new PermutationImpl_Kart<>(domain, image);

        // Top of the deck corresponds to domain index 0
        this.indexOfTopCardInStandardOrdering = 0;
    }

    // ---------------------- Deck interface ----------------------

    @Override
    public int size() {
        return this.cardsLeft;
    }

    @Override
    public Card top() {
        if (this.cardsLeft <= 0) throw new IllegalStateException("Deck is empty.");
        int stdIdx = imageOfStandardIndexAtOffset(0);
        return STANDARD_DECK.get(stdIdx);
    }

    @Override
    public void removeTop() {
        if (this.cardsLeft <= 0) throw new IllegalStateException("Deck is empty.");
        // advance the circular pointer in the domain space
        this.indexOfTopCardInStandardOrdering =
                (this.indexOfTopCardInStandardOrdering + 1) % 52;
        this.cardsLeft--;
    }

    /**
     * Value equality: same visible sequence from top to bottom.
     * (Only compares against the same class to avoid mutating foreign decks.)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DeckImpl_Skeleton)) return false;
        DeckImpl_Skeleton other = (DeckImpl_Skeleton) obj;
        if (this.cardsLeft != other.cardsLeft) return false;

        for (int k = 0; k < this.cardsLeft; k++) {
            int i1 = this.imageOfStandardIndexAtOffset(k);
            int i2 = other.imageOfStandardIndexAtOffset(k);
            if (!STANDARD_DECK.get(i1).equals(STANDARD_DECK.get(i2))) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 1;
        for (int k = 0; k < this.cardsLeft; k++) {
            int idx = imageOfStandardIndexAtOffset(k);
            h = 31 * h + STANDARD_DECK.get(idx).hashCode();
        }
        return h;
    }

    // -------------------------- helpers --------------------------

    private static boolean noDuplicates(List<Card> ordering) {
        return new HashSet<>(ordering).size() == ordering.size();
    }

    /**
     * From the current top, offset k -> index in the STANDARD_DECK
     * by applying the stored permutation to the domain position.
     */
    private int imageOfStandardIndexAtOffset(int k) {
        int domainIndex = (this.indexOfTopCardInStandardOrdering + k) % 52;
        return this.orderingOfStandardIndices.getImage(domainIndex);
    }
}
