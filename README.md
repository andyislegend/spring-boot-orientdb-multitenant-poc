# spring-boot-orientdb-multitenant-poc

This Spring Boot REST API application is the Proof of Concept for using OrientDB (https://orientdb.com/) as provider of the multi-tenant architecture.

The Multi-tenant data model for this app is built on top of the demo database which OrientDB 
provides out of the box (https://orientdb.com/docs/3.0.x/gettingstarted/demodb/DemoDB-DataModel.html).

The part of the data (Countries, Customers vertices; IsFromCountry, HasProfile edges) has been partitioned by tenant.
Actually the first 100 Countries and their referenced data - have been assigned to tenant 'client1',
the second 100 Countries and references - respectively to tenant 'client2'.     

# Prerequisites

1. docker
2. docker-compose
3. curl
4. unzip

# How to use

To run this application on your machine you can use the following command:

    sh startup.sh
    
To stop the application:

    sh shutdown.sh

# REST API and End Points

If everything is fine after startup you should be able to access REST endpoints by executing HTTP 
request GET localhost:8001 (the port could be changed in src/main/resources/application.yml)

In order to determine the Tenant we will use the “X-TenantID” HTTP header, 
otherwise return the appropriate error in the response.

Example:

    curl -X GET http://localhost:8001/api/countries
    
    {
        "error": "No tenant header (X-TenantID) supplied"
    } 

## GET /api/countries

Returns countries for particular Tenant

Example:

    curl -X GET http://localhost:8001/api/countries -H 'X-TenantID: client1'

    [
        {
            "CountryId": "66",
            "CountryName": "Western Sahara",
            "CountryCode": "EH"
        },
        {
            "CountryId": "25",
            "CountryName": "Benin",
            "CountryCode": "BJ"
        },
        ...
    ]

    200 OK
    
    curl -X GET http://localhost:8001/api/countries -H 'X-TenantID: client2'

    [
        {
            "CountryId": "167",
            "CountryName": "Norway",
            "CountryCode": "NO"
        },
        {
            "CountryId": "157",
            "CountryName": "Mexico",
            "CountryCode": "MX"
        },
        ...
    ]

    200 OK    

## GET /api/countries/{country}/customers

Returns Country's customers for particular Tenant

Example:

    curl -X GET http://localhost:8001/api/countries/EH/customers -H 'X-TenantID: client1'

    [
        {
            "customerSurname": "Carlson",
            "customerPhone": "+1253863166",
            "profileId": "604",
            "customerEmail": "bocfen@vi.edu",
            "customerBirthday": "Thu Oct 26 23:00:00 UTC 1950",
            "customerName": "Todd",
            "customerCountry": "Western Sahara"
        },
        {
            "customerSurname": "Gill",
            "customerPhone": "+1915331309",
            "profileId": "161",
            "customerEmail": "zuanodus@nijeon.com",
            "customerBirthday": "Fri Mar 03 23:00:00 UTC 1978",
            "customerName": "Myrtle",
            "customerCountry": "Western Sahara"
        }
    ]

    200 OK
    
    curl -X GET http://localhost:8001/api/countries/NO/customers -H 'X-TenantID: client2'

    [
        {
            "customerSurname": "Clark",
            "customerPhone": "+1577125176",
            "profileId": "66",
            "customerEmail": "jekrav@bagow.co.uk",
            "customerBirthday": "Fri Aug 02 23:00:00 UTC 1957",
            "customerName": "Keith",
            "customerCountry": "Norway"
        },
        {
            "customerSurname": "Shelton",
            "customerPhone": "+1667505489",
            "profileId": "776",
            "customerEmail": "zuj@dacidda.gov",
            "customerBirthday": "Fri Aug 16 23:00:00 UTC 1963",
            "customerName": "Larry",
            "customerCountry": "Norway"
        }
    ]

    200 OK    
