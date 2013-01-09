/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package example.classifier.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import us.jubat.classifier.Datum;
import us.jubat.classifier.TupleStringDouble;
import us.jubat.classifier.TupleStringString;

/**
 *
 * @author <a href="https://github.com/naokikimura">naokikimura</a>
 */
public class DatumBuilder {
    private List<TupleStringString> strings = new ArrayList<>();
    private List<TupleStringDouble> nums = new ArrayList<>();

    public Datum create() {
        Datum datum = new Datum();
        datum.num_values = nums;
        datum.string_values = strings;
        return datum;
    }

    public DatumBuilder reset() {
        strings.clear();
        nums.clear();
        return this;
    }

    public static TupleStringDouble createTuple(String first, double second) {
        TupleStringDouble tuple = new TupleStringDouble();
        tuple.first = first;
        tuple.second = second;
        return tuple;
    }

    public static TupleStringString createTuple(String first, String second) {
        TupleStringString tuple = new TupleStringString();
        tuple.first = first;
        tuple.second = second;
        return tuple;
    }

    public DatumBuilder addTuple(String first, String second) {
        return add(createTuple(first, second));
    }

    public DatumBuilder add(TupleStringString... tuple) {
        Collections.addAll(strings, tuple);
        return this;
    }

    public DatumBuilder setTuple(String first, String second) {
        return set(createTuple(first, second));
    }

    public DatumBuilder set(TupleStringString... tuple) {
        strings.clear();
        return add(tuple);
    }

    public DatumBuilder addTuple(String first, double second) {
        return add(createTuple(first, second));
    }

    public DatumBuilder add(TupleStringDouble... tuple) {
        Collections.addAll(nums, tuple);
        return this;
    }

    public DatumBuilder setTuple(String first, double second) {
        return set(createTuple(first, second));
    }

    public DatumBuilder set(TupleStringDouble... tuple) {
        nums.clear();
        return add(tuple);
    }
    
}
