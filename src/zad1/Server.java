/**
 *
 *  @author Szewczyk Ryszard S19234
 *
 */

package zad1;




import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.*;

public class Server {
    private String host;
    private int port;
    private volatile boolean isRunning;
    private Selector selector;
    private SelectionKey selectionKey;
    private Map<SocketChannel, Connection> connectionMap;
    private StringBuilder log;
    private ServerSocketChannel socketChannel;
    private ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
    private static Charset charset = StandardCharsets.UTF_8;
    private StringBuilder request = new StringBuilder();

    public Server(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.socketChannel = ServerSocketChannel.open();
        this.socketChannel.socket().bind(new InetSocketAddress(this.host, this.port));
        this.socketChannel.configureBlocking(false);
        this.selector = Selector.open();
        this.selectionKey = socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        this.log = new StringBuilder();
        this.connectionMap = new HashMap<>();
    }

    public void startServer(){
        new Thread(()->{
            isRunning = true;
            while(isRunning){
                try {
                    selector.select();

                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();

                    while(iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        if(key.isAcceptable()){
                            SocketChannel client = socketChannel.accept();
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                            continue;
                        }
                        if(key.isReadable()){
                            SocketChannel client = (SocketChannel)key.channel();
                            if(!client.isOpen()) return;
                            request.setLength(0);
                            byteBuffer.clear();

                            for(int bytesRead = client.read(byteBuffer); bytesRead > 0; bytesRead = client.read(byteBuffer)){
                                byteBuffer.flip();
                                CharBuffer charBuffer = charset.decode(byteBuffer);
                                request.append(charBuffer);
                            }

                            StringBuilder response = new StringBuilder();
                            String[] arr = new String[4];
                            arr[3] = "";
                            if(request.toString().contains("login")){
                                String str = "logged in";
                                response.append(str);
                                connectionMap.put(client, new Connection(request.toString().split(" ")[1]));
                                putLogClient(client, str);
                                arr[1] = str;
                            } else if(request.toString().equals("bye")){
                                String str = "logged out";
                                response.append(str);
                                connectionMap.get(client).close();
                                putLogClient(client,str);
                                arr[1] = str;
                            } else if(request.toString().equals("bye and log transfer")){
                                String str = "logged out";
                                putLogClient(client,str);
                                connectionMap.get(client).close();
                                response.append(connectionMap.get(client));
                                arr[1] = str;
                            } else {
                                putLogClient(client,"Request: " + request);
                                String[] split = request.toString().split(" ");
                                String result = Time.passed(split[0],split[1]);
                                putLogClient(client,"Result:");
                                putLogClient(client,result);
                                response.append(result);
                                arr[1] = "request";
                                arr[3] = ": \"" + request + "\"";
                            }
                            arr[0] = connectionMap.get(client).id;
                            arr[2] = "at " + LocalTime.now();
                            log(String.format("%s %s %s%s", arr[0], arr[1], arr[2], arr[3]));
                            ByteBuffer out = ByteBuffer.allocateDirect(response.toString().getBytes().length);
                            out.put(charset.encode(response.toString()));
                            out.flip();
                            client.write(out);
                        }
                    }

                } catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public void stopServer(){
        isRunning = false;
    }

    private void putLogClient(SocketChannel client, String log){
        if(!connectionMap.containsKey(client)){
            connectionMap.put(client, new Connection(log));
        } else {
            connectionMap.get(client).log.append(log).append("\n");
        }
    }

    private void log(String log){
        this.log.append(log).append("\n");
    }

    public String getServerLog() {
        return log.toString();
    }

    private static class Connection {
        StringBuilder log;
        String id;

        Connection(String id){
            this.id = id;
            log = new StringBuilder("\n=== " + id + " log start ===\n");
        }

        public void close(){
            log.append("=== ").append(id).append(" log end ===\n");
        }

        @Override
        public String toString() {
            return log.toString();
        }
    }
}
