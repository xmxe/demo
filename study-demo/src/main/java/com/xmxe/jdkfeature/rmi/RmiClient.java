package com.xmxe.jdkfeature.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiClient {
    public static void main(String[] args) {
        调用方式1();
        调用方式2();
    }

    public static void 调用方式1() {
        try {
            // 在RMI服务注册表中查找名称为RHello的对象,并调用其上的方法
            RMI rmi = (RMI) Naming.lookup("rmi://localhost:8888/RmiHello");
            System.out.println(rmi.helloWorld());
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void 调用方式2() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 2001);
            RMI rmi = (RMI) registry.lookup("test");
            System.out.println("Client:" + rmi.helloWorld());
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}
