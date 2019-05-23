public class Main {

    public static void main(String[] args) {
        if (args.length!=1){
            System.out.println("Please specify port number");
            System.exit(-1);
        }
        Server server = new Server(Integer.parseInt(args[0]));

        server.go();

	// write your code here
    }
}
