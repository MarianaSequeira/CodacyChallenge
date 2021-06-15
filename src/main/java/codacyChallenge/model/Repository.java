package codacyChallenge.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Repository {

    private String webURL;
    private ArrayList<String> branches;
    private HashMap<String, ArrayList<Commit>> commitsByBranch;


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


    public void addBranchCommitList(String branch, ArrayList<Commit> commit)  {
        commitsByBranch.put(branch, commit);
    }


}
