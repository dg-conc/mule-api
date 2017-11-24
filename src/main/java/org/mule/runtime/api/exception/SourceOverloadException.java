/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.api.exception;

import java.util.concurrent.RejectedExecutionException;

/**
 * Exception thrown when a {@link RejectedExecutionException} was encountered in the source, before entering the flow.
 *
 * @since 1.1
 */
public class SourceOverloadException extends RejectedExecutionException {

  private static final long serialVersionUID = 2992038225993918910L;

  /**
   * Constructs a new exception with the specified message.
   *
   * @param message the detail message
   */
  public SourceOverloadException(String message) {
    super(message);
  }

  /**
   * Constructs a new exception with the specified cause.
   *
   * @param cause the exception cause
   */
  public SourceOverloadException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new exception with the specified message and cause.
   *
   * @param message the detail message
   * @param cause the exception cause
   */
  public SourceOverloadException(String message, Throwable cause) {
    super(message, cause);
  }
}