import java.io.*;
import java.rmi.*;
import java.util.*;

public class criticalSection extends Thread {

    public criticalSection(String t_host, String t_id, String n_host, String n_id, String file, tok token, String id_longer_CS, String skipped) {
        
        this_host =t_host;
        this_id = t_id;
        next_host = n_host;
        next_id = n_id;
        this.file = file; // User specified file name
        this.token = token; // The token object
        this.rounds = 10; // Specified number of exchanges
        this.id_longer_CS = id_longer_CS; // Node to have longer in Critical section
        this.skipped_node_id = skipped; // node to be skipped

        if(skipped_node_id != null){
            this.skipping = true;
        }else{
            this.skipping = false;
        }

        try {
            //Finding how many nodes are in the ring
            this.listOfNodes = Naming.list("rmi://" + this_host); //Array holding a list of the nodes
            this.numOfNodes = listOfNodes.length; // variable which gets the length of this array
        }catch (Exception e){
            System.err.println(e);
        }
    }

    public void run() {

        int numOfToken = token.getToken();
        if(skipping){
            if (this_id.equals(skipped_node_id)) {
                boolean second_visit = token.visit_counter(this_host, this_id);
                if (second_visit) {
                    System.out.println("Second visit, moving to next node. Skipping node: " + this_id);
                    release(next_host, next_id, numOfToken);
                } else {
                    action(numOfToken);
                }
            } else {
                action(numOfToken);
            }
        }else{
            action(numOfToken);
        }

    }


    /**
     * Method to carry out the desired output to the file
     *
     * @param token_num  This is the value of the current token being passed
     */
    public void action(int token_num){
        // sleep to symbolise critical section duration
        try{
            if(this_id.equals(id_longer_CS)){
                System.out.println("Node selected to have a longer Critical section: " + this_id);
                Thread.sleep(5000); // Longer sleep for node that gets longer in Critical section
            }
            else {
                Thread.sleep(3000); // Default sleep
            }
        }catch (InterruptedException e){
            System.out.println("Sleep Failed: " + e);
        }
        // write timestamp (date) to file
        try{

            System.out.println("Writing to file: " + file);
            // Create a new date instance
            Date timeStmp = new Date();
            // Convert this date object into a string for printing to the shared file
            String timeStamp = timeStmp.toString();

            // create fileWriter - true means append to end of file
            FileWriter fw_id = new FileWriter(file, true);
            // create printWriter - true means flush buffer on each println
            PrintWriter pw_id = new PrintWriter(fw_id, true);
            pw_id.println("Record from ring node on host " + this_host + ", id: " + this_id +
                    ", is: " + timeStamp +", Token: " + token_num);

            pw_id.close();
            fw_id.close();
            token.setToken(token_num);
            System.out.println("Finished writing");

        }catch (IOException e){
            System.out.println(e);
        }
        // get remote reference to next ring element, and pass token on ...
        release(next_host, next_id, token_num);
    }

    /**
     * Method which handles the exchange of the token between processes
     *
     * @param host      Next host in the ring
     * @param id        Next id in the ring
     * @param tokenNum  Current token number
     */
    public void release(String host, String id, int tokenNum){
        ringMember member;

        if(skipping){
            numOfNodes = numOfNodes - 1;
        }

        // Checking that the token is under the specified number of exchanges
        if(tokenNum < rounds){
            try{
                // checking that there are enough exchanges left for the node to be able have another critical Section
                // if not, it will start the next process but this process will be terminated
                if(tokenNum > (rounds - numOfNodes)){
                    System.out.println("Look up RMIRegistry with: rmi://localhost/"+id);
                    System.out.println("Connecting to next host with id: " + id);
                    // Finding the next node on the host by using its id
                    member = (ringMember)Naming.lookup("rmi://"+host+"/"+id);
                    System.out.println("Received Token count value is: " + tokenNum);
                    System.out.println("Token Release: Exiting Critical region");
                    System.out.println("------------------------------------------------------");
                    System.out.println(" ");
                    member.takeToken(file, token, id_longer_CS, skipped_node_id);
                    System.out.println("Finished");
                    System.exit(1); // Advanced feature 6: Clean up in an orderly fashion
                }

                System.out.println("Look up RMIRegistry with: rmi://localhost/"+id);
                System.out.println("Connecting to next host with id: " + id);
                // Finding the next node on the host by using its id
                member = (ringMember)Naming.lookup("rmi://"+host+"/"+id);
                System.out.println("Received Token count value is: " + tokenNum);
                System.out.println("Token Release: Exiting Critical region");
                System.out.println("------------------------------------------------------");
                System.out.println(" ");
                member.takeToken(file, token, id_longer_CS, skipped_node_id);


            }catch(Exception e){
                System.out.println(e);
            }
        }else{
            System.out.println("Received Token count value is: " + tokenNum);
            System.out.println("Token Release: Exiting Critical region");
            System.out.println("------------------------------------------------------");
            System.out.println(" ");
            // Once the number of exchanges is reached
            // Finish and exit program
            System.out.println("Finished");
            System.exit(1); // Advanced feature 6: Clean up in an orderly fashion
        }

    }


    private String  this_id;
    private String  this_host;
    private String  next_id;
    private String  next_host;
    private String file;
    private tok token;
    private int rounds;
    private String id_longer_CS;
    private int numOfNodes;
    private String[] listOfNodes;
    private String skipped_node_id;
    private boolean skipping;
}