package com.messagehub;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.PostConstruct;
import java.util.Scanner;

@EntityScan("com.messagehub.model")
@EnableMongoRepositories("com.messagehub.repository")
@SpringBootApplication
public class RealTimeMessageSharingHubApplication {

	public static void main(String[] args) throws InterruptedException {
		ConfigurableApplicationContext context = SpringApplication.run(RealTimeMessageSharingHubApplication.class, args);
		checkMongoDBConnection(context);
	}

	@Bean
	CommandLineRunner commandLineRunner(KafkaTemplate<String, String> kafkaTemplate) {
		return args -> {
			for (int i = 0; i < 100; i++) {
				kafkaTemplate.send("messagehub", "hello world :) " + i);
			}
		};
	}

	public static void checkMongoDBConnection(ConfigurableApplicationContext context) throws InterruptedException {
		System.out.println("Checking MongoDB connection...");

		try {

			String connectionString = "mongodb://rootuser:rootpass@localhost:27017/admin";
			ConnectionString connString = new ConnectionString(connectionString);


			MongoClientSettings settings = MongoClientSettings.builder()
					.applyConnectionString(connString)
					.build();
			com.mongodb.client.MongoClient mongoClient = MongoClients.create(settings);


			mongoClient.listDatabaseNames().first();

			System.out.println("MongoDB connection successful!");

			System.out.println("Pressione qualquer tecla para encerrar o aplicativo...");
			Scanner scanner = new Scanner(System.in);
			scanner.nextLine();

		} catch (Exception e) {
			System.out.println("MongoDB connection failed!");
			e.printStackTrace();

		} finally {
			context.close();
		}
		}

}
