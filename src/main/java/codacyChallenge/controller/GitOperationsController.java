package codacyChallenge.controller;

import codacyChallenge.model.Commit;
import codacyChallenge.model.Repository;
import codacyChallenge.service.GitOperationsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Reader;
import java.util.ArrayList;

@RestController
@RequestMapping("/codacyChallenge/api")
public class GitOperationsController {

    private GitOperationsService gitOperationsService;

    private Repository repository;


    public GitOperationsController(GitOperationsService gitOperationsService) {
        this.gitOperationsService = gitOperationsService;
    }


    @GetMapping("/")
    public String index() {
        return "Let's gooooo";
    }


    @GetMapping("/gitOp/cloneRepository")
    public ResponseEntity<Object> cloneRepository(@RequestParam(name = "webURL", required = true) String webURL) {

        boolean success = gitOperationsService.cloneRepository(webURL);

        if (success) {
            this.repository = new Repository(webURL);
            return new ResponseEntity<>("Repository with Web URL " + webURL + " successfully cloned", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Error while cloning repository with Web URL " + webURL, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }


    @GetMapping("/gitOp/branchesList")
    public ResponseEntity<Object> getBranchesList(@RequestParam(name = "webURL", required = true) String webURL) {

        if (! webURL.equals(repository.getName())) return new ResponseEntity<>("The specified repository is not the same as the last cloned repository. ", HttpStatus.UNPROCESSABLE_ENTITY);

        ArrayList<String> branches = gitOperationsService.listBranches();

        if (branches == null)         return new ResponseEntity<>("Error while getting the branch list of repository with Web URL " + webURL, HttpStatus.UNPROCESSABLE_ENTITY);
        else if (branches.isEmpty())  return new ResponseEntity<>("System was not able to retrieve any branch from the repository with Web URL " + webURL, HttpStatus.OK);
        else                          return new ResponseEntity<>(branches, HttpStatus.OK);
    }


    @GetMapping("/gitOp/allCommitsLists")
    public ResponseEntity<Object> getAllCommitsList(@RequestParam(name = "webURL", required = true) String webURL,
                                                    @RequestParam(name = "branchName", required = true) String branchName) {

        if (! webURL.equals(repository.getName()))
            return new ResponseEntity<>("The specified repository is not the same as the last cloned repository. ", HttpStatus.UNPROCESSABLE_ENTITY);

        ArrayList<String> branches = gitOperationsService.listBranches();

        if (branches == null || !branches.contains(branchName))
            return new ResponseEntity<>("It was not possible to retrieve the branch '" + branchName + "' on the specified repository.", HttpStatus.UNPROCESSABLE_ENTITY);

        ArrayList<Commit> commits = gitOperationsService.getListOfCommits(branchName);

        if ( commits == null )            return new ResponseEntity<>("Error while getting the commits from the branch " + branchName + " of the specified repository.", HttpStatus.UNPROCESSABLE_ENTITY);
        else if ( commits.isEmpty() )     return new ResponseEntity<>("System was not able to retrieve any commit from the branch " + branchName + " of the specified repository.", HttpStatus.OK);
        else                              return new ResponseEntity<>(commits, HttpStatus.OK);
    }



}
