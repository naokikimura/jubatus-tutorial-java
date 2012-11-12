package example;

import java.io.*;
import java.net.URL;
import java.util.*;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import us.jubat.classifier.*;

/**
 * <p>
 * see <a href="https://github.com/jubatus/jubatus-tutorial-python/blob/master/tutorial.py">jubatus / jubatus-tutorial-python / tutorial.py</a>
 * </p>
 * 
 * @author <a href="https://github.com/naokikimura">naokikimura</a>
 */
public class App {

    public static void main(String[] args) throws Exception {
        Options options = buildOptions();

        CommandLineParser parser = new PosixParser();
        CommandLine cl = parser.parse(options, args);

        if (cl.hasOption("h")) {
            HelpFormatter help = new HelpFormatter();
            help.printHelp(App.class.getName(), options, true);
            return;
        }

        String id = "tutorial";
        String name = cl.getOptionValue("n", "tutorial");

        String host = cl.getOptionValue("s", "127.0.0.1");
        int port = Integer.parseInt(cl.getOptionValue("p", "9199"));
        double timeout_sec = 10.0;
        ClassifierClient client = new ClassifierClient(host, port, timeout_sec);
        try {
            ConfigData conf = new ConfigData();
            conf.method = cl.getOptionValue("a", "PA");
            conf.config = loadConverter(App.class.getResource("converter.json")).toString();

            client.set_config(name, conf);

            printConfig(System.err, client, name);
            printStatus(System.err, client, name);

            train(client, name, App.class.getResource("train.dat"));

            client.save(name, id);
            client.load(name, id);

            client.set_config(name, conf);
            printConfig(System.err, client, name);

            classify(client, name, App.class.getResource("test.dat"));
        } finally {
            client.close();
        }
    }

    private static void train(ClassifierClient client, String name, URL url) throws IOException {
        InputStream is = url.openStream();
        try {
            LineIterator it = IOUtils.lineIterator(is, "UTF-8");
            while (it.hasNext()) {
                String[] row = it.nextLine().split(",", 2);
                String label = row[0];
                String file = row[1];

                URL resource = App.class.getResource(file);
                if (resource == null) {
                    throw new FileNotFoundException("not found " + file);
                }
                Datum datum = createDatum(loadMessage(resource));
                TupleStringDatum trainDatum = new TupleStringDatum();
                trainDatum.first = label;
                trainDatum.second = datum;
                client.train(name, Arrays.asList(trainDatum));
                printStatus(System.err, client, name);
            }
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private static void classify(ClassifierClient client, String name, URL url) throws IOException {
        InputStream is = url.openStream();
        try {
            LineIterator it = IOUtils.lineIterator(is, "UTF-8");
            while (it.hasNext()) {
                String[] row = it.nextLine().split(",", 2);
                String label = row[0];
                String file = row[1];

                URL resource = App.class.getResource(file);
                if (resource == null) {
                    throw new FileNotFoundException("not found " + file);
                }
                Datum datum = createDatum(loadMessage(resource));
                List<List<EstimateResult>> ans = client.classify(name, Arrays.asList(datum));
                EstimateResult estm = getMostLikely(ans.get(0));
                String result = label.equals(estm.label) ? "OK" : "NG";
                System.out.printf("%s,%s,%s,%f%n", 
                        result, label, estm.label, estm.prob);
            }
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private static Datum createDatum(String message) throws IOException {
        TupleStringString stringValue = new TupleStringString();
        stringValue.first = "message";
        stringValue.second = message;

        Datum datum = new Datum();
        datum.string_values = Arrays.asList(stringValue);
        datum.num_values = Collections.<TupleStringDouble>emptyList();
        return datum;
    }

    private static String loadMessage(URL url) throws IOException {
        InputStream is = url.openStream();
        try {
            return IOUtils.toString(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private static EstimateResult getMostLikely(List<EstimateResult> EstimateResults) {
        return Collections.max(EstimateResults, new Comparator<EstimateResult>() {

            @Override
            public int compare(EstimateResult o1, EstimateResult o2) {
                return o1.prob == o2.prob ? 0 : o1.prob < o2.prob ? -1 : 1;
            }
        });
    }

    private static Options buildOptions() throws IllegalArgumentException {
        Options options = new Options();
        
        options.addOption(OptionBuilder.create('h'));

        OptionBuilder.withDescription("server_ip");
        OptionBuilder.withLongOpt("server ip");
        OptionBuilder.withType(String.class);
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("s"));

        OptionBuilder.withDescription("server_port");
        OptionBuilder.withLongOpt("server port");
        OptionBuilder.withType(Number.class);
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("p"));

        OptionBuilder.withDescription("name");
        OptionBuilder.withLongOpt("name");
        OptionBuilder.withType(String.class);
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("n"));

        OptionBuilder.withDescription("algo");
        OptionBuilder.withLongOpt("algo");
        OptionBuilder.withType(String.class);
        OptionBuilder.hasArg();
        options.addOption(OptionBuilder.create("a"));

        return options;
    }

    private static JSONObject loadConverter(URL url) throws IOException, ParseException {
        InputStream is = url.openStream();
        try {
            JSONParser jsonParser = new JSONParser();
            return (JSONObject) jsonParser.parse(new InputStreamReader(is));
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private static void printStatus(PrintStream out, ClassifierClient client, String name) {
        Map status = client.get_status(name);
        out.println(JSONObject.toJSONString(status));
    }

    private static void printConfig(PrintStream out, ClassifierClient client, String name) {
        ConfigData conf = client.get_config(name);
        out.println(String.format("{\"method\":\"%s\",\"config\":%s}", conf.method, conf.config));
    }
}