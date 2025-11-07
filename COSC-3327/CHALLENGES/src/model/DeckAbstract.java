package model;

import static model.Rank.ACE;
import static model.Rank.EIGHT;
import static model.Rank.FIVE;
import static model.Rank.FOUR;
import static model.Rank.JACK;
import static model.Rank.KING;
import static model.Rank.NINE;
import static model.Rank.QUEEN;
import static model.Rank.SEVEN;
import static model.Rank.SIX;
import static model.Rank.TEN;
import static model.Rank.THREE;
import static model.Rank.TWO;
import static model.Suit.CLUBS;
import static model.Suit.DIAMONDS;
import static model.Suit.HEARTS;
import static model.Suit.SPADES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import combinatorics.Permutation;
import combinatorics.PermutationImpl_Kart;

public abstract class DeckAbstract implements Deck
{
	//Note that:
	//Deck d = new DeckImpl(standardOrdering)
	//results in an orderingOfStandardIndices of:
	//cycles =
	//{(0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,
	// 13,14,15,16,17,18,19,20,21,22,23,24,25,
	// 26,27,28,29,30,31,32,33,34,35,36,37,38,
	// 39,40,41,42,43,44,45,46,47,48,49,50,51)}
	//and indexOfTopCardInStandardOrdering = 0
	protected Permutation<Integer> orderingOfStandardIndices;
	protected int indexOfTopCardInStandardOrdering;
	protected int cardsLeft;
}
