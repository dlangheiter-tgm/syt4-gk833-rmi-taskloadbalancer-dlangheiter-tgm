package server;

import compute.Compute;
import compute.Loadbalancer;
import compute.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ComputeServer implements Compute {

    private static final int REGISTRY_PORT = 1099;
    private static final String COMPUTE_STUB_NAME = "Compute";
    private static final String CMD_EXIT = "exit";

    @Override
    public <T> T executeTask(Task<T> t) {
        System.out.println("Execute task: " + t.toString());
        return t.execute();
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        Loadbalancer lb;
        Compute compute = new ComputeServer();

        try {
            Registry registry = LocateRegistry.getRegistry(REGISTRY_PORT);
            lb = (Loadbalancer) registry.lookup(COMPUTE_STUB_NAME);

            Compute computeStub = (Compute) UnicastRemoteObject.exportObject(compute, 0);
            lb.register(computeStub);

            System.out.println("ComputeServer registered");
        } catch (Exception e) {
            System.err.println("ComputeServer exception:");
            e.printStackTrace();
            System.exit(-1);
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String cmd = "";
        System.out.print("> ");
        try {
            while ((cmd = reader.readLine()) != null) {
                cmd = cmd.toLowerCase();
                if(cmd.startsWith(CMD_EXIT)) {
                    System.out.println("Exiting program...");
                    lb.unregister(compute);
                    System.exit(0);
                } else {
                    System.out.println("Unknown command");
                }
                System.out.print("> ");
            }
        } catch (IOException e) {
            System.out.println("Error reading output stream.");
            e.printStackTrace();
            System.exit(-2);
        }
    }
}
