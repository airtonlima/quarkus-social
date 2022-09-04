package io.github.airtonlima;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

import javax.ws.rs.core.Application;

@OpenAPIDefinition(
    info = @Info(
        title = "API Quarkus Social",
        version = "1.0.0",
        contact = @Contact(
            name = "Airton Lima",
            url = "http://airtonlima.com.br",
            email = "jose.airtonlimajr@gmail.com"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licences/LICENSE-2.0.html"
        )
    )
)
public class QuarkusSocialApplication extends Application { }

// Build and Run Jar File - to PROD
// BUILD: mvn clean package -DskipTests
// RUN: java -jar ./target/quarkus-app/quarkus-run.jar

// Build and Run With Docket - to PROD
// BUILD PROJECT: mvn clean package -DskipTests
// BUILD DOCKER IMAGE: docker build -f src/main/docker/Dockerfile.jvm -t quarkus-social:1.0.0 .
// RUN CONTAINER: docker run -i --rm -p 8080:8080 --name quarkus-social-container quarkus-social:1.0.0

