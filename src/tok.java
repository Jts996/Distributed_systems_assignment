import java.io.Serializable;

/**
 * Advanced Feature 1
 *
 * This is the token object
 *
 * It has been made serializable so it will not reset the token value every time it is instantiate
 * thus allowing for the token to increment with every exchange
 *
 *
 */
public class tok implements Serializable {

    private int token;
    private String[] tokens_list;
    private int[] visits;

    /**
     * Constructor
     *
     * @param t This is the value of the first token
     */
    public tok(int t) {

        this.token = t;

    }

    /**
     * Return the value of the current token
     *
     * @return
     */
    public int getToken() {
        return token;
    }

    /**
     * When called the token is incremented by 1
     *
     * @param t This is the value of the token before it has been incremented
     */
    public void setToken(int t) {
        token = t;
        // Checking that the passed starting token is not negative
        // if it is, inform user and exit program
        if (token < 0) {
            System.out.println("Token cannot be negative");
            System.exit(1);
        } else {
            // If not negative, increment
            token++;
        }
    }


    /**
     * Advanced Feature 5
     *
     * Method to increment the corresponding visit index to the correct node to
     * keep track of the number of visits each node has had
     *
     * @param host      This is the host computer
     * @param id        This is the id that is being visited
     * @return          If it is the second visit, return true, otherwise return false
     */
    public boolean visit_counter(String host, String id) {

        int index;
        boolean second_visit = false;
        String node = "//" + host + ":1099/" + id; // This is the structure of how the nodes are represented in the RMIRegistry for comparison
        for(index = 0; index < tokens_list.length; index++){
            if(node.equals(tokens_list[index])){
                visits[index] += 1; // Increment the value at the index of the ID up by one
                second_visit = visits[index] % 2 == 0; // Checking that the value is even, if even it means its the second visit
            }

        }

        return second_visit;
    }

    /**
     * Advanced Feature 5
     *
     * Method which initialises an array to track the number of visits to each of the nodes
     * in the ring
     *
     * @param nodes     This is an array containing the ID's of all the nodes, this is used to set the length of the visits array
     */
    public void populate_visits(String[] nodes){

        int index;
        this.tokens_list = nodes;
        this.visits = new int[tokens_list.length];

        for (index = 0; index < visits.length; index++){
            visits[index] = 0; // Initialising all the visits to 0 at the start of the ringManger
        }


    }

}
