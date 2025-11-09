package combinatorics;

import java.util.*;

import model.Card;
import model.Deck;
import model.DeckImpl_Jowad;
import model.Face;

public class PermutationDepot_Jowad {
    private PermutationDepot_Jowad() {
        throw new RuntimeException("DO NOT INSTANTIATE!");
    }

    public static java.util.Set<Permutation<Face>> getRotationsOfTheCube() {
        final java.util.List<Face> D = java.util.Arrays.asList(
                Face.TOP, Face.BOTTOM, Face.LEFT, Face.RIGHT, Face.FRONT, Face.BACK);

        java.util.Map<Face,Face> RX = mapOf(
                Face.TOP,   Face.FRONT,
                Face.FRONT, Face.BOTTOM,
                Face.BOTTOM,Face.BACK,
                Face.BACK,  Face.TOP,
                Face.LEFT,  Face.LEFT,
                Face.RIGHT, Face.RIGHT
        );

        java.util.Map<Face,Face> RY = mapOf(
                Face.FRONT, Face.RIGHT,
                Face.RIGHT, Face.BACK,
                Face.BACK,  Face.LEFT,
                Face.LEFT,  Face.FRONT,
                Face.TOP,   Face.TOP,
                Face.BOTTOM,Face.BOTTOM
        );

        java.util.Map<Face,Face> RZ = mapOf(
                Face.TOP,    Face.LEFT,
                Face.LEFT,   Face.BOTTOM,
                Face.BOTTOM, Face.RIGHT,
                Face.RIGHT,  Face.TOP,
                Face.FRONT,  Face.FRONT,
                Face.BACK,   Face.BACK
        );

        // Identity
        java.util.Map<Face,Face> ID = identityMap(D);

        // Closure under composition of RX, RY, RZ
        java.util.Set<java.util.Map<Face,Face>> raw = new java.util.HashSet<>();
        java.util.ArrayDeque<java.util.Map<Face,Face>> q = new java.util.ArrayDeque<>();
        raw.add(ID); q.add(ID);

        java.util.List<java.util.Map<Face,Face>> gens = java.util.Arrays.asList(RX, RY, RZ);

        while (!q.isEmpty()) {
            java.util.Map<Face,Face> p = q.removeFirst();
            for (java.util.Map<Face,Face> g : gens) {
                java.util.Map<Face,Face> pg = compose(p, g); // first p then g
                if (raw.add(pg)) q.add(pg);
            }
        }

        // Convert raw maps to Permutation<Face>
        java.util.Set<Permutation<Face>> result = new java.util.HashSet<>(24);
        for (java.util.Map<Face,Face> f : raw) {
            java.util.List<Face> image = new java.util.ArrayList<>(D.size());
            for (Face d : D) image.add(f.get(d));
            result.add(new PermutationImpl_Jowad<>(D, image));
        }

        // Should be 24
        if (result.size() != 24) {
            throw new IllegalStateException("Cube rotation group must have 24 elements; got " + result.size());
        }
        return result;
    }

    // ---- small helpers (also inside PermutationDepot_Skeleton) ----
    private static java.util.Map<Face,Face> identityMap(java.util.List<Face> D) {
        java.util.Map<Face,Face> m = new java.util.EnumMap<>(Face.class);
        for (Face f : D) m.put(f, f);
        return m;
    }

    @SafeVarargs
    private static java.util.Map<Face,Face> mapOf(Object... kv) {
        java.util.Map<Face,Face> m = new java.util.EnumMap<>(Face.class);
        for (int i = 0; i < kv.length; i += 2) {
            m.put((Face) kv[i], (Face) kv[i+1]);
        }
        return m;
    }

    /** r = q ∘ p (apply p, then q). */
    private static java.util.Map<Face,Face> compose(java.util.Map<Face,Face> p,
                                                    java.util.Map<Face,Face> q) {
        java.util.Map<Face,Face> r = new java.util.EnumMap<>(Face.class);
        for (java.util.Map.Entry<Face,Face> e : p.entrySet()) {
            r.put(e.getKey(), q.get(e.getValue()));
        }
        return r;
    }


    // s1 -> s2 index mapping that is *stable* for repeated chars
    public static Permutation<Integer> getS1ToS2Permutation_Anagrams(String s1, String s2) {
        Objects.requireNonNull(s1, "s1");
        Objects.requireNonNull(s2, "s2");
        if (s1.length() != s2.length())
            throw new IllegalArgumentException("Lengths must match (anagrams).");

        // Build queues of indices for each character in s2 (in increasing order)
        Map<Character, Deque<Integer>> positions = new HashMap<>();
        for (int j = 0; j < s2.length(); j++) {
            char ch = s2.charAt(j);
            positions.computeIfAbsent(ch, k -> new ArrayDeque<>()).addLast(j);
        }

        // image[i] = index in s2 to which i in s1 maps
        int n = s1.length();
        int[] image = new int[n];
        for (int i = 0; i < n; i++) {
            char ch = s1.charAt(i);
            Deque<Integer> q = positions.get(ch);
            if (q == null || q.isEmpty())
                throw new IllegalArgumentException("Not anagrams: extra '" + ch + "' in s1");
            image[i] = q.removeFirst(); // stable: earlier equal letters map to smaller indices
        }

        // Convert mapping to cycle notation for PermutationImpl_Kart
        Set<List<Integer>> cycles = toCycles(image);
        return new PermutationImpl_Jowad<>(cycles);
    }

    // Factory for a Deck from an explicit ordering of cards
    public static Deck getDeck(List<Card> ordering) {
        return new DeckImpl_Jowad(ordering);
    }

    // ---- helpers ----
    private static Set<List<Integer>> toCycles(int[] image) {
        int n = image.length;
        boolean[] seen = new boolean[n];
        Set<List<Integer>> cycles = new HashSet<>();
        for (int i = 0; i < n; i++) {
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
}
