import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

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
        context.getLogger().log(finalToken);
        timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
        context.getLogger().log("Invocation completed: " + timeStamp);
        return null;
    }
}
