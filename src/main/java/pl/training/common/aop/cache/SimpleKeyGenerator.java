package pl.training.common.aop.cache;

import org.aspectj.lang.Signature;

public final class SimpleKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Signature signature, Object[] args) {
        if (args.length == 0) {
            return SimpleKey.EMPTY;
        }
        if (args.length == 1) {
            var single = args[0];
            if (single != null && !single.getClass().isArray()) {
                return single;
            }
        }
        return new SimpleKey(args);
    }

}