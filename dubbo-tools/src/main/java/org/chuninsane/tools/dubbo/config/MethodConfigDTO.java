package org.chuninsane.tools.dubbo.config;

import java.io.Serializable;
import java.util.List;

/**
 * Method Config
 *
 * @author chuninsane
 */
public class MethodConfigDTO implements Serializable {

    private static final long serialVersionUID = 7192523258996128557L;

    /** interface name */
    private String interfaceName;

    /** method name */
    private String methodName;

    /** parameter type list */
    private List<String> parameterTypes;

    /** hystrix groupKey */
    private String groupKey;

    /** hystrix commandKey */
    private String commandKey;

    /** hystrix threadPoolKey */
    private String threadPoolKey;

    /** fallback class */
    private String fallbackClass;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Config{");
        sb.append("interfaceName='").append(interfaceName).append('\'');
        sb.append(", methodName='").append(methodName).append('\'');
        sb.append(", parameterTypes=").append(parameterTypes);
        sb.append(", groupKey='").append(groupKey).append('\'');
        sb.append(", commandKey='").append(commandKey).append('\'');
        sb.append(", threadPoolKey='").append(threadPoolKey).append('\'');
        sb.append(", fallbackClass='").append(fallbackClass).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(List<String> parameterTypes) {
        this.parameterTypes = parameterTypes;
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

    public String getFallbackClass() {
        return fallbackClass;
    }

    public void setFallbackClass(String fallbackClass) {
        this.fallbackClass = fallbackClass;
    }
}
