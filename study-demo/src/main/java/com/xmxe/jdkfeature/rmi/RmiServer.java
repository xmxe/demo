package com.xmxe.jdkfeature.rmi;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * 继承UnicastRemoteObject父类并实现自定义的远程接口
 * 因为UnicastRemoteObject的构造方法抛出了RemoteException异常,因此这里默认的构造方法必须写,必须声明抛出RemoteException异常
 */
public class RmiServer extends UnicastRemoteObject implements RMI {

    protected RmiServer() throws RemoteException {
        super();
    }

    private static final long serialVersionUID = -4430475342896979447L;

    @Override
    public String helloWorld() {
        return "hello rmi";
    }

}

/**
 * 定义一个远程接口,必须继承Remote接口,其中需要远程调用的方法必须抛出RemoteException异常
 */
interface RMI extends Remote {

    String helloWorld() throws RemoteException;
}

class Main {
    public static void main(String[] args) {
        // 当远程接口实现类继承了UnicastRemoteObject类时,使用方式1注册
        方式1();
        // 当远程接口实现类并没有继承UnicastRemoteObject类时,使用方式2注册
        方式2();
    }

    public static void 方式1() {
        try {
            // 创建一个远程对象
            RMI rmi = new RmiServer();
            // 远程主机远程对象注册表Registry的实例,并指定端口为8888,这一步必不可少（Java默认端口是1099）,
            // 必不可缺的一步,缺少注册表创建,则无法绑定对象到远程注册表上
            LocateRegistry.createRegistry(8888);

            // 把远程对象注册到RMI注册服务器上,并命名为RHello
            // 绑定的URL标准格式为：rmi://host:port/name(其中协议名可以省略,下面两种写法都是正确的）
            Naming.bind("rmi://localhost:8888/RmiHello",rmi);
            // Naming.bind("//localhost:8888/RmiHello",rmiHello);

            // 必须捕获这三个异常,否则需要在main方法中抛出
        } catch (RemoteException e) {
            System.out.println("创建远程对象发生异常");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.out.println("URL畸形异常");
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            System.out.println("重复绑定对象异常");
            e.printStackTrace();
        }
    }

    public static void 方式2() {
        try {
            RMI rmi = (RMI) UnicastRemoteObject.exportObject(new RmiServer(), 0);
            Registry registry = LocateRegistry.createRegistry(2001);
            registry.rebind("test", rmi);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}