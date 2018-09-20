package org.chuninsane.tools.dubbo.hystrix;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.RpcResult;
import com.alibaba.dubbo.rpc.cluster.LoadBalance;

import java.util.List;

/**
 * Default Fallback Adapter
 *
 * @author chuninsane
 */
public class DefaultFallbackAdapter {

    private Fallback fallback;

    public DefaultFallbackAdapter(Fallback fallback) {
        this.fallback = fallback;
    }

    public RpcResult fallback(Invocation invocation, List<?> invokers, LoadBalance loadbalance) {
        try {
            Object result = fallback.fallback(invocation.getArguments());
            return new RpcResult(result);
        } catch (Throwable t) {
            return new RpcResult(t);
        }
    }
}
