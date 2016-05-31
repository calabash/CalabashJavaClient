package com.company;

import com.sun.javafx.beans.annotations.NonNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellCommand {
    private String cmd;
    private boolean wait = true; //Sync by default

    public static String shell(String cmd) {
        return new ShellCommand(cmd).execute();
    }

    public ShellCommand(@NonNull String cmd) {
        this.cmd = cmd;
    }

    public String execute() {
        System.out.println("$ " + this.cmd);
        try {
            Process p = Runtime.getRuntime().exec(this.cmd);
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
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
