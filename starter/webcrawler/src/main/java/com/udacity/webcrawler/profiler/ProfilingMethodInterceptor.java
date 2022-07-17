package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Clock;
import java.util.Objects;
import java.time.Duration;
import java.time.Instant;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

  private final Object delegate;
  private final Clock clock;
  private final ProfilingState state;

  // TODO: You will need to add more instance fields and constructor arguments to this class.
  ProfilingMethodInterceptor(Object delegate, Clock clock, ProfilingState state) {

    this.delegate = Objects.requireNonNull(delegate);
    this.clock = Objects.requireNonNull(clock);
    this.state = Objects.requireNonNull(state);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // TODO: This method interceptor should inspect the called method to see if it is a profiled
    //       method. For profiled methods, the interceptor should record the start time, then
    //       invoke the method using the object that is being profiled. Finally, for profiled
    //       methods, the interceptor should record how long the method call took, using the
    //       ProfilingState methods.

    //for profiled methods only
    if (method.getAnnotation(Profiled.class) != null) {
      //record start time
      Instant start = clock.instant();
      //invoke the method
      try {
        return method.invoke(delegate, args);
      } catch (InvocationTargetException ite) {
        throw ite.getCause();
      } catch (Exception e){
        throw e;
      } finally {
        //record how long the method call took using state.record even if the method throws an exception
        state.record(delegate.getClass(), method, Duration.between(start, clock.instant()));
        //System.out.println("Duration of " + method + ": " + Duration.between(start, clock.instant()));
      }
    }

    return method.invoke(delegate, args);
  }
}
