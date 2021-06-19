package codacyChallenge.controller;

import codacyChallenge.model.Commit;
import codacyChallenge.utils.*;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/codacyChallenge")
public class GitOperationsController {

    private final GitHandler gitHandler;
    private final String GITHUB_API = "https://api.github.com/repos/%s/%s";

    public GitOperationsController( GitHandler gitHandler) {
        this.gitHandler = gitHandler;
    }


    @GetMapping("/")
    public String index() {
        return "Let's go!";
    }


    @GetMapping("/api/repos/{owner}/{repo}/clone")
    public DeferredResult<ResponseEntity<?>> cloneRepository(@PathVariable(name = "owner", required = true) String owner,
                                                             @PathVariable(name = "repo", required = true) String repo) {

        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();

        this.gitHandler.handleCloneDependentRequests(output, owner, repo, "", "", "", Operation.CLONE);

        output.onTimeout(() ->
            output.setErrorResult(
                ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                    .body("Repository is cloning.. Just wait a little bit more!")));

        return output;
    }


    @GetMapping("/api/repos/{owner}/{repo}/branches")
    public DeferredResult<ResponseEntity<?>> getBranchesList(@PathVariable(name = "owner", required = true) String owner,
                                                  @PathVariable(name = "repo", required = true) String repo) {

        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();

        this.gitHandler.handleCloneDependentRequests(output, owner, repo, "","", "", Operation.LIST_BRANCHES);

        output.onTimeout(() ->
                output.setErrorResult(
                        ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                                .body("We are processing your request.")));
        return output;
    }


    @GetMapping("/api/repos/{owner}/{repo}/allCommits")
    public DeferredResult<ResponseEntity<?>> getAllCommitsList(@PathVariable(name = "owner", required = true) String owner,
                                                    @PathVariable(name = "repo", required = true) String repo,
                                                    @RequestParam(name = "sha", required = false, defaultValue = "") String branch) {

        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();

        this.gitHandler.handleCloneDependentRequests(output, owner, repo, branch, "", "", Operation.LIST_ALL_COMMITS);

        output.onTimeout(() ->
                output.setErrorResult(
                        ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                                .body("We are processing your request.")));

        return output;
    }


    @RequestMapping("/api/repos/{owner}/{repo}/commits")
    public DeferredResult<ResponseEntity<?>>  getCommitListPagination(@PathVariable(name = "owner", required = true) String owner,
                                                          @PathVariable(name = "repo", required = true) String repo,
                                                          @RequestParam(name = "page", required = true) String page,
                                                          @RequestParam(name = "per_page", required = true, defaultValue = "10") String per_page) {

        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();

        if (!InputValidation.validatePageInformation(page) || !InputValidation.validatePageInformation(per_page)) {
            output.setResult(new ResponseEntity<>("** ERROR ** The inserted option is not a number. Please try again.", HttpStatus.UNPROCESSABLE_ENTITY));
            return output;
        }

        this.gitHandler.handleCloneDependentRequests(output, owner, repo, "", per_page, page, Operation.LIST_COMMITS_PAGE);

        output.onTimeout(() ->
                output.setErrorResult(
                        ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                                .body("We are processing your request.")));

        return output;
    }


    @GetMapping("/gitApi/repos/{owner}/{repo}/branches")
    public DeferredResult<ResponseEntity<?>> getBranchesListGAPI(@PathVariable(name = "owner", required = true) String owner,
                                                             @PathVariable(name = "repo", required = true) String repo) {

        String uri = String.format(GITHUB_API, owner, repo) + "/branches";
        DeferredResult<ResponseEntity<?>> result = new DeferredResult<>();
        try {
            MappingJackson2HttpMessageConverter converter = GitLogParser.getBranchesConverter();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(0, converter);

            String[] branches = restTemplate.getForObject(uri, String[].class);

            result.setResult(new ResponseEntity<>(branches, HttpStatus.OK));
            return result;
        } catch (Exception e ) {
            System.out.println("Error while trying to fetch information using the Github API");
            result = this.getBranchesList(owner, repo);
            return result;
        }
    }



    @RequestMapping("/gitApi/repos/{owner}/{repo}/allCommits")
    public DeferredResult<ResponseEntity<?>> getAllCommitsListGAPI(@PathVariable(name = "owner", required = true) String owner,
                                                                   @PathVariable(name = "repo", required = true) String repo,
                                                                   @RequestParam(name = "sha", required = false, defaultValue = "") String branch) {

        String uri = String.format(GITHUB_API, owner, repo) + "/commits?sha=" + branch;
        DeferredResult<ResponseEntity<?>> result = new DeferredResult<>();
        try {
            MappingJackson2HttpMessageConverter converter = GitLogParser.getCommitConverter();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(0, converter);

            result.setResult(new ResponseEntity<>(restTemplate.getForObject(uri, Commit[].class), HttpStatus.OK));
            return result;
        } catch (Exception e ) {
            System.out.println("Error while trying to fetch information using the Github API");
            result = this.getAllCommitsList(owner, repo, branch);
            return result;
        }
    }


    @RequestMapping("/gitApi/repos/{owner}/{repo}/commits")
    public DeferredResult<ResponseEntity<?>> getCommitListPaginationGAPI(@PathVariable(name = "owner", required = true) String owner,
                                                         @PathVariable(name = "repo", required = true) String repo,
                                                         @RequestParam(name = "page", required = true) String page,
                                                         @RequestParam(name = "per_page", required = true, defaultValue = "10") String per_page) {

        String uri = String.format(GITHUB_API, owner, repo) + "/commits?page=" + page + "&per_page=" + per_page;
        DeferredResult<ResponseEntity<?>> result = new DeferredResult<>();

        if (!InputValidation.validatePageInformation(page) || !InputValidation.validatePageInformation(per_page)) {
            result.setResult(new ResponseEntity<>("** ERROR ** The inserted option is not a number. Please try again.", HttpStatus.UNPROCESSABLE_ENTITY));
            return result;
        }

        try {
            MappingJackson2HttpMessageConverter converter = GitLogParser.getCommitConverter();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(0, converter);

            Commit[] commits = restTemplate.getForObject(uri, Commit[].class);

            result.setResult(new ResponseEntity<>(commits, HttpStatus.OK));
            return result;
        } catch (Exception e ) {
            System.out.println("Error while trying to fetch information using the Github API");
            result = this.getCommitListPagination(owner, repo, page, per_page);
            return result;
        }
    }

}
