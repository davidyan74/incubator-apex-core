/**
 * Copyright (c) 2012-2012 Malhar, Inc.
 * All rights reserved.
 */
package com.malhartech.dag;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.malhartech.stram.conf.ShipContainingJars;
import org.objenesis.instantiator.ObjectInstantiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Requires kryo and mockito in deployment
 */
@ShipContainingJars (classes={Kryo.class, ObjectInstantiator.class, com.esotericsoftware.minlog.Log.class})
public class DefaultSerDe implements SerDe
{
  private static final Logger logger = LoggerFactory.getLogger(DefaultSerDe.class);
  
  private Kryo kryo = new Kryo();
  private Output output = new Output(new byte[4096]);
  private Input input = new Input();

  public Object fromByteArray(byte[] bytes)
  {
    input.setBuffer(bytes);
    Object o = kryo.readClassAndObject(input);
    return o;
  }

  public byte[] toByteArray(Object o)
  {
    output.setPosition(0);
    kryo.writeClassAndObject(output, o);
    byte[] bytes = output.toBytes();
    logger.info("~~~~~~ toByteArray length = " + bytes.length);  
    return bytes;
  }

  public byte[] getPartition(Object o)
  {
    return null;
  }
}