package writing;

import java.util.Arrays;
import java.util.List;

public class Main_Jowad {
    public static void main(String[] args) {
        String peterPiper = "Peter Piper picked a peck of pickled peppers; A peck of pickled peppers Peter Piper picked; If Peter Piper picked a peck of pickled peppers, Where's the peck of pickled peppers Peter Piper picked?";
        GhostWriter gw = new GhostWriterImpl_Jowad(peterPiper);
        String seed = "If";
        List<Integer> selectionList = Arrays.asList(256374724, 467417507, 268504790, 755524626, 1221582886, 957269485);
        String out = gw.generate(seed, selectionList);
        System.out.println(out);
    }
}
