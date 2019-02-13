package uk.gov.ida.dropwizard.logstash.support;

import com.google.common.collect.Iterables;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class UdpServer {

    private Thread thread;
    private volatile DatagramSocket serverSocket;
    private List<String> received = new CopyOnWriteArrayList<>();
    private CyclicBarrier barrier = new CyclicBarrier(2);
    private AtomicBoolean stopped = new AtomicBoolean(false);

    public void start() {
        try {
            serverSocket = new DatagramSocket(new InetSocketAddress("localhost", 0));
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                byte[] receiveData = new byte[1024];
                while (!stopped.get()) {
                    try {
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        serverSocket.receive(receivePacket);
                        received.add(new String(receivePacket.getData()));
                        barrier.await();
                    } catch (IOException | BrokenBarrierException | InterruptedException e) {
                        if (!stopped.get()) e.printStackTrace();
                        stopped.set(true);
                    }
                }
            }
        });

        thread.start();
    }

    public void stop() throws Exception{
        stopped.set(true);
        serverSocket.close();
        thread.interrupt();
        thread.join();
    }

    public int getLocalPort() {
        return serverSocket.getLocalPort();
    }

    public String getReceivedPacket() throws InterruptedException, BrokenBarrierException, TimeoutException {
        barrier.await(5, TimeUnit.SECONDS);
        return Iterables.getLast(received, null);
    }
}
