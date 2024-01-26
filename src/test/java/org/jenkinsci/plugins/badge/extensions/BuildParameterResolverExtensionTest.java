package org.jenkinsci.plugins.badge.extensions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Run;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BuildParameterResolverExtensionTest {

    @Mock
    private Run<?, ?> runMock;

    @Mock
    private ParametersAction paramsMock;

    private BuildParameterResolverExtension resolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resolver = new BuildParameterResolverExtension();
    }

    // A custom class to control the toString() behavior
    private static class CustomObject {
        @Override
        public String toString() {
            return "default";
        }
    }

    @Test
    void resolveShouldReturnOriginalParameterIfRunIsNull() {
        String parameter = "params.paramName";
        assertEquals(parameter, resolver.resolve(null, parameter));
    }

    @Test
    void resolveShouldReturnOriginalParameterIfNoParametersAction() {
        String parameter = "params.paramName";
        when(runMock.getAction(ParametersAction.class)).thenReturn(null);

        assertEquals(parameter, resolver.resolve(runMock, parameter));
    }

    @Test
    void resolveShouldReplaceParameterWithParamValue() {
        String paramName = "paramName";
        String parameter = "params." + paramName;
        String paramValue = "paramValue";

        ParameterValue valueMock = mock(ParameterValue.class);
        when(valueMock.getValue()).thenReturn(paramValue);

        when(paramsMock.getParameter(paramName)).thenReturn(valueMock);
        when(runMock.getAction(ParametersAction.class)).thenReturn(paramsMock);

        assertEquals(paramValue, resolver.resolve(runMock, parameter));
    }

    @Test
    void resolveShouldReplaceParameterWithDefaultValueIfParamNotFound() {
        String paramName = "paramName";
        String defaultValue = "default";
        String parameter = "params." + paramName + "|" + defaultValue;

        when(paramsMock.getParameter(paramName)).thenReturn(null);
        when(runMock.getAction(ParametersAction.class)).thenReturn(paramsMock);

        assertEquals(defaultValue, resolver.resolve(runMock, parameter));
    }

    @Test
    void resolveShouldReplaceParameterWithDefaultValueIfParamValueIsNull() {
        String paramName = "paramName";
        String defaultValue = "default";
        String parameter = "params." + paramName + "|" + defaultValue;

        ParameterValue valueMock = mock(ParameterValue.class);
        when(valueMock.getValue()).thenReturn(null);

        when(paramsMock.getParameter(paramName)).thenReturn(valueMock);
        when(runMock.getAction(ParametersAction.class)).thenReturn(paramsMock);

        assertEquals(defaultValue, resolver.resolve(runMock, parameter));
    }

    @Test
    void resolveShouldReplaceParameterWithDefaultValueIfParamValueToStringIsNull() {
        String paramName = "paramName";
        String defaultValue = "default";
        String parameter = "params." + paramName + "|" + defaultValue;

        ParameterValue valueMock = mock(ParameterValue.class);
        when(valueMock.getValue()).thenReturn(new CustomObject());

        when(paramsMock.getParameter(paramName)).thenReturn(valueMock);
        when(runMock.getAction(ParametersAction.class)).thenReturn(paramsMock);

        assertEquals(defaultValue, resolver.resolve(runMock, parameter));
    }
}
