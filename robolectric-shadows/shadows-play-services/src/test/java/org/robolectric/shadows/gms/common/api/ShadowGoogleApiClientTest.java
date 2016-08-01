package org.robolectric.shadows.gms.common.api;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzf;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
        final ConnectionResult expected = new ConnectionResult(ConnectionResult.TIMEOUT, null);
        shadowGoogleApiClient.setConnectionResultForApi(mockApi, expected);

        final ConnectionResult actual = shadowGoogleApiClient.getConnectionResult(mockApi);

        assertThat(actual)
                .isNotNull().isEqualTo(expected);
    }
}
