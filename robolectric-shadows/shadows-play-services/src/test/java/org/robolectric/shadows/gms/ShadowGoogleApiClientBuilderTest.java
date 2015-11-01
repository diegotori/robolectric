package org.robolectric.shadows.gms;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.gms.fakes.FakeGoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by diegotori on 10/4/15.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ShadowGoogleApiClientBuilderTest {
    private static final String MOCK_APP_ID = "com.foo.app.id";

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
    private Api<Api.ApiOptions.HasOptions> mockApi;

    @Mock
    private Api.ApiOptions.HasOptions mockApiOptions;

    @Mock
    private Api<Api.ApiOptions.NotRequiredOptions> mockNonOptsApi;

    @Mock
    private Scope mockApiScope;

    @Mock
    private GoogleApiClient mockGoogleApiClient;

    private Context roboContext;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        roboContext = RuntimeEnvironment.application;
        when(mockHandler.getLooper()).thenReturn(mockLooper);
    }

    @After
    public void tearDown(){
        roboContext = null;
    }

    @Test
    public void shadowOf(){
        final ShadowGoogleApiClientBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(roboContext));
        assertThat(shadowBuilder).isNotNull();
    }

    @Test
    public void setFakeGoogleApiClientFactory(){
        final ShadowGoogleApiClientBuilder.FakeGoogleApiClientFactory mockFactory
                = mock(ShadowGoogleApiClientBuilder.FakeGoogleApiClientFactory.class);

        final ShadowGoogleApiClientBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(roboContext));
        shadowBuilder.setFakeGoogleApiClientFactory(mockFactory);

        assertThat(shadowBuilder.listener).isNotNull().isEqualTo(mockFactory);

    }

    @Test
    public void newInstance(){
        when(mockContext.getMainLooper()).thenReturn(mockLooper);
        when(mockContext.getPackageName()).thenReturn(MOCK_APP_ID);

        final ShadowGoogleApiClientBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(mockContext));

        verify(mockContext).getMainLooper();
        verify(mockContext).getPackageName();
        assertThat(shadowBuilder.getContext()).isNotNull().isEqualTo(mockContext);
        assertThat(shadowBuilder.looper).isNotNull().isEqualTo(mockLooper);
        assertThat(shadowBuilder.getAppId()).isNotNull().isNotEmpty().isEqualTo(MOCK_APP_ID);
    }

    @Test
    public void newInstance__WithListeners(){
        when(mockContext.getMainLooper()).thenReturn(mockLooper);
        when(mockContext.getPackageName()).thenReturn(MOCK_APP_ID);

        final ShadowGoogleApiClientBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(mockContext,
                mockConnCallbacks,
                mockConnFailedListener));

        verify(mockContext, times(2)).getMainLooper();
        verify(mockContext, times(2)).getPackageName();
        assertThat(shadowBuilder.looper).isNotNull().isEqualTo(mockLooper);
        assertThat(shadowBuilder.getAppId()).isNotNull().isNotEmpty().isEqualTo(MOCK_APP_ID);
        assertThat(shadowBuilder.getConnCallbacks()).isNotEmpty();
        assertThat(shadowBuilder.getConnCallbacks().contains(mockConnCallbacks)).isTrue();
        assertThat(shadowBuilder.getFailedListeners().contains(mockConnFailedListener)).isTrue();
    }

    @Test
    public void addConnectionCallbacks(){
        final ShadowGoogleApiClientBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(roboContext)
                                        .addConnectionCallbacks(mockConnCallbacks));
        assertThat(shadowBuilder.getConnCallbacks().contains(mockConnCallbacks)).isTrue();
    }

    @Test
    public void addOnConnectionFailedListener(){
        final ShadowGoogleApiClientBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(roboContext)
                                        .addOnConnectionFailedListener(mockConnFailedListener));
        assertThat(shadowBuilder.getFailedListeners().contains(mockConnFailedListener)).isTrue();
    }

    @Test
    public void useDefaultAccount(){
        final String expectedAccountName = "<<default account>>";
        final ShadowGoogleApiClientBuilder shadowBuilder
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

        final ShadowGoogleApiClientBuilder shadowBuilder
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
        when(mockApiScope.gO()).thenReturn(mockScopeUri);

        final ShadowGoogleApiClientBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(roboContext)
                .addScope(mockApiScope));

        verify(mockApiScope).gO();
        assertThat(shadowBuilder.getScopeUris().contains(mockScopeUri)).isTrue();
    }

    @Test
    public void setGravityForPopups(){
        final int expectedGravity = Gravity.CENTER;

        final ShadowGoogleApiClientBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(roboContext)
                .setGravityForPopups(expectedGravity));

        assertThat(shadowBuilder.getGravityForPopups()).isNotZero().isEqualTo(expectedGravity);
    }

    @Test
    public void setHandler(){
        final ShadowGoogleApiClientBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(roboContext)
                .setHandler(mockHandler));

        verify(mockHandler).getLooper();
        assertThat(shadowBuilder.getLooper()).isNotNull().isEqualTo(mockLooper);
    }

    @Test
    public void setViewForPopups(){
        final View mockView = mock(View.class);

        final ShadowGoogleApiClientBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(roboContext)
                .setViewForPopups(mockView));

        assertThat(shadowBuilder.getViewForPopups()).isNotNull().isEqualTo(mockView);
    }

    @Test
    public void addApi__WithOptions(){
        final String mockScopeUri = "foobar";
        when(mockApiScope.gO()).thenReturn(mockScopeUri);
        final List<Scope> mockApiScopes = new ArrayList<>();
        mockApiScopes.add(mockApiScope);
        when(mockApi.gy()).thenReturn(mockApiScopes);

        final ShadowGoogleApiClientBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(mockContext)
                .addApi(mockApi, mockApiOptions));

        verify(mockApi).gy();
        verify(mockApiScope).gO();
        assertThat(shadowBuilder.getScopeUris()).isNotEmpty();
        assertThat(shadowBuilder.getScopeUris().contains(mockScopeUri)).isTrue();
        assertThat(shadowBuilder.getApiMap()).isNotEmpty();
        assertThat(shadowBuilder.getApiMap().containsKey(mockApi)).isTrue();
        assertThat(shadowBuilder.getApiMap().containsValue(mockApiOptions)).isTrue();
    }

    @Test
    public void addApi(){
        final String mockScopeUri = "foobar";
        when(mockApiScope.gO()).thenReturn(mockScopeUri);
        final List<Scope> mockApiScopes = new ArrayList<>();
        mockApiScopes.add(mockApiScope);
        when(mockNonOptsApi.gy()).thenReturn(mockApiScopes);

        final ShadowGoogleApiClientBuilder shadowBuilder
                = Shadows.shadowOf(new GoogleApiClient.Builder(mockContext)
                                        .addApi(mockNonOptsApi));

        verify(mockNonOptsApi).gy();
        verify(mockApiScope).gO();
        assertThat(shadowBuilder.getScopeUris()).isNotEmpty();
        assertThat(shadowBuilder.getScopeUris().contains(mockScopeUri)).isTrue();
        assertThat(shadowBuilder.getApiMap().containsKey(mockNonOptsApi)).isTrue();
        assertThat(shadowBuilder.getApiMap().containsValue(null)).isTrue();
    }

    @Test
    public void enableAutoManage(){
        final int mockClientId = 12345;
        final FragmentActivity mockFragActivity = mock(FragmentActivity.class);

        final ShadowGoogleApiClientBuilder shadowBuilder
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
        final GoogleApiClient client = new GoogleApiClient.Builder(roboContext).build();
        assertThat(client).isNotNull().isInstanceOf(FakeGoogleApiClient.class);
    }

    @Test
    public void build__WithFakeGoogleApiClientFactory(){
        final ShadowGoogleApiClientBuilder.FakeGoogleApiClientFactory mockFactory
                = mock(ShadowGoogleApiClientBuilder.FakeGoogleApiClientFactory.class);
        when(mockFactory.onBuild(any(ShadowGoogleApiClientBuilder.class)))
                .thenReturn(mockGoogleApiClient);
        final GoogleApiClient.Builder builder = new GoogleApiClient.Builder(roboContext);
        final ShadowGoogleApiClientBuilder shadowBuilder = Shadows.shadowOf(builder);
        shadowBuilder.setFakeGoogleApiClientFactory(mockFactory);

        final GoogleApiClient actual = builder.build();

        final ArgumentCaptor<ShadowGoogleApiClientBuilder> shadowBuilderCaptor
                = ArgumentCaptor.forClass(ShadowGoogleApiClientBuilder.class);
        verify(mockFactory).onBuild(shadowBuilderCaptor.capture());
        final ShadowGoogleApiClientBuilder capturedShadowBuilder = shadowBuilderCaptor.getValue();
        assertThat(capturedShadowBuilder).isNotNull().isEqualTo(shadowBuilder);
        assertThat(actual).isNotNull().isEqualTo(mockGoogleApiClient);

    }

}
