/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.internal.app.declaration.serialization.adapter;

import static org.mule.runtime.api.app.declaration.fluent.ElementDeclarer.newFlow;
import static org.mule.runtime.internal.app.declaration.serialization.adapter.ElementDeclarationSerializationUtils.COMPONENTS;
import static org.mule.runtime.internal.app.declaration.serialization.adapter.ElementDeclarationSerializationUtils.CONFIG;
import static org.mule.runtime.internal.app.declaration.serialization.adapter.ElementDeclarationSerializationUtils.CONNECTION;
import static org.mule.runtime.internal.app.declaration.serialization.adapter.ElementDeclarationSerializationUtils.CONNECTION_FIELD;
import static org.mule.runtime.internal.app.declaration.serialization.adapter.ElementDeclarationSerializationUtils.DECLARING_EXTENSION;
import static org.mule.runtime.internal.app.declaration.serialization.adapter.ElementDeclarationSerializationUtils.FLOW;
import static org.mule.runtime.internal.app.declaration.serialization.adapter.ElementDeclarationSerializationUtils.KIND;
import static org.mule.runtime.internal.app.declaration.serialization.adapter.ElementDeclarationSerializationUtils.NAME;
import static org.mule.runtime.internal.app.declaration.serialization.adapter.ElementDeclarationSerializationUtils.REF_NAME;
import static org.mule.runtime.internal.app.declaration.serialization.adapter.ElementDeclarationSerializationUtils.GLOBAL_PARAMETER;
import static org.mule.runtime.internal.app.declaration.serialization.adapter.ElementDeclarationSerializationUtils.VALUE;
import static org.mule.runtime.internal.app.declaration.serialization.adapter.ElementDeclarationSerializationUtils.declareEnrichableElement;
import static org.mule.runtime.internal.app.declaration.serialization.adapter.ElementDeclarationSerializationUtils.declareParameterizedElement;
import static org.mule.runtime.internal.app.declaration.serialization.adapter.ElementDeclarationSerializationUtils.populateEnrichableObject;
import static org.mule.runtime.internal.app.declaration.serialization.adapter.ElementDeclarationSerializationUtils.populateParameterizedObject;
import org.mule.runtime.api.app.declaration.ComponentElementDeclaration;
import org.mule.runtime.api.app.declaration.ConfigurationElementDeclaration;
import org.mule.runtime.api.app.declaration.ConnectionElementDeclaration;
import org.mule.runtime.api.app.declaration.EnrichableElementDeclaration;
import org.mule.runtime.api.app.declaration.FlowElementDeclaration;
import org.mule.runtime.api.app.declaration.GlobalElementDeclaration;
import org.mule.runtime.api.app.declaration.ParameterValue;
import org.mule.runtime.api.app.declaration.ParameterizedElementDeclaration;
import org.mule.runtime.api.app.declaration.TopLevelParameterDeclaration;
import org.mule.runtime.api.app.declaration.fluent.ConfigurationElementDeclarer;
import org.mule.runtime.api.app.declaration.fluent.ElementDeclarer;
import org.mule.runtime.api.app.declaration.fluent.EnrichableElementDeclarer;
import org.mule.runtime.api.app.declaration.fluent.FlowElementDeclarer;
import org.mule.runtime.api.app.declaration.fluent.ParameterObjectValue;
import org.mule.runtime.api.app.declaration.fluent.ParameterizedElementDeclarer;
import org.mule.runtime.api.app.declaration.fluent.TopLevelParameterDeclarer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * A {@link TypeAdapter} for serializing instances of {@link ComponentElementDeclaration}
 *
 * @since 1.0
 */
class GlobalElementDeclarationTypeAdapter extends TypeAdapter<GlobalElementDeclaration> {

  private final Gson delegate;

  GlobalElementDeclarationTypeAdapter(Gson delegate) {
    this.delegate = delegate;
  }

  @Override
  public void write(JsonWriter out, GlobalElementDeclaration value) throws IOException {
    final String kind = getKind(value);

    out.beginObject();
    if (value instanceof TopLevelParameterDeclaration) {
      populateEnrichableObject(delegate, out, (EnrichableElementDeclaration) value, kind);
      out.name(REF_NAME).value(((TopLevelParameterDeclaration) value).getRefName());
      out.name(VALUE).jsonValue(delegate.toJson(((TopLevelParameterDeclaration) value).getValue(), ParameterValue.class));

    } else {
      populateParameterizedObject(delegate, out, (ParameterizedElementDeclaration) value, kind);

      if (value instanceof ConfigurationElementDeclaration) {
        ConfigurationElementDeclaration config = (ConfigurationElementDeclaration) value;
        out.name(REF_NAME).value(config.getRefName());
        populateConnection(out, config);

      } else if (value instanceof FlowElementDeclaration) {
        out.name(COMPONENTS).jsonValue(delegate.toJson(((FlowElementDeclaration) value).getComponents()));
      }
    }
    out.endObject();
  }

  @Override
  public GlobalElementDeclaration read(JsonReader in) throws IOException {
    final JsonElement parse = new JsonParser().parse(in);
    if (parse.isJsonObject()) {
      JsonObject jsonObject = parse.getAsJsonObject();
      JsonElement elementName = jsonObject.get(NAME);
      JsonElement elementKind = jsonObject.get(KIND);
      JsonElement elementExtension = jsonObject.get(DECLARING_EXTENSION);
      if (elementKind != null && elementExtension != null && elementName != null) {
        EnrichableElementDeclarer declarer = getDeclarer(ElementDeclarer.forExtension(elementExtension.getAsString()),
                                                         elementKind.getAsString(),
                                                         elementName.getAsString());

        if (elementKind.getAsString().equals(GLOBAL_PARAMETER)) {
          declareParameter(jsonObject, declarer);
        } else {
          declareParameterizedElement(delegate, jsonObject, (ParameterizedElementDeclarer) declarer);
          if (elementKind.getAsString().equals(CONFIG)) {
            declareConfiguration(jsonObject, (ConfigurationElementDeclarer) declarer);
          } else {
            declareFlow(jsonObject, (FlowElementDeclarer) declarer);
          }
        }

        return (GlobalElementDeclaration) declarer.getDeclaration();
      }
    }

    return null;
  }

  private void declareParameter(JsonObject jsonObject, EnrichableElementDeclarer declarer) {
    declareEnrichableElement(delegate, jsonObject, declarer);
    ((TopLevelParameterDeclarer) declarer).withRefName(jsonObject.get(REF_NAME).getAsString());
    ((TopLevelParameterDeclarer) declarer).withValue(delegate.fromJson(jsonObject.get(VALUE), ParameterObjectValue.class));
  }

  private void declareFlow(JsonObject jsonObject, FlowElementDeclarer declarer) {
    JsonArray components = jsonObject.get(COMPONENTS).getAsJsonArray();
    components.forEach(c -> declarer
        .withComponent(delegate.fromJson(c, ComponentElementDeclaration.class)));
  }

  private void declareConfiguration(JsonObject jsonObject, ConfigurationElementDeclarer declarer) {
    declarer.withRefName(jsonObject.get(REF_NAME).getAsString());
    declarer.withConnection(
                            delegate.fromJson(jsonObject.get(CONNECTION_FIELD), ConnectionElementDeclaration.class));
  }

  private <T extends EnrichableElementDeclarer> T getDeclarer(ElementDeclarer declarer, String kind, String name) {
    switch (kind) {
      case CONFIG:
        return (T) declarer.newConfiguration(name);
      case GLOBAL_PARAMETER:
        return (T) declarer.newGlobalParameter(name);
      case FLOW:
        return (T) newFlow(name);
      default:
        throw new IllegalArgumentException("Unknown kind: " + kind);
    }
  }

  private String getKind(GlobalElementDeclaration type) {
    if (type instanceof TopLevelParameterDeclaration) {
      return GLOBAL_PARAMETER;
    } else if (type instanceof ConfigurationElementDeclaration) {
      return CONFIG;
    } else if (type instanceof FlowElementDeclaration) {
      return FLOW;
    } else {
      throw new IllegalArgumentException("Unknown kind for type: " + type.getClass().getName());
    }
  }

  private void populateConnection(JsonWriter out, ConfigurationElementDeclaration config) throws IOException {
    out.name(CONNECTION_FIELD).beginObject();
    populateParameterizedObject(delegate, out, config.getConnection(), CONNECTION);
    out.endObject();
  }

}