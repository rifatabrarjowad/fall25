package writing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GhostWriterImpl_Skeleton implements GhostWriter {

    private final String inputText;

    public GhostWriterImpl_Skeleton(String inputText) {
        if (inputText == null) {
            throw new IllegalArgumentException("inputText is null!");
        }
        // newline -> space normalization
        this.inputText = inputText.replace('\n', ' ');
    }

    @Override
    public String getInputText() {
        return inputText;
    }

    @Override
    public String generate(String seed, List<Integer> selectionList) {
        assert seed != null : "seed is null!";
        assert seed.length() <= 10 : "seed.length() = " + seed.length() + " > 10!";
        assert selectionList != null : "selectionList is null!";
        assert getInputText().indexOf(seed) != -1 :
                "getInputText().indexOf(" + seed + ") is -1!";

        int desiredLen = selectionList.size();

        if (desiredLen < seed.length()) {
            throw new IllegalArgumentException(
                    "selectionList.size()(" + desiredLen +
                            ") < seed.length()(" + seed.length() + ")!"
            );
        }

        StringBuilder rv = new StringBuilder(desiredLen);
        rv.append(seed);

        int k = seed.length();

        while (rv.length() < desiredLen) {
            int position = rv.length();
            String context = rv.substring(rv.length() - k);

            List<Character> suffixList = getSortedSuffixList(context);
            if (suffixList.isEmpty()) {
                // corner case: stop if no continuation exists
                break;
            }

            int pick = Math.floorMod(selectionList.get(position), suffixList.size());
            rv.append(suffixList.get(pick));
        }

        if (rv.length() > desiredLen) {
            rv.setLength(desiredLen);
        }

        return rv.toString();
    }

    private List<Character> getSortedSuffixList(String s) {
        List<Character> result = new ArrayList<>();
        int n = inputText.length();
        int m = s.length();

        for (int i = 0; i + m < n; i++) {
            if (inputText.startsWith(s, i)) {
                result.add(inputText.charAt(i + m));
            }
        }

        Collections.sort(result);
        return result;
    }

    // ---------------- AssignmentMetaData methods ----------------
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
        return 7.5; // update with actual hours
    }

    @Override
    public int getScoreAgainstTestCasesSubset() {
        return 0; // update with actual subset score after running tests
    }
}
