package org.chuninsane.tools.dubbo.hystrix;

/**
 * Fallback
 *
 * @author chuninsane
 */
public interface Fallback {

    Object fallback(Object[] args);
}
