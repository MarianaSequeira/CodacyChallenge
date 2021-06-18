package codacyChallenge.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Repository {

    private String webURL;
    private ArrayList<String> branches;
    private HashMap<String, ArrayList<Commit>> commitsByBranch;
    private CloneStatus status ;


    public Repository() {
        this.branches = new ArrayList<>();
        this.commitsByBranch = new HashMap<>();
    }

    public Repository(String webURL, ArrayList<String> branches, HashMap<String, ArrayList<Commit>> commits) {
        this.webURL = webURL;
        this.branches = branches;
        this.commitsByBranch = commits;
    }

    public Repository(String webURL) {
        this.webURL = webURL;
        this.commitsByBranch = new HashMap<>();
    }


    public String getName() {
        return webURL;
    }
    public void setName(String name) {
        this.webURL = name;
    }

    public ArrayList<String> getBranches() {
        return branches;
    }
    public void setBranches(ArrayList<String> branches) {
        this.branches = branches;
    }

    public HashMap<String, ArrayList<Commit>> getCommits() {
        return commitsByBranch;
    }
    public void setCommits(HashMap<String, ArrayList<Commit>> commits) {
        this.commitsByBranch = commits;
    }

    public CloneStatus isStatus() {
        return status;
    }

    public CloneStatus getStatus() {
        return status;
    }

    public void setStatus(CloneStatus status) {
        this.status = status;
    }

    public void addBranchCommitList(String branch, ArrayList<Commit> commit)  {
        commitsByBranch.put(branch, commit);
    }


    public void printBranchCommitList(String branch) {
        commitsByBranch.get(branch).forEach(System.out::println);
    }
}
