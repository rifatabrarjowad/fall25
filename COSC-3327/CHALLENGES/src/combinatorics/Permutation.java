package combinatorics;

import java.util.List;

public interface Permutation<T> {
    List<T> getDomain();
    T getImage(T x);
    T getImage(int index);
}
