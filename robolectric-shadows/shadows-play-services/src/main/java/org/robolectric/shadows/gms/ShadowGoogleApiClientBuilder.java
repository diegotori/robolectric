package org.robolectric.shadows.gms;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadows.gms.fakes.FakeGoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by diegotori on 10/4/15.
 */
@Implements(GoogleApiClient.Builder.class)
public class ShadowGoogleApiClientBuilder {
    @RealObject
    private GoogleApiClient.Builder realBuilder;
    FakeGoogleApiClientFactory listener;
    private Map<Api<?>, Api.ApiOptions> apiToOptionsMap;
    private View viewForPopups;
    private int gravityForPopups;
    private FragmentActivity fragmentActivity;
    public Looper looper;
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
    }

    public void __constructor__(Context context,
                                GoogleApiClient.ConnectionCallbacks connectedListener,
                                GoogleApiClient.OnConnectionFailedListener connectionFailedListener){
        this.__constructor__(context);
        this.connCallbacks.add(connectedListener);
        this.failedListeners.add(connectionFailedListener);
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

    public void setFakeGoogleApiClientFactory(final FakeGoogleApiClientFactory listener){
        this.listener = listener;
    }

    @Implementation
    public GoogleApiClient.Builder addApi(final Api<?> api){
        //directlyOn(realBuilder, GoogleApiClient.Builder.class, "addApi", ReflectionHelpers.ClassParameter.from(Api.class, api));
        this.apiToOptionsMap.put(api, null);
        addScopeUrisFromApi(api.gy());
        return realBuilder;
    }

    @Implementation
    public GoogleApiClient.Builder addConnectionCallbacks(GoogleApiClient.ConnectionCallbacks connectionCallbacks){
        //directlyOn(realBuilder, GoogleApiClient.Builder.class, "addConnectionCallbacks", ReflectionHelpers.ClassParameter.from(GoogleApiClient.ConnectionCallbacks.class, connectionCallbacks));
        this.connCallbacks.add(connectionCallbacks);
        return realBuilder;
    }

    @Implementation
    public GoogleApiClient.Builder addOnConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener failedListener){
        //directlyOn(realBuilder, GoogleApiClient.Builder.class, "addOnConnectionFailedListener", ReflectionHelpers.ClassParameter.from(GoogleApiClient.OnConnectionFailedListener.class, failedListener));
        this.failedListeners.add(failedListener);
        return realBuilder;
    }

    @Implementation
    public GoogleApiClient.Builder setHandler(Handler handler) {
        this.looper = handler.getLooper();
        return realBuilder;
    }

    @Implementation
    public GoogleApiClient.Builder setViewForPopups(View viewForPopups) {
        this.viewForPopups = viewForPopups;
        return realBuilder;
    }

    @Implementation
    public GoogleApiClient.Builder addScope(Scope scope) {
        this.scopeUris.add(scope.gO());
        return realBuilder;
    }

    @Implementation
    public <O extends Api.ApiOptions.HasOptions> GoogleApiClient.Builder addApi(Api<O> api, O options) {
        this.apiToOptionsMap.put(api, options);
        addScopeUrisFromApi(api.gy());
        return realBuilder;
    }

    @Implementation
    public GoogleApiClient.Builder setAccountName(String accountName) {
        this.accountName = accountName;
        return realBuilder;
    }

    @Implementation
    public GoogleApiClient.Builder useDefaultAccount() {
        return setAccountName("<<default account>>");
    }

    @Implementation
    public GoogleApiClient.Builder setGravityForPopups(int gravityForPopups) {
        this.gravityForPopups = gravityForPopups;
        return realBuilder;
    }

    @Implementation
    public GoogleApiClient.Builder enableAutoManage(FragmentActivity fragmentActivity, int clientId, GoogleApiClient.OnConnectionFailedListener unresolvedConnectionFailedListener) {
        this.clientId = clientId;
        this.fragmentActivity = fragmentActivity;
        this.unresolvedConnectionFailedListener = unresolvedConnectionFailedListener;
        return realBuilder;
    }

    @Implementation
    public GoogleApiClient build(){
        if(listener != null){
            return listener.onBuild(this);
        } else {
            return new FakeGoogleApiClient(context,
                    looper,
                    new ArrayList<String>(scopeUris),
                    apiToOptionsMap,
                    connCallbacks,
                    failedListeners,
                    clientId);
        }
    }

    private void addScopeUrisFromApi(final List<Scope> apiScopes){
        if(apiScopes == null || apiScopes.isEmpty()){
            return;
        }
        int index = 0;
        for(int i = apiScopes.size(); index < i; ++index) {
            this.scopeUris.add(apiScopes.get(index).gO());
        }
    }

    public interface FakeGoogleApiClientFactory {
        GoogleApiClient onBuild(ShadowGoogleApiClientBuilder shadowBuilder);
    }

}
