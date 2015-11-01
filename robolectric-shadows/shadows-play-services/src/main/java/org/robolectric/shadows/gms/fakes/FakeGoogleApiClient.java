package org.robolectric.shadows.gms.fakes;

import android.content.Context;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.BaseImplementation;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.d;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by diegotori on 10/4/15.
 */
public class FakeGoogleApiClient implements GoogleApiClient {
    public Looper looper;
    private final Context context;
    private List<String> scopeUris;
    private Set<ConnectionCallbacks> connCallbacks;
    private Set<OnConnectionFailedListener> failedListeners;
    private Map<Api<?>, Api.ApiOptions> apiApiOptionsMap;
    private int clientId;
    private boolean isConnected = true;
    private boolean isConnecting = true;

    public FakeGoogleApiClient(Context context,
                               Looper looper,
                               List<String> scopeUris,
                               Map<Api<?>, Api.ApiOptions> apiToOptionsMap,
                               Set<ConnectionCallbacks> connCallbacks,
                               Set<OnConnectionFailedListener> failedListeners,
                               int clientId) {
        this.context = context;
        this.looper = looper;
        this.scopeUris = Collections.unmodifiableList(scopeUris);
        this.apiApiOptionsMap = apiToOptionsMap;
        this.connCallbacks = connCallbacks;
        this.failedListeners = failedListeners;
        this.clientId = clientId;
    }

    @Override
    public <A extends Api.a, R extends Result, T extends BaseImplementation.a<R, A>> T a(T t) {
        return null;
    }

    @Override
    public <A extends Api.a, T extends BaseImplementation.a<? extends Result, A>> T b(T t) {
        return null;
    }

    @Override
    public <L> d<L> d(L l) {
        return null;
    }

    @Override
    public <C extends Api.a> C a(Api.c<C> c) {
        return null;
    }

    @Override
    public boolean a(Scope scope) {
        return scopeUris.contains(scope.gO());
    }

    @Override
    public Looper getLooper() {
        return looper;
    }

    @Override
    public void connect() {
        final int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
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
            case ConnectionResult.API_UNAVAILABLE:
                for(OnConnectionFailedListener listener : failedListeners){
                    if(listener != null){
                        listener.onConnectionFailed(new ConnectionResult(ConnectionResult.API_UNAVAILABLE, null));
                    }
                }
                break;
        }
    }

    @Override
    public ConnectionResult blockingConnect() {
        return new ConnectionResult(ConnectionResult.SUCCESS, null);
    }

    @Override
    public ConnectionResult blockingConnect(long l, TimeUnit timeUnit) {
        return new ConnectionResult(ConnectionResult.SUCCESS, null);
    }

    @Override
    public void disconnect() {
        System.out.println("\nFakeGoogleApiClient.reconnect() called.");
        for (ConnectionCallbacks callback : connCallbacks) {
            if (callback != null) {
                callback.onConnectionSuspended(ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED);
            }
        }
    }

    @Override
    public void reconnect() {
        System.out.println("\nFakeGoogleApiClient.reconnect() called.");
        connect();
    }

    @Override
    public PendingResult<Status> clearDefaultAccountAndReconnect() {
        disconnect();
        reconnect();
        return null;
    }

    @Override
    public void stopAutoManage(FragmentActivity fragmentActivity) {
        System.out.println("\nFakeGoogleApiClient.stopAutoManage() called.");
    }

    public void setIsConnected(final boolean isConnected){
        this.isConnected = isConnected;
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    public void setIsConnecting(final boolean isConnecting){
        this.isConnecting = isConnecting;
    }

    @Override
    public boolean isConnecting() {
        return isConnecting;
    }

    @Override
    public void registerConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
        this.connCallbacks.add(connectionCallbacks);
    }

    @Override
    public boolean isConnectionCallbacksRegistered(ConnectionCallbacks connectionCallbacks) {
        return this.connCallbacks.contains(connectionCallbacks);
    }

    @Override
    public void unregisterConnectionCallbacks(ConnectionCallbacks connectionCallbacks) {
        this.connCallbacks.remove(connectionCallbacks);
    }

    @Override
    public void registerConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        this.failedListeners.add(onConnectionFailedListener);
    }

    @Override
    public boolean isConnectionFailedListenerRegistered(OnConnectionFailedListener onConnectionFailedListener) {
        return this.failedListeners.contains(onConnectionFailedListener);
    }

    @Override
    public void unregisterConnectionFailedListener(OnConnectionFailedListener onConnectionFailedListener) {
        this.failedListeners.remove(onConnectionFailedListener);
    }
}
