package com.xamarin.utils;

import edu.umd.cs.findbugs.annotations.NonNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellCommand {
    private String[] cmd;
    private boolean wait = true; //Sync by default

    public static String shell(String[] cmd) {
        return new ShellCommand(cmd).execute();
    }

    public static void asyncShell(String[] cmd) {
        new ShellCommand(cmd, false).execute();
    }

    public ShellCommand(@NonNull String[] cmd) {
        this(cmd, true);
    }

    public ShellCommand(@NonNull String[] cmd, boolean wait) {
        this.cmd = cmd;
        this.wait = wait;
    }

    public static String $HOME() {
        String homeDir = System.getenv("HOME");
        return homeDir;
    }

    public String execute() {
        System.out.print("$ ");
        for (String aCmd : this.cmd) {
            System.out.print(aCmd + " ");
        }
        System.out.println();
        try {
            final Process p = Runtime.getRuntime().exec(this.cmd);
            if (this.wait) {
                p.waitFor();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));

                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                String resp = sb.toString();
                System.out.println("==> " + resp);
                return resp;

            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            p.waitFor();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
