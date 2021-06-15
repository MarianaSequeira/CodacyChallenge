package codacyChallenge.service;

import codacyChallenge.model.Commit;
import codacyChallenge.utils.GitLogParser;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Service
public class GitOperationsService {

    public void cloneRepository(String webURL)  {
        this.createTempRepoDirectory();

        System.out.print("\tGetting remote repository...");
        Runtime run = Runtime.getRuntime();
        try {
            Process pr = run.exec( String.format("git clone --bare %s ./tempRepo",webURL));
            pr.waitFor();

            if (pr.exitValue() != 0)    System.out.println("ups");

            System.out.println("\tFinished!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public ArrayList<String> listBranches() {
        System.out.println("\nAvailable branches in the specified repo: ");
        ArrayList<String> results = new ArrayList<>();

        Runtime run = Runtime.getRuntime();
        try {
            Path path = Paths.get(System.getProperty("user.dir") + "/tempRepo");
            Process pr = run.exec("git branch -a", null, new File(String.valueOf(path)));
            pr.waitFor();

            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line = "";
            while ((line=buf.readLine())!=null)  results.add(line.trim().split("\\* ")[1]);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return results;
    }


    public ArrayList<Commit> getListOfCommits(String branch) {
        ArrayList<Commit> commits = new ArrayList<>();
        Runtime run = Runtime.getRuntime();
        try {
            Path path = Paths.get(System.getProperty("user.dir") + "/tempRepo");
            Process pr = run.exec(String.format("git log %s", branch), null, new File(String.valueOf(path)));
            pr.waitFor();

            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            commits = GitLogParser.parseGitLog(buf);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return commits;
    }


    public void createTempRepoDirectory()  {
        try
        {
            Path path = Paths.get(System.getProperty("user.dir") + "/tempRepo");

            if (!Files.exists(path)) Files.createDirectory(path);
            else                     FileUtils.cleanDirectory(new File(String.valueOf(path)));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
