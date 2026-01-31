import zmq
import time
import sys

def main():
    print("🧠 Gillian's Brain is initializing...")
    
    # Initialize ZeroMQ Context
    context = zmq.Context()
    
    # Create REQ socket (Client) to talk to Java REP socket (Server)
    socket = context.socket(zmq.REQ)
    
    # Connect to the Hytale Server Mod
    address = "tcp://localhost:5555"
    print(f"🔌 Connecting to Hytale Bridge at {address}...")
    socket.connect(address)

    print("✅ Brain connected! Ready to send commands.")
    print("commands: 'chat <msg>', 'attack <target>', 'follow <target>', 'idle'")

    try:
        while True:
            # Simple interactive loop for testing
            user_input = input("Gillian> ")
            
            if not user_input:
                continue
                
            if user_input.lower() in ["exit", "quit"]:
                break

            # Parse command to match Java protocol: "NPC_NAME|COMMAND|TARGET|CONTENT"
            # Defaulting NPC name to "Gillian"
            parts = user_input.split(" ", 1)
            cmd = parts[0].upper()
            arg = parts[1] if len(parts) > 1 else ""
            
            # Formatting the message
            # If command is CHAT, arg is content. If ATTACK, arg is target.
            protocol_msg = f"Gillian|{cmd}|{arg}|{arg}"
            
            print(f"📤 Sending: {protocol_msg}")
            
            # Send to Java
            socket.send_string(protocol_msg)
            
            # Wait for reply (Blocking)
            reply = socket.recv_string()
            print(f"📥 Received: {reply}")
            
    except KeyboardInterrupt:
        print("\n🧠 Brain shutting down...")
    finally:
        socket.close()
        context.term()

if __name__ == "__main__":
    main()
