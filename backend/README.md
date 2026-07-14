# Dual Database Demo

#### Run Unit Tests
`
./gradlew test --rerun
`

#### Run Integration Tests
`
./gradlew integrationTest --rerun
`

#### Start Application
`
./gradlew bootRun --args='--server.port=8080'
`

#### Run e2e Tests (in another TAB)
`
./gradlew e2eTests --rerun -Dtarget.url=http://localhost:8080
`

#### Test API
`
curl http://localhost:8080/api/posts | jq
`

If all goes well, you should see this: 
```json
[
  {
    "id": "5e95ee7b-ecf8-4a37-bd3e-115642b5da02",
    "title": "Post Title",
    "content": "Post Content",
    "comments": null
  }
]
```