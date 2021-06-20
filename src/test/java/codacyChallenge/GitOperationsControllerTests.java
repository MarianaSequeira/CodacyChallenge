package codacyChallenge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@WebMvcTest(GitOperationsController.class)
public class GitOperationsControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

//    @Autowired
//    private MockMvc mockMvc;


    String url = "https://github.com/%s/%s";
//
//    @MockBean
//    GitHandler gitHandler;


    @Test
    public void greetingShouldReturnDefaultMessage() throws Exception {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/codacyChallenge/",
                String.class)).contains("Let's go!");
    }


    @Test
    public void cloneRepositoryTest() throws Exception {

        // Testing a nonexistent repository

        String owner = "x";
        String repo = "x";
        String webURL = String.format(url, owner, repo);

        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/codacyChallenge/api/repos/"+owner+"/"+repo+"/clone",
                String.class)).contains("Error while cloning repository with Web URL " + webURL);

        /*
            I'm aware that this test is not valid, but at the time I don't know how to simulate a "timeout" response.
            I tried multiple methods to implement the test, including using EasyMock, McvResult but occurred a error related to beans manage process.

            To try to test the response I just used a repository that I know that will take time to clone.
            I'm adding not predicted behaviour to this test. I will leave it like this for now, but will definitely work to improve
            my capability of develop good and resilient tests.
         */


        owner = "torvalds";
        repo = "linux";
        webURL = String.format(url, owner, repo);

        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/codacyChallenge/api/repos/"+owner+"/"+repo+"/clone",
                String.class)).contains("Repository is cloning.. Just wait a little bit more!");

//        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();

//        doNothing().when(gitHandler).handleCloneDependentRequests(output, owner, repo, "", "", "", Operation.CLONE);

//        GitHandler gitHandlerMock = mock(GitHandler.class);
//
//        gitHandlerMock.handleCloneDependentRequests(EasyMock.anyObject(DeferredResult.class), owner, repo, "", "", "", Operation.CLONE);
//
//        EasyMock.expectLastCall()
//                .andAnswer(() -> {
//                    Thread.sleep(30000);
//                    return null;
//                });
//
//        EasyMock.replay(gitHandlerMock);


//        MvcResult mvcResult = mockMvc.perform(get("/codacyChallenge/api/repos/"+owner+"/"+repo+"/clone"))
//                .andExpect(request().asyncStarted())
//                .andReturn();
//
//        mvcResult.getAsyncResult();
//
//        mockMvc
//                .perform(asyncDispatch(mvcResult))
//                .andExpect(status().isOk());
    }


    /*
        Given the short amount of time I had free for this project (crazy work week at university) the tests have fallen behind the rest of the code.

        I could try harder to implemented but at the end I will still be with no idea if my final tests were indeed strong and resilient.
        Because of that I decided that it would be better to invest my time in increasing my knowledge
        of correct test implementation than pushing tests to be implemented, quite possibly internalizing incorrect strategies.
     */
}
