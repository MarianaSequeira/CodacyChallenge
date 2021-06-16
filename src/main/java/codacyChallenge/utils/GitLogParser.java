package codacyChallenge.utils;

import codacyChallenge.model.Commit;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class  GitLogParser {


    private static final Pattern commitHashPattern = Pattern.compile("(?i)^commit\\s+(\\w{40}).*");
    private static final Pattern authorNamePattern = Pattern.compile("(?i)^Author:\\s+([^<]+).*");
    private static final Pattern authorEmailPattern = Pattern.compile("(?i)^Author:\\s+[^<]+<([^>]+)>.*");
    private static final Pattern datePattern = Pattern.compile("(?i)^Date:\\s+([^<]+).*");

    public GitLogParser() {

    }

    public static ArrayList<Commit> parseGitLog(BufferedReader buf) throws IOException {

        String line = "";
        Commit currentCommit = null;
        boolean readingHeader = true;

        ArrayList<Commit> listCommits = new ArrayList<>();

        while ((line=buf.readLine())!=null)
        {
            System.out.println(line);
            Matcher commitHashMatcher = commitHashPattern.matcher(line);
            if (commitHashMatcher.matches()) {

                if (currentCommit != null)  listCommits.add(currentCommit);

                currentCommit = new Commit(commitHashMatcher.group(1));
                readingHeader = true;
                continue;
            }

            if (readingHeader && currentCommit != null) {
                Matcher authorNameMatcher = authorNamePattern.matcher(line);
                Matcher authorEmailMatcher = authorEmailPattern.matcher(line);

                if (authorNameMatcher.matches() || authorEmailMatcher.matches()) {
                    if (authorNameMatcher.matches()) {
                        currentCommit.setAuthorName(authorNameMatcher.group(1).trim());
                    }
                    if (authorEmailMatcher.matches()) {
                        String authorEmail = authorEmailMatcher.group(1).trim();
                        if (authorEmail.contains("@")) {
                            currentCommit.setAuthorEmail(authorEmail);
                        } else {
                            currentCommit.setAuthorEmail("");
                        }
                    }
                    continue;
                }

                Matcher authorDateMatcher = datePattern.matcher(line);
                if (authorDateMatcher.matches()) {
                    currentCommit.setDate(authorDateMatcher.group(1));
                    continue;
                }

                if (line.trim().length() == 0) readingHeader = false;
            }
            else {
                if (currentCommit == null)  continue;

                if (line.startsWith("    "))  {
                    currentCommit.appendDescription(line.substring(4));
                }
            }
        }

        if (!listCommits.contains(currentCommit)) listCommits.add(currentCommit);

        return listCommits;
    }
}
