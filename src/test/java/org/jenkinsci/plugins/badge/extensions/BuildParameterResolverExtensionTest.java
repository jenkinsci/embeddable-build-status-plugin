package org.jenkinsci.plugins.badge.extensions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Run;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuildParameterResolverExtensionTest {

    @Mock
    private Run<?, ?> runMock;

    @Mock
    private ParametersAction paramsMock;

    private BuildParameterResolverExtension resolver;

    @BeforeEach
    void setUp() {
        resolver = new BuildParameterResolverExtension();
    }

    // A custom class to control the toString() behavior
    private static class CustomObject {
        final String returnValue;

        CustomObject(String returnValue) {
            this.returnValue = returnValue;
        }

        @Override
        public String toString() {
            return returnValue;
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
    void resolveShouldReplaceParameterWithCustomObjectValueIfParamValueToStringIsNull() {
        String paramName = "paramName";
        String defaultValue = "default";
        String parameter = "params." + paramName + "|" + defaultValue;
        String customObjectReturnValue = defaultValue + "CustomObject";

        ParameterValue valueMock = mock(ParameterValue.class);
        when(valueMock.getValue()).thenReturn(new CustomObject(customObjectReturnValue));

        when(paramsMock.getParameter(paramName)).thenReturn(valueMock);
        when(runMock.getAction(ParametersAction.class)).thenReturn(paramsMock);

        assertEquals(customObjectReturnValue, resolver.resolve(runMock, parameter));
    }
}
