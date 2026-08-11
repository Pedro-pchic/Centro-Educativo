package com.umg.sgau;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
		classes = SgauBackendApiApplication.class,
		properties = {
				"jwt.secret=clave-de-prueba-segura-de-al-menos-32-caracteres",
				"spring.datasource.url=jdbc:h2:mem:sgau;MODE=PostgreSQL",
				"spring.datasource.driver-class-name=org.h2.Driver",
				"spring.jpa.hibernate.ddl-auto=create-drop"
		})
class SgauBackendApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
