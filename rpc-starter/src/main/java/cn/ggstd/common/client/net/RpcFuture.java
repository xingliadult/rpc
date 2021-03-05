package cn.ggstd.common.client.net;

import java.util.concurrent.*;

/**
 * Created by lixing on 2021-3-1 下午 5:29.
 */
public class RpcFuture<T> implements Future<T> {

    private T result;
    private final CountDownLatch downLatch = new CountDownLatch(1);

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return result != null;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        downLatch.await();
        return result;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        downLatch.await(timeout, unit);
        return result;
    }

    public void setResult(T result) {
        this.result = result;
        downLatch.countDown();
    }
}
