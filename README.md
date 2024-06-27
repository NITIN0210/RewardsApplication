### Setup Instructions
- Clone the repository to your local machine:
    `git clone https://github.com/nitin0210/RewardTask.git`
- Navigate to the project directory:
    ` cd your-project `
- Open the project in your preferred IDE (e.g., IntelliJ IDEA, Eclipse).
- Configure the database connection in the application.properties file:
- properties
    - `spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name `
    - ` spring.datasource.username=your_username `
    - ` spring.datasource.password=your_password `
    - Execute the data.sql and schema.sql file for the db 
- Run the application using Maven:
    ` mvn spring-boot:run `
- The application will start, and you can access it at http://localhost:8080.

  
### Sample Rest Request
Customer 2: This customer has a few transactins over the months 2023-05 to 2023-07
- #### URL
GET 'http://localhost:8080/api/rewards/for-customer/2?startMonth=2023-05&endMonth=2023-07)' 

- This returns a JSON object of the customer 2's month wise rewards
- If not valid customer or invalid date range respective error will be returned.

##### For more Rest endpoints see the documentation by folllowing the step below
- Swagger Documentation URI
- Access the Swagger documentation for the API endpoints at:
     ` http://localhost:8080/swagger-ui/index.html `
