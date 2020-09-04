# Sereverless-Functions-Google-Cloud-Functions

## Prerequisites

This project is for packaging the application's functions to be deployed on Google Cloud Functions so first make sure you have a Google Cloud account:

https://console.cloud.google.com/getting-started

The Azure maven plugin must be as well imported to the project and configured porperly:

https://mvnrepository.com/artifact/com.google.cloud.functions/function-maven-plugin

In the plugin settings the function target must be set to define the entry point for the application in ordr to invoke the function. In our case it is the GcfJarLauncher because we are using Spring Cloud Functions.

A Firebase intsnce must be created and its dependency must be imported to the pom.xml.

https://mvnrepository.com/artifact/com.google.firebase/firebase-admin

## Configuration

Each function is defined as a Java Bean and has its own jar for deployment. The GcfJarLauncher will find the bean which includes the function (written in Spring Cloud Function)
and will invoke it if the correct input is given.

In order to deploy the function the Maven plugin is used via the following: 

https://cloud.spring.io/spring-cloud-static/spring-cloud-function/3.0.6.RELEASE/reference/html/gcp.html

API_KEY, AUTHDOMAIN, and ProjectID must be taken from the firestore instance and added enviroment variables in able to authenticate and establish connection with the database:

https://firebase.google.com/docs/firestore/quickstart#java_3
