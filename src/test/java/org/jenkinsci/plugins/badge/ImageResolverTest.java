package org.jenkinsci.plugins.badge;



import org.junit.ClassRule;
import hudson.model.BallColor;

import org.junit.Test;


import org.jvnet.hudson.test.JenkinsRule;

import org.mockito.Mockito;
import org.mockito.Spy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

import java.io.IOException;

public class ImageResolverTest {
    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void TestGetDefault32x32Ball() throws Exception {
        ImageResolver ImageTester = new ImageResolver();
        String style="ball-Error"; // should give default due to url being null
        String status = null;
        String subject = null;
        String colorName=null;
        String fileName = "images/32x32/blue.png";
        BallColor ball = mock(BallColor.class);
        //default
        Mockito.when(ball.getImageOf("Error")).thenReturn(null);
        Mockito.when(ball.getImageOf("32x32")).thenReturn(fileName);
        Mockito.when(ball.noAnime()).thenCallRealMethod();
        StatusImage TestImage = ImageTester.getImage(ball, style,subject,status, colorName, null, null);
        assertThat(TestImage.getEtag(), containsString(fileName));

     
    }
}


