package codacyChallenge.commandLineInteraction;

import codacyChallenge.model.Repository;
import codacyChallenge.service.GitOperationsService;
import codacyChallenge.utils.GitLogParser;
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

//  https://github.com/MarianaSequeira/WS_SpotifyStats.git

@Component
public class CommandLineReader implements CommandLineRunner {

    private GitOperationsService gitOperationsService;

    public CommandLineReader(GitOperationsService gitOperationsService) {
        this.gitOperationsService = gitOperationsService;
    }

    @Override
    public void run(String... args)  {
        Scanner sc = new Scanner(System.in);

        System.out.print("\nEnter the web URL of the git: ");
        String webURL = sc.nextLine();

        this.gitOperationsService.cloneRepository(webURL);

        Repository repository = new Repository(webURL);

        ArrayList<String> branches = this.gitOperationsService.listBranches();
        repository.setBranches(branches);

        branches.forEach(value -> System.out.println("\t" + branches.indexOf(value) + ": " + value));

        System.out.print("\nInsert the number associated to the branch from which you want to get the list of commits: ");
        String branch = branches.get(sc.nextInt());

        repository.addBranchCommitList(branch, this.gitOperationsService.getListOfCommits(branch));
    }
}
