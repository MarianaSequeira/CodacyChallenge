package codacyChallenge.service;

import codacyChallenge.model.CloneStatus;
import codacyChallenge.model.Commit;
import codacyChallenge.utils.GitLogParser;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


@Service
public class GitOperationsService {

    private Path path = Paths.get( Paths.get(System.getProperty("user.dir")).getParent() + "/tempRepo");
    private CloneStatus cloneStatus = CloneStatus.INITIAL;


    public boolean cloneRepository(String webURL)  {
        this.createTempRepoDirectory();

        System.out.print("\tGetting remote repository...");

        BufferedReader success = this.executeCommand(String.format("git clone --bare %s .",webURL));

        if (success == null) {
            System.out.println("\n\t\tError while cloning repository with the Web URL " + webURL);
            System.out.println("\t\tA possible reason behind this error is that the specified repository is private."
                    + "\n\t\tIn the future this feature will be implemented, until then test the system with a public repository.");
            return false;
        }

        System.out.println("\tFinished!");

        return true;
    }


    public void asyncCloneRepository(String webUrl) {
        this.cloneStatus = CloneStatus.CLONE_PENDING;
        boolean success = this.cloneRepository(webUrl);
        if (success)      this.cloneStatus = CloneStatus.CLONE_SUCCESS;
        else              this.cloneStatus = CloneStatus.CLONE_FAILED;
    }


    public ArrayList<String> listBranches() {
        ArrayList<String> results = new ArrayList<>();

        BufferedReader success = this.executeCommand("git branch -a");

        if (success == null) {
            System.out.println("\n\t\tError while getting branches of repository in /tempRepo directory. " +
                    "\n\t\tThis method assumes that a repository was previously cloned. Please start again.");
            return null;
        }

        try {
            String line = "";
            while ((line=success.readLine())!=null)  results.add(line.trim().replace("* ", ""));
        } catch (IOException e) {
            System.out.println("Error processing branches of repository");
            return null;
        }

        return results;
    }


    public ArrayList<Commit> getListOfCommits(String branch) {
        ArrayList<Commit> commits;

        if (branch == null) return null;

        BufferedReader success = this.executeCommand(String.format("git --no-pager log %s", branch));

        if (success == null) {
            System.out.println("\n\t\tError while getting commit list of branch " + branch + "." +
                    "\n\t\tThis method assumes that a repository was previously cloned. Please start again.");
            return null;
        }

        try {
            commits = GitLogParser.parseGitLog(success);
        } catch (IOException e) {
            System.out.println("Error while getting commit list of branch " + branch);
            return null;
        }

        return commits;
    }


    public ArrayList<Commit> getCommitListPagination(int skip, String pageSize) {
        ArrayList<Commit> commits;

        BufferedReader success = this.executeCommand(String.format("git --no-pager log --skip=%d -n %s", skip, pageSize));

        if (success == null) {
            System.out.println("\n\t\tError while getting commit list in pagination method." +
                    "\n\t\tThis method assumes that a repository was previously cloned. Please start again.");
            return null;
        }

        try {
            commits = GitLogParser.parseGitLog(success);
        } catch (IOException e) {
            System.out.println("Error while getting commit list of branch ");
            return null;
        }

        return commits;
    }


    public void createTempRepoDirectory()  {
        try
        {
            if (!Files.exists(path)) Files.createDirectory(path );
            else                     FileUtils.cleanDirectory(new File(String.valueOf(path)));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public BufferedReader executeCommand(String command) {
        Runtime run = Runtime.getRuntime();

        try {
            Process process = run.exec(command, null, new File(String.valueOf(path)));

            String stdout = IOUtils.toString(process.getInputStream(), Charset.defaultCharset());

            int status = process.waitFor();

            if (status != 0) {
                return null;
            }

            return new BufferedReader( new StringReader(stdout));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }


    public CloneStatus getCloneStatus() {
        return cloneStatus;
    }
    public void setCloneStatus(CloneStatus cloneStatus) {
        this.cloneStatus = cloneStatus;
    }
}
