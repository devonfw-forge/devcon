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
package com.devonfw.devcon.common.exception;

/**
 * Exception thrown when the environment is in an unknown or invalid state. For example: a missing global environment
 * variable.
 *
 * @author ivanderk
 * @since 0.0.1
 */
public class InvalidEnvironentException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Exception thrown when the environment is in an unknown or invalid state. For example: a missing global environment
   * variable
   *
   * @param msg Text to denote exception
   */
  public InvalidEnvironentException(String msg) {
    super(msg);
  }

  /**
   * Exception thrown when the environment is in an unknown or invalid state. For example: a missing global environment
   * variable
   *
   * @param msg original Exception
   */
  public InvalidEnvironentException(Exception err) {
    super(err);
  }

}
