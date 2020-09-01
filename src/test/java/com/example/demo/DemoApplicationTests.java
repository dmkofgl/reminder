package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.ApiContextInitializer;

import java.time.Period;
import java.time.temporal.ChronoUnit;

@SpringBootTest
class DemoApplicationTests {

	static{
		ApiContextInitializer.init();

	}
	@Test
	void contextLoads() {

		String s = "P1Y2m";
		Period p =Period.parse(s);
		System.out.println(p);
	}

}
