/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.api.metadata.descriptor;

import org.mule.runtime.api.message.Message;

/**
 * Represents the view of all the Metadata associated to an Component's
 * {@link Message} output
 *
 * @since 1.0
 */
public interface OutputMetadataDescriptor {

  /**
   * @return a {@link TypeMetadataDescriptor} that describes the Component's output {@link Message#getPayload}
   */
  TypeMetadataDescriptor getPayloadMetadata();

  /**
   * @return a {@link TypeMetadataDescriptor} that describes Component's output {@link Message#getAttributes}
   */
  TypeMetadataDescriptor getAttributesMetadata();

}
