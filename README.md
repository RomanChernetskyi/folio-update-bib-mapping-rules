# FOLIO Update Bib Mapping Rules

This project is a script-based application for updating mapping rules in the FOLIO library system. It is built using Java, Spring Boot, and Maven.

## Prerequisites

- Java 21
- FOLIO environment

## Getting Started

### Clone the repository

```sh
git clone <repository-url>
cd folio-update-bib-mapping-rules
```

### Select release (eg. Ramsons, Sunflower, etc.)

```sh
git checkout <release>
```

### Build the project

```sh
mvn clean install
```

## Script execution

1. Create JSON file with configuration (see: [configuration file](#configuration-file))
2. Open terminal (Mac OS or UNIX Systems) or Power shell (Windows)
3. Go to the folder where the CLI tool executable artifact is located
4. Run the CLI tool/script with the configuration file path parameter (just file name if the script is located in the same folder)

## Configuration file

* okapiUrl - your library okapi url. (Can be seen in the app settings → software versions → okapi services → okapi)
* tenant - target tenant
* username - admin user name
* password - admin user name password

### Example

```
{
"okapiUrl": "https://folio-snapshot-okapi.dev.folio.org",
"tenant": "diku",
"username": "admin",
"password": "secret"
}
```
For consortia tenants add, additional centralTenant field in configuration.json

```
{
"okapiUrl": "https://folio-snapshot-okapi.dev.folio.org",
"tenant": "diku",
"centralTenant":"consortia_admin"
"username": "consortia_admin",
"password": "consortia_secret"
}
```