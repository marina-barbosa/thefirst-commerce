package com.gema.thefirst.services.exceptions;

public class ForbiddenException extends RuntimeException {

  public ForbiddenException(String msg) {
    super(msg);
  }
}
