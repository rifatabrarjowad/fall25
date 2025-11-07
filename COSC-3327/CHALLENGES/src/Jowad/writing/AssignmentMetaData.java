package writing;

public interface AssignmentMetaData
{
    //Your implementation here will look like: return "Sally"
    public String getFirstNameOfSubmitter();

    //Your implementation here will look like: return "Smith"
    public String getLastNameOfSubmitter();

    //Your implementation here will look like: return 6.5
    public double getHoursSpentWorkingOnThisAssignment();

    //Your return value needs to match your score on the distributed test cases
    //Your implementation here will look like: return 105
    public int getScoreAgainstTestCasesSubset();
}
