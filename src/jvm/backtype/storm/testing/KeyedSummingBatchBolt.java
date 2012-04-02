package backtype.storm.testing;

import backtype.storm.coordination.BatchOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBatchBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import java.util.HashMap;
import java.util.Map;

public class KeyedSummingBatchBolt extends BaseBatchBolt {
    BatchOutputCollector _collector;
    Object _id;
    Map<Object, Integer> _sums = new HashMap<Object, Integer>();
    
    @Override
    public void prepare(Map conf, TopologyContext context, BatchOutputCollector collector, Object id) {
        _collector = collector;
        _id = id;
    }

    @Override
    public void execute(Tuple tuple) {
        Object key = tuple.getValue(1);
        int curr = Utils.get(_sums, key, 0);
        _sums.put(key, curr + tuple.getInteger(2));
    }

    @Override
    public void finishBatch() {
        for(Object key: _sums.keySet()) {
            _collector.emit(new Values(_id, key, _sums.get(key)));
        }
    }   

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("tx", "key", "sum"));
    }    
}
