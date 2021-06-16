package codacyChallenge.model;

public class Commit {

    private String sha;
    private String authorName;
    private String authorEmail;
    private String date;
    private String description;

    public Commit() { }


    public Commit(String sha)  {
        this.sha = sha;
        this.description = "";
    }


    public Commit(String sha, String authorName, String authorEmail, String date, String description) {
        this.sha = sha;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.date = date;
        this.description = description;
    }


    public String getSha() {
        return sha;
    }
    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }
    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void appendDescription(String append) {
        this.description += append;
    }

    @Override
    public String toString() {

        return String.format("%-45s| Author: %-20s | Email: %-30s | Date: %30s | Description: %s", sha, authorName, authorEmail, date, description);

    }
}
