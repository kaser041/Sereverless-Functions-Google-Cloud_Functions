/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.entities.Activity;
import com.example.entities.User;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootApplication
public class CloudFunctionMain {
    private boolean initialized = false;

    public static void main(String[] args) {
        SpringApplication.run(CloudFunctionMain.class, args);
    }

	/*@Bean
	public Function<String, Message<List<User>>> users() {

        return value -> {
			try {
                Message<List<User>> users = MessageBuilder.withPayload(getUsers()).setHeader("Access-Control-Allow-Origin","*").build();
				return users;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		};
	}*/

  /*  @Bean
    public Function<String, Message<List<Activity>>> activities() {
        return value -> {
            try {
                Message<List<Activity>> activities = MessageBuilder.withPayload(findActivityByUser(value)).setHeader("Access-Control-Allow-Origin","*").build();
                return activities;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        };
    }*/

   /* @Bean
    public Function<String, Message<User>> addUser() {
        return value -> {
            try {
                Gson g = new Gson();
                User user = g.fromJson(value,User.class);
                Message<User> addeUser = MessageBuilder.withPayload(addUser(user)).setHeader("Access-Control-Allow-Origin","*").build();
                return addeUser;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        };
    }*/
    @Bean
    public Function<String, Message<Activity>> addActivity() {
        return value -> {
            try {
                Gson g = new Gson();
                Activity activity = g.fromJson(value,Activity.class);
                Message<Activity> addedActivity = MessageBuilder.withPayload(addActivity(activity)).setHeader("Access-Control-Allow-Origin","*").build();
                return addedActivity;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        };
    }

    public List<User> getUsers() throws ExecutionException, InterruptedException, IOException {
        if (!initialized) {
            if (!initialized) {
                initializeFireStore();
            }
        }

        Firestore db = FirestoreClient.getFirestore();

        // asynchronously retrieve all users
        ApiFuture<QuerySnapshot> query = db.collection("Users").get();

        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        return documents.stream().flatMap(document -> Stream.of(document.toObject(User.class))).collect(Collectors.toList());
    }

    public List<Activity> findActivityByUser(String username) throws ExecutionException, InterruptedException, IOException {
        if (!initialized) {
            initializeFireStore();
        }
        Firestore db = FirestoreClient.getFirestore();

        // asynchronously retrieve all users
        ApiFuture<QuerySnapshot> query = db.collection("Activities").get();

        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments().stream().filter(document -> document.getString("user").contains(username)).collect(Collectors.toList());
        return documents.stream().flatMap(document -> Stream.of(document.toObject(Activity.class))).collect(Collectors.toList());
    }

    public User addUser(User user) throws ExecutionException, InterruptedException, IOException {
        if (!initialized) {
            initializeFireStore();
        }

        Firestore db = FirestoreClient.getFirestore();

        // asynchronously retrieve all users
        ApiFuture<QuerySnapshot> query = db.collection("Users").get();

        Map<String, Object> docData = new HashMap<>();
        docData.put("username", user.getUsername());

        ApiFuture<WriteResult> future = db.collection("Users").document().set(docData);

        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        System.out.println(documents.stream().flatMap(document -> Stream.of(document.toObject(User.class))).collect(Collectors.toList()));

        return user;
    }

    public Activity addActivity(Activity activity) throws ExecutionException, InterruptedException, IOException {
        if (!initialized) {
            initializeFireStore();
        }
        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> docData = new HashMap<>();
        docData.put("name", activity.getName());
        docData.put("description", activity.getDescription());
        docData.put("date", activity.getDate());
        docData.put("user", activity.getUser());

        ApiFuture<WriteResult> future = db.collection("Activities").document().set(docData);
        System.out.println(future.get());
        ApiFuture<QuerySnapshot> query = db.collection("Activities").get();

        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();
        System.out.println(documents.stream().flatMap(document -> Stream.of(document.toObject(Activity.class))).collect(Collectors.toList()));

        return activity;
    }

    public void initializeFireStore() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId("serverless-functions-279007")
                .build();
        FirebaseApp.initializeApp(options);
        initialized = true;
    }
}
