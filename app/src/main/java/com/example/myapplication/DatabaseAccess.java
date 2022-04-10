package com.example.myapplication;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class DatabaseAccess {
    private String TAG = "APP_Database";

    private final String COGNITO_IDENTITY_POOL_ID = "";
    private final Regions COGNITO_IDENTITY_POOL_REGION = Regions.US_WEST_1;
    private final String DYNAMODB_TABLE = "App_Table";
    private Context context;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDBClient dbClient;
    private Table dbTable;

    private static volatile DatabaseAccess instance;

    private DatabaseAccess(Context context) {
        this.context = context;

        //create new credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider (context, COGNITO_IDENTITY_POOL_ID, COGNITO_IDENTITY_POOL_REGION);
        //Create a connection to the DynamoDB service
        dbClient = new AmazonDynamoDBClient(credentialsProvider);
        /*Must set db client region here so it does not select default*/
        dbClient.setRegion(Region.getRegion(Regions.US_WEST_1));
        //Create a table reference
        dbTable = Table.loadTable(dbClient, DYNAMODB_TABLE);

    }

    public static synchronized DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

}

