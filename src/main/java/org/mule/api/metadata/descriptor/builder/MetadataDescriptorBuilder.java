/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.api.metadata.descriptor.builder;


import org.mule.api.metadata.MetadataAware;

/**
 * Builder provider for metadata descriptor builders.
 * This class provides instances of {@link OperationMetadataDescriptorBuilder}, {@link OutputMetadataDescriptorBuilder}
 * and {@link TypeMetadataDescriptorBuilder} to describe the metadata capabilities of a {@link MetadataAware} component
 *
 * @since 1.0
 */
public abstract class MetadataDescriptorBuilder
{

    /**
     * @param name of the operation to describe
     * @return a new instance of {@link OperationMetadataDescriptorBuilder} associated to the operation name
     */
    public static OperationMetadataDescriptorBuilder operationDescriptor(String name)
    {
        return new OperationMetadataDescriptorBuilder(name);
    }

    /**
     * @return a new instance of {@link OutputMetadataDescriptorBuilder} to describe metadata output of a component
     */
    public static OutputMetadataDescriptorBuilder outputDescriptor()
    {
        return new OutputMetadataDescriptorBuilder();
    }

    /**
     * @param name of the component parameter to associate their metadata description
     * @return a new instance of {@link TypeMetadataDescriptorBuilder} associated to the {@param name}.
     */
    public static TypeMetadataDescriptorBuilder typeDescriptor(String name)
    {
        return new TypeMetadataDescriptorBuilder(name);
    }
}