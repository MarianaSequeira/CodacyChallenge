package codacyChallenge;

import codacyChallenge.commandLineInteraction.CommandLineReader;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
public class CommandLineReaderTest {

    @InjectMocks
    @Spy
    private CommandLineReader commandLineReader;


    @Test
    void commandLineReaderRunTest() {
        // TODO
    }

    @Test
    void validateWebURLTest() {
        assertThat(commandLineReader.validateWebURL("")).isEqualTo(false);
        assertThat(commandLineReader.validateWebURL("ups")).isEqualTo(false);
        assertThat(commandLineReader.validateWebURL("github.com/MarianaSequeira/CatsPrivateRepo.git")).isEqualTo(false);
        assertThat(commandLineReader.validateWebURL("https://CatsPrivateRepo.git")).isEqualTo(false);
        assertThat(commandLineReader.validateWebURL("https://MarianaSequeira/CatsPrivateRepo.git")).isEqualTo(false);
        assertThat(commandLineReader.validateWebURL("ssh://github.com/MarianaSequeira/CatsPrivateRepo.git")).isEqualTo(false);

        assertThat(commandLineReader.validateWebURL("https://github.com/MarianaSequeira/CatsPrivateRepo.git")).isEqualTo(true);
    }

    @Test
    void validateNumberOfBranchTest() {
        assertThat(commandLineReader.validateNumberOfBranch("a", 2)).isEqualTo(false);
        assertThat(commandLineReader.validateNumberOfBranch("3", 2)).isEqualTo(false);
        assertThat(commandLineReader.validateNumberOfBranch("2", 2)).isEqualTo(false);
        assertThat(commandLineReader.validateNumberOfBranch("1", 2)).isEqualTo(true);
        assertThat(commandLineReader.validateNumberOfBranch("0", 2)).isEqualTo(true);

    }





}
