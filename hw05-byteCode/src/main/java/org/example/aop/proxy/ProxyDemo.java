package org.example.aop.proxy;

public class ProxyDemo {
    public static void main(String[] args) {
        MyClassInterface myClass = (MyClassInterface) Ioc.createMyClass();
        myClass.secureAccess("Security Param");
    }
}
