#!/bin/bash

# Wait a moment to ensure DynamoDB is ready to accept connections
sleep 2

echo "Creating Posts table..."
aws dynamodb create-table \
    --endpoint-url http://dynamodb-local:8000 \
    --table-name Posts \
    --attribute-definitions AttributeName=id,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5

echo "Populating initial test data..."
aws dynamodb put-item \
    --endpoint-url http://dynamodb-local:8000 \
    --table-name Posts \
    --item '{"id": {"S": "5e95ee7b-ecf8-4a37-bd3e-115642b5da02"}, "title": {"S": "Post Title"}, "content": {"S": "Post Content"}}'



echo "DynamoDB initialization complete!"