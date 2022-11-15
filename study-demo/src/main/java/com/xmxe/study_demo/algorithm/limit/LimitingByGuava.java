package com.xmxe.study_demo.algorithm.limit;

import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.RateLimiter;

/**
 * 令牌桶算法 (Guava实现) 令牌桶算法的原理是系统会以一个恒定的速度往桶里放入令牌，而如果请求需要被处理，
 * 则需要先从桶里获取一个令牌，当桶里没有令牌可取时，则拒绝服务。 当桶满时，新添加的令牌被丢弃或拒绝。
 * 比如你希望自己的应用程序QPS不要超过1000，那么RateLimiter设置1000的速率后，就会每秒往桶里扔1000个令牌。
 * 
 * 
 */
public class LimitingByGuava {
    private static RateLimiter limiter = RateLimiter.create(5);

    public static void exec() {
        // limiter.acquire() 表示消费一个令牌
        // 当桶中有足够的令牌时，则直接返回0，否则阻塞，直到有可用的令牌数才返回，返回的值为阻塞的时间。加参数代表指定消费多少个令牌
        limiter.acquire(1);
        try {
            // 处理核心逻辑
            TimeUnit.SECONDS.sleep(1);
            System.out.println("--" + System.currentTimeMillis() / 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
/**
 * double acquire() 从RateLimiter获取一个许可，该方法会被阻塞直到获取到请求 
 * double acquire(int permits) 从RateLimiter获取指定许可数，该方法会被阻塞直到获取到请求 
 * 
 * static RateLimiter create(double permitsPerSecond) 根据指定的稳定吞吐率创建RateLimiter，这里的吞吐率是指每秒多少许可数（通常是指QPS，每秒多少查询） 
 * static RateLimiter create(double permitsPerSecond, long warmupPeriod, TimeUnit unit) 根据指定的稳定吞吐率和预热期来创建RateLimiter，这里的吞吐率是指每秒多少许可数（通常是指QPS，每秒多少个请求量），在这段预热时间内，RateLimiter每秒分配的许可数会平稳地增长直到预热期结束时达到其最大速率。（只要存在足够请求数来使其饱和）
 * 
 * double getRate() 返回RateLimiter 配置中的稳定速率，该速率单位是每秒多少许可数 
 * 
 * void setRate(double permitsPerSecond) 更新RateLimite的稳定速率，参数permitsPerSecond由构造RateLimiter的工厂方法提供。
 * 
 * String toString() 返回对象的字符表现形式 
 * 
 * boolean tryAcquire() 从RateLimiter获取许可，如果该许可可以在无延迟下的情况下立即获取得到的话 
 * boolean tryAcquire(int permits) 从RateLimiter 获取许可数，如果该许可数可以在无延迟下的情况下立即获取得到的话 
 * boolean tryAcquire(int permits, long timeout,TimeUnit unit) 从RateLimiter 获取指定许可数如果该许可数可以在不超过timeout的时间内获取得到的话，或者如果无法在timeout 过期之前获取得到许可数的话，那么立即返回false（无需等待） 
 * boolean tryAcquire(long timeout, TimeUnit unit) 从RateLimiter获取许可如果该许可可以在不超过timeout的时间内获取得到的话，或者如果无法在timeout过期之前获取得到许可的话，那么立即返回false（无需等待） 
 * 
 * 方法细节 
 * public static RateLimiter create(double permitsPerSecond)
 * 根据指定的稳定吞吐率创建RateLimiter，这里的吞吐率是指每秒多少许可数（通常是指QPS，每秒多少查询）。 
 * 返回的RateLimiter确保了在平均情况下，每秒发布的许可数不会超过permitsPerSecond，每秒钟会持续发送请求。
 * 当传入请求速率超过permitsPerSecond，速率限制器会每秒释放一个许可(1.0 / permitsPerSecond 这里是指设定了permitsPerSecond为1.0)
 * 当速率限制器闲置时，允许许可数暴增到permitsPerSecond，随后的请求会被平滑地限制在稳定速率permitsPerSecond中。 
 * 参数:permitsPerSecond – 返回的RateLimiter的速率，意味着每秒有多少个许可变成有效。 
 * 抛出:IllegalArgumentException – 如果permitsPerSecond为负数或者为0 
 * 
 * public static RateLimiter create(double permitsPerSecond,long warmupPeriod,TimeUnit unit)
 * 根据指定的稳定吞吐率和预热期来创建RateLimiter，这里的吞吐率是指每秒多少许可数（通常是指QPS，每秒多少查询），
 * 在这段预热时间内，RateLimiter每秒分配的许可数会平稳地增长直到预热期结束时达到其最大速率（只要存在足够请求数来使其饱和）。
 * 同样地，如果RateLimiter在warmupPeriod时间内闲置不用，它将会逐步地返回冷却状态。也就是说，它会像它第一次被创建般经历
 * 同样的预热期。返回的RateLimiter主要用于那些需要预热期的资源，这些资源实际上满足了请求（比如一个远程服务），而不是在
 * 稳定（最大）的速率下可以立即被访问的资源。返回的RateLimiter在冷却状态下启动（即预热期将会紧跟着发生），并且如果被
 * 长期闲置不用，它将回到冷却状态。 
 * 参数:
 * permitsPerSecond –返回的RateLimiter的速率，意味着每秒有多少个许可变成有效。 
 * warmupPeriod – 在这段时间内RateLimiter会增加它的速率，在抵达它的稳定速率或者最大速率之前 
 * unit – 参数warmupPeriod 的时间单位 
 * 抛出:IllegalArgumentException – 如果permitsPerSecond为负数或者为0 
 * 
 * public final void setRate(double permitsPerSecond) 
 * 更新RateLimite的稳定速率，参数permitsPerSecond由构造RateLimiter的工厂方法提供。调用该方法后，当前限制线程不会被唤醒，
 * 因此他们不会注意到最新的速率；只有接下来的请求才会。需要注意的是，由于每次请求偿还了（通过等待，如果需要的话）上一次
 * 请求的开销，这意味着紧紧跟着的下一个请求不会被最新的速率影响到，在调用了setRate之后；它会偿还上一次请求的开销，这个开销
 * 依赖于之前的速率。RateLimiter的行为在任何方式下都不会被改变，比如如果 RateLimiter有20秒的预热期配置，在此方法被调用
 * 后它还是会进行20秒的预热。 
 * 参数: permitsPerSecond – RateLimiter的新的稳定速率
 * 抛出: IllegalArgumentException – 如果permitsPerSecond为负数或者为0 
 * 
 * public final double getRate() 
 * 返回RateLimiter配置中的稳定速率，该速率单位是每秒多少许可数。它的初始值相当于构造这个RateLimiter的工厂方法中的参数
 * permitsPerSecond并且只有在调用setRate(double)后才会被更新。 
 * 
 * public double acquire()
 * 从RateLimiter获取一个许可，该方法会被阻塞直到获取到请求。如果存在等待的情况的话，告诉调用者获取到该请求所需要的睡眠时间。
 * 该方法等同于acquire(1)。
 * 返回: 执行速率的所需要的睡眠时间，单位为妙；如果没有则返回0 
 * Since: 16.0 (版本13.0没有返回值) 
 * 
 * public double acquire(int permits)
 * 从RateLimiter获取指定许可数，该方法会被阻塞直到获取到请求数。如果存在等待的情况的话，告诉调用者获取到这些请求数所需要的睡眠时间。 
 * 参数:permits – 需要获取的许可数 
 * 返回: 执行速率的所需要的睡眠时间，单位为妙；如果没有则返回0 
 * 抛出:IllegalArgumentException – 如果请求的许可数为负数或者为0 
 * Since: 16.0 (版本13.0没有返回值)
 * 
 * public boolean tryAcquire(long timeout,TimeUnit unit)
 * 从RateLimiter获取许可如果该许可可以在不超过timeout的时间内获取得到的话，或者如果无法在timeout
 * 过期之前获取得到许可的话，那么立即返回false（无需等待）。该方法等同于tryAcquire(1, timeout, unit)。 
 * 参数:
 * timeout – 等待许可的最大时间，负数以0处理 unit – 参数timeout 的时间单位 
 * 返回: true表示获取到许可，反之则是false
 * 抛出: IllegalArgumentException – 如果请求的许可数为负数或者为0
 * 
 * public boolean tryAcquire(int permits) 
 * 从RateLimiter获取许可数，如果该许可数可以在无延迟下的情况下立即获取得到的话。该方法等同于tryAcquire(permits, 0, anyUnit)。 
 * 参数:permits – 需要获取的许可数 
 * 返回: true表示获取到许可，反之则是false 
 * 抛出: IllegalArgumentException –如果请求的许可数为负数或者为0 
 * Since: 14.0 
 * 
 * public boolean tryAcquire()
 * 从RateLimiter 获取许可，如果该许可可以在无延迟下的情况下立即获取得到的话。 该方法等同于tryAcquire(1)。 
 * 返回:true表示获取到许可，反之则是false 
 * Since: 14.0 
 * 
 * public boolean tryAcquire(int permits,long timeout,TimeUnit unit) 
 * 从RateLimiter获取指定许可数如果该许可数可以在不超过timeout的时间内获取得到的话，或者如果无法在timeout 过期之前
 * 获取得到许可数的话，那么立即返回false（无需等待）。 
 * 参数: permits – 需要获取的许可数 timeout – 等待许可数的最大时间，负数以0处理 unit – 参数timeout的时间单位 
 * 返回: true表示获取到许可，反之则是false 
 * 抛出: IllegalArgumentException -如果请求的许可数为负数或者为0
 * 
 * public String toString() 
 * 以下描述复制于java.lang.Object类。返回对象的字符表现形式。通常来讲，toString 方法返回一个“文本化呈现”对象的字符串。
 * 结果应该是一个简明但易于读懂的信息表达式。建议所有子类都重写该方法。 toString方法返回一个由实例的类名，字符’@’和以
 * 无符号十六进制表示的对象的哈希值组成的字符串。换句话说，该方法返回的字符串等同于：
 * getClass().getName() + ‘@’ + Integer.toHexString(hashCode()) 重载:Object类的toString方法 返回: 对象的字符表现形式
 */