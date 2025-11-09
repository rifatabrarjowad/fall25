package combinatorics;

import java.util.*;
public final class PermutationImpl_Jowad<T> implements Permutation<T> {

    private final List<T> domain;
    private final List<T> image;
    private final Map<T, Integer> index;

    public PermutationImpl_Jowad(List<T> domain, List<T> image) {
        Objects.requireNonNull(domain, "domain");
        Objects.requireNonNull(image, "image");
        if (domain.size() != image.size()) {
            throw new IllegalArgumentException("domain and image sizes differ");
        }
        Set<T> dset = new HashSet<>(domain);
        if (dset.size() != domain.size()) {
            throw new IllegalArgumentException("domain contains duplicates");
        }
        if (!dset.equals(new HashSet<>(image))) {
            throw new IllegalArgumentException("image is not a permutation of the domain");
        }

        this.domain = Collections.unmodifiableList(new ArrayList<>(domain));
        this.image  = Collections.unmodifiableList(new ArrayList<>(image));

        Map<T, Integer> idx = new HashMap<>(domain.size() * 2);
        for (int i = 0; i < domain.size(); i++) idx.put(domain.get(i), i);
        this.index = Collections.unmodifiableMap(idx);
    }


    public static <U> PermutationImpl_Jowad<U> fromMap(Map<U, U> map, List<U> domainOrder) {
        List<U> image = new ArrayList<>(domainOrder.size());
        for (U d : domainOrder) {
            U im = map.get(d);
            if (im == null) throw new IllegalArgumentException("map missing image for: " + d);
            image.add(im);
        }
        return new PermutationImpl_Jowad<>(domainOrder, image);
    }


    public static PermutationImpl_Jowad<Integer> fromImage(int[] img) {
        int n = img.length;
        List<Integer> domain = new ArrayList<>(n);
        List<Integer> image  = new ArrayList<>(n);
        boolean[] seen = new boolean[n];

        for (int i = 0; i < n; i++) domain.add(i);

        for (int i = 0; i < n; i++) {
            int v = img[i];
            if (v < 0 || v >= n) {
                throw new IllegalArgumentException("image[" + i + "] = " + v + " out of range 0.." + (n-1));
            }
            image.add(v);
            seen[v] = true;
        }
        // ensure bijection
        for (int v = 0; v < n; v++) {
            if (!seen[v]) throw new IllegalArgumentException("image is not a permutation (missing " + v + ")");
        }

        return new PermutationImpl_Jowad<>(domain, image);
    }


    public PermutationImpl_Jowad(Set<List<Integer>> cycles) {
        Objects.requireNonNull(cycles, "cycles");

        // Find maximum index to establish domain size
        int max = -1;
        for (List<Integer> c : cycles) {
            for (Integer x : c) {
                if (x == null || x < 0) throw new IllegalArgumentException("negative/null index in cycle");
                if (x > max) max = x;
            }
        }
        int n = Math.max(0, max + 1);

        // Start as identity mapping
        int[] img = new int[n];
        for (int i = 0; i < n; i++) img[i] = i;

        // Apply each cycle (a1 a2 ... ak) meaning ai -> a_{i+1}, ak -> a1
        for (List<Integer> c : cycles) {
            if (c.isEmpty()) continue;
            if (c.size() == 1) {
                int a = c.get(0);
                ensureInRange(a, n);
                img[a] = a;
                continue;
            }
            for (int i = 0; i < c.size(); i++) {
                int a = c.get(i);
                int b = c.get((i + 1) % c.size());
                ensureInRange(a, n);
                ensureInRange(b, n);
                img[a] = b;
            }
        }

        PermutationImpl_Jowad<Integer> p = fromImage(img);
        @SuppressWarnings("unchecked")
        List<T> d = (List<T>) p.domain;
        @SuppressWarnings("unchecked")
        List<T> im = (List<T>) p.image;
        @SuppressWarnings("unchecked")
        Map<T,Integer> idx = (Map<T,Integer>) (Map<?,?>) p.index;

        this.domain = d;
        this.image  = im;
        this.index  = idx;
    }

    private static void ensureInRange(int a, int n) {
        if (a < 0 || a >= n) {
            throw new IllegalArgumentException("cycle index " + a + " out of range 0.." + (n-1));
        }
    }

    @Override
    public List<T> getDomain() {
        return domain;
    }

    @Override
    public T getImage(T x) {
        Integer i = index.get(x);
        return (i == null) ? null : image.get(i);
    }

    @Override
    public T getImage(int idx) {
        return image.get(idx);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permutation)) return false;
        Permutation<?> that = (Permutation<?>) o;

        // Compare images in domain order to be robust to different implementations
        if (!Objects.equals(this.domain, that.getDomain())) return false;

        for (int i = 0; i < domain.size(); i++) {
            Object a = this.getImage(i);
            Object b = that.getImage(i);
            if (!Objects.equals(a, b)) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain, image);
    }

    @Override
    public String toString() {
        return "Permutation(domain=" + domain + ", image=" + image + ")";
    }
}
