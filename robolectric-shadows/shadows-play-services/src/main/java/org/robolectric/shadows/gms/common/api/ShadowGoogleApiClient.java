package org.robolectric.shadows.gms.common.api;

import android.annotation.NonNull;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadows.gms.Shadows;
import org.robolectric.util.ReflectionHelpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.common.api.GoogleApiClient.SIGN_IN_MODE_OPTIONAL;
import static com.google.android.gms.common.api.GoogleApiClient.SIGN_IN_MODE_REQUIRED;
import static org.robolectric.internal.Shadow.directlyOn;
import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import static org.robolectric.internal.Shadow.invokeConstructor;
import static org.robolectric.util.ReflectionHelpers.ClassParameter;

/**
 * Shadow for {@link GoogleApiClient}.
 */
@Implements(GoogleApiClient.class)
public class ShadowGoogleApiClient {
    @RealObject
    protected GoogleApiClient realGoogleApiClient;
    private Looper looper;
    private Context context;
    private List<String> scopeUris;
    private PendingResult<Status> reconnectPendingResult;
    private Set<GoogleApiClient.ConnectionCallbacks> connCallbacks;
    private Set<GoogleApiClient.OnConnectionFailedListener> failedListeners;
    private Map<Api<?>, Api.ApiOptions> apiToApiOptionsMap;
    private int clientId;
    private Integer currentSignInMode;
    private boolean isConnected = true;
    private boolean isConnecting = true;
    private HashMap<Api<?>, ConnectionResult> apiToConnectionResultMap = new HashMap<>();
    private HashMap<Api<?>, Boolean> apiToConnectedMap = new HashMap<>();

    public void __constructor__(){
        connCallbacks = new HashSet<>();
        failedListeners = new HashSet<>();
        apiToApiOptionsMap = new HashMap<>();
    }

    @Implementation
    public Looper getLooper() {
        return looper;
    }

    @Implementation
    public Context getContext(){
        return context;
    }

    @Implementation
    public void connect() {
        final GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        final int statusCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        switch (statusCode){
            case ConnectionResult.SUCCESS:
                for (ConnectionCallbacks callback : connCallbacks) {
                    if (callback != null) {
                        callback.onConnected(null);
                    }
                }
                break;
            case ConnectionResult.NETWORK_ERROR:
                for (ConnectionCallbacks callback : connCallbacks) {
                    if (callback != null) {
                        callback.onConnectionSuspended(ConnectionCallbacks.CAUSE_NETWORK_LOST);
                    }
                }
                break;
            case ConnectionResult.INTERRUPTED:
                for (ConnectionCallbacks callback : connCallbacks) {
                    if (callback != null) {
                        callback.onConnectionSuspended(ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED);
                    }
                }
                break;
            default: //All other cases
                final ConnectionResult errorCause = new ConnectionResult(statusCode);
                for(OnConnectionFailedListener listener : failedListeners){
                    if(listener != null){
                        listener.onConnectionFailed(errorCause);
                    }
                }
                break;
        }
    }

    @Implementation
    public void connect(int signInMode){
        if(currentSignInMode == null){
            if(!(signInMode == 3 || signInMode == SIGN_IN_MODE_OPTIONAL ||
                signInMode == SIGN_IN_MODE_REQUIRED)){
                throw new IllegalArgumentException("Illegal sign-in mode: " + signInMode);
            }
            currentSignInMode = signInMode;
        } else if(currentSignInMode != signInMode){
            throw new IllegalStateException("Cannot use sign-in mode: " +
                getSignInModeLabel(signInMode) + ". Mode was "
                + "already set to " + getSignInModeLabel(currentSignInMode));
        }
        connect();
    }

    @Implementation
    public ConnectionResult getConnectionResult(@NonNull Api<?> api){
        return apiToConnectionResultMap.get(api);
    }

    @Implementation
    public boolean hasConnectedApi(@NonNull Api<?> api) {
        final Boolean apiConnected = apiToConnectedMap.get(api);
        return apiConnected != null && apiConnected;
    }

    @Implementation
    public ConnectionResult blockingConnect() {
        final GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        final int statusCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return new ConnectionResult(statusCode);
    }

    @Implementation
    public ConnectionResult blockingConnect(long l, TimeUnit timeUnit) {
        final GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        final int statusCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return new ConnectionResult(statusCode);
    }

    @Implementation
    public void disconnect() {
        System.out.println("\nFakeGoogleApiClient.disconnect() called.");
        for (ConnectionCallbacks callback : connCallbacks) {
            if (callback != null) {
                callback.onConnectionSuspended(ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED);
            }
        }
    }

    @Implementation
    public void reconnect() {
        System.out.println("\nFakeGoogleApiClient.reconnect() called.");
        connect();
    }

    @Implementation
    public PendingResult<Status> clearDefaultAccountAndReconnect() {
        disconnect();
        reconnect();
        return reconnectPendingResult;
    }

    @Implementation
    public void stopAutoManage(FragmentActivity fragmentActivity) {
        System.out.println("\nFakeGoogleApiClient.stopAutoManage() called.");
        realGoogleApiClient.stopAutoManage(fragmentActivity);
    }

    @Implementation
    public boolean isConnected(){
        return isConnected;
    }

    @Implementation
    public boolean isConnecting() {
        return isConnecting;
    }

    @Implementation
    public void registerConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
        this.connCallbacks.add(connectionCallbacks);
    }

    @Implementation
    public boolean isConnectionCallbacksRegistered(ConnectionCallbacks connectionCallbacks) {
        return this.connCallbacks.contains(connectionCallbacks);
    }

    @Implementation
    public void unregisterConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
        this.connCallbacks.remove(connectionCallbacks);
    }

    @Implementation
    public void registerConnectionFailedListener(OnConnectionFailedListener
                                                             onConnectionFailedListener) {
        this.failedListeners.add(onConnectionFailedListener);
    }

    @Implementation
    public boolean isConnectionFailedListenerRegistered(OnConnectionFailedListener
                                                                    onConnectionFailedListener) {
        return this.failedListeners.contains(onConnectionFailedListener);
    }

    @Implementation
    public void unregisterConnectionFailedListener(OnConnectionFailedListener
                                                               onConnectionFailedListener) {
        this.failedListeners.remove(onConnectionFailedListener);
    }

    public void setConnectionResultForApi(final Api<?> api, final ConnectionResult result){
        apiToConnectionResultMap.put(api, result);
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public void setIsConnecting(boolean isConnecting) {
        this.isConnecting = isConnecting;
    }

    public void setLooper(Looper looper) {
        this.looper = looper;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setHasConnectedApi(final Api<?> api, final Boolean isConnected){
        apiToConnectedMap.put(api, isConnected);
    }

   public Integer getCurrentSignInMode() {
       return currentSignInMode;
   }

   public void setReconnectPendingResult(final PendingResult<Status>
       pendingResult) {
       this.reconnectPendingResult = pendingResult;
   }

    private String getSignInModeLabel(int signInMode){
        switch (signInMode){
            case 1:
                return "SIGN_IN_MODE_REQUIRED";
            case 2:
                return "SIGN_IN_MODE_OPTIONAL";
            case 3:
                return "SIGN_IN_MODE_NONE";
            default:
                return "UNKNOWN";
        }
    }

    private void setScopeUris(List<String> scopeUris) {
        this.scopeUris = scopeUris;
    }

    private void setConnCallbacks(Set<GoogleApiClient.ConnectionCallbacks> connCallbacks) {
        this.connCallbacks = connCallbacks;
    }

    private void setFailedListeners(Set<GoogleApiClient.OnConnectionFailedListener> failedListeners) {
        this.failedListeners = failedListeners;
    }

    private void setApiToApiOptionsMap(Map<Api<?>, Api.ApiOptions> apiToApiOptionsMap) {
        this.apiToApiOptionsMap = apiToApiOptionsMap;
    }

    private void setClientId(int clientId) {
        this.clientId = clientId;
    }

    /**
     * Shadow for {@link GoogleApiClient.Builder}.
     */
    @Implements(GoogleApiClient.Builder.class)
    public static class ShadowBuilder {
        @RealObject
        private GoogleApiClient.Builder realBuilder;
        private Map<Api<?>, Api.ApiOptions> apiToOptionsMap;
        private View viewForPopups;
        private int gravityForPopups;
        private FragmentActivity fragmentActivity;
        private Looper looper;
        private Context context;
        private Set<String> scopeUris;
        private Set<GoogleApiClient.ConnectionCallbacks> connCallbacks;
        private Set<GoogleApiClient.OnConnectionFailedListener> failedListeners;
        private GoogleApiClient.OnConnectionFailedListener unresolvedConnectionFailedListener;
        private int clientId = -1;
        private String appId;

        private String accountName;

        public void __constructor__(Context context){
            this.scopeUris = new HashSet<String>();
            this.apiToOptionsMap = new HashMap<Api<?>, Api.ApiOptions>();
            this.clientId = -1;
            this.connCallbacks = new HashSet<GoogleApiClient.ConnectionCallbacks>();
            this.failedListeners = new HashSet<GoogleApiClient.OnConnectionFailedListener>();
            this.context = context;
            this.looper = context.getMainLooper();
            this.appId = context.getPackageName();
            invokeConstructor(GoogleApiClient.Builder.class, realBuilder,
                    ClassParameter.from(Context.class, context));
        }

        public void __constructor__(Context context,
                                    GoogleApiClient.ConnectionCallbacks connectedListener,
                                    GoogleApiClient.OnConnectionFailedListener connectionFailedListener){
            this.scopeUris = new HashSet<String>();
            this.apiToOptionsMap = new HashMap<Api<?>, Api.ApiOptions>();
            this.clientId = -1;
            this.connCallbacks = new HashSet<GoogleApiClient.ConnectionCallbacks>();
            this.failedListeners = new HashSet<GoogleApiClient.OnConnectionFailedListener>();
            this.context = context;
            this.looper = context.getMainLooper();
            this.appId = context.getPackageName();
            this.connCallbacks.add(connectedListener);
            this.failedListeners.add(connectionFailedListener);
            invokeConstructor(GoogleApiClient.Builder.class, realBuilder,
                    ClassParameter.from(Context.class, context),
                    ClassParameter.from(GoogleApiClient.ConnectionCallbacks.class, connectedListener),
                    ClassParameter.from(GoogleApiClient.OnConnectionFailedListener.class, connectionFailedListener));
        }

        public Map<Api<?>, Api.ApiOptions> getApiMap() {
            return apiToOptionsMap;
        }

        public FragmentActivity getFragmentActivity() {
            return fragmentActivity;
        }

        public Looper getLooper() {
            return looper;
        }

        public Context getContext() {
            return context;
        }

        public Set<String> getScopeUris() {
            return scopeUris;
        }

        public Set<GoogleApiClient.ConnectionCallbacks> getConnCallbacks() {
            return connCallbacks;
        }

        public Set<GoogleApiClient.OnConnectionFailedListener> getFailedListeners() {
            return failedListeners;
        }

        public int getClientId() {
            return clientId;
        }

        public String getAppId() {
            return appId;
        }

        public int getGravityForPopups() {
            return gravityForPopups;
        }

        public View getViewForPopups() {
            return viewForPopups;
        }

        public String getAccountName() {
            return accountName;
        }

        public GoogleApiClient.OnConnectionFailedListener getUnresolvedConnectionFailedListener() {
            return unresolvedConnectionFailedListener;
        }

        @Implementation
        public GoogleApiClient.Builder addApi(final Api<? extends Api.ApiOptions.NotRequiredOptions> api){
            this.apiToOptionsMap.put(api, null);
            addScopeUrisFromApi(api.zzoP().zzo(null));
            directlyOn(realBuilder, GoogleApiClient.Builder.class, "addApi",
                    ReflectionHelpers.ClassParameter.from(Api.class, api));
            return realBuilder;
        }

        @Implementation
        public GoogleApiClient.Builder addConnectionCallbacks(GoogleApiClient.ConnectionCallbacks
                                                                              connectionCallbacks){
            this.connCallbacks.add(connectionCallbacks);
            directlyOn(realBuilder, GoogleApiClient.Builder.class, "addConnectionCallbacks",
                    ReflectionHelpers.ClassParameter.from(GoogleApiClient.ConnectionCallbacks.class,
                            connectionCallbacks));
            return realBuilder;
        }

        @Implementation
        public GoogleApiClient.Builder addOnConnectionFailedListener(
                GoogleApiClient.OnConnectionFailedListener failedListener){
            this.failedListeners.add(failedListener);
            directlyOn(realBuilder, GoogleApiClient.Builder.class, "addOnConnectionFailedListener",
                    ReflectionHelpers.ClassParameter.from(GoogleApiClient.
                            OnConnectionFailedListener.class, failedListener));
            return realBuilder;
        }

        @Implementation
        public GoogleApiClient.Builder setHandler(Handler handler) {
            this.looper = handler.getLooper();
            directlyOn(realBuilder, GoogleApiClient.Builder.class, "setHandler",
                    ReflectionHelpers.ClassParameter.from(Handler.class, handler));
            return realBuilder;
        }

        @Implementation
        public GoogleApiClient.Builder setViewForPopups(View viewForPopups) {
            this.viewForPopups = viewForPopups;
            directlyOn(realBuilder, GoogleApiClient.Builder.class, "setViewForPopups",
                    ReflectionHelpers.ClassParameter.from(View.class, viewForPopups));
            return realBuilder;
        }

        @Implementation
        public GoogleApiClient.Builder addScope(Scope scope) {
            this.scopeUris.add(scope.zzpb());
            directlyOn(realBuilder, GoogleApiClient.Builder.class, "addScope",
                    ReflectionHelpers.ClassParameter.from(Scope.class, scope));
            return realBuilder;
        }

        @Implementation
        public <O extends Api.ApiOptions.HasOptions> GoogleApiClient.Builder addApi(Api<O> api,
                                                                                    O options) {
            this.apiToOptionsMap.put(api, options);
            addScopeUrisFromApi(api.zzoP().zzo(options));
            directlyOn(realBuilder, GoogleApiClient.Builder.class, "addApi",
                    ReflectionHelpers.ClassParameter.from(Api.class, api),
                    ReflectionHelpers.ClassParameter.from(Api.ApiOptions.HasOptions.class,
                            options));
            return realBuilder;
        }

        @Implementation
        public GoogleApiClient.Builder setAccountName(String accountName) {
            this.accountName = accountName;
            directlyOn(realBuilder, GoogleApiClient.Builder.class, "setAccountName",
                    ReflectionHelpers.ClassParameter.from(String.class, accountName));
            return realBuilder;
        }

        @Implementation
        public GoogleApiClient.Builder useDefaultAccount() {
            directlyOn(realBuilder, GoogleApiClient.Builder.class, "useDefaultAccount");
            return realBuilder;
        }

        @Implementation
        public GoogleApiClient.Builder setGravityForPopups(int gravityForPopups) {
            this.gravityForPopups = gravityForPopups;
            directlyOn(realBuilder, GoogleApiClient.Builder.class, "setGravityForPopups",
                    ReflectionHelpers.ClassParameter.from(int.class, gravityForPopups));
            return realBuilder;
        }

        @Implementation
        public GoogleApiClient.Builder enableAutoManage(
                FragmentActivity fragmentActivity, int clientId,
                GoogleApiClient.OnConnectionFailedListener unresolvedConnectionFailedListener) {
            this.clientId = clientId;
            this.fragmentActivity = fragmentActivity;
            this.unresolvedConnectionFailedListener = unresolvedConnectionFailedListener;
            directlyOn(realBuilder, GoogleApiClient.Builder.class, "enableAutoManage",
                    ReflectionHelpers.ClassParameter.from(FragmentActivity.class, fragmentActivity),
                    ReflectionHelpers.ClassParameter.from(int.class, clientId),
                    ReflectionHelpers.ClassParameter.from(
                            GoogleApiClient.OnConnectionFailedListener.class,
                            unresolvedConnectionFailedListener));
            return realBuilder;
        }

        @Implementation
        public GoogleApiClient build(){
            final GoogleApiClient result = directlyOn(realBuilder,
                    GoogleApiClient.Builder.class,
                    "build");
            populateShadow(result);
            return result;
        }

        private void populateShadow(final GoogleApiClient googleApiClient){
            ShadowGoogleApiClient s = Shadows.shadowOf(googleApiClient);
            s.setContext(context);
            s.setLooper(looper);
            s.setClientId(clientId);
            final ArrayList<String> scopeUriList = new ArrayList<String>(scopeUris);
            s.setScopeUris(Collections.unmodifiableList(scopeUriList));
            s.setApiToApiOptionsMap(apiToOptionsMap);
            s.setFailedListeners(failedListeners);
            s.setConnCallbacks(connCallbacks);
        }

        private void addScopeUrisFromApi(final List<Scope> apiScopes){
            if(apiScopes == null || apiScopes.isEmpty()){
                return;
            }
            int index = 0;
            for(int i = apiScopes.size(); index < i; ++index) {
                this.scopeUris.add(apiScopes.get(index).zzpb());
            }
        }
    }
}
