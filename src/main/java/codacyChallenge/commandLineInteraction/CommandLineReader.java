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

    private final GitOperationsService gitOperationsService;

    public CommandLineReader(GitOperationsService gitOperationsService) {
        this.gitOperationsService = gitOperationsService;
    }


    @Override
    public void run(String... args)  {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n\n=============================================================================================================================================================================================================================");
            System.out.println("====================================================================================== GET THE LIST OF COMMITS FROM A REPOSITORY ============================================================================================");
            System.out.println("=============================================================================================================================================================================================================================");

            System.out.print("\nFirst enter the web URL of the git repository: ");
            String webURL = sc.nextLine();


            /* ------------------------ Validate Web URL and Clone the Selected Repo -------------------------------- */

            if (validateWebURL(webURL)) this.gitOperationsService.cloneRepository(webURL);
            else continue;

            /*--------------------------------------------------------------------------------------------------------*/


            Repository repository = new Repository(webURL);


            /* -------------------------- Get and Print List of Branches of Repository  ----------------------------- */

            ArrayList<String> branches = this.gitOperationsService.listBranches();
            repository.setBranches(branches);

            System.out.println("\nThe chosen repository has the following branches: ");
            branches.forEach(value -> System.out.println("\t" + branches.indexOf(value) + ": " + value));

            /*--------------------------------------------------------------------------------------------------------*/



            /* ------------------------------------ Select Branch of Repository  ------------------------------------ */

            String index;

            do {
                System.out.print("\nInsert the number of the branch from which you want to get the list of commits: ");
                index = sc.nextLine();
            } while (!validateNumberOfBranch(index, branches.size()));

            String branch = branches.get(Integer.parseInt(index));
            /*--------------------------------------------------------------------------------------------------------*/



            /* ----------------------------- Get List of Commits of Select Branch  ---------------------------------- */

            repository.addBranchCommitList(branch, this.gitOperationsService.getListOfCommits(branch));
            System.out.println("\n================================================================================================= THE LIST OF COMMITS  ======================================================================================================\n");
            repository.printBranchCommitList(branch);
            /*--------------------------------------------------------------------------------------------------------*/
        }
    }


    /*
        Private method to allow to validate Web URL inserted by the user.
        //TODO: Add more validations, if possible
     */
    private boolean validateWebURL(String webURL) {

        if (!webURL.startsWith("http") || !webURL.contains("//github.com") || !webURL.endsWith(".git") || webURL.contains("\t")) {
            System.out.println("** ERROR ** The Web URL inserted is not valid. Please try again.");
            return false;
        }
        return true;
    }


    /*
        Private method to allow to validate the number of the branch inserted by the user.
        //TODO: Add more validations, if possible
     */
    private boolean validateNumberOfBranch(String index, int finalSize) {

        try {
            int i = Integer.parseInt(index);

            if ( i < 0 || i >= finalSize ) {
                System.out.println("** ERROR ** The inserted option is not valid. Please try again.");
                return false;
            }

        } catch (NumberFormatException e) {
            System.out.println("** ERROR ** The inserted option is not a number. Please try again.");
            return false;
        }

        return true;
    }
}
