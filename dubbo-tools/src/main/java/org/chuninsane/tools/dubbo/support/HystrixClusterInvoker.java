package org.chuninsane.tools.dubbo.support;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.cluster.Directory;
import com.alibaba.dubbo.rpc.cluster.LoadBalance;
import com.alibaba.dubbo.rpc.cluster.support.FailoverClusterInvoker;
import org.chuninsane.tools.dubbo.config.HystrixConfigDTO;
import org.chuninsane.tools.dubbo.config.HystrixConfigSubscriber;
import org.chuninsane.tools.dubbo.hystrix.DefaultFallbackAdapter;
import org.chuninsane.tools.dubbo.hystrix.RpcHystrixCommandBuilder;
import com.netflix.hystrix.HystrixExecutable;
import com.netflix.hystrix.contrib.javanica.command.ExecutionType;
import com.netflix.hystrix.contrib.javanica.command.MetaHolder;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Hystrix Cluster Extension, extend FailoverClusterInvoker
 *
 * @author chuninsane
 */
public class HystrixClusterInvoker<T> extends FailoverClusterInvoker<T> {

    private static final Logger logger = LoggerFactory.getLogger(HystrixClusterInvoker.class);

    private Class<?> interfaceClz;

    public HystrixClusterInvoker(Directory<T> directory) {
        super(directory);
        interfaceClz = directory.getInterface();
    }

    @Override
    public Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
        boolean needHystrixProxy = true;
        HystrixConfigDTO hystrixConfig = null;
        if ((hystrixConfig = getConfigByMethodSign(invocation.getMethodName(), invocation.getParameterTypes())) == null) {
            needHystrixProxy = false;
        }

        MetaHolder commandMetaHolder = null;
        if (needHystrixProxy
                && (commandMetaHolder = buildCommandMetaHolder(invocation, invokers, loadbalance)) == null) {
            needHystrixProxy = false;
        }

        MetaHolder fallbackMetaHolder = null;
        if (needHystrixProxy
                && (fallbackMetaHolder = buildFallbackMetaHolder(hystrixConfig, invocation, invokers, loadbalance)) == null) {
            needHystrixProxy = false;
        }

        HystrixExecutable invokable = null;
        if (needHystrixProxy) {
            invokable = RpcHystrixCommandBuilder.builder()
                    .hystrixConfig(hystrixConfig)
                    .commandMetaHolder(commandMetaHolder)
                    .fallbackMetaHolder(fallbackMetaHolder)
                    .build();
            needHystrixProxy = invokable != null;
        }

        if (needHystrixProxy) {
            return (Result) (invokable).execute();
        } else {
            return super.doInvoke(invocation, invokers, loadbalance);
        }
    }

    private HystrixConfigDTO getConfigByMethodSign(final String methodName,
                                                                     final Class<?>[] paramTypes) {
        try {
            Method method = interfaceClz.getMethod(methodName, paramTypes);
            return HystrixConfigSubscriber.getConfigByMethod(method);
        } catch (NoSuchMethodException e) {
            logger.error("Can not get " + methodName+ " method, must be a bug!", e);
            return null;
        }
    }

    /**
     * build command metaHolder
     */
    private MetaHolder buildCommandMetaHolder(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) {
        Method invokeMethod;
        try {
            invokeMethod = this.getClass().getMethod("doActualInvoke", Invocation.class,
                    List.class, LoadBalance.class);
        } catch (NoSuchMethodException e) {
            logger.error("Can not get doActualInvoke method, must be a bug!", e);
            return null;
        }

        return MetaHolder.builder()
                .obj(this)
                .method(invokeMethod)
                .args(new Object[]{invocation, invokers, loadbalance})
                .executionType(ExecutionType.SYNCHRONOUS)
                .build();
    }

    /**
     * build fallback metaHolder
     */
    private MetaHolder buildFallbackMetaHolder(HystrixConfigDTO hystrixConfig,
                                               Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) {
        DefaultFallbackAdapter fallback = hystrixConfig.getFallback();
        Method fallbackMethod;
        try {
            fallbackMethod = fallback.getClass().getMethod("fallback", Invocation.class,
                    List.class, LoadBalance.class);
        } catch (NoSuchMethodException e) {
            logger.error("Can not get fallback method, must be a bug!", e);
            return null;
        }

        return MetaHolder.builder()
                .obj(fallback)
                .method(fallbackMethod)
                .args(new Object[]{invocation, invokers, loadbalance})
                .executionType(ExecutionType.SYNCHRONOUS)
                .build();
    }

    /**
     * do actual invoke
     */
    public Result doActualInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) throws Throwable {
        Result result = super.doInvoke(invocation, invokers, loadbalance);
        if (result.hasException()) {
            throw result.getException();
        }
        return result;
    }
}
