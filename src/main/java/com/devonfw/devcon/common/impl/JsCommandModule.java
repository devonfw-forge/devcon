package com.devonfw.devcon.common.impl;

/**
 * This is the base class used to instantiate the Javascript executor class
 */
public abstract class JsCommandModule extends AbstractCommandModule {

  public abstract Object exec(String... arguments);

}
