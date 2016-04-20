package home.getpark;

import android.content.Context;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

import java.io.IOException;

/**
 * Created by home on 11/04/2016.
 */
public class GcmIDListenerService extends InstanceIDListenerService {
    Context context;
     @Override
     public void onTokenRefresh() {
         InstanceID instanceID = InstanceID.getInstance(context);
         try {
             String token = instanceID.getToken(context.getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
         } catch (IOException e) {
             e.printStackTrace();
         }
         //SEND TOKEN TO APP SERVER
        }
}
