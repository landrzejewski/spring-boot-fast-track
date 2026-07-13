package pl.training.common.aop.cache;

import org.aspectj.lang.Signature;

@FunctionalInterface
public interface KeyGenerator {

    Object generate(Signature signature, Object[] args);

}