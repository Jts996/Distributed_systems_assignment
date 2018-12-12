import java.rmi.*;
import java.net.*;
import java.io.*;

public class ringManager {

    private static String id_longer_CS; // id of node user has specified to have longer in Critical section
    private static String filename; // File user wants to use as shared resource
    private static String skipped_node_id; // ID of the node the user wants to skip every second visit

    public ringManager(String ring_node_host, String ring_node_id)   {
       System.setSecurityManager(new SecurityManager());

        initialise_resource(); // Create or clear file
        inject_token(ring_node_host, ring_node_id); // Inject token into the ring to start

   }

    /**
     * Method which creates the first the shared resource: file
     *
     *
     */
   public void initialise_resource(){
       // create fileWriter and clear file
       try{
           // To set filename to default if user doesn't specify one
           if(filename == null){
               filename = "record.txt";
           }
           System.out.println("Clearing File: " + filename);
           // Creating the shared resource - a File
           // create fileWriter - true means don't append to end of file
           FileWriter fw_id = new FileWriter(filename, false);
           fw_id.close();
       }catch(IOException e){
           System.out.println("Exception in clearing the file: " + e);
       }
   }

    /**
     * Method to start the ring by "injecting" the token into the ring
     *
     * This means connect to the first node which the user has specified
     *
     * @param node_host     This is the host of the first node
     * @param node_id       This is the ID of the first node
     */
   public void inject_token(String node_host, String node_id){
       // get remote reference to ring element/node and inject token by calling takeToken()
       System.out.println("Connecting to Node");
       try{
           String[] listOfNodes = Naming.list("rmi://" + node_host);
           //Creating the token instance
           int t = 0;
           tok token = new tok(t);
           token.populate_visits(listOfNodes); // Populate an array to keep track of the number of visits to each node
           //Setting the first token for the first exchange
           token.setToken(t);
           ringMember member = (ringMember) Naming.lookup("rmi://" + node_host + "/" + node_id);
           member.takeToken(filename, token, id_longer_CS, skipped_node_id);
           System.out.println("Connected to: " + node_id);

       }catch (Exception e){
           System.out.println(e);
       }

   }

    /**
     * Each of the if statements adds future functionality
     *
     *
     * If not looking to use a feature but want to use on the is more advanced, put null as the argument
     *
     * @param argv      User entered arguments from the terminal
     */
   public static void main(String argv[])
   {
	   
	    // instantiate ringManager with parameters

        //Checking that the required number of arguments have been input
        if (argv.length < 2){
            System.out.println("Usage: [host] <id> filename");
            System.exit(1);

        // Basic functionality
        }else if(argv.length == 2){

            //Extracting the arguments
            String host = argv[0];
            String id = argv[1];

            // Creating instance of ringManager
            ringManager manager = new ringManager(host, id);

        // Advanced feature 2: user specified filename
        }else if(argv.length == 3){

            //Extracting the arguments
            String host = argv[0];
            String id = argv[1];
            filename = argv[2];

            // Creating instance of ringManager
            ringManager manager = new ringManager(host, id);

            // Advanced feature 4: user specify a node to have a longer critical section
        }else if(argv.length == 4){

            //Extracting the arguments
            String host = argv[0];
            String id = argv[1];
            filename = argv[2];
            id_longer_CS = argv[3];

            // Creating instance of ringManager
            ringManager manager = new ringManager(host, id);

        // Advanced feature 5: user specify a node to be skipped every second visit
        }else if(argv.length == 5){

            //Extracting the arguments
            String host = argv[0];
            String id = argv[1];
            filename = argv[2];
            id_longer_CS = argv[3];
            skipped_node_id = argv[4];

            // Creating instance of ringManager
            ringManager manager = new ringManager(host, id);
        }
   }
}  