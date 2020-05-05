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
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Client implements Cloneable {
    private String host;
    private String id;
    private int port;
    private SocketChannel socketChannel;
    private StringBuilder log;
    private static Charset charset = StandardCharsets.UTF_8;

    public Client(String host, int port, String id){
        this.host = host;
        this.port = port;
        this.id = id;
        this.log = new StringBuilder();
    }

    public void connect(){
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(host, port));
            while((!socketChannel.finishConnect()));
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public String send(String text){
        ByteBuffer outBuf = ByteBuffer.allocateDirect(text.getBytes().length);
        ByteBuffer inBuf = ByteBuffer.allocateDirect(2137);
        StringBuilder response = new StringBuilder();
        try {

            outBuf.put(charset.encode(text));
            outBuf.flip();
            socketChannel.write(outBuf);

        } catch (IOException e) {
            e.printStackTrace();
        }
        inBuf.clear();

        try {
            int readBytes;

            while((readBytes = socketChannel.read(inBuf)) < 1);

            for( ; readBytes > 0 ; readBytes = socketChannel.read(inBuf) ){
                inBuf.flip();
                CharBuffer cbuf = charset.decode(inBuf);
                response.append(cbuf);
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        return response.toString();
    }

    public String getId() {
        return id;
    }

    public StringBuilder getLog() {
        return log;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
