package codacyChallenge.utils;

import codacyChallenge.model.Commit;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

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

    /*
        This method is responsible for getting the commits from the BufferedReader, and allows to retrieve the
        commit list presented on the output of the 'git log' command.

     */
    public static ArrayList<Commit> parseGitLog(BufferedReader buf) throws IOException {

        String line = "";
        Commit currentCommit = null;
        boolean readingHeader = true;

        ArrayList<Commit> listCommits = new ArrayList<>();

        while ((line=buf.readLine())!=null)
        {
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
                        currentCommit.setAuthorEmail("");
                    }
                    if (authorNameMatcher.matches() && authorEmailMatcher.matches()) {
                        currentCommit.setAuthorName(authorNameMatcher.group(1).trim());
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

        if ( currentCommit != null && !listCommits.contains(currentCommit)) {
            if (currentCommit.isValid()) listCommits.add(currentCommit);
        }

        return listCommits;
    }

    public static MappingJackson2HttpMessageConverter getCommitConverter() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("CommitDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(Commit.class, new CommitDeserializer());
        mapper.registerModule(module);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mapper);

        return converter;
    }

    public static MappingJackson2HttpMessageConverter getBranchesConverter() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("BranchesDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(String.class, new BranchesDeserializer());
        mapper.registerModule(module);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mapper);

        return converter;
    }
}
