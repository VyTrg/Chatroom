package com.example.chatroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
<<<<<<< HEAD
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication

@EntityScan(basePackages = "com.example.chatroom.model")
public class ChatroomApplication {
	public static void main(String[] args) {
		SpringApplication.run(ChatroomApplication.class, args);
	}
}

=======

@SpringBootApplication
public class ChatroomApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatroomApplication.class, args);
	}

}
>>>>>>> 656a5f37f2d1e12b1886acd83faa1157c10a0f91
