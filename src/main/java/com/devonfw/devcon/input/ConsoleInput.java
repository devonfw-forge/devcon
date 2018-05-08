/*******************************************************************************
 * Copyright 2015-2018 Capgemini SE.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package com.devonfw.devcon.input;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Implementqtion of {@link Input} based on the Command line/Console
 *
 * @author ivanderk
 *
 */
public class ConsoleInput implements Input {

  private PrintStream out_;

  private InputStream in_;

  public ConsoleInput() {
    this.in_ = System.in;
    this.out_ = System.out;
  }

  public ConsoleInput(InputStream in, PrintStream out) {
    this();
    this.in_ = in;
    this.out_ = out;
  }

  @Override
  public String promptUser(String msg, String... args) {

    Scanner reader = new Scanner(this.in_);
    this.out_.printf(msg, args);
    return reader.nextLine().trim();
  }

  @Override
  public boolean askForUserConfirmation(String message, String... args) {

    String[] validResponses = { "yes", "y", "no", "n" };
    Scanner reader = new Scanner(this.in_); // new Scanner(System.in);
    this.out_.printf(message, args);
    this.out_.println();
    this.out_.println("Y/N");
    String response = reader.next();
    while (!Arrays.asList(validResponses).contains(response.toLowerCase())) {
      this.out_.println("Please type 'yes' or 'no'");
      response = reader.next();
    }
    if (response.toLowerCase().equals("yes") || response.toLowerCase().equals("y")) {
      return true;
    } else {
      return false;
    }
  }
}
