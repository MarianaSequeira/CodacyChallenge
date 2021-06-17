package codacyChallenge.controller;

import codacyChallenge.model.CloneStatus;
import codacyChallenge.model.Commit;
import codacyChallenge.model.Repository;
import codacyChallenge.service.GitOperationsService;
import codacyChallenge.utils.InputValidation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/codacyChallenge/api")
public class GitOperationsController {

    private GitOperationsService gitOperationsService;
    private String DEFAULT_PAGE_SIZE = "10";
    private Repository repository;


    public GitOperationsController(GitOperationsService gitOperationsService) {
        this.gitOperationsService = gitOperationsService;
    }


    @GetMapping("/")
    public String index() {
        return "Let's go :D";
    }


    @GetMapping("/gitOp/cloneRepository")
    public ResponseEntity<Object> cloneRepository(@RequestParam(name = "webURL", required = true) String webURL) {

        CloneStatus status = this.gitOperationsService.getCloneStatus();

        if (status.equals(CloneStatus.CLONE_PENDING)) return new ResponseEntity<>("Your last request is still being processed. ", HttpStatus.OK);

        new Thread(() -> gitOperationsService.asyncCloneRepository(webURL)).start();

        status = this.gitOperationsService.getCloneStatus();

        if (status.equals(CloneStatus.CLONE_SUCCESS)) {
            this.repository = new Repository(webURL);
            return new ResponseEntity<>("Repository with Web URL " + webURL + " successfully cloned", HttpStatus.OK);
        }
        else if (status.equals(CloneStatus.CLONE_PENDING) || status.equals(CloneStatus.INITIAL)) {
            return new ResponseEntity<>("Repository is cloning.. Just wait a little bit more!", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Error while cloning repository with Web URL " + webURL, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }


    @GetMapping("/gitOp/branchesList")
    public ResponseEntity<Object> getBranchesList(@RequestParam(name = "webURL", required = true) String webURL) {

        if ( repository == null)
            return new ResponseEntity<>("No cloned repository. ", HttpStatus.UNPROCESSABLE_ENTITY);

        if (! webURL.equals(repository.getName()))
            return new ResponseEntity<>("The specified repository is not the same as the last cloned repository. ", HttpStatus.UNPROCESSABLE_ENTITY);

        ArrayList<String> branches = gitOperationsService.listBranches();

        if (branches == null)         return new ResponseEntity<>("Error while getting the branch list of repository with Web URL " + webURL, HttpStatus.UNPROCESSABLE_ENTITY);
        else if (branches.isEmpty())  return new ResponseEntity<>("System was not able to retrieve any branch from the repository with Web URL " + webURL, HttpStatus.OK);
        else                          return new ResponseEntity<>(branches, HttpStatus.OK);
    }


    @GetMapping("/gitOp/allCommitsLists")
    public ResponseEntity<Object> getAllCommitsList(@RequestParam(name = "webURL", required = true) String webURL,
                                                    @RequestParam(name = "branchName", required = true) String branchName) {

        if ( repository == null)
            return new ResponseEntity<>("No cloned repository. ", HttpStatus.UNPROCESSABLE_ENTITY);

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


    @RequestMapping(value = {"/gitOp/commitListPagination/{page}", "/gitOp/commitListPagination/{page}/{pageSize}"})
    public ResponseEntity<Object> getCommitListPagination(@RequestParam(name = "webURL", required = true) String webURL,
                                                          @PathVariable(name = "page", required = true) String page,
                                                          @PathVariable(name = "pageSize", required = false) String pageSize) {

        if ( repository == null)
            return new ResponseEntity<>("No cloned repository. ", HttpStatus.UNPROCESSABLE_ENTITY);

        if (! webURL.equals(repository.getName()))
            return new ResponseEntity<>("The specified repository is not the same as the last cloned repository. ", HttpStatus.UNPROCESSABLE_ENTITY);

        if (pageSize == null || pageSize.equals("")) pageSize = DEFAULT_PAGE_SIZE;

        if (!InputValidation.validatePageInformation(pageSize) || !InputValidation.validatePageInformation( page ))
            return new ResponseEntity<>("The pageSize or the page parameters are not valid. ", HttpStatus.UNPROCESSABLE_ENTITY);


        ArrayList<Commit> commits = gitOperationsService.getCommitListPagination(Integer.parseInt(pageSize) * Integer.parseInt(page), pageSize);


        if ( commits == null )            return new ResponseEntity<>("Error while getting the commits from the branch from page " + page + " of the specified repository.", HttpStatus.UNPROCESSABLE_ENTITY);
        else if ( commits.isEmpty() )     return new ResponseEntity<>("System was not able to retrieve any commit from page " + page + " of the specified repository.", HttpStatus.OK);
        else                              return new ResponseEntity<>(commits, HttpStatus.OK);
    }
}
