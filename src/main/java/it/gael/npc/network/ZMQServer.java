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

    public ZMQServer(ActionHandler actionHandler) {
        this.actionHandler = actionHandler;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void start() {
        if (running) return;
        running = true;
        
        executor.submit(() -> {
            try (ZContext context = new ZContext()) {
                // Socket to talk to clients (Python Brain)
                ZMQ.Socket socket = context.createSocket(SocketType.REP);
                // Bind to port 5555
                socket.bind("tcp://*:5555");
                
                logger.info("ZMQ Server listening on tcp://*:5555");

                while (running && !Thread.currentThread().isInterrupted()) {
                    try {
                        // Block until a message is received
                        // ZMQ recvStr defaults to UTF-8
                        String message = socket.recvStr(0); 
                        if (message != null) {
                            logger.debug("Received raw: {}", message);
                            
                            // Simple protocol: "NPC_NAME|COMMAND|TARGET|CONTENT"
                            String[] parts = message.split("\\|", 4);
                            String response = "OK";

                            if (parts.length >= 2) {
                                String npcName = parts[0];
                                String command = parts[1];
                                String target = parts.length > 2 ? parts[2] : "";
                                String content = parts.length > 3 ? parts[3] : "";

                                // Execute action on main server thread if possible or handle thread-safety internally
                                actionHandler.handle(npcName, command, target, content);
                            } else {
                                response = "ERROR: Invalid format";
                            }

                            // Send reply back to client
                            socket.send(response.getBytes(ZMQ.CHARSET), 0);
                        }
                    } catch (Exception e) {
                        logger.error("Error in ZMQ loop", e);
                    }
                }
            } catch (Exception e) {
                logger.error("Fatal ZMQ error", e);
            } finally {
                logger.info("ZMQ Server stopped");
            }
        });
    }

    public void stop() {
        running = false;
        executor.shutdownNow();
    }
}
