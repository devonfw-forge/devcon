package com.devonfw.devcon.common.api.data;

import com.google.common.base.Optional;

/**
 * Contains info about a command parameter
 *
 * @author pparrado
 */
public class CommandParameter {

  private String name;

  private String description;

  private int position;

  private boolean optional;

  private Optional<String> value;;

  public CommandParameter(String name, String description, int position, boolean isoptional) {

    this.name = name;
    this.description = description;
    this.position = position;
    this.optional = isoptional;
    this.value = Optional.absent();
  }

  public CommandParameter(CommandParameter other) {
    this(other.name, other.description, other.position, other.optional);
  }

  public static CommandParameter copy(CommandParameter other) {

    return new CommandParameter(other);
  }

  /**
   * @return name
   */
  public String getName() {

    return this.name;
  }

  /**
   * @return description
   */
  public String getDescription() {

    return this.description;
  }

  /**
   * @return whether parameter is mandatory or optional
   */
  public boolean isOptional() {

    return this.optional;
  }

  /**
   * @return position
   */
  public int getPosition() {

    return this.position;
  }

  /**
   * @return value
   */
  public Optional<String> getValue() {

    return this.value;
  }

  /**
   * @param value new value of {@link #getvalue}.
   */
  public void setValue(String value) {

    this.value = Optional.of(value);
  }
}
