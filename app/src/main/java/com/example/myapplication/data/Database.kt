package com.example.myapplication.data

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import aws.sdk.kotlin.services.dynamodb.model.DynamoDbException
import kotlin.system.exitProcess

class Database {

    private val credentials = StaticCredentialsProvider {
        accessKeyId = "AKIASQU22YWFEKLJGRFU"
        secretAccessKey = "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"
    }

    private val ddb = DynamoDbClient{
        region = "us-west-1"
        credentialsProvider = credentials
    }

    suspend fun putItemInTable(
        tableNameVal: String,
        key: String,
        keyVal: String,
        name: String,
        nameValue: String,
        email: String,
        emailVal: String,
        birthday: String,
        birthdayVal: String
    ) {
        val itemValues = mutableMapOf<String, AttributeValue>()

        // Add all content to the table.
        itemValues[key] = AttributeValue.S(keyVal)
        itemValues[name] = AttributeValue.S(nameValue)
        itemValues[email] = AttributeValue.S(emailVal)
        itemValues[birthday] = AttributeValue.S(birthdayVal)

        val request = PutItemRequest {
            tableName = tableNameVal
            item = itemValues
        }

        try {
            ddb.putItem(request)
            println(" A new item was placed into $tableNameVal.")
        } catch (ex: DynamoDbException) {
            println(ex.message)
            ddb.close()
            exitProcess(0)
        }
    }
}