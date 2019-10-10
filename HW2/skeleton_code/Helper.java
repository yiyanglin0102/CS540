public class Helper {

    /**
     * Class constructor.
     */
    private Helper() {

    }

    /**
     * This method is used to check if a number is prime or not
     *
     * @param number A positive integer number
     * @return boolean True if x is prime; Otherwise, false
     */
    public static boolean isPrime(int number) {
        int factors = 0;
        int n = 1;

        while (n <= number) {
            if (number % n == 0) {
                factors++;
            }
            n++;
        }
        return (factors == 2);
    }


    /**
     * This method is used to get the largest prime factor
     *
     * @param number A positive integer number
     * @return int The largest prime factor of x
     */
    public static int getLargestPrimeFactor(int number) {
        if (number <= 1) {
            return 0;
        } else {
            int divide = 2;
            while (divide < number) {
                if (number % divide != 0) {
					divide++;
                } else {
                    number = number / divide;
					divide = 2;
                }
            }
            return number;
        }
    }
}