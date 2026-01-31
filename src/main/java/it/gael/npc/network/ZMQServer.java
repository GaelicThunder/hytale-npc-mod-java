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
                
                // Connect to Python Brain (assumed localhost)
                logger.info("Connecting to Brain at tcp://localhost:5555...");
                socket.connect("tcp://localhost:5555");
                
                // Send initial handshake
                socket.send("SYSTEM|HELO|World Init");
                String reply = socket.recvStr(0);
                logger.info("Brain Connected: " + reply);

                // Main loop isn't polling anymore in REQ mode unless we have a queue
                // But we need to keep the thread alive for async sending if implemented later
                // For now, this thread just holds the socket open.
                
                while (running && !Thread.currentThread().isInterrupted()) {
                    Thread.sleep(1000); 
                    // Heartbeat or keep-alive if needed
                }
            } catch (Exception e) {
                logger.error("ZMQ Error", e);
            }
        });
    }

    // New method to send events to Python Brain
    // Synchronized because ZMQ sockets are not thread-safe
    public synchronized void sendEvent(String user, String message) {
        if (socket == null || !running) return;
        
        try {
            // Protocol: "USER|MESSAGE"
            String payload = user + "|" + message;
            socket.send(payload);
            
            // Wait for Brain Decision (Blocking)
            String response = socket.recvStr(0);
            
            if (response != null) {
                // Protocol: "COMMAND|TARGET|CONTENT"
                String[] parts = response.split("\\|", 3);
                if (parts.length >= 2) {
                    String command = parts[0];
                    String target = parts[1];
                    String content = parts.length > 2 ? parts[2] : "";
                    
                    // Execute Action
                    actionHandler.handle("Gillian", command, target, content);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to send event to brain", e);
            // Reconnect logic could go here
        }
    }

    public void stop() {
        running = false;
        if (context != null) context.close();
        executor.shutdownNow();
    }
}
