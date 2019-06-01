/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import com.sun.xml.internal.ws.api.pipe.Engine;
import compute.Compute;
import compute.Loadbalancer;
import compute.Task;

public class ComputeEngine implements Compute, Loadbalancer {

    private static final int REGISTRY_PORT = 1099;
    private static final String COMPUTE_STUB_NAME = "Compute";
    private static final String CMD_EXIT = "exit";

    private List<Compute> computes;
    private int computeIndex;

    public ComputeEngine() {
        super();
        this.computes = new ArrayList<>();
        this.computeIndex = 0;
    }

    private Compute getCurrentCompute() {
        this.computeIndex++;
        if(this.computeIndex >= this.computes.size()) {
            this.computeIndex = 0;
        }
        return computes.get(computeIndex);
    }

    public <T> T executeTask(Task<T> t) throws RemoteException {
        if(this.computes.size() == 0) {
            throw new IllegalStateException("No registered Server to handle executeTask.");
        }
        return getCurrentCompute().executeTask(t);
    }

    @Override
    public void register(Compute c) {
        System.out.println("ComputerServer registered");
        if(computes.contains(c)) {
            return;
        }
        computes.add(c);
    }

    @Override
    public void unregister(Compute c) {
        System.out.println("ComputerServer unregistered");
        computes.remove(c);
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        Registry registry;

        try {
            ComputeEngine computeEngine = new ComputeEngine();
            Compute computeStub =
                (Compute) UnicastRemoteObject.exportObject(computeEngine, 0);
            registry = LocateRegistry.createRegistry(REGISTRY_PORT);
            registry.rebind(COMPUTE_STUB_NAME, computeStub);
            System.out.println("ComputeEngine bound");
        } catch (Exception e) {
            System.err.println("ComputeEngine exception:");
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
                    registry.unbind(COMPUTE_STUB_NAME);
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
        } catch (NotBoundException e) {
            System.out.println("Error unbinding Stub.");
            e.printStackTrace();
            System.exit(-3);
        }
    }
}
