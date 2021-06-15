package codacyChallenge.CommandLineInteraction;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Scanner;

@Component
public class CommandLineReader implements CommandLineRunner {

    @Override
    public void run(String... args)  {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter a git command: ");
        String gitCommand = sc.nextLine();
        System.out.println("\t gitCommand = " + gitCommand);

    }
}
