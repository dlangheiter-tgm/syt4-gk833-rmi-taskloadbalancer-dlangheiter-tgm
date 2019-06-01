package compute;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Loadbalancer extends Remote {

    void register(Compute c) throws RemoteException;
    void unregister(Compute c) throws RemoteException;

}
