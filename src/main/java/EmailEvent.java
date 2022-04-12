import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;


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
        String SUBJECT = "Email Verification for opening account";
        String HTMLBODY = "<h1>Email Verification</h1>"
                + "<h2>Hi, please click the link below to verify your account!</h2>"
                + "<p><a href="+ finalToken +">"+ finalToken +"</a></p>";

        String TEXTBODY = "This email was sent to verfiy your account with the link: "
                + finalToken;
        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.US_EAST_1).build();
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(email))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(HTMLBODY)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(SUBJECT)))
                .withSource("verify@prod.kaifengruan.me");
        client.sendEmail(request);
        context.getLogger().log(finalToken + " email sent");
        timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        context.getLogger().log("Invocation completed: " + timeStamp);
        return null;
    }


}
