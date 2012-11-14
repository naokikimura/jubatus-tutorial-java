/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import java.util.List;
import java.util.Map;
import org.msgpack.rpc.Client;
import org.msgpack.rpc.loop.EventLoop;
import us.jubat.classifier.ClassifierClient.RPCInterface;
import us.jubat.classifier.ConfigData;
import us.jubat.classifier.Datum;
import us.jubat.classifier.EstimateResult;
import us.jubat.classifier.TupleStringDatum;

/**
 * @see us.jubat.classifier.ClassifierClient
 * @author <a href="https://github.com/naokikimura">naokikimura</a>
 */
class ClassifierClient implements AutoCloseable {
    private Client client;
    private RPCInterface proxy;

    ClassifierClient(String host, int port, double timeout_sec) throws Exception {
        EventLoop loop = EventLoop.defaultEventLoop();
        client = new Client(host, port, loop);
        proxy = client.proxy(RPCInterface.class);
    }

    boolean set_config(String name, ConfigData c) {
        return proxy.set_config(name, c);
    }

    ConfigData get_config(String name) {
        return proxy.get_config(name);
    }

    int train(String name, List<TupleStringDatum> data) {
        return proxy.train(name, data);
    }

    List<List<EstimateResult>> classify(String name, List<Datum> data) {
        return proxy.classify(name, data);
    }

    boolean save(String name, String id) {
        return proxy.save(name, id);
    }

    boolean load(String name, String id) {
        return proxy.load(name, id);
    }

    Map<String, Map<String, String>> get_status(String name) {
        return proxy.get_status(name);
    }

    public void close() {
        client.getEventLoop().shutdown();
        client.close();
    }
}
