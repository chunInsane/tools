package org.chuninsane.tools.dubbo.support;

import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.cluster.Cluster;
import com.alibaba.dubbo.rpc.cluster.Directory;

/**
 * Hystrix Cluster
 *
 * @author chuninsane
 */
public class HystrixCluster implements Cluster {

    public final static String NAME = "hystrix";

    @Override
    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        return new HystrixClusterInvoker<T>((directory));
    }
}
