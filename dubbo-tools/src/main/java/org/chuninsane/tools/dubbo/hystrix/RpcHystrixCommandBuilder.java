package org.chuninsane.tools.dubbo.hystrix;

import com.alibaba.dubbo.common.utils.StringUtils;
import org.chuninsane.tools.dubbo.config.HystrixConfigDTO;
import com.netflix.hystrix.HystrixExecutable;
import com.netflix.hystrix.contrib.javanica.command.*;

/**
 * @author chuninsane
 */
public class RpcHystrixCommandBuilder {

    private HystrixConfigDTO hystrixConfig;

    private MetaHolder commandMetaHolder;

    private MetaHolder fallbackMetaHolder;

    public static RpcHystrixCommandBuilder builder() {
        return new RpcHystrixCommandBuilder();
    }

    public RpcHystrixCommandBuilder hystrixConfig(HystrixConfigDTO hystrixConfig) {
        this.hystrixConfig = hystrixConfig;
        return this;
    }

    public RpcHystrixCommandBuilder commandMetaHolder(MetaHolder commandMetaHolder) {
        this.commandMetaHolder = commandMetaHolder;
        return this;
    }

    public RpcHystrixCommandBuilder fallbackMetaHolder(MetaHolder fallbackMetaHolder) {
        this.fallbackMetaHolder = fallbackMetaHolder;
        return this;
    }

    public HystrixExecutable build() {
        // build setter
        GenericSetterBuilder.Builder setterBuilder = GenericSetterBuilder.builder()
                .groupKey(hystrixConfig.getGroupKey())
                .commandKey(hystrixConfig.getCommandKey());
        if (!StringUtils.isBlank(hystrixConfig.getThreadPoolKey())) {
            setterBuilder.threadPoolKey(hystrixConfig.getThreadPoolKey());
        }

        // build actions
        CommandAction commandAction = new MethodExecutionAction(commandMetaHolder.getObj(), commandMetaHolder.getMethod(), commandMetaHolder.getArgs(), commandMetaHolder);
        CommandAction fallbackAction = new MethodExecutionAction(fallbackMetaHolder.getObj(), fallbackMetaHolder.getMethod(), fallbackMetaHolder.getArgs(), fallbackMetaHolder);
        CommandActions commandActions = CommandActions.builder().commandAction(commandAction).fallbackAction(fallbackAction).build();

        HystrixCommandBuilder builder = HystrixCommandBuilder.builder()
                .setterBuilder(setterBuilder.build())
                .commandActions(commandActions)
                .executionType(ExecutionType.SYNCHRONOUS)
                .build();

        // build invokable
        return new GenericCommand(builder);
    }
}
