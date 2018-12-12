import java.rmi.*;
import java.net.* ;
 
public class ringMemberImpl extends java.rmi.server.UnicastRemoteObject implements ringMember 
{
    public ringMemberImpl(String  t_node, String  t_id, String  n_node, String  n_id) throws RemoteException {
        this_host = t_node ;
        this_id = t_id ;
        next_host = n_node ;
        next_id = n_id ;
    }
   
    public synchronized void takeToken(String filename, tok token, String id_longer_CS, String skipped_node_id) throws RemoteException {
        // start critical section by instantiating and starting critical section thread
        System.out.println("Entered method: takeToken ringMemberImpl");
        c = new criticalSection(this_host, this_id, next_host, next_id, filename, token, id_longer_CS, skipped_node_id);
	    Thread cThread = new Thread(c);
	    cThread.start();
	    System.out.println("Exiting method: takeToken ringMemberImpl");
	    System.out.println("Token received: entering critical region");
    }


    public static void main(String argv[]) {
        System.setSecurityManager(new SecurityManager());

        // Checking that the required number of arguments have been input
        //  Exit program if not
        if(argv.length != 4){
            System.out.println("usage: [host] <id> [next host] <next id>");
            System.exit(1);
        }

        // Extracting the arguments
        String host = argv[0];
        String id = argv[1];
        String nHost = argv[2];
        String nID = argv[3];

        // instantiate ringMemberImpl class with appropriate parameters
        // register object with RMI registry
        try{
            ringMember member = new ringMemberImpl(host, id, nHost, nID);
            Naming.bind("//127.0.0.1/" + id, member);
            System.out.println("------------------------------------------------------");
            System.out.println("Ring node member " + id +" is bound with RMIRegistry");
            System.out.println("------------------------------------------------------");
            System.out.println(" ");
        }catch(Exception e){
            System.err.println(e);
        }
    }
   
    private String	next_id;
    private String  next_host;
    private String	this_id;
    private String  this_host;
    private criticalSection	c;
 }