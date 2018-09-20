package org.chuninsane.tools.dubbo.config;

import com.alibaba.fastjson.JSON;
import org.chuninsane.tools.dubbo.hystrix.DefaultFallbackAdapter;
import org.chuninsane.tools.dubbo.hystrix.Fallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hystrix Config Subscriber
 *
 * @author chuninsane
 */
public class HystrixConfigSubscriber {

    private static Logger LOGGER = LoggerFactory.getLogger(HystrixConfigSubscriber.class);

    private static Map<String, Class<?>> primitiveClazz;

    private static Map<Method, HystrixConfigDTO> cache = new ConcurrentHashMap<>();

    static {
        primitiveClazz = new HashMap<>();
        primitiveClazz.put("int", int.class);
        primitiveClazz.put("byte", byte.class);
        primitiveClazz.put("char", char.class);
        primitiveClazz.put("short", short.class);
        primitiveClazz.put("long", long.class);
        primitiveClazz.put("float", float.class);
        primitiveClazz.put("double", double.class);
        primitiveClazz.put("boolean", boolean.class);
    }

    public static HystrixConfigDTO getConfigByMethod(Method method) {
        return method == null ? null : cache.get(method);
    }

    /**
     * subscribe config
     */
    public static void subscribe(final String content) {
        List<MethodConfigDTO> configList;
        try {
            configList = JSON.parseArray(content, MethodConfigDTO.class);
        } catch (Exception e) {
            LOGGER.error("Failed to parse hystrix method config, [{}]", content, e);
            return;
        }

        for (MethodConfigDTO config : configList) {
            refresh(config);
        }
    }

    private static void refresh(final MethodConfigDTO config) {
        try {
            HystrixConfigDTO methodConfigDTO = new HystrixConfigDTO();

            Assert.hasText(config.getInterfaceName(), "interfaceName can not be blank");
            Class<?> interfaceClz = Class.forName(config.getInterfaceName());

            Assert.hasText(config.getMethodName(), "methodName can not be blank");
            String methodName = config.getMethodName();

            List<String> parameterTypeStrs = config.getParameterTypes();
            Class<?>[] parameterTypes = null;
            if (parameterTypeStrs == null || parameterTypeStrs.size() <= 0) {
                parameterTypes = new Class<?>[]{};
            } else {
                parameterTypes = new Class<?>[parameterTypeStrs.size()];
                for (int i = 0; i < parameterTypeStrs.size(); ++i) {
                    String parameterTypeStr = parameterTypeStrs.get(i);
                    Class<?> parameterType = getClzByType(parameterTypeStr);
                    parameterTypes[i] = parameterType;
                }
            }

            Method method = interfaceClz.getMethod(methodName, parameterTypes);

            Assert.hasText(config.getGroupKey(), "groupKey can not be blank");
            methodConfigDTO.setGroupKey(trim(config.getGroupKey()));
            Assert.hasText(config.getCommandKey(), "commandKey can not be blank");
            methodConfigDTO.setCommandKey(trim(config.getCommandKey()));
            methodConfigDTO.setThreadPoolKey(trim(config.getThreadPoolKey()));

            Assert.hasText(config.getFallbackClass(), "fallbackClass can not be blank");
            Class<?> fallbackClz = Class.forName(config.getFallbackClass());
            methodConfigDTO.setFallbackClz(config.getFallbackClass());
            Fallback fallback = (Fallback) fallbackClz.getConstructor().newInstance();
            methodConfigDTO.setFallback(new DefaultFallbackAdapter(fallback));

            cache.put(method, methodConfigDTO);
            LOGGER.info("Success to refresh config, interface[{}], method[{}], [{}]",
                    config.getInterfaceName(), config.getMethodName(), config);
        } catch (Exception e) {
            LOGGER.error("failed to refresh config, interface[{}], method[{}], [{}]",
                    config.getInterfaceName(), config.getMethodName(), config, e);
        }
    }

    private static Class<?> getClzByType(String type) throws ClassNotFoundException {
        if (isPrimitiveType(type)) {
            return primitiveClazz.get(type);
        } else {
            return Class.forName(type);
        }
    }

    private static boolean isPrimitiveType(String type) {
        return primitiveClazz.containsKey(type);
    }

    private static String trim(String source) {
        if (source == null) {
            return source;
        }
        return source.trim();
    }

    public static void main(String[] args) {
        subscribe("[{\"interfaceName\":\"com.alibaba.dubbo.demo.DemoService\", \"methodName\":\"sayHello\", \"parameterTypes\":[\"java.lang.String\"], \"groupKey\":\"test\", \"commandKey\":\"DemoService\", \"fallbackClass\":\"com.alibaba.dubbo.demo.consumer.dubbo.DemoFallback\"}]");
    }
}
