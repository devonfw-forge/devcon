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
package com.devonfw.devcon.common.api.data;

/**
 * Encapsulated Devcon global command line options (-h, -v)
 *
 * @author pparrado
 */
public class DevconOption {

  private String opt;

  private String longOpt;

  private String description;

  public DevconOption(String opt, String longOpt, String description) {

    this.opt = opt;
    this.longOpt = longOpt;
    this.description = description;
  }

  /**
   * @return opt
   */
  public String getOpt() {

    return this.opt;
  }

  /**
   * @param opt new value of {@link #getopt}.
   */
  public void setOpt(String opt) {

    this.opt = opt;
  }

  /**
   * @return longOpt
   */
  public String getLongOpt() {

    return this.longOpt;
  }

  /**
   * @param longOpt new value of {@link #getlongOpt}.
   */
  public void setLongOpt(String longOpt) {

    this.longOpt = longOpt;
  }

  /**
   * @return description
   */
  public String getDescription() {

    return this.description;
  }

  /**
   * @param description new value of {@link #getdescription}.
   */
  public void setDescription(String description) {

    this.description = description;
  }

}
