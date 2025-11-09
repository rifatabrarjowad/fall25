package model;

import combinatorics.PermutationImpl_Jowad;

import static model.Rank.*;
import static model.Suit.*;

import java.util.*;


public class DeckImpl_Jowad extends DeckAbstract {

    // ---- ctor ----
    public DeckImpl_Jowad(List<Card> cardOrdering) {
        Objects.requireNonNull(cardOrdering, "cardOrdering");
        if (cardOrdering.size() != 52) {
            throw new IllegalArgumentException("cardOrdering must contain 52 cards");
        }
        if (!noDuplicates(cardOrdering)) {
            throw new IllegalArgumentException("cardOrdering contains duplicates");
        }
        List<Card> standard = standardOrdering();
        int[] image = new int[52];
        Arrays.fill(image, -1);
        Map<Card, Integer> stdIndex = new HashMap<>(64);
        for (int i = 0; i < 52; i++) stdIndex.put(standard.get(i), i);

        for (int pos = 0; pos < 52; pos++) {
            Card c = cardOrdering.get(pos);
            Integer i = stdIndex.get(c);
            if (i == null) {
                throw new IllegalArgumentException("cardOrdering contains a non-standard card: " + c);
            }
            image[i] = pos;
        }
        int topStdIdx = -1;
        for (int i = 0; i < 52; i++) {
            if (image[i] == 0) { topStdIdx = i; break; }
        }
        if (topStdIdx < 0) throw new IllegalStateException("could not locate top card");
        this.orderingOfStandardIndices = new PermutationImpl_Jowad<>(toCycles(image));
        this.indexOfTopCardInStandardOrdering = topStdIdx;
        this.cardsLeft = 52;
    }

    // ---- Deck API ----
    @Override
    public int size() {
        return cardsLeft;
    }

    @Override
    public Card top() {
        if (cardsLeft == 0) return null;
        return standardOrdering().get(indexOfTopCardInStandardOrdering);
    }

    @Override
    public void removeTop() {
        if (cardsLeft == 0) return;
        int drawnSoFar = 52 - cardsLeft + 1; // we are moving top from pos k to k+1
        cardsLeft--;

        if (cardsLeft == 0) return; // empty now

        // Find the standard index whose image is the next position (drawnSoFar)
        int nextStd = -1;
        for (int i = 0; i < 52; i++) {
            Integer img = orderingOfStandardIndices.getImage(i);
            if (img != null && img == drawnSoFar) { nextStd = i; break; }
        }
        if (nextStd >= 0) {
            indexOfTopCardInStandardOrdering = nextStd;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DeckImpl_Jowad)) return false;
        DeckImpl_Jowad other = (DeckImpl_Jowad) obj;
        return this.cardsLeft == other.cardsLeft
                && this.indexOfTopCardInStandardOrdering == other.indexOfTopCardInStandardOrdering
                && Objects.equals(this.orderingOfStandardIndices, other.orderingOfStandardIndices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardsLeft, indexOfTopCardInStandardOrdering, orderingOfStandardIndices);
    }

    // ---- helpers (no new fields added) ----
    private static boolean noDuplicates(List<Card> cards) {
        return new HashSet<>(cards).size() == cards.size();
    }

    private static Set<List<Integer>> toCycles(int[] image) {
        boolean[] seen = new boolean[image.length];
        Set<List<Integer>> cycles = new HashSet<>();
        for (int i = 0; i < image.length; i++) {
            if (seen[i]) continue;
            List<Integer> cyc = new ArrayList<>();
            int cur = i;
            do {
                seen[cur] = true;
                cyc.add(cur);
                cur = image[cur];
            } while (!seen[cur]);
            cycles.add(cyc);
        }
        return cycles;
    }

    private static List<Card> standardOrdering() {
        List<Card> list = new ArrayList<>(52);
        Rank[] ranks = { ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING };
        Suit[] suits = { HEARTS, CLUBS, DIAMONDS, SPADES }; // matches screenshots
        for (Suit s : suits) for (Rank r : ranks) list.add(new CardImpl_Jowad(r, s));
        return list;
    }
}
