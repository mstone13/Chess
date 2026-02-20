# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Phase 2 Diagram Link: 
[Diagram Page](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdADZMAek8AlAKIA4gCSZAAqfj7wAPLSfpj2UBAArtgAxAAsAMwAHABMAJwgMKnI9gAWYDoISYY+KPZIqhZySBBomIiopAC0AHzklDRQAFwwANoAClFhALownkkGUAA6aADeAESLlGjAALYoG8MbMBsANKe46gDu0BxHJ+enKHvASAgPpwC+mMJDMH1WOwuJRRpttlBdgdPk8NldVLcoPdjqcLhsXm8PiiNj82JxuLBAX9RKMoPVGmBKAAKOoNJqUOoARxqTQAlL9BqIAf1ZPIlCp1KN7CgwABVJZUiFQlDs3mKZRqVTcow6YYAMSQnBg4socpgOksMCl+zEOjJwAA1tqljBrkgwGUjUtpTBgAhzRxDSgAB4UjRy-mKgFEzkqUY6qByjkiFTB-p-UEwBTulDAT2hS3oPzelTYAhtaP-bqAzrmUbpJyZVabA7qYDCo6nPxQRIjJ07E2ulNpw3yC3oHFmTiYAMK9RxgYxlCjNBJBAIQtcwGjgWqYYgc2UiOSpZy2XaQPjwHGYYKDgca267SL2PLg9jtcb1OUhRJB1U4Bvsp7kf31fKk8zwvV8HSjYlb0BPEQTbWkKQZNQ5ywKCCQnBM23BZ0TROUZHjRT8HVCCB+zQbDTm+QtKAnUsMFGXInCcastkw6EYBw1FTnwspCOI0jHh+dAOFMbwABkomCAA5aJYniRIUlSaB2GFYo1TgPxpDgBQYGEiAGnaajmBDahE0maZQjmTwDHUVo0EY41oWxNF4URZE+Iowl+mQxMMI7ezcMufQETuGFcWBFDDKoEkYAQHTNSpbTdKZFkwHZTAzVTK07MMaAYCcu4YEaGBZ3nC4XjzSwLmyzUqDdJBBPAwxi3jQZEwAITTRK1DAbNc3zdo0Ko5AyxgCsGLQGAa0VetDlYpsW2gUY9AvMlmU6wcBNMerlRXRUhRFbdMv3PkHwA1UYA1LUIz1A120hE1TDSy1L0jbQYAAXlewq5wQGBstypElQ4CBCogZhXjAEAyl-I7-0aycIrDT75xvBrwsTUVdi-aAkAALxQDhupQPNrLcgaujAcsnAARkY2tVCmxsNmbVtRiSDGHSx3H7n44dtqPcLIsu69Nv5hGhiQAAzSwJgC5yqT+jgLiaaA8ZS5Gtr-HaYCfOQUBAsoPy-H9eaVY9TqApNDaF0MUY80LE3izU6lURDME8wlUfQpifMOByOK-biBzYwc0Nh-TaPo2zmJ9vyNk4gOSKD7nBK8TxRICKJRVCKS4gSZI0gSFB0GKUoKgLov4uSLB9OVNDRjGWJhL8cJzMs1RrNWOOiPQEnILtttOI5lo2lGAAeTviN6V2+5r63Rmi+xK7inTK46tk1bvaHNeFMA9YNgiu7QQ75Rh-oTzOzVgMt+R9UNcfu9S80rTvsaCqqmqFZgPZGjbhwKtgQG1DA1BnICGUNj5BlhvVGcX114e3DGzMoHM8YEyJgWfqodBo0WGlTGmk0Gw4SZvNI0CCkFcyHMnY2qFZ4W1AlbKc6tN6CiMCgbgL4vx7y4gfI+h4Tan1OtIFhIpDB6zAtbCcbtRgVzfE7F2bsqFGXQj8dBJZMHkxgHRUa40k5CVTiELOAQFAAFk-BkGzjJPOqQyQXlSNgTUVphIUhgAEE0Ghq5wPGAEJu5l7Amg7v7A+Pdbb4kTAPKAOMh5oFHs-SeciRbTiihSHxtY4oUmcbWVeyV148g1kw7eu9n7cOOqbUY51L60OvtdZ+pgH7pVdP44i+UlRv2QB-L+Vlf4-X-hAQBaAQafxAZDShkDqFFQXMLdx6NQk42QTmQmvUe4qLJhTam6xTi03pgQuabZWZTM5icbRYCeHyPhvEkRdCTkMPAUw5ATQ0lqCpIUk+KoSkXycSaKMQy4mjDuVGWBQToKSNSS4mRCAkLT2GQowFTQR79DWKcJJahGxjE2AioI0hGyU1yJkdIaJrhlHtCgCM0oHjnFWKcKooALREqwscWECLxImlpV8GAMwlHNXdv0MO6iI5jThRsBFqgkUopNGijFWKcWnDxQS6lvlSVjQ2BSkAVKo4krRPSxlGwcQsoOd4OA-gFDhCcUYvwZjc5yQ4AAdnyE4FAThih+EyHAVSbh4CbkMHcmAHRVEz0heMKYsx5gIr8fvYi1Z1UHDZQo5UEi6nszCdjCJUT6noF6BcNY4aUAhWCe5OGkVtaUgRVSOAbq7kZJSg9K0GbGmI2+tlUqYBLB-Lhq1dqKAVpNBQfM5RnLVEU1Gny9Z+DZrM31GmGAy0kprWHFkmQOS1wwDyewgphyil8JeVqM5FTb7JvaDUx6z9q3NNqhcNpP97B-yMN0pUvTgHg0GXO45kVRlNtrtqEh8aZk9WJt2+AvbsErIHXgn2w6iE7Mxh++4MADmfKalOUYm7gAzqgVrN1byDhyipBm9kwtsmMPnfmlAdyqQ-O0I8iBa6kznjQygD5D6IUnO+e885S5e7ZtGMW58hGTQgrBdm45Xl4UivRaMTF2KoMLJ7Us7l-bBMHFFSJ8V4n1opwAFJRCCJJAxxjTWyTSJYFh0VrjFyQOUBtBmIBGZUxATU1HiiKotF6smPqhh1wmJnbxviEHx2rNgBAwB9NQDgBAaKUALioukJGos-yCSjF2YmmAY8d2ppgKsAAkHy3z-nKBBZCw8AA6iwII4RPAtWEgoOAABpOlQmxViazdBZzkUABW1mHAmipC1zUpa21JXLY-HKfmAs5egJ4cL1bRmdJgPWxt4zYMuZgG1DgGTO3fvZaTIaI1qxrKAwzQhbZFrjp66tJT06cOzrw7tHeS6d1kaPBR0pNDvwvUqTu+6-WD2vzQNVFpJ7v6anPZNgB16+lg1ATB3NCNn2zebW2SZ4Hpn41magvqa2MFSYrAB7bdYh2My2Szd9CP9nkNMOD5DCGZ3G0u0R8Lt3eHPIeyRrdC6hMrphl86jvyK0Day4F4LlVgfMCh2I+jiZkweksBmYiK20Go8WRtysW2NiDuA7jkdbpxcwD7AOE7ydocxqs117jCFQVTz4yLxREnf1SY0dWHVng4CNwUJEBQEwJjCSCBpUIQQoiSTgDEHOunUgJH88Z0zwfDSIFTLAYA2BfOEAiZ6txc3jINybn4cyxhAlAjY1o03DX2cgG4HgBQsfkAgAiUWovUAS9x-L71MtFO52jEL1HiMqgHms-I88mQgjKRdm+oLeQjeLtayr3c9vtOTqjAEawwwbpvqM8Q2dyno+o+70n8Unvs-++Pd+Xrvu7Gq817LxEnjeewruLWJFyiaOho2-WNooAA)

