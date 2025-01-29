package be.iccbxl.pid.reservationsspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "be.iccbxl.pid.reservationsspringboot")
public class ReservationsSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservationsSpringBootApplication.class, args);
	}

}