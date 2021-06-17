package codacyChallenge.commandLineInteraction;

import codacyChallenge.model.Repository;
import codacyChallenge.service.GitOperationsService;
import codacyChallenge.utils.InputValidation;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

//  https://github.com/MarianaSequeira/WS_SpotifyStats.git

@Profile("!test")
@Component
public class CommandLineReader implements CommandLineRunner {

    private final GitOperationsService gitOperationsService;

    public CommandLineReader(GitOperationsService gitOperationsService) {
        this.gitOperationsService = gitOperationsService;
    }


    @Override
    public void run(String... args)  {
        Scanner sc = new Scanner(System.in);

        String helper = String.join("", Collections.nCopies(88, "="));
        String helper2 = String.join("", Collections.nCopies(99, "="));

        while (true) {

            System.out.println("\n\n" +  String.join("", Collections.nCopies(220, "=")));
            System.out.println(helper + "  GET THE LIST OF COMMITS FROM A REPOSITORY " + helper);
            System.out.println(String.join("", Collections.nCopies(220, "=")));



            /* ------------------------ Validate Web URL and Clone the Selected Repo -------------------------------- */

            String webURL ;

            do {
                System.out.print("\nFirst enter the web URL of the git repository: ");
                webURL = sc.nextLine().trim();
            } while (!InputValidation.validateWebURL(webURL) || !this.gitOperationsService.cloneRepository(webURL));



            /*--------------------------------------------------------------------------------------------------------*/


            Repository repository = new Repository(webURL);


            /* -------------------------- Get and Print List of Branches of Repository  ----------------------------- */

            ArrayList<String> branches = this.gitOperationsService.listBranches();
            if (branches == null) continue;
            if (branches.isEmpty()) {
                System.out.println("The specified repository has no branches. Please start again");
                continue;
            }
            repository.setBranches(branches);

            System.out.println("\nThe chosen repository has the following branches: ");
            branches.forEach(value -> System.out.println("\t" + branches.indexOf(value) + ": " + value));

            /*--------------------------------------------------------------------------------------------------------*/



            /* ------------------------------------ Select Branch of Repository  ------------------------------------ */

            String index;

            do {
                System.out.print("\nInsert the number of the branch from which you want to get the list of commits: ");
                index = sc.nextLine();
            } while (!InputValidation.validateNumberOfBranch(index, branches.size()));

            String branch = branches.get(Integer.parseInt(index));

            /*--------------------------------------------------------------------------------------------------------*/



            /* ----------------------------- Get List of Commits of Select Branch  ---------------------------------- */

            repository.addBranchCommitList(branch, this.gitOperationsService.getListOfCommits(branch));
            System.out.println("\n" + helper2 + " THE LIST OF COMMITS  "+ helper2 +"\n");
            repository.printBranchCommitList(branch);
            /*--------------------------------------------------------------------------------------------------------*/
        }
    }
}
