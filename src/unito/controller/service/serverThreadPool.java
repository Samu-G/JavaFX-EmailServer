package unito.controller.service;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;

public class serverThreadPool {

    private static final int NUM_THREAD = 5;
    ExecutorService exec = Executors.newFixedThreadPool(NUM_THREAD);




    public serverThreadPool() {
        System.out.println("Lancio un task con ritardo");
        exec = Executors.newFixedThreadPool(NUM_THREAD);
        Vector<FutureTask<Integer>> tasks = new Vector<>();

        for (int i = 0; i < N; i++) {
            FutureTask<Integer> ft = new FutureTask<>(new ClientRequestService(i));
            tasks.add(ft);
            exec.execute(ft);
        }
        try {
            for (int i = 0; i < tasks.size(); i++) {
                FutureTask<Integer> f = (FutureTask<Integer>) tasks.get(i);
                total = total + (f.get()).intValue();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("\nIl totale �: " + total);
        exec.shutdown();
    }

    }
}
