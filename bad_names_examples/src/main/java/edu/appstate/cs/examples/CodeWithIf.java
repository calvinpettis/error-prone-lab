package edu.appstate.cs.examples;

public class CodeWithIf {
  public static String method() {
    if (true) {
      System.out.println("Hello, program!");
    }

    if (Math.random() < 0.5) {
      System.out.println("Hello, program2");
    } else {
      System.out.println("nessed up");
    }

    if (false) {
      System.out.println("I am the greatest programmer to ever live");
    }
    return null;
  }
}
