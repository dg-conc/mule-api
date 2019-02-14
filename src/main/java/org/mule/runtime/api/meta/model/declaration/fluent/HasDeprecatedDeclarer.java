/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.api.meta.model.declaration.fluent;

import org.mule.api.annotation.NoImplement;
import org.mule.runtime.api.meta.model.deprecated.DeprecationModel;

/**
 * Contract interface for a declarer in which it's possible to add
 * {@link DeprecationModel}
 *
 * @since 1.2
 */
@NoImplement
public interface HasDeprecatedDeclarer<T> {

  /**
   * Adds the given {@code stereotype}
   *
   * @param deprecation a {@link DeprecationModel}
   * @return {@code this} declarer
   */
  T withDeprecation(DeprecationModel deprecation);

}