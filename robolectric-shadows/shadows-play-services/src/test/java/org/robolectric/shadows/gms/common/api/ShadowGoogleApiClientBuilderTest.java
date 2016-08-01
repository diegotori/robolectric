package org.robolectric.shadows.gms.common.api;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;

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
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.gms.Shadows;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by diegotori on 2/14/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, shadows = {ShadowGoogleApiClient.ShadowBuilder.class, ShadowGoogleApiClient.class})
public class ShadowGoogleApiClientBuilderTest {
    private static final String MOCK_APP_ID = "com.foo.app.id";

    private static final String MOCK_API_NAME = "some_api_name";

    @Mock
    private Context mockContext;

    @Mock
    private Looper mockLooper;

    @Mock
    private Handler mockHandler;

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

    private Api.zzc<Api.zzb> mockClientKey;

    private Api<Api.ApiOptions.NotRequiredOptions> mockNonOptsApi;

    private Scope mockApiScope;

    private Context roboContext;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        mockClientKey = new Api.zzc<>();
        roboContext = RuntimeEnvironment.application;
        when(mockHandler.getLooper()).thenReturn(mockLooper);
    }

    @After
    public void tearDown(){
        roboContext = null;
        mockApiScope = null;
        mockClientKey = null;
        mockNonOptsApi = null;
    }

    @Test
    public void shadowOf(){
        final ShadowGoogleApiClient.ShadowBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(roboContext));
        assertThat(shadowBuilder).isNotNull();
    }

    @Test
    public void newInstance(){
        when(mockContext.getMainLooper()).thenReturn(mockLooper);
        when(mockContext.getPackageName()).thenReturn(MOCK_APP_ID);

        final ShadowGoogleApiClient.ShadowBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(mockContext));

        verify(mockContext, times(2)).getMainLooper();
        verify(mockContext, times(2)).getPackageName();
        assertThat(shadowBuilder.getContext()).isNotNull().isEqualTo(mockContext);
        assertThat(shadowBuilder.getLooper()).isNotNull().isEqualTo(mockLooper);
        assertThat(shadowBuilder.getAppId()).isNotNull().isNotEmpty().isEqualTo(MOCK_APP_ID);
    }

    @Test
    public void newInstance__WithListeners(){
        when(mockContext.getMainLooper()).thenReturn(mockLooper);
        when(mockContext.getPackageName()).thenReturn(MOCK_APP_ID);

        final ShadowGoogleApiClient.ShadowBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(mockContext,
                mockConnCallbacks,
                mockConnFailedListener));

        verify(mockContext, times(3)).getMainLooper();
        verify(mockContext, times(3)).getPackageName();
        assertThat(shadowBuilder.getLooper()).isNotNull().isEqualTo(mockLooper);
        assertThat(shadowBuilder.getAppId()).isNotNull().isNotEmpty().isEqualTo(MOCK_APP_ID);
        assertThat(shadowBuilder.getConnCallbacks()).isNotEmpty();
        assertThat(shadowBuilder.getConnCallbacks().contains(mockConnCallbacks)).isTrue();
        assertThat(shadowBuilder.getFailedListeners().contains(mockConnFailedListener)).isTrue();
    }

    @Test
    public void addConnectionCallbacks(){
        final ShadowGoogleApiClient.ShadowBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(roboContext)
                .addConnectionCallbacks(mockConnCallbacks));
        assertThat(shadowBuilder.getConnCallbacks().contains(mockConnCallbacks)).isTrue();
    }

    @Test
    public void addOnConnectionFailedListener(){
        final ShadowGoogleApiClient.ShadowBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(roboContext)
                .addOnConnectionFailedListener(mockConnFailedListener));
        assertThat(shadowBuilder.getFailedListeners().contains(mockConnFailedListener)).isTrue();
    }

    @Test
    public void useDefaultAccount(){
        final String expectedAccountName = "<<default account>>";
        final ShadowGoogleApiClient.ShadowBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(roboContext)
                .useDefaultAccount());
        assertThat(shadowBuilder.getAccountName())
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(expectedAccountName);
    }

    @Test
    public void setAccountName(){
        final String expectedAccountName = "foo account";

        final ShadowGoogleApiClient.ShadowBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(roboContext)
                .setAccountName(expectedAccountName));

        assertThat(shadowBuilder.getAccountName())
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(expectedAccountName);
    }

    @Test
    public void addScope(){
        final String mockScopeUri = "com.foo.scope.uri";
        mockApiScope = new Scope(mockScopeUri);

        final ShadowGoogleApiClient.ShadowBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(roboContext)
                .addScope(mockApiScope));

        assertThat(shadowBuilder.getScopeUris().contains(mockScopeUri)).isTrue();
    }

    @Test
    public void setGravityForPopups(){
        final int expectedGravity = Gravity.CENTER;

        final ShadowGoogleApiClient.ShadowBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(roboContext)
                .setGravityForPopups(expectedGravity));

        assertThat(shadowBuilder.getGravityForPopups()).isNotZero().isEqualTo(expectedGravity);
    }

    @Test
    public void setHandler(){
        final ShadowGoogleApiClient.ShadowBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(roboContext)
                .setHandler(mockHandler));

        verify(mockHandler, times(2)).getLooper();
        assertThat(shadowBuilder.getLooper()).isNotNull().isEqualTo(mockLooper);
    }

    @Test
    public void setViewForPopups(){
        final View mockView = mock(View.class);

        final ShadowGoogleApiClient.ShadowBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(roboContext)
                .setViewForPopups(mockView));

        assertThat(shadowBuilder.getViewForPopups()).isNotNull().isEqualTo(mockView);
    }

    @Test
    public void addApi__WithOptions(){
        final String mockScopeUri = "foobar";
        mockApiScope = new Scope(mockScopeUri);
        final List<Scope> mockApiScopes = new ArrayList<>();
        mockApiScopes.add(mockApiScope);
        final Api<Api.ApiOptions.HasOptions> mockApi = new Api<>(MOCK_API_NAME, mockClientBuilder, mockClientKey);
        when(mockClientBuilder.zzo(mockApiOptions)).thenReturn(mockApiScopes);

        final ShadowGoogleApiClient.ShadowBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(mockContext)
                .addApi(mockApi, mockApiOptions));

        verify(mockClientBuilder, times(2)).zzo(mockApiOptions);
        assertThat(shadowBuilder.getScopeUris()).isNotEmpty();
        assertThat(shadowBuilder.getScopeUris().contains(mockScopeUri)).isTrue();
        assertThat(shadowBuilder.getApiMap()).isNotEmpty();
        assertThat(shadowBuilder.getApiMap().containsKey(mockApi)).isTrue();
        assertThat(shadowBuilder.getApiMap().containsValue(mockApiOptions)).isTrue();
    }

    @Test
    public void addApi(){
        final String mockScopeUri = "foobar";
        mockApiScope = new Scope(mockScopeUri);
        final List<Scope> mockApiScopes = new ArrayList<>();
        mockApiScopes.add(mockApiScope);
        mockNonOptsApi = new Api<>(MOCK_API_NAME, mockNonOptsClientBuilder,
                mockClientKey);
        when(mockNonOptsClientBuilder.zzo(null)).thenReturn(mockApiScopes);

        final ShadowGoogleApiClient.ShadowBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(mockContext)
                .addApi(mockNonOptsApi));

        verify(mockNonOptsClientBuilder, times(2)).zzo(null);
        assertThat(shadowBuilder.getScopeUris()).isNotEmpty();
        assertThat(shadowBuilder.getScopeUris().contains(mockScopeUri)).isTrue();
        assertThat(shadowBuilder.getApiMap().containsKey(mockNonOptsApi)).isTrue();
        assertThat(shadowBuilder.getApiMap().containsValue(null)).isTrue();
    }

    @Test
    public void enableAutoManage(){
        final int mockClientId = 12345;
        final FragmentActivity mockFragActivity = mock(FragmentActivity.class);

        final ShadowGoogleApiClient.ShadowBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(mockContext)
                .enableAutoManage(mockFragActivity, mockClientId, mockConnFailedListener));

        assertThat(shadowBuilder.getUnresolvedConnectionFailedListener())
                .isNotNull()
                .isEqualTo(mockConnFailedListener);
        assertThat(shadowBuilder.getClientId())
                .isNotZero()
                .isEqualTo(mockClientId);
        assertThat(shadowBuilder.getFragmentActivity())
                .isNotNull()
                .isEqualTo(mockFragActivity);
    }

    @Test
    public void build(){
        final Api.zzb mockZzb = mock(Api.zzb.class);
        final String mockScopeUri = "some Scope URI";
        mockApiScope = new Scope(mockScopeUri);
        final List<Scope> mockApiScopes = new ArrayList<>();
        mockApiScopes.add(mockApiScope);
        final Api<Api.ApiOptions.NotRequiredOptions> mockApi = new Api<>(MOCK_API_NAME,
                mockNonOptsClientBuilder,
                mockClientKey);
        when(mockContext.getMainLooper()).thenReturn(mockLooper);
        when(mockContext.getPackageName()).thenReturn(MOCK_APP_ID);
        when(mockNonOptsClientBuilder.zzo(any(Api.ApiOptions.NotRequiredOptions.class)))
                .thenReturn(mockApiScopes);
        when(mockNonOptsClientBuilder.getPriority()).thenReturn(1);
        when(mockNonOptsClientBuilder.zza(eq(mockContext), eq(mockLooper), any(zzf.class),
                any(Api.ApiOptions.NotRequiredOptions.class),
                any(GoogleApiClient.ConnectionCallbacks.class),
                any(GoogleApiClient.OnConnectionFailedListener.class)))
                .thenReturn(mockZzb);
//        when(mockZzb.isConnected()).thenReturn(true);

        final GoogleApiClient client = new GoogleApiClient.Builder(mockContext)
                .addConnectionCallbacks(mockConnCallbacks)
                .addOnConnectionFailedListener(mockConnFailedListener)
                .addApi(mockApi)
                .build();

        verify(mockNonOptsClientBuilder, times(2)).zzo(any(Api.ApiOptions.NotRequiredOptions.class));
        verify(mockNonOptsClientBuilder).getPriority();
        verify(mockNonOptsClientBuilder).zza(eq(mockContext), eq(mockLooper), any(zzf.class),
                any(Api.ApiOptions.NotRequiredOptions.class),
                any(GoogleApiClient.ConnectionCallbacks.class),
                any(GoogleApiClient.OnConnectionFailedListener.class));
        verify(mockContext, atLeastOnce()).getMainLooper();
        verify(mockContext, atLeastOnce()).getPackageName();
        final ShadowGoogleApiClient shadowClient = Shadows.shadowOf(client);
        assertThat(shadowClient).isNotNull().isInstanceOf(ShadowGoogleApiClient.class);
        assertThat(shadowClient.isConnectionCallbacksRegistered(mockConnCallbacks)).isTrue();
    }
}
