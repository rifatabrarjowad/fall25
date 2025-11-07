package writing;

import java.util.List;

public interface GhostWriter extends AssignmentMetaData {
    //part of post: rv != null
    public String getInputText();

    //part of pre: seed != null : "seed is null!";
    //part of pre: seed.length() <= 10 :
    //	"seed.length() = " + seed.length() + " > 10!";
    //part of pre: selectionList != null :
    //	"selectionList is null!";
    //part of pre: getInputText().indexOf(seed) != -1 :
    //	"getInputText().indexOf(" + seed + ") is -1!";
    //part of post: rv.startsWith(seed)
    //part of post: rv.length() == selectionList.size()
    public String generate(String seed, List<Integer> selectionList);
}
