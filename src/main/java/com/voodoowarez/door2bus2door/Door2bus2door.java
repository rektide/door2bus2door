package com.voodoowarez.door2bus2door;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import com.voodoowarez.door2bus2door.model.BusPosition;

public class Door2Bus2DoorApp {
	public String appName = "Door2Bus2Door";
	public String logFilename = "door2bus2door.log";
	public String master = "spark://localhost:7077";
	public SparkConf conf = new SparkConf();

	public String kafkaBootstrap = "127.0.0.1";
	public String zk = "127.0.0.1";
	public KafkaProducer busKafkaProducer;

	public void initialize(){
		if(this.conf == null){
			this.conf = new SparkConf();
		}
		this.conf.setAppName(this.appName);
		this.conf.setMaster(this.master);
		this.conf.registerKryoClasses(new Class[]{BusPosition.class});

		this.busKafkaProducer = producer();
	}
	
	public void run(){
		JavaSparkContext sc = new JavaSparkContext(this.conf);
		JavaRDD<String> logData = sc.textFile(this.logFilename).cache();

		long numAs = logData.filter(new Function<String, Boolean>() {
			public Boolean call(String s) { return s.contains("a"); }
		}).count();

		long numBs = logData.filter(new Function<String, Boolean>() {
			public Boolean call(String s) { return s.contains("b"); }
		}).count();

		System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);
	}
	
	public static void main(String[] args) {
		final Door2Bus2DoorApp app = new Door2Bus2DoorApp();
		app.initialize();
		app.run();
	}
	

	public KafkaProducer<String, String> producer(){
		final Map<String, Object> props = new HashMap<String, Object>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, (Object) kafkaBootstrap);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, 2);
		return new KafkaProducer<String, String>(props);
	}	
}
