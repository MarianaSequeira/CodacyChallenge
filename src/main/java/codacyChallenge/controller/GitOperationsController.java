package codacyChallenge.controller;

import codacyChallenge.model.Commit;
import codacyChallenge.utils.CommitDeserializer;
import codacyChallenge.utils.GitHandler;
import codacyChallenge.utils.GitLogParser;
import codacyChallenge.utils.Operation;
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


    //https://api.github.com/repos/MarianaSequeira/COEUS/commits?sha=dev
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


    @RequestMapping(value = {"/api/repos/{owner}/{repo}/commits"})
    public DeferredResult<ResponseEntity<?>>  getCommitListPagination(@PathVariable(name = "owner", required = true) String owner,
                                                          @PathVariable(name = "repo", required = true) String repo,
                                                          @RequestParam(name = "page", required = true) String page,
                                                          @RequestParam(name = "per_page", required = true, defaultValue = "10") String per_page) {

        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();

        this.gitHandler.handleCloneDependentRequests(output, owner, repo, "", per_page, page, Operation.LIST_COMMITS_PAGE);

        output.onTimeout(() ->
                output.setErrorResult(
                        ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                                .body("We are processing your request.")));

        return output;
    }



    @RequestMapping(value = {"/gitApi/repos/{owner}/{repo}/commits"})
    public DeferredResult<ResponseEntity<?>> getAllCommitsListGAPI(@PathVariable(name = "owner", required = true) String owner,
                                                                   @PathVariable(name = "repo", required = true) String repo,
                                                                   @RequestParam(name = "sha", required = false, defaultValue = "") String branch) {

        String uri = String.format(GITHUB_API, owner, repo) + "/commits?sha=" + branch;
        DeferredResult<ResponseEntity<?>> result = new DeferredResult<>();
        try {
            MappingJackson2HttpMessageConverter converter = GitLogParser.getConverter();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(0, converter);

            result.setResult(new ResponseEntity<>(restTemplate.getForObject(uri, Commit[].class), HttpStatus.OK));

        } catch (Exception e ) {
            System.out.println("Error while trying to fetch information using the Github API");
            result = this.getAllCommitsList(owner, repo, branch);
            return result;
        }

        return result;
    }



    @RequestMapping(value = {"/gitApi/repos/{owner}/{repo}/commits"})
    public DeferredResult<ResponseEntity<?>> getCommitListPaginationGAPI(@PathVariable(name = "owner", required = true) String owner,
                                                         @PathVariable(name = "repo", required = true) String repo,
                                                         @RequestParam(name = "page", required = true) String page,
                                                         @RequestParam(name = "per_page", required = true, defaultValue = "10") String per_page) {

        String uri = String.format(GITHUB_API, owner, repo) + "/commits?page=" + page + "&per_page=" + per_page;
        DeferredResult<ResponseEntity<?>> result = new DeferredResult<>();
        try {
            MappingJackson2HttpMessageConverter converter = GitLogParser.getConverter();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(0, converter);

            result.setResult(new ResponseEntity<>(restTemplate.getForObject(uri, Commit[].class), HttpStatus.OK));

        } catch (Exception e ) {
            System.out.println("Error while trying to fetch information using the Github API");
            result = this.getCommitListPagination(owner, repo, page, per_page);
            return result;
        }

        return result;
    }

    // TODO;
    //      - implementar os dois paths;
    //      - validar o input;
    //      - testes!!!!!!
}
