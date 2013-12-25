/*
 * Copyright (c) 2013 DataTorrent, Inc. ALL Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datatorrent.api.codec;

import java.io.*;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.ser.std.RawSerializer;

import com.datatorrent.api.StreamCodec;
import com.datatorrent.api.util.ObjectMapperString;
import com.datatorrent.common.util.Slice;

/**
 * <p>JsonStreamCodec class.</p>
 *
 * @param <T> tuple type
 * @since 0.3.2
 */
public class JsonStreamCodec<T> implements StreamCodec<T>
{
  private ObjectMapper mapper;

  /**
   * <p>Constructor for JsonStreamCodec.</p>
   */
  public JsonStreamCodec()
  {
    mapper = new ObjectMapper();
    mapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
    SimpleModule module = new SimpleModule("MyModule", new Version(1, 0, 0, null));
    module.addSerializer(ObjectMapperString.class, new RawSerializer<Object>(Object.class));
    mapper.registerModule(module);
  }

  /** {@inheritDoc} */
  @Override
  public Object fromByteArray(Slice data)
  {
    ByteArrayInputStream bis = new ByteArrayInputStream(data.buffer, data.offset, data.length);
    try {
      return mapper.readValue(bis, Object.class);
    }
    catch (Exception ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /** {@inheritDoc} */
  @Override
  public Slice toByteArray(T o)
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    try {
      mapper.writeValue(bos, o);
      byte[] bytes = bos.toByteArray();
      return new Slice(bytes, 0, bytes.length);
    }
    catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /** {@inheritDoc} */
  @Override
  public int getPartition(T o)
  {
    return o.hashCode();
  }

}
