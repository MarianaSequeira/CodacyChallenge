package codacyChallenge;

import codacyChallenge.commandLineInteraction.CommandLineReader;
import codacyChallenge.utils.InputValidation;
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
        assertThat(InputValidation.validateWebURL("")).isEqualTo(false);
        assertThat(InputValidation.validateWebURL("ups")).isEqualTo(false);
        assertThat(InputValidation.validateWebURL("github.com/MarianaSequeira/CatsPrivateRepo.git")).isEqualTo(false);
        assertThat(InputValidation.validateWebURL("https://CatsPrivateRepo.git")).isEqualTo(false);
        assertThat(InputValidation.validateWebURL("https://MarianaSequeira/CatsPrivateRepo.git")).isEqualTo(false);
        assertThat(InputValidation.validateWebURL("ssh://github.com/MarianaSequeira/CatsPrivateRepo.git")).isEqualTo(false);

        assertThat(InputValidation.validateWebURL("https://github.com/MarianaSequeira/CatsPrivateRepo.git")).isEqualTo(true);
    }

    @Test
    void validateNumberOfBranchTest() {
        assertThat(InputValidation.validateNumberOfBranch("a", 2)).isEqualTo(false);
        assertThat(InputValidation.validateNumberOfBranch("3", 2)).isEqualTo(false);
        assertThat(InputValidation.validateNumberOfBranch("2", 2)).isEqualTo(false);
        assertThat(InputValidation.validateNumberOfBranch("1", 2)).isEqualTo(true);
        assertThat(InputValidation.validateNumberOfBranch("0", 2)).isEqualTo(true);

    }





}
