package org.chuninsane.tools.dubbo.config;

import org.chuninsane.tools.dubbo.hystrix.DefaultFallbackAdapter;

import java.io.Serializable;

/**
 * Hystrix Config
 *
 * @author chuninsane
 */
public class HystrixConfigDTO implements Serializable {

    private static final long serialVersionUID = 1615111267180530810L;

    /** group key */
    private String groupKey;

    /** command key */
    private String commandKey;

    /** threadPool key */
    private String threadPoolKey;

    /** fallback class */
    private String fallbackClz;

    /** fallback adapter */
    private DefaultFallbackAdapter fallback;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HystrixConfigDTO{");
        sb.append("groupKey='").append(groupKey).append('\'');
        sb.append(", commandKey='").append(commandKey).append('\'');
        sb.append(", threadPoolKey='").append(threadPoolKey).append('\'');
        sb.append(", fallbackClz='").append(fallbackClz).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getCommandKey() {
        return commandKey;
    }

    public void setCommandKey(String commandKey) {
        this.commandKey = commandKey;
    }

    public String getThreadPoolKey() {
        return threadPoolKey;
    }

    public void setThreadPoolKey(String threadPoolKey) {
        this.threadPoolKey = threadPoolKey;
    }

    public String getFallbackClz() {
        return fallbackClz;
    }

    public void setFallbackClz(String fallbackClz) {
        this.fallbackClz = fallbackClz;
    }

    public DefaultFallbackAdapter getFallback() {
        return fallback;
    }

    public void setFallback(DefaultFallbackAdapter fallback) {
        this.fallback = fallback;
    }
}
