package codacyChallenge.utils;


public class InputValidation {


    /*
        Method to allow to validate Web URL inserted by the user.
        //TODO: Think of more validations
     */
    public static boolean validateWebURL(String webURL) {

        if (!webURL.startsWith("http") || !webURL.contains("//github.com") || webURL.contains("\t")) {
            System.out.println("\t\t** ERROR ** The Web URL inserted is not valid. Please try again.");
            return false;
        }
        return true;
    }


    /*
        Method to allow to validate the number of the branch inserted by the user.
        //TODO: Think of more validations
     */
    public static boolean validateNumberOfBranch(String index, int finalSize) {

        try {
            int i = Integer.parseInt(index);

            if ( i < 0 || i >= finalSize ) {
                System.out.println("\t\t** ERROR ** The inserted option is not valid. Please try again.");
                return false;
            }

        } catch (NumberFormatException e) {
            System.out.println("\t\t** ERROR ** The inserted option is not a number. Please try again.");
            return false;
        }

        return true;
    }


    /*
        Method to allow to validate the page information inserted by the user.
        //TODO: Think of more validations
     */
    public static boolean validatePageInformation(String number) {

        try {
            int i = Integer.parseInt(number);

            if ( i < 0) {
                System.out.println("\t\t** ERROR ** The inserted option is not valid. Please try again.");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("\t\t** ERROR ** The inserted option is not a number. Please try again.");
            return false;
        }

        return true;
    }
}
