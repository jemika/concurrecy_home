package Semaphore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.Executors.*;

public class SemaphoreExample {

//    Существует парковка, которая одновременно может вмещать не более 5 автомобилей.
// Если парковка заполнена полностью, то вновь прибывший автомобиль должен
// подождать пока не освободится хотя бы одно место. После этого он сможет припарковаться.

    public static void main(String[] args) {

        AtomicInteger i = new AtomicInteger();
        AtomicInteger count = new AtomicInteger();

        Semaphore semaphore = new Semaphore(5, true);
        ExecutorService service = newCachedThreadPool(
                (Runnable r) ->
                {
                    Thread thread = new Thread(r);
                    thread.setName("Car #" + i.getAndIncrement());
                    return thread;
                });


        for (int j = 0; j < 10; j++) {
            service.submit(() -> {
                String name = Thread.currentThread().getName();
                try {
                    TimeUnit.SECONDS.sleep((long) (Math.random() * 10));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.printf("%s arrives to parking%n", name);
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.printf("%s on the parking now! %d/10%n", name, count.incrementAndGet());
                try {
                    TimeUnit.SECONDS.sleep((long) (Math.random() * 10));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.printf("%s leaves parking! %d/10%n", name, count.decrementAndGet());
                semaphore.release();
            });
        }
        service.shutdown();
    }

}
