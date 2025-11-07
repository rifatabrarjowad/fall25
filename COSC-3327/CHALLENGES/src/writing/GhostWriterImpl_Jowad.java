package writing;

import java.util.*;


public class GhostWriterImpl_Jowad implements GhostWriter {

    private final String text;
    private final Map<String, List<Character>> suffixCache = new HashMap<>();
    private final List<Character> globalSuffixes;

    public GhostWriterImpl_Jowad(String inputText) {
        String normalized = (inputText == null ? "" : inputText)
                .replace('\r', ' ')
                .replace('\n', ' ');
        this.text = normalized;
        this.globalSuffixes = buildGlobalSuffixes();
    }

    @Override
    public String getInputText() {
        return text;
    }

    @Override
    public String generate(String seed, List<Integer> selectionList) {

        assert seed != null : "seed is null!";
        assert seed.length() <= 10 : "seed.length() = " + seed.length() + " > 10!";
        assert selectionList != null : "selectionList is null!";
        assert getInputText().indexOf(seed) != -1
                : "getInputText().indexOf(" + seed + ") is -1!";

        final int outputLen = selectionList.size();
        final int k = seed.length();
        StringBuilder rv = new StringBuilder(outputLen);
        for (int i = 0; i < Math.min(k, outputLen); i++) {
            rv.append(seed.charAt(i));
        }
        if (outputLen <= k) {
            return rv.toString();
        }
        while (rv.length() < outputLen) {
            int position = rv.length();
            Character next = null;
            for (int ctxLen = k; ctxLen >= 0; ctxLen--) {
                List<Character> suffixes;
                if (ctxLen == 0) {
                    suffixes = globalSuffixes;
                } else if (rv.length() >= ctxLen) {
                    String ctx = rv.substring(rv.length() - ctxLen);
                    suffixes = sortedSuffixList(ctx);
                } else {
                    continue;
                }

                if (!suffixes.isEmpty()) {
                    int pick = Math.floorMod(selectionList.get(position), suffixes.size());
                    next = suffixes.get(pick);
                    break;
                }
            }

            if (next == null) next = ' ';
            rv.append(next);
        }

        return rv.toString();
    }

    private List<Character> sortedSuffixList(String s) {
        List<Character> cached = suffixCache.get(s);
        if (cached != null) return cached;

        List<Character> out = new ArrayList<>();
        final int n = text.length();
        final int m = s.length();

        if (m == 0) {
            out = new ArrayList<>(globalSuffixes);
            suffixCache.put(s, out);
            return out;
        }

        for (int i = 0; i + m < n; i++) {
            if (text.charAt(i) == s.charAt(0) && text.startsWith(s, i)) {
                out.add(text.charAt(i + m));
            }
        }

        Collections.sort(out);
        out = Collections.unmodifiableList(out);
        suffixCache.put(s, out);
        return out;
    }

    private List<Character> buildGlobalSuffixes() {
        List<Character> out = new ArrayList<>(text.length());
        for (int i = 0; i < text.length(); i++) out.add(text.charAt(i));
        Collections.sort(out);
        return Collections.unmodifiableList(out);
    }

    // ---------------- AssignmentMetaData ----------------
    @Override
    public String getFirstNameOfSubmitter() {
        return "Rifat";
    }

    @Override
    public String getLastNameOfSubmitter() {
        return "Jowad";
    }

    @Override
    public double getHoursSpentWorkingOnThisAssignment() {
        return 7.5;
    }

    @Override
    public int getScoreAgainstTestCasesSubset() {
        return 0;
    }
}
