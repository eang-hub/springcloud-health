import com.springhealth.intervention.InterventionApplication;
import com.springhealth.intervention.domain.UserList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InterventionApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureStubRunner(
        ids = { "com.springhealth.user:user-service:+:stubs:8080" },
        stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
public class InterventionAppTests {


    @Autowired(required = false) // 这个可以解决idea报错
    @Qualifier("getRestTemplate") // 表示根据名称来找bean
    private RestTemplate restTemplate;

    // ids 的格式为 groupId:artifactId:version:classifier:port
    // mvn install:install-file -Dfile=target/user-service-0.0.1-SNAPSHOT-stubs.jar -DgroupId=com.springhealth.user -DartifactId=user-service -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -Dclassifier=stubs
    @Test
    public void testGetUsers() {
        ParameterizedTypeReference<UserList> ptf = new ParameterizedTypeReference<UserList>() {};

        restTemplate.exchange("http://localhost:8080/users/userlist",HttpMethod.GET, null, ptf);

      //  Assert.assertEquals(3, responseEntity.getBody().getData().size());
    }
}