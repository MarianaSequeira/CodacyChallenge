package codacyChallenge.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Repository {

    private String name;
    private ArrayList<String> branches;
    private HashMap<String, ArrayList<Commit>> commitsByBranch;


    public Repository() {
        this.branches = new ArrayList<>();
        this.commitsByBranch = new HashMap<>();
    }

    public Repository(String name, ArrayList<String> branches, HashMap<String, ArrayList<Commit>> commits) {
        this.name = name;
        this.branches = branches;
        this.commitsByBranch = commits;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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


    public void addCommit(String branch, ArrayList<Commit> commit)  {
        if (!commitsByBranch.containsKey(branch)) commitsByBranch.put(branch, new ArrayList<>());
        commitsByBranch.get(branch).addAll(commit);
    }


}
