package codacyChallenge.utils;

import codacyChallenge.model.CloneStatus;
import codacyChallenge.model.Commit;
import codacyChallenge.model.Repository;
import codacyChallenge.service.GitOperationsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

@Component
public class GitHandler {

    private Repository repository;
    private GitOperationsService gitOperationsService;

    public GitHandler(GitOperationsService gitOperationsService) {
        this.gitOperationsService = gitOperationsService;
    }

    public CloneStatus handleClone(String webURL) {
        if (this.gitOperationsService.getCloneStatus().equals(CloneStatus.CLONE_PENDING)) return CloneStatus.CLONE_PENDING;

        if (this.repository == null || !repository.getName().equals(webURL)) {
            this.repository = new Repository(webURL);
            this.gitOperationsService.asyncCloneRepository(webURL);
        }

        if (this.gitOperationsService.getCloneStatus().equals(CloneStatus.CLONE_SUCCESS))  return CloneStatus.CLONE_SUCCESS;
        else return CloneStatus.CLONE_FAILED;

    }

    public void handleCloneDependentRequests(DeferredResult<ResponseEntity<?>> output, String owner, String repo, String branch, String per_page, String page, Operation operation) {
        String url = "https://github.com/%s/%s";
        String webURL = String.format(url, owner, repo);
        ForkJoinPool.commonPool().submit(() -> {
            CloneStatus result = this.handleClone(webURL);

            switch (result) {
                case CLONE_PENDING:
                    output.setResult( new ResponseEntity<>("We are processing your last request.", HttpStatus.OK));
                    break;
                case CLONE_FAILED:
                    output.setResult( new ResponseEntity<>("Error while cloning repository with Web URL " + webURL, HttpStatus.UNPROCESSABLE_ENTITY));
                    break;
                case CLONE_SUCCESS:
                    if (operation.equals(Operation.CLONE)) {
                        output.setResult(new ResponseEntity<>("Repository with Web URL " + webURL + " successfully cloned", HttpStatus.OK));
                    }
                    else if (operation.equals(Operation.LIST_BRANCHES)) {
                        ArrayList<String> branches = gitOperationsService.listBranches();

                        if (branches == null)         output.setResult( new ResponseEntity<>("Error while getting the branch list of "+owner+"'s "+ repo +" repository", HttpStatus.UNPROCESSABLE_ENTITY));
                        else if (branches.isEmpty())  output.setResult( new ResponseEntity<>("System was not able to retrieve any branch from "+owner+"'s "+ repo +" repository", HttpStatus.OK));
                        else                          output.setResult( new ResponseEntity<>(branches, HttpStatus.OK));
                    }
                    else if (operation.equals(Operation.LIST_ALL_COMMITS)) {
                        ArrayList<String> branches = gitOperationsService.listBranches();

                        if (branches == null || !branches.contains(branch))
                            output.setResult( new ResponseEntity<>("It was not possible to retrieve the branch '" + branch + "' on the specified repository.", HttpStatus.UNPROCESSABLE_ENTITY));

                        ArrayList<Commit> commits = gitOperationsService.getListOfCommits(branch);

                        if (commits == null)        output.setResult(new ResponseEntity<>("Error while getting the commits from the branch " + branch + " of the specified repository.", HttpStatus.UNPROCESSABLE_ENTITY));
                        else if (commits.isEmpty()) output.setResult(new ResponseEntity<>("System was not able to retrieve any commit from the branch " + branch + " of the specified repository.", HttpStatus.OK));
                        else                        output.setResult(new ResponseEntity<>(commits, HttpStatus.OK));
                    }
                    else if (operation.equals(Operation.LIST_COMMITS_PAGE)) {

                        ArrayList<Commit> commits = gitOperationsService.getCommitListPagination(Integer.parseInt(per_page) * Integer.parseInt(page), per_page);

                        if ( commits == null )            output.setResult(new ResponseEntity<>("Error while getting the commits from the branch from page " + page + " of the specified repository.", HttpStatus.UNPROCESSABLE_ENTITY));
                        else if ( commits.isEmpty() )     output.setResult(new ResponseEntity<>("System was not able to retrieve any commit from page " + page + " of the specified repository.", HttpStatus.OK));
                        else                              output.setResult(new ResponseEntity<>(commits, HttpStatus.OK));
                    }

                    break;
            }
        });
    }

}
