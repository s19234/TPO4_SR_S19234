/**
 *
 *  @author Szewczyk Ryszard S19234
 *
 */

package zad1;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;

public class ClientTask extends FutureTask<String> {

    private ClientTask(Callable<String> callable){
        super(callable);
    }

    public static ClientTask create(Client client, List<String> requests, boolean showRes){
        return new ClientTask(new Callable<String>() {
            @Override
            public String call() throws Exception {
                client.connect();
                client.send("login " + client.getId());
                requests.forEach(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        String response = client.send(s);
                        if(showRes)
                            System.out.println(response);
                    }
                });
                client.getLog().append(client.send("bye and log transfer"));
                return client.getLog().toString();
            }
        });
    }
}
