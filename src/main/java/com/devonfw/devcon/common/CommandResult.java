package com.devonfw.devcon.common;

/**
 * Possible restult states of executed Commands
 *
 * @author ivanderk
 *
 */
public enum CommandResult {

  OK, HELP_SHOWN, UNKNOWN_MODULE, UNKNOWN_COMMAND, MANDATORY_PARAMS_MISSING, UNKNOWN_PARAMS;

  public static String OK_MSG = "OK";
}
