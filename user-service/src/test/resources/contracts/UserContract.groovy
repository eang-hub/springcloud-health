package contracts

import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

Contract.make {
    description "return all users"

    request {
        url "/users/userlist"
        method GET()
    }

    response {
        status 200
        headers {
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
        }
        body("data": [
                [id: 1L, userCode: "user1", userName: "springhealth_user1"],
                [id: 2L, userCode: "user2", userName: "springhealth_user2"],
                [id: 3L, userCode: "user3", userName: "springhealth_user3"]])
    }
}

// mvn install:install-file -Dfile=target/user-service-0.0.1-SNAPSHOT-stubs.jar -DgroupId=com.springhealth.user -DartifactId=user-service -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -Dclassifier=stubs