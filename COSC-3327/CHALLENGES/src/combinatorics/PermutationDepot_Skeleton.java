package combinatorics;

import java.util.*;

import model.Card;
import model.Deck;
import model.DeckImpl_Skeleton;
import model.Face;

/**
 * DO NOT INSTANTIATE
 */
public class PermutationDepot_Skeleton {

    private PermutationDepot_Skeleton() {
        throw new RuntimeException("DO NOT INSTANTIATE!");
    }

    // ====================================================
    //  getRotationsOfTheCube()
    //  RETURNS A SET OF 24 PERMUTATIONS OF Faces
    //  As shown on the chalkboard
    // ====================================================
    public static Set<Permutation<Face>> getRotationsOfTheCube()
    {
        Set<Permutation<Face>> rotations = new HashSet<>();

        // faces in fixed order (domain)
        List<Face> domain = Arrays.asList(
                Face.TOP, Face.BOTTOM, Face.LEFT,
                Face.RIGHT, Face.FRONT, Face.BACK
        );

        // -------------------------
        // Identity rotation
        // -------------------------
        rotations.add(new PermutationImpl_Kart<>(
                domain,
                domain // identity image
        ));

        // ----------------------------------------------------
        //  FACE–TO–FACE  90°, 180°, 270°
        //  Using cycles your professor wrote: (TOP, LEFT, BOTTOM, RIGHT)
        // ----------------------------------------------------
        addCycle(rotations, domain,
                Arrays.asList(Face.TOP, Face.LEFT, Face.BOTTOM, Face.RIGHT));

        // ----------------------------------------------------
        //  EDGE–TO–EDGE ROTATIONS (your board showed these)
        //  (FRONT, RIGHT, BACK, LEFT)
        // ----------------------------------------------------
        addCycle(rotations, domain,
                Arrays.asList(Face.FRONT, Face.RIGHT, Face.BACK, Face.LEFT));

        // ----------------------------------------------------
        //  VERTEX–TO–VERTEX (120° rotations)
        //  (TOP, FRONT, BOTTOM), (RIGHT, BACK, LEFT)
        // ----------------------------------------------------
        add3Cycle(rotations, domain,
                Arrays.asList(Face.TOP, Face.FRONT, Face.BOTTOM));

        add3Cycle(rotations, domain,
                Arrays.asList(Face.RIGHT, Face.BACK, Face.LEFT));

        // Must have exactly 24
        assert rotations.size() == 24 : "Cube rotations must be 24!";

        return rotations;
    }

    // ====================================================
    // Helper: adds 90°, 180°, 270° cycle-permutations
    // ====================================================
    private static void addCycle(Set<Permutation<Face>> set,
                                 List<Face> domain,
                                 List<Face> cycle)
    {
        // 90° rotation
        set.add(new PermutationImpl_Kart<>(domain, applyCycle(domain, cycle, 1)));
        // 180°
        set.add(new PermutationImpl_Kart<>(domain, applyCycle(domain, cycle, 2)));
        // 270°
        set.add(new PermutationImpl_Kart<>(domain, applyCycle(domain, cycle, 3)));
    }

    // ====================================================
    // Helper: adds 120° and 240° (3-cycles)
    // ====================================================
    private static void add3Cycle(Set<Permutation<Face>> set,
                                  List<Face> domain,
                                  List<Face> cycle)
    {
        set.add(new PermutationImpl_Kart<>(domain, applyCycle(domain, cycle, 1)));
        set.add(new PermutationImpl_Kart<>(domain, applyCycle(domain, cycle, 2)));
    }

    // ====================================================
    // Applies k shifts of a cycle list
    // ====================================================
    private static List<Face> applyCycle(List<Face> domain,
                                         List<Face> cycleFaces,
                                         int shift)
    {
        Map<Face,Face> map = new HashMap<>();

        // identity to start
        for (Face f : domain)
            map.put(f, f);

        int n = cycleFaces.size();
        for (int i = 0; i < n; i++)
        {
            Face from = cycleFaces.get(i);
            Face to   = cycleFaces.get((i + shift) % n);
            map.put(from, to);
        }

        List<Face> image = new ArrayList<>();
        for (Face f : domain)
            image.add(map.get(f));

        return image;
    }

    // ====================================================
    // getS1ToS2Permutation_Anagrams(s1, s2)
    // EXACTLY like chalkboard
    // stable mapping for duplicates
    // ====================================================
    public static Permutation<Integer> getS1ToS2Permutation_Anagrams(
            String s1, String s2)
    {
        assert s1 != null;
        assert s2 != null;
        assert s1.length() == s2.length() : "length";

        int n = s1.length();

        // Build queues of positions in s2 for each char
        Map<Character,Deque<Integer>> pos = new HashMap<>();
        for (int j = 0; j < n; j++) {
            char c = s2.charAt(j);
            pos.computeIfAbsent(c, k -> new ArrayDeque<>()).addLast(j);
        }

        // Build permutation
        List<Integer> domain = new ArrayList<>();
        List<Integer> image  = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            char c = s1.charAt(i);
            domain.add(i);
            image.add(pos.get(c).removeFirst());
        }

        return new PermutationImpl_Kart<>(domain, image);
    }

    // ====================================================
    // getDeck(ordering)
    // EXACT as shown in class
    // ====================================================
    public static Deck getDeck(List<Card> ordering) {
        Deck deck = new DeckImpl_Skeleton(ordering);
        return deck;
    }
}
