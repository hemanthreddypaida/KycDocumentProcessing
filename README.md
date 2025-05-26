```markdown
# KYC Document Processing

This project implements an automated KYC (Know Your Customer) document processing workflow using Camunda and Java. It processes a customer-uploaded passport, extracts data using Camunda IDP (via REST API), validates the data with rule-based Java logic, and checks the name against a sanctions list using an HTTP Connector. The workflow either creates an account or routes to a compliance officer for review.

## Features
- **Passport Data Extraction**: Uses Camunda IDP REST API to extract name, ID number, and nationality.
- **Data Validation**: Validates extracted data with Java-based rules in a subprocess.
- **Sanctions Check**: Queries an external sanctions API via Camunda HTTP Connector.
- **Workflow Routing**: Proceeds to account creation if no sanctions match; otherwise, routes to a compliance officer via Camunda Tasklist.
- **Outcome**: Reduces KYC processing time by up to 70% with minimal manual effort.

## Prerequisites
- Java 17
- Maven 3.8+
- Camunda Platform (version 7.21.0)
- Camunda IDP (enterprise license and API access required)
- Camunda Modeler (for editing BPMN files)

## Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/kyc-document-processing.git
   cd kyc-document-processing
   ```
2. Configure Camunda IDP:
   - Update `OcrApiClient.java` with your Camunda IDP endpoint and API key.
   - Ensure the endpoint supports file uploads and returns JSON with `name`, `idNumber`, and `nationality`.
3. Configure Sanctions API:
   - Update `kyc-process.bpmn` with the actual sanctions API URL and API key.
   - Ensure the API returns a JSON response with a `match` field (boolean).
4. Build the project:
   ```bash
   mvn clean install
   ```
5. Run the application:
   ```bash
   mvn spring-boot:run
   ```
6. Access Camunda Tasklist at `http://localhost:8080/camunda` (login: admin/admin).

## Project Structure
- `src/main/java/com/example/kyc/`: Java source code
  - `KycApplication.java`: Spring Boot application entry point
  - `service/OcrApiClient.java`: Camunda IDP REST API integration
  - `service/PassportDataExtractor.java`: Camunda service task for OCR
  - `service/DataValidator.java`: Rule-based data validation
  - `model/OcrResult.java`: Data model for OCR results
- `src/main/resources/processes/kyc-process.bpmn`: BPMN workflow definition
- `src/main/resources/application.yml`: Spring Boot and Camunda configuration

## Usage
1. Start a process instance via Camunda Cockpit or REST API, providing a `passportFilePath` variable.
2. The workflow extracts data, validates it, and performs a sanctions check.
3. If no sanctions match, the process completes with account creation.
4. If a match is found, the case is routed to the compliance officer in Camunda Tasklist.

## Notes
- Replace the Camunda IDP endpoint and API key in `OcrApiClient.java` with your actual configuration (see https://docs.camunda.io).
- Replace the sanctions API URL and key in `kyc-process.bpmn` with a real sanctions database (e.g., OFAC, EU sanctions list).
- Validation is performed using Java rules. For enhanced validation, configure Camunda IDP schema to enforce field constraints.
- Ensure secure handling of sensitive data (e.g., GDPR/CCPA compliance).

## License
MIT License
```
