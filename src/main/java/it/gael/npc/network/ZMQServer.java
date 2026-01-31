package it.gael.npc.network;

import it.gael.npc.ActionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZMQServer {
    private static final Logger logger = LoggerFactory.getLogger("NPC-ZMQ");
    private final ActionHandler actionHandler;
    private final ExecutorService executor;
    private boolean running = false;
    private ZMQ.Socket socket;
    private ZContext context;

    public ZMQServer(ActionHandler actionHandler) {
        this.actionHandler = actionHandler;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void start() {
        if (running) return;
        running = true;
        
        executor.submit(() -> {
            try (ZContext ctx = new ZContext()) {
                this.context = ctx;
                // Switch to REQ: The Game Server initiates requests (Events) to the Brain
                socket = context.createSocket(SocketType.REQ);
                
                // DOCKER FIX: 
                // Container sees localhost as itself. We need to reach the Host OS.
                // Standard Docker bridge IP is 172.17.0.1 on Linux.
                // If this fails, we might need an Env Var, but this is the standard fix for "Container -> Host".
                String hostIp = System.getenv("NPC_BRAIN_HOST");
                if (hostIp == null || hostIp.isEmpty()) {
                    hostIp = "172.17.0.1"; // Default Docker Gateway
                }
                
                String address = "tcp://" + hostIp + ":5555";
                
                logger.info("Connecting to Brain at " + address + "...");
                socket.connect(address);
                
                // Send initial handshake
                socket.send("SYSTEM|HELO|World Init");
                String reply = socket.recvStr(0); // Blocking wait for reply
                logger.info("Brain Connected: " + reply);

                while (running && !Thread.currentThread().isInterrupted()) {
                    Thread.sleep(1000); 
                }
            } catch (Exception e) {
                logger.error("ZMQ Error (Check if Python is running and Firewall allows 5555)", e);
            }
        });
    }

    public synchronized void sendEvent(String user, String message) {
        if (socket == null || !running) return;
        
        try {
            String payload = user + "|" + message;
            socket.send(payload);
            
            String response = socket.recvStr(0);
            
            if (response != null) {
                String[] parts = response.split("\\|", 3);
                if (parts.length >= 2) {
                    String command = parts[0];
                    String target = parts[1];
                    String content = parts.length > 2 ? parts[2] : "";
                    
                    actionHandler.handle("Gillian", command, target, content);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to send event to brain", e);
        }
    }

    public void stop() {
        running = false;
        if (context != null) context.close();
        executor.shutdownNow();
    }
}
