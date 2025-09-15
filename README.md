# Distributed Systems Group Project 2024-2025

This application was developed as a group project for the **Distributed Systems** course at [Harokopio University of Athens – Dept. of Informatics and Telematics](https://www.dit.hua.gr).

# Apartment Rental Management System

## Contents
- [Overview](#overview)
- [Features](#features)
- [Email Notifications](#email-notifications)
- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage Instructions](#usage-instructions)
- [Role Management](#role-management)
- [Project Structure](#project-structure)
- [Contact](#contact)
- [Authors](#authors)
- [License](#license)

## Overview
The Apartment Rental Application is a web-based platform that allows users to:
- View available apartments for rent.
- Rent apartments by providing tenant details.
- Submit new apartments by providing necessary details.
- Manage user roles (e.g., User, Owner, Tenant).

## Features
- **View Apartments**: Browse the list of available apartments.
- **Submit Apartments**: Users can create new apartments by submitting their details.
- **Owner Role Assignment**: Users creating apartments for the first time automatically get assigned the `ROLE_OWNER` role and are prompted to provide their details to become owners.
- **Tenant Management**: Users renting apartments for the first time automatically get assigned the `ROLE_TENANT` role and are prompted to provide their details to become tenants.
- **Access Control**: Role-based access ensures only authorized users can perform certain actions.

## Email Notifications

**Note:** This feature was implemented independently by *Exarchou Athos* after the completion of the official *Distributed Systems* coursework. It is not part of the original course requirements or deliverables.

The platform provides automatic email notifications to improve user engagement and administrative transparency. Emails are sent for the following actions:
- **New User Registration** – A welcome message is sent upon account creation.
- **Account Deletion** – A notice is sent when a user account is deleted by the admin.
- **User Info Update** – A confirmation email is sent when personal user information is changed by the admin.
- **Apartment Creation** – Owners receive a confirmation when they submit a new apartment.
- **Apartment Approval** – Owners are notified once their submitted apartment is approved by the admin.
- **Apartment Deletion** – Owners are notified when their apartment listing is successfully removed.
- **Rent Application Approval** – Tenants receive an email when their rental application is approved by the owner.

All emails are sent using HTML templates for a professional and user-friendly look and feel.

## Requirements
To run this application, ensure you have the following installed:
- **Java**: Version 21 or higher.
- **Spring Boot**: Version 3.x.
- **Thymeleaf**: For front-end templates.
- **Database**: any (for development).

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/AthosExarchou/ds-exc-2024.git
   ```
2. Open 'ds-exc-2024' in your preferred IDE
3. Press 'Run' to boot the application
    > Or, via terminal, you can run: `mvn spring-boot:run`
4. Access the app in your browser at http://localhost:8080/

## Configuration

Before running the application, ensure you configure `src/main/resources/application.properties`.

### Required Properties:

1. **Application Name and Server Port**
   ```properties
   spring.application.name=<Your Application Name>
   server.port=8080
   ```
2. **Database Connection**
   ```properties
   spring.datasource.username=<DATABASE_USERNAME>
   spring.datasource.password=<DATABASE_PASSWORD>
   spring.datasource.url=jdbc:mysql://<HOST>:<PORT>/<DB_NAME>
   ```
3. **Gmail SMTP Settings (for Email Notifications)**
   ```properties
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=realestate2025project@gmail.com
   spring.mail.password=<YOUR_APP_PASSWORD>
   spring.mail.protocol=smtp
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```
   
## Usage Instructions

### For Tenants
1. Log in with your credentials.
2. Visit the "Apartments For Rental" page to view available apartments.
3. Click on the "Application for Rental" button for the desired apartment.
4. If renting for the first time, fill in your details (First Name, Last Name, Phone Number).
5. Submit the form to complete the rental process.

### For Owners
1. Log in with your credentials.
2. Navigate to the "Apartments For Rental" page.
3. Click on the "Create new Apartment" button.
4. Fill in the details of the apartment you want to list.
5. Submit the form to make the apartment available for rent (will first have to be approved by the administrator).

### Access Restrictions
- Only owners can delete apartments they have created.
- Tenants can only rent apartments that are available.

## Role Management

- **Users**: Default role upon registration.
- **Owners**: Assigned automatically when a user creates their first apartment.
- **Tenants**: Assigned when a user applies to rent an apartment for the first time.

## Project Structure

```text
src/main/java/gr/hua/dit/ds/ds_exc_2024/
│
├── config/                   # Configuration classes (security, initialization, etc.)
├── controllers/              # REST & MVC controllers (handle HTTP requests)
├── entities/                 # JPA entities (User, Role, Apartment, etc.)
├── repositories/             # Spring Data JPA repositories
├── services/                 # Business logic layer
└── DsExc2024Application.java # Main Spring Boot entry point

src/main/resources/
│
├── static/                # Static resources (CSS, JS, images)
├── templates/             # Thymeleaf templates (UI pages)
│   ├── apartment/         # Views related to apartments
│   ├── auth/              # Authentication & user management pages
│   ├── contact/           # Contact form pages
│   ├── email/             # Email templates
│   ├── error/             # Custom error pages
│   ├── owner/             # Owner-related pages
│   ├── page_layout/       # Common layout fragments (header, footer, etc.)
│   ├── tenant/            # Tenant-related pages
│   └── index.html         # Homepage
├── application.properties # App configuration (DB, SMTP, etc.)
```

## Contact
For support or inquiries:
- **Email**: realestate2025project@gmail.com
- **Website**: [localhost:8080](http://localhost:8080/)

## Authors

| Student ID   | Name                          | Contribution Context                                     |
|--------------|-------------------------------|----------------------------------------------------------|
| it2022134    | Exarchou Athos                | Distributed Systems course + Independent Email Feature   |
| it22149      | Alexandros-Georgios Zarkalis  | Distributed Systems course                               |

## License
This project is licensed under the **MIT License**.


