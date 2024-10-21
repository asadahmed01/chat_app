
# Console Chat Application

A simple **console-based chat application** implemented in **Java** that allows multiple clients to communicate with each other via a central server. This app demonstrates basic socket programming concepts.


## **Features**
- Server-Client communication using **Sockets**.
- Support for **multiple clients** using multithreading.
- Each client receives messages broadcasted by others in real time.
- Clean exit for clients and server shutdown.

---

## **Technologies Used**
- **Java**: Core programming language.
- **Socket Programming**: For communication between server and clients.
- **Multithreading**: To handle multiple clients simultaneously.

---

## **How It Works**
1. **Server**:
   - Listens for incoming client connections.
   - Broadcasts messages received from any client to all connected clients.
2. **Client**:
   - Connects to the server.
   - Sends and receives messages through the server.

---

## **Setup Instructions**

### **Prerequisites**
- Java Development Kit (JDK) installed (version 21 or higher).
- Basic knowledge of Java and Socket programming.
- A terminal/command prompt for running the server and clients.

### **Steps to Run**

#### 1. **Clone the Repository**
```bash
git clone <repository-url>
cd console-chat_app
```

#### 2. **Compile the Java Files**
```bash
javac Server.java Client.java
```

#### 3. **Run the Server**
In one terminal, start the server:
```bash
java Server
```

#### 4. **Run the Clients**
Open multiple terminals (for each client) and connect them to the server:
```bash
java Client
```

#### 5. **Start Chatting!**
- In each client terminal, type a message and press **Enter**.
- All connected clients will see the messages.



## **License**
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.
