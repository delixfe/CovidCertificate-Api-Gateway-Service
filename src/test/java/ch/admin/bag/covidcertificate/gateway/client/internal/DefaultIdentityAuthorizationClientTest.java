package ch.admin.bag.covidcertificate.gateway.client.internal;

import ch.admin.bag.covidcertificate.gateway.client.eiam.EIAMClient;
import ch.admin.bag.covidcertificate.gateway.eiam.adminservice.*;
import ch.admin.bag.covidcertificate.gateway.service.dto.CreateCertificateException;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static ch.admin.bag.covidcertificate.gateway.error.ErrorList.INVALID_IDENTITY_USER;
import static ch.admin.bag.covidcertificate.gateway.error.ErrorList.INVALID_IDENTITY_USER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultIdentityAuthorizationClientTest {
    private final JFixture jFixture = new JFixture();

    String uuid;
    String ipdSource;
    
    @Mock
    public EIAMClient eiamClient;
    @InjectMocks
    private DefaultIdentityAuthorizationClient client;

    @BeforeEach
    private void initialize() {
        uuid = jFixture.create(String.class);
        ipdSource = jFixture.create(String.class);
    }

    @Test
    void givenUserExists_whenAuthorize_thenOk() {
        // given
        when(eiamClient.queryUser(any(String.class), any(String.class), any(String.class)))
                .thenReturn(getQueryUsersResponse("9500.GGG-Covidcertificate.CertificateCreator"));
        // when
        client.authorize(uuid, ipdSource);
        // then
        verify(eiamClient).queryUser(any(String.class), any(String.class), any(String.class));
    }

    @Test
    void givenSuperUserExists_whenAuthorize_thenOk() {
        // given
        when(eiamClient.queryUser(any(String.class), any(String.class), any(String.class)))
                .thenReturn(getQueryUsersResponse("9500.GGG-Covidcertificate.SuperUserCC"));
        // when
        client.authorize(uuid, ipdSource);
        // then
        verify(eiamClient).queryUser(any(String.class), any(String.class), any(String.class));
    }

    @Test
    void givenUserNotExists_whenAuthorize_thenThrowsException() {
        // given
        when(eiamClient.queryUser(any(String.class), any(String.class), any(String.class)))
                .thenReturn(new QueryUsersResponse());
        // when then
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> client.authorize(uuid, ipdSource));
        assertEquals(INVALID_IDENTITY_USER, exception.getError());
    }

    @Test
    void givenUserExistsButHasNotRequiredRole_whenAuthorize_thenThrowsException() {
        // given
        when(eiamClient.queryUser(any(String.class), any(String.class), any(String.class)))
                .thenReturn(getQueryUsersResponse(jFixture.create(String.class)));
        // when then
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> client.authorize(uuid, ipdSource));
        assertEquals(INVALID_IDENTITY_USER_ROLE, exception.getError());
    }

    @Test
    void givenExceptionIsThrown_whenAuthorize_thenThrowsException() {
        // given
        var runtimeException = new RuntimeException();
        when(eiamClient.queryUser(any(String.class), any(String.class), any(String.class)))
                .thenThrow(runtimeException);
        // when then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> client.authorize(uuid, ipdSource));
        assertEquals(runtimeException, exception);
    }

    @Test
    void throwsExceptionOnAuthorize__ifUuidIsEmptyString() {
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> client.authorize("", ipdSource));
        assertEquals(INVALID_IDENTITY_USER, exception.getError());
        verify(eiamClient, never()).queryUser(anyString(), anyString(), anyString());
    }

    @Test
    void throwsExceptionOnAuthorize__ifIdpSourceIsEmptyString() {
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> client.authorize(uuid, ""));
        assertEquals(INVALID_IDENTITY_USER, exception.getError());
        verify(eiamClient, never()).queryUser(anyString(), anyString(), anyString());
    }

    @Test
    void throwsExceptionOnAuthorize__ifIdpSourceAndUuidAreEmptyString() {
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> client.authorize("", ""));
        assertEquals(INVALID_IDENTITY_USER, exception.getError());
        verify(eiamClient, never()).queryUser(anyString(), anyString(), anyString());
    }

    private QueryUsersResponse getQueryUsersResponse(String extId) {
        var queryUsersResponse = new QueryUsersResponse();
        var users = queryUsersResponse.getReturns();
        var user = new User();
        var profiles = user.getProfiles();
        var profile = new Profile();
        profile.setState(ProfileState.ACTIVE);
        var authorizations = profile.getAuthorizations();
        var authorization = new Authorization();
        var role = new Role();
        role.setExtId(extId);
        authorization.setRole(role);
        authorizations.add(authorization);
        profiles.add(profile);
        users.add(user);
        return queryUsersResponse;
    }
}
