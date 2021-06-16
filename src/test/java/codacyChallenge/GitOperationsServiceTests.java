package codacyChallenge;

import codacyChallenge.model.Commit;
import codacyChallenge.service.GitOperationsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.io.*;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
public class GitOperationsServiceTests {

    @InjectMocks
    @Spy
    private GitOperationsService gitOperationsService;


    @Test
    void cloneRepositoryOnlineTest() {
        // Test with invalid Web URL
        assertThat(gitOperationsService.cloneRepository("ups")).isEqualTo(false);

        // Test with valid Web URL of private repository
        assertThat(gitOperationsService.cloneRepository("https://github.com/MarianaSequeira/CatsPrivateRepo.git")).isEqualTo(false);

        // Test with valid Web URL of public repository
        assertThat(gitOperationsService.cloneRepository("https://github.com/MarianaSequeira/CuteDogsPublicRepo.git")).isEqualTo(true);
    }


    @Test
    void cloneRepositoryOfflineTest() throws IOException {
        BufferedReader bufferedReader = org.mockito.Mockito.mock(BufferedReader.class);
        when(bufferedReader.readLine())
                .thenReturn("")
                .thenReturn(null);

        String webURL = "test";

        Mockito.doReturn(bufferedReader).when(gitOperationsService).executeCommand(String.format("git clone --bare %s .",webURL));

        assertThat(gitOperationsService.cloneRepository(webURL)).isEqualTo(true);
    }


    @Test
    void listBranchesOnlineTest() {

        gitOperationsService.createTempRepoDirectory();
        assertThat(gitOperationsService.listBranches()).isNullOrEmpty();

        gitOperationsService.cloneRepository("https://github.com/MarianaSequeira/CatsPrivateRepo.git");
        assertThat(gitOperationsService.listBranches()).isNullOrEmpty();

        gitOperationsService.cloneRepository("https://github.com/MarianaSequeira/CuteDogsPublicRepo.git");
        ArrayList<String> branches = new ArrayList<>();
        branches.add("main");

        assertThat(gitOperationsService.listBranches()).isEqualTo(branches);
    }


    @Test
    void listBranchesOfflineTest() throws IOException {
        BufferedReader bufferedReader = org.mockito.Mockito.mock(BufferedReader.class);
        when(bufferedReader.readLine())
                .thenReturn("* main")
                .thenReturn(null);

        Mockito.doReturn(bufferedReader).when(gitOperationsService).executeCommand("git branch -a");

        ArrayList<String> branches = new ArrayList<>();
        branches.add("main");

        assertThat(gitOperationsService.listBranches().equals(branches));
    }


    @Test
    void getListOfCommitsTest() throws IOException {

        Commit commit = new Commit("781b629bc7da8f71ac693421268403f3e30db000", "MarianaSequeira", "mfs98@live.com.pt", "Sun Apr 11 14:23:46 2021 +0100", "Creation of Django project");
        ArrayList<Commit> commitList = new ArrayList<>();
        commitList.add(commit);

        BufferedReader bufferedReader = org.mockito.Mockito.mock(BufferedReader.class);
        when(bufferedReader.readLine())
                .thenReturn("commit 781b629bc7da8f71ac693421268403f3e30db000")
                .thenReturn("Author: MarianaSequeira <mfs98@live.com.pt>")
                .thenReturn("Date:   Sun Apr 11 14:23:46 2021 +0100")
                .thenReturn("")
                .thenReturn("    Creation of Django project")
                .thenReturn(null);


        String branch = "";
        Mockito.doReturn(bufferedReader).when(gitOperationsService).executeCommand(String.format("git log %s", branch));

        assertThat(gitOperationsService.getListOfCommits(branch).equals(commitList)).isEqualTo(true);
    }


    @Test
    void createTempRepoDirectoryTest() {
        // TODO
    }

    @Test
    void executeCommandTest() {
        // TODO
    }
}
