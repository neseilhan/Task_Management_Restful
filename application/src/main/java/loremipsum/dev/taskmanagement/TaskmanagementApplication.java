package loremipsum.dev.taskmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.SecureRandom;
import java.util.Base64;

@SpringBootApplication(scanBasePackages = "loremipsum.dev.taskmanagement")
public class TaskmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskmanagementApplication.class, args);
//
//		byte[] key = new byte[32];
//		new SecureRandom().nextBytes(key);
//		String base64Key = Base64.getEncoder().encodeToString(key);
//
//		System.out.println("Base64 Encoded Secret Key: " + base64Key);

	}

}
