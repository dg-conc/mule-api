/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */


package org.mule.runtime.api.component.location;

import org.mule.runtime.api.meta.AnnotatedObject;

/**
 * Interface for all objects that may have a relationship to an element in the configuration.
 * <p>
 * Common use case of this interface is to be implemented by exceptions that are related to failures in the creation/execution of
 * the component.
 *
 * @since 1.0
 */
public interface ComponentProvider {

  /**
   * @return the element in the configuration.
   */
  AnnotatedObject getComponent();

}