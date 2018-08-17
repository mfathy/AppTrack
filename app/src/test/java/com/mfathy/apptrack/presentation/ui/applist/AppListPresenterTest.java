package com.mfathy.apptrack.presentation.ui.applist;

import android.app.LoaderManager;
import android.content.Context;

import com.mfathy.data.AppsDataSource;
import com.mfathy.data.exception.AppsNotAvailableException;
import com.mfathy.data.model.AppEntry;
import com.mfathy.data.model.BlackListedApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

/**
 * Created by Mohammed Fathy on 17/08/2018.
 * dev.mfathy@gmail.com
 */
@RunWith(MockitoJUnitRunner.class)
public class AppListPresenterTest {

    private AppListPresenter appListPresenter;

    @Mock
    private Context mockContext;
    @Mock
    private AppsDataSource mockAppsDataRepository;
    @Mock
    private LoaderManager mockLoaderManager;
    @Mock
    private AppListContract.View mockView;
    @Mock
    private List<BlackListedApp> mockBlackListedApps;
    @Mock
    private List<AppEntry> mockAppEntries;

    @Captor
    private
    ArgumentCaptor<AppsDataSource.LoadApplicationEntitiesCallback> loadApplicationEntitiesCallbackArgumentCaptor;


    @Before
    public void setUp() {
        appListPresenter = new AppListPresenter(mockAppsDataRepository, mockLoaderManager, mockContext);
        appListPresenter.onBind(mockView);
    }

    @Test
    public void testGetBlackListedApplications() {
        given(mockView.context()).willReturn(mockContext);
        List<AppEntry> appEntries = new ArrayList<>();
        List<BlackListedApp> blackListedApps = new ArrayList<>();
        blackListedApps.add(new BlackListedApp("com.mfathy.apptrack", "AppTrack"));

        appListPresenter.getBlackListedApplications(appEntries);
        verify(mockAppsDataRepository).getApplicationEntities(Matchers.<List<AppEntry>>any(), loadApplicationEntitiesCallbackArgumentCaptor.capture());

        loadApplicationEntitiesCallbackArgumentCaptor.getValue().onAppsLoaded(appEntries, blackListedApps);

        verify(mockView).hideLoading();
        verify(mockView).hideError();
        verify(mockView).renderAppList(Matchers.<List<AppEntry>>any(), Matchers.<List<BlackListedApp>>any());
    }

    @Test
    public void testGetNoBlackListedApplications() {
        given(mockView.context()).willReturn(mockContext);
        List<AppEntry> appEntries = new ArrayList<>();
        appListPresenter.getBlackListedApplications(appEntries);
        verify(mockAppsDataRepository).getApplicationEntities(Matchers.<List<AppEntry>>any(), loadApplicationEntitiesCallbackArgumentCaptor.capture());

        loadApplicationEntitiesCallbackArgumentCaptor.getValue().onAppsLoaded(appEntries, null);

        verify(mockView).hideLoading();
        verify(mockView).hideError();
        verify(mockView).renderAppList(Matchers.<List<AppEntry>>any(), Matchers.<List<BlackListedApp>>any());
    }

    @Test
    public void testGetNoBlackListedApplicationsAndNoDeviceAppList() {
        given(mockView.context()).willReturn(mockContext);
        appListPresenter.getBlackListedApplications(null);
        verify(mockAppsDataRepository).getApplicationEntities(Matchers.<List<AppEntry>>any(), loadApplicationEntitiesCallbackArgumentCaptor.capture());

        loadApplicationEntitiesCallbackArgumentCaptor.getValue().onAppsNotAvailable(new AppsNotAvailableException());

        verify(mockView).hideLoading();
        verify(mockView).showError(anyString());
    }

}