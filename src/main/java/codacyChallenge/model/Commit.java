package codacyChallenge.model;

import java.util.Objects;

public class Commit {

    private String sha;
    private String authorName;
    private String authorEmail;
    private String date;
    private String description;

    public Commit() { }


    public Commit(String sha)  {
        this.sha = sha;
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
        if (this.description == null) this.description = "";
        this.description += append;
    }

    @Override
    public String toString() {
        return String.format("%-45s| Author: %-20s | Email: %-30s | Date: %30s | Description: %s", sha, authorName, authorEmail, date, description);
    }

    public boolean isValid() {

        if (authorName == null || authorName.isEmpty()) return false;
        if (date == null || date.isEmpty()) return false;
        if (description == null || description.isEmpty()) return false;

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commit commit = (Commit) o;
        return Objects.equals(sha, commit.sha) && Objects.equals(authorName, commit.authorName) && Objects.equals(date, commit.date) && Objects.equals(description, commit.description);
    }
}
