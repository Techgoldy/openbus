package com.produban.openbus.persistence;


import backtype.storm.tuple.Values;
import redis.clients.jedis.Jedis;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

/**
 * A basic Trident function to store counters on Redis
 */
public class RedisCounterStore extends BaseFunction {
    Jedis _jedis;
    String _redisHost;

    public RedisCounterStore(String redisHost) {
        _redisHost = redisHost;
    }

    @Override
    public void prepare(java.util.Map conf, TridentOperationContext context) {
        _jedis = new Jedis(_redisHost);
    }

    @Override
    public void execute(TridentTuple objects, TridentCollector tridentCollector) {
        String key = objects.getString(0);
        if (_jedis.exists(key)) {
            _jedis.incr(key);
        }
        else {
            _jedis.set(key, "1");
        }
        tridentCollector.emit(new Values(true));
    }

    @Override
    public void cleanup() { _jedis.shutdown(); }

}
