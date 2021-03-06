package storm.elasticity;

import storm.elasticity.bolt.TestBolt;
import storm.elasticity.spout.TestSpout;
import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.TopologyBuilder;

public class LinearTopology {
	public static void main(String[] args) throws Exception {
		int numBolt = 3;
		int paralellism = 16;

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("spout_head", new TestSpout(), 20);

		for (int i = 0; i < numBolt; i++) {
			if (i == 0) {
				builder.setBolt("bolt_linear_" + i, new TestBolt(), 20)
						.shuffleGrouping("spout_head");
			} else {
				if (i == (numBolt - 1)) {
					builder.setBolt("bolt_output_" + i, new TestBolt(),
							20).shuffleGrouping(
							"bolt_linear_" + (i - 1));
				} else {
					builder.setBolt("bolt_linear_" + i, new TestBolt(),
							20).shuffleGrouping(
							"bolt_linear_" + (i - 1));
				}
			}
		}

		Config conf = new Config();
		conf.setDebug(false);
		conf.put(Config.TOPOLOGY_DEBUG, false);

		//conf.setNumAckers(0);

		conf.setNumWorkers(16);

		StormSubmitter.submitTopologyWithProgressBar(args[0], conf,
				builder.createTopology());

	}

}
