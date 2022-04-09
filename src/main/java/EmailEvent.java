import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class EmailEvent implements RequestHandler<SNSEvent, Object> {

    @Override
    public Object handleRequest(SNSEvent snsEvent, Context context) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        context.getLogger().log("Invocation started: " + timeStamp);
        String org = snsEvent.getRecords().get(0).getSNS().getMessage();
        String[] arr = org.split(":");
        String email = arr[0];
        String token = arr[1];
        String finalToken = "http://prod.kaifengruan.me/v1/verifyUserEmail?email=" + email + "&token=" + token;
        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder
                        .standard()
                        .withCredentials(new InstanceProfileCredentialsProvider(false))
                        .build();
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(finalToken)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData("Verificaiton")))
                .withSource("verify@prod.kaifengruan.me");
        client.sendEmail(request);
        context.getLogger().log(finalToken + " email sent");
        timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        context.getLogger().log("Invocation completed: " + timeStamp);
        return null;
    }
}
