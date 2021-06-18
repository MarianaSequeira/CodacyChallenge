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
    private Repository repository;
    private String url = "https://github.com/%s/%s";


    public GitOperationsController(GitOperationsService gitOperationsService) {
        this.gitOperationsService = gitOperationsService;
    }


    @GetMapping("/")
    public String index() {
        return "Let's go!";
    }


    @GetMapping("/repos/{owner}/{repo}/clone")
    public ResponseEntity<Object> cloneRepository(@PathVariable(name = "owner", required = true) String owner,
                                                  @PathVariable(name = "repo", required = true) String repo) {

        String webURL = String.format(url, owner, repo);

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


    @GetMapping("/repos/{owner}/{repo}/branches")
    public ResponseEntity<Object> getBranchesList(@PathVariable(name = "owner", required = true) String owner,
                                                  @PathVariable(name = "repo", required = true) String repo) {

        if ( repository == null)
            return new ResponseEntity<>("No cloned repository. ", HttpStatus.UNPROCESSABLE_ENTITY);

        if (! String.format(url, owner, repo).equals(repository.getName()))
            return new ResponseEntity<>("The specified repository is not the same as the last cloned repository. ", HttpStatus.UNPROCESSABLE_ENTITY);

        ArrayList<String> branches = gitOperationsService.listBranches();

        if (branches == null)         return new ResponseEntity<>("Error while getting the branch list of "+owner+"'s "+ repo +" repository", HttpStatus.UNPROCESSABLE_ENTITY);
        else if (branches.isEmpty())  return new ResponseEntity<>("System was not able to retrieve any branch from "+owner+"'s "+ repo +" repository", HttpStatus.OK);
        else                          return new ResponseEntity<>(branches, HttpStatus.OK);
    }


    //https://api.github.com/repos/MarianaSequeira/COEUS/commits?sha=dev
    @GetMapping("/repos/{owner}/{repo}/allCommits")
    public ResponseEntity<Object> getAllCommitsList(@PathVariable(name = "owner", required = true) String owner,
                                                    @PathVariable(name = "repo", required = true) String repo,
                                                    @RequestParam(name = "branch", required = true) String branch) {

        ResponseEntity<Object> obj = this.getBranchesList(owner, repo);

        if (!obj.getStatusCode().equals(HttpStatus.OK)) return obj;
        if (! (obj.getBody() instanceof ArrayList ))    return obj;

        ArrayList<String> branches = (ArrayList<String>) getBranchesList(owner, repo).getBody();

        if (branches == null || !branches.contains(branch))
            return new ResponseEntity<>("It was not possible to retrieve the branch '" + branch + "' on the specified repository.", HttpStatus.UNPROCESSABLE_ENTITY);

        ArrayList<Commit> commits = gitOperationsService.getListOfCommits(branch);

        if ( commits == null )            return new ResponseEntity<>("Error while getting the commits from the branch " + branch + " of the specified repository.", HttpStatus.UNPROCESSABLE_ENTITY);
        else if ( commits.isEmpty() )     return new ResponseEntity<>("System was not able to retrieve any commit from the branch " + branch + " of the specified repository.", HttpStatus.OK);
        else                              return new ResponseEntity<>(commits, HttpStatus.OK);
    }


    @RequestMapping(value = {"/repos/{owner}/{repo}/commits"})
    public ResponseEntity<Object> getCommitListPagination(@PathVariable(name = "owner", required = true) String owner,
                                                          @PathVariable(name = "repo", required = true) String repo,
                                                          @RequestParam(name = "page", required = true) String page,
                                                          @RequestParam(name = "per_page", required = true, defaultValue = "10") String per_page) {

        if ( repository == null)
            return new ResponseEntity<>("No cloned repository. ", HttpStatus.UNPROCESSABLE_ENTITY);

        if (! String.format(url, owner, repo).equals(repository.getName()))
            return new ResponseEntity<>("The specified repository is not the same as the last cloned repository. ", HttpStatus.UNPROCESSABLE_ENTITY);

        if (!InputValidation.validatePageInformation(per_page) || !InputValidation.validatePageInformation( page ))
            return new ResponseEntity<>("The pageSize or the page parameters are not valid. ", HttpStatus.UNPROCESSABLE_ENTITY);


        ArrayList<Commit> commits = gitOperationsService.getCommitListPagination(Integer.parseInt(per_page) * Integer.parseInt(page), per_page);


        if ( commits == null )            return new ResponseEntity<>("Error while getting the commits from the branch from page " + page + " of the specified repository.", HttpStatus.UNPROCESSABLE_ENTITY);
        else if ( commits.isEmpty() )     return new ResponseEntity<>("System was not able to retrieve any commit from page " + page + " of the specified repository.", HttpStatus.OK);
        else                              return new ResponseEntity<>(commits, HttpStatus.OK);
    }
}
