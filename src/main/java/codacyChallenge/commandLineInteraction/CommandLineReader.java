package codacyChallenge.CommandLineInteraction;

import codacyChallenge.Model.Commit;
import codacyChallenge.Model.Repository;
import codacyChallenge.Utils.GitLogParser;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//  https://github.com/MarianaSequeira/WS_SpotifyStats.git

@Component
public class CommandLineReader implements CommandLineRunner {



    @Override
    public void run(String... args)  {
        Scanner sc = new Scanner(System.in);

        System.out.print("\nEnter the web URL of the git: ");
        String webURL = sc.nextLine();

        this.cloneRepository(webURL);

        ArrayList<String> branches = this.listBranches();
        branches.forEach(value -> System.out.println("\t" + branches.indexOf(value) + ": " + value));

        System.out.print("\nInsert the number associated to the branch from which you want to get the list of commits: ");
        String branch = branches.get(sc.nextInt());

        this.listCommit(branch);
    }


    private void cloneRepository(String webURL)  {
        this.createTempRepoDirectory();

        System.out.print("\t Getting remote repository...");
        Runtime run = Runtime.getRuntime();
        try {
            Process pr = run.exec( String.format("git clone --bare %s ./tempRepo",webURL));
            pr.waitFor();

            if (pr.exitValue() == 0)    System.out.println("nice");
            else                        System.out.println("ups");

            System.out.println("\tFinished!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private ArrayList<String> listBranches() {
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


    private void listCommit(String branch) {
        Repository repository = new Repository();
        System.out.println("Commits: ");

        Runtime run = Runtime.getRuntime();
        try {
            Path path = Paths.get(System.getProperty("user.dir") + "/tempRepo");
            Process pr = run.exec(String.format("git log %s", branch), null, new File(String.valueOf(path)));
            pr.waitFor();

            BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            repository.addCommit(branch, GitLogParser.parseGitLog(buf));

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        repository.getCommits().get(branch).forEach(value -> System.out.println(value));
    }


    private void createTempRepoDirectory()  {
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
