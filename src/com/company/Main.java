package com.company;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: CalabashJavaDriver serverURL");
            return;
        }

        String url = args[0];
        System.out.println("Connecting to DeviceAgent at " + url);
    }
}
