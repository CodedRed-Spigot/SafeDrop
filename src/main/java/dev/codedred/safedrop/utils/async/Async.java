package dev.codedred.safedrop.utils.async;

import lombok.Getter;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ForkJoinPool;

public class Async {

    @Getter
    private static final ForkJoinPool worker = new ForkJoinPool(
            8,
            ForkJoinPool.defaultForkJoinWorkerThreadFactory,
            (t, e) -> e.printStackTrace(),
            false
    );

    public static <T> CompletableFuture<T> run(Callable<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.call();
            } catch (Exception exception) {
                throw new CompletionException(exception);
            }
        }, worker);
    }

    public static CompletableFuture<Void> run(Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception exception) {
                throw new CompletionException(exception);
            }
        }, worker);
    }

}