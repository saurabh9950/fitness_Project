Fitness Project

A Spring Boot based Fitness Management Application designed to help users track workouts, manage fitness activities, and maintain a healthy lifestyle. This project demonstrates the use of Java, Spring Boot, REST APIs, and database integration to build a scalable backend system for fitness-related services.

🚀 Features

User registration and authentication

Manage fitness activities

Track workouts and progress

RESTful API architecture

Database integration

Scalable backend using Spring Boot

Clean layered architecture

🛠 Tech Stack

Backend

Java

Spring Boot

Spring Data JPA

Hibernate

Database

MySQL

Tools

Maven

IntelliJ IDEA

Git & GitHub

Postman (API testing)

Docker 

📂 Project Structure
fitness_project
│
├── controller        # REST API controllers
├── service           # Business logic
├── repository        # Database layer
├── model / entity    # Database entities
├── config            # Configuration classes
└── FitnessProjectApplication.java
⚙️ Installation & Setup
1️⃣ Clone the Repository
git clone https://github.com/saurabh9950/fitness_Project.git
2️⃣ Navigate to Project
cd fitness_Project
3️⃣ Configure Database

Update your application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/fitness_db
spring.datasource.username=root
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
4️⃣ Run the Application

Using Maven:

mvn spring-boot:run

Or run the main class:

FitnessProjectApplication.java


