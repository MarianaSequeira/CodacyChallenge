package codacyChallenge;

import codacyChallenge.model.Commit;
import codacyChallenge.utils.GitLogParser;
import org.junit.jupiter.api.Test;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
public class GitLogParserTest {


    @Test
    void parseGitLogTest() throws IOException {

        Commit commit1 = new Commit("781b629bc7da8f71ac693421268403f3e30db000", "MarianaSequeira", "mfs98@live.com.pt", "Sun Apr 11 14:23:46 2021 +0100", "Creation of Django project");
        Commit commit2 = new Commit("781b629bc7da8f71ac693421268403f3e30db000", "MarianaSequeira", "", "Sun Apr 11 14:23:46 2021 +0100", "Creation of Django project");
        Commit commit3 = new Commit("781b629bc7da8f71ac693421268403f3e30db001", "MarianaSequeira", "mfs98@live.com.pt", "Sun Apr 11 14:23:46 2021 +0100", "Creation of Django project");

        ArrayList<Commit> commitList1 = new ArrayList<>();
        commitList1.add(commit1);

        ArrayList<Commit> commitList2 = new ArrayList<>();
        commitList2.add(commit2);

        ArrayList<Commit> commitList3 = new ArrayList<>();
        commitList3.add(commit1);
        commitList3.add(commit3);

        ArrayList<Commit> whenError = new ArrayList<>();


        BufferedReader buf1 = org.mockito.Mockito.mock(BufferedReader.class);
        when(buf1.readLine())
                .thenReturn("")
                .thenReturn(null);


        BufferedReader buf2 = org.mockito.Mockito.mock(BufferedReader.class);
        when(buf2.readLine())
                .thenReturn("commit 781b629bc7da8f71ac693421268403f3e30db000")
                .thenReturn(null);


        BufferedReader buf3 = org.mockito.Mockito.mock(BufferedReader.class);
        when(buf3.readLine())
                .thenReturn("commit 781b629bc7da8f71ac693421268403f3e30db000")
                .thenReturn("Author: MarianaSequeira <mfs98@live.com.pt>")
                .thenReturn("Date:   Sun Apr 11 14:23:46 2021 +0100")
                .thenReturn("    Creation of Django project")
                .thenReturn(null);


        BufferedReader buf4 = org.mockito.Mockito.mock(BufferedReader.class);
        when(buf4.readLine())
                .thenReturn("commit 781b3e30db000")
                .thenReturn("Author: MarianaSequeira <mfs98@live.com.pt>")
                .thenReturn("Date:   Sun Apr 11 14:23:46 2021 +0100")
                .thenReturn("")
                .thenReturn("    Creation of Django project")
                .thenReturn(null);

        BufferedReader buf5 = org.mockito.Mockito.mock(BufferedReader.class);
        when(buf5.readLine())
                .thenReturn("commit 781b629bc7da8f71ac693421268403f3e30db000")
                .thenReturn("Author: MarianaSequeira")
                .thenReturn("Date:   Sun Apr 11 14:23:46 2021 +0100")
                .thenReturn("")
                .thenReturn("    Creation of Django project")
                .thenReturn(null);


        BufferedReader buf6 = org.mockito.Mockito.mock(BufferedReader.class);
        when(buf6.readLine())
                .thenReturn("commit 781b629bc7da8f71ac693421268403f3e30db000")
                .thenReturn("Author: MarianaSequeira <mfs98@live.com.pt>")
                .thenReturn("Date:   Sun Apr 11 14:23:46 2021 +0100")
                .thenReturn("")
                .thenReturn("    Creation of Django project")
                .thenReturn(null);

        BufferedReader buf7 = org.mockito.Mockito.mock(BufferedReader.class);
        when(buf7.readLine())
                .thenReturn("commit 781b629bc7da8f71ac693421268403f3e30db000")
                .thenReturn("Author: MarianaSequeira <mfs98@live.com.pt>")
                .thenReturn("Date:   Sun Apr 11 14:23:46 2021 +0100")
                .thenReturn("")
                .thenReturn("    Creation of Django project")
                .thenReturn("")
                .thenReturn("commit 781b629bc7da8f71ac693421268403f3e30db001")
                .thenReturn("Author: MarianaSequeira ")
                .thenReturn("Date:   Sun Apr 11 14:23:46 2021 +0100")
                .thenReturn("")
                .thenReturn("    Creation of Django project")
                .thenReturn(null);



        assertThat(GitLogParser.parseGitLog(buf1).equals(whenError)).isEqualTo(true);
        assertThat(GitLogParser.parseGitLog(buf2).equals(whenError)).isEqualTo(true);
        assertThat(GitLogParser.parseGitLog(buf3).equals(whenError)).isEqualTo(true);
        assertThat(GitLogParser.parseGitLog(buf4).equals(whenError)).isEqualTo(true);
        assertThat(GitLogParser.parseGitLog(buf6).equals(commitList1)).isEqualTo(true);
        assertThat(GitLogParser.parseGitLog(buf7).equals(commitList3)).isEqualTo(true);

    }

}
