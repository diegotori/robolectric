package org.robolectric.shadows.gms.common.api;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzf;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.gms.Shadows;
import org.robolectric.shadows.gms.common.ShadowGoogleApiAvailability;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by diegotori on 5/1/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, shadows = {ShadowGoogleApiClient.ShadowBuilder.class,
        ShadowGoogleApiClient.class, ShadowGoogleApiAvailability.class})
public class ShadowGoogleApiClientTest {
    private static final String MOCK_APP_ID = "com.foo.app.id";

    private static final String MOCK_API_NAME = "some_api_name";

    @Mock
    private Context mockContext;

    @Mock
    private Looper mockLooper;

    @Mock
    private GoogleApiClient.ConnectionCallbacks mockConnCallbacks;

    @Mock
    private GoogleApiClient.OnConnectionFailedListener mockConnFailedListener;

    @Mock
    private Api.ApiOptions.HasOptions mockApiOptions;

    @Mock
    private Api.zza<Api.zzb, Api.ApiOptions.NotRequiredOptions> mockNonOptsClientBuilder;

    @Mock
    private Api.zza<Api.zzb, Api.ApiOptions.HasOptions> mockClientBuilder;

    @Mock
    private Api.zzb mockZzb;

    private Api.zzc<Api.zzb> mockClientKey;

    private Api<Api.ApiOptions.NotRequiredOptions> mockApi;

    private Scope mockApiScope;

    private ShadowGoogleApiClient shadowGoogleApiClient;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        mockClientKey = new Api.zzc<>();
        when(mockContext.getMainLooper()).thenReturn(mockLooper);
        when(mockContext.getPackageName()).thenReturn(MOCK_APP_ID);
        final String mockScopeUri = "some Scope URI";
        mockApiScope = new Scope(mockScopeUri);
        final List<Scope> mockApiScopes = new ArrayList<>();
        mockApiScopes.add(mockApiScope);
        mockApi = new Api<>(MOCK_API_NAME,
                mockNonOptsClientBuilder,
                mockClientKey);
        when(mockNonOptsClientBuilder.zzo(any(Api.ApiOptions.NotRequiredOptions.class)))
                .thenReturn(mockApiScopes);
        when(mockNonOptsClientBuilder.getPriority()).thenReturn(1);
        when(mockNonOptsClientBuilder.zza(eq(mockContext), eq(mockLooper), any(zzf.class),
                any(Api.ApiOptions.NotRequiredOptions.class),
                any(GoogleApiClient.ConnectionCallbacks.class),
                any(GoogleApiClient.OnConnectionFailedListener.class)))
                .thenReturn(mockZzb);
        shadowGoogleApiClient = Shadows.shadowOf(new GoogleApiClient.Builder(mockContext)
                .addConnectionCallbacks(mockConnCallbacks)
                .addOnConnectionFailedListener(mockConnFailedListener)
                .addApi(mockApi)
                .build());
    }

    @After
    public void tearDown(){
        mockApiScope = null;
        mockClientKey = null;
        shadowGoogleApiClient = null;
    }

    @Test
    public void setIsConnected(){
        shadowGoogleApiClient.setIsConnected(true);

        final boolean actual = shadowGoogleApiClient.isConnected();

        assertThat(actual).isTrue();
    }

    @Test
    public void setIsConnecting(){
        shadowGoogleApiClient.setIsConnecting(true);

        final boolean actual = shadowGoogleApiClient.isConnecting();

        assertThat(actual).isTrue();
    }

    @Test
    public void setConnectionResultForApi() {
        final ConnectionResult expected = new ConnectionResult(ConnectionResult.TIMEOUT);
        shadowGoogleApiClient.setConnectionResultForApi(mockApi, expected);

        final ConnectionResult actual = shadowGoogleApiClient.getConnectionResult(mockApi);

        assertThat(actual)
                .isNotNull().isEqualTo(expected);
    }

    @Test
    public void setHasConnectedApi() {
        shadowGoogleApiClient.setHasConnectedApi(mockApi, true);

        final boolean actual = shadowGoogleApiClient.hasConnectedApi(mockApi);

        assertThat(actual)
            .isTrue();
    }

    @Test
    public void setHasConnectedApi__NullBoolean() {
        shadowGoogleApiClient.setHasConnectedApi(mockApi, null);

        final boolean actual = shadowGoogleApiClient.hasConnectedApi(mockApi);

        assertThat(actual)
            .isFalse();
    }

    @Test
    public void setHasConnectedApi__False() {
        shadowGoogleApiClient.setHasConnectedApi(mockApi, false);

        final boolean actual = shadowGoogleApiClient.hasConnectedApi(mockApi);

        assertThat(actual)
            .isFalse();
    }

    @Test
    public void setLooper() {
        shadowGoogleApiClient.setLooper(mockLooper);

        final Looper actual = shadowGoogleApiClient.getLooper();

        assertThat(actual)
                .isNotNull()
                .isEqualTo(mockLooper);
    }

    @Test
    public void setContext() {
        shadowGoogleApiClient.setContext(mockContext);

        final Context actual = shadowGoogleApiClient.getContext();

        assertThat(actual)
            .isNotNull()
            .isEqualTo(mockContext);
    }

    @Test
    public void registerConnectionCallbacks() {
        shadowGoogleApiClient.registerConnectionCallbacks(mockConnCallbacks);

        final boolean actual = shadowGoogleApiClient.isConnectionCallbacksRegistered
            (mockConnCallbacks);

        assertThat(actual)
            .isTrue();
    }

    @Test
    public void unregisterConnectionCallbacks() {
        shadowGoogleApiClient.registerConnectionCallbacks(mockConnCallbacks);
        shadowGoogleApiClient.unregisterConnectionCallbacks(mockConnCallbacks);

        final boolean actual = shadowGoogleApiClient.isConnectionCallbacksRegistered
            (mockConnCallbacks);

        assertThat(actual)
            .isFalse();

    }

    @Test
    public void registerConnectionFailedListener() {
        shadowGoogleApiClient.registerConnectionFailedListener(mockConnFailedListener);

        final boolean actual = shadowGoogleApiClient.isConnectionFailedListenerRegistered
            (mockConnFailedListener);

        assertThat(actual)
            .isTrue();
    }

    @Test
    public void unregisterConnectionFailedListener() {
        shadowGoogleApiClient.registerConnectionFailedListener(mockConnFailedListener);
        shadowGoogleApiClient.unregisterConnectionFailedListener(mockConnFailedListener);

        final boolean actual = shadowGoogleApiClient.isConnectionFailedListenerRegistered
            (mockConnFailedListener);

        assertThat(actual)
            .isFalse();
    }

    @Test
    public void connect__ConnectionResult__Success() {
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
            = Shadows.shadowOf(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.SUCCESS);
        shadowGoogleApiClient.registerConnectionCallbacks(mockConnCallbacks);

        shadowGoogleApiClient.connect();

        verify(mockConnCallbacks).onConnected(null);
    }

    @Test
    public void connect__ConnectionResult__Success__NullConnectionCallbacks() {
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
            = Shadows.shadowOf(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.SUCCESS);
        shadowGoogleApiClient.registerConnectionCallbacks(null);

        try {
            shadowGoogleApiClient.connect();
        } catch (Exception e) {
            Assertions.fail("Caught while trying to call back to a null ConnectionCallbacks "
                + "instance: " + e.getMessage(), e);
        }
    }

    @Test
    public void connect__ConnectionResult__NetworkError() {
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
            = Shadows.shadowOf(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.NETWORK_ERROR);
        shadowGoogleApiClient.registerConnectionCallbacks(mockConnCallbacks);

        shadowGoogleApiClient.connect();

        verify(mockConnCallbacks)
            .onConnectionSuspended(GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST);
    }

    @Test
    public void connect__ConnectionResult__NetworkError__NullConnectionCallbacks() {
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
            = Shadows.shadowOf(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.NETWORK_ERROR);
        shadowGoogleApiClient.registerConnectionCallbacks(null);

        try {
            shadowGoogleApiClient.connect();
        } catch (Exception e) {
            Assertions.fail("Caught while trying to call back to a null ConnectionCallbacks "
                + "instance: " + e.getMessage(), e);
        }
    }

    @Test
    public void connect__ConnectionResult__Interrupted() {
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
            = Shadows.shadowOf(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.INTERRUPTED);
        shadowGoogleApiClient.registerConnectionCallbacks(mockConnCallbacks);

        shadowGoogleApiClient.connect();

        verify(mockConnCallbacks)
            .onConnectionSuspended(GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED);
    }

    @Test
    public void connect__ConnectionResult__Interrupted__NullConnectionCallbacks() {
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
            = Shadows.shadowOf(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.INTERRUPTED);
        shadowGoogleApiClient.registerConnectionCallbacks(null);

        try {
            shadowGoogleApiClient.connect();
        } catch (Exception e) {
            Assertions.fail("Caught while trying to call back to a null ConnectionCallbacks "
                + "instance: " + e.getMessage(), e);
        }
    }

    @Test
    public void connect__ConnectionResult__AllOtherErrorCodes() {
        final ArgumentCaptor<ConnectionResult> connResultCaptor = ArgumentCaptor.forClass
            (ConnectionResult.class);
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
            = Shadows.shadowOf(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.API_UNAVAILABLE);
        shadowGoogleApiClient.registerConnectionFailedListener(mockConnFailedListener);

        shadowGoogleApiClient.connect();

        verify(mockConnFailedListener)
            .onConnectionFailed(connResultCaptor.capture());
        final ConnectionResult capturedConnResult = connResultCaptor.getValue();
        assertThat(capturedConnResult)
            .isNotNull();
        assertThat(capturedConnResult.getErrorCode())
            .isNotEqualTo(ConnectionResult.SUCCESS)
            .isEqualTo(ConnectionResult.API_UNAVAILABLE);
    }

    @Test
    public void connect__ConnectionResult__AllOtherErrorCodes__NullConnectionCallbacks() {
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
            = Shadows.shadowOf(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.API_UNAVAILABLE);
        shadowGoogleApiClient.registerConnectionFailedListener(null);

        try {
            shadowGoogleApiClient.connect();
        } catch (Exception e) {
            Assertions.fail("Caught while trying to call back to a null ConnectionCallbacks "
                + "instance: " + e.getMessage(), e);
        }
    }

    @Test
    public void connect__SignInModeRequired() {
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
            = Shadows.shadowOf(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.SUCCESS);
        shadowGoogleApiClient.registerConnectionCallbacks(mockConnCallbacks);

        shadowGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_REQUIRED);

        verify(mockConnCallbacks).onConnected(null);
        assertThat(shadowGoogleApiClient.getCurrentSignInMode())
            .isNotNull()
            .isEqualTo(GoogleApiClient.SIGN_IN_MODE_REQUIRED);
    }

    @Test
    public void connect__SignInModeOptional() {
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
            = Shadows.shadowOf(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.SUCCESS);
        shadowGoogleApiClient.registerConnectionCallbacks(mockConnCallbacks);

        shadowGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);

        verify(mockConnCallbacks).onConnected(null);
        assertThat(shadowGoogleApiClient.getCurrentSignInMode())
            .isNotNull()
            .isEqualTo(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
    }

    @Test
    public void connect__SignInModeNone() {
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
            = Shadows.shadowOf(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.SUCCESS);
        shadowGoogleApiClient.registerConnectionCallbacks(mockConnCallbacks);

        shadowGoogleApiClient.connect(3);

        verify(mockConnCallbacks).onConnected(null);
        assertThat(shadowGoogleApiClient.getCurrentSignInMode())
            .isNotNull()
            .isEqualTo(3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void connect__SignInModeInvalidValue() {
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
            = Shadows.shadowOf(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.SUCCESS);

        shadowGoogleApiClient.connect(0);
    }

    @Test(expected = IllegalStateException.class)
    public void connect__SignInMode__CalledWithDifferentValue() {
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
            = Shadows.shadowOf(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.SUCCESS);
        shadowGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_REQUIRED);

        shadowGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
    }

    @Test
    public void disconnect() {
        shadowGoogleApiClient.registerConnectionCallbacks(mockConnCallbacks);

        shadowGoogleApiClient.disconnect();

        verify(mockConnCallbacks)
            .onConnectionSuspended(GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED);
    }

    @Test
    public void disconnect__NullConnectionCallbacks() {
        shadowGoogleApiClient.registerConnectionCallbacks(null);

        try {
            shadowGoogleApiClient.disconnect();
        } catch (Exception e) {
            Assertions.fail("Caught while trying to call back to a null ConnectionCallbacks "
                + "instance while disconnecting: " + e.getMessage(), e);
        }
    }

    @Test
    public void reconnect() {
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
            = Shadows.shadowOf(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.SUCCESS);
        shadowGoogleApiClient.registerConnectionCallbacks(mockConnCallbacks);

        shadowGoogleApiClient.reconnect();

        verify(mockConnCallbacks).onConnected(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void clearDefaultAccountAndReconnect() {
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
            = Shadows.shadowOf(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.SUCCESS);
        final PendingResult<Status> mockPendingResult = mock(PendingResult.class);
        shadowGoogleApiClient.setReconnectPendingResult(mockPendingResult);
        shadowGoogleApiClient.registerConnectionCallbacks(mockConnCallbacks);

        final PendingResult<Status> actual = shadowGoogleApiClient
            .clearDefaultAccountAndReconnect();

        verify(mockConnCallbacks)
            .onConnectionSuspended(GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED);
        verify(mockConnCallbacks).onConnected(null);
        assertThat(actual)
            .isNotNull()
            .isEqualTo(mockPendingResult);
    }

    @Test
    public void blockingConnect() {
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
            = Shadows.shadowOf(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.SUCCESS);

        final ConnectionResult actual = shadowGoogleApiClient.blockingConnect();

        assertThat(actual)
            .isNotNull();
        assertThat(actual.getErrorCode())
            .isEqualTo(ConnectionResult.SUCCESS);
    }

    @Test
    public void blockingConnect__WithTimeout() {
        final ShadowGoogleApiAvailability shadowGoogleApiAvailability
            = Shadows.shadowOf(GoogleApiAvailability.getInstance());
        shadowGoogleApiAvailability.setIsGooglePlayServicesAvailable(ConnectionResult.SUCCESS);

        final ConnectionResult actual = shadowGoogleApiClient.blockingConnect(10, TimeUnit.SECONDS);

        assertThat(actual)
            .isNotNull();
        assertThat(actual.getErrorCode())
            .isEqualTo(ConnectionResult.SUCCESS);
    }
}
