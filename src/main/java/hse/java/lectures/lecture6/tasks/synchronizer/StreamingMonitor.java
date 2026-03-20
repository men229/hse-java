package hse.java.lectures.lecture6.tasks.synchronizer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

import java.io.PrintStream;

@Getter
public class StreamingMonitor {
    // impl your sync here
    private final Object key = new Object();
    private final int N;
    private final int ticksPerWriter;
    private int cnt = 0;
    private int nowId = 1;

    public StreamingMonitor(int size, int ticksPerWriter) {
        this.N = size;
        this.ticksPerWriter = size * ticksPerWriter;
    }

    public boolean check(){
        return cnt == ticksPerWriter;
    }

    public synchronized void tick(int idCome, Runnable onTick, PrintStream output, String message) throws InterruptedException {

        while (idCome != nowId || cnt >= ticksPerWriter) {
            wait();
        }

        nowId = Integer.max(1, (nowId + 1) % (N+1));
        cnt += 1;

        output.print(message);
        onTick.run();

        notifyAll();
    }
}